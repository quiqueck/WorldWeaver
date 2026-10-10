package de.ambertation.wover.config.api.client;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for issue #31: the welcome screen came back on every client start. The decision is made by
 * {@code VersionCheckerClient} from {@link ClientConfig#didPresentWelcomeScreen}, so the flag must survive a
 * save/reload of {@code config/wover/client.json}, and a hand edit of that file must be honoured. (These all
 * held already; the actual bug was the screen not setting the flag when closed with Escape, see
 * {@code WelcomeScreenCloseTest}.)
 * <p>
 * Each test builds a fresh {@link ClientConfig} (which is what happens on the next game start) instead of using
 * the {@link ClientConfigs#CLIENT} singleton. The {@code test} task runs in {@code build/unit-test-run} (see
 * this module's build.gradle), so the config dir is a throw-away folder; {@link #onlyInThrowAwayDir()} refuses
 * to run anywhere else.
 */
class ClientConfigRoundTripTest {
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().toAbsolutePath().normalize();
    private static final Path FILE = CONFIG_DIR.resolve("wover").resolve("client.json");

    @BeforeAll
    static void onlyInThrowAwayDir() {
        assertTrue(
                CONFIG_DIR.toString().replace('\\', '/').contains("/build/unit-test-run/"),
                "refusing to write configs outside build/unit-test-run, config dir is " + CONFIG_DIR
        );
    }

    @BeforeEach
    void clear() throws IOException {
        Files.deleteIfExists(FILE);
    }

    @Test
    void freshConfigHasNotPresentedWelcomeScreen() {
        assertFalse(new ClientConfig().didPresentWelcomeScreen.get());
    }

    @Test
    void welcomeScreenFlagSurvivesSaveAndReload() {
        ClientConfig firstStart = new ClientConfig();
        // what the welcome screen's "Proceed" button does
        firstStart.didPresentWelcomeScreen.set(true);
        firstStart.checkForNewVersions.set(false);
        firstStart.save();

        assertTrue(Files.exists(FILE), "client config was not written to " + FILE);

        ClientConfig secondStart = new ClientConfig();
        assertTrue(secondStart.didPresentWelcomeScreen.get(), () -> "flag lost on reload, file was:\n" + read());
        assertFalse(secondStart.checkForNewVersions.get());
    }

    @Test
    void handEditedFlagIsHonoured() throws IOException {
        // the layout written by the game, edited by hand
        Files.createDirectories(FILE.getParent());
        Files.writeString(
                FILE,
                "{\n" +
                        "  \"internal\": {\n" +
                        "    \"did_present_welcome_screen\": true\n" +
                        "  }\n" +
                        "}",
                StandardCharsets.UTF_8
        );

        assertTrue(new ClientConfig().didPresentWelcomeScreen.get());
    }

    @Test
    void writtenFileUsesDocumentedKey() {
        ClientConfig cfg = new ClientConfig();
        cfg.didPresentWelcomeScreen.set(true);
        cfg.save();

        String json = read();
        assertTrue(json.contains("\"internal\""), json);
        assertTrue(json.contains("\"did_present_welcome_screen\": true"), json);
    }

    private static String read() {
        try {
            return Files.readString(FILE, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
