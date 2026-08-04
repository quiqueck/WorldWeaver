package de.ambertation.wover.testmod.entrypoint;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.core.api.registry.DatapackRegistryBuilder;
import de.ambertation.wover.core.api.registry.DatapackRegistryEntrypoint;
import de.ambertation.wover.pottable.api.PottablePlantRegistry;
import de.ambertation.wover.pottable.api.PottableSoilRegistry;
import de.ambertation.wover.pottable.api.trait.PottablePlantBlockTrait;
import de.ambertation.wover.pottable.api.trait.PottableSoilBlockTrait;

import net.minecraft.world.level.block.Blocks;

/**
 * Contributes the test entries into the two datapack-driven registries the pottable-api owns.
 * <p>
 * This is the runtime equivalent of a consumer mod that marks its own blocks pottable: it uses
 * {@link DatapackRegistryBuilder#addBootstrap} to append a {@code test-plant} to
 * {@link PottablePlantRegistry#POTTABLE_PLANT_REGISTRY} and a {@code test-soil} to
 * {@link PottableSoilRegistry#POTTABLE_SOIL_REGISTRY}. The production
 * {@code PottableDatapackRegistryEntrypoint} creates the registries (with their codecs); these
 * bootstraps only add elements, so registration order between the two entrypoints does not matter.
 * <p>
 * Guarded by {@link ModCore#isDatagen()} so the injected entries never leak into datagen output.
 */
public class TestModPottableDatapackRegistryEntrypoint implements DatapackRegistryEntrypoint {
    @Override
    public void registerDatapackRegistries() {
        if (ModCore.isDatagen()) return;

        DatapackRegistryBuilder.addBootstrap(
                PottablePlantRegistry.POTTABLE_PLANT_REGISTRY,
                ctx -> {
                    PottablePlantRegistry.register(ctx, TestModWoverPottable.TEST_PLANT, Blocks.FERN);

                    // The trait-driven sweep: exactly what WoverPottablePlantRegistryProvider does at
                    // datagen time, but exercised at runtime so a GameTest can read the result back. It
                    // walks every block this testmod registered and turns each PottablePlantBlockTrait
                    // into a registry entry keyed by the block's own id.
                    PottablePlantBlockTrait.bootstrapPottablePlants(
                            TestModWoverPottable.C,
                            ctx,
                            (key, block) -> true
                    );
                }
        );

        DatapackRegistryBuilder.addBootstrap(
                PottableSoilRegistry.POTTABLE_SOIL_REGISTRY,
                ctx -> {
                    PottableSoilRegistry.register(ctx, TestModWoverPottable.TEST_SOIL, Blocks.PODZOL);

                    PottableSoilBlockTrait.bootstrapPottableSoils(
                            TestModWoverPottable.C,
                            ctx,
                            (key, block) -> true
                    );
                }
        );
    }
}
