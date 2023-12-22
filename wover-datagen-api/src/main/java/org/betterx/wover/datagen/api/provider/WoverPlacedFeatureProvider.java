package org.betterx.wover.datagen.api.provider;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.NotNull;


/**
 * A {@link WoverRegistryContentProvider} for {@link PlacedFeature}s.
 */
public abstract class WoverPlacedFeatureProvider extends WoverRegistryContentProvider<PlacedFeature> {

    /**
     * Creates a new instance of {@link WoverRegistryContentProvider}.
     *
     * @param modCore The ModCore instance of the Mod that is providing this instance.
     */
    public WoverPlacedFeatureProvider(
            @NotNull ModCore modCore
    ) {
        this(modCore, modCore.id("default"));
    }

    /**
     * Creates a new instance of {@link WoverRegistryContentProvider}.
     *
     * @param modCore    The ModCore instance of the Mod that is providing this instance.
     * @param providerId The id of the provider. Every Provider (for the same Registry)
     *                   needs a unique id.
     */
    public WoverPlacedFeatureProvider(
            @NotNull ModCore modCore,
            @NotNull ResourceLocation providerId
    ) {
        super(modCore, providerId.toString() + " (Placed Features)", Registries.PLACED_FEATURE);
    }

    /**
     * Called, when the Elements of the Registry need to be created and registered.
     *
     * @param context The context to add the elements to.
     */
    @Override
    abstract protected void bootstrap(BootstapContext<PlacedFeature> context);
}
