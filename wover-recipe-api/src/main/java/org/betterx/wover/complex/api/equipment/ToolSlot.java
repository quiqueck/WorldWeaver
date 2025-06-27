package org.betterx.wover.complex.api.equipment;

import static org.betterx.wover.complex.api.equipment.ToolTier.DIGGER_ITEM_PROPERTIES;
import static org.betterx.wover.complex.api.equipment.ToolTier.SWORD_ITEM_PROPERTIES;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Item;

public enum ToolSlot {
    PICKAXE_SLOT(0, "pickaxe", RecipeCategory.TOOLS, DIGGER_ITEM_PROPERTIES),
    AXE_SLOT(1, "axe", RecipeCategory.TOOLS, DIGGER_ITEM_PROPERTIES),
    SHOVEL_SLOT(2, "shovel", RecipeCategory.TOOLS, DIGGER_ITEM_PROPERTIES),
    SWORD_SLOT(3, "sword", RecipeCategory.COMBAT, SWORD_ITEM_PROPERTIES),
    HOE_SLOT(4, "hoe", RecipeCategory.TOOLS, DIGGER_ITEM_PROPERTIES),
    SHEARS_SLOT(5, "shears", RecipeCategory.TOOLS, DIGGER_ITEM_PROPERTIES),
    HAMMER_SLOT(6, "hammer", RecipeCategory.COMBAT, DIGGER_ITEM_PROPERTIES);

    public interface PropertiesBuilder {
        Item.Properties build(ToolSlot slot, ToolTier tier);
    }

    public final RecipeCategory category;
    public final String name;
    public final int slotIndex;
    private final PropertiesBuilder propertiesBuilder;

    ToolSlot(int slotIndex, String name, RecipeCategory category, PropertiesBuilder propertiesBuilder) {
        this.name = name;
        this.category = category;
        this.slotIndex = slotIndex;
        this.propertiesBuilder = propertiesBuilder;
    }

    public Item.Properties buildProperties(ToolTier tier) {
        return propertiesBuilder.build(this, tier);
    }
}