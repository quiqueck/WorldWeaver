package org.betterx.wover.datagen.api.provider;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;
import org.betterx.wover.surface.api.AssignedSurfaceRule;
import org.betterx.wover.surface.api.SurfaceRuleRegistry;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;

/**
 * A {@link WoverRegistryContentProvider} for Surface Rules ({@link AssignedSurfaceRule}).
 */
public abstract class WoverSurfaceRuleProvider extends WoverRegistryContentProvider<AssignedSurfaceRule> {
    /**
     * Creates a new instance of {@link WoverRegistryContentProvider}.
     *
     * @param modCore The ModCore instance of the Mod that is providing this instance.
     */
    public WoverSurfaceRuleProvider(
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
    public WoverSurfaceRuleProvider(
            @NotNull ModCore modCore,
            @NotNull ResourceLocation providerId
    ) {
        super(modCore, providerId.toString() + " (Surface Rules)", SurfaceRuleRegistry.SURFACE_RULES_REGISTRY);
    }

    /**
     * Called, when the Elements of the Registry need to be created and registered.
     *
     * @param context The context to add the elements to.
     */
    @Override
    protected abstract void bootstrap(BootstapContext<AssignedSurfaceRule> context);
}

