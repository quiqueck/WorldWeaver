package org.betterx.wover.datagen.api.provider;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.levelgen.structure.Structure;

/**
 * A {@link WoverRegistryContentProvider} for {@link Structure}s.
 */
public abstract class WoverStructureProvider extends WoverRegistryContentProvider<Structure> {
    /**
     * Creates a new instance.
     *
     * @param modCore The ModCore instance of the Mod that is providing this instance.
     */
    public WoverStructureProvider(
            ModCore modCore
    ) {
        super(modCore, modCore.modId + " - Structures", Registries.STRUCTURE);
    }

    /**
     * Called, when the Elements of the Registry need to be created and registered.
     *
     * @param context The context to add the elements to.
     */
    @Override
    protected abstract void bootstrap(BootstapContext<Structure> context);
}
