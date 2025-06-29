package org.betterx.wover.block.api.trait;

import org.betterx.wover.core.api.ModCore;

import java.util.Objects;

public final class BlockTraitKey {
    public final String namespace;
    public final String path;

    private BlockTraitKey(ModCore modCore, String path) {
        this.namespace = modCore.namespace;
        this.path = path;
    }

    public static BlockTraitKey of(ModCore modCore, String path) {
        return new BlockTraitKey(modCore, path);
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