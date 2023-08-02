package org.betterx.wover.feature.api.features;

import org.betterx.wover.feature.api.features.config.TemplateFeatureConfig;
import org.betterx.wover.feature.impl.features.FeatureTemplate;
import org.betterx.wover.structure.api.StructureNBT;

import com.mojang.serialization.Codec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class TemplateFeature<FC extends TemplateFeatureConfig> extends Feature<FC> {
    public TemplateFeature(Codec<FC> codec) {
        super(codec);
    }

    protected FeatureTemplate randomStructure(TemplateFeatureConfig cfg, RandomSource random) {
        return cfg.structures.getRandomValue(random);
    }

    @Override
    public boolean place(FeaturePlaceContext<FC> ctx) {
        FeatureTemplate structure = randomStructure(ctx.config(), ctx.random());
        return structure.generateIfPlaceable(
                ctx.level(),
                ctx.origin(),
                StructureNBT.getRandomRotation(ctx.random()),
                StructureNBT.getRandomMirror(ctx.random())
        );
    }
}
