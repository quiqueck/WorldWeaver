package de.ambertation.wover.datagen.api.provider.multi;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.AbstractMultiProvider;
import de.ambertation.wover.datagen.api.PackBuilder;
import de.ambertation.wover.datagen.api.WoverMultiProvider;
import de.ambertation.wover.datagen.api.provider.WoverFeatureContentProvider;
import de.ambertation.wover.datagen.api.provider.WoverPlacedFeatureProvider;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.NotNull;

/**
 * A {@link WoverMultiProvider} for {@link Feature}s and {@link PlacedFeature}s.
 * <p>
 * The "configured" half of this provider now writes the {@code minecraft:feature} registry:
 * Minecraft 26.3 removed {@code ConfiguredFeature<FC, F>} and folded the configuration into
 * {@link Feature} itself, so the element type of the former configured-feature registry is now
 * plain {@link Feature}. See {@link WoverFeatureContentProvider} for details.
 * <p>
 * {@code bootstrapConfigured} was renamed to {@code bootstrapFeatures} in the 26.3 port, for the same
 * reason: it no longer bootstraps "configured features", it bootstraps features.
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
    public WoverFeatureProvider(@NotNull ModCore modCore, @NotNull Identifier providerId) {
        super(modCore, providerId);
    }

    /**
     * Called, when the Elements of the Registry need to be created and registered.
     *
     * @param context The context to add the elements to.
     */
    protected abstract void bootstrapFeatures(BootstrapContext<Feature> context);

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
                new WoverFeatureContentProvider(modCore, providerId) {

                    @Override
                    protected void bootstrap(BootstrapContext<Feature> context) {
                        bootstrapFeatures(context);
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
