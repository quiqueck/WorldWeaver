package org.betterx.wover.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Objects;

public class Triple<A, B, C> extends Pair<A, B> {
    public static <A, B, C> Codec<Triple<A, B, C>> tripleCodec(
            Codec<A> a,
            Codec<B> b,
            Codec<C> c,
            String first,
            String second,
            String third
    ) {
        return RecordCodecBuilder.create(instance -> instance
                .group(
                        a.fieldOf(first).forGetter(o -> o.first),
                        b.fieldOf(second).forGetter(o -> o.second),
                        c.fieldOf(third).forGetter(o -> o.third)
                )
                .apply(instance, Triple::new));
    }

    public final C third;

    public Triple(A first, B second, C third) {
        super(first, second);
        this.third = third;
    }

    @Override
    public String toString() {
        return "Triple{" + "first=" + first + ", second=" + second + ", third=" + third + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Triple)) return false;
        if (!super.equals(o)) return false;
        Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;
        return Objects.equals(third, triple.third);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), third);
    }
}

