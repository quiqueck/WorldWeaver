package org.betterx.wover.complex.api.equipment;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverRecipe;
import org.betterx.wover.item.api.ArmorItemDefinition;
import org.betterx.wover.item.api.ItemRegistry;
import org.betterx.wover.item.api.trait.ItemRecipeGeneratorTrait;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import org.jetbrains.annotations.Nullable;

public record ArmorDescription<I extends Item>(I item, ResourceKey<Item> itemKey, ArmorSlot slot) {
    @SuppressWarnings("unchecked")
    private static TagKey<Item>[] getTagKey(ArmorSlot slot) {
        return switch (slot) {
            case HELMET_SLOT -> new TagKey[]{ItemTags.HEAD_ARMOR};
            case CHESTPLATE_SLOT -> new TagKey[]{ItemTags.CHEST_ARMOR};
            case LEGGINGS_SLOT -> new TagKey[]{ItemTags.LEG_ARMOR};
            case BOOTS_SLOT -> new TagKey[]{ItemTags.FOOT_ARMOR};
            default -> new TagKey[0];
        };
    }

    public static <I extends Item> ArmorDescription<I> registerArmor(
            ModCore modCore,
            ArmorSlot slot,
            String path,
            ArmorItemDefinition.ItemFactory<I> creator,
            EquipmentSet equipmentSet
    ) {
        var itemDefinition = ItemRegistry
                .forMod(modCore)
                .defineArmorItem(path, creator)
                .addTags(getTagKey(slot))
                .humanoidArmor(equipmentSet.armorTier.armorMaterial, slot.armorType);

        itemDefinition.addTrait(
                ItemRecipeGeneratorTrait
                        .BUILDER
                        .with((key, item, context) -> addRecipe(
                                context,
                                itemDefinition.itemKey.location(),
                                item,
                                equipmentSet.armorTier,
                                slot,
                                equipmentSet.templateBaseSet
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
        if (values != null && values.smithingTemplate() != null && sourceSet != null) {
            RecipeBuilder
                    .smithing(location, item)
                    .template(values.smithingTemplate())
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
                    .group(location.getPath())
                    .build(context);

        }
    }
}
