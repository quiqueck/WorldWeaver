package org.betterx.wover.complex.api.equipment;

import org.betterx.wover.item.api.ToolItemDefinition;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Item;

public enum ToolSlot {
    PICKAXE_SLOT(0, "pickaxe", RecipeCategory.TOOLS, ToolTier.ConfigureDiggerItemTrait::new),
    AXE_SLOT(1, "axe", RecipeCategory.TOOLS, ToolTier.ConfigureDiggerItemTrait::new),
    SHOVEL_SLOT(2, "shovel", RecipeCategory.TOOLS, ToolTier.ConfigureDiggerItemTrait::new),
    SWORD_SLOT(3, "sword", RecipeCategory.COMBAT, ToolTier.ConfigureSwordItemTrait::new),
    HOE_SLOT(4, "hoe", RecipeCategory.TOOLS, ToolTier.ConfigureDiggerItemTrait::new),
    SHEARS_SLOT(5, "shears", RecipeCategory.TOOLS, ToolTier.ConfigureDiggerItemTrait::new),
    HAMMER_SLOT(6, "hammer", RecipeCategory.COMBAT, ToolTier.ConfigureDiggerItemTrait::new);


    public final RecipeCategory category;
    public final String name;
    public final int slotIndex;
    private final ToolTier.TraitBuilder traitBuilder;

    ToolSlot(int slotIndex, String name, RecipeCategory category, ToolTier.TraitBuilder traitBuilder) {
        this.name = name;
        this.category = category;
        this.slotIndex = slotIndex;
        this.traitBuilder = traitBuilder;
    }

    public <I extends Item> void addToolConfigTrait(ToolItemDefinition<I> definition, ToolTier tier) {
        definition.addTrait(traitBuilder.with(ToolSlot.this, tier));
    }
}