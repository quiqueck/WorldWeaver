package org.betterx.wover.block.api.trait.type;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitKey;
import org.betterx.wover.block.api.trait.behaviour.FlammableBlockTrait;
import org.betterx.wover.block.api.trait.behaviour.StripableBlockTrait;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverBlock;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public class LogBlockTrait extends BlockTrait<Block, BlockTrait.VoidRuntime<Block>> {
    public static final LogBlockTrait.Builder BUILDER = new LogBlockTrait.Builder();

    public static class Builder extends BlockTrait.TraitBuilder {
        private Builder() {
            super(BlockTraitKey.of(LibWoverBlock.C, "type_log"));
        }

        public @Nullable BlockTrait<?, ?> withDefault() {
            if (!ModCore.isDatagen()) return null;
            return new LogBlockTrait();
        }

        public @Nullable List<BlockTrait<?, ?>> with(Block strippedBlockState) {
            return with(() -> strippedBlockState.defaultBlockState());
        }

        public @Nullable List<BlockTrait<?, ?>> with(BlockState strippedBlockState) {
            return with(() -> strippedBlockState);
        }

        public @Nullable List<BlockTrait<?, ?>> with(Supplier<BlockState> strippedBlockState) {
            if (!ModCore.isDatagen()) return combine(StripableBlockTrait.BUILDER.with(strippedBlockState));
            return combine(new LogBlockTrait(), StripableBlockTrait.BUILDER.with(strippedBlockState));
        }
    }


    protected LogBlockTrait() {
        super(BUILDER.ID);
    }

    @Override
    public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
        definition.addTags(BlockTags.LOGS);
        definition.addItemTags(ItemTags.LOGS);

        if (definition.hasTrait(FlammableBlockTrait.BUILDER.ID)) {
            definition.addTags(BlockTags.LOGS_THAT_BURN);
        }
    }
}
