package org.betterx.wover.testmod.structure.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.multi.WoverStructureProvider;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;
import org.betterx.wover.testmod.entrypoint.WoverStructureTestMod;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

public class StructureProvider extends WoverStructureProvider {
    private final ModCore C = WoverStructureTestMod.C;

    /**
     * Creates a new instance of {@link WoverStructureProvider}.
     *
     * @param modCore The {@link ModCore} of the Mod.
     */
    public StructureProvider(ModCore modCore) {
        super(modCore);
    }

    @Override
    protected void bootstrapSturctures(BootstapContext<Structure> context) {
        WoverStructureTestMod.TEST_STRUCTURE
                .bootstrap(context)
                .adjustment(TerrainAdjustment.BEARD_BOX)
                .register();

        WoverStructureTestMod.JIGSAW_STRUCTURE
                .bootstrap(context)
                .startPool(WoverStructureTestMod.TEST_STRUCTURE_POOL_START)
                .maxDepth(5)
                .projectStartToHeightmap(Heightmap.Types.MOTION_BLOCKING)
                .adjustment(TerrainAdjustment.BEARD_BOX)
                .register();
    }

    @Override
    protected void bootstrapSets(BootstapContext<StructureSet> context) {
        WoverStructureTestMod.TEST_STRUCTURE_SET
                .bootstrap(context)
                .addStructure(WoverStructureTestMod.TEST_STRUCTURE)
                .addStructure(WoverStructureTestMod.JIGSAW_STRUCTURE)
                .randomPlacement()
                .spreadType(RandomSpreadType.TRIANGULAR)
                .setPlacement()
                .register();
    }

    @Override
    protected void bootstrapPools(BootstapContext<StructureTemplatePool> context) {
        WoverStructureTestMod.TEST_STRUCTURE_POOL_START
                .bootstrap(context)
                .terminator(WoverStructureTestMod.TEST_STRUCTURE_POOL_TERMINAL)
                .startSingle(C.id("street"))
                .endElement()
                .projection(StructureTemplatePool.Projection.TERRAIN_MATCHING)
                .register();

        WoverStructureTestMod.TEST_STRUCTURE_POOL_HOUSE
                .bootstrap(context)
                .startSingle(C.id("house"))
                .endElement()
                .register();

        WoverStructureTestMod.TEST_STRUCTURE_POOL_TERMINAL
                .bootstrap(context)
                .startSingle(C.id("terminator"))
                .processor(WoverStructureTestMod.TEST_STRUCTURE_PROCESSOR)
                .endElement()
                .register();
    }

    @Override
    protected void bootstrapPorcessors(BootstapContext<StructureProcessorList> context) {
        WoverStructureTestMod.TEST_STRUCTURE_PROCESSOR
                .bootstrap(context)

                .startRule()
                .startProcessor()
                .inputPredicateRandom(Blocks.RED_GLAZED_TERRACOTTA, 0.33f)
                .outputState(Blocks.RED_CONCRETE)
                .endProcessor()
                .endRule()

                .register();
    }

    @Override
    protected void prepareBiomeTags(TagBootstrapContext<Biome> context) {
        context.add(WoverStructureTestMod.TEST_STRUCTURE.getBiomeTag(),
                Biomes.SAVANNA, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.NETHER_WASTES
        );
    }
}
