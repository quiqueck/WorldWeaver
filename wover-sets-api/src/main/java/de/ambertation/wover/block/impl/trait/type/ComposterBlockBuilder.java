package de.ambertation.wover.block.impl.trait.type;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.*;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverSets;
import de.ambertation.wover.loot.api.LootLookupProvider;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class ComposterBlockBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.BuilderWithDefaults {
    public static final GenericBlockTrait.BuilderWithDefaults BUILDER = new ComposterBlockBuilder();

    private ComposterBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "is_composter"));
    }

    public @Nullable List<BlockTrait<?, ?>> withDefault() {
        if (!ModCore.isDatagen()) return null;
        return combine(new Trait(), BlockTraits.LOOT_TABLE.with(ComposterBlockBuilder::drops));
    }

    private static LootTable.Builder drops(
            ResourceKey<LootTable> tableKey,
            ResourceKey<Block> blockKey,
            Block block,
            LootLookupProvider provider
    ) {
        return provider.dropComposter(block);
    }

    private class Trait extends BlockTraitImpl.Generic {
        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            definition.addTags(CommonBlockTags.COMPOSTER);
            if (definition.hasTrait(BlockTraits.WOOD_BLOCK)) {
                definition.addTags(CommonBlockTags.WOODEN_COMPOSTER);
            }
        }
    }
}
