package org.betterx.wover.testmod.biome.datagen;

import org.betterx.wover.biome.api.modification.BiomeModification;
import org.betterx.wover.biome.api.modification.BiomeModificationRegistry;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.NetherPlacements;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;

public class ModificationProvider extends WoverRegistryContentProvider<BiomeModification> {
    /**
     * Creates a new instance of {@link WoverRegistryContentProvider}.
     *
     * @param modCore The ModCore instance of the Mod that is providing this instance.
     */
    public ModificationProvider(
            ModCore modCore
    ) {
        super(modCore, "Biome Modifications", BiomeModificationRegistry.BIOME_MODIFICATION_REGISTRY);
    }

    @Override
    protected void bootstrap(BootstapContext<BiomeModification> context) {
        var features = context.lookup(Registries.PLACED_FEATURE);

        BiomeModification
                .build(context, modCore.id("test_features"))
                .inBiomes(Biomes.BEACH, Biomes.MEADOW)
                .addFeature(
                        GenerationStep.Decoration.SURFACE_STRUCTURES,
                        features.getOrThrow(NetherPlacements.SMALL_BASALT_COLUMNS)
                )
                .register();

        BiomeModification
                .build(context, modCore.id("test_tags"))
                .inNether()
                .addToTag(BiomeTags.HAS_SWAMP_HUT)
                .register();
    }
}
