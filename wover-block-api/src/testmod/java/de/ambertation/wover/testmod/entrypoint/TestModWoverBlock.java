package de.ambertation.wover.testmod.entrypoint;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.tabs.api.CreativeTabs;
import de.ambertation.wover.testmod.block.CallOrderTestBlocks;
import de.ambertation.wover.testmod.block.TestBlockRegistry;

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