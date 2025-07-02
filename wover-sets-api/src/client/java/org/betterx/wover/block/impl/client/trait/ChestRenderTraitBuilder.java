package org.betterx.wover.block.impl.client.trait;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.client.ChestRenderTrait;
import org.betterx.wover.block.api.trait.AbstractBlockTraitBuilder;
import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitKey;
import org.betterx.wover.block.impl.trait.BlockTraitImpl;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverSets;

import static net.minecraft.client.renderer.Sheets.CHEST_MAPPER;
import net.minecraft.world.level.block.Block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.Nullable;

public class ChestRenderTraitBuilder extends AbstractBlockTraitBuilder<Block, ChestRenderTrait> implements ChestRenderTrait.Builder {
    public static final ChestRenderTraitBuilder BUILDER = new ChestRenderTraitBuilder();

    protected ChestRenderTraitBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "chest_renderer"));
    }

    @Override
    public @Nullable BlockTrait<?, ?> withDefault() {
        if (ModCore.isClient()) return new Trait();
        return null;
    }

    @Environment(EnvType.CLIENT)
    class Trait extends BlockTraitImpl<Block, ChestRenderTrait> implements ChestRenderTrait {
        private ChestMaterialSet material;

        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            material = new ChestMaterialSet(
                    CHEST_MAPPER.apply(definition.blockKey.location()),
                    CHEST_MAPPER.apply(definition.blockKey.location().withSuffix("_left")),
                    CHEST_MAPPER.apply(definition.blockKey.location().withSuffix("_right"))
            );
        }

        @Override
        public ChestRenderTrait forRuntime() {
            return this;
        }

        @Override
        public ChestMaterialSet getMaterial() {
            return material;
        }
    }
}
