package org.betterx.wover.complex.api.equipment;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.item.api.ItemRegistry;
import org.betterx.wover.recipe.api.CraftingRecipeBuilder;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;
import java.util.function.Supplier;

abstract class ItemDescription<I extends Item> {
    public final I item;
    public final ResourceLocation location;

    public ItemDescription(
            ModCore modCore,
            String path,
            Supplier<I> creator,
            TagKey<Item>... tags
    ) {
        this.location = modCore.mk(path);
        this.item = creator.get();

        ItemRegistry.forMod(modCore).registerAsTool(path, item, tags);
    }

    static boolean buildRecipe(Item tool, ItemLike stick, CraftingRecipeBuilder builder) {
        final var equipableComponent = tool.components().get(DataComponents.EQUIPPABLE);
        if (tool instanceof ShearsItem) {
            builder.shape(" #", "# ");
        } else if (equipableComponent != null) {
            if (equipableComponent.slot() == EquipmentSlot.FEET) {
                builder.shape("# #", "# #");
            } else if (equipableComponent.slot() == EquipmentSlot.HEAD) {
                builder.shape("###", "# #");
            } else if (equipableComponent.slot() == EquipmentSlot.CHEST) {
                builder.shape("# #", "###", "###");
            } else if (equipableComponent.slot() == EquipmentSlot.LEGS) {
                builder.shape("###", "# #", "# #");
            } else return true;
        } else {
            final var toolComponent = tool.components().get(DataComponents.TOOL);
            if (toolComponent == null) return false;
            final Function<TagKey<Block>, Boolean> is = (tagKey) -> toolComponent
                    .rules()
                    .stream()
                    .anyMatch(rule -> rule.correctForDrops().orElse(false) && rule.blocks()
                                                                                  .unwrapKey()
                                                                                  .map(b -> b.location()
                                                                                             .equals(tagKey.location()))
                                                                                  .orElse(false));
            builder.addMaterial('I', stick);
            if (is.apply(ToolTiers.WOOD_TOOL.getValues(ToolSlot.PICKAXE_SLOT).minableWithTag())) {
                builder.shape("###", " I ", " I ");
            } else if (is.apply(ToolTiers.WOOD_TOOL.getValues(ToolSlot.AXE_SLOT).minableWithTag())) {
                builder.shape("##", "#I", " I");
            } else if (is.apply(ToolTiers.WOOD_TOOL.getValues(ToolSlot.HOE_SLOT).minableWithTag())) {
                builder.shape("##", " I", " I");
            } else if (is.apply(ToolTiers.WOOD_TOOL.getValues(ToolSlot.SHOVEL_SLOT).minableWithTag())) {
                builder.shape("#", "I", "I");
            } else if (is.apply(ToolTiers.WOOD_TOOL.getValues(ToolSlot.SWORD_SLOT).minableWithTag())) {
                builder.shape("#", "#", "I");
            } else return true;
        }

        return false;
    }

    public I getItem() {
        return item;
    }
}
