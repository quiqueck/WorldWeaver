package org.betterx.wover.testmod.structure.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;
import org.betterx.wover.testmod.entrypoint.WoverStructureTestMod;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;

public class StructureProvider extends WoverRegistryContentProvider<Structure> {
    /**
     * Creates a new instance of {@link WoverRegistryContentProvider}.
     *
     * @param modCore The ModCore instance of the Mod that is providing this instance.
     */
    public StructureProvider(
            ModCore modCore
    ) {
        super(modCore, "Test Structures", Registries.STRUCTURE);
    }

    @Override
    protected void bootstrap(BootstapContext<Structure> context) {
        WoverStructureTestMod.TEST_STRUCTURE
                .bootstrap(context)
                .adjustment(TerrainAdjustment.BEARD_BOX)
                .register();
    }
}
