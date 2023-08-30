package org.betterx.wover.util;

import org.betterx.wover.core.api.ModCore;

import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;

/**
 * A set of resource locations that can contain wildcard locations. A wildcard location is a {@link ResourceLocation}
 * with a regular namespace and {@code *} as the path. For example, {@code minecraft:*} is a wildcard location for all
 * resources in the {@code minecraft} namespace.
 * <p>
 * This is a {@link HashSet} that overrides {@link #contains(Object)} to check for wildcard locations.
 * First, it checks if the location is contained as is in the set. If not, it checks if the namespace of the location
 * is contained in the set. If not, it returns false.
 */
public class ResourceLocationSet extends HashSet<ResourceLocation> {
    /**
     * Returns true if this set contains the specified element or a wildcard location for the element's namespace.
     *
     * @param o element whose presence in this set is to be tested
     * @return
     */
    @Override
    public boolean contains(Object o) {
        if (o instanceof ResourceLocation location) {
            return containsResource(location);
        }
        return super.contains(o);
    }

    private boolean containsResource(ResourceLocation o) {
        if (!super.contains(o)) {
            final var namespace = new WildcardResourceLocation(o.getNamespace());
            return super.contains(namespace);
        }

        return true;
    }

    public static class WildcardResourceLocation extends ResourceLocation {
        public WildcardResourceLocation(String namespace) {
            super(namespace, "*", null);
        }

        public WildcardResourceLocation(ResourceLocation location) {
            super(location.getNamespace(), "*", null);
        }

        public WildcardResourceLocation(ModCore mod) {
            super(mod.namespace, "*", null);
        }

        public static ResourceLocation parse(String string) {
            final var decomposed = ResourceLocation.decompose(string, ':');
            if (decomposed[1].equals("*")) {
                return new WildcardResourceLocation(decomposed[0]);
            } else {
                return new ResourceLocation(decomposed[0], decomposed[1]);
            }
        }
    }
}
