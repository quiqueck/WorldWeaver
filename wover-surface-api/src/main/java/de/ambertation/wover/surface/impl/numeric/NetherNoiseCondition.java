package de.ambertation.wover.surface.impl.numeric;

import de.ambertation.wover.math.api.random.RandomHelper;
import de.ambertation.wover.surface.api.Conditions;
import de.ambertation.wover.surface.api.conditions.SurfaceRulesContext;
import de.ambertation.wover.surface.api.conditions.VolumeThresholdCondition;
import de.ambertation.wover.surface.api.noise.NumericProvider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;

public class NetherNoiseCondition implements NumericProvider {
    /**
     * A simple scalar random number provider
     */
    public static final NumericProvider INSTANCE = new NetherNoiseCondition();
    public static final MapCodec<NetherNoiseCondition> CODEC = Codec
            .BYTE.fieldOf("nether_noise")
                 .xmap(
                         (obj) -> (NetherNoiseCondition) INSTANCE,
                         obj -> (byte) 0
                 );


    public NetherNoiseCondition() {
    }


    @Override
    public MapCodec<? extends NumericProvider> pcodec() {
        return CODEC;
    }

    @Override
    public int getNumber(SurfaceRulesContext context) {
        final int x = context.getBlockX();
        final int y = context.getBlockY();
        final int z = context.getBlockZ();
        final VolumeThresholdCondition noise = Conditions.NETHER_VOLUME_NOISE;
        double value = noise.getNoiseContext().getNoise().eval(
                x * noise.getScaleX(),
                y * noise.getScaleY(),
                z * noise.getScaleZ()
        );

        // All three draws below come from one source seeded by this block's position. They used to be taken
        // from a single running source shared by every block and every worker thread, so which material a
        // block got depended on how many blocks had been surfaced before it - and the same seed therefore
        // could not build the same Nether twice.
        final RandomSource random = noise.getNoiseContext().randomAt(x, y, z);

        int offset = random.nextInt(20) == 0 ? 3 : 0;

        float cmp = RandomHelper.inRange(random, 0.4F, 0.5F);
        if (value > cmp || value < -cmp) return 2 + offset;

        if (value > noise.getRoughness().sample(random)) return 0 + offset;

        return 1 + offset;
    }
}
