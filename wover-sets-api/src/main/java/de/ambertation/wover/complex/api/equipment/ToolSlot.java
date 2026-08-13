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
    PICKAXE_SLOT(0, "pickaxe", RecipeCategory.TOOLS, ToolTier.ConfigureDiggerItemTrait::new, true),
    AXE_SLOT(1, "axe", RecipeCategory.TOOLS, ToolTier.ConfigureDiggerItemTrait::new, true),
    SHOVEL_SLOT(2, "shovel", RecipeCategory.TOOLS, ToolTier.ConfigureDiggerItemTrait::new, true),
    SWORD_SLOT(3, "sword", RecipeCategory.COMBAT, ToolTier.ConfigureSwordItemTrait::new, true),
    HOE_SLOT(4, "hoe", RecipeCategory.TOOLS, ToolTier.ConfigureDiggerItemTrait::new, true),
    // Vanilla shears use item/generated, not item/handheld - keep the flat model here too.
    SHEARS_SLOT(5, "shears", RecipeCategory.TOOLS, ToolTier.ConfigureDiggerItemTrait::new, false),
    HAMMER_SLOT(6, "hammer", RecipeCategory.COMBAT, ToolTier.ConfigureDiggerItemTrait::new, true),
    // The spear brings its own (two-model) binding, see ToolTier.ConfigureSpearItemTrait.
    SPEAR_SLOT(7, "spear", RecipeCategory.COMBAT, ToolTier.ConfigureSpearItemTrait::new, false);


    /** The {@link RecipeCategory} used for this slot's auto-generated recipe. */
    public final RecipeCategory category;
    /** The naming suffix appended to an {@link EquipmentSet}'s base name for this slot (e.g. {@code "pickaxe"}). */
    public final String name;
    /** The index of this slot's values within a {@link ToolTier}'s internal value array. */
    public final int slotIndex;
    /**
     * Whether this slot's item model is generated with vanilla's {@code item/handheld} parent (the tool grip)
     * rather than the flat {@code item/generated} one. Slots that bring their own model binding (the spear) or
     * that vanilla itself renders flat (shears) set this to {@code false}.
     */
    public final boolean handheldModel;
    private final ToolTier.TraitBuilder traitBuilder;

    ToolSlot(
            int slotIndex,
            String name,
            RecipeCategory category,
            ToolTier.TraitBuilder traitBuilder,
            boolean handheldModel
    ) {
        this.name = name;
        this.category = category;
        this.slotIndex = slotIndex;
        this.traitBuilder = traitBuilder;
        this.handheldModel = handheldModel;
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