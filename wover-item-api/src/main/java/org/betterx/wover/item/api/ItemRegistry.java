package org.betterx.wover.item.api;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.item.api.smithing.SmithingTemplates;
import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;

import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

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

    public <I extends Item> ArmorItemConfig<I> defineToolItem(
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

    public static final DefaultDispenseItemBehavior DISPENSE_SPAWN_EGG_BEHAVIOUR = new DefaultDispenseItemBehavior() {
        @Override
        public @NotNull ItemStack execute(BlockSource blockSource, ItemStack stack) {
            Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
            EntityType<?> entityType = ((SpawnEggItem) stack.getItem()).getType(
                    blockSource.level().registryAccess(),
                    stack
            );

            try {
                entityType.spawn(
                        blockSource.level(),
                        stack,
                        null,
                        blockSource.pos().relative(direction),
                        EntitySpawnReason.DISPENSER,
                        direction != Direction.UP,
                        false
                );
            } catch (Exception var6) {
                LOGGER.error("Error while dispensing spawn egg from dispenser at {}", blockSource.pos(), var6);
                return ItemStack.EMPTY;
            }

            stack.shrink(1);
            blockSource.level().gameEvent(null, GameEvent.ENTITY_PLACE, blockSource.pos());
            return stack;
        }
    };


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
