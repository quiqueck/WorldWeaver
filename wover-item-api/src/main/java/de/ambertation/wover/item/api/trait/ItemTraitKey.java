package de.ambertation.wover.item.api.trait;

import de.ambertation.wover.core.api.ModCore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A unique, interned identifier for an {@link ItemTrait}/{@link RuntimeItemTrait} kind.
 *
 * <p>Trait keys are identified by a namespace/path pair (similar to a {@link net.minecraft.resources.ResourceLocation})
 * and are interned per mod namespace + path combination: calling {@link #of(ModCore, String)} twice with the same
 * arguments returns the same instance. This allows traits to be compared cheaply and consistently, both for
 * checking whether an {@link de.ambertation.wover.item.api.ItemDefinition} already carries a trait
 * ({@link ItemTraitLookup}) and for checking whether a built item carries a {@link RuntimeItemTrait} at runtime
 * ({@link RuntimeItemTrait#is(ItemTraitKey)}).
 *
 * @see ItemTrait
 * @see RuntimeItemTrait
 * @see AbstractItemTraitBuilder
 */
public final class ItemTraitKey {
    private static final List<ItemTraitKey> TRAIT_KEYS = new ArrayList<>(4);

    /**
     * The namespace (mod id) this key was created for
     */
    public final String namespace;
    /**
     * The path identifying this trait within its namespace
     */
    public final String path;

    private ItemTraitKey(ModCore modCore, String path) {
        this.namespace = modCore.namespace;
        this.path = path;
    }

    /**
     * Creates a new trait key for the given namespace/path combination, failing if one already exists.
     * Use this for trait kinds that must only ever be created once (e.g. from a singleton trait builder).
     *
     * @param modCore The mod core whose namespace the key belongs to
     * @param path    The path identifying this trait within the namespace
     * @return The newly created trait key
     * @throws IllegalStateException if a key with the same namespace and path was already created
     */
    public static ItemTraitKey ofUnique(ModCore modCore, String path) {
        var key = TRAIT_KEYS.stream()
                            .filter(k -> k.namespace.equals(modCore.namespace) && k.path.equals(path))
                            .findAny();
        if (key.isPresent()) {
            throw new IllegalStateException("Duplicate trait key: " + key);
        } else {
            ItemTraitKey newKey = new ItemTraitKey(modCore, path);
            TRAIT_KEYS.add(newKey);
            return newKey;
        }
    }

    /**
     * Gets or creates the interned trait key for the given namespace/path combination.
     * Unlike {@link #ofUnique(ModCore, String)}, repeated calls with the same arguments return the same instance
     * instead of throwing.
     *
     * @param modCore The mod core whose namespace the key belongs to
     * @param path    The path identifying this trait within the namespace
     * @return The interned trait key for this namespace/path combination
     */
    public static ItemTraitKey of(ModCore modCore, String path) {
        return TRAIT_KEYS.stream()
                         .filter(k -> k.namespace.equals(modCore.namespace) && k.path.equals(path))
                         .findAny()
                         .orElseGet(() -> {
                             ItemTraitKey newKey = new ItemTraitKey(modCore, path);
                             TRAIT_KEYS.add(newKey);
                             return newKey;
                         });
    }

    @Override
    public String toString() {
        return "ItemTraitKey{" + namespace + ":" + path + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ItemTraitKey that = (ItemTraitKey) o;
        return Objects.equals(namespace, that.namespace) && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, path);
    }
}
