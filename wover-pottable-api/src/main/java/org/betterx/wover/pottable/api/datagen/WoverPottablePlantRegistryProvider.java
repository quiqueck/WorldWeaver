package org.betterx.wover.pottable.api.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;
import org.betterx.wover.pottable.api.PottablePlant;
import org.betterx.wover.pottable.api.PottablePlantRegistry;
import org.betterx.wover.pottable.api.trait.PottablePlantBlockTrait;

import net.minecraft.data.worldgen.BootstrapContext;

/**
 * A generic, reusable datagen provider that serializes a {@link PottablePlant} entry
 * for every block of a mod that carries a {@link PottablePlantBlockTrait}.
 * <p>
 * Add {@code globalPack.addRegistryProvider(WoverPottablePlantRegistryProvider::new)}
 * to your datagen entrypoint - no custom provider class is needed.
 */
public class WoverPottablePlantRegistryProvider extends WoverRegistryContentProvider<PottablePlant> {
    public WoverPottablePlantRegistryProvider(ModCore modCore) {
        super(modCore, modCore.modId + " - Pottable Plants", PottablePlantRegistry.POTTABLE_PLANT_REGISTRY);
    }

    @Override
    protected void bootstrap(BootstrapContext<PottablePlant> context) {
        PottablePlantBlockTrait.bootstrapPottablePlants(modCore, context, (key, block) -> true);
    }
}
