package de.ambertation.wover.block.impl.trait.behaviour;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.AbstractBlockTraitBuilder;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockTraitKey;
import de.ambertation.wover.block.api.trait.GenericBlockTrait;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverSets;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.Nullable;

public class ClimbableBlockTraitBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.BuilderWithDefault {
    public static final GenericBlockTrait.BuilderWithDefault BUILDER = new ClimbableBlockTraitBuilder();

    private ClimbableBlockTraitBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "climbable"));
    }

    public @Nullable BlockTrait<?, ?> withDefault() {
        if (!ModCore.isDatagen()) return null;
        return new Trait();
    }

    class Trait extends BlockTraitImpl.Generic {
        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            definition.addTags(BlockTags.CLIMBABLE);
        }
    }
}
