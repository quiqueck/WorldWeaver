package de.ambertation.wover.block.api.model;

import de.ambertation.wover.block.api.trait.BlockTraitKey;

import java.util.Objects;

/**
 * A typed, payload-carrying identity for a client-side block model factory. Every ready-made model shape (and
 * every third-party one) is identified by a {@code ModelKey}; a block carries a {@link BlockModelBinding} that
 * pairs a {@code ModelKey} with a common-safe {@code payload}, and the client source set resolves the key to a
 * {@code ClientModelFactory} at datagen time (see {@code ClientBlockModelRegistry}).
 * <p>
 * The key wraps an interned {@link BlockTraitKey} for its identity, so equal namespace/path keys are equal (and
 * usable as {@link java.util.Map} keys in the client registry). {@code ModelKey} itself references only
 * common-safe types, so it - unlike the client factory it resolves to - can live in {@code src/main} and be
 * attached from common block-creation code.
 *
 * @param <P> the common-safe payload type this model shape consumes (e.g. {@code Supplier<Block>}, a small
 *            {@code record}, or {@link Void} for shapes that need no payload)
 */
public final class ModelKey<P> {
    private final BlockTraitKey id;

    /**
     * Creates a new model key wrapping the given (interned) trait key identity.
     *
     * @param id the interned identity of this model key
     */
    public ModelKey(BlockTraitKey id) {
        this.id = id;
    }

    /**
     * @return the interned identity of this model key
     */
    public BlockTraitKey id() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModelKey<?> modelKey)) return false;
        return Objects.equals(id, modelKey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ModelKey{" + id + "}";
    }
}
