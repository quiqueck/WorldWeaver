package de.ambertation.wover.recipe.impl;

import de.ambertation.wover.entrypoint.LibWoverRecipe;
import de.ambertation.wunderlib.network.ClientBoundMessage;
import de.ambertation.wunderlib.network.NetworkRegistry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.ApiStatus;

/**
 * The loader-specific half of {@link de.ambertation.wover.recipe.api.SyncedRecipes}.
 * <p>
 * On 26.1+ this file is four lines that hand everything to {@code fabric-recipe-api-v1}'s
 * {@code RecipeSynchronization}. That module does not exist on this branch - {@code fabric-recipe-api-v1} is
 * still 8.1.x for every 1.21.8 build of Fabric API and ships no {@code ...recipe.v1.sync} package - so the
 * synchronisation is hand-rolled here, over WunderLib's {@link NetworkRegistry}. The public API is unchanged,
 * which is the whole point: call sites are identical on all four branches.
 *
 * <h2>When the recipes are sent</h2>
 * The receiver of this data is a recipe viewer, and JEI decides when to build its recipe list from a very
 * specific packet: it restarts its plugins every time the client handles a {@code ClientboundUpdateRecipesPacket}.
 * Both of the places vanilla sends that packet are therefore the deadline, and both are met by getting in
 * immediately ahead of it:
 * <ul>
 *     <li>on join, {@code PlayerList.placeNewPlayer} sends it a few instructions after the point where Fabric
 *     fires {@link ServerPlayConnectionEvents#JOIN} (the {@code ClientboundPlayerAbilitiesPacket} construction),
 *     so a push from that event always lands first - a connection delivers in order, so this is an ordering
 *     guarantee and not a race that usually goes our way;</li>
 *     <li>on {@code /reload}, {@code PlayerList.reloadResources} sends it again, and
 *     {@code PlayerListMixin} calls {@link #onDataPackReload(PlayerList)} at that method's head. Fabric's
 *     {@code END_DATA_PACK_RELOAD} would have been the obvious hook and is the wrong one: it fires when the
 *     whole reload future completes, which is after the packet has already gone out and JEI has already
 *     rebuilt from stale data.</li>
 * </ul>
 * Arriving first is only half of it: the client-side handler has to run where the payload is delivered rather
 * than reschedule itself, or it queues up behind the very packet it was supposed to beat. See
 * {@code LibWoverRecipeClient}.
 *
 * <h2>What is sent</h2>
 * Every loaded recipe whose serializer was passed to {@link #register(RecipeSerializer)} - so a mod opts its
 * own types in and vanilla's thousands of crafting recipes are never on the wire. Note the asymmetry in
 * {@link #allOfType}, which matches the Fabric-backed implementation: on the server the answer comes from the
 * full {@code RecipeMap} and covers <em>any</em> type, registered or not. Only the client half is limited to
 * what was declared.
 *
 * <h2>Wire format</h2>
 * {@code (byte formatVersion, boolean first, count, then per recipe: serializer id, recipe id, length-prefixed
 * body)} - see {@link #encodeOne} for why the serializer is named rather than numbered, and why the body is
 * length-prefixed. {@code first} marks the batch that starts a transfer, so the client knows to drop what it
 * had; the payload is split into as many batches as it takes to keep each one well under the 1 MiB a
 * clientbound custom payload may weigh, and the connection's ordering does the reassembly. A payload whose
 * {@code formatVersion} is not {@link #FORMAT_VERSION} is skipped whole and ignored, so a client and server
 * that disagree about the format end up with empty recipe-viewer categories rather than a decode error that
 * drops the connection.
 * <p>
 * A client that does not know the channel - vanilla, or modded without this library - discards the payload on
 * arrival (see {@link #sendTo} for why it is sent to everyone regardless), and a server that sends nothing
 * leaves the client's view empty. Both degrade to exactly what this branch did before recipes were synced at
 * all.
 */
@ApiStatus.Internal
public final class SyncedRecipesImpl {
    private SyncedRecipesImpl() {
    }

    /**
     * Bump when {@link Batch}'s encoding changes in a way an older reader would misread. Both sides ignore a
     * batch they cannot read, so a mismatch costs the feature, not the connection.
     */
    public static final byte FORMAT_VERSION = 1;

    /**
     * Batch budget. A clientbound custom payload may be 1 MiB; staying at half of that leaves room for the
     * envelope and for a client that discards the payload without understanding it.
     */
    private static final int MAX_BATCH_BYTES = 512 * 1024;

    private static final Set<RecipeSerializer<?>> SYNCED_SERIALIZERS = new LinkedHashSet<>();

    /**
     * One message of a transfer. {@code first} is set on the batch that opens one.
     */
    public record Batch(byte formatVersion, boolean first, List<RecipeHolder<?>> recipes) {
    }

    private static final StreamCodec<ByteBuf, ResourceKey<Recipe<?>>> RECIPE_KEY_CODEC =
            ResourceKey.streamCodec(Registries.RECIPE);

    /**
     * Writes one recipe as {@code (serializer id, recipe id, length-prefixed body)}.
     * <p>
     * Deliberately <em>not</em> {@code RecipeHolder.STREAM_CODEC}: that one identifies the serializer by its
     * numeric position in {@code BuiltInRegistries.RECIPE_SERIALIZER}, and the two ends of this connection do
     * not agree on those numbers. That registry is not among the ones the server sends the client, and its
     * order is simply whatever got registered - a client-only mod adding a serializer of its own (JEI does,
     * for its internal shaped recipe) shifts every index after it. The observed failure was the client
     * decoding {@code bclib:alloying}'s bytes with JEI's shaped-recipe serializer and throwing inside vanilla
     * code, far from anything that looked related.
     * <p>
     * The length prefix is what makes an unknown serializer survivable: without it there is no way to find
     * where the next recipe starts, so one recipe the client cannot build would cost the whole batch.
     */
    private static void encodeOne(RegistryFriendlyByteBuf buf, RecipeHolder<?> holder) {
        @SuppressWarnings("unchecked")
        StreamCodec<RegistryFriendlyByteBuf, Recipe<?>> recipeCodec =
                (StreamCodec<RegistryFriendlyByteBuf, Recipe<?>>) holder.value().getSerializer().streamCodec();

        ResourceLocation.STREAM_CODEC.encode(
                buf,
                BuiltInRegistries.RECIPE_SERIALIZER.getKey(holder.value().getSerializer())
        );
        RECIPE_KEY_CODEC.encode(buf, holder.id());

        int lengthAt = buf.writerIndex();
        buf.writeInt(0);
        int bodyAt = buf.writerIndex();
        recipeCodec.encode(buf, holder.value());
        buf.setInt(lengthAt, buf.writerIndex() - bodyAt);
    }

    /**
     * @return the recipe, or {@code null} when this side cannot build it - the body is skipped either way, so
     * the next recipe still reads cleanly.
     */
    private static RecipeHolder<?> decodeOne(RegistryFriendlyByteBuf buf) {
        ResourceLocation serializerId = ResourceLocation.STREAM_CODEC.decode(buf);
        ResourceKey<Recipe<?>> recipeId = RECIPE_KEY_CODEC.decode(buf);
        int length = buf.readInt();
        int resumeAt = buf.readerIndex() + length;

        try {
            RecipeSerializer<?> serializer = BuiltInRegistries.RECIPE_SERIALIZER.getValue(serializerId);
            if (serializer == null) {
                LibWoverRecipe.C.LOG.warn("No recipe serializer {} here; skipping {}.", serializerId, recipeId);
                return null;
            }
            return new RecipeHolder<>(recipeId, serializer.streamCodec().decode(buf));
        } catch (Exception e) {
            LibWoverRecipe.C.LOG.error("Could not read recipe {}; skipping it.", recipeId, e);
            return null;
        } finally {
            buf.readerIndex(resumeAt);
        }
    }

    private static final StreamCodec<RegistryFriendlyByteBuf, Batch> CODEC =
            new StreamCodec<>() {
                @Override
                public void encode(RegistryFriendlyByteBuf buf, Batch batch) {
                    buf.writeByte(batch.formatVersion());
                    buf.writeBoolean(batch.first());
                    buf.writeVarInt(batch.recipes().size());
                    for (RecipeHolder<?> holder : batch.recipes()) encodeOne(buf, holder);
                }

                @Override
                public Batch decode(RegistryFriendlyByteBuf buf) {
                    byte version = buf.readByte();
                    if (version != FORMAT_VERSION) {
                        // Not ours to read. The payload is the whole packet, so dropping the rest of the
                        // buffer leaves the stream in the state vanilla expects after a decode.
                        buf.skipBytes(buf.readableBytes());
                        return new Batch(version, false, List.of());
                    }

                    try {
                        boolean first = buf.readBoolean();
                        int count = buf.readVarInt();
                        List<RecipeHolder<?>> recipes = new ArrayList<>(count);
                        for (int i = 0; i < count; i++) {
                            RecipeHolder<?> holder = decodeOne(buf);
                            if (holder != null) recipes.add(holder);
                        }
                        return new Batch(version, first, recipes);
                    } catch (Exception e) {
                        // The framing itself is broken, so nothing after this point can be trusted. Losing the
                        // recipe list is the acceptable outcome; throwing here would drop the connection.
                        LibWoverRecipe.C.LOG.error("Could not read synchronised recipes; ignoring them.", e);
                        buf.skipBytes(buf.readableBytes());
                        return new Batch(version, false, List.of());
                    }
                }
            };

    public static final ClientBoundMessage<Batch> MESSAGE = NetworkRegistry.registerClientBound(
            LibWoverRecipe.C.mk("synced_recipes"),
            CODEC
    );

    // ---------------------------------------------------------------- registration

    public static void register(RecipeSerializer<?> serializer) {
        if (serializer == null) return;
        synchronized (SYNCED_SERIALIZERS) {
            SYNCED_SERIALIZERS.add(serializer);
        }
    }

    /**
     * Wires the server half up. Called from the common mod initialiser, so it also covers the integrated
     * server behind a singleplayer world - the client there reads what was sent to it over the memory
     * connection, exactly like it does on a dedicated server.
     */
    @ApiStatus.Internal
    public static void initialize() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> sendTo(handler.player));
        // Also drops the cache's reference to the server, which otherwise outlives a singleplayer world.
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> invalidate());
    }

    // ---------------------------------------------------------------- server half

    private static volatile MinecraftServer cachedFor;
    private static volatile List<Batch> cachedBatches;

    @ApiStatus.Internal
    public static void invalidate() {
        cachedFor = null;
        cachedBatches = null;
    }

    /**
     * Called at the head of {@code PlayerList.reloadResources}, i.e. after {@code /reload} has swapped in the
     * new {@code RecipeManager} but before the packet that makes JEI rebuild goes out.
     */
    @ApiStatus.Internal
    public static void onDataPackReload(PlayerList playerList) {
        invalidate();
        for (ServerPlayer player : playerList.getPlayers()) sendTo(player);
    }

    /**
     * Sent unconditionally, without first asking whether the client declared the channel.
     * <p>
     * At the moment of the join push the server does not know yet: a Fabric client announces its play-phase
     * channels only once it has handled the login packet, which is <em>after</em> everything
     * {@code placeNewPlayer} sends. Asking {@code ServerPlayNetworking.canSend} here answers "no" for every
     * client, modded ones included, and the push would never happen. Waiting until the answer is trustworthy
     * would mean sending after the packet a recipe viewer rebuilds on, which defeats the point. So this
     * behaves like vanilla's own recipe push - everyone gets it, and a client that cannot identify the payload
     * discards it, which is exactly how vanilla degrades.
     */
    private static void sendTo(ServerPlayer player) {
        if (player == null) return;

        MinecraftServer server = player.getServer();
        if (server == null) return;

        for (Batch batch : batchesFor(server)) {
            NetworkRegistry.sendToClient(player, MESSAGE, batch);
        }
    }

    private static List<Batch> batchesFor(MinecraftServer server) {
        List<Batch> cached = cachedBatches;
        if (cached != null && cachedFor == server) return cached;

        List<Batch> built = buildBatches(server);
        cachedFor = server;
        cachedBatches = built;
        return built;
    }

    private static List<Batch> buildBatches(MinecraftServer server) {
        List<RecipeHolder<?>> selected = new ArrayList<>();
        RecipeManager manager = server.getRecipeManager();
        Set<RecipeSerializer<?>> serializers;
        synchronized (SYNCED_SERIALIZERS) {
            serializers = Set.copyOf(SYNCED_SERIALIZERS);
        }

        if (!serializers.isEmpty()) {
            for (RecipeHolder<?> holder : manager.getRecipes()) {
                if (holder == null || holder.value() == null) continue;
                if (serializers.contains(holder.value().getSerializer())) selected.add(holder);
            }
        }

        // Encoded once here so the split is by real size rather than a guess, and so the log line can say what
        // this actually costs on the wire.
        RegistryFriendlyByteBuf scratch = new RegistryFriendlyByteBuf(Unpooled.buffer(), server.registryAccess());
        List<Batch> batches = new ArrayList<>();
        List<RecipeHolder<?>> current = new ArrayList<>();
        int currentBytes = 0;
        int totalBytes = 0;

        try {
            for (RecipeHolder<?> holder : selected) {
                scratch.clear();
                int size;
                try {
                    encodeOne(scratch, holder);
                    size = scratch.readableBytes();
                } catch (Exception e) {
                    LibWoverRecipe.C.LOG.error("Recipe {} cannot be synchronised; skipping it.", holder.id(), e);
                    continue;
                }

                if (!current.isEmpty() && currentBytes + size > MAX_BATCH_BYTES) {
                    batches.add(new Batch(FORMAT_VERSION, batches.isEmpty(), List.copyOf(current)));
                    current = new ArrayList<>();
                    currentBytes = 0;
                }
                current.add(holder);
                currentBytes += size;
                totalBytes += size;
            }
        } finally {
            scratch.release();
        }

        // Always at least one batch, and always one marked first: an empty transfer is how a client is told to
        // drop whatever the last server told it.
        batches.add(new Batch(FORMAT_VERSION, batches.isEmpty(), List.copyOf(current)));

        LibWoverRecipe.C.LOG.info(
                "Synchronising {} recipes of {} registered serializer(s), {} bytes in {} message(s)",
                selected.size(), serializers.size(), totalBytes, batches.size()
        );
        return List.copyOf(batches);
    }

    // ---------------------------------------------------------------- client half

    // The accumulator is written wherever the payload is delivered and cleared on the client thread at
    // disconnect, so it is guarded; the index a recipe viewer reads is a volatile swap of an immutable map, so
    // reading it needs no lock and never sees a half-built batch.
    private static final List<RecipeHolder<?>> received = new ArrayList<>();
    private static volatile Map<RecipeType<?>, List<RecipeHolder<?>>> byType = Map.of();

    @ApiStatus.Internal
    public static void acceptOnClient(Batch batch) {
        if (batch.formatVersion() != FORMAT_VERSION) {
            LibWoverRecipe.C.LOG.warn(
                    "Ignoring synchronised recipes in format {} (this build reads {}). Recipe viewers will not " +
                            "list this server's custom recipes.",
                    batch.formatVersion(), FORMAT_VERSION
            );
            return;
        }

        int total;
        synchronized (received) {
            if (batch.first()) received.clear();
            received.addAll(batch.recipes());
            total = received.size();
            reindex();
        }

        LibWoverRecipe.C.LOG.info(
                "Received {} synchronised recipes in {} type(s): {}", total, byType.size(), typeSummary()
        );
    }

    @ApiStatus.Internal
    public static void clearOnClient() {
        synchronized (received) {
            received.clear();
            byType = Map.of();
        }
    }

    /**
     * {@code namespace:path=count} per recipe type. A recipe viewer showing an empty category is the symptom
     * this whole class exists to prevent, and this is the one line that says whether the recipes made it over
     * or the viewer dropped them afterwards.
     */
    private static String typeSummary() {
        return byType
                .entrySet()
                .stream()
                .map(e -> BuiltInRegistries.RECIPE_TYPE.getKey(e.getKey()) + "=" + e.getValue().size())
                .sorted()
                .collect(Collectors.joining(", "));
    }

    private static void reindex() {
        Map<RecipeType<?>, List<RecipeHolder<?>>> index = new HashMap<>();
        for (RecipeHolder<?> holder : received) {
            index.computeIfAbsent(holder.value().getType(), t -> new ArrayList<>()).add(holder);
        }
        Map<RecipeType<?>, List<RecipeHolder<?>>> frozen = new HashMap<>();
        index.forEach((type, holders) -> frozen.put(type, List.copyOf(holders)));
        byType = Map.copyOf(frozen);
    }

    // ---------------------------------------------------------------- query

    @SuppressWarnings("unchecked")
    public static <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeHolder<T>> allOfType(
            Level level,
            RecipeType<T> type
    ) {
        if (level != null && level.recipeAccess() instanceof RecipeManager manager) {
            List<RecipeHolder<T>> found = new ArrayList<>();
            for (RecipeHolder<?> holder : manager.getRecipes()) {
                if (holder == null || holder.value() == null) continue;
                if (holder.value().getType() == type) found.add((RecipeHolder<T>) holder);
            }
            return found;
        }

        return (Collection<RecipeHolder<T>>) (Collection<?>) byType.getOrDefault(type, List.of());
    }
}
