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

/**
 * Abstract base class for configuring and building Minecraft items with a fluent API.
 * This class provides a builder pattern for item creation with support for tags, properties,
 * and automatic registration.
 * 
 * @param <I> The type of item being created, must extend {@link Item}
 * @param <C> The concrete configuration class type for method chaining
 * 
 * @author Quiqueck
 * @since 21.6.0
 */
public abstract class ItemConfig<I extends Item, C extends ItemConfig<I, C>> {
    /**
     * Factory interface for creating items from configuration objects.
     * 
     * @param <I> The type of item to create
     * @param <C> The configuration type used to create the item
     */
    public interface ItemFactory<I extends Item, C extends ItemConfig<I, C>> {
        /**
         * Creates an item instance from the given configuration.
         * 
         * @param config The configuration object containing all item settings
         * @return The created item instance
         */
        I createItem(C config);
    }

    /** The item registry used for registering the item */
    public final ItemRegistry registry;
    
    /** The resource key identifying this item */
    public final ResourceKey<Item> itemKey;
    
    /** The properties configuration for the item */
    protected final Item.Properties properties;
    
    /** Optional tags to be applied to the item */
    protected TagKey<Item>[] tags;
    
    /** Factory instance used to create the item */
    protected final ItemConfig.ItemFactory<I, C> itemFactory;

    /**
     * Creates a new item configuration.
     * 
     * @param registry The item registry to use for registration
     * @param itemName The name identifier for the item
     * @param itemFactory The factory used to create the item instance
     */
    protected ItemConfig(ItemRegistry registry, String itemName, ItemConfig.ItemFactory<I, C> itemFactory) {
        this.itemKey = registry.key(itemName);
        this.properties = new Item.Properties().setId(this.itemKey);
        this.itemFactory = itemFactory;
        this.registry = registry;
    }

    /**
     * Called before the item is built to allow subclasses to perform any final configuration.
     * This method is called automatically by {@link #build()} and should be implemented
     * by subclasses to set up any last-minute properties or validations.
     */
    abstract protected void beforeBuild();

    /**
     * Builds the item instance using the configured properties.
     * This method calls {@link #beforeBuild()} before creating the item.
     * 
     * @return The created item instance
     */
    @SuppressWarnings("unchecked")
    public I build() {
        this.beforeBuild();
        return itemFactory.createItem((C) this);
    }

    /**
     * Builds the item and automatically registers it with the item registry.
     * This is a convenience method that combines {@link #build()} and registration.
     * 
     * @return The created and registered item instance
     */
    public I buildAndRegister() {
        I item = this.build();
        this.registry.register(this.itemKey, item, tags);
        return item;
    }

    // **********************************************************************
    // Handle Tags
    
    /**
     * Sets the tags that should be applied to this item.
     * 
     * @param itemTags The tags to apply to the item
     * @return This configuration instance for method chaining
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final C setTags(TagKey<Item>... itemTags) {
        this.tags = itemTags;
        return (C) this;
    }

    /**
     * Gets the currently configured tags for this item.
     * 
     * @return Array of tags applied to this item, may be null
     */
    public TagKey<Item>[] getTags() {
        return this.tags;
    }


    // **********************************************************************
    // Redirect all (but setId) Item.Properties methods to this.properties

    /**
     * Sets the item that this item converts to when used in crafting.
     * 
     * @param convertToItem The item to convert to
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public C usingConvertsTo(Item convertToItem) {
        this.properties.usingConvertsTo(convertToItem);
        return (C) this;
    }

    /**
     * Sets the cooldown duration for this item when used.
     * 
     * @param cooldownSeconds The cooldown duration in seconds
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public C useCooldown(float cooldownSeconds) {
        this.properties.useCooldown(cooldownSeconds);
        return (C) this;
    }

    /**
     * Sets the maximum stack size for this item.
     * 
     * @param maxStackSize The maximum number of items that can be stacked (1-64)
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public C stacksTo(int maxStackSize) {
        this.properties.stacksTo(maxStackSize);
        return (C) this;
    }

    /**
     * Sets the durability (maximum damage) for this item.
     * Items with durability can be damaged and repaired.
     * 
     * @param maxDurability The maximum durability value
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public C durability(int maxDurability) {
        this.properties.durability(maxDurability);
        return (C) this;
    }

    /**
     * Sets the item that remains in the crafting grid after this item is used in a recipe.
     * 
     * @param remainderItem The item to leave behind after crafting
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public C craftRemainder(Item remainderItem) {
        this.properties.craftRemainder(remainderItem);
        return (C) this;
    }

    /**
     * Sets the rarity of this item, which affects its text color and other display properties.
     * 
     * @param itemRarity The rarity level (COMMON, UNCOMMON, RARE, EPIC)
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public C rarity(Rarity itemRarity) {
        this.properties.rarity(itemRarity);
        return (C) this;
    }

    /**
     * Makes this item immune to fire and lava damage.
     * 
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public C fireResistant() {
        this.properties.fireResistant();
        return (C) this;
    }

    /**
     * Makes this item playable in a jukebox.
     * 
     * @param songKey The resource key for the jukebox song
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public C jukeboxPlayable(ResourceKey<JukeboxSong> songKey) {
        this.properties.jukeboxPlayable(songKey);
        return (C) this;
    }

    /**
     * Sets the enchantability value for this item.
     * Higher values make the item more likely to receive better enchantments.
     * 
     * @param enchantability The enchantability value
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public C enchantable(int enchantability) {
        this.properties.enchantable(enchantability);
        return (C) this;
    }

    /**
     * Sets an item that can be used to repair this item.
     * 
     * @param repairItem The item that can repair this item
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public C repairable(Item repairItem) {
        this.properties.repairable(repairItem);
        return (C) this;
    }

    /**
     * Sets a tag of items that can be used to repair this item.
     * 
     * @param repairTag The tag containing items that can repair this item
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public C repairable(TagKey<Item> repairTag) {
        this.properties.repairable(repairTag);
        return (C) this;
    }

    /**
     * Makes this item equippable in the specified equipment slot.
     * 
     * @param slot The equipment slot where this item can be equipped
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public C equippable(EquipmentSlot slot) {
        this.properties.equippable(slot);
        return (C) this;
    }

    /**
     * Makes this item equippable in the specified equipment slot, but prevents swapping with other items.
     * 
     * @param slot The equipment slot where this item can be equipped
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public C equippableUnswappable(EquipmentSlot slot) {
        this.properties.equippableUnswappable(slot);
        return (C) this;
    }

    /**
     * Sets the required feature flags for this item to be available.
     * 
     * @param requiredFlags The feature flags required for this item
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public C requiredFeatures(FeatureFlag... requiredFlags) {
        this.properties.requiredFeatures(requiredFlags);
        return (C) this;
    }

    /**
     * Overrides the description key for this item.
     * 
     * @param descriptionKey The custom description key
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public C overrideDescription(String descriptionKey) {
        this.properties.overrideDescription(descriptionKey);
        return (C) this;
    }

    /**
     * Uses the block description prefix for this item's translation key.
     * 
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public C useBlockDescriptionPrefix() {
        this.properties.useBlockDescriptionPrefix();
        return (C) this;
    }

    /**
     * Uses the item description prefix for this item's translation key.
     * 
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public C useItemDescriptionPrefix() {
        this.properties.useItemDescriptionPrefix();
        return (C) this;
    }

    /**
     * Gets the effective model location for this item.
     * 
     * @return The resource location of the item's model
     */
    public ResourceLocation effectiveModel() {
        return this.properties.effectiveModel();
    }

    /**
     * Adds a data component to this item.
     * 
     * @param <T> The type of the component data
     * @param componentType The type of data component to add
     * @param componentData The data for the component
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T> C component(DataComponentType<T> componentType, T componentData) {
        this.properties.component(componentType, componentData);
        return (C) this;
    }

    /**
     * Sets the attribute modifiers for this item.
     * 
     * @param attributeModifiers The attribute modifiers to apply
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public C attributes(ItemAttributeModifiers attributeModifiers) {
        this.properties.attributes(attributeModifiers);
        return (C) this;
    }

    /**
     * Gets the underlying Item.Properties object used by this configuration.
     * This provides direct access to the properties for advanced configuration scenarios.
     * 
     * @return The Item.Properties instance containing all configured properties
     */
    public Item.Properties getProperties() {
        return this.properties;
    }
}
