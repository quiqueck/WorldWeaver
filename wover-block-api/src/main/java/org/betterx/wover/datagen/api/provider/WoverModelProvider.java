package org.betterx.wover.datagen.api.provider;

import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.model.BlockModelProvider;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;
import org.betterx.wover.block.impl.ModelProviderExclusions;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverDataProvider;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class WoverModelProvider implements WoverDataProvider<FabricModelProvider> {
    /**
     * The title of the provider. Mainly used for logging.
     */
    public final String title;

    /**
     * The ModCore instance of the Mod that is providing this instance.
     */
    protected final ModCore modCore;

    public WoverModelProvider(ModCore modCore) {
        this(modCore, modCore.namespace);
    }

    public WoverModelProvider(ModCore modCore, String title) {
        this.modCore = modCore;
        this.title = title;
    }

    protected void addFromRegistry(
            WoverBlockModelGenerators generator,
            BlockRegistry registry,
            boolean validate
    ) {
        addFromRegistry(generator, registry, validate, ModelOverides.create());
    }

    public static class ModelOverides {
        public interface BlockModelProvider {
            void provideModels(Block block);
        }

        private final Map<Block, BlockModelProvider> OVERRIDES = new HashMap<>();
        private static final BlockModelProvider IGNORE = (block) -> {
        };

        public static ModelOverides create() {
            return new ModelOverides();
        }

        public ModelOverides override(@Nullable Block block, @NotNull BlockModelProvider provider) {
            if (block == Blocks.AIR || block == null) return this;

            final var old = OVERRIDES.put(block, provider);
            if (old != null) {
                throw new IllegalStateException("Block " + block + " already has an override.");
            }
            return this;
        }

        public ModelOverides overrideLike(@Nullable Block block, @NotNull Block copyFromBlock) {
            if (block == Blocks.AIR || block == null) return this;
            return this.override(block, OVERRIDES.get(copyFromBlock));
        }

        public ModelOverides ignore(@Nullable Block block) {
            if (block == Blocks.AIR || block == null) return this;
            return this.override(block, IGNORE);
        }

        public boolean contain(Block block) {
            return OVERRIDES.containsKey(block);
        }

        boolean provideBlockModel(Block block) {
            final var override = OVERRIDES.get(block);
            if (override != null) {
                override.provideModels(block);
                return true;
            }
            return false;
        }

        private ModelOverides() {
        }
    }


    protected void addFromRegistry(
            WoverBlockModelGenerators generator,
            BlockRegistry registry,
            boolean validateMissing,
            ModelOverides overrides
    ) {
        registry
                .allBlocks()
                .forEach(block -> {
                    // If the block is not in the overrides, and it is a BlockModelProvider, provide the models.
                    if (!overrides.provideBlockModel(block) && block instanceof BlockModelProvider bmp) {
                        bmp.provideBlockModels(generator);
                    } else if (validateMissing) {
                        ModelProviderExclusions.excludeFromBlockModelValidation(block);
                    }

                    if (!validateMissing) {
                        ModelProviderExclusions.excludeFromBlockModelValidation(block);
                    }

                });
    }

    protected abstract void bootstrapBlockStateModels(WoverBlockModelGenerators generator);
    protected abstract void bootstrapItemModels(ItemModelGenerators itemModelGenerator);

    @Override
    public FabricModelProvider getProvider(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        return new FabricModelProvider(output) {
            @Override
            public String getName() {
                return super.getName() + " - " + title;
            }

            @Override
            public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
                bootstrapBlockStateModels(new WoverBlockModelGenerators(blockStateModelGenerator));
            }

            @Override
            public void generateItemModels(ItemModelGenerators itemModelGenerator) {
                bootstrapItemModels(itemModelGenerator);
            }
        };
    }
}
