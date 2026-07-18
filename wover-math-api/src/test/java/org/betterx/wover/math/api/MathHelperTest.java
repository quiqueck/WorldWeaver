package org.betterx.wover.math.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure-logic JUnit tests for {@link MathHelper}. These need no Minecraft bootstrap and run via
 * {@code ./gradlew :wover-math-api:test}. They pin the numeric contract of the stateless helpers so a
 * refactor cannot silently change world-generation seeding or floor behaviour.
 */
class MathHelperTest {
    @Test
    void floorRoundsTowardsNegativeInfinity() {
        // The whole point of MathHelper.floor over a plain (int) cast: negatives round down, not toward 0.
        assertEquals(0, MathHelper.floor(0.0));
        assertEquals(0, MathHelper.floor(0.9));
        assertEquals(1, MathHelper.floor(1.0));
        assertEquals(-1, MathHelper.floor(-0.5));
        assertEquals(-1, MathHelper.floor(-0.0001));
        assertEquals(-2, MathHelper.floor(-1.5));
        assertEquals(5, MathHelper.floor(5.999));
    }

    @Test
    void lengthSqrIsSumOfSquares() {
        assertEquals(0f, MathHelper.lengthSqr(0, 0, 0));
        assertEquals(14f, MathHelper.lengthSqr(1, 2, 3));
        // Squared length is sign-independent.
        assertEquals(14f, MathHelper.lengthSqr(-1, -2, -3));
        assertEquals(25f, MathHelper.lengthSqr(3, 4, 0));
    }

    @Test
    void getSeedFromSingleValueIsDeterministicAndUnsigned() {
        // Deterministic: same input -> same output.
        assertEquals(MathHelper.getSeed(12345), MathHelper.getSeed(12345));
        // Result is the 32-bit hash zero-extended into a long, so it must never be negative.
        assertTrue(MathHelper.getSeed(-1) >= 0L);
        assertTrue(MathHelper.getSeed(Integer.MIN_VALUE) >= 0L);
        assertTrue(MathHelper.getSeed(Integer.MAX_VALUE) <= 0xFFFFFFFFL);
        assertEquals(0L, MathHelper.getSeed(0));
    }

    @Test
    void positionalSeedsAreDeterministic() {
        // Same seed + coordinates always produce the same value (reproducible world generation).
        assertEquals(MathHelper.getSeed(42, 10, 20), MathHelper.getSeed(42, 10, 20));
        assertEquals(MathHelper.getSeed(42, 10, 20, 30), MathHelper.getSeed(42, 10, 20, 30));
        // Different coordinates should generally hash to different seeds.
        assertNotEquals(MathHelper.getSeed(42, 10, 20), MathHelper.getSeed(42, 20, 10));
        assertNotEquals(MathHelper.getSeed(42, 10, 20, 30), MathHelper.getSeed(42, 10, 20, 31));
    }
}
