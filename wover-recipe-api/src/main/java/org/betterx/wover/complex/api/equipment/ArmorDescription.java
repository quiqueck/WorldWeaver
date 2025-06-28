package org.betterx.wover.complex.api.equipment;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public class ArmorDescription<I extends Item> extends ItemDescription<I> {
    private final ArmorSlot slot;

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

    public ArmorDescription(
            ModCore modCore,
            ArmorSlot slot,
            String path,
            Supplier<I> creator
    ) {
        super(modCore, path, creator, getTagKey(slot));
        this.slot = slot;
    }

    public void addRecipe(
            HolderGetter<Item> items, RecipeProvider provider, RecipeOutput ctx,
            ArmorTier tier,
            ItemLike stick,
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
                    .base(sourceSet.get(this.slot))
                    .addon(repairWith)
                    .category(slot.category)
                    .build(items, provider, ctx);
        } else {
            var builder = RecipeBuilder.crafting(location, item)
                                       .addMaterial('#', repairWith)
                                       .category(RecipeCategory.TOOLS);

            if (buildRecipe(item, stick, builder)) return;
            builder
                    .category(slot.category)
                    .group(location.getPath())
                    .build(items, provider, ctx);
        }
    }
}
