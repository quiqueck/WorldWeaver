package org.betterx.wover.feature.impl.placed;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlacedFeatureManagerImpl {
    @Nullable
    public static Holder<PlacedFeature> getHolder(
            @Nullable HolderGetter<PlacedFeature> getter,
            @NotNull ResourceKey<PlacedFeature> key
    ) {
        if (getter == null) return null;

        final Optional<Holder.Reference<PlacedFeature>> h = getter.get(key);
        return h.orElse(null);
    }
}
