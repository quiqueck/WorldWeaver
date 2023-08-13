package org.betterx.wover.datagen.api.provider.multi;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.PackBuilder;
import org.betterx.wover.datagen.api.WoverMultiProvider;
import org.betterx.wover.datagen.api.provider.WoverConfiguredFeatureProvider;
import org.betterx.wover.datagen.api.provider.WoverPlacedFeatureProvider;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * A {@link WoverMultiProvider} for {@link ConfiguredFeature}s and {@link PlacedFeature}s.
 */
public abstract class WoverFeatureProvider implements WoverMultiProvider {
    /**
     * The {@link ModCore} of the Mod.
     */
    protected final ModCore modCore;

    /**
     * Creates a new instance of {@link WoverFeatureProvider}.
     *
     * @param modCore The {@link ModCore} of the Mod.
     */
    public WoverFeatureProvider(ModCore modCore) {
        this.modCore = modCore;
    }

    /**
     * Called, when the Elements of the Registry need to be created and registered.
     *
     * @param context The context to add the elements to.
     */
    protected abstract void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context);

    /**
     * Called, when the Elements of the Registry need to be created and registered.
     *
     * @param context The context to add the elements to.
     */
    protected abstract void bootstrapPlaced(BootstapContext<PlacedFeature> context);

    /**
     * Registers all  providers
     *
     * @param pack The {@link PackBuilder} to register the providers to.
     */
    @Override
    public void registerAllProviders(PackBuilder pack) {
        pack.addRegistryProvider(modCore ->
                new WoverConfiguredFeatureProvider(modCore) {

                    @Override
                    protected void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
                        bootstrapConfigured(context);
                    }
                }
        );

        pack.addRegistryProvider(modCore ->
                new WoverPlacedFeatureProvider(modCore) {

                    @Override
                    protected void bootstrap(BootstapContext<PlacedFeature> context) {
                        bootstrapPlaced(context);
                    }
                }
        );
    }
}
