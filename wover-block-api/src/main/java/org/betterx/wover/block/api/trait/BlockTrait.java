package org.betterx.wover.block.api.trait;

import org.betterx.wover.block.api.BlockDefinition;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BlockTrait<B extends Block, C extends BlockTrait.Config, R extends BlockTrait.RuntimeTrait<B, R>> {
    public interface Config {

    }

    public final static class NoConfig implements Config {
        public static final NoConfig NO_CONFIG = new NoConfig();
    }

    public abstract static class BlockTraitNoConfig<B extends Block, R extends BlockTrait.RuntimeTrait<B, R>>
            extends BlockTrait<B, NoConfig, R> {

        protected BlockTraitNoConfig() {

        }

        @Override
        public NoConfig getDefaultConfig() {
            return NoConfig.NO_CONFIG;
        }
    }

    public static class RuntimeTrait<B extends Block, R extends BlockTrait.RuntimeTrait<B, R>> {
        private final Class<BlockTrait<B, ?, R>> traitClass;

        @SuppressWarnings("unchecked")
        protected RuntimeTrait(@NotNull BlockTrait<B, ?, R> sourceTrait) {
            this((Class<BlockTrait<B, ?, R>>) sourceTrait.getClass());
        }

        @SuppressWarnings("unchecked")
        protected RuntimeTrait(Class<? extends BlockTrait<B, ?, R>> traitClass) {
            this.traitClass = (Class<BlockTrait<B, ?, R>>) traitClass;
        }

        public boolean is(@Nullable BlockTrait<?, ?, ?> trait) {
            if (trait == null) return false;
            return this.traitClass.equals(trait.getClass());
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj != null && getClass() == obj.getClass()) return true;
            if (obj instanceof BlockTrait<?, ?, ?> bt) {
                return this.is(bt);
            }

            return super.equals(obj);
        }
    }

    @SuppressWarnings("unchecked")
    public static <B extends Block> @Nullable BlockWithTraits<B> asBlockWithTraits(
            @Nullable B block
    ) {
        if (block instanceof BlockWithTraits<?> blockWithTraits) {
            return (BlockWithTraits<B>) blockWithTraits;
        }
        return null;
    }

    public static <B extends Block> @NotNull BlockWithTraits<B> asBlockWithTraitsOrThrow(
            @Nullable B block
    ) {
        BlockWithTraits<B> blockWithTraits = asBlockWithTraits(block);
        if (blockWithTraits == null) {
            throw new IllegalArgumentException("Block does not implement BlockWithTraits: " + block);
        }
        return blockWithTraits;
    }

    public static boolean hasRuntimeTraits(Block block) {
        var blockWithTraits = asBlockWithTraits(block);
        if (blockWithTraits == null) return false;
        final var traits = blockWithTraits.wover_traits();
        return traits != null && !traits.isEmpty();
    }


    public static <B extends Block> void forEachRuntimeTrait(
            @Nullable B block,
            @NotNull BiConsumer<B, RuntimeTrait<B, ?>> consumer
    ) {
        BlockWithTraits<B> blockWithTraits = asBlockWithTraits(block);
        if (blockWithTraits == null) return;
        final var traits = blockWithTraits.wover_traits();
        if (traits != null) {
            traits.forEach(trait -> consumer.accept(block, trait));
        }
    }

    protected BlockTrait() {
    }


    public boolean hasRuntimeTrait(@Nullable B block) {
        var blockWithTraits = asBlockWithTraits(block);
        if (blockWithTraits == null) return false;

        final var traits = blockWithTraits.wover_traits();
        return traits != null && traits.stream().anyMatch(this::is);
    }

    public @Nullable R getRuntimeTrait(B block) {
        var blockWithTraits = asBlockWithTraits(block);
        if (blockWithTraits == null) return null;

        final var traits = blockWithTraits.wover_traits();
        if (traits != null) {
            for (BlockTrait.RuntimeTrait<B, ?> trait : traits) {
                if (trait != null && trait.is(this)) {
                    @SuppressWarnings("unchecked")
                    R result = (R) trait;
                    return result;
                }
            }
        }
        return null;
    }


    public boolean datagenOnly() {
        return this.clientOnly();
    }

    public boolean clientOnly() {
        return false;
    }

    public R forRuntime(C config) {
        return null;
    }

    public boolean is(@Nullable RuntimeTrait<?, ?> trait) {
        if (trait == null) return false;
        return trait.is(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj != null && getClass() == obj.getClass()) return true;
        if (obj instanceof BlockTrait.RuntimeTrait<?, ?> rt) {
            return this.is(rt);
        }

        return super.equals(obj);
    }

    public abstract C getDefaultConfig();

    public void configureProperties(BlockBehaviour.Properties properties, C config) {
        // Default implementation does nothing
    }

    public void afterBlockRegistration(
            B block,
            BlockDefinition<B, ? extends BlockDefinition<B, ?>> definition,
            C config
    ) {
        // Default implementation does nothing
    }

}
