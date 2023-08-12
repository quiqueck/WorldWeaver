package org.betterx.wover.datagen.api.provider;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

/**
 * A {@link WoverRegistryContentProvider} for {@link ConfiguredFeature}s.
 */
public abstract class WoverConfiguredFeatureProvider extends WoverRegistryContentProvider<ConfiguredFeature<?, ?>> {

    /**
     * Creates a new instance of {@link WoverRegistryContentProvider}.
     *
     * @param modCore The ModCore instance of the Mod that is providing this instance.
     */
    public WoverConfiguredFeatureProvider(
            ModCore modCore
    ) {
        super(modCore, modCore.modId + " - Configured Features", Registries.CONFIGURED_FEATURE);
    }

    /**
     * Called, when the Elements of the Registry need to be created and registered.
     *
     * @param context The context to add the elements to.
     */
    @Override
    abstract protected void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context);
}
