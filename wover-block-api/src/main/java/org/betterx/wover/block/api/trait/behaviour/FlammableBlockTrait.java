package org.betterx.wover.block.api.trait.behaviour;

import org.betterx.wover.block.api.trait.GenericBlockTrait;

/**
 * A trait that makes a block ignitable by lava and registers it with Fabric's
 * {@code FlammableBlockRegistry} so it can burn and spread fire, mirroring
 * {@link org.betterx.wover.block.api.BlockDefinition#ignitedByLava()} combined with
 * {@link org.betterx.wover.block.api.BlockRegistry#registerAsFlammable(net.minecraft.world.level.block.Block, int, int)}.
 * <p>
 * Obtain instances from {@code FlammableBlockBuilder.BUILDER} (in {@code wover-block-api}'s impl package),
 * e.g. {@code FlammableBlockBuilder.BUILDER.withDefault()} for the default burn/spread chance of 5, or
 * {@code FlammableBlockBuilder.BUILDER.with(burn, speed)} for a custom chance.
 */
public interface FlammableBlockTrait extends GenericBlockTrait {
    /**
     * Builder for {@link FlammableBlockTrait} instances.
     */
    interface Builder extends GenericBlockTrait.BuilderWithDefault {
        /**
         * Creates the default flammable trait (burn/spread chance of 5).
         *
         * @return The default trait instance
         */
        FlammableBlockTrait withDefault();

        /**
         * Creates a flammable trait with a custom burn/spread chance.
         *
         * @param burn  The chance (0-300) that the block burns when adjacent to fire
         * @param speed The chance (0-100) that fire spreads to the block
         * @return The trait instance
         */
        FlammableBlockTrait with(int burn, int speed);
    }

    /**
     * Gets the chance that the block burns when adjacent to fire.
     *
     * @return The burn chance
     */
    int burn();

    /**
     * Gets the chance that fire spreads to the block.
     *
     * @return The spread chance
     */
    int speed();
}
