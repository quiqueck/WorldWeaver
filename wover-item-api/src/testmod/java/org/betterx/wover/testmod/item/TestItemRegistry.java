package org.betterx.wover.testmod.item;

import org.betterx.wover.item.api.ItemRegistry;
import org.betterx.wover.testmod.entrypoint.TestModWoverItem;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;

import org.jetbrains.annotations.ApiStatus;

public class TestItemRegistry {
    private static final ItemRegistry R = ItemRegistry.forMod(TestModWoverItem.C);

    private TestItemRegistry() {
    }

    @ApiStatus.Internal
    public static void ensureStaticallyLoaded() {
        // NO-OP
    }

    public static Item ENCHANTED_AXE = R.register(
            "enchanted_axe", new EnchantedAxe(R.C.mk("enchanted_axe")),
            ItemTags.AXES, ItemTags.MINING_ENCHANTABLE, ItemTags.MINING_LOOT_ENCHANTABLE
    );
}
