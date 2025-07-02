package org.betterx.wover.block.impl.client.trait;

import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;
import org.betterx.wover.block.api.trait.AbstractBlockTraitBuilder;
import org.betterx.wover.block.api.trait.BlockTraitKey;
import org.betterx.wover.block.impl.trait.BlockTraitImpl;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverRecipe;
import org.betterx.wover.entrypoint.LibWoverSets;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.function.BiPredicate;

public class BlockModelTraitBuilder extends AbstractBlockTraitBuilder<Block, BlockModelTrait> implements BlockModelTrait.Builder {
    public static final BlockModelTrait.Builder BUILDER = new BlockModelTraitBuilder();

    private BlockModelTraitBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "model"));
    }

    @Override
    public BlockModelTrait with(BlockModelTrait.ModelFactory factory) {
        if (!ModCore.isDatagen() || !ModCore.isClient()) return null;
        return new Trait(factory);
    }


    @Environment(EnvType.CLIENT)
    public static void bootstrapModels(
            ModCore modCore,
            WoverBlockModelGenerators generator,
            BiPredicate<ResourceKey<Block>, Block> filter
    ) {
        BlockRegistry
                .forMod(modCore)
                .allEntries().filter(e -> filter.test(e.getKey(), e.getValue())).forEach(e -> {
                    var runtimeTraits = BUILDER.getRuntimeTraits(e.getValue());
                    if (runtimeTraits == null) return;
                    runtimeTraits.forEach(trait -> {
                        try {
                            trait.modelFactory().provideBlockModels(e.getKey(), e.getValue(), generator);
                        } catch (Exception ex) {
                            LibWoverRecipe.C.LOG.error("Failed to build model for block: " + e.getKey(), ex);
                        }
                    });
                });
    }

    @Environment(EnvType.CLIENT)
    private class Trait extends BlockTraitImpl<Block, BlockModelTrait> implements BlockModelTrait {
        private final ModelFactory modelFactory;

        public Trait(ModelFactory modelFactory) {
            super();
            this.modelFactory = modelFactory;
        }


        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public ModelFactory modelFactory() {
            return this.modelFactory;
        }

        @Override
        public BlockModelTrait forRuntime() {
            return this;
        }
    }
}
