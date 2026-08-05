package de.ambertation.wover.enchantment.api;

import de.ambertation.wover.enchantment.impl.EnchantmentKeyImpl;
import de.ambertation.wover.enchantment.impl.EnchantmentManagerImpl;
import de.ambertation.wover.events.api.Event;
import de.ambertation.wover.events.api.types.OnBootstrapRegistry;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.function.UnaryOperator;

/**
 * Central manager for enchantment registration and effect component management.
 *
 * <p>This class provides the main class for enchantment-related operations in the Wover framework.
 * It handles the creation of enchantment keys, registration of enchantment effect components, and
 * provides access to bootstrap events for enchantment registration.
 *
 * <h2>Key Features</h2>
 * <ul>
 *   <li>Enchantment key creation and management</li>
 *   <li>Effect component registration for enchantments</li>
 *   <li>Bootstrap event integration for data-driven enchantment registration</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>Creating an Enchantment Key</h3>
 * <pre class="java">
 * ModCore C = ModCore.create("mymod");
 * EnchantmentKey myEnchantment = EnchantmentManager.createKey(
 *     C.id("special_enchantment")
 * );
 * </pre>
 *
 * <h3>Registering Effect Components</h3>
 * <pre class="java">
 * DataComponentType&lt;Float&gt; damageComponent = EnchantmentManager.registerEffectComponent(
 *     C.id("damage_multiplier"),
 *     builder -> builder.persistent(Codec.FLOAT)
 * );
 * </pre>
 *
 * @see EnchantmentKey
 * @see OnBootstrapRegistry
 */
public class EnchantmentManager {
    /**
     * Event fired during enchantment bootstrap phase.
     *
     * <p>Use this event to register your custom enchantments during the data generation
     * or bootstrap phase. This ensures proper timing and integration with Minecraft's
     * enchantment registration system.
     */
    public static final Event<OnBootstrapRegistry<Enchantment>> BOOTSTRAP_ENCHANTMENTS = EnchantmentManagerImpl.BOOTSTRAP_ENCHANTMENTS;

    /**
     * Creates a new enchantment key for the specified resource location.
     *
     * <p>The enchantment key serves as a type-safe reference to an enchantment that can be
     * used for registration and holder retrieval across different contexts.
     *
     * @param enchantmentId The resource location identifying the enchantment
     * @return A new enchantment key instance
     */
    public static EnchantmentKey createKey(Identifier enchantmentId) {
        return new EnchantmentKeyImpl(ResourceKey.create(Registries.ENCHANTMENT, enchantmentId));
    }

    /**
     * Registers a new enchantment effect component type.
     *
     * <p>Effect components are used to store additional data for enchantments, such as
     * damage multipliers, special effects, or other custom properties. This method
     * provides a convenient way to register these components with proper typing.
     *
     * @param <T>                 The type of data stored in the component
     * @param componentId         The resource location identifying the component
     * @param builderConfigurator Function to configure the component type builder
     * @return The registered data component type
     */
    public static <T> DataComponentType<T> registerEffectComponent(
            Identifier componentId,
            UnaryOperator<DataComponentType.Builder<T>> builderConfigurator
    ) {
        return Registry.register(
                BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, componentId, builderConfigurator
                        .apply(DataComponentType.builder())
                        .build()
        );
    }
}
