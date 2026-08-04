package de.ambertation.wover.block.impl.trait.behaviour;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.api.trait.AbstractBlockTraitBuilder;
import de.ambertation.wover.block.api.trait.BlockTraitKey;
import de.ambertation.wover.block.api.trait.GenericBlockTrait;
import de.ambertation.wover.block.api.trait.behaviour.FlammableBlockTrait;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.entrypoint.LibWoverBlock;

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
        @SuppressWarnings("removal")
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            definition.ignitedByLava();
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
