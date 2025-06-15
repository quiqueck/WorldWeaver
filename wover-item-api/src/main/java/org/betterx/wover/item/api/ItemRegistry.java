package org.betterx.wover.item.api;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.item.api.smithing.SmithingTemplates;
import org.betterx.wover.tag.api.event.context.ItemTagBootstrapContext;

import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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

    public <T extends Item> T register(String path, T item, TagKey<Item>... tags) {
        if (item != null && item != Items.AIR) {
            ResourceLocation id = C.mk(path);
            Registry.register(BuiltInRegistries.ITEM, id, item);
            items.put(id, item);

            if (datagenTags != null && tags != null && tags.length > 0) datagenTags.put(item, tags);
        }

        return item;
    }

    public <T extends Item> T registerAsTool(String path, T item, TagKey<Item>... tags) {
        return register(path, item, tags);
    }

    @Deprecated(forRemoval = true)
    public FoodProperties.Builder foodPropertiesOf(int hunger, float saturation, MobEffectInstance... effects) {
        return this.foodPropertiesOf(hunger, saturation);
    }

    public FoodProperties.Builder foodPropertiesOf(int hunger, float saturation) {
        return new FoodProperties.Builder().nutrition(hunger).saturationModifier(saturation);
    }

    public FoodProperties.Builder drinkPropertiesOf(int hunger, float saturation) {
        return new FoodProperties.Builder().nutrition(hunger).saturationModifier(saturation);
    }


    public <T extends Item> T registerFood(
            String name, Function<Item.Properties, T> factory,
            int hunger, float saturation,
            MobEffectInstance... effects
    ) {
        return registerFood(name, factory, createDefaultItemSettings(), hunger, saturation, effects);
    }

    public <T extends Item> T registerFood(
            String name, Function<Item.Properties, T> factory, Item.Properties properties,
            int hunger, float saturation,
            MobEffectInstance... effects
    ) {
        final Consumable.Builder consumable = Consumables.defaultFood();
        for (MobEffectInstance effect : effects) {
            consumable.onConsume(new ApplyStatusEffectsConsumeEffect(
                    effect,
                    1F
            ));
        }
        final FoodProperties.Builder foodProps = this.foodPropertiesOf(hunger, saturation);

        return this.register(
                name, factory.apply(properties.food(foodProps.build(), consumable.build()))
        );
    }

    public <T extends Item> T registerDrink(
            String name, Function<Item.Properties, T> factory,
            int hunger, float saturation,
            MobEffectInstance... effects
    ) {
        return registerDrink(name, factory, createDefaultItemSettings(), hunger, saturation, effects);
    }


    public <T extends Item> T registerDrink(
            String name, Function<Item.Properties, T> factory, Item.Properties properties,
            int hunger, float saturation,
            MobEffectInstance... effects
    ) {
        return this.register(
                name, factory.apply(properties.food(this
                        .drinkPropertiesOf(hunger, saturation)
                        .build()))
        );
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
