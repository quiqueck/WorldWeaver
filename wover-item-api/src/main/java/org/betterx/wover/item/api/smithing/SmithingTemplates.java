package org.betterx.wover.item.api.smithing;

import org.betterx.wover.core.api.ModCore;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SmithingTemplateItem;

import org.spongepowered.include.com.google.common.collect.ImmutableList;

import java.util.List;

public class SmithingTemplates {
    public static final ChatFormatting TITLE_FORMAT = ChatFormatting.GRAY;
    public static final ChatFormatting DESCRIPTION_FORMAT = ChatFormatting.BLUE;

    public static final ResourceLocation EMPTY_SLOT_HELMET = ResourceLocation.withDefaultNamespace(
            "item/empty_armor_slot_helmet");
    public static final ResourceLocation EMPTY_SLOT_CHESTPLATE = ResourceLocation.withDefaultNamespace(
            "item/empty_armor_slot_chestplate");
    public static final ResourceLocation EMPTY_SLOT_LEGGINGS = ResourceLocation.withDefaultNamespace(
            "item/empty_armor_slot_leggings");
    public static final ResourceLocation EMPTY_SLOT_BOOTS = ResourceLocation.withDefaultNamespace(
            "item/empty_armor_slot_boots");
    public static final ResourceLocation EMPTY_SLOT_HOE = ResourceLocation.withDefaultNamespace("item/empty_slot_hoe");
    public static final ResourceLocation EMPTY_SLOT_AXE = ResourceLocation.withDefaultNamespace("item/empty_slot_axe");
    public static final ResourceLocation EMPTY_SLOT_SWORD = ResourceLocation.withDefaultNamespace(
            "item/empty_slot_sword");
    public static final ResourceLocation EMPTY_SLOT_SHOVEL = ResourceLocation.withDefaultNamespace(
            "item/empty_slot_shovel");
    public static final ResourceLocation EMPTY_SLOT_PICKAXE = ResourceLocation.withDefaultNamespace(
            "item/empty_slot_pickaxe");
    public static final ResourceLocation EMPTY_SLOT_INGOT = ResourceLocation.withDefaultNamespace(
            "item/empty_slot_ingot");
    public static final ResourceLocation EMPTY_SLOT_REDSTONE_DUST = ResourceLocation.withDefaultNamespace(
            "item/empty_slot_redstone_dust");
    public static final ResourceLocation EMPTY_SLOT_DIAMOND = ResourceLocation.withDefaultNamespace(
            "item/empty_slot_diamond");

    public static final List<ResourceLocation> TOOLS = List.of(
            EMPTY_SLOT_SWORD,
            EMPTY_SLOT_PICKAXE,
            EMPTY_SLOT_AXE,
            EMPTY_SLOT_HOE,
            EMPTY_SLOT_SHOVEL
    );

    public static final List<ResourceLocation> ARMOR = List.of(
            EMPTY_SLOT_HELMET,
            EMPTY_SLOT_CHESTPLATE,
            EMPTY_SLOT_LEGGINGS,
            EMPTY_SLOT_BOOTS
    );
    public static final List<ResourceLocation> ARMOR_AND_TOOLS = combine(ARMOR, TOOLS);

    public static List<ResourceLocation> combine(List<ResourceLocation>... sources) {
        final ImmutableList.Builder<ResourceLocation> builder = ImmutableList.builder();
        for (var s : sources) {
            builder.addAll(s);
        }
        return builder.build();
    }

    public static Builder create(ModCore modCore, String path) {
        return new Builder(modCore, path);
    }

    public static class Builder {
        private final ModCore C;
        private final String path;
        private List<ResourceLocation> baseSlotEmptyIcons;
        private List<ResourceLocation> additionalSlotEmptyIcons;
        private Item.Properties properties;

        private Builder(ModCore modCore, String path) {
            this.C = modCore;
            this.path = path;
            this.properties = new Item.Properties();
        }

        public Builder setBaseSlotEmptyIcons(List<ResourceLocation> baseSlotEmptyIcons) {
            this.baseSlotEmptyIcons = baseSlotEmptyIcons;
            return this;
        }

        public Builder setAdditionalSlotEmptyIcons(List<ResourceLocation> additionalSlotEmptyIcons) {
            this.additionalSlotEmptyIcons = additionalSlotEmptyIcons;
            return this;
        }

        public Builder setProperties(Item.Properties properties) {
            this.properties = properties;
            return this;
        }

        public SmithingTemplateItem build() {
            if (baseSlotEmptyIcons == null || baseSlotEmptyIcons.isEmpty()) {
                throw new IllegalStateException("Base slot empty icons must contain at least one icon");
            }
            if (additionalSlotEmptyIcons == null || additionalSlotEmptyIcons.isEmpty()) {
                throw new IllegalStateException("Additional slot empty icons must contain at least one icon");
            }

            if (this.properties == null) {
                this.properties = new Item.Properties();
            }
            this.properties = properties.setId(ResourceKey.create(BuiltInRegistries.ITEM.key(), C.mk(path)));

            return new SmithingTemplateItem(
                    Component.translatable(Util.makeDescriptionId(
                            "item",
                            C.mk("smithing_template." + path + ".applies_to")
                    )).withStyle(DESCRIPTION_FORMAT),
                    Component.translatable(Util.makeDescriptionId(
                            "item",
                            C.mk("smithing_template." + path + ".ingredients")
                    )).withStyle(DESCRIPTION_FORMAT),
                    Component.translatable(Util.makeDescriptionId(
                            "item",
                            C.mk("smithing_template." + path + ".base_slot_description")
                    )),
                    Component.translatable(Util.makeDescriptionId(
                            "item",
                            C.mk("smithing_template." + path + ".additions_slot_description")
                    )),
                    baseSlotEmptyIcons,
                    additionalSlotEmptyIcons,
                    this.properties
            );
        }
    }
}