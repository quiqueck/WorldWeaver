package de.ambertation.wover.pottable.api.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverRegistryContentProvider;
import de.ambertation.wover.pottable.api.PottablePlant;
import de.ambertation.wover.pottable.api.PottablePlantRegistry;
import de.ambertation.wover.pottable.api.trait.PottablePlantBlockTrait;

import net.minecraft.data.worldgen.BootstrapContext;

/**
 * A generic, reusable datagen provider that serializes a {@link PottablePlant} entry
 * for every block of a mod that carries a {@link PottablePlantBlockTrait}.
 * <p>
 * Add {@code globalPack.addRegistryProvider(WoverPottablePlantRegistryProvider::new)}
 * to your datagen entrypoint - no custom provider class is needed.
 */
public class WoverPottablePlantRegistryProvider extends WoverRegistryContentProvider<PottablePlant> {
    /**
     * Creates a new instance of {@link WoverPottablePlantRegistryProvider}.
     *
     * @param modCore The ModCore instance of the Mod that is providing this instance.
     */
    public WoverPottablePlantRegistryProvider(ModCore modCore) {
        super(modCore, modCore.modId + " - Pottable Plants", PottablePlantRegistry.POTTABLE_PLANT_REGISTRY);
    }

    /**
     * Registers a {@link PottablePlant} for every block of this provider's mod that carries a
     * {@link PottablePlantBlockTrait}.
     *
     * @param context The bootstrap context used to register the plants.
     */
    @Override
    protected void bootstrap(BootstrapContext<PottablePlant> context) {
        PottablePlantBlockTrait.bootstrapPottablePlants(modCore, context, (key, block) -> true);
    }
}
