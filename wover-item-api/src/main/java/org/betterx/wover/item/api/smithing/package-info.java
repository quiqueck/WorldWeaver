/**
 * Smithing template utilities and creation tools for the Wover framework.
 *
 * <p>This package provides some tools for creating custom smithing templates with
 * predefined slot configurations and automatic localization support. It simplifies the process
 * of creating smithing table templates by providing common slot layouts and a fluent builder API.
 *
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link org.betterx.wover.item.api.smithing.SmithingTemplates} - Builder and utilities for smithing templates</li>
 * </ul>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Predefined empty slot icons for common item types (tools, armor, materials)</li>
 *   <li>Builder pattern for easy template creation</li>
 *   <li>Automatic localization key generation</li>
 *   <li>Consistent formatting for template descriptions</li>
 *   <li>Support for custom slot icon combinations</li>
 * </ul>
 *
 * <h2>Predefined Slot Collections</h2>
 * <ul>
 *   <li>{@code TOOLS} - Sword, pickaxe, axe, hoe, shovel slots</li>
 *   <li>{@code ARMOR} - Helmet, chestplate, leggings, boots slots</li>
 *   <li>{@code ARMOR_AND_TOOLS} - Combined collection of all equipment slots</li>
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
 * <h2>Localization</h2>
 * <p>The builder automatically generates the following localization keys:
 * <ul>
 *   <li>{@code item.modid.smithing_template.template_name.applies_to}</li>
 *   <li>{@code item.modid.smithing_template.template_name.ingredients}</li>
 *   <li>{@code item.modid.smithing_template.template_name.base_slot_description}</li>
 *   <li>{@code item.modid.smithing_template.template_name.additions_slot_description}</li>
 * </ul>
 */
package org.betterx.wover.item.api.smithing;
