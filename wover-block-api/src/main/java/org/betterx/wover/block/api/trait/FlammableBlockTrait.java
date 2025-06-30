package org.betterx.wover.block.api.trait;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.entrypoint.LibWoverBlock;

import net.minecraft.world.level.block.Block;

import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;

import java.util.List;

public final class FlammableBlockTrait extends BlockTrait<Block, FlammableBlockTrait.RuntimeTrait> {
    public static final FlammableBlockTrait.Builder BUILDER = new Builder();
    private static final FlammableBlockTrait DEFAULT = new FlammableBlockTrait(5, 5);

    public static class Builder extends BlockTrait.TraitBuilder {
        private Builder() {
            super(BlockTraitKey.of(LibWoverBlock.C, "flammable"));
        }

        public FlammableBlockTrait withDefault() {
            return DEFAULT;
        }

        public FlammableBlockTrait with(int burn, int speed) {
            if (burn == 5 && speed == 5) {
                return DEFAULT;
            }

            return new FlammableBlockTrait(burn, speed);
        }

        @Override
        @SuppressWarnings("unchecked")
        public List<RuntimeTrait> getRuntimeTraits(Block block) {
            return BlockTrait.getRuntimeTraits(block, ID);
        }
    }

    public final static class RuntimeTrait extends RuntimeBlockTrait<Block, RuntimeTrait> {
        private RuntimeTrait(FlammableBlockTrait sourceTrait) {
            super(sourceTrait);
        }
    }

    public final int burn;
    public final int speed;

    FlammableBlockTrait(int burn, int speed) {
        super(BUILDER.ID);
        this.burn = burn;
        this.speed = speed;
    }

    public static void registerAsFlammable(Block block) {
        registerAsFlammable(block, 5, 5);
    }

    public static void registerAsFlammable(
            Block block,
            int burn,
            int spread
    ) {
        if (block.defaultBlockState().ignitedByLava()
                && FlammableBlockRegistry.getDefaultInstance()
                                         .get(block)
                                         .getBurnChance() == 0) {
            FlammableBlockRegistry.getDefaultInstance().add(block, burn, spread);
        }
    }

    @Override
    public FlammableBlockTrait.RuntimeTrait forRuntime() {
        return new RuntimeTrait(this);
    }

    @Override
    public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
        definition.getProperties().ignitedByLava();
    }

    @Override
    public void afterBlockRegistration(
            Block block,
            BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition
    ) {
        registerAsFlammable(block, this.burn, this.speed);
    }
}
