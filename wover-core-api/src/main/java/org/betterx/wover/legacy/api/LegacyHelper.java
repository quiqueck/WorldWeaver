package org.betterx.wover.legacy.api;

import org.betterx.wover.core.api.ModCore;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

public class LegacyHelper {
    public static <A> Codec<A> wrap(Codec<A> codec) {
        return new Codec<A>() {
            @Override
            public <T> DataResult<T> encode(final A input, final DynamicOps<T> ops, final T prefix) {
                return codec.encode(input, ops, prefix);
            }

            @Override
            public <T> DataResult<Pair<A, T>> decode(final DynamicOps<T> ops, final T input) {
                return codec.decode(ops, input);
            }

            @Override
            public String toString() {
                return codec.toString();
            }
        };
    }

    public static final ModCore WORLDS_TOGETHER_CORE = ModCore.create("worlds_together");
    public static final ModCore BCLIB_CORE = ModCore.create("bclib");
}
