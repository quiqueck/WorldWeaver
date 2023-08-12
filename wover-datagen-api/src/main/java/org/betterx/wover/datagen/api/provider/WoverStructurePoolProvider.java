package org.betterx.wover.datagen.api.provider;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

/**
 * A {@link WoverRegistryContentProvider} for {@link StructureTemplatePool}s.
 */
public abstract class WoverStructurePoolProvider extends WoverRegistryContentProvider<StructureTemplatePool> {
    /**
     * Creates a new instance.
     *
     * @param modCore The ModCore instance of the Mod that is providing this instance.
     */
    public WoverStructurePoolProvider(
            ModCore modCore
    ) {
        super(modCore, modCore.modId + " - Structure Pools", Registries.TEMPLATE_POOL);
    }

    @Override
    protected abstract void bootstrap(BootstapContext<StructureTemplatePool> context);
}
