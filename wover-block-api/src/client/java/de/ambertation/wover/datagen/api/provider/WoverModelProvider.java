package de.ambertation.wover.datagen.api.provider;

import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.api.model.WoverBlockModelGenerators;
import de.ambertation.wover.block.impl.ModelProviderExclusions;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverDataProvider;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for client-side model datagen providers. Subclasses implement
 * {@link #bootstrapBlockStateModels(WoverBlockModelGenerators)} and {@link #bootstrapItemModels(ItemModelGenerators)}
 * to emit blockstate/model files, and can use {@link #addFromRegistry} to automatically process every block in a
 * {@link BlockRegistry}, applying any per-block {@link ModelOverides} that were configured.
 */
public abstract class WoverModelProvider implements WoverDataProvider<FabricModelProvider> {
    /**
     * The title of the provider. Mainly used for logging.
     */
    public final String title;

    /**
     * The ModCore instance of the Mod that is providing this instance.
     */
    protected final ModCore modCore;

    /**
     * Creates a new provider, using the mod's namespace as the title.
     *
     * @param modCore The mod this provider generates models for
     */
    public WoverModelProvider(ModCore modCore) {
        this(modCore, modCore.namespace);
    }

    /**
     * Creates a new provider with an explicit title.
     *
     * @param modCore The mod this provider generates models for
     * @param title   The title of the provider, mainly used for logging
     */
    public WoverModelProvider(ModCore modCore, String title) {
        this.modCore = modCore;
        this.title = title;
    }

    /**
     * Processes every block in {@code registry}, without any overrides.
     *
     * @param generator The generator to emit models through
     * @param registry  The registry whose blocks should be processed
     * @param validate  If {@code true}, blocks that don't provide models are excluded from model
     *                  validation instead of failing it
     */
    protected void addFromRegistry(
            WoverBlockModelGenerators generator,
            BlockRegistry registry,
            boolean validate
    ) {
        addFromRegistry(generator, registry, validate, ModelOverides.create());
    }

    /**
     * A set of per-block overrides that can replace, skip, or share a block's model generation when
     * iterating a {@link BlockRegistry} with {@link #addFromRegistry}.
     */
    public static class ModelOverides {
        /**
         * A callback that generates the models for a single block, used to override the default
         * per-block model generation.
         */
        public interface BlockModelProvider {
            /**
             * Generates the models for the given block.
             *
             * @param block The block to generate models for
             */
            void provideModels(Block block);
        }

        private final Map<Block, BlockModelProvider> OVERRIDES = new HashMap<>();
        private static final BlockModelProvider IGNORE = (block) -> {
        };

        /**
         * Creates a new, empty set of overrides.
         *
         * @return A new overrides instance
         */
        public static ModelOverides create() {
            return new ModelOverides();
        }

        /**
         * Registers a custom model provider for {@code block}, replacing whatever it would otherwise use.
         *
         * @param block    The block to override, ignored if {@code null} or {@link Blocks#AIR}
         * @param provider The provider that generates the block's models
         * @return This instance, for chaining
         * @throws IllegalStateException if {@code block} already has an override
         */
        public ModelOverides override(@Nullable Block block, @NotNull BlockModelProvider provider) {
            if (block == Blocks.AIR || block == null) return this;

            final var old = OVERRIDES.put(block, provider);
            if (old != null) {
                throw new IllegalStateException("Block " + block + " already has an override.");
            }
            return this;
        }

        /**
         * Registers {@code block} to reuse the override already registered for {@code copyFromBlock}.
         *
         * @param block          The block to override, ignored if {@code null} or {@link Blocks#AIR}
         * @param copyFromBlock  The block whose override should be reused
         * @return This instance, for chaining
         */
        public ModelOverides overrideLike(@Nullable Block block, @NotNull Block copyFromBlock) {
            if (block == Blocks.AIR || block == null) return this;
            return this.override(block, OVERRIDES.get(copyFromBlock));
        }

        /**
         * Excludes {@code block} from model generation entirely (no models are generated for it).
         *
         * @param block The block to ignore, ignored itself if {@code null} or {@link Blocks#AIR}
         * @return This instance, for chaining
         */
        public ModelOverides ignore(@Nullable Block block) {
            if (block == Blocks.AIR || block == null) return this;
            return this.override(block, IGNORE);
        }

        /**
         * Checks whether {@code block} has an override registered.
         *
         * @param block The block to check
         * @return {@code true} if the block has an override
         */
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


    /**
     * Processes every block in {@code registry}: runs the per-block {@code overrides} (which emit the block's
     * models, or deliberately provide none for blocks whose blockstate/model is hand-authored), then excludes
     * the block from vanilla's "missing blockstate" validation.
     * <p>
     * Historically the exclusion was skipped for blocks implementing the (now-removed) {@code BlockModelProvider}
     * hook, which generated their own models and were meant to be validated. With that hook gone, every block
     * handled here is excluded - it either generated a blockstate through an override, or intentionally has
     * none - so {@code validateMissing} no longer changes the outcome and is kept only for API compatibility.
     *
     * @param generator      The generator to emit models through
     * @param registry       The registry whose blocks should be processed
     * @param validateMissing Retained for API compatibility; no longer affects the outcome (see above)
     * @param overrides      Per-block overrides that replace or skip the default model generation
     */
    protected void addFromRegistry(
            WoverBlockModelGenerators generator,
            BlockRegistry registry,
            boolean validateMissing,
            ModelOverides overrides
    ) {
        registry
                .allBlocks()
                .forEach(block -> {
                    // provideBlockModel runs the override (emitting the block's models, if any) as a side effect.
                    overrides.provideBlockModel(block);
                    ModelProviderExclusions.excludeFromBlockModelValidation(block);
                });
    }

    /**
     * Generates every blockstate and block model this provider is responsible for.
     *
     * @param generator The generator to emit blockstate/model files through
     */
    protected abstract void bootstrapBlockStateModels(WoverBlockModelGenerators generator);

    /**
     * Generates every item model this provider is responsible for.
     *
     * @param itemModelGenerator The vanilla generator to emit item model files through
     */
    protected abstract void bootstrapItemModels(ItemModelGenerators itemModelGenerator);

    @Override
    public FabricModelProvider getProvider(
            FabricPackOutput output,
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
