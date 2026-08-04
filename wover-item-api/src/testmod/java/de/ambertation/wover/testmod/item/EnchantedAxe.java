package de.ambertation.wover.testmod.item;

import de.ambertation.wover.common.item.api.ItemWithCustomStack;
import de.ambertation.wover.enchantment.api.EnchantmentUtils;
import de.ambertation.wover.item.api.DefaultItemDefinition;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.enchantment.Enchantments;

public class EnchantedAxe extends AxeItem implements ItemWithCustomStack {
    public EnchantedAxe(DefaultItemDefinition<EnchantedAxe> config) {
        super(
                ToolMaterial.WOOD,
                6.0F,
                -3.1F,
                config.getProperties()
        );
    }

    @Override
    public void setupItemStack(ItemStack stack, HolderLookup.Provider provider) {
        EnchantmentUtils.enchantInWorld(stack, Enchantments.SHARPNESS, 5, provider);
    }
}
