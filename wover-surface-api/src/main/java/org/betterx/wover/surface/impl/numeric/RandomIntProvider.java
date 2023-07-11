package org.betterx.wover.surface.impl.numeric;

import org.betterx.wover.math.api.MathHelper;
import org.betterx.wover.surface.api.conditions.SurfaceRulesContext;
import org.betterx.wover.surface.api.noise.NumericProvider;

import com.mojang.serialization.Codec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

import java.util.Objects;

public final class RandomIntProvider implements NumericProvider {
    public static final Codec<RandomIntProvider> CODEC = Codec
            .INT.fieldOf("range")
                .xmap(RandomIntProvider::new, obj -> obj.range)
                .codec();
    public final int range;
    private final RandomSource random;


    RandomIntProvider(int range) {
        this.range = range;
        random = new XoroshiroRandomSource(MathHelper.getSeed(range));
    }

    public static RandomIntProvider max(int range) {
        return new RandomIntProvider(range);
    }

    @Override
    public int getNumber(SurfaceRulesContext context) {
        return random.nextInt(range);
    }

    @Override
    public Codec<? extends NumericProvider> pcodec() {
        return CODEC;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (RandomIntProvider) obj;
        return this.range == that.range;
    }

    @Override
    public int hashCode() {
        return Objects.hash(range);
    }

    @Override
    public String toString() {
        return "RandomIntProvider[" +
                "range=" + range + ']';
    }
}
