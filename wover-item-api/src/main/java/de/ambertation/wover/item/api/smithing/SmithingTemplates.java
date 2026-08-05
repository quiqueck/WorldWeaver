package de.ambertation.wover.item.api.smithing;

import de.ambertation.wover.core.api.ModCore;

import net.minecraft.ChatFormatting;
import net.minecraft.util.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SmithingTemplateItem;

import org.spongepowered.include.com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Utility class for creating custom smithing templates with predefined slot configurations.
 *
 * <p>This class provides a comprehensive system for creating smithing table templates with
 * common slot layouts, automatic localization support, and consistent formatting. It includes
 * predefined empty slot icons for various item types and a fluent builder API for easy template creation.
 *
 * <h2>Key Features</h2>
 * <ul>
 *   <li>Predefined empty slot icons for tools, armor, and materials</li>
 *   <li>Builder pattern for easy template configuration</li>
 *   <li>Automatic localization key generation</li>
 *   <li>Consistent formatting with predefined chat colors</li>
 *   <li>Slot icon combination utilities</li>
 * </ul>
 *
 * <h2>Predefined Slot Collections</h2>
 * <ul>
 *   <li>{@link #TOOLS} - All tool slots (sword, pickaxe, axe, hoe, shovel)</li>
 *   <li>{@link #ARMOR} - All armor slots (helmet, chestplate, leggings, boots)</li>
 *   <li>{@link #ARMOR_AND_TOOLS} - Combined collection of equipment slots</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 * <pre class="java">
 * SmithingTemplateItem template = SmithingTemplates.create(modCore, "upgrade_template")
 *     .setBaseSlotEmptyIcons(SmithingTemplates.ARMOR_AND_TOOLS)
 *     .setAdditionalSlotEmptyIcons(List.of(SmithingTemplates.EMPTY_SLOT_INGOT))
 *     .build();
 * </pre>
 *
 * @see SmithingTemplateItem
 * @see ModCore
 */
public class SmithingTemplates {
    /**
     * Chat formatting used for template titles
     */
    public static final ChatFormatting TITLE_FORMAT = ChatFormatting.GRAY;
    /**
     * Chat formatting used for template descriptions
     */
    public static final ChatFormatting DESCRIPTION_FORMAT = ChatFormatting.BLUE;

    // Armor slot empty icons
    /**
     * Empty slot icon for helmet armor pieces
     */
    public static final Identifier EMPTY_SLOT_HELMET = Identifier.withDefaultNamespace(
            "container/slot/helmet");
    /**
     * Empty slot icon for chestplate armor pieces
     */
    public static final Identifier EMPTY_SLOT_CHESTPLATE = Identifier.withDefaultNamespace(
            "container/slot/chestplate");
    /**
     * Empty slot icon for leggings armor pieces
     */
    public static final Identifier EMPTY_SLOT_LEGGINGS = Identifier.withDefaultNamespace(
            "container/slot/leggings");
    /**
     * Empty slot icon for boots armor pieces
     */
    public static final Identifier EMPTY_SLOT_BOOTS = Identifier.withDefaultNamespace(
            "container/slot/boots");

    // Tool slot empty icons
    /**
     * Empty slot icon for hoe tools
     */
    public static final Identifier EMPTY_SLOT_HOE = Identifier.withDefaultNamespace("container/slot/hoe");
    /**
     * Empty slot icon for axe tools
     */
    public static final Identifier EMPTY_SLOT_AXE = Identifier.withDefaultNamespace("container/slot/axe");
    /**
     * Empty slot icon for sword weapons
     */
    public static final Identifier EMPTY_SLOT_SWORD = Identifier.withDefaultNamespace(
            "container/slot/sword");
    /**
     * Empty slot icon for shovel tools
     */
    public static final Identifier EMPTY_SLOT_SHOVEL = Identifier.withDefaultNamespace(
            "container/slot/shovel");
    /**
     * Empty slot icon for pickaxe tools
     */
    public static final Identifier EMPTY_SLOT_PICKAXE = Identifier.withDefaultNamespace(
            "container/slot/pickaxe");

    // Material slot empty icons
    /**
     * Empty slot icon for ingot materials
     */
    public static final Identifier EMPTY_SLOT_INGOT = Identifier.withDefaultNamespace(
            "container/slot/ingot");
    /**
     * Empty slot icon for redstone dust materials
     */
    public static final Identifier EMPTY_SLOT_REDSTONE_DUST = Identifier.withDefaultNamespace(
            "container/slot/redstone_dust");
    /**
     * Empty slot icon for diamond materials
     */
    public static final Identifier EMPTY_SLOT_DIAMOND = Identifier.withDefaultNamespace(
            "container/slot/diamond");

    /**
     * Predefined collection of all tool slot icons
     */
    public static final List<Identifier> TOOLS = List.of(
            EMPTY_SLOT_SWORD,
            EMPTY_SLOT_PICKAXE,
            EMPTY_SLOT_AXE,
            EMPTY_SLOT_HOE,
            EMPTY_SLOT_SHOVEL
    );

    /**
     * Predefined collection of all armor slot icons
     */
    public static final List<Identifier> ARMOR = List.of(
            EMPTY_SLOT_HELMET,
            EMPTY_SLOT_CHESTPLATE,
            EMPTY_SLOT_LEGGINGS,
            EMPTY_SLOT_BOOTS
    );

    /**
     * Predefined collection combining armor and tool slot icons
     */
    public static final List<Identifier> ARMOR_AND_TOOLS = combine(ARMOR, TOOLS);

    /**
     * Combines multiple resource location lists into a single immutable list.
     *
     * @param sourceLists The lists to combine
     * @return A new immutable list containing all elements from the source lists
     */
    public static List<Identifier> combine(List<Identifier>... sourceLists) {
        final ImmutableList.Builder<Identifier> builder = ImmutableList.builder();
        for (var sourceList : sourceLists) {
            builder.addAll(sourceList);
        }
        return builder.build();
    }

    /**
     * Creates a new builder for a smithing template.
     *
     * @param modCore      The mod core instance for resource location generation
     * @param templatePath The path identifier for this template
     * @return A new builder instance
     */
    public static Builder create(ModCore modCore, String templatePath) {
        return new Builder(modCore, templatePath);
    }

    /**
     * Builder class for creating smithing templates with fluent API.
     *
     * <p>This builder handles the creation of smithing templates with automatic localization
     * key generation and validation of required properties.
     */
    public static class Builder {
        private final ModCore C;
        private final String path;
        private List<Identifier> baseSlotEmptyIcons;
        private List<Identifier> additionalSlotEmptyIcons;
        private Item.Properties properties;
        private ResourceKey<Item> itemKey;

        private Builder(ModCore modCore, String templatePath) {
            this.C = modCore;
            this.path = templatePath;
            this.properties = new Item.Properties();
        }

        /**
         * Sets the empty slot icons for the base (equipment) slot.
         *
         * <p>These icons are displayed in the smithing table's first slot to indicate
         * what types of items can be upgraded (e.g., tools, armor pieces).
         *
         * @param baseSlotIcons List of resource locations for base slot empty icons
         * @return This builder instance for chaining
         */
        public Builder setBaseSlotEmptyIcons(List<Identifier> baseSlotIcons) {
            this.baseSlotEmptyIcons = baseSlotIcons;
            return this;
        }

        /**
         * Sets the empty slot icons for the additional (material) slot.
         *
         * <p>These icons are displayed in the smithing table's second slot to indicate
         * what types of materials can be used for the upgrade (e.g., ingots, gems).
         *
         * @param additionalSlotIcons List of resource locations for additional slot empty icons
         * @return This builder instance for chaining
         */
        public Builder setAdditionalSlotEmptyIcons(List<Identifier> additionalSlotIcons) {
            this.additionalSlotEmptyIcons = additionalSlotIcons;
            return this;
        }

        /**
         * Sets custom item properties for the smithing template.
         *
         * <p>If not set, default properties will be used. The resource ID is taken from
         * {@link #itemKey(ResourceKey)}, or derived from the template path when no key was supplied.
         *
         * @param itemProperties The item properties to use
         * @return This builder instance for chaining
         */
        public Builder setProperties(Item.Properties itemProperties) {
            this.properties = itemProperties;
            return this;
        }

        /**
         * Sets the resource key identifying the template item.
         *
         * <p>Callers that already own the item's identity - such as
         * {@code SmithingTemplateDefinition}, which bakes its key into the properties it hands to
         * {@link #setProperties(Item.Properties)} - must pass it here rather than let this builder
         * re-derive one, so that a single component owns the identity. When no key is supplied, {@link #build()}
         * falls back to deriving it from the template path via {@link #itemPath(String)}.
         *
         * @param itemKey The resource key the built item identifies itself by
         * @return This builder instance for chaining
         */
        public Builder itemKey(ResourceKey<Item> itemKey) {
            this.itemKey = itemKey;
            return this;
        }

        /**
         * Builds the smithing template item with the configured properties.
         *
         * <p>This method validates that all required properties are set and creates
         * the final {@link SmithingTemplateItem} with automatically generated
         * localization keys and proper formatting.
         *
         * <h3>Generated Localization Keys</h3>
         * <ul>
         *   <li>{@code item.modid.smithing_template.template_name.applies_to}</li>
         *   <li>{@code item.modid.smithing_template.template_name.ingredients}</li>
         *   <li>{@code item.modid.smithing_template.template_name.base_slot_description}</li>
         *   <li>{@code item.modid.smithing_template.template_name.additions_slot_description}</li>
         * </ul>
         *
         * @return The configured smithing template item
         * @throws IllegalStateException if required properties are missing
         */
        /**
         * The registry path of the template ITEM, which is the builder's {@code path} plus vanilla's
         * {@code _smithing_template} suffix - {@code netherite_upgrade} is registered as
         * {@code minecraft:netherite_upgrade_smithing_template}, while its description keys stay on the bare
         * path ({@code item.minecraft.smithing_template.netherite_upgrade.applies_to}). The bare path is
         * therefore what callers pass, and both the baked id and the registry key must use this.
         *
         * @param path the builder path
         * @return the item's registry path
         */
        public static String itemPath(String path) {
            return path + "_smithing_template";
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

            // The item's identity is an INPUT here, not something this builder re-derives. A caller that owns
            // the identity (SmithingTemplateDefinition, whose properties already carry the baked id) passes its
            // key via itemKey(), and we bake exactly that - re-deriving one would make this builder a second
            // claimant on the identity, agreeing with the definition only by convention. Only callers with no
            // definition behind them (the deprecated ItemRegistry#registerSmithingTemplateItem path) leave the
            // key unset; for those we still derive it from the template path.
            if (this.itemKey == null) {
                this.itemKey = ResourceKey.create(BuiltInRegistries.ITEM.key(), C.mk(itemPath(path)));
            }
            this.properties = properties.setId(this.itemKey);

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