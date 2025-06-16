package org.betterx.wover.item.api;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public abstract class ItemConfig<I extends Item, C extends ItemConfig<I, C>> {
    public interface ItemFactory<I extends Item, C extends ItemConfig<I, C>> {
        I createItem(C config);
    }

    public final ItemRegistry registry;
    public final ResourceKey<Item> itemKey;
    protected final Item.Properties properties;
    protected TagKey<Item>[] tags;
    protected final ItemConfig.ItemFactory<I, C> itemFactory;

    protected ItemConfig(ItemRegistry registry, String name, ItemConfig.ItemFactory<I, C> itemFactory) {
        this.itemKey = registry.key(name);
        this.properties = new Item.Properties().setId(this.itemKey);
        this.itemFactory = itemFactory;
        this.registry = registry;
    }

    abstract protected void beforeBuild();

    @SuppressWarnings("unchecked")
    public I build() {
        this.beforeBuild();
        return itemFactory.createItem((C) this);
    }

    public I buildAndRegister() {
        I item = this.build();
        this.registry.register(this.itemKey, item, tags);
        return item;
    }

    // **********************************************************************
    // Handle Tags
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final C setTags(TagKey<Item>... tags) {
        this.tags = tags;
        return (C) this;
    }

    public TagKey<Item>[] getTags() {
        return this.tags;
    }


    // **********************************************************************
    // Redirect all (but setId) Item.Properties methods to this.properties


    @SuppressWarnings("unchecked")
    public C usingConvertsTo(Item item) {
        this.properties.usingConvertsTo(item);
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C useCooldown(float f) {
        this.properties.useCooldown(f);
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C stacksTo(int i) {
        this.properties.stacksTo(i);
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C durability(int i) {
        this.properties.durability(i);
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C craftRemainder(Item item) {
        this.properties.craftRemainder(item);
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C rarity(Rarity rarity) {
        this.properties.rarity(rarity);
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C fireResistant() {
        this.properties.fireResistant();
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C jukeboxPlayable(ResourceKey<JukeboxSong> resourceKey) {
        this.properties.jukeboxPlayable(resourceKey);
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C enchantable(int i) {
        this.properties.enchantable(i);
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C repairable(Item item) {
        this.properties.repairable(item);
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C repairable(TagKey<Item> tagKey) {
        this.properties.repairable(tagKey);
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C equippable(EquipmentSlot equipmentSlot) {
        this.properties.equippable(equipmentSlot);
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C equippableUnswappable(EquipmentSlot equipmentSlot) {
        this.properties.equippableUnswappable(equipmentSlot);
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C requiredFeatures(FeatureFlag... featureFlags) {
        this.properties.requiredFeatures(featureFlags);
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C overrideDescription(String string) {
        this.properties.overrideDescription(string);
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C useBlockDescriptionPrefix() {
        this.properties.useBlockDescriptionPrefix();
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C useItemDescriptionPrefix() {
        this.properties.useItemDescriptionPrefix();
        return (C) this;
    }

    public ResourceLocation effectiveModel() {
        return this.properties.effectiveModel();
    }

    @SuppressWarnings("unchecked")
    public <T> C component(DataComponentType<T> dataComponentType, T object) {
        this.properties.component(dataComponentType, object);
        return (C) this;
    }

    @SuppressWarnings("unchecked")
    public C attributes(ItemAttributeModifiers itemAttributeModifiers) {
        this.properties.attributes(itemAttributeModifiers);
        return (C) this;
    }

    // Getter for the properties
    public Item.Properties getProperties() {
        return this.properties;
    }
}
