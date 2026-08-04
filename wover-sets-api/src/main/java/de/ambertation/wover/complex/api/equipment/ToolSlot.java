package de.ambertation.wover.complex.api.equipment;

import de.ambertation.wover.item.api.ToolItemDefinition;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Item;

/**
 * The tool pieces an {@link EquipmentSet} can register.
 * <p>
 * Each constant carries the naming suffix used by {@link EquipmentSet#add(ToolSlot)} (e.g.
 * {@code <baseName>_pickaxe}), the {@link RecipeCategory} used for the auto-generated recipe, and the internal
 * item trait used to configure a {@link ToolItemDefinition} with this slot's {@link ToolTier.ToolValues}
 * ({@link #addToolConfigTrait}).
 */
public enum ToolSlot {
    PICKAXE_SLOT(0, "pickaxe", RecipeCategory.TOOLS, ToolTier.ConfigureDiggerItemTrait::new),
    AXE_SLOT(1, "axe", RecipeCategory.TOOLS, ToolTier.ConfigureDiggerItemTrait::new),
    SHOVEL_SLOT(2, "shovel", RecipeCategory.TOOLS, ToolTier.ConfigureDiggerItemTrait::new),
    SWORD_SLOT(3, "sword", RecipeCategory.COMBAT, ToolTier.ConfigureSwordItemTrait::new),
    HOE_SLOT(4, "hoe", RecipeCategory.TOOLS, ToolTier.ConfigureDiggerItemTrait::new),
    SHEARS_SLOT(5, "shears", RecipeCategory.TOOLS, ToolTier.ConfigureDiggerItemTrait::new),
    HAMMER_SLOT(6, "hammer", RecipeCategory.COMBAT, ToolTier.ConfigureDiggerItemTrait::new);


    /** The {@link RecipeCategory} used for this slot's auto-generated recipe. */
    public final RecipeCategory category;
    /** The naming suffix appended to an {@link EquipmentSet}'s base name for this slot (e.g. {@code "pickaxe"}). */
    public final String name;
    /** The index of this slot's values within a {@link ToolTier}'s internal value array. */
    public final int slotIndex;
    private final ToolTier.TraitBuilder traitBuilder;

    ToolSlot(int slotIndex, String name, RecipeCategory category, ToolTier.TraitBuilder traitBuilder) {
        this.name = name;
        this.category = category;
        this.slotIndex = slotIndex;
        this.traitBuilder = traitBuilder;
    }

    /**
     * Adds the internal item trait that configures {@code definition} (as a digger or sword item, depending on
     * this slot) with the {@link ToolTier.ToolValues} {@code tier} defines for this slot.
     *
     * @param definition the item definition to configure
     * @param tier       the tool tier to read values from
     * @param <I>        the item type being defined
     */
    public <I extends Item> void addToolConfigTrait(ToolItemDefinition<I> definition, ToolTier tier) {
        definition.addTrait(traitBuilder.with(ToolSlot.this, tier));
    }
}