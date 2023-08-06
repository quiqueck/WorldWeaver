package org.betterx.wover.feature.api.configured.configurators;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/**
 * A {@link FeatureConfigurator} that configures a patch distribution.
 *
 * @param <FC> The feature configuration
 * @param <F>  The feature
 * @param <W>  The configurator itself
 */
public interface BasePatch<FC extends FeatureConfiguration, F extends Feature<FC>, W extends BasePatch<FC, F, W>> {
    /**
     * Configures the patch distribution similar to the default nether vegetation.
     *
     * @return the same instance
     */
    W likeDefaultNetherVegetation();
    /**
     * Configures the patch distribution similar to the default nether vegetation with custom spread values.
     *
     * @param xzSpread The spread in the x and z direction
     * @param ySpread  The spread in the height
     * @return the same instance
     */
    W likeDefaultNetherVegetation(int xzSpread, int ySpread);

    /**
     * Configures the patch distribution similar to the default bonemeal spreading.
     *
     * @return the same instance
     */
    default W likeDefaultBonemeal() {
        return this.tries(9)
                   .spreadXZ(3)
                   .spreadY(1);
    }

    /**
     * Number of tries to place the feature. The Feature will try to place itself at most this often.
     * In each iteration a random position is selected, and if the
     * {@link net.minecraft.world.level.levelgen.placement.PlacementModifier}s allow it, the feature is
     * placed.
     *
     * @param tries The number of tries
     * @return the same instance
     */
    W tries(int tries);

    /**
     * The horizontal spread of the patch. The patch will be placed in a square with the given side radius.
     *
     * @param spread The radius of the square
     * @return the same instance
     */
    W spreadXZ(int spread);
    /**
     * The vertical spread of the patch. The patch will be placed in a square with the given side radius.
     * <p>
     * The game will select a spawn position for the feature. The Feature itself will generate new locations
     * in a volume around that spawn position, and try to place features. The height spread is used to account
     * for the surface height changes in the volume.
     *
     * @param spread The radius of the square
     * @return the same instance
     */
    W spreadY(int spread);
}
