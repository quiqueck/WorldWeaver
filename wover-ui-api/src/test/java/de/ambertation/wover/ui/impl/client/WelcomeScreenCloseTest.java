package de.ambertation.wover.ui.impl.client;

import de.ambertation.wover.config.api.client.ClientConfig;
import de.ambertation.wover.config.api.client.ClientConfigs;

import net.fabricmc.loader.api.FabricLoader;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression test for issue #31: closing the welcome screen with Escape (which ends in {@code Screen#onClose()},
 * not in the "Proceed" button) never set {@code did_present_welcome_screen}, so the screen came back on every
 * start. {@link WelcomeScreen#onClose()} now delegates to {@link WelcomeScreen#markPresented()}; constructing the
 * screen itself needs a running {@code Minecraft}, so this drives that method directly.
 * <p>
 * Runs in {@code build/unit-test-run} (see this module's build.gradle), never against run/config.
 */
class WelcomeScreenCloseTest {
    @Test
    void closingTheScreenPersistsThatItWasPresented() {
        assertTrue(
                FabricLoader.getInstance().getConfigDir().toAbsolutePath().toString().replace('\\', '/')
                            .contains("/build/unit-test-run/"),
                "refusing to write configs outside build/unit-test-run"
        );

        // no update check, so marking the screen as presented never starts a network request
        ClientConfigs.CLIENT.checkForNewVersions.set(false);
        ClientConfigs.CLIENT.didPresentWelcomeScreen.set(false);
        ClientConfigs.CLIENT.save(true);

        WelcomeScreen.markPresented();

        // a fresh instance reads the file, just like the next game start
        assertTrue(new ClientConfig().didPresentWelcomeScreen.get(), "welcome screen would be shown again");
        assertFalse(new ClientConfig().checkForNewVersions.get(), "choices made on the screen were not saved");
    }
}
