package de.ambertation.wover.math.api.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;

/**
 * A three-axis counterpart to vanilla's {@link IntProvider}: samples an x, y and z component
 * independently, each from its own {@link IntProvider}, and combines them into a {@link Vec3i}.
 * <p>
 * This is intended to be embedded (via {@link #CODEC} or {@link #codec(int, int)}) into other
 * datapack-driven types that need a randomized 3D offset or position, such as feature placement
 * modifiers. {@code wover-math-api} itself does not ship any datapack files; this class only
 * provides the reusable Java/Codec building block.
 */
public class Vec3iProvider {
    /**
     * Codec that (de)serializes a {@link Vec3iProvider} from an object with {@code x}, {@code y}
     * and {@code z} fields, each an {@link IntProvider}.
     */
    public static final Codec<Vec3iProvider> CODEC = RecordCodecBuilder.create((instance) -> instance
            .group(
                    IntProviders.CODEC.fieldOf("x").forGetter(o -> o.x),
                    IntProviders.CODEC.fieldOf("y").forGetter(o -> o.y),
                    IntProviders.CODEC.fieldOf("z").forGetter(o -> o.z)
            )
            .apply(instance, Vec3iProvider::new));

    /**
     * Creates a {@link #CODEC} variant that additionally validates that every axis' possible
     * value range is fully contained within {@code [minInclusive, maxInclusive]}.
     *
     * @param minInclusive the smallest value any axis of the decoded provider is allowed to
     *                      produce
     * @param maxInclusive the largest value any axis of the decoded provider is allowed to
     *                      produce
     * @return a codec that fails decoding with a descriptive error if the provider's value range
     * exceeds the given bounds
     */
    public static Codec<Vec3iProvider> codec(int minInclusive, int maxInclusive) {
        return CODEC.validate((provider) -> validate(minInclusive, maxInclusive, provider));
    }

    private static DataResult<Vec3iProvider> validate(int i, int j, Vec3iProvider provider) {
        if (provider.getMinValue() < i) {
            return DataResult.error(() -> {
                return "Value provider too low: " + i + " [" + provider.getMinValue() + "-" + provider.getMaxValue() + "]";
            });
        } else {
            return provider.getMaxValue() > j ? DataResult.error(() -> {
                return "Value provider too high: " + j + " [" + provider.getMinValue() + "-" + provider.getMaxValue() + "]";
            }) : DataResult.success(provider);
        }
    }

    /** The value provider used to sample the x component. */
    public final IntProvider x;
    /** The value provider used to sample the y component. */
    public final IntProvider y;
    /** The value provider used to sample the z component. */
    public final IntProvider z;

    /**
     * Creates a new provider that samples each axis from its own {@link IntProvider}.
     *
     * @param x the value provider for the x component
     * @param y the value provider for the y component
     * @param z the value provider for the z component
     */
    public Vec3iProvider(IntProvider x, IntProvider y, IntProvider z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Samples a random {@link Vec3i} by drawing each component from its respective
     * {@link IntProvider}.
     *
     * @param rnd the random source to sample from
     * @return a new vector with independently sampled x, y and z components
     */
    public Vec3i sample(RandomSource rnd) {
        return new Vec3i(x.sample(rnd), y.sample(rnd), z.sample(rnd));
    }

    /**
     * The smallest value that any axis of this provider can possibly sample.
     *
     * @return the minimum of the x, y and z providers' minimum values
     */
    public int getMinValue() {
        return Math.min(Math.min(x.minInclusive(), y.minInclusive()), z.minInclusive());
    }

    /**
     * The largest value that any axis of this provider can possibly sample.
     *
     * @return the maximum of the x, y and z providers' maximum values
     */
    public int getMaxValue() {
        return Math.max(Math.max(x.maxInclusive(), y.maxInclusive()), z.maxInclusive());
    }
}
