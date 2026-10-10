package de.ambertation.wover.surface.impl.conditions;


import de.ambertation.wover.surface.api.noise.NoiseParameterManager;
import de.ambertation.wover.surface.mixin.SurfaceRulesContextAccessor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviders;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import net.minecraft.world.level.levelgen.material.condition.ConditionEvaluator;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;
import net.minecraft.world.level.levelgen.synth.Noise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class RoughNoiseConditionImpl implements MaterialCondition {
    public static final MapCodec<RoughNoiseConditionImpl> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    ResourceKey.codec(Registries.NOISE).fieldOf("noise").forGetter(o -> o.noise),
                    Codec.DOUBLE.fieldOf("min_threshold").forGetter(o -> o.minThreshold),
                    Codec.DOUBLE.fieldOf("max_threshold").orElse(Double.MAX_VALUE).forGetter(o -> o.maxThreshold),
                    FloatProviders.CODEC.fieldOf("roughness").forGetter(o -> o.roughness)
            )
            .apply(
                    instance,
                    (noise1, minThreshold1, maxThreshold1, roughness1) -> new RoughNoiseConditionImpl(
                            noise1,
                            roughness1,
                            minThreshold1,
                            maxThreshold1
                    )
            ));

    private final ResourceKey<NormalNoise> noise;
    private final double minThreshold;
    private final double maxThreshold;
    private final FloatProvider roughness;

    public RoughNoiseConditionImpl(
            ResourceKey<NormalNoise> noise,
            FloatProvider roughness,
            double minThreshold,
            double maxThreshold
    ) {
        this.noise = noise;
        this.minThreshold = minThreshold;

        this.maxThreshold = maxThreshold;
        this.roughness = roughness;
    }

    public RoughNoiseConditionImpl(
            ResourceKey<NormalNoise> noise,
            double minThreshold,
            double maxThreshold
    ) {
        this(noise, UniformFloat.of(-0.1f, 0.4f), minThreshold, maxThreshold);
    }

    @Override
    public MapCodec<? extends MaterialCondition> codec() {
        return CODEC;
    }

    @Override
    public ConditionEvaluator compile(final MaterialRuleContext context2) {
        final SurfaceRulesContextAccessor ctx = SurfaceRulesContextAccessor.class.cast(context2);
        final Noise normalNoise = ctx.getRandomState().getOrCreateNoise(this.noise);
        final RandomSource roughnessSource = ctx.getRandomState()
                                                .getOrCreateRandomFactory(NoiseParameterManager.ROUGHNESS_NOISE.identifier())
                                                .fromHashOf(NoiseParameterManager.ROUGHNESS_NOISE.identifier());

        // 26.3 dropped the generic SurfaceRules.LazyCondition. LazyYCondition is invalidated on every Y
        // step and on every new column (MaterialRuleContext#updateXZ bumps both counters), which is the
        // same per-block caching the old lastUpdateY + lastUpdateXZ key gave us.
        class NoiseThresholdCondition extends MaterialRuleContext.LazyYCondition {
            NoiseThresholdCondition() {
                super(context2);
            }

            @Override
            protected boolean compute() {
                double d = normalNoise
                        .get(
                                ctx.getBlockX(),
                                ctx.getBlockY(),
                                ctx.getBlockZ()
                        ) + roughness.sample(roughnessSource);
                return d >= minThreshold && d <= maxThreshold;
            }
        }

        return new NoiseThresholdCondition();
    }
}
