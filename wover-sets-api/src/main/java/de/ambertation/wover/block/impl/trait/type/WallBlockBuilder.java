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

public class WallBlockBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.BuilderWithDefaults {
    public static final GenericBlockTrait.BuilderWithDefaults BUILDER = new WallBlockBuilder();
    private final Trait DEFAULT;

    private WallBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "is_wall"));
        this.DEFAULT = new Trait();
    }


    public @Nullable List<BlockTrait<?, ?>> withDefault() {
        if (!ModCore.isDatagen()) return combine(
                DEFAULT
        );
        return combine(
                DEFAULT,
                BlockTraits.LOOT_TABLE.dropSelf()
        );
    }

    private class Trait extends BlockTraitImpl.Generic {
        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            definition.forceSolidOn();

            if (ModCore.isDatagen()) {
                definition.addTags(BlockTags.WALLS);
                definition.addItemTags(ItemTags.WALLS);
            }
        }
    }
}
