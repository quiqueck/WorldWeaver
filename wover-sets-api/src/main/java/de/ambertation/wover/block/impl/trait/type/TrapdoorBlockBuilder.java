package de.ambertation.wover.block.impl.trait.type;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.render.BlockRenderTraits;
import de.ambertation.wover.block.api.trait.*;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverSets;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class TrapdoorBlockBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.BuilderWithDefaults {
    public static final GenericBlockTrait.BuilderWithDefaults BUILDER = new TrapdoorBlockBuilder();
    private final Trait DEFAULT;

    private TrapdoorBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "is_trapdoor"));
        this.DEFAULT = new Trait();
    }


    public @Nullable List<BlockTrait<?, ?>> withDefault() {
        if (!ModCore.isDatagen()) return combine(
                DEFAULT,
                BlockRenderTraits.RENDER_LAYER.cutout()
        );
        return combine(
                DEFAULT,
                BlockTraits.LOOT_TABLE.dropSelf(),
                BlockRenderTraits.RENDER_LAYER.cutout()
        );
    }

    private class Trait extends BlockTraitImpl.Generic {
        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            definition
                    .strength(3.0F)
                    .noOcclusion()
                    .isValidSpawn(Blocks::never);

            if (ModCore.isDatagen()) {
                definition.addTags(BlockTags.TRAPDOORS);
                definition.addItemTags(ItemTags.TRAPDOORS);

                if (definition.hasTrait(BlockTraits.WOOD_BLOCK)) {
                    definition.addTags(BlockTags.WOODEN_TRAPDOORS);
                    definition.addItemTags(ItemTags.WOODEN_TRAPDOORS);
                }
            }
        }
    }
}
