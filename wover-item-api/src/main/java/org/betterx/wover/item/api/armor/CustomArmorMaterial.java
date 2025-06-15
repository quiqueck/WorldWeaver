package org.betterx.wover.item.api.armor;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class CustomArmorMaterial {
    public static CustomArmorMaterial.Builder start(ResourceLocation location) {
        return new CustomArmorMaterial.Builder(location);
    }

    public static class Builder {
        private final ResourceLocation location;
        private final EnumMap<ArmorType, Integer> defense;
        private int enchantmentValue;
        private Holder<SoundEvent> equipSound;
        private float toughness;
        private int durability;
        private float knockbackResistance;
        private TagKey<Item> repairIngredient;
        private ResourceKey<EquipmentAsset> assetId;

        private Builder(ResourceLocation location) {
            this.location = location;
            this.defense = new EnumMap<>(ArmorType.class);
        }

        public Builder defense(int boots, int leggings, int chestplate, int helmet, int body) {
            defense(ArmorType.BOOTS, boots);
            defense(ArmorType.LEGGINGS, leggings);
            defense(ArmorType.CHESTPLATE, chestplate);
            defense(ArmorType.HELMET, helmet);
            defense(ArmorType.BODY, body);
            return this;
        }

        public Builder defense(ArmorType type, int defense) {
            this.defense.put(type, defense);
            return this;
        }

        public Builder durability(int durability) {
            this.durability = durability;
            return this;
        }

        public Builder enchantmentValue(int enchantmentValue) {
            this.enchantmentValue = enchantmentValue;
            return this;
        }

        public Builder equipSound(Holder<SoundEvent> equipSound) {
            this.equipSound = equipSound;
            return this;
        }

        public Builder toughness(float toughness) {
            this.toughness = toughness;
            return this;
        }

        public Builder knockbackResistance(float knockbackResistance) {
            this.knockbackResistance = knockbackResistance;
            return this;
        }

        public Builder repairIngredient(TagKey<Item> repairIngredient) {
            this.repairIngredient = repairIngredient;
            return this;
        }

        @Deprecated(forRemoval = true)
        public Builder repairIngredientSupplier(Supplier<Ingredient> repairIngredientSupplier) {
            return this;
        }

        public Builder assetId(ResourceKey<EquipmentAsset> assetId) {
            this.assetId = assetId;
            return this;
        }

        @Deprecated(forRemoval = true)
        public Builder layers(List<Object> layers) {
            return this;
        }

        protected void validate() throws IllegalStateException {
            if (defense.size() != ArmorType.values().length) {
                throw new IllegalStateException("Defense values must be set for all armor types");
            }

            if (durability <= 0) {
                throw new IllegalStateException("Durability must be positive");
            }

            if (enchantmentValue < 0) {
                throw new IllegalStateException("Enchantment value must be non-negative");
            }

            if (equipSound == null) {
                throw new IllegalStateException("Equip sound must be set");
            }

            if (toughness < 0) {
                throw new IllegalStateException("Toughness must be non-negative");
            }

            if (knockbackResistance < 0) {
                throw new IllegalStateException("Knockback resistance must be non-negative");
            }

            if (repairIngredient == null) {
                throw new IllegalStateException("Repair ingredient must be set");
            }

            if (assetId == null) {
                throw new IllegalStateException("Asset ID must be set");
            }
        }

        public ArmorMaterial build() {
            if (assetId == null) {
                assetId = ResourceKey.create(EquipmentAssets.ROOT_ID, location);
            }
            validate();
            return new ArmorMaterial(
                    durability,
                    defense, enchantmentValue, equipSound,
                    toughness,
                    knockbackResistance,
                    repairIngredient, assetId
            );
        }

        public Holder<ArmorMaterial> buildAndRegister() {
            return Holder.direct(build());
        }
    }
}
