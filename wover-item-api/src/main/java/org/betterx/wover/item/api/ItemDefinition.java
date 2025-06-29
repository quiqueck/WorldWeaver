package org.betterx.wover.item.api;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.item.api.trait.ItemTrait;
import org.betterx.wover.item.api.trait.ItemWithTraits;
import org.betterx.wover.util.GrowableArray;

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

import java.util.LinkedList;
import java.util.List;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base class for configuring and building Minecraft items with a fluent API.
 * This class provides a builder pattern for item creation with support for tags, properties,
 * and automatic registration.
 *
 * @param <I> The type of item being created, must extend {@link Item}
 * @param <D> The concrete configuration class type for method chaining
 * @author Quiqueck
 * @since 21.6.0
 */
public abstract class ItemDefinition<I extends Item, D extends ItemDefinition<I, D>> {
    /**
     * Factory interface for creating items from configuration objects.
     *
     * @param <I> The type of item to create
     * @param <D> The configuration type used to create the item
     */
    public interface ItemFactory<I extends Item, D extends ItemDefinition<I, D>> {
        /**
         * Creates an item instance from the given configuration.
         *
         * @param definition The configuration object containing all item settings
         * @return The created item instance
         */
        I createItem(D definition);
    }

    /**
     * The item registry used for registering the item
     */
    public final ItemRegistry registry;

    /**
     * The resource key identifying this item
     */
    public final ResourceKey<Item> itemKey;

    /**
     * The properties configuration for the item
     */
    protected final Item.Properties properties;

    /**
     * Optional tags to be applied to the item
     */
    protected GrowableArray<TagKey<Item>> tags;

    /**
     * List of traits applied to this item.
     * Each trait is configured with its own configuration object.
     */
    protected List<ItemTrait<? super I, ?>> traits;

    /**
     * Factory instance used to create the item
     */
    protected final ItemDefinition.ItemFactory<I, D> itemFactory;

    /**
     * Creates a new item configuration.
     *
     * @param registry    The item registry to use for registration
     * @param itemName    The name identifier for the item
     * @param itemFactory The factory used to create the item instance
     */
    protected ItemDefinition(ItemRegistry registry, String itemName, ItemDefinition.ItemFactory<I, D> itemFactory) {
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
     * Called before the item is registered to allow subclasses to perform any final modifications.
     * This method is called automatically by {@link #buildAndRegister()} after the item is built
     * but before it is registered with the registry. Subclasses can use this to perform any
     * post-creation setup or modifications that need to happen before registration.
     *
     * @param item The built item instance that will be registered
     * @return The item instance (potentially modified) that should be registered
     */
    abstract protected I beforeRegister(I item);

    /**
     * Builds the item instance using the configured properties.
     * This method calls {@link #beforeBuild()} before creating the item.
     *
     * @return The created item instance
     */
    @SuppressWarnings("unchecked")
    public final I build() {
        this.beforeBuild();

        final List<ItemTrait.RuntimeTrait<I, ?>> runtimeTraits;

        // If traits are defined, configure them and collect RuntimeTraits
        if (this.traits != null && !this.traits.isEmpty()) {
            runtimeTraits = new LinkedList<>();
            for (var configuredTrait : this.traits) {
                this.configurePropertiesUnchecked(configuredTrait);

                final ItemTrait.RuntimeTrait<I, ?> runtimeTrait = this.forRuntimeUnchecked(configuredTrait);
                if (runtimeTrait != null) runtimeTraits.add(runtimeTrait);
            }
        } else runtimeTraits = null;

        I item = itemFactory.createItem((D) this);

        // If runtime traits were collected, set them on the item
        if (runtimeTraits != null && !runtimeTraits.isEmpty() && item instanceof ItemWithTraits<?>) {
            ((ItemWithTraits<I>) item).wover_setItemTraits(runtimeTraits);
        }

        return item;
    }

    /**
     * Builds the item and automatically registers it with the item registry.
     * This is a convenience method that combines {@link #build()}, {@link #beforeRegister(I)},
     * and registration. The process is: build item → call beforeRegister → register with registry.
     *
     * @return The created and registered item instance
     */
    public final I buildAndRegister() {
        I item = this.beforeRegister(this.build());
        this.registry.register(this.itemKey, item, tags == null ? null : tags.elements());

        // If traits are defined, call afterItemRegistration for each trait
        if (this.traits != null) {
            for (var trait : this.traits) {

                this.afterItemRegistrationUnchecked(item, trait);
            }
        }

        return item;
    }

    /**
     * Adds a trait to this item definition.
     * Traits are used to add additional behaviors or properties to the item.
     *
     * @param trait The trait to add
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T extends ItemTrait<? super I, ?>> D addTrait(
            @Nullable T trait
    ) {
        if (trait == null) {
            // Skip null traits
            return (D) this;
        }

        if (trait.clientOnly() && !ModCore.isClient()) {
            // Skip traits that are only for the client side
            return (D) this;
        }
        if (trait.datagenOnly() && !ModCore.isDatagen()) {
            // Skip traits that are only for data generation
            return (D) this;
        }


        if (this.traits == null) this.traits = new LinkedList<>();

        this.traits.add(trait);
        return (D) this;
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
    public final D addTags(TagKey<Item>... itemTags) {
        if (this.tags == null) {
            this.tags = new GrowableArray<>(itemTags);
        } else {
            this.tags.add(itemTags);
        }
        return (D) this;
    }

    /**
     * Gets the currently configured tags for this item.
     *
     * @return Array of tags applied to this item, may be null
     */
    public TagKey<Item>[] tags() {
        return this.tags.elements();
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
    public D usingConvertsTo(Item convertToItem) {
        this.properties.usingConvertsTo(convertToItem);
        return (D) this;
    }

    /**
     * Sets the cooldown duration for this item when used.
     *
     * @param cooldownSeconds The cooldown duration in seconds
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D useCooldown(float cooldownSeconds) {
        this.properties.useCooldown(cooldownSeconds);
        return (D) this;
    }

    /**
     * Sets the maximum stack size for this item.
     *
     * @param maxStackSize The maximum number of items that can be stacked (1-64)
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D stacksTo(int maxStackSize) {
        this.properties.stacksTo(maxStackSize);
        return (D) this;
    }

    /**
     * Sets the durability (maximum damage) for this item.
     * Items with durability can be damaged and repaired.
     *
     * @param maxDurability The maximum durability value
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D durability(int maxDurability) {
        this.properties.durability(maxDurability);
        return (D) this;
    }

    /**
     * Sets the item that remains in the crafting grid after this item is used in a recipe.
     *
     * @param remainderItem The item to leave behind after crafting
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D craftRemainder(Item remainderItem) {
        this.properties.craftRemainder(remainderItem);
        return (D) this;
    }

    /**
     * Sets the rarity of this item, which affects its text color and other display properties.
     *
     * @param itemRarity The rarity level (COMMON, UNCOMMON, RARE, EPIC)
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D rarity(Rarity itemRarity) {
        this.properties.rarity(itemRarity);
        return (D) this;
    }

    /**
     * Makes this item immune to fire and lava damage.
     *
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D fireResistant() {
        this.properties.fireResistant();
        return (D) this;
    }

    /**
     * Makes this item playable in a jukebox.
     *
     * @param songKey The resource key for the jukebox song
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D jukeboxPlayable(ResourceKey<JukeboxSong> songKey) {
        this.properties.jukeboxPlayable(songKey);
        return (D) this;
    }

    /**
     * Sets the enchantability value for this item.
     * Higher values make the item more likely to receive better enchantments.
     *
     * @param enchantability The enchantability value
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D enchantable(int enchantability) {
        this.properties.enchantable(enchantability);
        return (D) this;
    }

    /**
     * Sets an item that can be used to repair this item.
     *
     * @param repairItem The item that can repair this item
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D repairable(Item repairItem) {
        this.properties.repairable(repairItem);
        return (D) this;
    }

    /**
     * Sets a tag of items that can be used to repair this item.
     *
     * @param repairTag The tag containing items that can repair this item
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D repairable(TagKey<Item> repairTag) {
        this.properties.repairable(repairTag);
        return (D) this;
    }

    /**
     * Makes this item equippable in the specified equipment slot.
     *
     * @param slot The equipment slot where this item can be equipped
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D equippable(EquipmentSlot slot) {
        this.properties.equippable(slot);
        return (D) this;
    }

    /**
     * Makes this item equippable in the specified equipment slot, but prevents swapping with other items.
     *
     * @param slot The equipment slot where this item can be equipped
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D equippableUnswappable(EquipmentSlot slot) {
        this.properties.equippableUnswappable(slot);
        return (D) this;
    }

    /**
     * Sets the required feature flags for this item to be available.
     *
     * @param requiredFlags The feature flags required for this item
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D requiredFeatures(FeatureFlag... requiredFlags) {
        this.properties.requiredFeatures(requiredFlags);
        return (D) this;
    }

    /**
     * Overrides the description key for this item.
     *
     * @param descriptionKey The custom description key
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D overrideDescription(String descriptionKey) {
        this.properties.overrideDescription(descriptionKey);
        return (D) this;
    }

    /**
     * Uses the block description prefix for this item's translation key.
     *
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D useBlockDescriptionPrefix() {
        this.properties.useBlockDescriptionPrefix();
        return (D) this;
    }

    /**
     * Uses the item description prefix for this item's translation key.
     *
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D useItemDescriptionPrefix() {
        this.properties.useItemDescriptionPrefix();
        return (D) this;
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
     * @param <T>           The type of the component data
     * @param componentType The type of data component to add
     * @param componentData The data for the component
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public <T> D component(DataComponentType<T> componentType, T componentData) {
        this.properties.component(componentType, componentData);
        return (D) this;
    }

    /**
     * Sets the attribute modifiers for this item.
     *
     * @param attributeModifiers The attribute modifiers to apply
     * @return This configuration instance for method chaining
     */
    @SuppressWarnings("unchecked")
    public D attributes(ItemAttributeModifiers attributeModifiers) {
        this.properties.attributes(attributeModifiers);
        return (D) this;
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


    // Helper methods to handle generic type casting
    @SuppressWarnings("unchecked")
    private void configurePropertiesUnchecked(
            ItemTrait<? super I, ?> trait
    ) {
        ((ItemTrait<I, ?>) trait).configure((D) this);
    }

    @SuppressWarnings("unchecked")
    private ItemTrait.RuntimeTrait<I, ?> forRuntimeUnchecked(
            ItemTrait<? super I, ?> trait
    ) {
        // Cast is safe because the trait can work with B (since B extends the super type)
        return (ItemTrait.RuntimeTrait<I, ?>) trait.forRuntime();
    }

    @SuppressWarnings("unchecked")
    private void afterItemRegistrationUnchecked(
            I item,
            ItemTrait<? super I, ?> trait
    ) {
        // Cast is safe because the trait can work with B (since B extends the super type)
        ((ItemTrait<I, ?>) trait).afterItemRegistration(item, (D) this);
    }
}
