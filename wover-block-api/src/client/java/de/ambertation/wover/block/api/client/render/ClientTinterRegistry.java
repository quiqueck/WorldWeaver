package de.ambertation.wover.block.api.client.render;

import de.ambertation.wover.block.api.render.TintBinding;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.render.TinterKey;
import de.ambertation.wover.entrypoint.LibWoverBlock;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.world.level.block.Block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Client-side registry mapping a {@link TinterKey} to the {@link TintSourceFactory} that builds its
 * {@code BlockTintSource} - the tint analogue of {@code ClientBlockModelRegistry}. wover populates it with the
 * built-in catalogue from a {@code wover.client.traits} entrypoint; third-party mods register their own keys the
 * same way.
 * <p>
 * Resolved sources are cached per block: the in-world colour walk and the datagen item-tint walk both ask for the
 * same block, and a source may be non-trivial to build.
 */
@Environment(EnvType.CLIENT)
public class ClientTinterRegistry {
    private static final Map<TinterKey<?>, TintSourceFactory<?>> FACTORIES = new HashMap<>();
    private static final Map<Block, BlockTintSource> RESOLVED = new HashMap<>();

    private ClientTinterRegistry() {
    }

    /**
     * Registers the factory for a tinter key. Later registrations for the same key replace earlier ones.
     *
     * @param key     the tint shape
     * @param factory builds the tint source for that shape
     * @param <P>     the payload type
     */
    public static <P> void register(TinterKey<P> key, TintSourceFactory<P> factory) {
        FACTORIES.put(key, factory);
    }

    /**
     * Resolves the tint source a block carries, if any - the lookup for code that has a block and wants its
     * colour, rather than a binding already in hand (particles matching the block they fall from, tooltips, ...).
     *
     * @param block the block to look up
     * @return the block's tint source, or {@code null} if it carries no tint binding
     */
    public static @Nullable BlockTintSource sourceFor(Block block) {
        final var bindings = BlockTrait.<Block, TintBinding>getRuntimeTraits(block, TintBinding.TINT_KEY);
        if (bindings == null || bindings.isEmpty()) return null;
        return resolve(bindings.getLast(), block);
    }

    /**
     * Resolves the tint source for a block's binding.
     *
     * @param binding the block's tint binding
     * @param block   the block being tinted
     * @return the tint source, or {@code null} if the key was never registered
     */
    @SuppressWarnings("unchecked")
    public static @Nullable BlockTintSource resolve(TintBinding binding, Block block) {
        final var cached = RESOLVED.get(block);
        if (cached != null) return cached;

        final TintSourceFactory<Object> factory = (TintSourceFactory<Object>) FACTORIES.get(binding.tinterKey());
        if (factory == null) {
            LibWoverBlock.C.LOG.error(
                    "No client tint source factory registered for " + binding.tinterKey() + " (block " + block + ")"
            );
            return null;
        }

        final var source = factory.create(binding.payload(), block);
        RESOLVED.put(block, source);
        return source;
    }
}
