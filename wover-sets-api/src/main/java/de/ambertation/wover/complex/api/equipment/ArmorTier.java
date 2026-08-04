package de.ambertation.wover.complex.api.equipment;

import net.minecraft.core.Holder;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.item.equipment.ArmorMaterial;

import java.util.Arrays;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The per-material configuration used by {@link EquipmentSet} to build a full set of armor pieces: the vanilla
 * {@link ArmorMaterial} to build items with, plus one {@link ArmorValues} entry (durability offset and an
 * optional smithing template) per {@link ArmorSlot}.
 * <p>
 * Built with {@link #builder(String)}; see {@link ArmorTiers} for the predefined vanilla tiers, and
 * {@link #copyWithOffset} for deriving a new tier (e.g. an upgraded material) from an existing one.
 */
public class ArmorTier {
    /**
     * The per-{@link ArmorSlot} configuration of an {@link ArmorTier}.
     *
     * @param durability       the durability offset (relative to {@link #copyWithOffset}'s base tier) used for
     *                         this slot
     * @param smithingTemplate Lazily supplies the upgrade template, since it is only needed when
     *                          recipes are actually built and eagerly resolving it here can trigger
     *                          circular class-initialization (e.g. a template registry that itself
     *                          depends on equipment tiers).
     */
    public record ArmorValues(int durability, @Nullable Supplier<SmithingTemplateItem> smithingTemplate) {
        /**
         * Creates values with no smithing template.
         *
         * @param durability the durability to use for this slot
         */
        public ArmorValues(int durability) {
            this(durability, null);
        }

        /**
         * @return the resolved smithing template item, or {@code null} if this slot has none
         */
        @Nullable
        public SmithingTemplateItem resolveSmithingTemplate() {
            return smithingTemplate == null ? null : smithingTemplate.get();
        }

        /**
         * @param offset the durability offset (and, if set, replacement smithing template) to apply
         * @return a new {@link ArmorValues} with {@code offset} added
         */
        public ArmorValues copyWithOffset(ArmorValues offset) {
            return new ArmorValues(
                    durability + offset.durability,
                    offset.smithingTemplate
            );
        }
    }

    /** The vanilla {@link ArmorMaterial} items built for this tier use. */
    public final ArmorMaterial armorMaterial;
    private final ArmorValues[] armorValues;
    /** The name of this tier, used as a naming component and in {@link #toString()}. */
    public final String name;

    private ArmorTier(
            String name,
            ArmorMaterial armorMaterial,
            ArmorValues[] armorValues
    ) {
        this.armorMaterial = armorMaterial;
        this.armorValues = armorValues;
        this.name = name;
    }

    /**
     * @param slot the slot to look up
     * @return the values configured for {@code slot}, or {@code null} if this tier does not support it
     */
    @Nullable
    public ArmorValues getValues(ArmorSlot slot) {
        return armorValues[slot.slotIndex];
    }

    /**
     * @param name the name of the new tier
     * @return a new {@link Builder}
     */
    public static Builder builder(String name) {
        return new Builder(name);
    }

    /**
     * @param mat the material to compare against
     * @return {@code true} if this tier builds items from {@code mat}
     */
    public boolean is(ArmorMaterial mat) {
        return this.armorMaterial == mat;
    }

    @Override
    public String toString() {
        return "ArmorTier - " + this.name;
    }

    /**
     * Fluent builder for {@link ArmorTier}.
     */
    //a BuilderWithDefaults class
    public static class Builder {
        private ArmorMaterial armorMaterial;
        private final ArmorValues[] armorValues = new ArmorValues[ArmorSlot.values().length];
        private final String name;

        /**
         * @param name the name of the tier being built
         */
        public Builder(String name) {
            this.name = name;
        }

        /**
         * Use {@link #armorMaterial(ArmorMaterial)} instead.
         *
         * @param armorMaterial the armor material to use
         * @return this builder
         * @deprecated Use {@link #armorMaterial(ArmorMaterial)} instead.
         */
        @Deprecated(forRemoval = true)
        public Builder armorMaterial(Holder<ArmorMaterial> armorMaterial) {
            this.armorMaterial = armorMaterial.value();
            return this;
        }


        /**
         * Sets the armor material and derives default {@link ArmorValues} (durability only, no smithing
         * template) for every slot from {@link ArmorMaterial#durability()}.
         *
         * @param armorMaterial the armor material to use
         * @return this builder
         */
        public Builder armorMaterialWithValues(ArmorMaterial armorMaterial) {
            return this.armorMaterial(armorMaterial).allArmorValues(new ArmorValues(armorMaterial.durability()));
        }

        /**
         * @param armorMaterial the armor material to use
         * @return this builder
         */
        public Builder armorMaterial(ArmorMaterial armorMaterial) {
            this.armorMaterial = armorMaterial;
            return this;
        }

        /**
         * Sets the same {@link ArmorValues} for every {@link ArmorSlot}.
         *
         * @param armorValues the values to apply to every slot
         * @return this builder
         */
        public Builder allArmorValues(ArmorValues armorValues) {
            Arrays.fill(this.armorValues, armorValues);
            return this;
        }

        /**
         * Sets the {@link ArmorValues} for a single slot.
         *
         * @param slot        the slot to configure
         * @param armorValues the values to use for that slot
         * @return this builder
         */
        public Builder armorValues(ArmorSlot slot, ArmorValues armorValues) {
            this.armorValues[slot.slotIndex] = armorValues;
            return this;
        }

        /**
         * Copies every slot's values from {@code source}, offset by {@code offset}. Slots {@code source} does
         * not configure are left untouched.
         *
         * @param source the tier to copy values from
         * @param offset the offset to apply to each copied value
         * @return this builder
         */
        public Builder armorValuesWithOffset(ArmorTier source, ArmorValues offset) {
            for (int i = 0; i < armorValues.length; i++) {
                if (source.armorValues[i] != null)
                    this.armorValues[i] = source.armorValues[i].copyWithOffset(offset);
            }
            return this;
        }

        /**
         * @return the built {@link ArmorTier}
         */
        public ArmorTier build() {
            return new ArmorTier(name, armorMaterial, armorValues);
        }
    }

    /**
     * Create a new ArmorTier for the specified ArmorMaterial with where all Values are offset by the given amount
     *
     * @param newName     New name to use for the copy
     * @param newMaterial New ArmorMaterial to use or null if the one from this ArmorTier should be used
     * @param offset      Offset to apply to all values
     * @return New ArmorTier with the specified offset
     */
    public ArmorTier copyWithOffset(
            @NotNull String newName,
            @Nullable ArmorMaterial newMaterial,
            ArmorValues offset
    ) {
        ArmorValues[] newValues = new ArmorValues[armorValues.length];
        for (int i = 0; i < armorValues.length; i++) {
            if (armorValues[i] != null)
                newValues[i] = armorValues[i].copyWithOffset(offset);
        }
        return new ArmorTier(newName, newMaterial == null ? this.armorMaterial : newMaterial, newValues);
    }
}
