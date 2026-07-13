package org.betterx.wover.ui.api;

import org.betterx.wover.config.api.client.ClientConfigs;
import org.betterx.wover.config.impl.CachedConfig;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverUi;

import net.fabricmc.loader.api.FabricLoader;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

/**
 * Checks a WorldWeaver web service for newer versions of the currently installed WorldWeaver-based mods.
 * <p>
 * The check is throttled to run at most once every {@link #WAIT_FOR_DAYS} days (the result of the last check is
 * cached in {@code CachedConfig} and reused until it expires) and only ever runs on the client (see
 * {@link #startCheck(boolean)}). Mods that should be considered by the checker first need to be registered with
 * {@link #registerMod(ModCore)}; any mod contained in the server response that was not registered is ignored.
 * <p>
 * This class is not meant to be instantiated by mod developers directly &mdash; use the static helper methods
 * instead. It also serves as the base class for the client-only {@code VersionCheckerClient}, which drives the
 * actual welcome/update screens.
 */
public class VersionChecker implements Runnable {
    private static final boolean TEST_UPDATE_SCREEN = false && ModCore.isDevEnvironment();

    /**
     * Callback used by {@link #forEachUpdate(UpdateInfoProvider)} to report a single mod update.
     */
    @FunctionalInterface
    public interface UpdateInfoProvider {
        /**
         * Called once for every mod for which a newer version was found.
         *
         * @param modID          the id of the mod that has an update available
         * @param currentVersion the version that is currently installed
         * @param newVersion     the version that was announced by the update service
         */
        void send(String modID, String currentVersion, String newVersion);
    }

    private static final List<String> KNOWN_MODS = new LinkedList<>();
    private static final List<ModVersion> NEW_VERSIONS = new LinkedList<>();

    /**
     * The version of a single mod, as reported by the update service.
     * <p>
     * Instances are deserialized directly from the JSON response, so the field names ({@code n} for the mod id
     * and {@code v} for the version string) are intentionally short.
     */
    public static class ModVersion {
        /** The mod id, as used by the Fabric Loader (i.e. {@code fabric.mod.json}'s {@code id}). */
        String n;
        /** The version string reported by the update service. */
        String v;

        @Override
        public String toString() {
            return n + ":" + v;
        }
    }

    /**
     * The response of the version-check web service, deserialized directly from JSON.
     */
    public static class Versions {
        /** The Minecraft version this response was generated for. */
        String mc;
        /** The mod loader this response was generated for (e.g. {@code "fabric"}). */
        String loader;
        /** The list of known mod versions for the requested Minecraft version/loader combination. */
        List<ModVersion> mods;

        @Override
        public String toString() {
            return "Versions{" +
                    "mc='" + mc + '\'' +
                    ", loader='" + loader + '\'' +
                    ", mods=" + mods +
                    '}';
        }
    }

    /** Minimum number of days that need to pass before the version check is run again. */
    public static final int WAIT_FOR_DAYS = 5;
    private static final String BASE_URL = "https://wunderreich.ambertation.de/api/v1/versions/";
    private static Thread versionChecker;

    /**
     * Starts (or re-uses the cached result of) the version check, if the necessary conditions are met.
     * <p>
     * The check only runs when {@code isClient} is {@code true}, the version checker was not already started,
     * the welcome screen was already presented to the user and the
     * {@code check_for_new_versions} client config option is enabled. If the last check is not yet stale (see
     * {@link #WAIT_FOR_DAYS}), the cached response is reused instead of contacting the update service again.
     *
     * @param isClient {@code true} when called from a client instance, {@code false} on a dedicated server
     */
    public static void startCheck(boolean isClient) {
        if (versionChecker == null && isClient) {
            final VersionChecker checker = new VersionChecker();

            if (ClientConfigs.CLIENT.checkForNewVersions.get() && ClientConfigs.CLIENT.didPresentWelcomeScreen.get()) {
                if (TEST_UPDATE_SCREEN) {
                    Gson gson = new Gson();
                    String fakeVersions = "{\n" +
                            "    \"mc\":\"1.21-rc.1\",\n" +
                            "    \"loader\":\"fabric\",\n" +
                            "    \"mods\":[\n" +
                            "      {\"n\":\"bclib\", \"v\":\"21.0.0\"},\n" +
                            "      {\"n\":\"wover\", \"v\":\"21.0.0\"},\n" +
                            "      {\"n\":\"betterend\", \"v\":\"21.0.0\"},\n" +
                            "      {\"n\":\"betternether\", \"v\":\"21.0.0\"},\n" +
                            "      {\"n\":\"wunderreich\", \"v\":\"21.0.0\"}\n" +
                            "    ]\n" +
                            "  }";
                    Versions json = gson.fromJson(fakeVersions, Versions.class);
                    CachedConfig.INSTANCE.setLastVersionJson(fakeVersions);
                    CachedConfig.INSTANCE.save();
                    checker.processVersions(json);
                } else if (checker.needRecheck()) {
                    versionChecker = new Thread(checker);
                    versionChecker.start();
                } else {
                    String str = CachedConfig.INSTANCE.lastVersionJson();
                    if (str != null && str.trim().length() > 0) {
                        Gson gson = new Gson();
                        Versions json = gson.fromJson(str, Versions.class);
                        checker.processVersions(json);
                    }
                }
            }
        }
    }

    /**
     * Registers a mod so it is considered by the version check.
     * <p>
     * Only mods registered through this method are looked up in the update service's response; any other mod
     * contained in the response is ignored.
     *
     * @param modCore the {@link ModCore} instance of the mod to register
     */
    public static void registerMod(ModCore modCore) {
        KNOWN_MODS.add(modCore.namespace);
    }

    private static void registerMod(String modId) {
        KNOWN_MODS.add(modId);
    }

    /**
     * Whether enough time has passed since the last check that a new one is due.
     *
     * @return {@code true} if the last check is older than {@link #WAIT_FOR_DAYS} days
     */
    boolean needRecheck() {
        Instant lastCheck = CachedConfig.INSTANCE.lastCheckDate().plus(WAIT_FOR_DAYS, ChronoUnit.DAYS);
        Instant now = Instant.now();


        return now.isAfter(lastCheck);
    }

    /**
     * Contacts the update service, parses its response and caches it.
     * <p>
     * This method performs blocking network I/O and is meant to run on the background {@link #versionChecker}
     * thread created by {@link #startCheck(boolean)}, not on the main thread.
     */
    @Override
    public void run() {
        Gson gson = new Gson();

        ModCore modCore = ModCore.create("minecraft");
        String minecraftVersion = modCore.getModVersion().toString().replace(".", "_");
        LibWoverUi.C.LOG.info("Check Versions for minecraft=" + minecraftVersion);

        try {
            String fileName = "mc_fabric_" + URLEncoder.encode(
                    minecraftVersion,
                    StandardCharsets.ISO_8859_1.toString()
            ) + ".json";

            URL url = new URL(BASE_URL + fileName);
            try (InputStreamReader reader = new InputStreamReader(url.openStream())) {
                Versions json = gson.fromJson(reader, Versions.class);
                String str = gson.getAdapter(Versions.class).toJson(json);
                CachedConfig.INSTANCE.setLastVersionJson(str);
                CachedConfig.INSTANCE.setLastCheckDate();
                CachedConfig.INSTANCE.save();

                processVersions(json);
            }
        } catch (UnsupportedEncodingException e) {
            LibWoverUi.C.LOG.error("Failed to encode URL during VersionCheck", e);
            return;
        } catch (MalformedURLException e) {
            LibWoverUi.C.LOG.error("Invalid URL during VersionCheck", e);
            return;
        } catch (IOException e) {
            LibWoverUi.C.LOG.error("I/O Error during VersionCheck", e);
            return;
        }

    }

    private void processVersions(Versions json) {
        if (json != null) {
            LibWoverUi.C.LOG.info("Received Version Info for minecraft=" + json.mc + ", loader=" + json.loader);
            if (json.mods != null) {
                for (ModVersion mod : json.mods) {
                    if (!KNOWN_MODS.contains(mod.n)) {
                        if (FabricLoader.getInstance().getModContainer(mod.n).isPresent())
                            registerMod(mod.n);
                    }
                    if (mod.n != null && mod.v != null && KNOWN_MODS.contains(mod.n)) {
                        final ModCore modCore = ModCore.create(mod.n);
                        var installedVersion = modCore.getModVersion();


                        boolean isNew = TEST_UPDATE_SCREEN || installedVersion.isLessThan(mod.v)
                                && !installedVersion.equals("0.0.0");
                        LibWoverUi.C.LOG.info(" - " + mod.n + ":" + mod.v + (isNew ? " (update available)" : ""));
                        if (isNew)
                            NEW_VERSIONS.add(mod);
                    }
                }
            }
        } else {
            LibWoverUi.C.LOG.warn("No valid Version Info");
        }
    }

    /**
     * Whether the last check found any updates.
     *
     * @return {@code true} if no updates were found (or no check has completed yet)
     */
    public static boolean isEmpty() {
        return NEW_VERSIONS.isEmpty();
    }

    /**
     * Invokes {@code consumer} once for every mod for which the last check found a newer version.
     *
     * @param consumer the callback to invoke for each available update
     */
    public static void forEachUpdate(UpdateInfoProvider consumer) {
        for (ModVersion v : NEW_VERSIONS) {
            final ModCore modCore = ModCore.create(v.n);
            String currrent = modCore.getModVersion().toString();
            consumer.send(v.n, currrent, v.v);
        }
    }
}
