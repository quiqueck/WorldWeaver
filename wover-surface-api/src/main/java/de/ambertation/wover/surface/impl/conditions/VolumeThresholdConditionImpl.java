package de.ambertation.wover.surface.impl.conditions;

import de.ambertation.wover.math.api.MathHelper;
import de.ambertation.wover.math.api.noise.OpenSimplexNoise;
import de.ambertation.wover.surface.api.conditions.SurfaceRulesContext;
import de.ambertation.wover.surface.api.conditions.VolumeNoiseCondition;
import de.ambertation.wover.surface.api.conditions.VolumeThresholdCondition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.ThreadSafeLegacyRandomSource;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VolumeThresholdConditionImpl extends VolumeNoiseCondition implements VolumeThresholdCondition {
    /**
     * One {@link Context} per noise seed, shared by every evaluation of this condition.
     * <p>
     * Concurrent, because worldgen evaluates surface rules on several chunk worker threads at once and
     * this is reached through {@code computeIfAbsent}. Doing that on a plain {@code HashMap} from more
     * than one thread can lose an entry or corrupt the table outright, which is a data race rather than
     * merely a source of non-determinism.
     */
    private static final Map<Long, Context> NOISES = new ConcurrentHashMap<>();
    public static final MapCodec<VolumeThresholdConditionImpl> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
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
        // The roughness comes from a source seeded by the block position rather than from a running one:
        // the same block has to yield the same value whichever worker thread asks for it, and no matter
        // how many other blocks were evaluated before it.
        return noiseContext.eval(xx * scaleX, yy * scaleY, zz * scaleZ)
                + roughness.sample(noiseContext.randomAt(xx, yy, zz));
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
        /**
         * @deprecated see {@link VolumeThresholdCondition.Context#getRandom()}
         */
        @Deprecated(forRemoval = true)
        public final RandomSource random;
        public final long seed;

        // The one-entry memo the noise evaluation used to keep lived on this shared Context, so two worker
        // threads evaluating different columns overwrote each other's entry - and a thread could read back
        // a value another thread had just stored for a different position. It is per thread now, which makes
        // it a pure memo: a hit returns exactly what a miss would have computed.
        private final ThreadLocal<double[]> memo = ThreadLocal.withInitial(
                () -> new double[]{Double.NaN, Double.NaN, Double.NaN, 0}
        );

        public OpenSimplexNoise getNoise() {
            return noise;
        }

        /**
         * {@inheritDoc}
         */
        @Deprecated(forRemoval = true)
        public RandomSource getRandom() {
            return random;
        }

        public long getSeed() {
            return seed;
        }

        double eval(double x, double y, double z) {
            final double[] last = memo.get();
            if (last[0] == x && last[1] == y && last[2] == z) return last[3];

            final double value = noise.eval(x, y, z);
            last[0] = x;
            last[1] = y;
            last[2] = z;
            last[3] = value;
            return value;
        }

        Context(long seed) {
            this.seed = seed;
            this.noise = new OpenSimplexNoise(seed);
            this.random = new ThreadSafeLegacyRandomSource(seed * 3 + 1);
        }
    }
}
