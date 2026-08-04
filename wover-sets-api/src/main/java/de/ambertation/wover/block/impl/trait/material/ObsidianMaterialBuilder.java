package de.ambertation.wover.block.impl.trait.material;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.*;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverSets;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class ObsidianMaterialBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.BuilderWithDefaults {
    public static final GenericBlockTrait.BuilderWithDefaults BUILDER = new ObsidianMaterialBuilder();
    private final GenericBlockTrait DEFAULT = new Trait();

    private ObsidianMaterialBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "obsidian"));
    }

    public @Nullable List<BlockTrait<?, ?>> withDefault() {
        if (!ModCore.isDatagen()) return combine(DEFAULT);
        return combine(DEFAULT, BlockTraits.MINEABLE_WITH.needsPickAxe());
    }

    class Trait extends BlockTraitImpl.Generic {
        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            // IS_OBSIDIAN and NEEDS_DIAMOND_TOOL come from the BehaviourObsidian marker (via bclib's
            // BCLAutoBlockTagProvider), so they must be supplied here for this trait to be a complete
            // replacement for it - otherwise a block that swaps the marker for the trait silently loses both.
            definition.addTags(
                    CommonBlockTags.IS_OBSIDIAN,
                    CommonBlockTags.IMMOBILE,
                    BlockTags.DRAGON_IMMUNE,
                    BlockTags.NEEDS_DIAMOND_TOOL
            );

            definition
                    .mapColor(MapColor.COLOR_BLACK)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(50.0F, 1200.0F);
        }
    }
}
