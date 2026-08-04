package de.ambertation.wover.tag.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverTagProvider;
import de.ambertation.wover.tag.api.event.context.ItemTagBootstrapContext;
import de.ambertation.wover.tag.api.predefined.CommonItemTags;
import de.ambertation.wover.tag.api.predefined.ToolTags;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Set;

public class ItemTagProvider extends WoverTagProvider.ForItems {
    public ItemTagProvider(ModCore modCore) {
        // SHEARS and HAMMERS are force-written even when empty: the vanilla enchantable/* tags below
        // reference them, and a tag entry pointing at a tag file that does not exist fails the datapack load.
        super(
                modCore,
                List.of(modCore.namespace, modCore.modId, "c", "minecraft"),
                Set.of(CommonItemTags.SHEARS, CommonItemTags.HAMMERS)
        );
    }

    public void prepareTags(ItemTagBootstrapContext ctx) {
        ctx.add(CommonItemTags.SOUL_GROUND, Blocks.SOUL_SAND.asItem(), Blocks.SOUL_SOIL.asItem());
        ctx.add(
                CommonItemTags.VINES,
                Blocks.VINE.asItem(),
                Blocks.TWISTING_VINES.asItem(),
                Blocks.WEEPING_VINES.asItem()
        );
        ctx.add(CommonItemTags.CHEST, Items.CHEST);

        // c:tools/shears is Wover's tag, not the Fabric convention one - the convention tag is
        // c:tools/shear (singular), so nothing was ever putting vanilla shears in here. That mattered
        // beyond the tag itself: BCLib's shears.MatchToolMixin rewrites every vanilla
        // MatchTool(minecraft:shears) predicate into "is in c:tools/shears", so an empty tag silently
        // disabled shears on every leaves/cobweb/grass loot table, vanilla ones included. Add the vanilla
        // item, and pull in the convention tag optionally so third-party shears keep working.
        ctx.add(CommonItemTags.SHEARS, Items.SHEARS);
        ctx.addOptional(CommonItemTags.SHEARS, CommonItemTags.FABRIC_SHEAR_TOOLS, ToolTags.FABRIC_SHEARS);

        ctx.add(CommonItemTags.IRON_INGOTS, Items.IRON_INGOT);
        ctx.add(CommonItemTags.FURNACES, Blocks.FURNACE.asItem());
        ctx.add(CommonItemTags.WATER_BOTTLES, Items.WATER_BUCKET);

        // Vanilla derives enchantability from item tags (Enchantment#canEnchant checks the enchantment's
        // supported-items tag), and its own tools are listed there one by one - so tools that only exist in a
        // modded tag are not enchantable at all outside of creative mode. Mirror what vanilla grants its
        // shears (mining + durability) and its digging tools (mining + mining_loot + durability, which is what
        // a hammer used to get from DiggerItem before enchantability moved to tags).
        ctx.add(ItemTags.MINING_ENCHANTABLE, CommonItemTags.SHEARS);
        ctx.add(ItemTags.DURABILITY_ENCHANTABLE, CommonItemTags.SHEARS);

        ctx.add(ItemTags.MINING_ENCHANTABLE, CommonItemTags.HAMMERS);
        ctx.add(ItemTags.MINING_LOOT_ENCHANTABLE, CommonItemTags.HAMMERS);
        ctx.add(ItemTags.DURABILITY_ENCHANTABLE, CommonItemTags.HAMMERS);

        ctx.add(
                CommonItemTags.MUSIC_DISCS,
                Items.MUSIC_DISC_13,
                Items.MUSIC_DISC_CAT,
                Items.MUSIC_DISC_BLOCKS,
                Items.MUSIC_DISC_CHIRP,
                Items.MUSIC_DISC_CREATOR,
                Items.MUSIC_DISC_CREATOR_MUSIC_BOX,
                Items.MUSIC_DISC_FAR,
                Items.MUSIC_DISC_MALL,
                Items.MUSIC_DISC_MELLOHI,
                Items.MUSIC_DISC_STAL,
                Items.MUSIC_DISC_STRAD,
                Items.MUSIC_DISC_WARD,
                Items.MUSIC_DISC_11,
                Items.MUSIC_DISC_WAIT,
                Items.MUSIC_DISC_OTHERSIDE,
                Items.MUSIC_DISC_RELIC,
                Items.MUSIC_DISC_5,
                Items.MUSIC_DISC_PIGSTEP,
                Items.MUSIC_DISC_PRECIPICE
        );
    }
}
