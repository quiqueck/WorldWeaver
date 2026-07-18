package org.betterx.wover.item.api;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.AbstractBoat;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.entity.vehicle.ChestRaft;
import net.minecraft.world.entity.vehicle.Raft;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.level.Level;

/**
 * Specialized configuration class for creating boat items with an associated boat entity type.
 * This class extends {@link ItemDefinition} to provide boat-specific setup: it automatically
 * registers the {@link EntityType} used by the boat (optionally with a chest) and links it back
 * to the built {@link BoatItem} instance.
 *
 * @param <I> The type of boat item being created, must extend {@link BoatItem}
 * @author Quiqueck
 * @since 21.6.0
 */
public class BoatItemDefinition<I extends BoatItem> extends ItemDefinition<I, BoatItemDefinition<I>> {
    /**
     * Pairs the registered boat entity type with the built boat item.
     * Returned by {@link #buildAndRegisterBoat()} as a convenience result of the build/register process.
     *
     * @param entityType The entity type registered for this boat
     * @param item       The registered boat item
     */
    public record BoatType(
            EntityType<? extends AbstractBoat> entityType,
            BoatItem item
    ) {
    }

    /**
     * Factory interface for creating boat items from configuration objects.
     * Extends the base ItemFactory to work specifically with BoatItemDefinition.
     *
     * @param <I> The type of boat item to create
     */
    public interface ItemFactory<I extends BoatItem> extends ItemDefinition.ItemFactory<I, BoatItemDefinition<I>> {
    }

    /**
     * The entity type registered for this boat during {@link #beforeBuild()}
     */
    private EntityType<? extends AbstractBoat> entityType;
    /**
     * The built boat item, set in {@link #beforeRegister(BoatItem)} and referenced by the entity factory
     */
    private BoatItem boatItem;
    /**
     * Whether this boat has a chest (spawns a {@link ChestBoat}/{@link ChestRaft} instead of a plain
     * {@link Boat}/{@link Raft})
     */
    private final boolean withChest;
    /**
     * Whether this is a raft (spawns a {@link Raft}/{@link ChestRaft} instead of a {@link Boat}/{@link ChestBoat})
     */
    private final boolean isRaft;

    /**
     * Creates a new boat item configuration.
     *
     * @param registry    The item registry to use for registration
     * @param itemName    The name identifier for the boat item
     * @param itemFactory The factory used to create the boat item instance
     * @param withChest   Whether this boat should spawn as a chest boat
     */
    protected BoatItemDefinition(
            ItemRegistry registry,
            String itemName,
            ItemFactory<I> itemFactory,
            boolean withChest
    ) {
        this(registry, itemName, itemFactory, withChest, false);
    }

    /**
     * Creates a new boat/raft item configuration.
     *
     * @param registry    The item registry to use for registration
     * @param itemName    The name identifier for the boat item
     * @param itemFactory The factory used to create the boat item instance
     * @param withChest   Whether this boat should spawn as a chest boat/raft
     * @param isRaft      Whether this should spawn a {@link Raft}/{@link ChestRaft} instead of a
     *                    {@link Boat}/{@link ChestBoat}
     */
    protected BoatItemDefinition(
            ItemRegistry registry,
            String itemName,
            ItemFactory<I> itemFactory,
            boolean withChest,
            boolean isRaft
    ) {
        super(registry, itemName, itemFactory);
        this.withChest = withChest;
        this.isRaft = isRaft;
    }

    /**
     * Entity factory used to spawn a plain {@link Boat} for the registered entity type.
     *
     * @param entityType The entity type being spawned
     * @param level      The level the boat is spawned in
     * @return A new {@link Boat} instance backed by this definition's boat item
     */
    @SuppressWarnings("unchecked")
    Boat boatFactory(EntityType<? extends AbstractBoat> entityType, Level level) {
        return new Boat((EntityType<? extends Boat>) entityType, level, () -> this.boatItem);
    }

    /**
     * Entity factory used to spawn a {@link ChestBoat} for the registered entity type.
     *
     * @param entityType The entity type being spawned
     * @param level      The level the boat is spawned in
     * @return A new {@link ChestBoat} instance backed by this definition's boat item
     */
    @SuppressWarnings("unchecked")
    ChestBoat chestBoatFactory(EntityType<? extends AbstractBoat> entityType, Level level) {
        return new ChestBoat((EntityType<? extends ChestBoat>) entityType, level, () -> this.boatItem);
    }

    /**
     * Entity factory used to spawn a plain {@link Raft} for the registered entity type.
     *
     * @param entityType The entity type being spawned
     * @param level      The level the raft is spawned in
     * @return A new {@link Raft} instance backed by this definition's boat item
     */
    @SuppressWarnings("unchecked")
    Raft raftFactory(EntityType<? extends AbstractBoat> entityType, Level level) {
        return new Raft((EntityType<? extends Raft>) entityType, level, () -> this.boatItem);
    }

    /**
     * Entity factory used to spawn a {@link ChestRaft} for the registered entity type.
     *
     * @param entityType The entity type being spawned
     * @param level      The level the raft is spawned in
     * @return A new {@link ChestRaft} instance backed by this definition's boat item
     */
    @SuppressWarnings("unchecked")
    ChestRaft chestRaftFactory(EntityType<? extends AbstractBoat> entityType, Level level) {
        return new ChestRaft((EntityType<? extends ChestRaft>) entityType, level, () -> this.boatItem);
    }

    /**
     * Registers the entity type for this boat before the item is built.
     * Picks a raft or boat entity factory (with or without a chest) depending on {@link #isRaft}/{@link #withChest}.
     */
    @Override
    protected void beforeBuild() {
        properties.stacksTo(1);

        final var entityKey = registry.entityKey(itemKey);
        final EntityType.EntityFactory<? extends AbstractBoat> factory = isRaft
                ? (withChest ? this::chestRaftFactory : this::raftFactory)
                : (withChest ? this::chestBoatFactory : this::boatFactory);

        this.entityType = Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                entityKey,
                EntityType.Builder
                        .of(factory, MobCategory.MISC)
                        .noLootTable()
                        .sized(1.375F, 0.5625F)
                        .eyeHeight(0.5625F)
                        .clientTrackingRange(10)
                        .build(entityKey)
        );
    }

    /**
     * Stores the built item so the boat entity factory can reference it, then returns it unchanged.
     *
     * @param item The built boat item instance
     * @return The boat item instance, unchanged
     */
    @Override
    protected I beforeRegister(I item) {
        this.boatItem = item;
        return item;
    }

    /**
     * Gets the entity type registered for this boat.
     * Only valid after {@link #build()} (or {@link #buildAndRegister()}/{@link #buildAndRegisterBoat()}) has run.
     *
     * @return The registered boat entity type, or {@code null} if the boat has not been built yet
     */
    public EntityType<? extends AbstractBoat> entityType() {
        return this.entityType;
    }

    /**
     * @return {@code true} if this definition spawns a {@link Raft}/{@link ChestRaft} rather than a
     * {@link Boat}/{@link ChestBoat}
     */
    public boolean isRaft() {
        return this.isRaft;
    }

    /**
     * Builds and registers the boat item, returning both the item and its associated entity type.
     * This is a convenience method equivalent to calling {@link #buildAndRegister()} and pairing the
     * result with {@link #entityType()}.
     *
     * @return A {@link BoatType} containing the registered entity type and boat item
     */
    public BoatType buildAndRegisterBoat() {
        var item = super.buildAndRegister();
        return new BoatType(this.entityType, item);
    }
}
