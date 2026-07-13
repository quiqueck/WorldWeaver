package org.betterx.wover.legacy.api;

import org.betterx.wover.core.api.ModCore;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;

import java.util.stream.Stream;

/**
 * Helpers to ease migration from BCLib/"Worlds Together" (the predecessors of WorldWeaver) to WorldWeaver.
 * <p>
 * {@link #WORLDS_TOGETHER_CORE} and {@link #BCLIB_CORE} identify those legacy mods, and {@link #wrap(Codec)}/
 * {@link #wrap(MapCodec)} can be used to re-expose a {@link Codec}/{@link MapCodec} under a plain interface
 * (hiding record-ness or other implementation details) when interfacing with old, legacy code that expects one.
 */
public class LegacyHelper {
    /**
     * Wraps the given {@link Codec} in a new instance that forwards every call to it. This can be used to hide
     * implementation details (for example that the passed codec is a {@code record}) from legacy code that
     * expects a plain {@link Codec} instance.
     *
     * @param codec The codec to wrap.
     * @param <A>   The type that is encoded/decoded by the codec.
     * @return A new {@link Codec} instance that forwards all calls to {@code codec}.
     */
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

    /**
     * Wraps the given {@link MapCodec} in a new instance that forwards every call to it. This can be used to hide
     * implementation details (for example that the passed codec is a {@code record}) from legacy code that
     * expects a plain {@link MapCodec} instance.
     *
     * @param codec The codec to wrap.
     * @param <A>   The type that is encoded/decoded by the codec.
     * @return A new {@link MapCodec} instance that forwards all calls to {@code codec}.
     */
    public static <A> MapCodec<A> wrap(MapCodec<A> codec) {
        return new MapCodec<A>() {
            @Override
            public <T> RecordBuilder<T> encode(A input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                return codec.encode(input, ops, prefix);
            }

            @Override
            public <T> DataResult<A> decode(DynamicOps<T> ops, MapLike<T> input) {
                return codec.decode(ops, input);
            }

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return codec.keys(ops);
            }

            @Override
            public String toString() {
                return codec.toString();
            }
        };
    }

    /** {@link ModCore} identifying the legacy "Worlds Together" mod. */
    public static final ModCore WORLDS_TOGETHER_CORE = ModCore.create("worlds_together");
    /** {@link ModCore} identifying the legacy BCLib mod. */
    public static final ModCore BCLIB_CORE = ModCore.create("bclib");

    /**
     * Whether compatibility with the legacy BCLib/"Worlds Together" mods is currently enabled.
     *
     * @return Always {@code false}. Legacy compatibility is not currently implemented.
     */
    public static boolean isLegacyEnabled() {
        return false;
    }
}
