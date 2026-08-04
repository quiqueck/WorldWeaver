package de.ambertation.wover.block.impl.trait.behaviour;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.AbstractBlockTraitBuilder;
import de.ambertation.wover.block.api.trait.BlockTraitKey;
import de.ambertation.wover.block.api.trait.behaviour.ValidForBlockEntityTypeTrait;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.entrypoint.LibWoverSets;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import org.jetbrains.annotations.NotNull;


public class ValidForBlockEntityTypeBuilder extends AbstractBlockTraitBuilder<Block, ValidForBlockEntityTypeTrait> implements ValidForBlockEntityTypeTrait.Builder {
    public static final ValidForBlockEntityTypeTrait.Builder BUILDER = new ValidForBlockEntityTypeBuilder();

    private ValidForBlockEntityTypeBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "valid_block_entity"));
    }


    @Override
    public ValidForBlockEntityTypeTrait with(@NotNull BlockEntityType<?> blockEntityType) {
        return new Trait(blockEntityType);
    }

    class Trait extends BlockTraitImpl<Block, ValidForBlockEntityTypeTrait> implements ValidForBlockEntityTypeTrait {
        private final @NotNull BlockEntityType<?> blockEntityType;

        Trait(@NotNull BlockEntityType<?> blockEntityType) {
            this.blockEntityType = blockEntityType;
        }

        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public void afterBlockRegistration(
                Block block,
                BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition
        ) {
            ValidForBlockEntityTypeTrait.makeValid(block, blockEntityType);
        }

        @Override
        public @NotNull BlockEntityType<?> getBlockEntityType() {
            return blockEntityType;
        }
    }
}