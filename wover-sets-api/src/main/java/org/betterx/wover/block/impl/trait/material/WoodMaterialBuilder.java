package org.betterx.wover.block.impl.trait.material;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.trait.*;
import org.betterx.wover.block.impl.trait.BlockTraitImpl;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverSets;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class WoodMaterialBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.BuilderWithDefaults {
    public static final GenericBlockTrait.BuilderWithDefaults BUILDER = new WoodMaterialBuilder();
    private final GenericBlockTrait DEFAULT = new Trait();

    private WoodMaterialBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "wood"));
    }

    public @Nullable List<BlockTrait<?, ?>> withDefault() {
        if (!ModCore.isDatagen()) return combine(DEFAULT, BlockTraits.FLAMMABLE.withDefault());
        return combine(DEFAULT, BlockTraits.MINEABLE_WITH.needsAxe(), BlockTraits.FLAMMABLE.withDefault());
    }

    public @Nullable List<BlockTrait<?, ?>> withFireResistance() {
        if (!ModCore.isDatagen()) return combine(DEFAULT);
        return combine(DEFAULT, BlockTraits.MINEABLE_WITH.needsAxe());
    }

    class Trait extends BlockTraitImpl.Generic {
        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            definition
                    .mapColor(MapColor.WOOD)
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD);
        }
    }
}
