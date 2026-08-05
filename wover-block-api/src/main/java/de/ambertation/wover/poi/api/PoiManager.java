package de.ambertation.wover.poi.api;

import de.ambertation.wover.poi.impl.PoiManagerImpl;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;
import org.jetbrains.annotations.Nullable;

/**
 * Entry point for registering custom points of interest (POI) - the vanilla mechanism villagers/other AI
 * use to find and claim work stations, homes, beds, etc.
 * <p>
 * POI types are registered directly in code (there is no datapack format for them); use
 * {@link WoverPoiType#setTag(net.minecraft.tags.TagKey)} (or the {@code tag} parameter here) to also let
 * datapacks add matching block states to the type via a block tag.
 */
public class PoiManager {
    /**
     * Register a new PoiType
     *
     * @param location       The location of the PoiType
     * @param matchingStates The states that this PoiType should match
     * @param maxTickets     The maximum number of tickets
     * @param validRanges    The valid ranges
     * @return The new PoiType
     */
    public static WoverPoiType register(
            Identifier location,
            Set<BlockState> matchingStates,
            int maxTickets,
            int validRanges
    ) {
        return PoiManagerImpl.register(location, matchingStates, maxTickets, validRanges, null);
    }

    /**
     * Register a new PoiType
     *
     * @param location       The location of the PoiType
     * @param matchingStates The states that this PoiType should match
     * @param maxTickets     The maximum number of tickets
     * @param validRanges    The valid ranges
     * @param tag            The tag to associate with this PoiType or null
     * @return The new PoiType
     */
    public static WoverPoiType register(
            Identifier location,
            Set<BlockState> matchingStates,
            int maxTickets,
            int validRanges,
            @Nullable TagKey<Block> tag
    ) {
        return PoiManagerImpl.register(location, matchingStates, maxTickets, validRanges, tag);
    }
}
