package de.ambertation.wover.datagen.api.provider.multi;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.AbstractMultiProvider;
import de.ambertation.wover.datagen.api.PackBuilder;
import de.ambertation.wover.datagen.api.WoverMultiProvider;
import de.ambertation.wover.datagen.api.provider.WoverConfiguredFeatureProvider;
import de.ambertation.wover.datagen.api.provider.WoverPlacedFeatureProvider;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.NotNull;

/**
 * A {@link WoverMultiProvider} for {@link ConfiguredFeature}s and {@link PlacedFeature}s.
 */
public abstract class WoverFeatureProvider extends AbstractMultiProvider {
    /**
     * Creates a new instance of {@link WoverFeatureProvider}.
     *
     * @param modCore The {@link ModCore} of the Mod.
     */
    public WoverFeatureProvider(@NotNull ModCore modCore) {
        super(modCore);
    }

    /**
     * Creates a new instance of {@link WoverFeatureProvider}.
     *
     * @param modCore    The {@link ModCore} of the Mod.
     * @param providerId The id of the provider. Every Provider (for the same Registry)
     *                   needs a unique id.
     */
    public WoverFeatureProvider(@NotNull ModCore modCore, @NotNull ResourceLocation providerId) {
        super(modCore, providerId);
    }

    /**
     * Called, when the Elements of the Registry need to be created and registered.
     *
     * @param context The context to add the elements to.
     */
    protected abstract void bootstrapConfigured(BootstrapContext<ConfiguredFeature<?, ?>> context);

    /**
     * Called, when the Elements of the Registry need to be created and registered.
     *
     * @param context The context to add the elements to.
     */
    protected abstract void bootstrapPlaced(BootstrapContext<PlacedFeature> context);

    /**
     * Registers all  providers
     *
     * @param pack The {@link PackBuilder} to register the providers to.
     */
    @Override
    public void registerAllProviders(PackBuilder pack) {
        pack.addRegistryProvider(modCore ->
                new WoverConfiguredFeatureProvider(modCore, providerId) {

                    @Override
                    protected void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
                        bootstrapConfigured(context);
                    }
                }
        );

        pack.addRegistryProvider(modCore ->
                new WoverPlacedFeatureProvider(modCore, providerId) {

                    @Override
                    protected void bootstrap(BootstrapContext<PlacedFeature> context) {
                        bootstrapPlaced(context);
                    }
                }
        );
    }
}
