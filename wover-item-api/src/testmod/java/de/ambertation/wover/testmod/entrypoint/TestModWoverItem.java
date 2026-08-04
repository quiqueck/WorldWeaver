package de.ambertation.wover.testmod.entrypoint;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.enchantment.api.EnchantmentKey;
import de.ambertation.wover.enchantment.api.EnchantmentManager;
import de.ambertation.wover.tabs.api.CreativeTabs;
import de.ambertation.wover.testmod.item.TestItemRegistry;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;

import net.fabricmc.api.ModInitializer;

import java.util.List;

public class TestModWoverItem implements ModInitializer {
    // ModCore for the TestMod. TestMod's do not share the wover namespace,
    // but (like other Mods that include Wover) have a unique one
    public static final ModCore C = ModCore.create("wover-item-testmod");
    public static final EnchantmentKey TEST_ENCHANT = EnchantmentManager
            .createKey(C.mk("test_enchant"));

    public static final EnchantmentKey BREAKER_ENCHANT = EnchantmentManager
            .createKey(C.mk("breaker_enchant"));


    private static Holder<Attribute> register(ResourceLocation string, Attribute attribute) {
        return Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, string, attribute);
    }

    public static final Holder<Attribute> OBSIDIAN_BLOCK_BREAK_SPEED = register(
            C.mk("player.bn_obsidian_block_break_speed"),
            new RangedAttribute("attribute.name.player.bn_obsidian_block_break_speed", 1.0, 1.0f, 100.0f).setSyncable(true)
    );

    @Override
    public void onInitialize() {

        TestItemRegistry.ensureStaticallyLoaded();

        CreativeTabs.start(C)
                    .createItemOnlyTab(Items.WOODEN_AXE).buildAndAdd()
                    .processRegistries()
                    .registerAllTabs();

        EnchantmentManager.BOOTSTRAP_ENCHANTMENTS.subscribe(context -> {
            HolderGetter<Item> itemGetter = context.lookup(Registries.ITEM);
            BREAKER_ENCHANT.register(context, Enchantment
                    .enchantment(
                            Enchantment.definition(
                                    itemGetter.getOrThrow(ItemTags.MINING_ENCHANTABLE),
                                    10, 5,
                                    Enchantment.dynamicCost(20, 20),
                                    Enchantment.dynamicCost(120, 20),
                                    1,
                                    EquipmentSlotGroup.MAINHAND
                            )
                    )
                    .withEffect(
                            EnchantmentEffectComponents.ATTRIBUTES,
                            new EnchantmentAttributeEffect(
                                    OBSIDIAN_BLOCK_BREAK_SPEED.unwrapKey().orElseThrow().location(),
                                    OBSIDIAN_BLOCK_BREAK_SPEED,
                                    new LevelBasedValue.Lookup(List.of(6f, 12f, 18f), new LevelBasedValue.LevelsSquared(9.0F)),
                                    AttributeModifier.Operation.ADD_VALUE
                            )
                    )
            );
        });
    }
}