package org.betterx.wover.complex.api.equipment;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverRecipe;
import org.betterx.wover.item.api.ItemRegistry;
import org.betterx.wover.item.api.ToolItemDefinition;
import org.betterx.wover.item.api.trait.ItemRecipeGeneratorTrait;
import org.betterx.wover.recipe.api.RecipeBuilder;
import org.betterx.wover.tag.api.predefined.CommonItemTags;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import org.jetbrains.annotations.Nullable;


public record ToolDescription<I extends Item>(I item, ResourceKey<Item> itemKey, ToolSlot slot) {
    @SuppressWarnings("unchecked")
    private static TagKey<Item>[] getTagKey(ToolSlot slot) {
        return switch (slot) {
            case PICKAXE_SLOT -> new TagKey[]{ItemTags.PICKAXES};
            case AXE_SLOT -> new TagKey[]{ItemTags.AXES};
            case SHOVEL_SLOT -> new TagKey[]{ItemTags.SHOVELS};
            case HOE_SLOT -> new TagKey[]{ItemTags.HOES};
            case SWORD_SLOT -> new TagKey[]{ItemTags.SWORDS};
            case SHEARS_SLOT -> new TagKey[]{CommonItemTags.SHEARS};
            case HAMMER_SLOT -> new TagKey[]{CommonItemTags.HAMMERS};
            default -> new TagKey[0];
        };
    }

    public static <I extends Item> ToolDescription<I> registerTool(
            ModCore modCore,
            ToolSlot slot,
            String path,
            ToolItemDefinition.ItemFactory<I> creator,
            EquipmentSet equipmentSet
    ) {
        var itemDefinition = ItemRegistry
                .forMod(modCore)
                .defineToolItem(path, creator)
                .addTags(getTagKey(slot));

        slot.addToolConfigTrait(itemDefinition, equipmentSet.toolTier);
        itemDefinition.addTrait(
                ItemRecipeGeneratorTrait
                        .BUILDER
                        .with((key, item, context) -> addRecipe(
                                context,
                                itemDefinition.itemKey.location(),
                                item,
                                equipmentSet.toolTier,
                                slot,
                                equipmentSet.handleItem,
                                equipmentSet.templateBaseSet
                        ))
        );

        return new ToolDescription<>(itemDefinition.buildAndRegister(), itemDefinition.itemKey, slot);
    }

    private static void addRecipe(
            RecipeBuilder.Context context,
            ResourceLocation location,
            Item item,
            ToolTier tier,
            ToolSlot slot,
            ItemLike stick,
            @Nullable EquipmentSet sourceSet
    ) {
        if (item == null) return;
        if (tier == null) return;
        var repairItems = tier.toolMaterial.repairItems();

        var values = tier.getValues(slot);
        if (values != null && values.smithingTemplate() != null && sourceSet != null) {
            RecipeBuilder
                    .smithing(location, item)
                    .template(values.smithingTemplate())
                    .base(sourceSet.get(slot))
                    .addon(repairItems)
                    .category(slot.category)
                    .build(context);
        } else {
            var builder = RecipeBuilder.crafting(location, item)
                                       .addMaterial('#', repairItems)
                                       .category(RecipeCategory.TOOLS);

            builder.addMaterial('I', stick);
            if (slot == ToolSlot.SHEARS_SLOT) {
                builder.shape(" #", "# ");
            } else {
                builder.addMaterial('I', stick);
                if (slot == ToolSlot.PICKAXE_SLOT) {
                    builder.shape("###", " I ", " I ");
                } else if (slot == ToolSlot.AXE_SLOT) {
                    builder.shape("##", "#I", " I");
                } else if (slot == ToolSlot.HOE_SLOT) {
                    builder.shape("##", " I", " I");
                } else if (slot == ToolSlot.SHOVEL_SLOT) {
                    builder.shape("#", "I", "I");
                } else if (slot == ToolSlot.SWORD_SLOT) {
                    builder.shape("#", "#", "I");
                } else {
                    LibWoverRecipe.C.LOG.error("Invalid Tool slot " + slot.name() + " for item " + item + " at " + location);
                    return;
                }
            }
            builder.category(slot.category).group(location.getPath()).build(context);
        }
    }


}