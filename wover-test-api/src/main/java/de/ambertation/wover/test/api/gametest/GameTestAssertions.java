package de.ambertation.wover.test.api.gametest;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;

import java.util.List;

/** Small shared assertion helpers, factored out of the pattern every sweep below already follows. */
public final class GameTestAssertions {
    private GameTestAssertions() {}

    /**
     * Throws a {@code GameTestAssertException} listing every failure if {@code failures} is non-empty;
     * does nothing otherwise. Callers still need their own {@code helper.succeed()} on the way out.
     */
    public static void failIfAny(GameTestHelper helper, String headline, List<String> failures) {
        if (!failures.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    headline + ":\n - " + String.join("\n - ", failures)
            ));
        }
    }
}
