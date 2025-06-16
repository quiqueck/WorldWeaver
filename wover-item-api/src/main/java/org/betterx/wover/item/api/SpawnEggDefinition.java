package org.betterx.wover.item.api;

import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jetbrains.annotations.NotNull;

/**
 * Specialized configuration class for creating spawn egg items.
 * This class extends {@link ItemDefinition} to provide specific functionality for spawn eggs,
 * including automatic dispenser behavior registration and entity type configuration.
 *
 * <p>Spawn eggs in Minecraft have specialized behavior including:</p>
 * <ul>
 *   <li>Spawning specific entities when used</li>
 *   <li>Custom dispenser behavior for automated spawning</li>
 *   <li>Specific colors and textures based on the entity type</li>
 *   <li>Right-click spawning on blocks and in creative mode</li>
 * </ul>
 *
 * <p>This configuration automatically registers the appropriate dispenser behavior
 * when the spawn egg is built and registered.</p>
 *
 * @param <I> The type of spawn egg item being created, must extend {@link SpawnEggItem}
 * @author WorldWeaver
 * @since 1.21.6
 */
public class SpawnEggDefinition<I extends SpawnEggItem> extends ItemDefinition<I, SpawnEggDefinition<I>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpawnEggDefinition.class);

    /**
     * Factory interface for creating spawn egg items from configuration objects.
     * Extends the base ItemFactory to work specifically with SpawnEggDefinition.
     *
     * @param <I> The type of spawn egg item to create
     */
    public interface ItemFactory<I extends SpawnEggItem> extends ItemDefinition.ItemFactory<I, SpawnEggDefinition<I>> {
    }

    /**
     * Default dispenser behavior for spawn eggs that enables automatic entity spawning.
     *
     * <p>This behavior is automatically registered for spawn eggs created through SpawnEggDefinition
     * and handles the following when a spawn egg is dispensed:</p>
     * <ul>
     *   <li>Determines the spawn direction based on the dispenser's facing direction</li>
     *   <li>Extracts the entity type from the spawn egg item</li>
     *   <li>Spawns the entity at the appropriate location relative to the dispenser</li>
     *   <li>Handles spawn positioning (entities don't spawn floating when dispensed upward)</li>
     *   <li>Consumes one spawn egg from the stack</li>
     *   <li>Triggers the ENTITY_PLACE game event</li>
     *   <li>Provides error handling and logging for failed spawning attempts</li>
     * </ul>
     *
     * <p>The spawned entity will appear one block away from the dispenser in the direction
     * the dispenser is facing. If spawning fails for any reason, the error is logged and
     * an empty ItemStack is returned to prevent item duplication.</p>
     *
     * @see DispenserBlock#registerBehavior(net.minecraft.world.item.Item, net.minecraft.core.dispenser.DispenseItemBehavior)
     * @see EntitySpawnReason#DISPENSER
     */
    public static final DefaultDispenseItemBehavior DISPENSE_SPAWN_EGG_BEHAVIOUR = new DefaultDispenseItemBehavior() {
        @Override
        public @NotNull ItemStack execute(BlockSource blockSource, ItemStack stack) {
            Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
            EntityType<?> entityType = ((SpawnEggItem) stack.getItem()).getType(
                    blockSource.level().registryAccess(),
                    stack
            );

            try {
                entityType.spawn(
                        blockSource.level(),
                        stack,
                        null,
                        blockSource.pos().relative(direction),
                        EntitySpawnReason.DISPENSER,
                        direction != Direction.UP,
                        false
                );
            } catch (Exception var6) {
                LOGGER.error("Error while dispensing spawn egg from dispenser at {}", blockSource.pos(), var6);
                return ItemStack.EMPTY;
            }

            stack.shrink(1);
            blockSource.level().gameEvent(null, GameEvent.ENTITY_PLACE, blockSource.pos());
            return stack;
        }
    };

    /**
     * The entity type that this spawn egg will create when used
     */
    protected EntityType<? extends Mob> entityType;

    /**
     * The primary color of the spawn egg
     */
    protected int primaryColor;

    /**
     * The secondary color (spots/pattern) of the spawn egg
     */
    protected int secondaryColor;

    /**
     * Creates a new spawn egg configuration.
     *
     * @param registry    The item registry to use for registration
     * @param eggName     The name identifier for the spawn egg item
     * @param itemFactory The factory used to create the spawn egg item instance
     */
    protected SpawnEggDefinition(
            ItemRegistry registry,
            String eggName,
            ItemDefinition.ItemFactory<I, SpawnEggDefinition<I>> itemFactory
    ) {
        super(registry, eggName, itemFactory);
    }

    /**
     * Called before the spawn egg is built to validate configuration and perform setup.
     * This method validates that required properties like entity type are set before
     * the spawn egg is created.
     *
     * @throws IllegalStateException if the entity type is not set
     */
    @Override
    protected void beforeBuild() {
        if (this.entityType == null) {
            throw new IllegalStateException("Entity type must be set before building spawn egg for: " + this.itemKey);
        }
    }

    /**
     * Called before the spawn egg is registered to set up dispenser behavior and perform modifications.
     * This implementation automatically registers the spawn egg with the dispenser system so that
     * dispensers can spawn entities when the egg is dispensed.
     *
     * @param item The built spawn egg item instance
     * @return The spawn egg item instance with dispenser behavior registered
     */
    @Override
    protected I beforeRegister(I item) {
        DispenserBlock.registerBehavior(item, DISPENSE_SPAWN_EGG_BEHAVIOUR);
        return item;
    }

    /**
     * Sets the entity type that this spawn egg will spawn.
     *
     * @param entityType The entity type to spawn when this egg is used
     * @return This configuration instance for method chaining
     */
    public SpawnEggDefinition<I> entityType(EntityType<? extends Mob> entityType) {
        this.entityType = entityType;
        return this;
    }

    /**
     * Sets the primary color of the spawn egg.
     * This is typically the base color of the egg's texture.
     *
     * @param color The primary color as an RGB integer (0xRRGGBB format)
     * @return This configuration instance for method chaining
     */
    public SpawnEggDefinition<I> primaryColor(int color) {
        this.primaryColor = color;
        return this;
    }

    /**
     * Sets the secondary color of the spawn egg.
     * This is typically used for spots or patterns on the egg's texture.
     *
     * @param color The secondary color as an RGB integer (0xRRGGBB format)
     * @return This configuration instance for method chaining
     */
    public SpawnEggDefinition<I> secondaryColor(int color) {
        this.secondaryColor = color;
        return this;
    }

    /**
     * Sets both the primary and secondary colors of the spawn egg.
     * This is a convenience method for setting both colors at once.
     *
     * @param primaryColor   The primary color as an RGB integer (0xRRGGBB format)
     * @param secondaryColor The secondary color as an RGB integer (0xRRGGBB format)
     * @return This configuration instance for method chaining
     */
    public SpawnEggDefinition<I> colors(int primaryColor, int secondaryColor) {
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        return this;
    }

    /**
     * Gets the configured entity type for this spawn egg.
     *
     * @return The entity type that will be spawned, or null if not set
     */
    public EntityType<? extends Mob> getEntityType() {
        return this.entityType;
    }

    /**
     * Gets the configured primary color for this spawn egg.
     *
     * @return The primary color as an RGB integer
     */
    public int getPrimaryColor() {
        return this.primaryColor;
    }

    /**
     * Gets the configured secondary color for this spawn egg.
     *
     * @return The secondary color as an RGB integer
     */
    public int getSecondaryColor() {
        return this.secondaryColor;
    }

    /**
     * Static factory method that creates a standard SpawnEggItem from the configuration.
     * This method can be used as a default ItemFactory implementation when you don't
     * need a custom spawn egg subclass.
     *
     * <p>Usage example:</p>
     * <pre class="java">
     * SpawnEggItem egg = registry.defineSpawnEgg("my_mob", SpawnEggDefinition::createSpawnEgg)
     *     .entityType(MyEntityTypes.MY_MOB)
     *     .colors(0xFF0000, 0x00FF00)
     *     .buildAndRegister();
     * </pre>
     *
     * @param config The spawn egg configuration containing entity type, colors, and properties
     * @return A new SpawnEggItem instance configured with the provided settings
     */
    public static SpawnEggItem createSpawnEgg(SpawnEggDefinition<SpawnEggItem> config) {
        return new SpawnEggItem(
                config.entityType,
                config.getProperties()
        );
    }
}
