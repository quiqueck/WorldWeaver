package org.betterx.wover.math.api.noise;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure-logic JUnit tests pinning the determinism of WorldWeaver's noise sources. World generation must
 * be reproducible: the same seed and coordinates must always yield the same noise value, or worlds stop
 * regenerating identically. These need no Minecraft bootstrap and run via
 * {@code ./gradlew :wover-math-api:test}.
 */
class NoiseDeterminismTest {
    // A spread of sample coordinates including negatives and non-integers (exercises MathHelper.floor).
    private static final double[][] POINTS_3D = {
            {0.0, 0.0, 0.0}, {1.5, -2.25, 3.75}, {-10.1, 42.0, -0.5}, {100.0, 100.0, 100.0}, {-0.5, -0.5, -0.5}
    };

    @Test
    void openSimplexIsDeterministicForSameSeed() {
        final OpenSimplexNoise a = new OpenSimplexNoise(123456789L);
        final OpenSimplexNoise b = new OpenSimplexNoise(123456789L);

        for (double[] p : POINTS_3D) {
            assertEquals(a.eval(p[0], p[1]), b.eval(p[0], p[1]),
                    "2D eval must be identical for the same seed at (" + p[0] + "," + p[1] + ")");
            assertEquals(a.eval(p[0], p[1], p[2]), b.eval(p[0], p[1], p[2]),
                    "3D eval must be identical for the same seed");
        }
    }

    @Test
    void openSimplexDiffersBetweenSeeds() {
        final OpenSimplexNoise a = new OpenSimplexNoise(1L);
        final OpenSimplexNoise b = new OpenSimplexNoise(2L);

        boolean anyDifferent = false;
        for (double[] p : POINTS_3D) {
            if (a.eval(p[0], p[1], p[2]) != b.eval(p[0], p[1], p[2])) {
                anyDifferent = true;
                break;
            }
        }
        assertTrue(anyDifferent, "Different seeds should produce a different noise field");
    }

    @Test
    void voronoiIsDeterministicForSameSeed() {
        final VoronoiNoise a = new VoronoiNoise(555);
        final VoronoiNoise b = new VoronoiNoise(555);

        for (double[] p : POINTS_3D) {
            assertEquals(a.sample(p[0], p[1], p[2]), b.sample(p[0], p[1], p[2]),
                    "Voronoi sample must be identical for the same seed");
        }
    }

    @Test
    void voronoiSampleIsNonNegative() {
        // sample() returns a Euclidean distance to the nearest feature point, so it is never negative.
        final VoronoiNoise noise = new VoronoiNoise(7);
        for (double[] p : POINTS_3D) {
            assertTrue(noise.sample(p[0], p[1], p[2]) >= 0.0,
                    "Voronoi sample (a distance) must be non-negative");
        }
    }
}
