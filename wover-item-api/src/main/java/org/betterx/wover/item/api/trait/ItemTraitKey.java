package org.betterx.wover.item.api.trait;

import org.betterx.wover.core.api.ModCore;

import java.util.Objects;

public final class ItemTraitKey {
    public final String namespace;
    public final String path;

    private ItemTraitKey(ModCore modCore, String path) {
        this.namespace = modCore.namespace;
        this.path = path;
    }

    public static ItemTraitKey of(ModCore modCore, String path) {
        return new ItemTraitKey(modCore, path);
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
