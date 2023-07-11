package org.betterx.wover.surface.api.noise;

import org.betterx.wover.surface.impl.numeric.NetherNoiseCondition;
import org.betterx.wover.surface.impl.numeric.RandomIntProvider;

/**
 * Default Numeric Providers.
 *
 * @see NumericProviderRegistry
 */
public class NumericProviders {
    /**
     * A Numeric Provider that returns a random integer sequence in [0..bound).
     * The maximum value is provided as the {@code bound} attribute.
     * <p>
     * You can use this numeric provider in a .json file like this:
     * <pre class="json"> {
     *  "type": "wover:rnd_int",
     *  "range": 2
     * }</pre>
     *
     * @param bound the maximum value of the generated sequence
     *              (exclusive, i.e. the generated values will be in [0..bound))
     * @return a {@link NumericProvider} that generates a random integer in [0..bound)
     */
    public static NumericProvider randomInt(int bound) {
        return RandomIntProvider.max(bound);
    }

    /**
     * A Numeric sequence based on {@link org.betterx.wover.surface.api.Conditions#NETHER_VOLUME_NOISE}.
     * The generated values will be in the bound of [0..5]. There are no configurable attributes
     * <p>
     * All netherNoise providers share the same random source with
     * {@link org.betterx.wover.surface.api.Conditions#NETHER_VOLUME_NOISE}.
     * <p>
     * You can use this numeric provider in a .json file like this:
     * <pre class="json"> {
     *   "type": "wover:nether_noise"
     * }</pre>
     *
     * @return a {@link NumericProvider} that generates a random integer in [0..5]
     */
    public static NumericProvider netherNoise() {
        return NetherNoiseCondition.INSTANCE;
    }

    private NumericProviders() {
    }
}
