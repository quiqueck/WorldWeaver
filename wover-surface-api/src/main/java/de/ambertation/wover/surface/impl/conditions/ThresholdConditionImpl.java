package de.ambertation.wover.surface.impl.conditions;

import de.ambertation.wover.math.api.MathHelper;
import de.ambertation.wover.math.api.noise.OpenSimplexNoise;
import de.ambertation.wover.surface.api.conditions.SurfaceNoiseCondition;
import de.ambertation.wover.surface.api.conditions.SurfaceRulesContext;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.levelgen.SurfaceRules;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ThresholdConditionImpl extends SurfaceNoiseCondition {
    /**
     * One {@link Context} per noise seed, shared by every evaluation of this condition.
     * <p>
     * Concurrent, because worldgen evaluates surface rules on several chunk worker threads at once and
     * this is reached through {@code computeIfAbsent}. Doing that on a plain {@code HashMap} from more
     * than one thread can lose an entry or corrupt the table outright, which is a data race rather than
     * merely a source of non-determinism.
     */
    private static final Map<Long, Context> NOISES = new ConcurrentHashMap<>();
    public static final MapCodec<ThresholdConditionImpl> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    Codec.LONG.fieldOf("seed").forGetter(p -> p.noiseContext.seed),
                    Codec.DOUBLE.fieldOf("threshold").orElse(0.0).forGetter(p -> p.threshold),
                    FloatProvider.CODEC.fieldOf("roughness").orElse(ConstantFloat.of(0)).forGetter(p -> p.roughness),
                    Codec.DOUBLE.fieldOf("scale_x").orElse(0.1).forGetter(p -> p.scaleX),
                    Codec.DOUBLE.fieldOf("scale_z").orElse(0.1).forGetter(p -> p.scaleZ)
            )
            .apply(instance, ThresholdConditionImpl::new));
    public static final KeyDispatchDataCodec<ThresholdConditionImpl> KEY_CODEC = KeyDispatchDataCodec.of(CODEC);
    private final Context noiseContext;
    private final double threshold;
    private final FloatProvider roughness;
    private final double scaleX;
    private final double scaleZ;

    public ThresholdConditionImpl(
            long noiseSeed,
            double threshold,
            FloatProvider roughness,
            double scaleX,
            double scaleZ
    ) {
        this.threshold = threshold;
        this.roughness = roughness;
        this.scaleX = scaleX;
        this.scaleZ = scaleZ;

        noiseContext = NOISES.computeIfAbsent(noiseSeed, Context::new);
    }

    @Override
    public boolean test(SurfaceRulesContext context) {
        final int blockX = context.getBlockX();
        final int blockZ = context.getBlockZ();

        // The roughness comes from a source seeded by the block position rather than from a running one:
        // the same column has to yield the same value whichever worker thread asks for it, and no matter
        // how many other columns were evaluated before it.
        final double value = noiseContext.eval(blockX * scaleX, blockZ * scaleZ);
        return value + roughness.sample(noiseContext.randomAt(blockX, blockZ)) > threshold;
    }

    @Override
    public KeyDispatchDataCodec<? extends SurfaceRules.ConditionSource> codec() {
        return KEY_CODEC;
    }

    static class Context {
        public final OpenSimplexNoise noise;
        public final long seed;

        // Per thread, not shared: two worker threads evaluating different columns used to overwrite each
        // other's entry here, and could read back a value stored for a different position. Per thread it is
        // a pure memo - a hit returns exactly what a miss would have computed.
        private final ThreadLocal<double[]> memo = ThreadLocal.withInitial(
                () -> new double[]{Double.NaN, Double.NaN, 0}
        );

        Context(long seed) {
            this.seed = seed;
            this.noise = new OpenSimplexNoise(seed);
        }

        double eval(double x, double z) {
            final double[] last = memo.get();
            if (last[0] == x && last[1] == z) return last[2];

            final double value = noise.eval(x, z);
            last[0] = x;
            last[1] = z;
            last[2] = value;
            return value;
        }

        RandomSource randomAt(int x, int z) {
            return RandomSource.create(MathHelper.getSeed(Long.hashCode(seed), x, 0, z));
        }
    }
}
