package de.ambertation.wover.pottable.api.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverRegistryContentProvider;
import de.ambertation.wover.pottable.api.PottableSoil;
import de.ambertation.wover.pottable.api.PottableSoilRegistry;
import de.ambertation.wover.pottable.api.trait.PottableSoilBlockTrait;

import net.minecraft.data.worldgen.BootstrapContext;

/**
 * A generic, reusable datagen provider that serializes a {@link PottableSoil} entry
 * for every block of a mod that carries a {@link PottableSoilBlockTrait}.
 * <p>
 * Add {@code globalPack.addRegistryProvider(WoverPottableSoilRegistryProvider::new)}
 * to your datagen entrypoint - no custom provider class is needed.
 */
public class WoverPottableSoilRegistryProvider extends WoverRegistryContentProvider<PottableSoil> {
    /**
     * Creates a new instance of {@link WoverPottableSoilRegistryProvider}.
     *
     * @param modCore The ModCore instance of the Mod that is providing this instance.
     */
    public WoverPottableSoilRegistryProvider(ModCore modCore) {
        super(modCore, modCore.modId + " - Pottable Soils", PottableSoilRegistry.POTTABLE_SOIL_REGISTRY);
    }

    /**
     * Registers a {@link PottableSoil} for every block of this provider's mod that carries a
     * {@link PottableSoilBlockTrait}.
     *
     * @param context The bootstrap context used to register the soils.
     */
    @Override
    protected void bootstrap(BootstrapContext<PottableSoil> context) {
        PottableSoilBlockTrait.bootstrapPottableSoils(modCore, context, (key, block) -> true);
    }
}
