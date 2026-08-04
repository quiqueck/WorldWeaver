package de.ambertation.wover.block.impl.trait.type;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.*;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverSets;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class ButtonBlockBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.BuilderWithDefaults {
    public static final GenericBlockTrait.BuilderWithDefaults BUILDER = new ButtonBlockBuilder();
    private final List<BlockTrait<?, ?>> DEFAULT;

    private ButtonBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "is_button"));
        DEFAULT = combine(new Trait(), BlockTraits.LOOT_TABLE.dropSelf());
    }

    @Override
    public @Nullable List<BlockTrait<?, ?>> withDefault() {
        return DEFAULT;
    }

    private class Trait extends BlockTraitImpl.Generic {
        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            definition.noOcclusion();

            if (ModCore.isDatagen()) {
                definition.addTags(BlockTags.BUTTONS);
                definition.addItemTags(ItemTags.BUTTONS);

                if (definition.hasTrait(BlockTraits.WOOD_BLOCK)) {
                    definition.strength(0.5F, 0.5F);
                    definition.addTags(BlockTags.WOODEN_BUTTONS);
                    definition.addItemTags(ItemTags.WOODEN_BUTTONS);
                }

                if (definition.hasTrait(BlockTraits.STONE_BLOCK)) {
                    definition.addTags(BlockTags.STONE_BUTTONS);
                    definition.addItemTags(ItemTags.STONE_BUTTONS);
                }
            }
        }
    }
}
