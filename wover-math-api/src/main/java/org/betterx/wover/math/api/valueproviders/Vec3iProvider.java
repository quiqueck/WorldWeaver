package org.betterx.wover.math.api.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;

public class Vec3iProvider {
    public static final Codec<Vec3iProvider> CODEC = RecordCodecBuilder.create((instance) -> instance
            .group(
                    IntProvider.CODEC.fieldOf("x").forGetter(o -> o.x),
                    IntProvider.CODEC.fieldOf("y").forGetter(o -> o.y),
                    IntProvider.CODEC.fieldOf("z").forGetter(o -> o.z)
            )
            .apply(instance, Vec3iProvider::new));

    public static Codec<Vec3iProvider> codec(int i, int j) {
        return ExtraCodecs.validate(CODEC, provider -> {
            if (provider.getMinValue() < i) {
                return DataResult.error(() -> "Value provider too low: " + i + " [" + provider.getMinValue() + "-" + provider.getMaxValue() + "]");
            }
            if (provider.getMaxValue() > j) {
                return DataResult.error(() -> "Value provider too high: " + j + " [" + provider.getMinValue() + "-" + provider.getMaxValue() + "]");
            }
            return DataResult.success(provider);
        });
    }

    public final IntProvider x;
    public final IntProvider y;
    public final IntProvider z;

    public Vec3iProvider(IntProvider x, IntProvider y, IntProvider z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3i sample(RandomSource rnd) {
        return new Vec3i(x.sample(rnd), y.sample(rnd), z.sample(rnd));
    }

    public int getMinValue() {
        return Math.min(Math.min(x.getMinValue(), y.getMinValue()), z.getMinValue());
    }

    public int getMaxValue() {
        return Math.max(Math.max(x.getMaxValue(), y.getMaxValue()), z.getMaxValue());
    }
}
