package de.ambertation.wover.complex.api.equipment;

import de.ambertation.wover.entrypoint.LibWoverRecipe;
import de.ambertation.wover.item.api.ItemDefinition;
import de.ambertation.wover.item.api.ToolItemDefinition;
import de.ambertation.wover.item.api.trait.ItemTrait;
import de.ambertation.wover.item.api.trait.ItemTraitKey;
import de.ambertation.wover.item.impl.trait.ItemTraitImpl;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The per-material configuration used by {@link EquipmentSet} to build a full set of tools: the vanilla
 * {@link ToolMaterial} to build items with, plus one {@link ToolValues} entry (attack stats, mining tag, and an
 * optional smithing template) per {@link ToolSlot}.
 * <p>
 * Built with {@link #builder(String)}; see {@link ToolTiers} for the predefined vanilla tiers, and
 * {@link #copyWithOffset} for deriving a new tier (e.g. an upgraded material) from an existing one.
 */
public class ToolTier {
    interface TraitBuilder {
        ItemTrait<Item, ?> with(ToolSlot slot, ToolTier tier);
    }

    static abstract class ConfigureToolItemTrait extends ItemTraitImpl<Item, ConfigureToolItemTrait> {
        protected final ToolSlot slot;
        protected final ToolTier tier;
        private final ItemTraitKey traitKey;

        protected ConfigureToolItemTrait(ItemTraitKey traitKey, ToolSlot slot, ToolTier tier) {
            this.traitKey = traitKey;
            this.slot = slot;
            this.tier = tier;
        }

        @Override
        public ItemTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(ItemDefinition<Item, ? extends ItemDefinition<Item, ?>> definition) {
            ToolTier.ToolValues values = this.tier.getValues(slot);
            if (values == null)
                throw new IllegalArgumentException("No values for slot " + slot + " in tier " + this.tier);

            configure(definition, values);
        }

        protected abstract void configure(
                ItemDefinition<Item, ? extends ItemDefinition<Item, ?>> definition,
                ToolTier.ToolValues values
        );
    }

    static class ConfigureDiggerItemTrait extends ConfigureToolItemTrait {
        public static final ItemTraitKey ID = ItemTraitKey.ofUnique(LibWoverRecipe.C, "configure_digger_item");

        public ConfigureDiggerItemTrait(ToolSlot slot, ToolTier tier) {
            super(ID, slot, tier);
        }

        @Override
        protected void configure(
                ItemDefinition<Item, ? extends ItemDefinition<Item, ?>> definition,
                ToolTier.ToolValues values
        ) {
            if (definition instanceof ToolItemDefinition toolDef) {
                toolDef.tool(
                        this.tier.toolMaterial,
                        values.minableWithTag,
                        values.attackDamage,
                        values.attackSpeed,
                        values.disableBlockingForSeconds
                );
            } else {
                throw new IllegalArgumentException("Definition must be a ToolItemDefinition");
            }
        }
    }

    static class ConfigureSwordItemTrait extends ConfigureToolItemTrait {
        public static final ItemTraitKey ID = ItemTraitKey.ofUnique(LibWoverRecipe.C, "configure_sword_item");

        public ConfigureSwordItemTrait(ToolSlot slot, ToolTier tier) {
            super(ID, slot, tier);
        }

        @Override
        protected void configure(
                ItemDefinition<Item, ? extends ItemDefinition<Item, ?>> definition,
                ToolTier.ToolValues values
        ) {
            if (definition instanceof ToolItemDefinition toolDef) {
                toolDef.sword(
                        tier.toolMaterial,
                        (int) values.attackDamage,
                        values.attackSpeed
                );
            } else {
                throw new IllegalArgumentException("Definition must be a ToolItemDefinition");
            }
        }
    }


    /**
     * The per-{@link ToolSlot} configuration of a {@link ToolTier}.
     *
     * @param attackDamage               the attack damage bonus for this slot
     * @param attackSpeed                the attack speed modifier for this slot
     * @param disableBlockingForSeconds  seconds a shield is disabled for when hit by this tool (0 for none)
     * @param smithingTemplate           the smithing template used to upgrade into this slot's item, or
     *                                   {@code null} for a plain crafting recipe
     * @param minableWithTag             the block tag this slot's tool is effective against, used both for the
     *                                   item's {@code tool(...)} component and its mining tag
     */
    public record ToolValues(
            float attackDamage,
            float attackSpeed,
            float disableBlockingForSeconds,
            SmithingTemplateItem smithingTemplate,
            TagKey<Block> minableWithTag
    ) {
        /** Values representing "no change", useful as an offset for {@link ToolTier#copyWithOffset}. */
        public static ToolValues NO_OFFSET = new ToolValues(0, 0);

        /**
         * Creates values with no blocking penalty, smithing template, or mining tag.
         *
         * @param attackDamage the attack damage bonus for this slot
         * @param attackSpeed  the attack speed modifier for this slot
         */
        public ToolValues(float attackDamage, float attackSpeed) {
            this(attackDamage, attackSpeed, 0, null, null);
        }

        /**
         * Creates values with no smithing template or mining tag.
         *
         * @param attackDamage              the attack damage bonus for this slot
         * @param attackSpeed               the attack speed modifier for this slot
         * @param disableBlockingForSeconds seconds a shield is disabled for when hit by this tool
         */
        public ToolValues(float attackDamage, float attackSpeed, float disableBlockingForSeconds) {
            this(attackDamage, attackSpeed, disableBlockingForSeconds, null, null);
        }

        /**
         * Creates values with no blocking penalty or mining tag.
         *
         * @param attackDamage     the attack damage bonus for this slot
         * @param attackSpeed      the attack speed modifier for this slot
         * @param smithingTemplate the smithing template used to upgrade into this slot's item
         */
        public ToolValues(float attackDamage, float attackSpeed, SmithingTemplateItem smithingTemplate) {
            this(attackDamage, attackSpeed, 0, smithingTemplate, null);
        }

        /**
         * Creates values with no blocking penalty or smithing template.
         *
         * @param attackDamage    the attack damage bonus for this slot
         * @param attackSpeed     the attack speed modifier for this slot
         * @param minableWithTag  the block tag this slot's tool is effective against
         */
        public ToolValues(float attackDamage, float attackSpeed, TagKey<Block> minableWithTag) {
            this(attackDamage, attackSpeed, 0, null, minableWithTag);
        }

        /**
         * Creates values with no mining tag.
         *
         * @param attackDamage              the attack damage bonus for this slot
         * @param attackSpeed               the attack speed modifier for this slot
         * @param disableBlockingForSeconds seconds a shield is disabled for when hit by this tool
         * @param smithingTemplate          the smithing template used to upgrade into this slot's item
         */
        public ToolValues(
                float attackDamage,
                float attackSpeed,
                float disableBlockingForSeconds,
                SmithingTemplateItem smithingTemplate
        ) {
            this(attackDamage, attackSpeed, disableBlockingForSeconds, smithingTemplate, null);
        }

        /**
         * Creates values with no smithing template.
         *
         * @param attackDamage              the attack damage bonus for this slot
         * @param attackSpeed               the attack speed modifier for this slot
         * @param disableBlockingForSeconds seconds a shield is disabled for when hit by this tool
         * @param minableWithTag            the block tag this slot's tool is effective against
         */
        public ToolValues(
                float attackDamage,
                float attackSpeed,
                float disableBlockingForSeconds,
                TagKey<Block> minableWithTag
        ) {
            this(attackDamage, attackSpeed, disableBlockingForSeconds, null, minableWithTag);
        }

        /**
         * @param offset the values to add on top of this instance
         * @return a new {@link ToolValues} with {@code offset} added; a non-null {@code smithingTemplate}/
         *         {@code minableWithTag} in {@code offset} replaces this instance's value
         */
        ToolValues copyWithOffset(ToolValues offset) {
            return new ToolValues(
                    attackDamage + offset.attackDamage,
                    attackSpeed + offset.attackSpeed,
                    disableBlockingForSeconds + offset.disableBlockingForSeconds,
                    offset.smithingTemplate != null ? offset.smithingTemplate : smithingTemplate,
                    offset.minableWithTag != null ? offset.minableWithTag : this.minableWithTag
            );
        }
    }

    /** The name of this tier, used as a naming component and in {@link #toString()}. */
    public final String name;
    /** The vanilla {@link ToolMaterial} items built for this tier use. */
    public final ToolMaterial toolMaterial;
    /** The mining level of this tier (higher is stronger; matches vanilla's tier ordering). */
    public final int level;
    /** The block tag identifying which blocks require at least this tier to be mined correctly. */
    public final TagKey<Block> blockTag;
    private final ToolValues[] toolValues;


    private ToolTier(
            String name,
            ToolMaterial toolMaterial,
            ToolValues[] toolValues,
            TagKey<Block> blockTag,
            int level
    ) {
        this.toolMaterial = toolMaterial;
        this.toolValues = toolValues;
        this.name = name;
        this.blockTag = blockTag;
        this.level = level;
    }

    /**
     * @param slot the slot to look up
     * @return the values configured for {@code slot}, or {@code null} if this tier does not support it
     */
    @Nullable
    public ToolValues getValues(ToolSlot slot) {
        return toolValues[slot.slotIndex];
    }

    /**
     * @param name the name of the new tier
     * @return a new {@link Builder}
     */
    public static ToolTier.Builder builder(String name) {
        return new ToolTier.Builder(name);
    }

    /**
     * Fluent builder for {@link ToolTier}.
     */
    //a BuilderWithDefaults class
    public static class Builder {
        private int level;
        private ToolMaterial toolMaterial;
        private final ToolValues[] toolValues = new ToolValues[ToolSlot.values().length];
        private final String name;
        private TagKey<Block> blockTag;

        Builder(String name) {
            this.name = name;
        }

        /**
         * @param level the mining level of the tier being built
         * @return this builder
         */
        public Builder level(int level) {
            this.level = level;
            return this;
        }

        /**
         * @param blockTag the block tag identifying which blocks require at least this tier to be mined correctly
         * @return this builder
         */
        public Builder blockTag(TagKey<Block> blockTag) {
            this.blockTag = blockTag;
            return this;
        }

        /**
         * @param toolMaterial the tool material to use
         * @return this builder
         */
        public Builder toolMaterial(ToolMaterial toolMaterial) {
            this.toolMaterial = toolMaterial;
            return this;
        }

        /**
         * Sets the {@link ToolValues} for a single slot.
         *
         * @param slot       the slot to configure
         * @param toolValues the values to use for that slot
         * @return this builder
         */
        public Builder toolValues(ToolSlot slot, ToolValues toolValues) {
            this.toolValues[slot.slotIndex] = toolValues;
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
        public Builder toolValuesWithOffset(ToolTier source, ToolValues offset) {
            for (int i = 0; i < toolValues.length; i++) {
                if (source.toolValues[i] != null)
                    this.toolValues[i] = source.toolValues[i].copyWithOffset(offset);

            }
            return this;
        }

        /**
         * @return the built {@link ToolTier}
         */
        public ToolTier build() {
            return new ToolTier(name, toolMaterial, toolValues, blockTag, level);
        }
    }

    @Override
    public String toString() {
        return "ToolTier - " + this.name;
    }

    /**
     * Create a new ToolTier for the specified Tier where all Values are offset by the given amount
     *
     * @param newName  New Name to use for the copy
     * @param newTier  New Tier to use or null if the one from this ToolTier should be used
     * @param offset   Offset to apply to all values
     * @param blockTag New block tag identifying which blocks require at least this tier to be mined correctly
     * @return New ToolTier with the specified offset
     */
    public ToolTier copyWithOffset(
            @NotNull String newName,
            @Nullable ToolMaterial newTier,
            ToolValues offset,
            @Nullable TagKey<Block> blockTag
    ) {
        ToolValues[] newValues = new ToolValues[toolValues.length];
        for (int i = 0; i < toolValues.length; i++) {
            if (toolValues[i] != null)
                newValues[i] = toolValues[i].copyWithOffset(offset);
        }
        return new ToolTier(newName, newTier == null ? this.toolMaterial : newTier, newValues, blockTag, level);
    }
}
