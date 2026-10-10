package de.ambertation.wover.testmod.entrypoint;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.tabs.api.CreativeTabs;
import de.ambertation.wover.testmod.block.CallOrderTestBlocks;
import de.ambertation.wover.testmod.block.TestBlockRegistry;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;

public class TestModWoverBlock implements ModInitializer {
    // ModCore for the TestMod. TestMod's do not share the wover namespace,
    // but (like other Mods that include Wover) have a unique one
    public static final ModCore C = ModCore.create("wover-block-testmod");
    public static final TagKey<Block> FLAMMABLE_PROBE_TAG = TagKey.create(
            Registries.BLOCK,
            C.id("flammable_probe")
    );

    @Override
    public void onInitialize() {
        // Regression guard for #37: another mod (Adorn) registering a flammable *tag* before our blocks
        // made FlammableBlockTrait query Fabric's registry, which resolves tags -> "Tags not bound" crash
        // during mod init. Must run before TestBlockRegistry is loaded.
        FlammableBlockRegistry.getDefaultInstance().add(FLAMMABLE_PROBE_TAG, 5, 20);
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