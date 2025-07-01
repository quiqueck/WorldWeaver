package org.betterx.wover.block.api.trait;

import org.betterx.wover.block.api.BlockDefinition;

import net.minecraft.world.level.block.Block;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BlockTrait<B extends Block, R extends RuntimeBlockTrait<B, R>> extends RuntimeBlockTrait<B, R> {
    @SuppressWarnings("unchecked")
    static <B extends Block> @Nullable BlockWithTraits<B> asBlockWithTraits(
            @Nullable B block
    ) {
        if (block instanceof BlockWithTraits<?> blockWithTraits) {
            return (BlockWithTraits<B>) blockWithTraits;
        }
        return null;
    }

    static <B extends Block> @NotNull BlockWithTraits<B> asBlockWithTraitsOrThrow(
            @Nullable B block
    ) {
        BlockWithTraits<B> blockWithTraits = asBlockWithTraits(block);
        if (blockWithTraits == null) {
            throw new IllegalArgumentException("Block does not implement BlockWithTraits: " + block);
        }
        return blockWithTraits;
    }

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

    static boolean hasRuntimeTraits(Block block) {
        var blockWithTraits = asBlockWithTraits(block);
        if (blockWithTraits == null) return false;
        final var traits = blockWithTraits.wover_traits();
        return traits != null && !traits.isEmpty();
    }

    static boolean hasRuntimeTrait(@Nullable Block block, BlockTraitKey traitKey) {
        var blockWithTraits = asBlockWithTraits(block);
        if (blockWithTraits == null) return false;

        final var traits = blockWithTraits.wover_traits();
        return traits != null && traits.get(traitKey) != null;
    }

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

    R forRuntime();
    void configure(BlockDefinition<B, ? extends BlockDefinition<B, ?>> definition);
    void afterBlockRegistration(
            B block,
            BlockDefinition<B, ? extends BlockDefinition<B, ?>> definition
    );
}
