package de.ambertation.wover.data_components;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.function.UnaryOperator;

/**
 * Manager for registering custom data components and attributes in the Wover framework.
 *
 * <p>This class provides utilities for extending Minecraft's data component system with custom
 * components and attribute types. It simplifies the registration process and ensures proper
 * integration with Minecraft's built-in registries.
 *
 * <h2>Key Features</h2>
 * <ul>
 *   <li>Custom data component type registration</li>
 *   <li>Attribute registration with holder support</li>
 *   <li>Type-safe component creation</li>
 *   <li>Integration with Minecraft's registry system</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>Registering a Data Component</h3>
 * <pre class="java">
 * ModCore C = ModCore.create("mymod");
 * DataComponentType&lt;Integer&gt; customValue = DataComponentManager.registerDataComponent(
 *     C.id("custom_value"),
 *     builder -> builder.persistent(Codec.INT)
 * );
 * </pre>
 *
 * <h3>Registering an Attribute</h3>
 * <pre class="java">
 * Holder&lt;Attribute&gt; customAttribute = DataComponentManager.registerAttribute(
 *     C.id("custom_stat"),
 *     new RangedAttribute("attribute.mymod.custom_stat", 0.0, 0.0, 1024.0)
 * );
 * </pre>
 *
 * @see DataComponentType
 * @see net.minecraft.world.entity.ai.attributes.Attribute
 */
public class DataComponentManager {
    /**
     * Registers a new data component type in the registry.
     *
     * <p>Data components are used to attach additional data to various game objects.
     * This method provides a convenient way to register custom component types with
     * proper configuration through the builder pattern.
     *
     * @param <T>                 The type of data stored in the component
     * @param componentId         The resource location identifying the component
     * @param builderConfigurator Function to configure the component type builder
     * @return The registered data component type
     */
    public static <T> DataComponentType<T> registerDataComponent(
            Identifier componentId,
            UnaryOperator<DataComponentType.Builder<T>> builderConfigurator
    ) {
        return Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                componentId,
                builderConfigurator.apply(DataComponentType.builder()).build()
        );
    }

    /**
     * Registers a new attribute in the registry and returns a holder reference.
     *
     * <p>Attributes define various properties and statistics for entities, items, and other
     * game objects. This method registers the attribute and provides a holder that can be
     * used for safe references throughout the game.
     *
     * @param attributeId The resource location identifying the attribute
     * @param attribute   The attribute instance to register
     * @return A holder reference to the registered attribute
     */
    public static Holder<Attribute> registerAttribute(Identifier attributeId, Attribute attribute) {
        return Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, attributeId, attribute);
    }
}
