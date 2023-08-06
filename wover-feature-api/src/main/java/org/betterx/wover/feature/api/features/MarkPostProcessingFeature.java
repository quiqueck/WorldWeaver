package org.betterx.wover.feature.api.features;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Places a mark for postprocessing  (<b>{@code wover:mark_postprocessing}</b>).
 *
 * @see org.betterx.wover.feature.api.Features#MARK_POSTPROCESSING
 */
public class MarkPostProcessingFeature extends Feature<NoneFeatureConfiguration> {
    /**
     * Creates a new instance.
     */
    public MarkPostProcessingFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    /**
     * Places a mark on the chunk for postprocessing.
     *
     * @param ctx The context
     * @return {@code true}
     */
    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        BlockPos pos = ctx.origin();
        ctx.level().getChunk(pos.getX() >> 4, pos.getZ() >> 4)
           .markPosForPostprocessing(new BlockPos(pos.getX() & 15, pos.getY(), pos.getZ() & 15));
        return true;
    }
}
