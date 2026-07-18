package org.betterx.wover.ui.api;

import com.google.gson.Gson;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure-logic JUnit tests for the version-check wire model ({@link VersionChecker.Versions} and
 * {@link VersionChecker.ModVersion}). These need no Minecraft bootstrap and run via
 * {@code ./gradlew :wover-ui-api:test} using only Gson.
 * <p>
 * wover-ui-api delegates all actual layout/geometry to the external {@code wunderlib} library, so the
 * only deterministic, MC-free behaviour the module itself owns is how it (de)serializes the update
 * service's JSON. The service reports mods with the deliberately short field names {@code n} (mod id)
 * and {@code v} (version), inside a {@code {mc, loader, mods}} envelope. {@code VersionChecker.run()}
 * parses that response and, crucially, re-serializes it via {@code getAdapter(Versions.class).toJson(...)}
 * before caching the string in {@code CachedConfig}. If a version port renamed a field, dropped the
 * short JSON keys, or changed the {@code ModVersion#toString()} contract used in the update log, the
 * cached payload and update detection would silently break. These tests pin that contract.
 */
class VersionCheckerModelTest {
    private final Gson gson = new Gson();

    private static final String SERVICE_RESPONSE = "{\n" +
            "    \"mc\":\"1.21-rc.1\",\n" +
            "    \"loader\":\"fabric\",\n" +
            "    \"mods\":[\n" +
            "      {\"n\":\"bclib\", \"v\":\"21.0.0\"},\n" +
            "      {\"n\":\"wover\", \"v\":\"21.1.2\"}\n" +
            "    ]\n" +
            "  }";

    @Test
    void deserializesEnvelopeAndShortModFields() {
        VersionChecker.Versions versions = gson.fromJson(SERVICE_RESPONSE, VersionChecker.Versions.class);

        assertNotNull(versions);
        assertEquals("1.21-rc.1", versions.mc);
        assertEquals("fabric", versions.loader);
        assertNotNull(versions.mods);
        assertEquals(2, versions.mods.size());

        // The short field names are the whole contract: n -> mod id, v -> version string.
        VersionChecker.ModVersion first = versions.mods.get(0);
        assertEquals("bclib", first.n);
        assertEquals("21.0.0", first.v);
        assertEquals("wover", versions.mods.get(1).n);
        assertEquals("21.1.2", versions.mods.get(1).v);
    }

    @Test
    void modVersionToStringIsIdColonVersion() {
        // processVersions() logs each entry via this contract; keep it stable.
        VersionChecker.ModVersion mv = gson.fromJson("{\"n\":\"wover\",\"v\":\"21.1.2\"}", VersionChecker.ModVersion.class);
        assertEquals("wover:21.1.2", mv.toString());
    }

    @Test
    void cacheRoundTripPreservesData() {
        // Mirrors VersionChecker.run(): parse, re-serialize with the type adapter, cache, then re-parse.
        VersionChecker.Versions parsed = gson.fromJson(SERVICE_RESPONSE, VersionChecker.Versions.class);
        String cached = gson.getAdapter(VersionChecker.Versions.class).toJson(parsed);
        VersionChecker.Versions reparsed = gson.fromJson(cached, VersionChecker.Versions.class);

        assertEquals(parsed.mc, reparsed.mc);
        assertEquals(parsed.loader, reparsed.loader);
        assertEquals(parsed.mods.size(), reparsed.mods.size());
        for (int i = 0; i < parsed.mods.size(); i++) {
            assertEquals(parsed.mods.get(i).n, reparsed.mods.get(i).n);
            assertEquals(parsed.mods.get(i).v, reparsed.mods.get(i).v);
        }
        // The re-serialized payload must still use the short keys (that is what gets cached and re-read).
        assertTrue(cached.contains("\"n\":\"bclib\""), () -> "expected short mod-id key in " + cached);
        assertTrue(cached.contains("\"v\":\"21.0.0\""), () -> "expected short version key in " + cached);
    }

    @Test
    void absentModsListStaysNullForNullGuard() {
        // processVersions() explicitly null-guards json.mods; an envelope without a mods array must
        // therefore leave the field null rather than fabricating an empty list.
        VersionChecker.Versions versions = gson.fromJson("{\"mc\":\"1.21\",\"loader\":\"fabric\"}", VersionChecker.Versions.class);
        assertNotNull(versions);
        assertNull(versions.mods);
    }
}
