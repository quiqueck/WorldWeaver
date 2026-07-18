package org.betterx.wover.testmod.entrypoint;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.core.api.registry.DatapackRegistryEntrypoint;
import org.betterx.wover.pottable.api.PottablePlantRegistry;
import org.betterx.wover.pottable.api.PottableSoilRegistry;

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
                ctx -> PottablePlantRegistry.register(ctx, TestModWoverPottable.TEST_PLANT, Blocks.FERN)
        );

        DatapackRegistryBuilder.addBootstrap(
                PottableSoilRegistry.POTTABLE_SOIL_REGISTRY,
                ctx -> PottableSoilRegistry.register(ctx, TestModWoverPottable.TEST_SOIL, Blocks.PODZOL)
        );
    }
}
