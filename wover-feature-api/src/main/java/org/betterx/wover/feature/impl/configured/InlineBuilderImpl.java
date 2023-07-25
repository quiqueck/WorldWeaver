package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.feature.api.configured.ConfiguredFeatureManager;
import org.betterx.wover.feature.api.configured.builders.ForSimpleBlock;
import org.betterx.wover.feature.api.configured.builders.RandomPatch;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.ApiStatus;

public class InlineBuilderImpl implements ConfiguredFeatureManager.InlineBuilder {
    private final ResourceKey<PlacedFeature> key;

    public InlineBuilderImpl() {
        this(null);
    }

    /**
     * For internal use only
     *
     * @param key A Transitive key for the source {@link PlacedFeature}
     */
    @ApiStatus.Internal
    public InlineBuilderImpl(ResourceKey<PlacedFeature> key) {
        this.key = key;
    }

    public ForSimpleBlock simple() {
        final var res = new ForSimpleBlockImpl(null);
        res.setTransitiveFeatureKey(key);
        return res;
    }

    public RandomPatch randomPatch() {
        final var res = new RandomPatchImpl(null);
        res.setTransitiveFeatureKey(key);
        return res;
    }
}
