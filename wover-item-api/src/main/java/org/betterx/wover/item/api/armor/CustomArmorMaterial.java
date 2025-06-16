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

/**
 * Utility class for creating custom armor materials with a fluent builder API.
 *
 * <p>This class simplifies the creation of custom armor materials by providing a builder pattern
 * with comprehensive validation and automatic resource key generation. It abstracts Minecraft's
 * armor system and ensures all required settings are properly configured.
 *
 * <h2>Key Features</h2>
 * <ul>
 *   <li>Fluent builder API for easy configuration</li>
 *   <li>Comprehensive validation of  armor properties</li>
 *   <li>Support for all armor types including the new body armor</li>
 *   <li>Automatic equipment asset ID generation</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 * <pre class="java">
 * ModCore C = ModCore.create("mymod");
 * ArmorMaterial mythrilMaterial  = CustomArmorMaterial
 *         .start(C.id("mythril"))
 *         .defense(3, 6, 8, 3, 11)  // boots, leggings, chestplate, helmet, body
 *         .durability(500)
 *         .enchantmentValue(15)
 *         .equipSound(SoundEvents.ARMOR_EQUIP_DIAMOND)
 *         .toughness(2.0f)
 *         .knockbackResistance(0.1f)
 *         .repairIngredient(ItemTags.IRON_TOOL_MATERIALS)
 *         .build();
 * </pre>
 *
 * @see ArmorMaterial
 * @see ArmorType
 * @see EquipmentAsset
 */
public class CustomArmorMaterial {
    /**
     * Creates a new builder for a custom armor material.
     *
     * @param materialLocation The resource location identifying this armor material
     * @return A new builder instance
     */
    public static CustomArmorMaterial.Builder start(ResourceLocation materialLocation) {
        return new CustomArmorMaterial.Builder(materialLocation);
    }

    /**
     * Builder class for creating custom armor materials with fluent API.
     *
     * <p>This builder provides methods to configure all aspects of an armor material,
     * including defense values, durability, enchantability, and visual/audio properties.
     * All properties are validated before the material is built.
     */
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

        private Builder(ResourceLocation materialLocation) {
            this.location = materialLocation;
            this.defense = new EnumMap<>(ArmorType.class);
        }

        /**
         * Sets defense values for all armor types in one call.
         *
         * @param bootsDefense      Defense value for boots
         * @param leggingsDefense   Defense value for leggings
         * @param chestplateDefense Defense value for chestplate
         * @param helmetDefense     Defense value for helmet
         * @param bodyDefense       Defense value for body armor
         * @return This builder instance for chaining
         */
        public Builder defense(
                int bootsDefense,
                int leggingsDefense,
                int chestplateDefense,
                int helmetDefense,
                int bodyDefense
        ) {
            defense(ArmorType.BOOTS, bootsDefense);
            defense(ArmorType.LEGGINGS, leggingsDefense);
            defense(ArmorType.CHESTPLATE, chestplateDefense);
            defense(ArmorType.HELMET, helmetDefense);
            defense(ArmorType.BODY, bodyDefense);
            return this;
        }

        /**
         * Sets the defense value for a specific armor type.
         *
         * @param armorType    The armor type to set defense for
         * @param defenseValue The defense value (should be positive)
         * @return This builder instance for chaining
         */
        public Builder defense(ArmorType armorType, int defenseValue) {
            this.defense.put(armorType, defenseValue);
            return this;
        }

        /**
         * Sets the base durability for this armor material.
         *
         * <p>This value is used as a multiplier with armor type-specific durability values
         * to determine the actual durability of each armor piece.
         *
         * @param baseDurability The base durability (must be positive)
         * @return This builder instance for chaining
         */
        public Builder durability(int baseDurability) {
            this.durability = baseDurability;
            return this;
        }

        /**
         * Sets the enchantment value (enchantability) for this armor material.
         *
         * <p>Higher values make the armor more likely to receive better enchantments
         * when enchanted at an enchanting table.
         *
         * @param enchantability The enchantment value (must be non-negative)
         * @return This builder instance for chaining
         */
        public Builder enchantmentValue(int enchantability) {
            this.enchantmentValue = enchantability;
            return this;
        }

        /**
         * Sets the sound played when this armor is equipped.
         *
         * @param equipmentSound The sound event to play when equipping armor
         * @return This builder instance for chaining
         */
        public Builder equipSound(Holder<SoundEvent> equipmentSound) {
            this.equipSound = equipmentSound;
            return this;
        }

        /**
         * Sets the toughness value for this armor material.
         *
         * <p>Toughness reduces damage taken from attacks that deal high amounts of damage.
         * It provides additional protection beyond the base defense value.
         *
         * @param armorToughness The toughness value (must be non-negative)
         * @return This builder instance for chaining
         */
        public Builder toughness(float armorToughness) {
            this.toughness = armorToughness;
            return this;
        }

        /**
         * Sets the knockback resistance provided by this armor material.
         *
         * <p>Knockback resistance reduces the distance the wearer is pushed back
         * when hit by attacks or explosions.
         *
         * @param knockbackResistanceValue The knockback resistance (0.0 to 1.0, must be non-negative)
         * @return This builder instance for chaining
         */
        public Builder knockbackResistance(float knockbackResistanceValue) {
            this.knockbackResistance = knockbackResistanceValue;
            return this;
        }

        /**
         * Sets the repair ingredient tag for this armor material.
         *
         * <p>Items matching this tag can be used to repair armor pieces made from this material
         * in an anvil or crafting table.
         *
         * @param repairIngredientTag The item tag containing valid repair ingredients
         * @return This builder instance for chaining
         */
        public Builder repairIngredient(TagKey<Item> repairIngredientTag) {
            this.repairIngredient = repairIngredientTag;
            return this;
        }

        /**
         * @deprecated This method is deprecated and no longer working. Use {@link #repairIngredient(TagKey)} instead.
         */
        @Deprecated(forRemoval = true)
        public Builder repairIngredientSupplier(Supplier<Ingredient> repairIngredientSupplier) {
            return this;
        }

        /**
         * Sets the equipment asset ID for this armor material.
         *
         * <p>The asset ID determines the visual appearance and model of the armor when worn.
         * If not specified, it will be automatically generated based on the material location.
         *
         * @param equipmentAssetId The resource key for the equipment asset
         * @return This builder instance for chaining
         */
        public Builder assetId(ResourceKey<EquipmentAsset> equipmentAssetId) {
            this.assetId = equipmentAssetId;
            return this;
        }

        /**
         * @deprecated This method is deprecated and no longer working. Visual configuration is now handled through equipment assets.
         */
        @Deprecated(forRemoval = true)
        public Builder layers(List<Object> layers) {
            return this;
        }

        /**
         * Validates all builder properties to ensure they meet requirements.
         *
         * @throws IllegalStateException if any required property is missing or invalid
         */
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

        /**
         * Builds the armor material with the configured properties.
         *
         * <p>This method validates all properties and creates the final {@link ArmorMaterial} instance.
         * If no asset ID was explicitly set, it will be automatically generated from the material location.
         *
         * @return The configured armor material
         * @throws IllegalStateException if validation fails
         */
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

        /**
         * Builds the armor material and wraps it in a holder for direct registration.
         *
         * @return A holder containing the built armor material
         * @throws IllegalStateException if validation fails
         */
        public Holder<ArmorMaterial> buildAndRegister() {
            return Holder.direct(build());
        }
    }
}
