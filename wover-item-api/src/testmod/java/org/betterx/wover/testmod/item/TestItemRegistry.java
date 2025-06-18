package org.betterx.wover.testmod.item;

import org.betterx.wover.item.api.ItemRegistry;
import org.betterx.wover.testmod.entrypoint.TestModWoverItem;

import net.minecraft.tags.ItemTags;

import org.jetbrains.annotations.ApiStatus;

public class TestItemRegistry {
    private static final ItemRegistry R = ItemRegistry.forMod(TestModWoverItem.C);

    private TestItemRegistry() {
    }

    @ApiStatus.Internal
    public static void ensureStaticallyLoaded() {
        // NO-OP
    }

    public static EnchantedAxe ENCHANTED_AXE = R
            .defineDefaultItem("enchanted_axe", EnchantedAxe::new)
            .addTags(ItemTags.AXES, ItemTags.MINING_ENCHANTABLE, ItemTags.MINING_LOOT_ENCHANTABLE)
            .buildAndRegister();
}
