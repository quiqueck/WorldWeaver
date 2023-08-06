package org.betterx.wover.feature.api.features;

import org.betterx.wover.feature.api.features.config.PlaceBlockFeatureConfig;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

/**
 * Places a block (<b>{@code wover:place_block}</b>).
 *
 * @see org.betterx.wover.feature.api.Features#PLACE_BLOCK
 * @see org.betterx.wover.feature.api.features.config.PlaceFacingBlockConfig
 */
public class PlaceBlockFeature<FC extends PlaceBlockFeatureConfig> extends Feature<FC> {
    /**
     * Creates a new Instance.
     *
     * @param codec The codec to use for this instance
     */
    public PlaceBlockFeature(Codec<FC> codec) {
        super(codec);
    }

    /**
     * Places a block.
     *
     * @param ctx The context
     * @return {@code true}
     */
    @Override
    public boolean place(FeaturePlaceContext<FC> ctx) {
        return ctx.config().place(ctx);
    }
}
