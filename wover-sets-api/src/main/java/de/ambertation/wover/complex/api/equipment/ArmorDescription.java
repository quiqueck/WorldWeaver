package de.ambertation.wover.complex.api.equipment;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverRecipe;
import de.ambertation.wover.item.api.ArmorItemDefinition;
import de.ambertation.wover.item.api.ItemRegistry;
import de.ambertation.wover.item.api.trait.ItemRecipeTrait;
import de.ambertation.wover.item.impl.trait.ItemRecipeTraitBuilder;
import de.ambertation.wover.recipe.api.RecipeBuilder;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import org.jetbrains.annotations.Nullable;

/**
 * The built and registered result of registering a single {@link ArmorSlot} on an {@link EquipmentSet}, created
 * by {@link #registerArmor}.
 *
 * @param item    the built and registered item
 * @param itemKey the registry key the item was registered under
 * @param slot    the armor slot this item was registered for
 * @param <I>     the item type
 */
public record ArmorDescription<I extends Item>(I item, ResourceKey<Item> itemKey, ArmorSlot slot) {
    /**
     * Builds and registers a single armor item for {@code equipmentSet}, with an auto-generated crafting/smithing
     * recipe unless {@code recipeOverride} is given.
     *
     * @param modCore        the mod to register the item under
     * @param slot           the armor slot being registered
     * @param path           the item's registration name
     * @param creator        creates the item from its definition
     * @param equipmentSet   the set the item belongs to (for its tier, template base set, and recipe material)
     * @param recipeOverride the recipe trait to use instead of the auto-generated recipe, or {@code null} to
     *                       keep the default
     * @param <I>            the item type being registered
     * @return the registered item description
     */
    public static <I extends Item> ArmorDescription<I> registerArmor(
            ModCore modCore,
            ArmorSlot slot,
            String path,
            ArmorItemDefinition.ItemFactory<I> creator,
            EquipmentSet equipmentSet,
            @Nullable ItemRecipeTrait recipeOverride
    ) {
        var itemDefinition = ItemRegistry
                .forMod(modCore)
                .defineArmorItem(path, creator)
                .addTags(slot.humanoidArmorTags())
                .humanoidArmor(equipmentSet.armorTier.armorMaterial, slot.armorType);

        itemDefinition.addTrait(
                recipeOverride != null ? recipeOverride : ItemRecipeTraitBuilder
                        .BUILDER
                        .with((key, item, context) -> addRecipe(
                                context,
                                itemDefinition.itemKey.location(),
                                item,
                                equipmentSet.armorTier,
                                slot,
                                equipmentSet.templateBaseSet == null ? null : equipmentSet.templateBaseSet.get()
                        ))
        );

        return new ArmorDescription<I>(itemDefinition.buildAndRegister(), itemDefinition.itemKey, slot);
    }


    private static void addRecipe(
            RecipeBuilder.Context context,
            ResourceLocation location,
            Item item,
            ArmorTier tier,
            ArmorSlot slot,
            @Nullable EquipmentSet sourceSet
    ) {
        if (item == null) return;
        if (tier == null) return;

        var repairWith = tier.armorMaterial.repairIngredient();

        var values = tier.getValues(slot);
        var smithingTemplate = values == null ? null : values.resolveSmithingTemplate();
        if (values != null && smithingTemplate != null && sourceSet != null) {
            RecipeBuilder
                    .smithing(location, item)
                    .template(smithingTemplate)
                    .base(sourceSet.get(slot))
                    .addon(repairWith)
                    .category(slot.category)
                    .build(context);
        } else {
            var builder = RecipeBuilder.crafting(location, item)
                                       .addMaterial('#', repairWith)
                                       .category(RecipeCategory.TOOLS);

            if (slot == ArmorSlot.BOOTS_SLOT) {
                builder.shape("# #", "# #");
            } else if (slot == ArmorSlot.HELMET_SLOT) {
                builder.shape("###", "# #");
            } else if (slot == ArmorSlot.CHESTPLATE_SLOT) {
                builder.shape("# #", "###", "###");
            } else if (slot == ArmorSlot.LEGGINGS_SLOT) {
                builder.shape("###", "# #", "# #");
            } else {
                LibWoverRecipe.C.LOG.error("Invalid Armor slot " + slot.name() + " for item " + item + " at " + location);
                return;
            }
            builder
                    .category(slot.category)
                    .group(slot.name)
                    .build(context);

        }
    }
}
