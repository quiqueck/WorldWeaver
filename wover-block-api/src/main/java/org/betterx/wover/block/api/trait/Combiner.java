package org.betterx.wover.block.api.trait;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class Combiner {
    private static final List<BlockTrait<?, ?>> EMPTY = List.of();

    public static @NotNull List<BlockTrait<?, ?>> combine(BlockTrait<?, ?> t0) {
        if (t0 == null) return EMPTY;
        return List.of(t0);
    }

    public static @NotNull List<BlockTrait<?, ?>> combine(BlockTrait<?, ?> t0, BlockTrait<?, ?> t1) {
        if (t0 == null && t1 == null) return EMPTY;
        if (t0 == null) return List.of(t1);
        if (t1 == null) return List.of(t0);
        return List.of(t0, t1);
    }

    public static @NotNull List<BlockTrait<?, ?>> combine(
            BlockTrait<?, ?> t0,
            BlockTrait<?, ?> t1,
            BlockTrait<?, ?> t2
    ) {
        if (t0 == null && t1 == null && t2 == null) return EMPTY;
        if (t0 == null && t1 == null) return List.of(t2);
        if (t0 == null && t2 == null) return List.of(t1);
        if (t1 == null && t2 == null) return List.of(t0);
        if (t0 == null) return combine(t1, t2);
        if (t1 == null) return combine(t0, t2);
        if (t2 == null) return combine(t0, t1);
        return List.of(t0, t1, t2);
    }

    public static @NotNull Combiner of(List<BlockTrait<?, ?>> traits) {
        return new Combiner().add(traits);
    }

    public static @NotNull Combiner of(BlockTrait<?, ?>... traits) {
        return new Combiner().add(traits);
    }
    
    public static @NotNull Combiner of(@NotNull BlockTraitBuilder.WithDefault<?, ?> traitBuilder) {
        return new Combiner().add(traitBuilder.withDefault());
    }

    public static @NotNull Combiner of(@NotNull BlockTraitBuilder.WithDefaults<?, ?> traitBuilder) {
        return new Combiner().add(traitBuilder.withDefault());
    }

    private final @NotNull List<BlockTrait<?, ?>> blockTraits;

    private Combiner() {
        blockTraits = new ArrayList<>();
    }

    public Combiner add(BlockTrait<?, ?>... traits) {
        if (traits == null || traits.length == 0) return this;
        for (BlockTrait<?, ?> trait : traits) {
            if (trait != null) blockTraits.add(trait);
        }
        return this;
    }

    public Combiner add(List<BlockTrait<?, ?>> traits) {
        if (traits != null && !traits.isEmpty()) {
            for (BlockTrait<?, ?> trait : traits) {
                if (trait != null) blockTraits.add(trait);
            }
        }
        return this;
    }

    public Combiner add(@NotNull BlockTraitBuilder.WithDefault<?, ?> traitBuilder) {
        return this.add(traitBuilder.withDefault());
    }

    public Combiner add(@NotNull BlockTraitBuilder.WithDefaults<?, ?> traitBuilder) {
        return this.add(traitBuilder.withDefault());
    }

    public @NotNull List<BlockTrait<?, ?>> combine() {
        return blockTraits.isEmpty() ? EMPTY : blockTraits;
    }
}
