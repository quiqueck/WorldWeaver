/**
 * Enchantment management and registration utilities for the Wover framework.
 *
 * <p>This package provides a comprehensive system for managing enchantments, including
 * key-based references, holder retrieval, and registration utilities. It abstracts the
 * complexity of Minecraft's enchantment system and provides a clean API for mod developers.
 *
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link org.betterx.wover.enchantment.api.EnchantmentKey} - Type-safe enchantment references</li>
 *   <li>{@link org.betterx.wover.enchantment.api.EnchantmentManager} - Registration and bootstrap utilities</li>
 * </ul>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Type-safe enchantment key system</li>
 *   <li>Flexible holder retrieval from various contexts</li>
 *   <li>Bootstrap event integration for enchantment registration</li>
 *   <li>Custom enchantment effect component registration</li>
 *   <li>Simplified enchantment creation and management</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>Creating an Enchantment Key</h3>
 * <pre class="java">
 * ModCore C = ModCore.create("mymod");
 * EnchantmentKey MY_ENCHANTMENT = EnchantmentManager
 *         .createKey(C.mk("my_enchantment"));
 * </pre>
 *
 * <h3>Registering an Enchantment</h3>
 * <pre class="java">
 * Holder<Attribute> ENCHANTMENT_ATTRIBUTE = DataComponentManager.registerAttribute(
 *     C.id("enchantment_attribute"),
 *     new RangedAttribute("attribute.mymod.custom_range", 0.0, 0.0, 1024.0)
 * );
 *
 * EnchantmentManager.BOOTSTRAP_ENCHANTMENTS.subscribe(context -> {
 *     final HolderGetter<Item> itemGetter = context.lookup(Registries.ITEM);
 *     MY_ENCHANTMENT.register(
 *             context,
 *             Enchantment
 *                     .enchantment(
 *                             Enchantment.definition(
 *                                     itemGetter.getOrThrow(ItemTags.MINING_ENCHANTABLE),
 *                                     10, 5,
 *                                     Enchantment.dynamicCost(20, 20),
 *                                     Enchantment.dynamicCost(120, 20),
 *                                     1,
 *                                     EquipmentSlotGroup.MAINHAND
 *                             )
 *                     )
 *                     .withEffect(
 *                             EnchantmentEffectComponents.ATTRIBUTES,
 *                             new EnchantmentAttributeEffect(
 *                                     ENCHANTMENT_ATTRIBUTE.unwrapKey().orElseThrow().location(),
 *                                     ENCHANTMENT_ATTRIBUTE,
 *                                     new LevelBasedValue.Lookup(
 *                                             List.of(6f, 12f, 18f),
 *                                             new LevelBasedValue.LevelsSquared(9.0F)
 *                                     ),
 *                                     AttributeModifier.Operation.ADD_VALUE
 *                             )
 *                     )
 *     );
 * });
 * </pre>
 *
 * <h3>Retrieving Enchantment Holders</h3>
 * <pre class="java">
 * // From registry access
 * Holder<Enchantment> holder = MY_ENCHANTMENT.getHolder(registryAccess);
 *
 * // From bootstrap context
 * Holder<Enchantment> holder = MY_ENCHANTMENT.getHolder(bootstrapContext);
 * </pre>
 *
 * <h2>Integration</h2>
 * <p>This system integrates with Minecraft's data-driven enchantment system and should be
 * used in conjunction with datapack generation or bootstrap contexts for proper enchantment
 * registration and configuration.
 */
package org.betterx.wover.enchantment.api;
