package org.betterx.wover.testmod.entrypoint;

import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.testmod.sets.TestEquipmentSet;
import org.betterx.wover.testmod.sets.TestWoodSet;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.fabricmc.api.ModInitializer;

public class TestModWoverSets implements ModInitializer {
    // ModCore for the TestMod. TestMod's do not share the wover namespace,
    // but (like other Mods that include Wover) have a unique one
    public static final ModCore C = ModCore.create("wover-sets-testmod");

    @Override
    public void onInitialize() {
        TestEquipmentSet.ensureStaticInit();
        var woodBlockSet = new TestWoodSet().register();

        final Block bl = BlockRegistry.forMod(C)
                                      .register(
                                              "foo",
                                              new Block(BlockBehaviour.Properties.of().setId(ResourceKey.create(
                                                      Registries.BLOCK,
                                                      C.id("foo")
                                              ))),
                                              new TagKey[0],
                                              new TagKey[0]
                                      );

        C.LOG.info(bl + ", " + bl.asItem());
    }
}