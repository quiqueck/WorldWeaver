package org.betterx.wover.datagen.api.provider.multi;

import org.betterx.wover.biome.api.builder.BiomeBootstrapContext;
import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.api.data.BiomeDataRegistry;
import org.betterx.wover.biome.impl.BiomeBootstrapContextImpl;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.PackBuilder;
import org.betterx.wover.datagen.api.WoverMultiProvider;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;
import org.betterx.wover.datagen.api.WoverTagProvider;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.biome.Biome;

public abstract class WoverBiomeProvider implements WoverMultiProvider {
    /**
     * The {@link ModCore} of the Mod.
     */
    protected final ModCore modCore;
    private BiomeBootstrapContextImpl context;

    /**
     * Creates a new instance of {@link WoverBiomeProvider}.
     *
     * @param modCore The {@link ModCore} of the Mod.
     */
    public WoverBiomeProvider(ModCore modCore) {
        this.modCore = modCore;
    }

    protected abstract void bootstrap(BiomeBootstrapContext context);

    private <T> BiomeBootstrapContextImpl initContext(BootstapContext<T> ctx) {
        if (context == null) {
            context = new BiomeBootstrapContextImpl();
            context.setLookupContext(ctx);
            bootstrap(context);
        } else {
            context.setLookupContext(ctx);
        }

        return context;
    }

    private void bootstrapBiomes(BootstapContext<Biome> ctx) {
        final BiomeBootstrapContextImpl context = initContext(ctx);
        context.bootstrapBiome(ctx);
    }

    private void bootstrapData(BootstapContext<BiomeData> ctx) {
        final BiomeBootstrapContextImpl context = initContext(ctx);
        context.bootstrapBiomeData(ctx);
    }

    private void prepareBiomeTags(TagBootstrapContext<Biome> ctx) {
        final BiomeBootstrapContextImpl context = initContext(null);
        context.prepareTags(ctx);
    }

    /**
     * Registers all  providers
     *
     * @param pack The {@link PackBuilder} to register the providers to.
     */
    @Override
    public void registerAllProviders(PackBuilder pack) {
        pack.addRegistryProvider(modCore ->
                new WoverRegistryContentProvider<>(modCore, modCore.modId + " - Biomes", Registries.BIOME) {

                    @Override
                    protected void bootstrap(BootstapContext<Biome> context) {
                        bootstrapBiomes(context);
                    }
                }
        );

        pack.addRegistryProvider(modCore ->
                new WoverRegistryContentProvider<>(
                        modCore,
                        modCore.modId + " - Biome Data",
                        BiomeDataRegistry.BIOME_DATA_REGISTRY
                ) {

                    @Override
                    protected void bootstrap(BootstapContext<BiomeData> context) {
                        bootstrapData(context);
                    }
                }
        );

        pack.addProvider(modCore ->
                new WoverTagProvider.ForBiomes(modCore) {

                    @Override
                    protected void prepareTags(TagBootstrapContext<Biome> provider) {
                        prepareBiomeTags(provider);
                    }
                }
        );
    }
}
