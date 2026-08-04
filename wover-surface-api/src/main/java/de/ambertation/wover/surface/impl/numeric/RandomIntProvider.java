package de.ambertation.wover.surface.impl.numeric;

import de.ambertation.wover.math.api.MathHelper;
import de.ambertation.wover.surface.api.conditions.SurfaceRulesContext;
import de.ambertation.wover.surface.api.noise.NumericProvider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;

import java.util.Objects;

public final class RandomIntProvider implements NumericProvider {
    public static final MapCodec<RandomIntProvider> CODEC = Codec
            .INT.fieldOf("range")
                .xmap(RandomIntProvider::new, obj -> obj.range);
    public final int range;
    private final int seed;


    RandomIntProvider(int range) {
        this.range = range;
        this.seed = (int) MathHelper.getSeed(range);
    }

    public static RandomIntProvider max(int range) {
        return new RandomIntProvider(range);
    }

    @Override
    public int getNumber(SurfaceRulesContext context) {
        // Seeded from the block position instead of drawn from one running source shared by every block on
        // every worker thread: which entry of a SwitchRuleSource a block picks has to be the same every time
        // the same seed is generated, and the order in which chunks reach the surface builder is not.
        final RandomSource random = RandomSource.create(MathHelper.getSeed(
                seed,
                context.getBlockX(),
                context.getBlockY(),
                context.getBlockZ()
        ));
        return random.nextInt(range);
    }

    @Override
    public MapCodec<? extends NumericProvider> pcodec() {
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
