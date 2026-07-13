package org.betterx.wover.pottable.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import org.jetbrains.annotations.ApiStatus;

/**
 * A plant that can be potted (e.g. inside a flower pot).
 * <p>
 * Instances of this class should never get created directly, they are built when a
 * plant is added to the {@link PottablePlantRegistry}.
 */
public class PottablePlant {
    /**
     * The block that can be potted.
     */
    public final ResourceKey<Block> block;

    /**
     * The soils this plant can be potted on. If empty, the plant can be potted on any
     * registered {@link PottableSoil}.
     */
    public final Optional<TagKey<Block>> validSoils;

    /**
     * There should not be a need to create instances of this class directly.
     *
     * @param block      The block that can be potted.
     * @param validSoils The soils this plant can be potted on.
     * @see PottablePlantRegistry#register
     */
    @ApiStatus.Internal
    protected PottablePlant(ResourceKey<Block> block, Optional<TagKey<Block>> validSoils) {
        this.block = block;
        this.validSoils = validSoils;
    }

    /**
     * Checks if this plant can be potted on the given soil block.
     *
     * @param soil The soil block to check.
     * @return {@code true} if this plant can be potted on the given soil block.
     */
    public boolean isValidSoil(Block soil) {
        return validSoils.map(tag -> soil.builtInRegistryHolder().is(tag)).orElse(true);
    }
}
