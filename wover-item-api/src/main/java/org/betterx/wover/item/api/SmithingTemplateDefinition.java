package org.betterx.wover.item.api;

import org.betterx.wover.item.api.smithing.SmithingTemplates;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.SmithingTemplateItem;

import java.util.List;

/**
 * Specialized configuration class for creating smithing template items.
 * This class extends {@link ItemDefinition} to provide specific functionality for smithing templates,
 * including configuration of base slot icons, additional slot icons, and template descriptions.
 *
 * <p>Smithing templates in Minecraft are used to customize armor and tools at smithing tables.
 * They define:</p>
 * <ul>
 *   <li>The appearance of empty slots in the smithing table UI</li>
 *   <li>The description and tooltip text for the template</li>
 *   <li>Which items can be used as base materials</li>
 *   <li>Which items can be used as additional materials</li>
 * </ul>
 *
 * <p>This configuration class automatically handles the creation of smithing templates
 * using the {@link SmithingTemplates} builder system for consistent template creation.</p>
 *
 * @param <I> The type of smithing template item being created, must extend {@link SmithingTemplateItem}
 * @author Quiqueck
 * @since 21.6.0
 */
public class SmithingTemplateDefinition<I extends SmithingTemplateItem> extends ItemDefinition<I, SmithingTemplateDefinition<I>> {
    /**
     * Factory interface for creating smithing template items from configuration objects.
     * Extends the base ItemFactory to work specifically with SmithingTemplateDefinition.
     *
     * @param <I> The type of smithing template item to create
     */
    public interface ItemFactory<I extends SmithingTemplateItem> extends ItemDefinition.ItemFactory<I, SmithingTemplateDefinition<I>> {
    }

    /**
     * List of resource locations for base slot empty icons
     */
    protected List<ResourceLocation> baseSlotEmptyIcons;

    /**
     * List of resource locations for additional slot empty icons
     */
    protected List<ResourceLocation> additionalSlotEmptyIcons;

    /**
     * The template path/name used for generating descriptions
     */
    /**
     * The bare template name, fixed at construction. Everything the item is identified by derives from it -
     * the registry key and the baked id via {@link SmithingTemplates.Builder#itemPath}, the four description
     * keys as-is - so it must not be settable: re-pointing it after construction would move one and not the
     * others. Vanilla goes further and bakes its description Components as static finals, with no path at all.
     */
    protected final String templatePath;

    /**
     * Creates a new smithing template configuration.
     *
     * @param registry     The item registry to use for registration
     * @param templateName The name identifier for the smithing template item
     * @param itemFactory  The factory used to create the smithing template item instance
     */
    protected SmithingTemplateDefinition(
            ItemRegistry registry,
            String templateName,
            ItemDefinition.ItemFactory<I, SmithingTemplateDefinition<I>> itemFactory
    ) {
        // The item registers as <templateName>_smithing_template, matching vanilla
        // (minecraft:netherite_upgrade_smithing_template), while templatePath keeps the bare name for the
        // description keys (item.<ns>.smithing_template.<templateName>.applies_to). This key MUST match the
        // id SmithingTemplates.Builder.build() bakes via Item.Properties#setId - the client resolves the
        // item model from the baked id, so a desync renders the item as a placeholder.
        super(registry, SmithingTemplates.Builder.itemPath(templateName), itemFactory);
        this.templatePath = templateName;
    }

    /**
     * Called before the smithing template is built to validate configuration.
     * This method validates that required properties like slot icons are set before
     * the template is created.
     *
     * @throws IllegalStateException if base slot icons or additional slot icons are not set
     */
    @Override
    protected void beforeBuild() {
        if (this.baseSlotEmptyIcons == null || this.baseSlotEmptyIcons.isEmpty()) {
            throw new IllegalStateException("Base slot empty icons must be set before building smithing template for: " + this.itemKey);
        }
        if (this.additionalSlotEmptyIcons == null || this.additionalSlotEmptyIcons.isEmpty()) {
            throw new IllegalStateException(
                    "Additional slot empty icons must be set before building smithing template for: " + this.itemKey);
        }
    }

    /**
     * Called before the smithing template is registered to allow for any final modifications.
     * This default implementation returns the item unchanged, but subclasses can override
     * this method to perform custom post-creation setup before registration.
     *
     * @param item The built smithing template item instance
     * @return The smithing template item instance (potentially modified) that should be registered
     */
    @Override
    protected I beforeRegister(I item) {
        return item;
    }

    /**
     * Sets the resource locations for the base slot empty icons.
     * These icons are displayed in the smithing table UI when the base slot is empty.
     *
     * @param icons List of resource locations pointing to the base slot empty icon textures
     * @return This configuration instance for method chaining
     */
    public SmithingTemplateDefinition<I> baseSlotEmptyIcons(List<ResourceLocation> icons) {
        this.baseSlotEmptyIcons = icons;
        return this;
    }

    /**
     * Sets the resource locations for the additional slot empty icons.
     * These icons are displayed in the smithing table UI when the additional material slot is empty.
     *
     * @param icons List of resource locations pointing to the additional slot empty icon textures
     * @return This configuration instance for method chaining
     */
    public SmithingTemplateDefinition<I> additionalSlotEmptyIcons(List<ResourceLocation> icons) {
        this.additionalSlotEmptyIcons = icons;
        return this;
    }

    /**
     * Sets both base and additional slot empty icons in one method call.
     * This is a convenience method for setting both icon lists at once.
     *
     * @param baseIcons       List of resource locations for base slot empty icons
     * @param additionalIcons List of resource locations for additional slot empty icons
     * @return This configuration instance for method chaining
     */
    public SmithingTemplateDefinition<I> slotIcons(
            List<ResourceLocation> baseIcons,
            List<ResourceLocation> additionalIcons
    ) {
        this.baseSlotEmptyIcons = baseIcons;
        this.additionalSlotEmptyIcons = additionalIcons;
        return this;
    }

    /**
     * Gets the configured base slot empty icons.
     *
     * @return List of resource locations for base slot empty icons, or null if not set
     */
    public List<ResourceLocation> getBaseSlotEmptyIcons() {
        return this.baseSlotEmptyIcons;
    }

    /**
     * Gets the configured additional slot empty icons.
     *
     * @return List of resource locations for additional slot empty icons, or null if not set
     */
    public List<ResourceLocation> getAdditionalSlotEmptyIcons() {
        return this.additionalSlotEmptyIcons;
    }

    /**
     * Gets the configured template path.
     *
     * @return The template path used for description generation
     */
    public String getTemplatePath() {
        return this.templatePath;
    }

    /**
     * Static factory method that creates a standard SmithingTemplateItem from the configuration.
     * This method can be used as a default ItemFactory implementation when you don't
     * need a custom smithing template subclass.
     *
     * <p>Usage example:</p>
     * <pre class="java">
     * SmithingTemplateItem template = registry.defineSmithingTemplate("my_upgrade", SmithingTemplateDefinition::createSmithingTemplate)
     *     .baseSlotEmptyIcons(List.of(baseIcon1, baseIcon2))
     *     .additionalSlotEmptyIcons(List.of(addIcon1, addIcon2))
     *     .buildAndRegister();
     * </pre>
     *
     * @param config The smithing template configuration containing icons and properties
     * @return A new SmithingTemplateItem instance configured with the provided settings
     */
    public static SmithingTemplateItem createSmithingTemplate(SmithingTemplateDefinition<SmithingTemplateItem> config) {
        return SmithingTemplates
                .create(config.registry.C, config.templatePath)
                .setBaseSlotEmptyIcons(config.baseSlotEmptyIcons)
                .setAdditionalSlotEmptyIcons(config.additionalSlotEmptyIcons)
                .setProperties(config.getProperties())
                .build();
    }
}
