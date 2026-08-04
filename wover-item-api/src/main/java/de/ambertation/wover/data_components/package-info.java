/**
 * Data component management utilities for the Wover framework.
 *
 * <p>This package provides utilities for registering custom data components and attributes
 * within the Minecraft registry system. It simplifies the process of extending Minecraft's
 * data component system with custom components and attribute types.
 *
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link de.ambertation.wover.data_components.DataComponentManager} - Registry utilities for data components and attributes</li>
 * </ul>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Simplified data component type registration</li>
 *   <li>Attribute registration with holder support</li>
 *   <li>Integration with Minecraft's built-in registries</li>
 *   <li>Type-safe component creation through builder patterns</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>Registering a Data Component</h3>
 * <pre class="java">
 * ModCore C = ModCore.create("mymod");
 * DataComponentType&lt;Integer&gt; CUSTOM_COMPONENT = DataComponentManager.registerDataComponent(
 *     C.id("custom_value"),
 *     builder -> builder.persistent(Codec.INT)
 * );
 * </pre>
 *
 * <h3>Registering an Attribute</h3>
 * <pre class="java">
 * Holder&lt;Attribute&gt; CUSTOM_ATTRIBUTE = DataComponentManager.registerAttribute(
 *     C.id("custom_stat"),
 *     new RangedAttribute("attribute.modid.custom_stat", 0.0, 0.0, 1024.0)
 * );
 * </pre>
 *
 * <h2>Integration</h2>
 * <p>This package integrates directly with Minecraft's registry system and should be used
 * during the appropriate mod initialization phases to ensure proper registration timing.
 */
package de.ambertation.wover.data_components;
