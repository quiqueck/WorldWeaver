package org.betterx.wover.block.api.trait;

import org.betterx.wover.core.api.ModCore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Uniquely identifies a {@link BlockTrait}/{@link BlockTraitBuilder} pair by namespace and path, similar to
 * a {@link net.minecraft.resources.ResourceLocation}. Instances are interned - {@link #of(ModCore, String)}
 * returns the same instance for the same namespace/path combination.
 */
public final class BlockTraitKey {
    private static final List<BlockTraitKey> TRAIT_KEYS = new ArrayList<>(32);
    /**
     * The namespace (mod id) this key belongs to.
     */
    public final String namespace;
    /**
     * The path identifying this key within its namespace.
     */
    public final String path;

    private BlockTraitKey(ModCore modCore, String path) {
        this.namespace = modCore.namespace;
        this.path = path;
    }

    /**
     * Creates a new key for the given namespace/path, throwing if that combination was already used.
     * Intended for trait builders that must only ever be defined once (typically singletons stored in a
     * static field).
     *
     * @param modCore The mod that owns the key
     * @param path    The path identifying the key within the mod's namespace
     * @return The newly created key
     * @throws IllegalStateException if a key with the same namespace/path already exists
     */
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

    /**
     * Gets (or lazily creates) the key for the given namespace/path.
     *
     * @param modCore The mod that owns the key
     * @param path    The path identifying the key within the mod's namespace
     * @return The key for the given namespace/path
     */
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