package org.betterx.wover.block.api.trait.material;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitKey;
import org.betterx.wover.block.api.trait.MineableWithTrait;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class MetalMaterialBlockTrait extends BlockTrait<Block, BlockTrait.VoidRuntime<Block>> {
    public static final MetalMaterialBlockTrait.Builder BUILDER = new MetalMaterialBlockTrait.Builder();
    private static final MetalMaterialBlockTrait DEFAULT = new MetalMaterialBlockTrait();

    public static class Builder extends BlockTrait.TraitBuilder {
        private Builder() {
            super(BlockTraitKey.of(LibWoverBlock.C, "metal_block"));
        }

        public @Nullable List<BlockTrait<?, ?>> withDefault() {
            if (!ModCore.isDatagen()) return combine(DEFAULT);
            return combine(DEFAULT, MineableWithTrait.BUILDER.needsAxe());
        }
    }

    protected MetalMaterialBlockTrait() {
        super(BUILDER.ID);
    }

    @Override
    public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
        definition.getProperties().instrument(NoteBlockInstrument.IRON_XYLOPHONE).sound(SoundType.IRON);
    }
}
