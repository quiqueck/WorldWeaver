package org.betterx.wover.poi.api;

import org.betterx.wover.poi.impl.PoiManagerImpl;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.border.WorldBorder;

import com.google.common.collect.ImmutableSet;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;

/**
 * Wraps a registered vanilla {@link PoiType} together with the data it was registered with, and offers
 * convenience methods for tagging and locating instances of it in a world. Obtained from
 * {@link org.betterx.wover.poi.api.PoiManager#register}.
 */
public class WoverPoiType {
    /**
     * The resource key identifying the underlying {@link PoiType}.
     */
    public final ResourceKey<PoiType> key;
    /**
     * The underlying vanilla POI type instance.
     */
    public final PoiType type;
    /**
     * The block states that are recognized as this POI type.
     */
    public final Set<BlockState> matchingStates;
    /**
     * The maximum number of villagers/entities that may simultaneously claim this POI.
     */
    public final int maxTickets;
    /**
     * The maximum distance (in blocks) at which this POI is still considered valid/reachable.
     */
    public final int validRange;

    /**
     * Wraps an already-registered {@link PoiType}. Use {@link org.betterx.wover.poi.api.PoiManager#register}
     * instead of calling this constructor directly.
     *
     * @param key            The resource key identifying the POI type
     * @param type           The underlying vanilla POI type instance
     * @param matchingStates The block states recognized as this POI type
     * @param maxTickets     The maximum number of simultaneous claims
     * @param validRange     The maximum valid distance in blocks
     */
    public WoverPoiType(
            ResourceKey<PoiType> key,
            PoiType type,
            Set<BlockState> matchingStates,
            int maxTickets,
            int validRange
    ) {
        this.key = key;
        this.type = type;
        this.matchingStates = matchingStates;
        this.maxTickets = maxTickets;
        this.validRange = validRange;
    }

    /**
     * Gets every possible {@link BlockState} of the given block, useful for building the
     * {@code matchingStates} set passed to {@link org.betterx.wover.poi.api.PoiManager#register}.
     *
     * @param block The block to enumerate states for
     * @return An immutable set of all possible states of the block
     */
    public static Set<BlockState> getBlockStates(Block block) {
        return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
    }

    /**
     * Associates a block tag with this POI type, so datapacks can extend which block states count as this
     * POI by adding them to the tag.
     *
     * @param tag The block tag to associate
     */
    public void setTag(TagKey<Block> tag) {
        PoiManagerImpl.setTag(key, tag);
    }

    /**
     * Finds the closest position of this POI type around {@code center}, within the world border.
     *
     * @param level       The level to search in
     * @param center      The position to search around
     * @param wideSearch  If {@code true}, searches a 128-block radius; otherwise a 16-block radius
     * @param worldBorder The world border results must be within
     * @return The found position, if any
     */
    public Optional<BlockPos> findPoiAround(
            ServerLevel level,
            BlockPos center,
            boolean wideSearch,
            WorldBorder worldBorder
    ) {
        return findPoiAround(key, level, center, wideSearch, worldBorder);
    }

    /**
     * Finds the closest position of this POI type around {@code center}, regardless of occupancy.
     *
     * @param level  The level to search in
     * @param center The position to search around
     * @param radius The search radius in blocks
     * @return The found position, if any
     */
    public Optional<BlockPos> findClosest(
            ServerLevel level,
            BlockPos center,
            int radius
    ) {
        return level.getPoiManager().findClosest(
                holder -> holder.is(this.key),
                (pos) -> true,
                center,
                radius,
                PoiManager.Occupancy.ANY
        );
    }

    /**
     * Finds the closest position of this POI type around {@code center}, within the world border.
     *
     * @param level       The level to search in
     * @param center      The position to search around
     * @param radius      The search radius in blocks
     * @param worldBorder The world border results must be within
     * @return The found position, if any
     */
    public Optional<BlockPos> findPoiAround(
            ServerLevel level,
            BlockPos center,
            int radius,
            WorldBorder worldBorder
    ) {
        return findPoiAround(key, level, center, radius, worldBorder);
    }

    /**
     * Finds the closest position of the given POI type around {@code center}, within the world border.
     *
     * @param key         The resource key of the POI type to search for
     * @param level       The level to search in
     * @param center      The position to search around
     * @param wideSearch  If {@code true}, searches a 128-block radius; otherwise a 16-block radius
     * @param worldBorder The world border results must be within
     * @return The found position, if any
     */
    public static Optional<BlockPos> findPoiAround(
            ResourceKey<PoiType> key,
            ServerLevel level,
            BlockPos center,
            boolean wideSearch,
            WorldBorder worldBorder
    ) {
        return findPoiAround(key, level, center, wideSearch ? 16 : 128, worldBorder);
    }

    /**
     * Finds the closest position of the given POI type around {@code center}, within the world border.
     * <p>
     * Note: results are additionally filtered to block states that have the
     * {@link BlockStateProperties#HORIZONTAL_AXIS} property, so this method only returns matches for POI
     * types whose blocks expose that property.
     *
     * @param key         The resource key of the POI type to search for
     * @param level       The level to search in
     * @param center      The position to search around
     * @param radius      The search radius in blocks
     * @param worldBorder The world border results must be within
     * @return The found position, if any
     */
    public static Optional<BlockPos> findPoiAround(
            ResourceKey<PoiType> key,
            ServerLevel level,
            BlockPos center,
            int radius,
            WorldBorder worldBorder
    ) {
        PoiManager poiManager = level.getPoiManager();

        poiManager.ensureLoadedAndValid(level, center, radius);
        Optional<PoiRecord> record = poiManager
                .getInSquare(holder -> holder.is(key), center, radius, PoiManager.Occupancy.ANY)
                .filter(poiRecord -> worldBorder.isWithinBounds(poiRecord.getPos()))
                .sorted(Comparator.<PoiRecord>comparingDouble(poiRecord -> poiRecord.getPos().distSqr(center))
                                  .thenComparingInt(poiRecord -> poiRecord.getPos().getY()))
                .filter(poiRecord -> level.getBlockState(poiRecord.getPos())
                                          .hasProperty(BlockStateProperties.HORIZONTAL_AXIS))
                .findFirst();

        return record.map(poiRecord -> poiRecord.getPos());
    }
}
