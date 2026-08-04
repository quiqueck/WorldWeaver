package de.ambertation.wover.block.impl.client.trait;

import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.api.client.model.ClientBlockModelRegistry;
import de.ambertation.wover.block.api.client.trait.BlockModelTrait;
import de.ambertation.wover.block.api.model.BlockModelBinding;
import de.ambertation.wover.block.api.model.WoverBlockModelGenerators;
import de.ambertation.wover.block.api.trait.AbstractBlockTraitBuilder;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockTraitKey;
import de.ambertation.wover.block.api.trait.RuntimeBlockTrait;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.client.api.ClientTraitBootstrap;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverRecipe;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.function.BiPredicate;

public class BlockModelTraitBuilder extends AbstractBlockTraitBuilder<Block, BlockModelTrait> implements BlockModelTrait.Builder {
    public static final BlockModelTrait.Builder BUILDER = new BlockModelTraitBuilder();

    private BlockModelTraitBuilder() {
        // Shares the identity with BlockModelBinding, so the datagen walk collects both the data-driven
        // bindings and any bespoke client lambdas attached to a block, in attachment order.
        super(BlockModelBinding.MODEL_TRAIT_KEY);
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
        // Collect every mod's client-trait registrations before generating (present in the datagen launch too).
        ClientTraitBootstrap.ensureRegistered();

        BlockRegistry
                .forMod(modCore)
                .allEntries().filter(e -> filter.test(e.getKey(), e.getValue())).forEach(e -> {
                    BlockTrait.runtimeTraits(e.getValue())
                              .filter(trait -> trait.is(BlockModelBinding.MODEL_TRAIT_KEY))
                              .forEach(trait -> apply(e.getKey(), e.getValue(), generator, trait));
                });
    }

    @Environment(EnvType.CLIENT)
    private static void apply(
            ResourceKey<Block> key,
            Block block,
            WoverBlockModelGenerators generator,
            RuntimeBlockTrait<?, ?> trait
    ) {
        try {
            if (trait instanceof BlockModelBinding binding) {
                ClientBlockModelRegistry.apply(binding, key, block, generator);
            } else if (trait instanceof BlockModelTrait modelTrait) {
                modelTrait.modelFactory().provideBlockModels(key, block, generator);
            }
        } catch (Exception ex) {
            LibWoverRecipe.C.LOG.error("Failed to build model for block: " + key, ex);
        }
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
