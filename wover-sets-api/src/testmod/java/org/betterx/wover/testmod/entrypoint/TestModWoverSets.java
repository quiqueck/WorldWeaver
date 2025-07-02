package org.betterx.wover.testmod.entrypoint;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.sets.api.blocks.SlotType;
import org.betterx.wover.tabs.api.CreativeTabs;
import org.betterx.wover.testmod.sets.TestEquipmentSet;
import org.betterx.wover.testmod.sets.TestWoodSet;

import net.fabricmc.api.ModInitializer;

public class TestModWoverSets implements ModInitializer {
    // ModCore for the TestMod. TestMod's do not share the wover namespace,
    // but (like other Mods that include Wover) have a unique one
    public static final ModCore C = ModCore.create("wover-sets-testmod");

    @Override
    public void onInitialize() {
        TestEquipmentSet.ensureStaticInit();
        var woodBlockSet = new TestWoodSet().buildAndRegister();


//        var boat = ItemRegistry
//                .forMod(C)
//                .defineBoatItem("wooden_boat")
//                .addTrait(ClientBlockTraits.BOAT_RENDERER.withDefault())
//                .buildAndRegisterBoat();

        CreativeTabs.start(C)
                    .createTab("all")
                    .setIcon(woodBlockSet.getBlock(SlotType.LOG))
                    .buildAndAdd()
                    .processRegistries()
                    .registerAllTabs();
    }
}