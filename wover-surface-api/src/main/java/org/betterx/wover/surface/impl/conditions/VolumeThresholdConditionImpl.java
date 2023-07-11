package org.betterx.wover.surface.impl.conditions;

import org.betterx.wover.math.api.noise.OpenSimplexNoise;
import org.betterx.wover.surface.api.conditions.SurfaceRulesContext;
import org.betterx.wover.surface.api.conditions.VolumeNoiseCondition;
import org.betterx.wover.surface.api.conditions.VolumeThresholdCondition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.ThreadSafeLegacyRandomSource;

import com.google.common.collect.Maps;

import java.util.Map;

public class VolumeThresholdConditionImpl extends VolumeNoiseCondition implements VolumeThresholdCondition {
    private static final Map<Long, Context> NOISES = Maps.newHashMap();
    public static final Codec<VolumeThresholdConditionImpl> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    Codec.LONG.fieldOf("seed").forGetter(p -> p.noiseContext.seed),
                    Codec.DOUBLE.fieldOf("threshold").orElse(0.0).forGetter(p -> p.threshold),
                    FloatProvider.CODEC.fieldOf("roughness").orElse(ConstantFloat.of(0)).forGetter(p -> p.roughness),
                    Codec.DOUBLE.fieldOf("scale_x").orElse(0.1).forGetter(p -> p.scaleX),
                    Codec.DOUBLE.fieldOf("scale_y").orElse(0.1).forGetter(p -> p.scaleY),
                    Codec.DOUBLE.fieldOf("scale_z").orElse(0.1).forGetter(p -> p.scaleZ)
            )
            .apply(instance, VolumeThresholdConditionImpl::new));
    public static final KeyDispatchDataCodec<VolumeThresholdConditionImpl> KEY_CODEC = KeyDispatchDataCodec.of(CODEC);
    public final Context noiseContext;
    public final double threshold;
    public final FloatProvider roughness;
    public final double scaleX;
    public final double scaleY;
    public final double scaleZ;

    @Override
    public VolumeThresholdCondition.Context getNoiseContext() {
        return noiseContext;
    }

    @Override
    public double getScaleX() {
        return scaleX;
    }

    @Override
    public double getScaleY() {
        return scaleY;
    }

    @Override
    public double getScaleZ() {
        return scaleZ;
    }

    @Override
    public FloatProvider getRoughness() {
        return roughness;
    }

    public VolumeThresholdConditionImpl(
            long noiseSeed,
            double threshold,
            FloatProvider roughness,
            double scaleX,
            double scaleY,
            double scaleZ
    ) {
        this.threshold = threshold;
        this.roughness = roughness;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;

        noiseContext = NOISES.computeIfAbsent(noiseSeed, seed -> new Context(seed));
    }

    public double getValue(SurfaceRulesContext context) {
        return getValue(context.getBlockX(), context.getBlockY(), context.getBlockZ());
    }

    public double getValue(int xx, int yy, int zz) {
        final double x = xx * scaleX;
        final double y = yy * scaleY;
        final double z = zz * scaleZ;

        if (noiseContext.lastX == x
                && noiseContext.lastY == y
                && noiseContext.lastZ == z)
            return noiseContext.lastValue + roughness.sample(noiseContext.random);

        double value = noiseContext.noise.eval(x, y, z);

        noiseContext.lastX = x;
        noiseContext.lastZ = z;
        noiseContext.lastY = y;
        noiseContext.lastValue = value;

        return value + roughness.sample(noiseContext.random);
    }

    @Override
    public boolean test(SurfaceRulesContext context) {
        return getValue(context) > threshold;
    }

    @Override
    public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
        return KEY_CODEC;
    }


    public static class Context implements VolumeThresholdCondition.Context {
        public final OpenSimplexNoise noise;
        public final RandomSource random;
        public final long seed;

        public OpenSimplexNoise getNoise() {
            return noise;
        }

        public RandomSource getRandom() {
            return random;
        }

        public long getSeed() {
            return seed;
        }

        double lastX = Integer.MIN_VALUE;
        double lastY = Integer.MIN_VALUE;
        double lastZ = Integer.MIN_VALUE;
        double lastValue = 0;

        Context(long seed) {
            this.seed = seed;
            this.noise = new OpenSimplexNoise(seed);
            this.random = new ThreadSafeLegacyRandomSource(seed * 3 + 1);
        }
    }
}
