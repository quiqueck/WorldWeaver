package org.betterx.wover.testmod.entrypoint;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.structure.api.StructureKey;
import org.betterx.wover.structure.api.StructureManager;
import org.betterx.wover.structure.api.builders.StructureBuilder;
import org.betterx.wover.structure.api.sets.StructureSetKey;
import org.betterx.wover.structure.api.sets.StructureSetManager;
import org.betterx.wover.tag.api.TagManager;
import org.betterx.wover.testmod.structure.TestStructure;

import net.minecraft.world.level.levelgen.GenerationStep;

import net.fabricmc.api.ModInitializer;

public class WoverStructureTestMod implements ModInitializer {
    // ModCore for the TestMod. TestMod's do not share the wover namespace,
    // but (like other Mods that include Wover) have a unique one
    public static final ModCore C = ModCore.create("wover-structure-testmod");

    public static final StructureKey<TestStructure, StructureBuilder<TestStructure>> TEST_STRUCTURE
            = StructureManager.structure(C.id("test_structure"), TestStructure::new)
                              .setBiomeTag(TagManager.BIOMES.makeStructureTag(C, "test_structure"))
                              .setDecoration(GenerationStep.Decoration.VEGETAL_DECORATION);

    public static final StructureSetKey TEST_STRUCTURE_SET = StructureSetManager.createKey(C.id("test_structure_set"));

    @Override
    public void onInitialize() {

    }
}