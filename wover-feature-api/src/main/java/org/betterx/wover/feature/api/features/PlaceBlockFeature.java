package org.betterx.wover.feature.api.features;

import org.betterx.wover.feature.api.features.config.PlaceBlockFeatureConfig;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class PlaceBlockFeature<FC extends PlaceBlockFeatureConfig> extends Feature<FC> {
    public PlaceBlockFeature(Codec<FC> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<FC> ctx) {
        return ctx.config().place(ctx);
    }
}
