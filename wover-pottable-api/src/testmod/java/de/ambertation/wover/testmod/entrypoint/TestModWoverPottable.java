package de.ambertation.wover.testmod.entrypoint;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.pottable.api.PottablePlant;
import de.ambertation.wover.pottable.api.PottablePlantRegistry;
import de.ambertation.wover.pottable.api.PottableSoil;
import de.ambertation.wover.pottable.api.PottableSoilRegistry;
import de.ambertation.wover.testmod.block.TestPottableBlocks;

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

    /**
     * A pottable plant that is <em>not</em> contributed by any bootstrap, but loaded from a plain
     * datapack JSON file shipped in this testmod's {@code data/} directory. It restricts its soils to
     * the {@code minecraft:dirt} tag, so the optional {@code valid_soils} field is exercised too.
     *
     * @see #JSON_SOIL
     */
    public static final ResourceKey<PottablePlant> JSON_PLANT =
            PottablePlantRegistry.createKey(C.id("json-plant"));

    /** A soil loaded from a plain datapack JSON file, the counterpart of {@link #JSON_PLANT}. */
    public static final ResourceKey<PottableSoil> JSON_SOIL =
            PottableSoilRegistry.createKey(C.id("json-soil"));

    @Override
    public void onInitialize() {
        // Block registration has to happen during normal mod init, long before the datapack registries
        // bootstrap - TestPottableBlocks' traits are what the trait sweep in
        // TestModPottableDatapackRegistryEntrypoint later collects.
        TestPottableBlocks.ensureStaticallyLoaded();
    }
}
