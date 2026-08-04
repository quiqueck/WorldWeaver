package de.ambertation.wover.item.api.model;

import de.ambertation.wover.item.api.trait.ItemTraitKey;

import java.util.Objects;

/**
 * The item-model counterpart of {@code ModelKey}: a typed, payload-carrying identity for a client-side item
 * model factory. A block/item carries an {@link ItemModelBinding} pairing an {@code ItemModelKey} with a
 * common-safe payload; the client source set resolves the key to a {@code ClientItemModelFactory} at datagen
 * time.
 *
 * @param <P> the common-safe payload type this item model shape consumes
 */
public final class ItemModelKey<P> {
    private final ItemTraitKey id;

    /**
     * Creates a new item model key wrapping the given (interned) trait key identity.
     *
     * @param id the interned identity of this model key
     */
    public ItemModelKey(ItemTraitKey id) {
        this.id = id;
    }

    /**
     * @return the interned identity of this model key
     */
    public ItemTraitKey id() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemModelKey<?> modelKey)) return false;
        return Objects.equals(id, modelKey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ItemModelKey{" + id + "}";
    }
}
