package de.ambertation.wover.block.api.trait;

import de.ambertation.wover.block.api.BlockDefinition;

import net.minecraft.world.level.block.Block;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A build-time trait that can be added to a {@link BlockDefinition} to attach additional behavior or
 * properties to a block. A trait is configured once (via {@link #configure(BlockDefinition)}) while the
 * block definition is being built, optionally contributes a {@link RuntimeBlockTrait} that is stored on the
 * finished block (if it implements {@link BlockWithTraits}), and can run further setup once the block is
 * registered (via {@link #afterBlockRegistration(Block, BlockDefinition)}).
 * <p>
 * Traits are usually not implemented directly; instead a paired {@link BlockTraitBuilder} exposes one or
 * more ready-made instances (see {@link BlockDefinition#addTrait(BlockTraitBuilder.WithDefault)}).
 *
 * @param <B> The type of {@link Block} this trait applies to
 * @param <R> The runtime trait type this trait produces via {@link #forRuntime()}
 */
public interface BlockTrait<B extends Block, R extends RuntimeBlockTrait<B, R>> extends RuntimeBlockTrait<B, R> {
    /**
     * Casts {@code block} to {@link BlockWithTraits} if it implements the interface.
     *
     * @param block The block to cast
     * @param <B>   The block type
     * @return The block as {@link BlockWithTraits}, or {@code null} if it doesn't implement the interface
     */
    @SuppressWarnings("unchecked")
    static <B extends Block> @Nullable BlockWithTraits<B> asBlockWithTraits(
            @Nullable B block
    ) {
        if (block instanceof BlockWithTraits<?> blockWithTraits) {
            return (BlockWithTraits<B>) blockWithTraits;
        }
        return null;
    }

    /**
     * Casts {@code block} to {@link BlockWithTraits}.
     *
     * @param block The block to cast
     * @param <B>   The block type
     * @return The block as {@link BlockWithTraits}
     * @throws IllegalArgumentException if the block does not implement {@link BlockWithTraits}
     */
    static <B extends Block> @NotNull BlockWithTraits<B> asBlockWithTraitsOrThrow(
            @Nullable B block
    ) {
        BlockWithTraits<B> blockWithTraits = asBlockWithTraits(block);
        if (blockWithTraits == null) {
            throw new IllegalArgumentException("Block does not implement BlockWithTraits: " + block);
        }
        return blockWithTraits;
    }

    /**
     * Streams every runtime trait attached to {@code block}, across all trait keys.
     *
     * @param block The block to inspect
     * @return A stream of the block's runtime traits, empty if the block has none or isn't a {@link BlockWithTraits}
     */
    static @NotNull Stream<? extends RuntimeBlockTrait<?, ?>> runtimeTraits(
            @Nullable Block block
    ) {
        var blockWithTraits = asBlockWithTraits(block);
        if (blockWithTraits == null) return Stream.empty();
        ;
        final var traits = blockWithTraits.wover_traits();
        if (traits == null || traits.isEmpty()) {
            return Stream.empty();
        }
        // Flatten the map values into a single stream
        return traits.values().stream().flatMap(Collection::stream);
    }

    /**
     * Checks whether {@code block} has any runtime traits attached at all.
     *
     * @param block The block to inspect
     * @return {@code true} if the block has at least one runtime trait
     */
    static boolean hasRuntimeTraits(Block block) {
        var blockWithTraits = asBlockWithTraits(block);
        if (blockWithTraits == null) return false;
        final var traits = blockWithTraits.wover_traits();
        return traits != null && !traits.isEmpty();
    }

    /**
     * Checks whether {@code block} has a runtime trait registered under the given key.
     *
     * @param block    The block to inspect
     * @param traitKey The trait key to check for
     * @return {@code true} if the block has a matching runtime trait
     */
    static boolean hasRuntimeTrait(@Nullable Block block, BlockTraitKey traitKey) {
        var blockWithTraits = asBlockWithTraits(block);
        if (blockWithTraits == null) return false;

        final var traits = blockWithTraits.wover_traits();
        return traits != null && traits.get(traitKey) != null;
    }

    /**
     * Gets every runtime trait attached to {@code block} under the given key.
     *
     * @param block    The block to inspect
     * @param traitKey The trait key to look up
     * @param <B>      The block type
     * @param <R>      The runtime trait type
     * @return The matching runtime traits, or {@code null} if there are none
     */
    static <B extends Block, R extends RuntimeBlockTrait<B, R>> @Nullable List<R> getRuntimeTraits(
            B block,
            BlockTraitKey traitKey
    ) {
        var blockWithTraits = asBlockWithTraits(block);
        if (blockWithTraits == null) return null;

        final var traits = blockWithTraits.wover_traits();
        List<R> result = null;
        if (traits != null) {
            result = (List<R>) traits.get(traitKey);
        }

        if (result == null || result.isEmpty()) return null;
        return result;
    }

    /**
     * Creates the runtime representation of this trait that will be attached to the finished block (if it
     * implements {@link BlockWithTraits}).
     *
     * @return The runtime trait, or {@code null} if this trait needs no runtime component
     */
    R forRuntime();

    /**
     * Called while the owning {@link BlockDefinition} is being built, before the block instance is created.
     * Implementations typically call configuration methods on {@code definition} (e.g.
     * {@link BlockDefinition#ignitedByLava()}) to apply the trait's effect.
     *
     * @param definition The block definition being built
     */
    void configure(BlockDefinition<B, ? extends BlockDefinition<B, ?>> definition);

    /**
     * Called once the block has been built and registered with its {@link de.ambertation.wover.block.api.BlockRegistry}.
     * Implementations typically use this to register the block with other systems (e.g.
     * {@link de.ambertation.wover.block.api.BlockRegistry#registerAsFlammable(Block, int, int)}).
     *
     * @param block      The already registered block instance
     * @param definition The block definition that produced the block
     */
    void afterBlockRegistration(
            B block,
            BlockDefinition<B, ? extends BlockDefinition<B, ?>> definition
    );

    /**
     * Indicates whether this trait should only be available once in a block definition.
     * If true, only the latest instance of this trait will be kept in the block definition.
     * If false, multiple instances of this trait can exist in the block definition.
     *
     * @return
     */
    default boolean keepLatestOnly() {
        return false;
    }
}
