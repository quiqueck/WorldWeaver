package org.betterx.wover.feature.impl.placed;

import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;
import org.betterx.wover.events.impl.EventImpl;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Optional;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlacedFeatureManagerImpl {
    public static final EventImpl<OnBootstrapRegistry<PlacedFeature>> BOOTSTRAP_PLACED_FEATURES =
            new EventImpl<>("BOOTSTRAP_PLACED_FEATURES");

    @Nullable
    public static Holder<PlacedFeature> getHolder(
            @Nullable HolderGetter<PlacedFeature> getter,
            @NotNull ResourceKey<PlacedFeature> key
    ) {
        if (getter == null) return null;

        final Optional<Holder.Reference<PlacedFeature>> h = getter.get(key);
        return h.orElse(null);
    }

    @ApiStatus.Internal
    public static void initialize() {
        DatapackRegistryBuilder.addBootstrap(
                Registries.PLACED_FEATURE,
                PlacedFeatureManagerImpl::onBootstrap
        );
    }

    private static void onBootstrap(BootstapContext<PlacedFeature> context) {
        BOOTSTRAP_PLACED_FEATURES.emit(c -> c.bootstrap(context));
    }
}
