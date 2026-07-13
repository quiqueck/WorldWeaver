package org.betterx.wover.pottable.api.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;
import org.betterx.wover.pottable.api.PottableSoil;
import org.betterx.wover.pottable.api.PottableSoilRegistry;
import org.betterx.wover.pottable.api.trait.PottableSoilBlockTrait;

import net.minecraft.data.worldgen.BootstrapContext;

/**
 * A generic, reusable datagen provider that serializes a {@link PottableSoil} entry
 * for every block of a mod that carries a {@link PottableSoilBlockTrait}.
 * <p>
 * Add {@code globalPack.addRegistryProvider(WoverPottableSoilRegistryProvider::new)}
 * to your datagen entrypoint - no custom provider class is needed.
 */
public class WoverPottableSoilRegistryProvider extends WoverRegistryContentProvider<PottableSoil> {
    public WoverPottableSoilRegistryProvider(ModCore modCore) {
        super(modCore, modCore.modId + " - Pottable Soils", PottableSoilRegistry.POTTABLE_SOIL_REGISTRY);
    }

    @Override
    protected void bootstrap(BootstrapContext<PottableSoil> context) {
        PottableSoilBlockTrait.bootstrapPottableSoils(modCore, context, (key, block) -> true);
    }
}
