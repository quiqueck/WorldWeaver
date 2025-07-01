package org.betterx.wover.block.api.trait;

import org.betterx.wover.core.api.ModCore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class BlockTraitKey {
    private static final List<BlockTraitKey> TRAIT_KEYS = new ArrayList<>(32);
    public final String namespace;
    public final String path;

    private BlockTraitKey(ModCore modCore, String path) {
        this.namespace = modCore.namespace;
        this.path = path;
    }

    public static BlockTraitKey ofUnique(ModCore modCore, String path) {
        var key = TRAIT_KEYS.stream()
                            .filter(k -> k.namespace.equals(modCore.namespace) && k.path.equals(path))
                            .findAny();
        if (key.isPresent()) {
            throw new IllegalStateException("Duplicate trait key: " + key);
        } else {
            BlockTraitKey newKey = new BlockTraitKey(modCore, path);
            TRAIT_KEYS.add(newKey);
            return newKey;
        }
    }

    public static BlockTraitKey of(ModCore modCore, String path) {
        return TRAIT_KEYS.stream()
                         .filter(k -> k.namespace.equals(modCore.namespace) && k.path.equals(path))
                         .findAny()
                         .orElseGet(() -> {
                             BlockTraitKey newKey = new BlockTraitKey(modCore, path);
                             TRAIT_KEYS.add(newKey);
                             return newKey;
                         });
    }

    @Override
    public String toString() {
        return "BlockTraitKey{" + namespace + ":" + path + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BlockTraitKey that = (BlockTraitKey) o;
        return Objects.equals(namespace, that.namespace) && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, path);
    }
}