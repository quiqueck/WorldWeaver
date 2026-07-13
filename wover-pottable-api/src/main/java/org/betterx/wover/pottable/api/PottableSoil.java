package org.betterx.wover.pottable.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.ApiStatus;

/**
 * A soil block that a {@link PottablePlant} can be potted on (e.g. inside a flower pot).
 * <p>
 * Instances of this class should never get created directly, they are built when a
 * soil is added to the {@link PottableSoilRegistry}.
 */
public class PottableSoil {
    /**
     * The block that can be used as soil.
     */
    public final ResourceKey<Block> block;

    /**
     * There should not be a need to create instances of this class directly.
     *
     * @param block The block that can be used as soil.
     * @see PottableSoilRegistry#register
     */
    @ApiStatus.Internal
    protected PottableSoil(ResourceKey<Block> block) {
        this.block = block;
    }
}
