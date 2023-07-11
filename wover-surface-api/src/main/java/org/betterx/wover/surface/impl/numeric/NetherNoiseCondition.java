package org.betterx.wover.surface.impl.numeric;

import org.betterx.wover.math.api.random.RandomHelper;
import org.betterx.wover.surface.api.Conditions;
import org.betterx.wover.surface.api.conditions.SurfaceRulesContext;
import org.betterx.wover.surface.api.noise.NumericProvider;

import com.mojang.serialization.Codec;

public class NetherNoiseCondition implements NumericProvider {
    /**
     * A simple scalar random number provider
     */
    public static final NumericProvider INSTANCE = new NetherNoiseCondition();
    public static final Codec<NetherNoiseCondition> CODEC = Codec
            .BYTE.fieldOf("nether_noise")
                 .xmap(
                         (obj) -> (NetherNoiseCondition) INSTANCE,
                         obj -> (byte) 0
                 )
                 .codec();


    public NetherNoiseCondition() {
    }


    @Override
    public Codec<? extends NumericProvider> pcodec() {
        return CODEC;
    }

    @Override
    public int getNumber(SurfaceRulesContext context) {
        final int x = context.getBlockX();
        final int y = context.getBlockY();
        final int z = context.getBlockZ();
        double value = Conditions.NETHER_VOLUME_NOISE.getNoiseContext().getNoise().eval(
                x * Conditions.NETHER_VOLUME_NOISE.getScaleX(),
                y * Conditions.NETHER_VOLUME_NOISE.getScaleY(),
                z * Conditions.NETHER_VOLUME_NOISE.getScaleZ()
        );

        int offset = Conditions.NETHER_VOLUME_NOISE.getNoiseContext().getRandom().nextInt(20) == 0 ? 3 : 0;


        float cmp = RandomHelper.inRange(Conditions.NETHER_VOLUME_NOISE.getNoiseContext().getRandom(), 0.4F, 0.5F);
        if (value > cmp || value < -cmp) return 2 + offset;

        if (value > Conditions.NETHER_VOLUME_NOISE.getRoughness()
                                                  .sample(Conditions.NETHER_VOLUME_NOISE.getNoiseContext().getRandom()))
            return 0 + offset;

        return 1 + offset;
    }
}
