package org.betterx.wover.biome.api.builder;

import org.betterx.wover.core.api.registry.LookupProvider;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import org.jetbrains.annotations.NotNull;

/**
 * Collects {@link BiomeBuilder}s while the Biome-related registries ({@link net.minecraft.world.level.biome.Biome},
 * {@link org.betterx.wover.biome.api.data.BiomeData}, surface rules and Biome tags) are bootstrapped.
 * <p>
 * A {@link BiomeBuilder} registers itself with this context by calling {@link BiomeBuilder#register()}
 * (which in turn calls {@link #register(BiomeBuilder)}). Once every registry that needs the builder's data
 * has been bootstrapped, the context applies the builders to each of them in turn.
 * <p>
 * Instances are created and managed internally; mod code only ever receives one, either through
 * {@link org.betterx.wover.biome.api.BiomeKey#bootstrap(BiomeBootstrapContext)} or a
 * {@link org.betterx.wover.biome.api.builder.event.OnBootstrapBiomes} subscriber.
 */
public interface BiomeBootstrapContext extends LookupProvider {
    /**
     * Registers a {@link BiomeBuilder} with this context, using the given {@link Lifecycle}.
     * <p>
     * The builder will later be used to populate the Biome, {@link org.betterx.wover.biome.api.data.BiomeData},
     * surface rule and Biome-tag registries.
     *
     * @param builder   The builder to register.
     * @param lifecycle The {@link Lifecycle} to register the Biome with.
     */
    void register(@NotNull BiomeBuilder<?> builder, Lifecycle lifecycle);

    /**
     * Registers a {@link BiomeBuilder} with this context, using {@link Lifecycle#stable()}.
     *
     * @param builder The builder to register.
     * @see #register(BiomeBuilder, Lifecycle)
     */
    default void register(@NotNull BiomeBuilder<?> builder) {
        this.register(builder, Lifecycle.stable());
    }

    /**
     * Looks up the {@link HolderGetter} for the given registry.
     *
     * @param registryKey The key of the registry to look up.
     * @param <S>         The type of the registry.
     * @return The {@link HolderGetter} for the registry.
     */
    <S> HolderGetter<S> lookup(@NotNull ResourceKey<? extends Registry<? extends S>> registryKey);
}
