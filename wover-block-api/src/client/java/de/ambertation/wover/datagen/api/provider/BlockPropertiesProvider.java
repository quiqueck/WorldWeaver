package de.ambertation.wover.datagen.api.provider;

import de.ambertation.wover.block.api.render.RenderLayerBinding;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverDataProvider;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import com.google.common.hash.Hashing;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.ToIntFunction;
import org.jetbrains.annotations.NotNull;

/**
 * Writes a single, deterministic {@code block_properties.txt} snapshot of the runtime {@link BlockBehaviour}
 * properties of <em>every</em> block in {@link BuiltInRegistries#BLOCK} (vanilla + the consuming mod + any
 * other mod present at datagen time), one line per block sorted by id.
 *
 * <p>Block property changes have no other datagen surface, so a change to e.g. a block's destroy time is
 * otherwise invisible to a {@code diff -rq} of the generated assets. This file makes such changes show up as a
 * reviewable git diff. Register it on a datagen pack (e.g. {@code globalPack.addProvider(BlockPropertiesProvider::new)});
 * it is normally committed under {@code src/main/generated} but excluded from the packed jar via
 * {@code processResources { exclude 'block_properties.txt' }} in the consuming mod's build.gradle.
 *
 * <p>State-dependent columns (currently {@code lightEmission} and {@code mapColor}) are aggregated across
 * <em>all</em> blockstates rather than read from the default state only: a block that emits light only in a
 * {@code LIT} state (smelter, lanterns, forges, …) would otherwise report {@code 0} forever and a dropped
 * {@code lightLevel(...)} lambda would be invisible. When every state shares a value the single value is
 * emitted; otherwise a compact {@code min..max#<distinctCount>} summary is emitted (deterministic).
 */
public class BlockPropertiesProvider implements WoverDataProvider<DataProvider> {
    /**
     * {@code hasCollision} has no public getter on {@link BlockBehaviour}; read the protected final field
     * reflectively. Datagen runs in the named (mojmap) dev environment, so the field name is stable here.
     */
    private static final Field HAS_COLLISION_FIELD = resolveHasCollisionField();

    /**
     * {@code forceSolidOn} / {@code forceSolidOff} live on {@link BlockBehaviour.Properties} (retained on the
     * behaviour via its protected final {@code properties} field), with no public getter; read reflectively.
     */
    private static final Field PROPERTIES_FIELD = resolveBehaviourField("properties");
    private static final Field FORCE_SOLID_ON_FIELD = resolvePropertiesField("forceSolidOn");
    private static final Field FORCE_SOLID_OFF_FIELD = resolvePropertiesField("forceSolidOff");
    /**
     * The concrete {@link BlockBehaviour.OffsetType} a block was built with is not retained (it is baked into an
     * {@code OffsetFunction}), but it is fully reconstructable from the two protected max-offset getters plus the
     * public {@code hasOffsetFunction()}: no function → {@code NONE}, else {@code XZ} when the vertical offset is
     * {@code 0}, else {@code XYZ}. Read the two getters reflectively.
     */
    private static final Method MAX_VERTICAL_OFFSET_METHOD = resolveBehaviourMethod("getMaxVerticalOffset");

    private static Field resolveHasCollisionField() {
        return resolveBehaviourField("hasCollision");
    }

    private static Field resolveBehaviourField(String name) {
        try {
            final Field field = BlockBehaviour.class.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Could not resolve BlockBehaviour." + name + " for the block audit", e);
        }
    }

    private static Field resolvePropertiesField(String name) {
        try {
            final Field field = BlockBehaviour.Properties.class.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(
                    "Could not resolve BlockBehaviour.Properties." + name + " for the block audit", e);
        }
    }

    private static Method resolveBehaviourMethod(String name) {
        try {
            final Method method = BlockBehaviour.class.getDeclaredMethod(name);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Could not resolve BlockBehaviour." + name + " for the block audit", e);
        }
    }

    /**
     * A stable name for every {@link SoundType} constant, resolved by reflecting over {@link SoundType}'s
     * public static final fields and keying by identity. Lets the audit print e.g. {@code sound=METAL} /
     * {@code sound=NETHERRACK} instead of an unstable {@code toString()}, so a change to a block's
     * {@link SoundType} shows up as a reviewable diff.
     */
    private static final Map<SoundType, String> SOUND_TYPE_NAMES = resolveSoundTypeNames();

    private static Map<SoundType, String> resolveSoundTypeNames() {
        final Map<SoundType, String> map = new IdentityHashMap<>();
        for (Field field : SoundType.class.getFields()) {
            if (Modifier.isStatic(field.getModifiers())
                    && Modifier.isFinal(field.getModifiers())
                    && SoundType.class.equals(field.getType())) {
                try {
                    map.put((SoundType) field.get(null), field.getName());
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Could not read SoundType." + field.getName(), e);
                }
            }
        }
        return map;
    }

    /**
     * The stable name of the block's {@link SoundType} (e.g. {@code METAL}, {@code STONE}, {@code NETHERRACK}),
     * or, for a {@link SoundType} that is not one of {@link SoundType}'s named constants (a modded or inline
     * instance), a lowercase {@code path:<break-sound-path>} fallback so the value is still deterministic.
     */
    private static String soundName(Block block) {
        final SoundType soundType = block.defaultBlockState().getSoundType();
        final String name = SOUND_TYPE_NAMES.get(soundType);
        if (name != null) return name;
        return "path:" + soundType.getBreakSound().location().getPath();
    }

    protected final ModCore modCore;

    public BlockPropertiesProvider(ModCore modCore) {
        this.modCore = modCore;
    }

    @Override
    public DataProvider getProvider(
            FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        return new Provider(output);
    }

    private static String renderLayer(Block block) {
        return BlockTrait.runtimeTraits(block)
                         .filter(trait -> trait.is(RenderLayerBinding.RENDER_LAYER_KEY))
                         .filter(trait -> trait instanceof RenderLayerBinding)
                         .map(trait -> ((RenderLayerBinding) trait).layer())
                         .findFirst()
                         .map(layer -> switch (layer) {
                             case CUTOUT -> "CUTOUT";
                             case TRANSLUCENT -> "TRANSLUCENT";
                         })
                         .orElse("SOLID");
    }

    private static boolean hasCollision(Block block) {
        try {
            return HAS_COLLISION_FIELD.getBoolean(block);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Could not read BlockBehaviour.hasCollision for " + block, e);
        }
    }

    private static String forceSolid(Block block, Field field) {
        try {
            final Object properties = PROPERTIES_FIELD.get(block);
            return String.valueOf(field.getBoolean(properties));
        } catch (ReflectiveOperationException e) {
            return "err";
        }
    }

    /**
     * Reconstructs the {@link BlockBehaviour.OffsetType} the block was built with: {@code NONE} when it has no
     * offset function, {@code XZ} when it offsets only horizontally, {@code XYZ} when it also offsets vertically.
     */
    private static String offsetType(Block block) {
        try {
            if (!block.defaultBlockState().hasOffsetFunction()) return "NONE";
            final float maxVertical = (float) MAX_VERTICAL_OFFSET_METHOD.invoke(block);
            return maxVertical == 0.0F ? "XZ" : "XYZ";
        } catch (ReflectiveOperationException e) {
            return "err";
        }
    }

    /**
     * Aggregates an int-valued, state-dependent property over <em>all</em> possible blockstates. Emits the single
     * value when uniform, else a deterministic {@code min..max#<distinctCount>} summary (values compared
     * numerically). A per-state evaluation that throws contributes the sentinel {@code errValue} instead of
     * crashing the whole audit.
     */
    private static String aggregateInt(Block block, ToIntFunction<BlockState> extractor, int errValue) {
        final TreeSet<Integer> distinct = new TreeSet<>();
        for (BlockState state : block.getStateDefinition().getPossibleStates()) {
            int value;
            try {
                value = extractor.applyAsInt(state);
            } catch (Throwable t) {
                value = errValue;
            }
            distinct.add(value);
        }
        if (distinct.size() == 1) {
            return String.valueOf(distinct.first());
        }
        return distinct.first() + ".." + distinct.last() + "#" + distinct.size();
    }

    private static int mapColorId(BlockState state) {
        // A block's map-color function may return null for a state (some modded blocks do); emit -1 rather
        // than NPE, matching the historical default-state behaviour.
        final MapColor mapColor = state.getMapColor(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
        return mapColor == null ? -1 : mapColor.id;
    }

    /**
     * Evaluates a default-state block predicate against {@link EmptyBlockGetter}. Some blocks throw when probed
     * without a real level; such a probe yields {@code err} rather than crashing the audit.
     */
    private static String predicate(BlockPredicate predicate, BlockState state) {
        try {
            return String.valueOf(predicate.test(state, EmptyBlockGetter.INSTANCE, BlockPos.ZERO));
        } catch (Throwable t) {
            return "err";
        }
    }

    @FunctionalInterface
    private interface BlockPredicate {
        boolean test(BlockState state, EmptyBlockGetter level, BlockPos pos);
    }

    /**
     * {@code isValidSpawn} probed with a fixed ({@link EntityTypes#OCELOT}, {@link EntityTypes#ZOMBIE}) pair so
     * that {@code Blocks::ocelotOrParrot}-style lambdas (which allow only some entities) are distinguishable from
     * a blanket allow/deny. Emitted as {@code ocelot/zombie} booleans, e.g. {@code isValidSpawn=true/false}.
     * <p>
     * 26.2 moved the {@code EntityType.*} constants off {@link EntityType} itself (which now only carries the
     * two codecs) into the new {@link EntityTypes} holder class; the constants themselves are unchanged, so the
     * probed pair is identical to 26.1's.
     */
    private static String validSpawn(BlockState state) {
        final String ocelot = spawnFor(state, EntityTypes.OCELOT);
        final String zombie = spawnFor(state, EntityTypes.ZOMBIE);
        return ocelot + "/" + zombie;
    }

    private static String spawnFor(BlockState state, EntityType<?> type) {
        try {
            return String.valueOf(state.isValidSpawn(EmptyBlockGetter.INSTANCE, BlockPos.ZERO, type));
        } catch (Throwable t) {
            return "err";
        }
    }

    private static String lineFor(Identifier id, Block block) {
        final BlockState state = block.defaultBlockState();
        // Locale-independent formatting throughout (String.valueOf / Float.toString use '.', never a locale
        // decimal comma), so the file is byte-identical regardless of the machine's default locale.
        return "{\"id\":\"" + id + "\""
                + ",\"class\":\"" + block.getClass().getSimpleName() + "\""
                + ",\"destroyTime\":\"" + block.defaultDestroyTime() + "\""
                + ",\"instabreak\":\"" + (block.defaultDestroyTime() == 0.0F) + "\""
                + ",\"resistance\":\"" + block.getExplosionResistance() + "\""
                + ",\"reqTool\":\"" + state.requiresCorrectToolForDrops() + "\""
                + ",\"mapColor\":\"" + aggregateInt(block, BlockPropertiesProvider::mapColorId, -1) + "\""
                + ",\"instrument\":\"" + state.instrument().name() + "\""
                + ",\"sound\":\"" + soundName(block) + "\""
                + ",\"friction\":\"" + block.getFriction() + "\""
                + ",\"speed\":\"" + block.getSpeedFactor() + "\""
                + ",\"jump\":\"" + block.getJumpFactor() + "\""
                + ",\"ignitedByLava\":\"" + state.ignitedByLava() + "\""
                + ",\"lightEmission\":\"" + aggregateInt(block, BlockState::getLightEmission, 0) + "\""
                + ",\"replaceable\":\"" + state.canBeReplaced() + "\""
                + ",\"waterlogged\":\"" + state.hasProperty(BlockStateProperties.WATERLOGGED) + "\""
                + ",\"randomlyTicks\":\"" + state.isRandomlyTicking() + "\""
                + ",\"hasCollision\":\"" + hasCollision(block) + "\""
                + ",\"canOcclude\":\"" + state.canOcclude() + "\""
                + ",\"renderLayer\":\"" + renderLayer(block) + "\""
                + ",\"pushReaction\":\"" + state.getPistonPushReaction().name() + "\""
                + ",\"offsetType\":\"" + offsetType(block) + "\""
                + ",\"forceSolidOn\":\"" + forceSolid(block, FORCE_SOLID_ON_FIELD) + "\""
                + ",\"forceSolidOff\":\"" + forceSolid(block, FORCE_SOLID_OFF_FIELD) + "\""
                + ",\"dynamicShape\":\"" + block.hasDynamicShape() + "\""
                + ",\"spawnTerrainParticles\":\"" + state.shouldSpawnTerrainParticles() + "\""
                + ",\"isSuffocating\":\"" + predicate(BlockState::isSuffocating, state) + "\""
                + ",\"isViewBlocking\":\"" + predicate(BlockState::isViewBlocking, state) + "\""
                + ",\"isRedstoneConductor\":\"" + predicate(BlockState::isRedstoneConductor, state) + "\""
                + ",\"hasPostProcess\":\"" + predicate((s, g, p) -> s.getPostProcessPos(g, p) != null, state) + "\""
                // 26.2 narrowed emissive rendering from a BlockBehaviour.StatePredicate (state, level, pos) to a
                // plain Predicate<BlockState>, so BlockState#emissiveRendering() no longer takes a level or a
                // position. Probed through the same err-guarded helper as the other predicate columns (with the
                // now-unused getter/pos arguments dropped on the floor) so the column keeps its exact format.
                + ",\"emissiveRendering\":\"" + predicate((s, g, p) -> s.emissiveRendering(), state) + "\""
                + ",\"isValidSpawn\":\"" + validSpawn(state) + "\"}";
    }

    private class Provider implements DataProvider {
        private final FabricPackOutput output;

        private Provider(FabricPackOutput output) {
            this.output = output;
        }

        @Override
        public @NotNull CompletableFuture<?> run(@NotNull CachedOutput writer) {
            final List<String> lines = new ArrayList<>();
            for (Block block : BuiltInRegistries.BLOCK) {
                final Identifier id = BuiltInRegistries.BLOCK.getKey(block);
                if (!id.getNamespace().equals(modCore.namespace)) continue;
                lines.add(lineFor(id, block));
            }
            // Sorting by the full line orders by the (unique) id prefix, so the file is fully deterministic.
            Collections.sort(lines);

            final byte[] bytes = ("[\n" + String.join(",\n", lines) + "\n]\n").getBytes(StandardCharsets.UTF_8);
            final Path path = output.getOutputFolder().resolve("block_properties.json");
            try {
                writer.writeIfNeeded(path, bytes, Hashing.sha1().hashBytes(bytes));
            } catch (IOException e) {
                return CompletableFuture.failedFuture(e);
            }
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public @NotNull String getName() {
            return "Block Properties Audit (" + modCore.namespace + ")";
        }
    }
}
