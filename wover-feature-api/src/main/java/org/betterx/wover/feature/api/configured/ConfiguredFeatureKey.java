package org.betterx.wover.feature.api.configured;

import org.betterx.wover.feature.api.FeatureUtils;
import org.betterx.wover.feature.api.configured.configurators.FeatureConfigurator;
import org.betterx.wover.feature.impl.configured.FeatureConfiguratorImpl;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
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
     * @param context The {@link BootstrapContext} to get the holder from
     * @return The holder for the {@link ConfiguredFeature} or {@code null} if it is not present
     */
    @Nullable
    public Holder<ConfiguredFeature<?, ?>> getHolder(@NotNull BootstrapContext<?> context) {
        return getHolder(context.lookup(Registries.CONFIGURED_FEATURE));
    }

    /**
     * Gets the {@link Holder} for the {@link ConfiguredFeature} from the given getter.
     *
     * <p>
     * This method internally looks up {@link Registries#CONFIGURED_FEATURE}. If you need to retrieve
     * a lot of holders, it is recommended to manually lookup the
     * Registry first and use {@link #getHolder(HolderGetter)} instead.
     *
     * @param access The {@link RegistryAccess} to get the holder from
     * @return The holder for the {@link ConfiguredFeature} or {@code null} if it is not present
     */
    @Nullable
    public Holder<ConfiguredFeature<?, ?>> getHolder(@NotNull RegistryAccess access) {
        return getHolder(access.lookupOrThrow(Registries.CONFIGURED_FEATURE));
    }

    /**
     * Places the {@link ConfiguredFeature} referenced by this key in the world.
     * <p>
     * The {@link RegistryAccess} used to resolve the {@link Holder} is taken from the given {@code level}.
     *
     * @param level  the level to place the feature in
     * @param pos    the position to place the feature at
     * @param random the random source to use
     * @return {@code true} if the feature was placed, {@code false} otherwise (for example if this key is
     * not registered)
     */
    public boolean placeInWorld(
            @NotNull WorldGenLevel level,
            @NotNull BlockPos pos,
            @NotNull RandomSource random
    ) {

        return placeInWorld(level.registryAccess(), level, pos, random, null);
    }

    /**
     * Places the {@link ConfiguredFeature} referenced by this key in the world.
     * <p>
     * The {@link RegistryAccess} used to resolve the {@link Holder} is taken from the given {@code level}.
     *
     * @param level     the level to place the feature in
     * @param pos       the position to place the feature at
     * @param random    the random source to use
     * @param generator the {@link ChunkGenerator} to use for the placement, or {@code null} to resolve
     *                  it automatically from the level
     * @return {@code true} if the feature was placed, {@code false} otherwise (for example if this key is
     * not registered)
     */
    public boolean placeInWorld(
            @NotNull WorldGenLevel level,
            @NotNull BlockPos pos,
            @NotNull RandomSource random,
            @Nullable ChunkGenerator generator
    ) {
        return placeInWorld(level.registryAccess(), level, pos, random, generator);
    }

    /**
     * Places the {@link ConfiguredFeature} referenced by this key in the world.
     *
     * @param access the {@link RegistryAccess} to resolve the {@link Holder} from, or {@code null} to
     *               fail immediately
     * @param level  the level to place the feature in
     * @param pos    the position to place the feature at
     * @param random the random source to use
     * @return {@code true} if the feature was placed, {@code false} otherwise (for example if {@code access}
     * is {@code null} or this key is not registered)
     */
    public boolean placeInWorld(
            @Nullable RegistryAccess access,
            @NotNull WorldGenLevel level,
            @NotNull BlockPos pos,
            @NotNull RandomSource random
    ) {
        if (access == null) return false;
        final Holder<ConfiguredFeature<?, ?>> holder = getHolder(access);
        if (holder != null) {
            return FeatureUtils.placeInWorld(holder.value(), level, pos, random, false);
        }
        return false;
    }

    /**
     * Places the {@link ConfiguredFeature} referenced by this key in the world.
     *
     * @param access    the {@link RegistryAccess} to resolve the {@link Holder} from, or {@code null} to
     *                  fail immediately
     * @param level     the level to place the feature in
     * @param pos       the position to place the feature at
     * @param random    the random source to use
     * @param generator the {@link ChunkGenerator} to use for the placement, or {@code null} to resolve
     *                  it automatically from the level
     * @return {@code true} if the feature was placed, {@code false} otherwise (for example if {@code access}
     * is {@code null} or this key is not registered)
     */
    public boolean placeInWorld(
            @Nullable RegistryAccess access,
            @NotNull WorldGenLevel level,
            @NotNull BlockPos pos,
            @NotNull RandomSource random,
            @Nullable ChunkGenerator generator
    ) {
        if (access == null) return false;
        final Holder<ConfiguredFeature<?, ?>> holder = getHolder(access);
        if (holder != null) {
            return FeatureUtils.placeInWorld(holder.value(), level, pos, random, generator, false);
        }
        return false;
    }


    /**
     * Creates a new {@link FeatureConfigurator} for this Feature.
     * <p>
     * The builder is used to alter the settings of this configured feature.
     * When done, you should call {@link FeatureConfigurator#register()}
     * to register the configured feature.
     *
     * @param ctx The {@link BootstrapContext} this bootstrap is performed in
     * @return The builder for this Feature
     * @see FeatureConfigurator#register()
     */
    public abstract B bootstrap(@NotNull BootstrapContext<ConfiguredFeature<?, ?>> ctx);
}