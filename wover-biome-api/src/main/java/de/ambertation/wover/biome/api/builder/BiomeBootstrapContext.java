package de.ambertation.wover.biome.api.builder;

import de.ambertation.wover.core.api.registry.LookupProvider;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import org.jetbrains.annotations.NotNull;

/**
 * Collects {@link BiomeBuilder}s while the Biome-related registries ({@link net.minecraft.world.level.biome.Biome},
 * {@link de.ambertation.wover.biome.api.data.BiomeData}, surface rules and Biome tags) are bootstrapped.
 * <p>
 * A {@link BiomeBuilder} registers itself with this context by calling {@link BiomeBuilder#register()}
 * (which in turn calls {@link #register(BiomeBuilder)}). Once every registry that needs the builder's data
 * has been bootstrapped, the context applies the builders to each of them in turn.
 * <p>
 * Instances are created and managed internally; mod code only ever receives one, either through
 * {@link de.ambertation.wover.biome.api.BiomeKey#bootstrap(BiomeBootstrapContext)} or a
 * {@link de.ambertation.wover.biome.api.builder.event.OnBootstrapBiomes} subscriber.
 */
public interface BiomeBootstrapContext extends LookupProvider {
    /**
     * Registers a {@link BiomeBuilder} with this context.
     * <p>
     * The builder will later be used to populate the Biome, {@link de.ambertation.wover.biome.api.data.BiomeData},
     * surface rule and Biome-tag registries.
     * <p>
     * There used to be a {@code register(BiomeBuilder, Lifecycle)} overload here, with this method
     * delegating to it with {@code Lifecycle.stable()}. Minecraft 26.3 removed
     * {@code BootstrapContext#register(ResourceKey, T, Lifecycle)}, so there is no longer any way to act
     * on a caller-supplied {@link com.mojang.serialization.Lifecycle} when the Biome is finally
     * registered - and vanilla offers no replacement. The overload is therefore gone; callers that used to
     * pass a lifecycle simply drop that argument.
     * <p>
     * Note that the lifecycle was already being discarded before 26.3: the only implementation
     * ({@code BiomeBootstrapContextImpl}) accepted it and never used it, so removing it loses no
     * behaviour that ever worked.
     *
     * @param builder The builder to register.
     */
    void register(@NotNull BiomeBuilder<?> builder);

    /**
     * Looks up the {@link HolderGetter} for the given registry.
     *
     * @param registryKey The key of the registry to look up.
     * @param <S>         The type of the registry.
     * @return The {@link HolderGetter} for the registry.
     */
    <S> HolderGetter<S> lookup(@NotNull ResourceKey<? extends Registry<? extends S>> registryKey);
}
