package de.ambertation.wover.common.registry.api;

import net.minecraft.resources.ResourceLocation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure-logic JUnit tests for {@link CustomRegistryData.DataKey}. These need no running server (only the
 * value-type {@link ResourceLocation}, which constructs without a Minecraft bootstrap) and run via
 * {@code ./gradlew :wover-common-api:test}.
 * <p>
 * The whole custom-registry-data mechanism keys its storage on {@code DataKey}: callers do
 * {@code wover_putData(createKey(id), data)} at one call site and {@code wover_getData(createKey(id))} at
 * another, expecting the second, independently-built key to retrieve what the first stored. That only works
 * because {@code DataKey} has <b>value semantics</b> derived solely from its {@code id} — identity is never
 * used, and the generic type parameter is erased and irrelevant to equality. A version port that regressed
 * this to reference equality (or folded the type into the comparison) would compile cleanly but silently
 * break every custom-data lookup, so it is worth pinning.
 */
class CustomRegistryDataKeyTest {
    @Test
    void createKeyExposesThePassedId() {
        final ResourceLocation id = ResourceLocation.fromNamespaceAndPath("wover", "test_key");
        final CustomRegistryData.DataKey<String> key = CustomRegistryData.createKey(id);

        assertNotNull(key);
        assertSame(id, key.id, "createKey must retain the exact ResourceLocation it was given");
    }

    @Test
    void keysAreEqualWhenIdsAreEqualEvenAcrossSeparateConstruction() {
        // Two independently built ResourceLocations that describe the same id...
        final CustomRegistryData.DataKey<String> a = CustomRegistryData.createKey(ResourceLocation.parse("wover:data"));
        final CustomRegistryData.DataKey<String> b = CustomRegistryData.createKey(
                ResourceLocation.fromNamespaceAndPath("wover", "data")
        );

        // ...must compare equal and share a hash bucket, otherwise a get()/put() pair with matching ids misses.
        assertNotSame(a, b);
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void keysWithDifferentIdsAreNotEqual() {
        final CustomRegistryData.DataKey<String> a = CustomRegistryData.createKey(ResourceLocation.parse("wover:one"));
        final CustomRegistryData.DataKey<String> b = CustomRegistryData.createKey(ResourceLocation.parse("wover:two"));
        // Different namespace, same path must also stay distinct.
        final CustomRegistryData.DataKey<String> c = CustomRegistryData.createKey(ResourceLocation.parse("other:one"));

        assertNotEquals(a, b);
        assertNotEquals(a, c);
    }

    @Test
    void equalityIgnoresTheGenericTypeParameter() {
        // Same id, but the two keys were declared with different data types. Equality keys only on `id`,
        // so these must still be interchangeable for storage/lookup.
        final ResourceLocation id = ResourceLocation.parse("wover:shared");
        final CustomRegistryData.DataKey<String> asString = CustomRegistryData.createKey(id);
        final CustomRegistryData.DataKey<Integer> asInteger = CustomRegistryData.createKey(id);

        assertEquals(asString, asInteger);
        assertEquals(asString.hashCode(), asInteger.hashCode());
    }

    @Test
    void keyIsNotEqualToUnrelatedObjectsOrNull() {
        final CustomRegistryData.DataKey<String> key = CustomRegistryData.createKey(ResourceLocation.parse("wover:x"));

        assertNotEquals(null, key);
        // The bare ResourceLocation is not a DataKey and must not compare equal to one.
        assertNotEquals(key, ResourceLocation.parse("wover:x"));
    }
}
