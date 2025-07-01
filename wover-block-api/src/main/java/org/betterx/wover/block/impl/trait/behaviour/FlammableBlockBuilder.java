package org.betterx.wover.block.impl.trait.behaviour;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.trait.AbstractBlockTraitBuilder;
import org.betterx.wover.block.api.trait.BlockTraitKey;
import org.betterx.wover.block.api.trait.GenericBlockTrait;
import org.betterx.wover.block.api.trait.behaviour.FlammableBlockTrait;
import org.betterx.wover.block.impl.trait.BlockTraitImpl;
import org.betterx.wover.entrypoint.LibWoverBlock;

import net.minecraft.world.level.block.Block;

public class FlammableBlockBuilder extends AbstractBlockTraitBuilder<Block, GenericBlockTrait> implements FlammableBlockTrait.Builder {
    public static final FlammableBlockTrait.Builder BUILDER = new FlammableBlockBuilder();
    private final Trait DEFAULT = new Trait(5, 5);

    private FlammableBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverBlock.C, "flammable"));
    }

    public FlammableBlockTrait withDefault() {
        return DEFAULT;
    }

    public FlammableBlockTrait with(int burn, int speed) {
        if (burn == 5 && speed == 5) {
            return DEFAULT;
        }

        return new Trait(burn, speed);
    }

    private final class Trait extends BlockTraitImpl<Block, GenericBlockTrait> implements FlammableBlockTrait {
        private final int burn;
        private final int speed;

        Trait(int burn, int speed) {
            this.burn = burn;
            this.speed = speed;
        }

        @Override
        public BlockTraitKey key() {
            return traitKey;
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
            BlockRegistry.registerAsFlammable(block, this.burn, this.speed);
        }

        @Override
        public int burn() {
            return this.burn;
        }

        @Override
        public int speed() {
            return this.speed;
        }
    }
}
