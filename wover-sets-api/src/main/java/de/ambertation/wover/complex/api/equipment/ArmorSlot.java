package de.ambertation.wover.complex.api.equipment;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;

/**
 * The armor pieces an {@link EquipmentSet} can register, one per vanilla {@link ArmorType}.
 * <p>
 * Each constant carries the naming suffix used by {@link EquipmentSet#add(ArmorSlot)} (e.g.
 * {@code <baseName>_helmet}), the matching vanilla {@link ArmorType}, and the {@link RecipeCategory} used for
 * the auto-generated recipe.
 */
public enum ArmorSlot {
    HELMET_SLOT(0, "helmet", ArmorType.HELMET, RecipeCategory.COMBAT),
    CHESTPLATE_SLOT(1, "chestplate", ArmorType.CHESTPLATE, RecipeCategory.COMBAT),
    LEGGINGS_SLOT(2, "leggings", ArmorType.LEGGINGS, RecipeCategory.COMBAT),
    BOOTS_SLOT(3, "boots", ArmorType.BOOTS, RecipeCategory.COMBAT),
    BODY_SLOT(4, "body", ArmorType.BODY, RecipeCategory.COMBAT);

    /**
     * @param slot the slot to convert
     * @return the matching vanilla {@link EquipmentSlot}
     * @throws IllegalArgumentException if {@code slot} has no vanilla {@link EquipmentSlot} counterpart
     */
    public static EquipmentSlot toEquipmentSlot(ArmorSlot slot) {
        return switch (slot) {
            case HELMET_SLOT -> EquipmentSlot.HEAD;
            case CHESTPLATE_SLOT -> EquipmentSlot.CHEST;
            case LEGGINGS_SLOT -> EquipmentSlot.LEGS;
            case BOOTS_SLOT -> EquipmentSlot.FEET;
            case BODY_SLOT -> EquipmentSlot.BODY;
            default -> throw new IllegalArgumentException("Invalid ArmorSlot: " + slot);
        };
    }

    /**
     * The vanilla item tags every humanoid armor piece in this slot needs to be part of.
     * <p>
     * These are what make a piece enchantable: vanilla derives {@code #minecraft:enchantable/head_armor},
     * {@code .../chest_armor}, {@code .../armor}, {@code .../durability}, {@code .../equippable} and
     * {@code .../vanishing} from {@code #minecraft:head_armor} and friends, and
     * {@link net.minecraft.world.item.enchantment.Enchantment#canEnchant} only accepts an item that is in the
     * enchantment's supported-items tag. Armor that is missing these tags can only be enchanted in creative
     * mode (see {@code AnvilMenu}, which bypasses the check for players with infinite materials).
     * <p>
     * Note that being in these tags also makes a piece part of {@code #minecraft:trimmable_armor}, so items
     * that merely occupy an armor slot without being humanoid armor (an elytra, for instance) should not use
     * them.
     *
     * @return the tags for this slot, or an empty array for slots without a vanilla armor tag
     */
    @SuppressWarnings("unchecked")
    public TagKey<Item>[] humanoidArmorTags() {
        return switch (this) {
            case HELMET_SLOT -> new TagKey[]{ItemTags.HEAD_ARMOR};
            case CHESTPLATE_SLOT -> new TagKey[]{ItemTags.CHEST_ARMOR};
            case LEGGINGS_SLOT -> new TagKey[]{ItemTags.LEG_ARMOR};
            case BOOTS_SLOT -> new TagKey[]{ItemTags.FOOT_ARMOR};
            default -> new TagKey[0];
        };
    }

    /**
     * @param slot the slot to convert
     * @return the matching {@link ArmorSlot}
     * @throws IllegalArgumentException if {@code slot} has no {@link ArmorSlot} counterpart
     */
    public static ArmorSlot fromEquipmentSlot(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> HELMET_SLOT;
            case CHEST -> CHESTPLATE_SLOT;
            case LEGS -> LEGGINGS_SLOT;
            case FEET -> BOOTS_SLOT;
            case BODY -> BODY_SLOT;
            default -> throw new IllegalArgumentException("Invalid EquipmentSlot: " + slot);
        };
    }

    /** The {@link RecipeCategory} used for this slot's auto-generated recipe. */
    public final RecipeCategory category;
    /** The naming suffix appended to an {@link EquipmentSet}'s base name for this slot (e.g. {@code "helmet"}). */
    public final String name;
    /** The index of this slot's values within an {@link ArmorTier}'s internal value array. */
    public final int slotIndex;
    /** The vanilla {@link ArmorType} this slot corresponds to. */
    public final ArmorType armorType;

    ArmorSlot(
            int slotIndex,
            String name,
            ArmorType armorType,
            RecipeCategory category
    ) {
        this.name = name;
        this.category = category;
        this.slotIndex = slotIndex;
        this.armorType = armorType;
    }
}
