package de.ambertation.wover.datagen.api.provider.multi;

import de.ambertation.wover.biome.api.builder.BiomeBootstrapContext;
import de.ambertation.wover.biome.api.data.BiomeData;
import de.ambertation.wover.biome.impl.BiomeBootstrapContextImpl;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.AbstractMultiProvider;
import de.ambertation.wover.datagen.api.PackBuilder;
import de.ambertation.wover.datagen.api.WoverMultiProvider;
import de.ambertation.wover.datagen.api.WoverTagProvider;
import de.ambertation.wover.datagen.api.provider.WoverBiomeDataProvider;
import de.ambertation.wover.datagen.api.provider.WoverBiomeOnlyProvider;
import de.ambertation.wover.datagen.api.provider.WoverSurfaceRuleProvider;
import de.ambertation.wover.surface.api.AssignedSurfaceRule;
import de.ambertation.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link WoverMultiProvider} for {@link Biome}s and {@link BiomeData}.
 */
public abstract class WoverBiomeProvider extends AbstractMultiProvider {
    private BiomeBootstrapContextImpl context;

    /**
     * Creates a new instance of {@link WoverBiomeProvider}.
     *
     * @param modCore The {@link ModCore} of the Mod.
     */
    public WoverBiomeProvider(@NotNull ModCore modCore) {
        super(modCore);
    }

    /**
     * Creates a new instance of {@link WoverBiomeProvider}.
     *
     * @param modCore    The {@link ModCore} of the Mod.
     * @param providerId The id of the provider. Every Provider (for the same Registry)
     */
    public WoverBiomeProvider(@NotNull ModCore modCore, ResourceLocation providerId) {
        super(modCore, providerId);
    }

    /**
     * Called, when the Elements of the Registry needs to be created and registered.
     *
     * @param context The context to add the elements to.
     */
    protected abstract void bootstrap(BiomeBootstrapContext context);

    private <T> BiomeBootstrapContextImpl initContext(BootstrapContext<T> ctx) {
        synchronized (this) {
            if (context == null) {
                context = new BiomeBootstrapContextImpl();
                context.setLookupContext(ctx);
                bootstrap(context);
            } else {
                context.setLookupContext(ctx);
            }

            return context;
        }
    }

    private void bootstrapBiomes(BootstrapContext<Biome> ctx) {
        final BiomeBootstrapContextImpl context = initContext(ctx);
        context.bootstrapBiome(ctx);
    }

    private void bootstrapData(BootstrapContext<BiomeData> ctx) {
        final BiomeBootstrapContextImpl context = initContext(ctx);
        context.bootstrapBiomeData(ctx);
    }

    private void bootstrapSurface(BootstrapContext<AssignedSurfaceRule> ctx) {
        final BiomeBootstrapContextImpl context = initContext(ctx);
        context.bootstrapSurfaceRules(ctx);
    }

    private void prepareBiomeTags(TagBootstrapContext<Biome> ctx) {
        final BiomeBootstrapContextImpl context = initContext(null);
        context.prepareTags(ctx);
    }

    /**
     * Registers all providers
     *
     * @param pack The {@link PackBuilder} to register the providers to.
     */
    @Override
    public void registerAllProviders(PackBuilder pack) {
        pack.addRegistryProvider(modCore ->
                new WoverBiomeOnlyProvider(modCore, providerId) {
                    @Override
                    protected void bootstrap(BootstrapContext<Biome> context) {
                        bootstrapBiomes(context);
                    }
                }
        );

        pack.addRegistryProvider(modCore ->
                new WoverBiomeDataProvider(modCore, providerId) {
                    @Override
                    protected void bootstrap(BootstrapContext<BiomeData> context) {
                        bootstrapData(context);
                    }
                }
        );

        pack.addRegistryProvider(modCore ->
                new WoverSurfaceRuleProvider(modCore, providerId) {
                    @Override
                    protected void bootstrap(BootstrapContext<AssignedSurfaceRule> context) {
                        bootstrapSurface(context);
                    }
                }
        );

        pack.addProvider(modCore ->
                new WoverTagProvider.ForBiomes(modCore, List.of(modCore.namespace, modCore.modId)) {
                    @Override
                    public void prepareTags(TagBootstrapContext<Biome> provider) {
                        prepareBiomeTags(provider);
                    }
                }
        );
    }
}
