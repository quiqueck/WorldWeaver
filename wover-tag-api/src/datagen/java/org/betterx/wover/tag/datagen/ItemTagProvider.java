package org.betterx.wover.tag.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverTagProvider;
import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;
import org.betterx.wover.tag.api.predefined.CommonItemTags;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class ItemTagProvider extends WoverTagProvider.ForItems {
    public ItemTagProvider(ModCore modCore) {
        super(modCore, List.of(modCore.namespace, modCore.modId, "c", "minecraft"));
    }

    protected void prepareTags(ItemTagBootstrapContext ctx) {
        ctx.add(CommonItemTags.SOUL_GROUND, Blocks.SOUL_SAND.asItem(), Blocks.SOUL_SOIL.asItem());

        ctx.add(CommonItemTags.CHEST, Items.CHEST);
        ctx.add(CommonItemTags.IRON_INGOTS, Items.IRON_INGOT);
        ctx.add(CommonItemTags.FURNACES, Blocks.FURNACE.asItem());
        ctx.add(CommonItemTags.WATER_BOTTLES, Items.WATER_BUCKET);
    }
}
