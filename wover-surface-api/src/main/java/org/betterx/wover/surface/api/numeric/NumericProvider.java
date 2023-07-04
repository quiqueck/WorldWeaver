package org.betterx.wover.surface.api.numeric;

import org.betterx.wover.surface.mixin.SurfaceRulesContextAccessor;

import com.mojang.serialization.Codec;

import java.util.function.Function;

public interface NumericProvider {
    Codec<NumericProvider> CODEC = NumericProviderRegistry
            .NUMERIC_PROVIDER.byNameCodec()
                             .dispatch(
                                     NumericProvider::pcodec,
                                     Function.identity()
                             );
    int getNumber(SurfaceRulesContextAccessor context);

    Codec<? extends NumericProvider> pcodec();
}
