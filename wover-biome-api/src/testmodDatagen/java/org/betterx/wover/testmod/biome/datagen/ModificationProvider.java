package org.betterx.wover.testmod.biome.datagen;

import org.betterx.wover.biome.api.modification.BiomeModification;
import org.betterx.wover.biome.api.modification.BiomeModificationRegistry;
import org.betterx.wover.biome.api.modification.predicates.IsBiome;
import org.betterx.wover.biome.api.modification.predicates.Or;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.NetherPlacements;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;

import java.util.List;

public class ModificationProvider extends WoverRegistryContentProvider<BiomeModification> {
    /**
     * Creates a new instance of {@link WoverRegistryContentProvider}.
     *
     * @param modCore     The ModCore instance of the Mod that is providing this instance.
     * @param title       The title of the provider. Mainly used for logging.
     * @param registryKey The Key to the Registry.
     */
    public ModificationProvider(
            ModCore modCore
    ) {
        super(modCore, "Biome Modifications", BiomeModificationRegistry.BIOME_MODIFICATION_REGISTRY);
    }

    @Override
    protected void bootstrap(BootstapContext<BiomeModification> context) {
        var features = context.lookup(Registries.PLACED_FEATURE);
        BiomeModification modification = new BiomeModification(
                new Or(List.of(new IsBiome(Biomes.BEACH), new IsBiome(Biomes.PLAINS)))
        ).addFeature(
                GenerationStep.Decoration.VEGETAL_DECORATION,
                features.getOrThrow(NetherPlacements.SMALL_BASALT_COLUMNS)
        );

        context.register(BiomeModificationRegistry.createKey(modCore.id("test_modification")), modification);
    }
}
