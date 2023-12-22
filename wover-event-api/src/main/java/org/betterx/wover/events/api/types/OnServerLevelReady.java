package org.betterx.wover.events.api.types;

import org.betterx.wover.events.api.Subscriber;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;


/**
 * Used for subscribers of the
 * {@link org.betterx.wover.events.api.WorldLifecycle#SERVER_LEVEL_READY}
 * event.
 */
@FunctionalInterface
public interface OnServerLevelReady extends Subscriber {
    /**
     * Called when the event is emitted.
     *
     * @param serverLevel The {@link ServerLevel} that was created
     * @param levelKey    The {@link ResourceKey} of the {@link Level} that is managed
     * @param levelStem   The {@link LevelStem} of the {@link Level} that is managed
     * @param seed        The seed that was used to generate the {@link Level}
     */
    void notify(ServerLevel serverLevel, ResourceKey<Level> levelKey, LevelStem levelStem, long seed);
}
