package org.betterx.wover.complex.api.equipment;

import org.betterx.wover.entrypoint.LibWoverRecipe;
import org.betterx.wover.item.api.ItemDefinition;
import org.betterx.wover.item.api.trait.ItemTrait;
import org.betterx.wover.item.api.trait.ItemTraitKey;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ToolTier {
    interface TraitBuilder {
        ItemTrait<Item, ?> with(ToolSlot slot, ToolTier tier);
    }

    static abstract class ConfigureToolItemTrait extends ItemTrait<Item, ItemTrait.VoidRuntime> {
        protected final ToolSlot slot;
        protected final ToolTier tier;

        protected ConfigureToolItemTrait(ItemTraitKey traitKey, ToolSlot slot, ToolTier tier) {
            super(traitKey);
            this.slot = slot;
            this.tier = tier;
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
        public static final ItemTraitKey ID = ItemTraitKey.of(LibWoverRecipe.C, "configure_digger_item");

        public ConfigureDiggerItemTrait(ToolSlot slot, ToolTier tier) {
            super(ID, slot, tier);
        }

        @Override
        protected void configure(
                ItemDefinition<Item, ? extends ItemDefinition<Item, ?>> definition,
                ToolTier.ToolValues values
        ) {
            definition.getProperties().tool(
                    this.tier.toolMaterial,
                    values.minableWithTag,
                    values.attackDamage,
                    values.attackSpeed,
                    values.disableBlockingForSeconds
            );
        }
    }

    static class ConfigureSwordItemTrait extends ConfigureToolItemTrait {
        public static final ItemTraitKey ID = ItemTraitKey.of(LibWoverRecipe.C, "configure_sword_item");

        public ConfigureSwordItemTrait(ToolSlot slot, ToolTier tier) {
            super(ID, slot, tier);
        }

        @Override
        protected void configure(
                ItemDefinition<Item, ? extends ItemDefinition<Item, ?>> definition,
                ToolTier.ToolValues values
        ) {
            definition.getProperties().sword(
                    tier.toolMaterial,
                    (int) values.attackDamage,
                    values.attackSpeed
            );
        }
    }


    public record ToolValues(
            float attackDamage,
            float attackSpeed,
            float disableBlockingForSeconds,
            SmithingTemplateItem smithingTemplate,
            TagKey<Block> minableWithTag
    ) {
        public ToolValues(float attackDamage, float attackSpeed) {
            this(attackDamage, attackSpeed, 0, null, null);
        }

        public ToolValues(float attackDamage, float attackSpeed, float disableBlockingForSeconds) {
            this(attackDamage, attackSpeed, disableBlockingForSeconds, null, null);
        }

        public ToolValues(float attackDamage, float attackSpeed, SmithingTemplateItem smithingTemplate) {
            this(attackDamage, attackSpeed, 0, smithingTemplate, null);
        }

        public ToolValues(float attackDamage, float attackSpeed, TagKey<Block> minableWithTag) {
            this(attackDamage, attackSpeed, 0, null, minableWithTag);
        }

        public ToolValues(
                float attackDamage,
                float attackSpeed,
                float disableBlockingForSeconds,
                SmithingTemplateItem smithingTemplate
        ) {
            this(attackDamage, attackSpeed, disableBlockingForSeconds, smithingTemplate, null);
        }

        public ToolValues(
                float attackDamage,
                float attackSpeed,
                float disableBlockingForSeconds,
                TagKey<Block> minableWithTag
        ) {
            this(attackDamage, attackSpeed, disableBlockingForSeconds, null, minableWithTag);
        }

        ToolValues copyWithOffset(ToolValues offset) {
            return new ToolValues(
                    attackDamage + offset.attackDamage,
                    attackSpeed + offset.attackSpeed,
                    disableBlockingForSeconds + offset.disableBlockingForSeconds,
                    offset.smithingTemplate,
                    offset.minableWithTag != null ? offset.minableWithTag : this.minableWithTag
            );
        }
    }

    public final String name;
    public final ToolMaterial toolMaterial;
    public final TagKey<Block> blockTag;
    private final ToolValues[] toolValues;

    private ToolTier(
            String name,
            ToolMaterial toolMaterial,
            ToolValues[] toolValues,
            TagKey<Block> blockTag
    ) {
        this.toolMaterial = toolMaterial;
        this.toolValues = toolValues;
        this.name = name;
        this.blockTag = blockTag;
    }

    @Nullable
    public ToolValues getValues(ToolSlot slot) {
        return toolValues[slot.slotIndex];
    }

    public static ToolTier.Builder builder(String name) {
        return new ToolTier.Builder(name);
    }

    //a BuilderWithDefaults class
    public static class Builder {
        private ToolMaterial toolMaterial;
        private final ToolValues[] toolValues = new ToolValues[ToolSlot.values().length];
        private final String name;
        private TagKey<Block> blockTag;

        Builder(String name) {
            this.name = name;
        }

        public Builder blockTag(TagKey<Block> blockTag) {
            this.blockTag = blockTag;
            return this;
        }

        public Builder toolMaterial(ToolMaterial toolMaterial) {
            this.toolMaterial = toolMaterial;
            return this;
        }

        public Builder toolValues(ToolSlot slot, ToolValues toolValues) {
            this.toolValues[slot.slotIndex] = toolValues;
            return this;
        }

        public Builder toolValuesWithOffset(ToolTier source, ToolValues offset) {
            for (int i = 0; i < toolValues.length; i++) {
                if (source.toolValues[i] != null)
                    this.toolValues[i] = source.toolValues[i].copyWithOffset(offset);

            }
            return this;
        }

        public ToolTier build() {
            return new ToolTier(name, toolMaterial, toolValues, blockTag);
        }
    }

    @Override
    public String toString() {
        return "ToolTier - " + this.name;
    }

    /**
     * Create a new ToolTier for the specified Tier where all Values are offset by the given amount
     *
     * @param newName New Name to use for the copy
     * @param newTier New Tier to use or null if the one from this ToolTier should be used
     * @param offset  Offset to apply to all values
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
        return new ToolTier(newName, newTier == null ? this.toolMaterial : newTier, newValues, blockTag);
    }
}
