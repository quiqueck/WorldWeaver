package org.betterx.wover.datagen.api.provider;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.levelgen.structure.StructureSet;

/**
 * A {@link WoverRegistryContentProvider} for {@link StructureSet}s.
 */
public abstract class WoverStructureSetProvider extends WoverRegistryContentProvider<StructureSet> {
    /**
     * Creates a new instance.
     *
     * @param modCore The ModCore instance of the Mod that is providing this instance.
     */
    public WoverStructureSetProvider(
            ModCore modCore
    ) {
        super(modCore, modCore.modId + " - Structure Sets", Registries.STRUCTURE_SET);
    }

    /**
     * Called, when the Elements of the Registry need to be created and registered.
     *
     * @param context The context to add the elements to.
     */
    @Override
    protected abstract void bootstrap(BootstapContext<StructureSet> context);
}
