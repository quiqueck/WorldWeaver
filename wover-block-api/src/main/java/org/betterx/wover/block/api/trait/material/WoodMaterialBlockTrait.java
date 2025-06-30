package org.betterx.wover.block.api.trait.material;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitKey;
import org.betterx.wover.block.api.trait.MineableWithTrait;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class WoodMaterialBlockTrait extends BlockTrait<Block, BlockTrait.VoidRuntime<Block>> {
    public static final WoodMaterialBlockTrait.Builder BUILDER = new WoodMaterialBlockTrait.Builder();
    private static final WoodMaterialBlockTrait DEFAULT = new WoodMaterialBlockTrait();

    public static class Builder extends BlockTrait.TraitBuilder {
        private Builder() {
            super(BlockTraitKey.of(LibWoverBlock.C, "wood_block"));
        }

        public @Nullable List<BlockTrait<?, ?>> withDefault() {
            if (!ModCore.isDatagen()) return combine(DEFAULT);
            return combine(DEFAULT, MineableWithTrait.BUILDER.needsAxe());
        }
    }

    protected WoodMaterialBlockTrait() {
        super(BUILDER.ID);
    }

    @Override
    public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
        definition.getProperties().instrument(NoteBlockInstrument.BASS);
    }
}
