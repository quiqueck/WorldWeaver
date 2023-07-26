package org.betterx.wover.biome.impl.modification.predicates;

import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;

import com.mojang.serialization.Codec;
import net.minecraft.util.KeyDispatchDataCodec;

public record IsNamespace(String namespace) implements BiomePredicate {
    public static final KeyDispatchDataCodec<IsNamespace> CODEC = KeyDispatchDataCodec
            .of(Codec.STRING
                    .xmap(IsNamespace::new, IsNamespace::namespace)
                    .fieldOf("namespace")
            );


    @Override
    public KeyDispatchDataCodec<? extends BiomePredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean test(Context ctx) {
        return ctx.biomeKey.location().getNamespace().equals(namespace);
    }
}
