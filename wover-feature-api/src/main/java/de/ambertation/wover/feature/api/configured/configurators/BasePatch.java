package de.ambertation.wover.feature.api.configured.configurators;


/**
 * A {@link FeatureConfigurator} that configures a patch distribution.
 * <p>
 * 26.3 collapsed {@code ConfiguredFeature} into {@link net.minecraft.world.level.levelgen.feature.Feature},
 * which is non-generic and carries its own configuration, so the {@code <FC>}/{@code <F>} type parameters
 * were dropped. Only the self-type is left.
 *
 * @param <W> The configurator itself
 */
public interface BasePatch<W extends BasePatch<W>> {
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
