package de.ambertation.wover.complex.api.equipment;

import de.ambertation.wover.complex.api.equipment.ToolTier.ToolValues;
import de.ambertation.wover.tag.api.predefined.MineableTags;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ToolMaterial;

/**
 * Ready-made {@link ToolTier}s matching every vanilla {@link ToolMaterial}, including hammer values (a
 * WoVer/BetterX addition, not part of vanilla). For use with {@link EquipmentSet} or as a base for
 * {@link ToolTier#copyWithOffset}.
 */
public class ToolTiers {
    /** The vanilla wooden tool tier. */
    public static ToolTier WOOD_TOOL = ToolTier
            .builder("wooden")
            .level(0)
            .toolMaterial(ToolMaterial.WOOD)
            .blockTag(MineableTags.NEEDS_WOOD_TOOL)
            .toolValues(ToolSlot.SWORD_SLOT, new ToolValues(3, -2.4f, MineableTags.SWORD))
            .toolValues(ToolSlot.SHOVEL_SLOT, new ToolValues(1.5f, -3.0f, BlockTags.MINEABLE_WITH_SHOVEL))
            .toolValues(ToolSlot.PICKAXE_SLOT, new ToolValues(1, -2.8f, BlockTags.MINEABLE_WITH_PICKAXE))
            .toolValues(ToolSlot.AXE_SLOT, new ToolValues(6, -3.2f, 5, BlockTags.MINEABLE_WITH_AXE))
            .toolValues(ToolSlot.HOE_SLOT, new ToolValues(0, -3.0f, BlockTags.MINEABLE_WITH_HOE))
            .toolValues(ToolSlot.HAMMER_SLOT, new ToolValues(7, -3.0f, 7, MineableTags.HAMMER))
            .build();

    /** The vanilla stone tool tier. */
    public static ToolTier STONE_TOOL = ToolTier
            .builder("stone")
            .level(1)
            .toolMaterial(ToolMaterial.STONE)
            .blockTag(BlockTags.NEEDS_STONE_TOOL)
            .toolValues(ToolSlot.SWORD_SLOT, new ToolValues(3, -2.4f, MineableTags.SWORD))
            .toolValues(ToolSlot.SHOVEL_SLOT, new ToolValues(1.5f, -3.0f, BlockTags.MINEABLE_WITH_SHOVEL))
            .toolValues(ToolSlot.PICKAXE_SLOT, new ToolValues(1, -2.8f, BlockTags.MINEABLE_WITH_PICKAXE))
            .toolValues(ToolSlot.AXE_SLOT, new ToolValues(7, -3.2f, 5, BlockTags.MINEABLE_WITH_AXE))
            .toolValues(ToolSlot.HOE_SLOT, new ToolValues(-1, -2.0f, BlockTags.MINEABLE_WITH_HOE))
            .toolValues(ToolSlot.SHEARS_SLOT, new ToolValues(-1, -2.5f, MineableTags.SHEARS))
            .toolValues(ToolSlot.HAMMER_SLOT, new ToolValues(9, -5.0f, 8, MineableTags.HAMMER))
            .build();

    /** The vanilla golden tool tier. */
    public static ToolTier GOLD_TOOL = ToolTier
            .builder("golden")
            .level(2)
            .toolMaterial(ToolMaterial.GOLD)
            .blockTag(MineableTags.NEEDS_GOLD_TOOL)
            .toolValues(ToolSlot.SWORD_SLOT, new ToolValues(3, -2.4f, MineableTags.SWORD))
            .toolValues(ToolSlot.SHOVEL_SLOT, new ToolValues(1.5f, -3.0f, BlockTags.MINEABLE_WITH_SHOVEL))
            .toolValues(ToolSlot.PICKAXE_SLOT, new ToolValues(1, -2.8f, BlockTags.MINEABLE_WITH_PICKAXE))
            .toolValues(ToolSlot.AXE_SLOT, new ToolValues(6, -3.0f, 5, BlockTags.MINEABLE_WITH_AXE))
            .toolValues(ToolSlot.HOE_SLOT, new ToolValues(0, -3.0f, BlockTags.MINEABLE_WITH_HOE))
            .toolValues(ToolSlot.SHEARS_SLOT, new ToolValues(0, -3.5f, MineableTags.SHEARS)).
            toolValues(ToolSlot.HAMMER_SLOT, new ToolValues(8, -4.0f, 6, MineableTags.HAMMER))
            .build();

    /** The vanilla iron tool tier. */
    public static ToolTier IRON_TOOL = ToolTier
            .builder("iron")
            .level(2)
            .toolMaterial(ToolMaterial.IRON)
            .blockTag(BlockTags.NEEDS_IRON_TOOL)
            .toolValues(ToolSlot.SWORD_SLOT, new ToolValues(3, -2.4f, MineableTags.SWORD))
            .toolValues(ToolSlot.SHOVEL_SLOT, new ToolValues(1.5f, -3.0f, BlockTags.MINEABLE_WITH_SHOVEL))
            .toolValues(ToolSlot.PICKAXE_SLOT, new ToolValues(1, -2.8f, BlockTags.MINEABLE_WITH_PICKAXE))
            .toolValues(ToolSlot.AXE_SLOT, new ToolValues(6, -3.1f, 5, BlockTags.MINEABLE_WITH_AXE))
            .toolValues(ToolSlot.HOE_SLOT, new ToolValues(-2, -1.0f, BlockTags.MINEABLE_WITH_HOE))
            .toolValues(ToolSlot.SHEARS_SLOT, new ToolValues(-2, -4.0f, MineableTags.SHEARS))
            .toolValues(ToolSlot.HAMMER_SLOT, new ToolValues(8, -4.3f, 7, MineableTags.HAMMER))
            .build();

    /** The vanilla diamond tool tier. */
    public static ToolTier DIAMOND_TOOL = ToolTier
            .builder("diamond")
            .level(3)
            .toolMaterial(ToolMaterial.DIAMOND)
            .blockTag(BlockTags.NEEDS_DIAMOND_TOOL)
            .toolValues(ToolSlot.SWORD_SLOT, new ToolValues(3, -2.4f, MineableTags.SWORD))
            .toolValues(ToolSlot.SHOVEL_SLOT, new ToolValues(1.5f, -3.0f, BlockTags.MINEABLE_WITH_SHOVEL))
            .toolValues(ToolSlot.PICKAXE_SLOT, new ToolValues(1, -2.8f, BlockTags.MINEABLE_WITH_PICKAXE))
            .toolValues(ToolSlot.AXE_SLOT, new ToolValues(5, -3.0f, 5, BlockTags.MINEABLE_WITH_AXE))
            .toolValues(ToolSlot.HOE_SLOT, new ToolValues(-3, 0.0f, BlockTags.MINEABLE_WITH_HOE))
            .toolValues(ToolSlot.SHEARS_SLOT, new ToolValues(-3, -0.5f, MineableTags.SHEARS))
            .toolValues(ToolSlot.HAMMER_SLOT, new ToolValues(7, -4.0f, 6, MineableTags.HAMMER))
            .build();

    /** The vanilla netherite tool tier. */
    public static ToolTier NETHERITE_TOOL = ToolTier
            .builder("netherite")
            .level(4)
            .toolMaterial(ToolMaterial.NETHERITE)
            .blockTag(MineableTags.NEEDS_NETHERITE_TOOL)
            .toolValues(ToolSlot.SWORD_SLOT, new ToolValues(3, -2.4f, MineableTags.SWORD))
            .toolValues(ToolSlot.SHOVEL_SLOT, new ToolValues(1.5f, -3.0f, BlockTags.MINEABLE_WITH_SHOVEL))
            .toolValues(ToolSlot.PICKAXE_SLOT, new ToolValues(1, -2.8f, BlockTags.MINEABLE_WITH_PICKAXE))
            .toolValues(ToolSlot.AXE_SLOT, new ToolValues(5, -3.0f, 5, BlockTags.MINEABLE_WITH_AXE))
            .toolValues(ToolSlot.HOE_SLOT, new ToolValues(-4, -0.5f, BlockTags.MINEABLE_WITH_HOE))
            .toolValues(ToolSlot.SHEARS_SLOT, new ToolValues(-4, 0f, MineableTags.SHEARS))
            .toolValues(ToolSlot.HAMMER_SLOT, new ToolValues(8, -3.2f, 5, MineableTags.HAMMER))
            .build();


}
