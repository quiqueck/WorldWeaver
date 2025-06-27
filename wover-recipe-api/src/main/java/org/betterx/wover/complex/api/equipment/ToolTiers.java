package org.betterx.wover.complex.api.equipment;

import org.betterx.wover.complex.api.equipment.ToolTier.ToolValues;
import org.betterx.wover.tag.api.predefined.MineableTags;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ToolMaterial;

public class ToolTiers {
    public static ToolTier WOOD_TOOL = ToolTier
            .builder("wooden")
            .toolMaterial(ToolMaterial.WOOD)
            .blockTag(MineableTags.NEEDS_WOOD_TOOL)
            .toolValues(ToolSlot.SWORD_SLOT, new ToolValues(3, -2.4f))
            .toolValues(ToolSlot.SHOVEL_SLOT, new ToolValues(1.5f, -3.0f, BlockTags.MINEABLE_WITH_SHOVEL))
            .toolValues(ToolSlot.PICKAXE_SLOT, new ToolValues(1, -2.8f, BlockTags.MINEABLE_WITH_PICKAXE))
            .toolValues(ToolSlot.AXE_SLOT, new ToolValues(6, -3.2f, BlockTags.MINEABLE_WITH_AXE))
            .toolValues(ToolSlot.HOE_SLOT, new ToolValues(0, -3.0f, BlockTags.MINEABLE_WITH_HOE))
            .toolValues(ToolSlot.HAMMER_SLOT, new ToolValues(7, -3.0f, MineableTags.HAMMER))
            .build();

    public static ToolTier STONE_TOOL = ToolTier
            .builder("stone")
            .toolMaterial(ToolMaterial.STONE)
            .blockTag(BlockTags.NEEDS_STONE_TOOL)
            .toolValues(ToolSlot.SWORD_SLOT, new ToolValues(3, -2.4f))
            .toolValues(ToolSlot.SHOVEL_SLOT, new ToolValues(1.5f, -3.0f, BlockTags.MINEABLE_WITH_SHOVEL))
            .toolValues(ToolSlot.PICKAXE_SLOT, new ToolValues(1, -2.8f, BlockTags.MINEABLE_WITH_PICKAXE))
            .toolValues(ToolSlot.AXE_SLOT, new ToolValues(7, -3.2f, BlockTags.MINEABLE_WITH_AXE))
            .toolValues(ToolSlot.HOE_SLOT, new ToolValues(-1, -2.0f, BlockTags.MINEABLE_WITH_HOE))
            .toolValues(ToolSlot.SHEARS_SLOT, new ToolValues(-1, -2.5f))
            .toolValues(ToolSlot.HAMMER_SLOT, new ToolValues(9, -5.0f, MineableTags.HAMMER))
            .build();

    public static ToolTier GOLD_TOOL = ToolTier
            .builder("golden")
            .toolMaterial(ToolMaterial.GOLD)
            .blockTag(MineableTags.NEEDS_GOLD_TOOL)
            .toolValues(ToolSlot.SWORD_SLOT, new ToolValues(3, -2.4f))
            .toolValues(ToolSlot.SHOVEL_SLOT, new ToolValues(1.5f, -3.0f, BlockTags.MINEABLE_WITH_SHOVEL))
            .toolValues(ToolSlot.PICKAXE_SLOT, new ToolValues(1, -2.8f, BlockTags.MINEABLE_WITH_PICKAXE))
            .toolValues(ToolSlot.AXE_SLOT, new ToolValues(6, -3.0f, BlockTags.MINEABLE_WITH_AXE))
            .toolValues(ToolSlot.HOE_SLOT, new ToolValues(0, -3.0f, BlockTags.MINEABLE_WITH_HOE))
            .toolValues(ToolSlot.SHEARS_SLOT, new ToolValues(0, -3.5f)).
            toolValues(ToolSlot.HAMMER_SLOT, new ToolValues(8, -4.0f, MineableTags.HAMMER))
            .build();

    public static ToolTier IRON_TOOL = ToolTier
            .builder("iron")
            .toolMaterial(ToolMaterial.IRON)
            .blockTag(BlockTags.NEEDS_IRON_TOOL)
            .toolValues(ToolSlot.SWORD_SLOT, new ToolValues(3, -2.4f))
            .toolValues(ToolSlot.SHOVEL_SLOT, new ToolValues(1.5f, -3.0f, BlockTags.MINEABLE_WITH_SHOVEL))
            .toolValues(ToolSlot.PICKAXE_SLOT, new ToolValues(1, -2.8f, BlockTags.MINEABLE_WITH_PICKAXE))
            .toolValues(ToolSlot.AXE_SLOT, new ToolValues(6, -3.1f, BlockTags.MINEABLE_WITH_AXE))
            .toolValues(ToolSlot.HOE_SLOT, new ToolValues(-2, -1.0f, BlockTags.MINEABLE_WITH_HOE))
            .toolValues(ToolSlot.SHEARS_SLOT, new ToolValues(-2, -4.0f, MineableTags.SHEARS))
            .toolValues(ToolSlot.HAMMER_SLOT, new ToolValues(8, -4.3f, MineableTags.HAMMER))
            .build();

    public static ToolTier DIAMOND_TOOL = ToolTier
            .builder("diamond")
            .toolMaterial(ToolMaterial.DIAMOND)
            .blockTag(BlockTags.NEEDS_DIAMOND_TOOL)
            .toolValues(ToolSlot.SWORD_SLOT, new ToolValues(3, -2.4f))
            .toolValues(ToolSlot.SHOVEL_SLOT, new ToolValues(1.5f, -3.0f, BlockTags.MINEABLE_WITH_SHOVEL))
            .toolValues(ToolSlot.PICKAXE_SLOT, new ToolValues(1, -2.8f, BlockTags.MINEABLE_WITH_PICKAXE))
            .toolValues(ToolSlot.AXE_SLOT, new ToolValues(5, -3.0f, BlockTags.MINEABLE_WITH_AXE))
            .toolValues(ToolSlot.HOE_SLOT, new ToolValues(-3, 0.0f, BlockTags.MINEABLE_WITH_HOE))
            .toolValues(ToolSlot.SHEARS_SLOT, new ToolValues(-3, -0.5f, MineableTags.SHEARS))
            .toolValues(ToolSlot.HAMMER_SLOT, new ToolValues(7, -4.0f, MineableTags.HAMMER))
            .build();

    public static ToolTier NETHERITE_TOOL = ToolTier
            .builder("netherite")
            .toolMaterial(ToolMaterial.NETHERITE)
            .blockTag(MineableTags.NEEDS_NETHERITE_TOOL)
            .toolValues(ToolSlot.SWORD_SLOT, new ToolValues(3, -2.4f))
            .toolValues(ToolSlot.SHOVEL_SLOT, new ToolValues(1.5f, -3.0f, BlockTags.MINEABLE_WITH_SHOVEL))
            .toolValues(ToolSlot.PICKAXE_SLOT, new ToolValues(1, -2.8f, BlockTags.MINEABLE_WITH_PICKAXE))
            .toolValues(ToolSlot.AXE_SLOT, new ToolValues(5, -3.0f, BlockTags.MINEABLE_WITH_AXE))
            .toolValues(ToolSlot.HOE_SLOT, new ToolValues(-4, -0.5f, BlockTags.MINEABLE_WITH_HOE))
            .toolValues(ToolSlot.HAMMER_SLOT, new ToolValues(8, -3.2f, MineableTags.HAMMER))
            .build();


}
