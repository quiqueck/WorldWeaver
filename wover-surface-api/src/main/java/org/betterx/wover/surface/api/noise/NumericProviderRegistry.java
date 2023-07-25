package org.betterx.wover.surface.api.noise;

import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.entrypoint.WoverSurface;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * Registry for custom Numeric Providers. Numeric Providers can generate number sequences
 * based on a set of parameters. This is a builting registry, that can provide a list of
 * default Numeric Providers. It is not loaded from datapacks.
 * <p>
 * You can however add your own Numeric Providers to this registry while your mod is initialized using
 * the {@link #register(ResourceKey, Codec)} method.
 */
public class NumericProviderRegistry {
    /**
     * The Key for the Registry. ({@code wover/numeric_provider})
     */
    public static final ResourceKey<Registry<Codec<? extends NumericProvider>>> NUMERIC_PROVIDER_REGISTRY = DatapackRegistryBuilder.createRegistryKey(
            WoverSurface.C.id("wover/numeric_provider")
    );

    /**
     * The actual Registry for the Numeric Providers.
     */
    public static final Registry<Codec<? extends NumericProvider>> NUMERIC_PROVIDER = new MappedRegistry<>(
            NUMERIC_PROVIDER_REGISTRY,
            Lifecycle.stable()
    );

    /**
     * Creates a ResourceKey for the Numeric Provider Registry.
     *
     * @param location The location of the Numeric Provider.
     * @return The ResourceKey for the Numeric Provider.
     */
    public static ResourceKey<Codec<? extends NumericProvider>> createKey(ResourceLocation location) {
        return ResourceKey.create(NUMERIC_PROVIDER_REGISTRY, location);
    }

    /**
     * Registers a new Numeric Provider.
     *
     * @param key   The ResourceKey for the Numeric Provider.
     * @param codec The Codec for the Numeric Provider.
     * @return The same ResourceKey that was passed in.
     */
    public static ResourceKey<Codec<? extends NumericProvider>> register(
            ResourceKey<Codec<? extends NumericProvider>> key,
            Codec<? extends NumericProvider> codec
    ) {
        Registry.register(NUMERIC_PROVIDER, key, codec);
        return key;
    }

    /**
     * Registers a new Numeric Provider.
     *
     * @param location The location of the Numeric Provider.
     * @param codec    The Codec for the Numeric Provider.
     * @return The newly created ResourceKey for the Numeric Provider.
     */
    public static ResourceKey<Codec<? extends NumericProvider>> register(
            ResourceLocation location,
            Codec<? extends NumericProvider> codec
    ) {
        return register(createKey(location), codec);
    }
}
