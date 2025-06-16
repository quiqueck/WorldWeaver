package org.betterx.wover.item.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

public class ArmorItemConfig<I extends Item> extends ItemConfig<I, ArmorItemConfig<I>> {
    public interface ItemFactory<I extends Item> extends ItemConfig.ItemFactory<I, ArmorItemConfig<I>> {
    }

    protected ArmorItemConfig(
            ItemRegistry registry,
            String name,
            ItemConfig.ItemFactory<I, ArmorItemConfig<I>> itemFactory
    ) {
        super(registry, name, itemFactory);
    }

    @Override
    protected void beforeBuild() {

    }

    public ArmorItemConfig<I> humanoidArmor(ArmorMaterial armorMaterial, ArmorType armorType) {
        this.properties.humanoidArmor(armorMaterial, armorType);
        return this;
    }

    public ArmorItemConfig<I> wolfArmor(ArmorMaterial armorMaterial) {
        this.properties.wolfArmor(armorMaterial);
        return this;
    }

    public ArmorItemConfig<I> horseArmor(ArmorMaterial armorMaterial) {
        this.properties.horseArmor(armorMaterial);
        return this;
    }

    public ArmorItemConfig<I> trimMaterial(ResourceKey<TrimMaterial> resourceKey) {
        this.properties.trimMaterial(resourceKey);
        return this;
    }
}
