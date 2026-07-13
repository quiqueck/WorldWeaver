package org.betterx.wover.feature.api.placed;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base interface for keys that reference a {@link PlacedFeature}.
 * <p>
 * This is shared by {@link PlacedFeatureKey} (which can start a fresh {@link FeaturePlacementBuilder}) and
 * {@link PlacedConfiguredFeatureKey} (which places a specific, already known {@link ConfiguredFeature}) and
 * exposes the parts common to both: resolving the {@link Holder}, and getting/setting the
 * {@link GenerationStep.Decoration} the feature is placed in.
 *
 * @param <K> The concrete type of the key, used to make the fluent {@link #setDecoration(GenerationStep.Decoration)}
 *            return the correct type
 */
public interface BasePlacedFeatureKey<K extends BasePlacedFeatureKey<K>> {
    /**
     * The key for the {@link PlacedFeature} you can use to reference it.
     *
     * @return The key
     */
    ResourceKey<PlacedFeature> key();

    /**
     * Gets the {@link Holder} for the {@link PlacedFeature} from the given getter.
     *
     * @param getter The getter to get the holder from or {@code null}
     * @return The holder for the {@link PlacedFeature} or {@code null} if it is not present
     */
    @Nullable
    Holder<PlacedFeature> getHolder(@Nullable HolderGetter<PlacedFeature> getter);

    /**
     * Gets the {@link Holder} for the {@link PlacedFeature} from the given getter.
     *
     * @param access The registry access to get the holder from
     * @return The holder for the {@link PlacedFeature} or {@code null} if it is not present
     */
    @Nullable
    Holder<PlacedFeature> getHolder(@Nullable RegistryAccess access);

    /**
     * Gets the {@link Holder} for the {@link PlacedFeature} from the given getter.
     *
     * <p>
     * This method internally looks up {@link Registries#PLACED_FEATURE}. If you need to retrieve
     * a lot of holders, it is recommended to manually lookup the
     * Registry first and use {@link #getHolder(HolderGetter)} instead.
     *
     * @param context The {@link BootstrapContext} to get the holder from
     * @return The holder for the {@link PlacedFeature} or {@code null} if it is not present
     */
    Holder<PlacedFeature> getHolder(@NotNull BootstrapContext<?> context);

    /**
     * Get the {@link GenerationStep.Decoration} for the {@link PlacedFeature}.
     *
     * @return The decoration
     */
    GenerationStep.Decoration getDecoration();

    /**
     * Sets the {@link GenerationStep.Decoration} for the {@link PlacedFeature}.
     *
     * @param decoration The decoration to set
     * @return This {@link PlacedFeatureKey} for chaining
     */
    K setDecoration(GenerationStep.Decoration decoration);
}
