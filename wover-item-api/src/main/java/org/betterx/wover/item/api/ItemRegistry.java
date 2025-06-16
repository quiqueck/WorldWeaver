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

public class ItemRegistry {
    private static final Map<ModCore, ItemRegistry> REGISTRIES = new HashMap<>();
    public final ModCore C;
    private final Map<ResourceLocation, Item> items = new HashMap<>();
    private Map<Item, TagKey<Item>[]> datagenTags;

    private ItemRegistry(ModCore modeCore) {
        this.C = modeCore;

        if (ModCore.isDatagen()) {
            datagenTags = new HashMap<>();
        }
    }

    public static Stream<ItemRegistry> streamAll() {
        return REGISTRIES.values().stream();
    }

    public static ItemRegistry forMod(ModCore modCore) {
        return REGISTRIES.computeIfAbsent(modCore, c -> new ItemRegistry(modCore));
    }

    public Stream<Item> allItems() {
        return items.values().stream();
    }


    public ResourceKey<Item> key(String name) {
        return ResourceKey.create(BuiltInRegistries.ITEM.key(), C.mk(name));
    }

    public VanillaItemConfig defineDefaultItem(String name) {
        return new VanillaItemConfig(this, name);
    }

    public <I extends Item> DefaultItemConfig<I> defineDefaultItem(
            String name,
            DefaultItemConfig.ItemFactory<I> itemFactory
    ) {
        return new DefaultItemConfig<>(this, name, itemFactory);
    }

    public <I extends Item> ToolItemConfig<I> defineToolItem(
            String name,
            ToolItemConfig.ItemFactory<I> itemFactory
    ) {
        return new ToolItemConfig<>(this, name, itemFactory);
    }

    /**
     * Creates a configuration for an armor item.
     *
     * @param name        The name identifier for the armor item
     * @param itemFactory The factory used to create the armor item instance
     * @param <I>         The type of armor item to create
     * @return A new ArmorItemConfig instance for method chaining
     */
    public <I extends Item> ArmorItemConfig<I> defineArmorItem(
            String name,
            ArmorItemConfig.ItemFactory<I> itemFactory
    ) {
        return new ArmorItemConfig<>(this, name, itemFactory);
    }

    public <I extends Item> FoodItemConfig<I> defineFoodItem(
            String name,
            FoodItemConfig.ItemFactory<I> itemFactory
    ) {
        return new FoodItemConfig<>(this, name, itemFactory);
    }

    public <I extends Item> DrinkItemConfig<I> defineDrinkItem(
            String name,
            FoodItemConfig.ItemFactory<I> itemFactory
    ) {
        return new DrinkItemConfig<>(this, name, itemFactory);
    }

    /**
     * Creates a configuration for a spawn egg item.
     *
     * @param name        The name identifier for the spawn egg
     * @param itemFactory The factory used to create the spawn egg item instance
     * @param <I>         The type of spawn egg item to create
     * @return A new SpawnEggConfig instance for method chaining
     */
    public <I extends SpawnEggItem> SpawnEggConfig<I> defineSpawnEgg(
            String name,
            SpawnEggConfig.ItemFactory<I> itemFactory
    ) {
        return new SpawnEggConfig<>(this, name, itemFactory);
    }


    <T extends Item> void register(ResourceKey<Item> key, T item, TagKey<Item>[] tags) {
        if (item != null && item != Items.AIR) {
            Registry.register(BuiltInRegistries.ITEM, key, item);
            items.put(key.location(), item);

            if (datagenTags != null && tags != null && tags.length > 0) datagenTags.put(item, tags);
        }
    }

    public <T extends Item> T register(String path, T item, TagKey<Item>... tags) {
        register(key(path), item, tags);
        return item;
    }

    @Deprecated(forRemoval = true)
    public <T extends Item> T registerAsTool(String path, T item, TagKey<Item>... tags) {
        return register(path, item, tags);
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
        DispenserBlock.registerBehavior(item, DISPENSE_SPAWN_EGG_BEHAVIOUR);
        return register(path, item, tags);
    }

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

    public <T extends SmithingTemplateItem> T registerSmithingTemplateItem(
            String path,
            T item
    ) {
        register(path, item);
        return item;
    }

    @Deprecated(forRemoval = true)
    public Item.Properties createDefaultItemSettings() {
        return new Item.Properties();
    }

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
