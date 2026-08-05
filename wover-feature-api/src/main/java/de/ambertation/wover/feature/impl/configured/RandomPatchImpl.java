package de.ambertation.wover.feature.impl.configured;

import de.ambertation.wover.feature.api.configured.ConfiguredFeatureKey;
import de.ambertation.wover.feature.api.configured.configurators.RandomPatch;
import de.ambertation.wover.feature.api.placed.BasePlacedFeatureKey;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import de.ambertation.wover.feature.impl.random.RandomPatchFeature;
import de.ambertation.wover.feature.impl.random.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// Compat shim for the vanilla-removed random_patch feature (see RandomPatch); intentionally implements the
// deprecated API.
@SuppressWarnings("removal")
public class RandomPatchImpl extends FeatureConfiguratorImpl<RandomPatchConfiguration, RandomPatchFeature> implements RandomPatch {
    private Holder<PlacedFeature> featureToPlace;
    private int tries = 96;
    private int xzSpread = 7;
    private int ySpread = 3;

    RandomPatchImpl(
            @Nullable BootstrapContext<ConfiguredFeature<?, ?>> ctx,
            @Nullable ResourceKey<ConfiguredFeature<?, ?>> featureKey
    ) {
        super(ctx, featureKey);
    }

    @Override
    public RandomPatch likeDefaultNetherVegetation() {
        return likeDefaultNetherVegetation(8, 4);
    }

    @Override
    public RandomPatch likeDefaultNetherVegetation(int xzSpread, int ySpread) {
        this.xzSpread = xzSpread;
        this.ySpread = ySpread;
        tries = xzSpread * xzSpread;
        return this;
    }

    @Override
    public RandomPatch tries(int tries) {
        this.tries = tries;
        return this;
    }

    @Override
    public RandomPatch spreadXZ(int spread) {
        xzSpread = spread;
        return this;
    }

    @Override
    public RandomPatch spreadY(int spread) {
        ySpread = spread;
        return this;
    }

    @Override
    public <K extends BasePlacedFeatureKey<K>> RandomPatch featureToPlace(BasePlacedFeatureKey<K> featureToPlace) {
        return this.featureToPlace(featureToPlace.getHolder(bootstrapContext != null
                ? bootstrapContext
                : getTransitiveBootstrapContext()));
    }

    @Override
    public RandomPatch featureToPlace(Holder<PlacedFeature> featureToPlace) {
        this.featureToPlace = featureToPlace;
        return this;
    }

    @Override
    protected @NotNull RandomPatchFeature getFeature() {
        return de.ambertation.wover.feature.impl.FeatureManagerImpl.RANDOM_PATCH;
    }

    @Override
    protected @NotNull RandomPatchConfiguration createConfiguration() {
        if (featureToPlace == null) {
            throwStateError("No PlacedFeature was provided.");
        }
        return new RandomPatchConfiguration(tries, xzSpread, ySpread, featureToPlace);
    }

    public static class Key extends ConfiguredFeatureKey<RandomPatch> {
        public Key(Identifier id) {
            super(id);
        }

        @Override
        public RandomPatch bootstrap(@NotNull BootstrapContext<ConfiguredFeature<?, ?>> ctx) {
            return new RandomPatchImpl(ctx, key);
        }
    }
}
