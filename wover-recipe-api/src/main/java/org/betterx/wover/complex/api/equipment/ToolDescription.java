package org.betterx.wover.complex.api.equipment;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.recipe.api.RecipeBuilder;
import org.betterx.wover.tag.api.predefined.CommonItemTags;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;


class ToolDescription<I extends Item> extends ItemDescription<I> {
    public final ToolSlot slot;

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

    public ToolDescription(
            ModCore modCore,
            ToolSlot slot,
            String path,
            Supplier<I> creator
    ) {
        super(modCore, path, creator, getTagKey(slot));
        this.slot = slot;

    }

    public void addRecipe(
            RecipeBuilder.Context context,
            ToolTier tier,
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
                    .base(sourceSet.get(this.slot))
                    .addon(repairItems)
                    .category(slot.category)
                    .build(context);
        } else {
            var builder = RecipeBuilder.crafting(location, item)
                                       .addMaterial('#', repairItems)
                                       .category(RecipeCategory.TOOLS);

            if (buildRecipe(item, stick, builder)) return;
            builder.category(slot.category).group(location.getPath()).build(context);
        }
    }


}