package org.betterx.wover.item.api;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.item.api.smithing.SmithingTemplates;
import org.betterx.wover.item.impl.ItemRegistryImpl;
import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.DispenserBlock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
 * <pre class="java">
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
 * </pre>
 *
 * @author Quiqueck
 * @see ItemDefinition
 * @see ModCore
 * @since 21.6.0
 */
public abstract class ItemRegistry {
    /**
     * The mod core this registry belongs to
     */
    public final ModCore C;

    /**
     * Map of all registered items by their resource location
     */
    private final Map<ResourceKey<Item>, Item> items = new HashMap<>();

    /**
     * Map of items to their tags for data generation (only populated during datagen)
     */
    private Map<Item, TagKey<Item>[]> datagenTags;

    /**
     * Creates a new item registry for the specified mod core.
     * Private constructor to ensure controlled creation through {@link #forMod(ModCore)}.
     *
     * @param modCore The mod core this registry will belong to
     */
    protected ItemRegistry(ModCore modCore) {
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
        return ItemRegistryImpl.streamAll();
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
        return ItemRegistryImpl.forMod(modCore);
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
     * Returns a stream of all items registered through this registry.
     * This is useful for operations that need to iterate over all items
     * belonging to a specific mod.
     *
     * @return Stream of all registered Item instances
     */
    public Stream<Map.Entry<ResourceKey<Item>, Item>> allEntries() {
        return items.entrySet().stream();
    }

    /**
     * Creates a resource key for an item with the given name.
     * The resource key uses this registry's mod namespace.
     *
     * @param itemName The name identifier for the item
     * @return A ResourceKey for the item in this mod's namespace
     */
    public @NotNull ResourceKey<Item> key(@NotNull String itemName) {
        return ResourceKey.create(BuiltInRegistries.ITEM.key(), C.mk(itemName));
    }

    /**
     * Creates a resource key for an item Entity with the given name.
     *
     * @param itemKey The key for the item
     * @return A ResourceKey for the item in this mod's namespace
     */
    public @NotNull ResourceKey<EntityType<?>> entityKey(@NotNull ResourceKey<Item> itemKey) {
        return ResourceKey.create(Registries.ENTITY_TYPE, itemKey.location());
    }

    /**
     * Creates a resource key for an item Entity with the given name.
     *
     * @param itemName The name identifier for the item
     * @return A ResourceKey for the item in this mod's namespace
     */
    public @NotNull ResourceKey<EntityType<?>> entityKey(@NotNull String itemName) {
        return ResourceKey.create(Registries.ENTITY_TYPE, C.mk(itemName));
    }

    /**
     * Creates a configuration for a vanilla-style item.
     * This is a convenience method for creating basic items that behave exactly like
     * vanilla Minecraft items without any custom behavior.
     *
     * @param itemName The name identifier for the vanilla item
     * @return A new VanillaItemDefinition instance for method chaining
     */
    public VanillaItemDefinition defineDefaultItem(String itemName) {
        return new VanillaItemDefinition(this, itemName);
    }

    /**
     * Creates a configuration for a default item with a custom factory.
     * This allows for creating custom item types while still using the default
     * configuration pattern.
     *
     * @param itemName    The name identifier for the item
     * @param itemFactory The factory used to create the item instance
     * @param <I>         The type of item to create
     * @return A new DefaultItemDefinition instance for method chaining
     */
    public <I extends Item> DefaultItemDefinition<I> defineDefaultItem(
            String itemName,
            DefaultItemDefinition.ItemFactory<I> itemFactory
    ) {
        return new DefaultItemDefinition<>(this, itemName, itemFactory);
    }

    /**
     * Creates a configuration for a default item with a custom factory.
     * This allows for creating custom item types while still using the default
     * configuration pattern.
     *
     * @param itemName    The name identifier for the item
     * @param itemFactory The factory used to create the item instance
     * @param <I>         The type of item to create
     * @return A new DefaultItemDefinition instance for method chaining
     */
    public <I extends Item> DefaultItemDefinition<I> defineDefaultItem(
            String itemName,
            Function<Item.Properties, I> itemFactory
    ) {
        return new DefaultItemDefinition<>(this, itemName, (def) -> itemFactory.apply(def.getProperties()));
    }

    /**
     * Creates a configuration for a tool item.
     * This allows for creating various types of tools including pickaxes, axes,
     * shovels, hoes, swords, and custom tools with specific block effectiveness.
     *
     * @param toolName    The name identifier for the tool item
     * @param itemFactory The factory used to create the tool item instance
     * @param <I>         The type of tool item to create
     * @return A new ToolItemDefinition instance for method chaining
     */
    public <I extends Item> ToolItemDefinition<I> defineToolItem(
            String toolName,
            ToolItemDefinition.ItemFactory<I> itemFactory
    ) {
        return new ToolItemDefinition<>(this, toolName, itemFactory);
    }

    /**
     * Creates a configuration for an armor item.
     * This allows for creating various types of armor including humanoid armor,
     * wolf armor, horse armor, and custom armor with trim support.
     *
     * @param armorName   The name identifier for the armor item
     * @param itemFactory The factory used to create the armor item instance
     * @param <I>         The type of armor item to create
     * @return A new ArmorItemDefinition instance for method chaining
     */
    public <I extends Item> ArmorItemDefinition<I> defineArmorItem(
            String armorName,
            ArmorItemDefinition.ItemFactory<I> itemFactory
    ) {
        return new ArmorItemDefinition<>(this, armorName, itemFactory);
    }

    /**
     * Creates a configuration for a food item.
     * This allows for creating consumable food items with nutrition values,
     * saturation, status effects, consumption animations, and sounds.
     *
     * @param foodName    The name identifier for the food item
     * @param itemFactory The factory used to create the food item instance
     * @param <I>         The type of food item to create
     * @return A new FoodItemDefinition instance for method chaining
     */
    public <I extends Item> FoodItemDefinition<I> defineFoodItem(
            String foodName,
            FoodItemDefinition.ItemFactory<I> itemFactory
    ) {
        return new FoodItemDefinition<>(this, foodName, itemFactory);
    }

    /**
     * Creates a configuration for a drink item.
     * This is a specialized food item that uses drink-specific animations,
     * sounds, and consumption behavior instead of eating behavior.
     *
     * @param drinkName   The name identifier for the drink item
     * @param itemFactory The factory used to create the drink item instance
     * @param <I>         The type of drink item to create
     * @return A new DrinkItemDefinition instance for method chaining
     */
    public <I extends Item> DrinkItemDefinition<I> defineDrinkItem(
            String drinkName,
            FoodItemDefinition.ItemFactory<I> itemFactory
    ) {
        return new DrinkItemDefinition<>(this, drinkName, itemFactory);
    }

    /**
     * Creates a configuration for a spawn egg item.
     * This allows for creating spawn eggs with custom entity types, colors,
     * and automatic dispenser behavior registration.
     *
     * @param eggName     The name identifier for the spawn egg
     * @param itemFactory The factory used to create the spawn egg item instance
     * @param <I>         The type of spawn egg item to create
     * @return A new SpawnEggDefinition instance for method chaining
     */
    public <I extends SpawnEggItem> SpawnEggDefinition<I> defineSpawnEgg(
            String eggName,
            SpawnEggDefinition.ItemFactory<I> itemFactory
    ) {
        return new SpawnEggDefinition<>(this, eggName, itemFactory);
    }

    /**
     * Creates a configuration for a smithing template item.
     * This allows for creating smithing templates with custom slot icons,
     * descriptions, and template paths for use in smithing tables.
     *
     * @param templateName The name identifier for the smithing template
     * @param itemFactory  The factory used to create the smithing template item instance
     * @param <I>          The type of smithing template item to create
     * @return A new SmithingTemplateDefinition instance for method chaining
     */
    public <I extends SmithingTemplateItem> SmithingTemplateDefinition<I> defineSmithingTemplate(
            String templateName,
            SmithingTemplateDefinition.ItemFactory<I> itemFactory
    ) {
        return new SmithingTemplateDefinition<>(this, templateName, itemFactory);
    }

    /**
     * Creates a configuration for a boat item.
     * This allows for creating custom boat items with specific properties and behaviors.
     *
     * @param boatName    The name identifier for the boat item
     * @param itemFactory The factory used to create the boat item instance
     * @param <I>         The type of boat item to create
     * @return A new BoatItemDefinition instance for method chaining
     */
    public <I extends BoatItem> BoatItemDefinition<I> defineBoatItem(
            String boatName,
            BoatItemDefinition.ItemFactory<I> itemFactory
    ) {
        return new BoatItemDefinition<>(this, boatName, itemFactory);
    }

    /**
     * Creates a configuration for a boat item with default properties.
     * This is a convenience method that uses the default boat item factory
     * to create a boat item with standard properties.
     *
     * @param boatName The name identifier for the boat item
     * @return A new BoatItemDefinition instance for method chaining
     */
    public BoatItemDefinition<BoatItem> defineBoatItem(String boatName) {
        return new BoatItemDefinition<>(this, boatName, (def) -> new BoatItem(def.entityType(), def.getProperties()));
    }

    /**
     * Internal method used by item configurations to register items with the Minecraft registry.
     * This method handles the actual registration with both the game registry and internal tracking,
     * and stores tags for data generation if applicable.
     *
     * @param key  The resource key for the item
     * @param item The item instance to register
     * @param tags Optional tags to apply to the item during data generation
     * @param <T>  The type of item being registered
     */
    protected <T extends Item> void register(@NotNull ResourceKey<Item> key, T item, @Nullable TagKey<Item>[] tags) {
        if (item != null && item != Items.AIR) {
            Registry.register(BuiltInRegistries.ITEM, key, item);
            items.put(key, item);

            if (datagenTags != null && tags != null && tags.length > 0) datagenTags.put(item, tags);
        }
    }

    /**
     * Registers an item with the given path and optional tags.
     * This is a convenience method that creates a resource key from the path.
     *
     * @param itemPath The registry path for the item
     * @param item     The item instance to register
     * @param tags     Optional tags to apply to the item
     * @param <T>      The type of item being registered
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
     * @param item     The tool item to register
     * @param tags     Optional tags to apply to the tool
     * @param <T>      The type of tool item
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
     * @deprecated Use {@link #defineSpawnEgg(String, SpawnEggDefinition.ItemFactory)} with
     * {@link SpawnEggDefinition#buildAndRegister()} instead for better configuration options
     */
    @Deprecated(forRemoval = true)
    @SafeVarargs
    public final <T extends SpawnEggItem> T registerEgg(String path, T item, TagKey<Item>... tags) {
        DispenserBlock.registerBehavior(item, SpawnEggDefinition.DISPENSE_SPAWN_EGG_BEHAVIOUR);
        return register(path, item, tags);
    }

    /**
     * Registers a smithing template item with the specified slot icons.
     *
     * @param path                     The registry path for the smithing template
     * @param baseSlotEmptyIcons       Icons for base slot when empty
     * @param additionalSlotEmptyIcons Icons for additional slot when empty
     * @return The registered smithing template item
     * @deprecated Use {@link #defineSmithingTemplate(String, SmithingTemplateDefinition.ItemFactory)} with
     * {@link SmithingTemplateDefinition#buildAndRegister()} instead for better configuration options
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
     * @deprecated Use {@link #defineSmithingTemplate(String, SmithingTemplateDefinition.ItemFactory)} with
     * {@link SmithingTemplateDefinition#buildAndRegister()} instead for better configuration options
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
                .forEach(i -> ((ItemTagProvider) i.getValue()).registerItemTags(i.getKey().location(), ctx));
    }
}
