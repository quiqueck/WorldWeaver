package org.betterx.wover.feature.api.configured;

import org.betterx.wover.feature.api.configured.configurators.FeatureConfigurator;
import org.betterx.wover.feature.impl.configured.FeatureConfiguratorImpl;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A key for a {@link ConfiguredFeature} that can be used to reference the feature as well as
 * to bootstrap it for a registry.
 *
 * @param <B> The type of the {@link FeatureConfigurator} that can build the key
 */
public abstract class ConfiguredFeatureKey<B extends FeatureConfigurator<?, ?>> {
    /**
     * The key for the {@link ConfiguredFeature} you can use to reference it.
     */
    @NotNull
    public final ResourceKey<ConfiguredFeature<?, ?>> key;

    /**
     * Creates a new {@link ConfiguredFeatureKey} from the given id.
     *
     * @param id The id of the {@link ConfiguredFeature}
     */
    protected ConfiguredFeatureKey(@NotNull ResourceLocation id) {
        this(FeatureConfiguratorImpl.createKey(id));
    }

    /**
     * Creates a new {@link ConfiguredFeatureKey} from the given key.
     *
     * @param key The key of the {@link ConfiguredFeature}
     */
    protected ConfiguredFeatureKey(@NotNull ResourceKey<ConfiguredFeature<?, ?>> key) {
        this.key = key;
    }

    /**
     * Gets the {@link Holder} for the {@link ConfiguredFeature} from the given getter.
     *
     * @param getter The getter to get the holder from or {@code null}
     * @return The holder for the {@link ConfiguredFeature} or {@code null} if it is not present
     */
    @Nullable
    public Holder<ConfiguredFeature<?, ?>> getHolder(@Nullable HolderGetter<ConfiguredFeature<?, ?>> getter) {
        return FeatureConfiguratorImpl.getHolder(getter, key);
    }

    /**
     * Gets the {@link Holder} for the {@link ConfiguredFeature} from the given getter.
     *
     * <p>
     * This method internally looks up {@link Registries#CONFIGURED_FEATURE}. If you need to retrieve
     * a lot of holders, it is recommended to manually lookup the
     * Registry first and use {@link #getHolder(HolderGetter)} instead.
     *
     * @param context The {@link BootstapContext} to get the holder from
     * @return The holder for the {@link ConfiguredFeature} or {@code null} if it is not present
     */
    @Nullable
    public Holder<ConfiguredFeature<?, ?>> getHolder(@NotNull BootstapContext<?> context) {
        return getHolder(context.lookup(Registries.CONFIGURED_FEATURE));
    }

    /**
     * Creates a new {@link FeatureConfigurator} for this Feature.
     * <p>
     * The builder is used to alter the settings of this configured feature.
     * When done, you should call {@link FeatureConfigurator#register()}
     * to register the configured feature.
     *
     * @param ctx The {@link BootstapContext} this bootstrap is performed in
     * @return The builder for this Feature
     * @see FeatureConfigurator#register()
     */
    public abstract B bootstrap(@NotNull BootstapContext<ConfiguredFeature<?, ?>> ctx);
}