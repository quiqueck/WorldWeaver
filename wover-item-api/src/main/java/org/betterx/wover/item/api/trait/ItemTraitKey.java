package org.betterx.wover.item.api.trait;

import org.betterx.wover.core.api.ModCore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ItemTraitKey {
    private static final List<ItemTraitKey> TRAIT_KEYS = new ArrayList<>(4);

    public final String namespace;
    public final String path;

    private ItemTraitKey(ModCore modCore, String path) {
        this.namespace = modCore.namespace;
        this.path = path;
    }

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
