package org.betterx.wover.testmod.entrypoint;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.structure.api.StructureKey;
import org.betterx.wover.structure.api.StructureKeys;
import org.betterx.wover.structure.api.pools.StructurePoolKey;
import org.betterx.wover.structure.api.processors.StructureProcessorKey;
import org.betterx.wover.structure.api.sets.StructureSetKey;
import org.betterx.wover.tag.api.TagManager;
import org.betterx.wover.testmod.structure.TestStructure;

import net.minecraft.world.level.levelgen.GenerationStep;

import net.fabricmc.api.ModInitializer;

public class TestModWoverStructure implements ModInitializer {
    // ModCore for the TestMod. TestMod's do not share the wover namespace,
    // but (like other Mods that include Wover) have a unique one
    public static final ModCore C = ModCore.create("wover-structure-testmod");

    public static final StructureKey.Simple<TestStructure> TEST_STRUCTURE
            = StructureKeys.structure(C.id("test_structure"), TestStructure::new)
                           .biomeTag(TagManager.BIOMES.makeStructureTag(C, "test_structure"))
                           .step(GenerationStep.Decoration.VEGETAL_DECORATION);

    public static final StructureKey.Jigsaw JIGSAW_STRUCTURE
            = StructureKeys.jigsaw(C.id("jigsaw_structure"))
                           .biomeTag(TEST_STRUCTURE.biomeTag())
                           .step(GenerationStep.Decoration.SURFACE_STRUCTURES);

    public static final StructureKey.RandomNbt RND_STRUCTURE
            = StructureKeys.randomNbt(C.id("rnd_structure"))
                           .step(GenerationStep.Decoration.SURFACE_STRUCTURES);

    public static final StructureSetKey TEST_STRUCTURE_SET
            = StructureKeys.set(C.id("test_structure_set"));

    public static final StructurePoolKey TEST_STRUCTURE_POOL_START
            = StructureKeys.pool(C.id("start"));
    public static final StructurePoolKey TEST_STRUCTURE_POOL_HOUSE
            = StructureKeys.pool(C.id("house"));
    public static final StructurePoolKey TEST_STRUCTURE_POOL_TERMINAL
            = StructureKeys.pool(C.id("terminal"));
    public static final StructureProcessorKey TEST_STRUCTURE_PROCESSOR
            = StructureKeys.processor(C.id("test_structure_processor"));


    @Override
    public void onInitialize() {

    }
}