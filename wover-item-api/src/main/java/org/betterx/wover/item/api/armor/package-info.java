/**
 * Armor-related utilities and custom armor material creation for the Wover framework.
 *
 * <p>This package provides tools for creating custom armor materials with simplified configuration
 * and validation. It abstracts the complexity of Minecraft's armor system and provides a builder
 * pattern for easy armor material creation.
 *
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link org.betterx.wover.item.api.armor.CustomArmorMaterial} - Builder for custom armor materials</li>
 * </ul>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Simplified armor material creation with fluent builder API</li>
 *   <li>Comprehensive validation of armor material properties</li>
 *   <li>Support for all armor types (helmet, chestplate, leggings, boots, body)</li>
 *   <li>Automatic resource key generation</li>
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
 */
package org.betterx.wover.item.api.armor;
