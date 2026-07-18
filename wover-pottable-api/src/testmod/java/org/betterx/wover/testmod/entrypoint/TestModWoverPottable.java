package org.betterx.wover.testmod.entrypoint;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.pottable.api.PottablePlant;
import org.betterx.wover.pottable.api.PottablePlantRegistry;
import org.betterx.wover.pottable.api.PottableSoil;
import org.betterx.wover.pottable.api.PottableSoilRegistry;

import net.minecraft.resources.ResourceKey;

import net.fabricmc.api.ModInitializer;

/**
 * Main entrypoint of the pottable-api testmod.
 * <p>
 * The class itself only owns the shared {@link ModCore} and the {@link ResourceKey}s of the test
 * entries. The entries themselves are contributed at datapack-registry bootstrap time by
 * {@link TestModPottableDatapackRegistryEntrypoint}, and asserted at runtime by
 * {@code PottableGameTest}. Keeping the keys here lets the entrypoint and the GameTest agree on the
 * exact same ids without duplicating string literals.
 */
public class TestModWoverPottable implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-pottable-testmod");

    /** A pottable plant registered on any soil (empty {@code validSoils}). */
    public static final ResourceKey<PottablePlant> TEST_PLANT =
            PottablePlantRegistry.createKey(C.id("test-plant"));

    /** A soil the {@link #TEST_PLANT} may be potted on. */
    public static final ResourceKey<PottableSoil> TEST_SOIL =
            PottableSoilRegistry.createKey(C.id("test-soil"));

    @Override
    public void onInitialize() {
    }
}
