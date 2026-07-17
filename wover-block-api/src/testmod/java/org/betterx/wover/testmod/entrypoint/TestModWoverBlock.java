package org.betterx.wover.testmod.entrypoint;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.tabs.api.CreativeTabs;
import org.betterx.wover.testmod.block.CallOrderTestBlocks;
import org.betterx.wover.testmod.block.TestBlockRegistry;

import net.minecraft.world.item.Items;

import net.fabricmc.api.ModInitializer;

public class TestModWoverBlock implements ModInitializer {
    // ModCore for the TestMod. TestMod's do not share the wover namespace,
    // but (like other Mods that include Wover) have a unique one
    public static final ModCore C = ModCore.create("wover-block-testmod");

    @Override
    public void onInitialize() {
        TestBlockRegistry.ensureStaticallyLoaded();
        // Register the call-order precedence probe blocks (their own namespace; read by CallOrderGameTest).
        CallOrderTestBlocks.ensureStaticallyLoaded();

        CreativeTabs.start(C)
                    .createBlockOnlyTab(Items.OAK_LOG)
                    .buildAndAdd()
                    .processRegistries()
                    .registerAllTabs();
    }
}