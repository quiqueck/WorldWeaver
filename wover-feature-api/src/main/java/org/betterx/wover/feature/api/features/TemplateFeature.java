package org.betterx.wover.feature.api.features;

import org.betterx.wover.feature.api.features.config.TemplateFeatureConfig;
import org.betterx.wover.structure.api.StructureNBT;

import com.mojang.serialization.Codec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

/**
 * Places a structure (<b>{@code wover:template}</b>).
 *
 * @param <FC> The config type
 * @see org.betterx.wover.feature.api.Features#TEMPLATE
 * @see TemplateFeatureConfig
 */
public class TemplateFeature<FC extends TemplateFeatureConfig> extends Feature<FC> {
    /**
     * Creates a new Instance.
     *
     * @param codec The codec to use for this instance
     */
    public TemplateFeature(Codec<FC> codec) {
        super(codec);
    }

    /**
     * Places a structure with a random rotation and mirror.
     *
     * @param ctx The context
     * @return {@code true} if the structure was placed
     */
    @Override
    public boolean place(FeaturePlaceContext<FC> ctx) {
        final TemplateFeatureConfig cfg = ctx.config();
        final RandomSource random = ctx.random();
        final TemplateFeatureConfig.FeatureTemplate structure = cfg.randomStructure(random);
        return structure.generateIfPlaceable(
                ctx.level(),
                ctx.origin(),
                StructureNBT.getRandomRotation(ctx.random()),
                StructureNBT.getRandomMirror(ctx.random())
        );
    }
}
