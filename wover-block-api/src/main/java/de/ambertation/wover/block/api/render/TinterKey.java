package de.ambertation.wover.block.api.render;

import de.ambertation.wover.block.api.trait.BlockTraitKey;

import java.util.Objects;

/**
 * A typed, payload-carrying identity for a client-side {@code BlockTintSource} factory - the tint analogue of
 * {@link de.ambertation.wover.block.api.model.ModelKey}.
 * <p>
 * A block carries a {@link TintBinding} pairing a {@code TinterKey} with a common-safe payload, and the client
 * source set resolves the key to a {@code TintSourceFactory} (see {@code ClientTinterRegistry}). The resulting
 * {@code BlockTintSource} drives both halves of a block's colour: the in-world block colour registered at client
 * init, and - when the binding opts in - the constant tint baked into the generated item model.
 * <p>
 * {@code TinterKey} references only common-safe types, so it can live in {@code src/main} and be attached from
 * common block-creation code, unlike the client factory it resolves to.
 *
 * @param <P> the common-safe payload type this tint shape consumes (e.g. {@link Integer}, a small {@code record},
 *            or {@link Void} for shapes that need no payload)
 */
public final class TinterKey<P> {
    private final BlockTraitKey id;

    /**
     * Creates a new tinter key wrapping the given (interned) trait key identity.
     *
     * @param id the interned identity of this tinter key
     */
    public TinterKey(BlockTraitKey id) {
        this.id = id;
    }

    /**
     * @return the interned identity of this tinter key
     */
    public BlockTraitKey id() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TinterKey<?> tinterKey)) return false;
        return Objects.equals(id, tinterKey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TinterKey{" + id + "}";
    }
}
