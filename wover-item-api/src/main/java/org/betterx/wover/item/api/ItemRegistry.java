package org.betterx.wover.item.api;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.item.api.smithing.SmithingTemplates;
import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.DispenserBlock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Central registry for managing item registration and configuration within a mod.
 * This class provides a fluent API for creating, configuring, and registering various types
 * of Minecraft items including basic items, tools, armor, food, spawn eggs, and smithing templates.
 * 
 * <p>The ItemRegistry follows a builder pattern approach where items are configured through
 * specialized configuration classes before being built and registered. Each mod gets its own
 * registry instance that handles:</p>
 * <ul>
 *   <li>Item creation through type-specific configuration builders</li>
 *   <li>Automatic registration with Minecraft's built-in registries</li>
 *   <li>Tag management for data generation</li>
 *   <li>Special behavior registration (e.g., dispenser behaviors for spawn eggs)</li>
 * </ul>
 * 
 * <p>Usage example:</p>
 * <pre>{@code
 * ItemRegistry registry = ItemRegistry.forMod(myMod);
 * 
 * // Create a basic item
 * Item basicItem = registry.defineDefaultItem("my_item", MyItem::new)
 *     .stacksTo(16)
 *     .rarity(Rarity.UNCOMMON)
 *     .buildAndRegister();
 *     
 * // Create a food item
 * Item apple = registry.defineFoodItem("magic_apple", MyFoodItem::new)
 *     .nutrition(4)
 *     .saturationModifier(0.3f)
 *     .setEffects(new MobEffectInstance(MobEffects.REGENERATION, 100))
 *     .buildAndRegister();
 * }</pre>
 * 
 * @author Quiqueck
 * @since 21.6.0
 * @see ItemConfig
 * @see ModCore
 */
public class ItemRegistry {
    /** Global registry mapping mod cores to their respective item registries */
    private static final Map<ModCore, ItemRegistry> REGISTRIES = new HashMap<>();
    
    /** The mod core this registry belongs to */
    public final ModCore C;
    
    /** Map of all registered items by their resource location */
    private final Map<ResourceLocation, Item> items = new HashMap<>();
    
    /** Map of items to their tags for data generation (only populated during datagen) */
    private Map<Item, TagKey<Item>[]> datagenTags;

    /**
     * Creates a new item registry for the specified mod core.
     * Private constructor to ensure controlled creation through {@link #forMod(ModCore)}.
     * 
     * @param modCore The mod core this registry will belong to
     */
    private ItemRegistry(ModCore modCore) {
        this.C = modCore;

        if (ModCore.isDatagen()) {
            datagenTags = new HashMap<>();
        }
    }

    /**
     * Returns a stream of all existing item registries.
     * This is useful for operations that need to work across all registered mods.
     * 
     * @return Stream of all ItemRegistry instances
     */
    public static Stream<ItemRegistry> streamAll() {
        return REGISTRIES.values().stream();
    }

    /**
     * Gets or creates an item registry for the specified mod core.
     * This is the primary way to obtain an ItemRegistry instance. Each mod core
     * gets exactly one registry instance that is reused across multiple calls.
     * 
     * @param modCore The mod core to get/create a registry for
     * @return The ItemRegistry instance for the specified mod core
     */
    public static ItemRegistry forMod(ModCore modCore) {
        return REGISTRIES.computeIfAbsent(modCore, c -> new ItemRegistry(modCore));
    }

    /**
     * Returns a stream of all items registered through this registry.
     * This is useful for operations that need to iterate over all items
     * belonging to a specific mod.
     * 
     * @return Stream of all registered Item instances
     */
    public Stream<Item> allItems() {
        return items.values().stream();
    }

    /**
     * Creates a resource key for an item with the given name.
     * The resource key uses this registry's mod namespace.
     * 
     * @param itemName The name identifier for the item
     * @return A ResourceKey for the item in this mod's namespace
     */
    public ResourceKey<Item> key(String itemName) {
        return ResourceKey.create(BuiltInRegistries.ITEM.key(), C.mk(itemName));
    }

    /**
     * Creates a configuration for a vanilla-style item.
     * This is a convenience method for creating basic items that behave exactly like
     * vanilla Minecraft items without any custom behavior.
     * 
     * @param itemName The name identifier for the vanilla item
     * @return A new VanillaItemConfig instance for method chaining
     */
    public VanillaItemConfig defineDefaultItem(String itemName) {
        return new VanillaItemConfig(this, itemName);
    }

    /**
     * Creates a configuration for a default item with a custom factory.
     * This allows for creating custom item types while still using the default
     * configuration pattern.
     * 
     * @param itemName The name identifier for the item
     * @param itemFactory The factory used to create the item instance
     * @param <I> The type of item to create
     * @return A new DefaultItemConfig instance for method chaining
     */
    public <I extends Item> DefaultItemConfig<I> defineDefaultItem(
            String itemName,
            DefaultItemConfig.ItemFactory<I> itemFactory
    ) {
        return new DefaultItemConfig<>(this, itemName, itemFactory);
    }

    /**
     * Creates a configuration for a tool item.
     * This allows for creating various types of tools including pickaxes, axes,
     * shovels, hoes, swords, and custom tools with specific block effectiveness.
     * 
     * @param toolName The name identifier for the tool item
     * @param itemFactory The factory used to create the tool item instance
     * @param <I> The type of tool item to create
     * @return A new ToolItemConfig instance for method chaining
     */
    public <I extends Item> ToolItemConfig<I> defineToolItem(
            String toolName,
            ToolItemConfig.ItemFactory<I> itemFactory
    ) {
        return new ToolItemConfig<>(this, toolName, itemFactory);
    }

    /**
     * Creates a configuration for an armor item.
     * This allows for creating various types of armor including humanoid armor,
     * wolf armor, horse armor, and custom armor with trim support.
     *
     * @param armorName The name identifier for the armor item
     * @param itemFactory The factory used to create the armor item instance
     * @param <I> The type of armor item to create
     * @return A new ArmorItemConfig instance for method chaining
     */
    public <I extends Item> ArmorItemConfig<I> defineArmorItem(
            String armorName,
            ArmorItemConfig.ItemFactory<I> itemFactory
    ) {
        return new ArmorItemConfig<>(this, armorName, itemFactory);
    }

    /**
     * Creates a configuration for a food item.
     * This allows for creating consumable food items with nutrition values,
     * saturation, status effects, consumption animations, and sounds.
     * 
     * @param foodName The name identifier for the food item
     * @param itemFactory The factory used to create the food item instance
     * @param <I> The type of food item to create
     * @return A new FoodItemConfig instance for method chaining
     */
    public <I extends Item> FoodItemConfig<I> defineFoodItem(
            String foodName,
            FoodItemConfig.ItemFactory<I> itemFactory
    ) {
        return new FoodItemConfig<>(this, foodName, itemFactory);
    }

    /**
     * Creates a configuration for a drink item.
     * This is a specialized food item that uses drink-specific animations,
     * sounds, and consumption behavior instead of eating behavior.
     * 
     * @param drinkName The name identifier for the drink item
     * @param itemFactory The factory used to create the drink item instance
     * @param <I> The type of drink item to create
     * @return A new DrinkItemConfig instance for method chaining
     */
    public <I extends Item> DrinkItemConfig<I> defineDrinkItem(
            String drinkName,
            FoodItemConfig.ItemFactory<I> itemFactory
    ) {
        return new DrinkItemConfig<>(this, drinkName, itemFactory);
    }

    /**
     * Creates a configuration for a spawn egg item.
     * This allows for creating spawn eggs with custom entity types, colors,
     * and automatic dispenser behavior registration.
     *
     * @param eggName The name identifier for the spawn egg
     * @param itemFactory The factory used to create the spawn egg item instance
     * @param <I> The type of spawn egg item to create
     * @return A new SpawnEggConfig instance for method chaining
     */
    public <I extends SpawnEggItem> SpawnEggConfig<I> defineSpawnEgg(
            String eggName,
            SpawnEggConfig.ItemFactory<I> itemFactory
    ) {
        return new SpawnEggConfig<>(this, eggName, itemFactory);
    }

    /**
     * Creates a configuration for a smithing template item.
     * This allows for creating smithing templates with custom slot icons,
     * descriptions, and template paths for use in smithing tables.
     *
     * @param templateName The name identifier for the smithing template
     * @param itemFactory The factory used to create the smithing template item instance
     * @param <I> The type of smithing template item to create
     * @return A new SmithingTemplateConfig instance for method chaining
     */
    public <I extends SmithingTemplateItem> SmithingTemplateConfig<I> defineSmithingTemplate(
            String templateName,
            SmithingTemplateConfig.ItemFactory<I> itemFactory
    ) {
        return new SmithingTemplateConfig<>(this, templateName, itemFactory);
    }

    /**
     * Internal method used by item configurations to register items with the Minecraft registry.
     * This method handles the actual registration with both the game registry and internal tracking,
     * and stores tags for data generation if applicable.
     * 
     * @param key The resource key for the item
     * @param item The item instance to register
     * @param tags Optional tags to apply to the item during data generation
     * @param <T> The type of item being registered
     */
    <T extends Item> void register(ResourceKey<Item> key, T item, TagKey<Item>[] tags) {
        if (item != null && item != Items.AIR) {
            Registry.register(BuiltInRegistries.ITEM, key, item);
            items.put(key.location(), item);

            if (datagenTags != null && tags != null && tags.length > 0) datagenTags.put(item, tags);
        }
    }

    /**
     * Registers an item with the given path and optional tags.
     * This is a convenience method that creates a resource key from the path.
     * 
     * @param itemPath The registry path for the item
     * @param item The item instance to register
     * @param tags Optional tags to apply to the item
     * @param <T> The type of item being registered
     * @return The registered item instance
     */
    public <T extends Item> T register(String itemPath, T item, TagKey<Item>... tags) {
        register(key(itemPath), item, tags);
        return item;
    }

    /**
     * Legacy method for registering tools.
     * 
     * @param itemPath The registry path for the tool
     * @param item The tool item to register
     * @param tags Optional tags to apply to the tool
     * @param <T> The type of tool item
     * @return The registered tool item
     * @deprecated Use {@link #register(String, Item, TagKey[])} instead - tools are registered the same way as other items
     */
    @Deprecated(forRemoval = true)
    public <T extends Item> T registerAsTool(String itemPath, T item, TagKey<Item>... tags) {
        return register(itemPath, item, tags);
    }


    /**
     * Registers a spawn egg item with automatic dispenser behavior.
     *
     * @param path The registry path for the spawn egg
     * @param item The spawn egg item to register
     * @param tags Optional tags to apply to the spawn egg
     * @param <T>  The type of spawn egg item
     * @return The registered spawn egg item
     * @deprecated Use {@link #defineSpawnEgg(String, SpawnEggConfig.ItemFactory)} with
     * {@link SpawnEggConfig#buildAndRegister()} instead for better configuration options
     */
    @Deprecated(forRemoval = true)
    @SafeVarargs
    public final <T extends SpawnEggItem> T registerEgg(String path, T item, TagKey<Item>... tags) {
        DispenserBlock.registerBehavior(item, SpawnEggConfig.DISPENSE_SPAWN_EGG_BEHAVIOUR);
        return register(path, item, tags);
    }

    /**
     * Registers a smithing template item with the specified slot icons.
     *
     * @param path                     The registry path for the smithing template
     * @param baseSlotEmptyIcons       Icons for base slot when empty
     * @param additionalSlotEmptyIcons Icons for additional slot when empty
     * @return The registered smithing template item
     * @deprecated Use {@link #defineSmithingTemplate(String, SmithingTemplateConfig.ItemFactory)} with
     * {@link SmithingTemplateConfig#buildAndRegister()} instead for better configuration options
     */
    @Deprecated(forRemoval = true)
    public SmithingTemplateItem registerSmithingTemplateItem(
            String path,
            List<ResourceLocation> baseSlotEmptyIcons,
            List<ResourceLocation> additionalSlotEmptyIcons
    ) {
        final SmithingTemplateItem item = SmithingTemplates
                .create(C, path)
                .setBaseSlotEmptyIcons(baseSlotEmptyIcons)
                .setAdditionalSlotEmptyIcons(additionalSlotEmptyIcons)
                .build();

        return registerSmithingTemplateItem(path + "_smithing_template", item);
    }

    /**
     * Registers a pre-created smithing template item.
     *
     * @param path The registry path for the smithing template
     * @param item The smithing template item to register
     * @param <T>  The type of smithing template item
     * @return The registered smithing template item
     * @deprecated Use {@link #defineSmithingTemplate(String, SmithingTemplateConfig.ItemFactory)} with
     * {@link SmithingTemplateConfig#buildAndRegister()} instead for better configuration options
     */
    @Deprecated(forRemoval = true)
    public <T extends SmithingTemplateItem> T registerSmithingTemplateItem(
            String path,
            T item
    ) {
        return register(path, item);
    }

    /**
     * Legacy method for creating default item properties.
     * 
     * @return A new Item.Properties instance
     * @deprecated Use {@code new Item.Properties()} directly instead
     */
    @Deprecated(forRemoval = true)
    public Item.Properties createDefaultItemSettings() {
        return new Item.Properties();
    }

    /**
     * Bootstraps item tags for data generation.
     * This method is called during the data generation process to register all
     * item tags that were specified during item configuration. It handles both
     * tags specified through configuration classes and tags from items that
     * implement {@link ItemTagProvider}.
     * 
     * @param ctx The item tag bootstrap context for registering tags
     */
    public void bootstrapItemTags(ItemTagBootstrapContext ctx) {
        if (datagenTags != null) {
            datagenTags.forEach(ctx::add);
        }
        items
                .entrySet()
                .stream()
                .filter(i -> i.getValue() instanceof ItemTagProvider)
                .forEach(i -> ((ItemTagProvider) i.getValue()).registerItemTags(i.getKey(), ctx));
    }
}
