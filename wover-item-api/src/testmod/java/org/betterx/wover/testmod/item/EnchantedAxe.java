package org.betterx.wover.testmod.item;

import org.betterx.wover.common.item.api.ItemWithCustomStack;
import org.betterx.wover.enchantment.api.EnchantmentUtils;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.enchantment.Enchantments;

public class EnchantedAxe extends AxeItem implements ItemWithCustomStack {
    public EnchantedAxe(ResourceLocation id) {
        super(
                ToolMaterial.WOOD,
                6.0F,
                -3.1F,
                new Item.Properties().setId(ResourceKey.create(BuiltInRegistries.ITEM.key(), id))
        );
    }

    @Override
    public void setupItemStack(ItemStack stack, HolderLookup.Provider provider) {
        EnchantmentUtils.enchantInWorld(stack, Enchantments.SHARPNESS, 5, provider);
    }
}
