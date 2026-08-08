package de.ambertation.wover.testmod.item;

import de.ambertation.wover.common.item.api.ItemWithCustomStack;
import de.ambertation.wover.enchantment.api.EnchantmentUtils;
import de.ambertation.wover.item.api.DefaultItemDefinition;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.enchantment.Enchantments;

// 26.3 removed AxeItem (and the other Item subclasses per tool type) - tools are now data-driven through
// Item.Properties#axe/pickaxe/sword/shovel/hoe/tool, which install the same tool component + attack
// attribute modifiers the old constructors set up internally (verified via javap against the 26.3 jar:
// vanilla's own Items.WOODEN_AXE registration calls properties.axe(ToolMaterial.WOOD, 6.0F, -3.2F) the
// same way). So this now extends plain Item and applies the axe properties onto config.getProperties()
// before handing it to super(), keeping the same material and attack damage/speed (WOOD, 6.0F, -3.1F)
// this test relied on.
public class EnchantedAxe extends Item implements ItemWithCustomStack {
    public EnchantedAxe(DefaultItemDefinition<EnchantedAxe> config) {
        super(config.getProperties().axe(ToolMaterial.WOOD, 6.0F, -3.1F));
    }

    @Override
    public void setupItemStack(ItemStack stack, HolderLookup.Provider provider) {
        EnchantmentUtils.enchantInWorld(stack, Enchantments.SHARPNESS, 5, provider);
    }
}
