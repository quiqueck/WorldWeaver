package org.betterx.wover.feature.api.placed;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    Holder<PlacedFeature> getHolder(@Nullable HolderGetter<PlacedFeature> getter);

    /**
     * Gets the {@link Holder} for the {@link PlacedFeature} from the given getter.
     *
     * <p>
     * This method internally looks up {@link Registries#PLACED_FEATURE}. If you need to retrieve
     * a lot of holders, it is recommended to manually lookup the
     * Registry first and use {@link #getHolder(HolderGetter)} instead.
     *
     * @param context The {@link BootstapContext} to get the holder from
     * @return The holder for the {@link PlacedFeature} or {@code null} if it is not present
     */
    Holder<PlacedFeature> getHolder(@NotNull BootstapContext<?> context);

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
