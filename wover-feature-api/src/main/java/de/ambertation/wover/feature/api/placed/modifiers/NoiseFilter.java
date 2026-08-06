package de.ambertation.wover.feature.api.placed.modifiers;

import de.ambertation.wover.feature.impl.placed.modifiers.PlacementModifiersImpl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import org.jetbrains.annotations.NotNull;

/**
 * Rejects the input based on the noise value at the xz-coordinate of the input position
 * <p>
 * The modifier will look up the noise-vale {@code v} at {@code (x*scaleXZ, y*scaleY, z*scaleXZ)}. If {@code min < v < max}
 * the position will be accepted. Otherwise, it is rejected.
 */
public class NoiseFilter extends PlacementFilter {
    /**
     * Codec for this placement modifier.
     */
    public static final MapCodec<NoiseFilter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    ResourceKey.codec(Registries.NOISE).fieldOf("noise").forGetter(o -> o.noise),
                    Codec.DOUBLE.optionalFieldOf("min_noise_level", -Double.MAX_VALUE).forGetter(o -> o.minNoiseLevel),
                    Codec.DOUBLE.optionalFieldOf("max_noise_level", Double.MAX_VALUE).forGetter(o -> o.maxNoiseLevel),
                    Codec.FLOAT.optionalFieldOf("scale_xz", 1f).forGetter(o -> o.scaleXZ),
                    Codec.FLOAT.optionalFieldOf("scale_y", 1f).forGetter(o -> o.scaleY)
            )
            .apply(instance, NoiseFilter::new));

    private final ResourceKey<NormalNoise.NoiseParameters> noise;

    private final double minNoiseLevel;
    private final double maxNoiseLevel;
    private final float scaleXZ;
    private final float scaleY;


    /**
     * Creates a new instance
     *
     * @param noise         The noise to use
     * @param minNoiseLevel The minimum noise level
     * @param maxNoiseLevel The maximum noise level
     * @param scaleXZ       The xz-scale for the input position
     * @param scaleY        The y-scale for the input position
     */
    public NoiseFilter(
            ResourceKey<NormalNoise.NoiseParameters> noise,
            double minNoiseLevel,
            double maxNoiseLevel,
            float scaleXZ,
            float scaleY
    ) {
        this.noise = noise;
        this.minNoiseLevel = minNoiseLevel;
        this.maxNoiseLevel = maxNoiseLevel;
        this.scaleXZ = scaleXZ;
        this.scaleY = scaleY;
    }

    /**
     * Tests the input position against the noise value at the xz-coordinate of the input position
     * <p>
     * The noise comes from the level's own {@link RandomState}, which seeds it from the world seed and
     * caches it per world. It must not come from {@code NoiseParameterManager.getOrCreateNoise}, which is
     * what this used to call: that cache is static, keyed on the noise alone, and seeded by whichever
     * {@link RandomSource} reached it first in the process. The field a world got therefore depended on
     * which chunk happened to be generated first in that session, so it differed between loads of the
     * same world and was shared across every world open in one process. Chunks generated before and
     * after a restart were laid out against different fields and did not line up along their seam.
     * <p>
     * {@code RoughNoiseConditionImpl} on the surface-rule side already reads its noise this way; this is
     * the placement side catching up.
     *
     * @param ctx    The placement context
     * @param random The random source
     * @param pos    The input position
     * @return {@code true} if the noise value at {@code (x*scaleXZ, y*scaleY, z*scaleXZ)} is in the range {@code (minNoiseLevel, maxNoiseLevel)}
     */
    @Override
    protected boolean shouldPlace(PlacementContext ctx, RandomSource random, BlockPos pos) {
        final RandomState randomState = ctx.getLevel().getLevel().getChunkSource().randomState();
        final NormalNoise normalNoise = randomState.getOrCreateNoise(this.noise);
        final double v = normalNoise.getValue(pos.getX() * scaleXZ, pos.getY() * scaleY, pos.getZ() * scaleXZ);
        return v > minNoiseLevel && v < maxNoiseLevel;
    }

    /**
     * Gets the type of this placement modifier
     *
     * @return The type of this placement modifier
     */
    @Override
    public @NotNull PlacementModifierType<?> type() {
        return PlacementModifiersImpl.NOISE_FILTER;
    }
}
