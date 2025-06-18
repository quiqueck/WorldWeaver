package org.betterx.wover.block.api.trait;

import org.betterx.wover.block.api.BlockDefinition;

import net.minecraft.world.level.block.Block;

import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;

public class FlammableBlockTrait extends BlockTrait<Block, FlammableBlockTrait.Config, FlammableBlockTrait.RuntimeTrait> {
    public record Config(int burn, int speed) implements BlockTrait.Config {
        public static final Config DEFAULT = new Config(5, 5);
    }

    public final static class RuntimeTrait extends BlockTrait.RuntimeTrait<Block, RuntimeTrait> {
        private RuntimeTrait(FlammableBlockTrait sourceTrait) {
            super(sourceTrait);
        }
    }

    FlammableBlockTrait() {
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
    public FlammableBlockTrait.RuntimeTrait forRuntime(FlammableBlockTrait.Config config) {
        return new RuntimeTrait(this);
    }

    @Override
    public FlammableBlockTrait.Config getDefaultConfig() {
        return Config.DEFAULT;
    }

    @Override
    public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition, Config config) {
        definition.getProperties().ignitedByLava();
    }

    @Override
    public void afterBlockRegistration(
            Block block,
            BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition,
            FlammableBlockTrait.Config config
    ) {
        registerAsFlammable(block, config.burn, config.speed);
    }
}
