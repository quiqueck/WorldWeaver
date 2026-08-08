package de.ambertation.wover.datagen.api.provider;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverRegistryContentProvider;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.Feature;

import org.jetbrains.annotations.NotNull;

/**
 * A {@link WoverRegistryContentProvider} for {@link Feature}s.
 * <p>
 * Minecraft 26.3 collapsed the configured-feature concept into {@link Feature} itself:
 * {@code ConfiguredFeature<FC, F>} no longer exists, and {@code Feature} is now a plain
 * (non-generic) interface that carries its own configuration and its own
 * {@link com.mojang.serialization.MapCodec}. Consequently the datapack registry
 * {@code minecraft:configured_feature} became {@code minecraft:feature}
 * ({@code Registries.CONFIGURED_FEATURE} -> {@code Registries.FEATURE}) with
 * {@code Feature} as its element type.
 * <p>
 * Renamed from {@code WoverConfiguredFeatureProvider} in the 26.3 port: the old name referred to a
 * type that no longer exists. "ContentProvider" matches the {@link WoverRegistryContentProvider}
 * base class and keeps this distinct from the multi-provider
 * {@link de.ambertation.wover.datagen.api.provider.multi.WoverFeatureProvider}, which bundles this
 * provider together with the placed-feature one.
 */
public abstract class WoverFeatureContentProvider extends WoverRegistryContentProvider<Feature> {

    /**
     * Creates a new instance of {@link WoverRegistryContentProvider}.
     *
     * @param modCore The ModCore instance of the Mod that is providing this instance.
     */
    public WoverFeatureContentProvider(
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
    public WoverFeatureContentProvider(
            @NotNull ModCore modCore,
            @NotNull Identifier providerId
    ) {
        super(modCore, providerId.toString() + " (Features)", Registries.FEATURE);
    }

    /**
     * Called, when the Elements of the Registry need to be created and registered.
     *
     * @param context The context to add the elements to.
     */
    @Override
    abstract protected void bootstrap(BootstrapContext<Feature> context);
}
