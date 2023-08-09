package org.betterx.wover.testmod.structure.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;
import org.betterx.wover.testmod.entrypoint.WoverStructureTestMod;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;

public class StructureSetProvider extends WoverRegistryContentProvider<StructureSet> {
    /**
     * Creates a new instance of {@link WoverRegistryContentProvider}.
     *
     * @param modCore The ModCore instance of the Mod that is providing this instance.
     */
    public StructureSetProvider(
            ModCore modCore
    ) {
        super(modCore, "Test StructureSets", Registries.STRUCTURE_SET);
    }

    @Override
    protected void bootstrap(BootstapContext<StructureSet> context) {
        WoverStructureTestMod.TEST_STRUCTURE_SET
                .bootstrap(context)
                .addStructure(WoverStructureTestMod.TEST_STRUCTURE)
                .randomPlacement()
                .spreadType(RandomSpreadType.TRIANGULAR)
                .setPlacement()
                .register();
    }
}
