package de.ambertation.wover.feature.api;

import net.minecraft.core.BlockPos;

import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure-arithmetic JUnit tests for {@link WriteZone}'s fitting helpers. No level, no registries, no
 * Minecraft bootstrap - run via {@code ./gradlew :wover-feature-api:test}.
 * <p>
 * These guard the contract the tree features rely on to stop being clipped at the write-zone wall:
 * <ul>
 *     <li>a fitted radius never reaches outside the zone, and is never bigger than what was asked for,</li>
 *     <li>a fitted segment only ever gets shorter - never longer, never re-aimed,</li>
 *     <li>{@link WriteZone#UNBOUNDED} changes nothing, so sapling growth in a live level keeps building
 *     the full-size tree,</li>
 *     <li>{@link WriteZone#fitRadius} answers "skip this one" with a negative number and only then.</li>
 * </ul>
 * A regression in any of those is either a tree that is still cut off (radius too large) or a whole
 * species that quietly shrank (radius too small), neither of which is visible from a compile.
 */
class WriteZoneTest {
    /**
     * The zone a feature decorating the chunk at (0,0) gets from vanilla's write radius of 1: the 3x3
     * chunks around it, i.e. blocks [-16..31] on both axes.
     */
    private static final WriteZone ZONE = new WriteZone(-16, -16, 31, 31);

    private static final BlockPos ORIGIN = new BlockPos(8, 64, 8);

    // ---------------------------------------------------------------- headroom

    @Test
    void headroomIsTheDistanceToTheNearestWall() {
        // Dead centre of the 48x48 band: 23 to the +X/+Z wall, 24 to the -X/-Z wall.
        assertEquals(23.0F, ZONE.headroom(8, 8), 1e-6F);
        // Pushed towards +X, the +X wall is the binding one.
        assertEquals(6.0F, ZONE.headroom(25, 8), 1e-6F);
        // Pushed towards -Z, the -Z wall is.
        assertEquals(4.0F, ZONE.headroom(8, -12), 1e-6F);
        // Exactly on the wall: the last legal column, so zero room, not one.
        assertEquals(0.0F, ZONE.headroom(31, 8), 1e-6F);
        assertEquals(0.0F, ZONE.headroom(8, -16), 1e-6F);
    }

    @Test
    void headroomNeverGoesNegativeOutsideTheZone() {
        assertEquals(0.0F, ZONE.headroom(100, 8), 1e-6F);
        assertEquals(0.0F, ZONE.headroom(8, -100), 1e-6F);
    }

    // ---------------------------------------------------------------- fitRadius

    @Test
    void aFittedRadiusNeverLeavesTheBand() {
        // Every centre in and around the zone, every radius a canopy might ask for: whatever comes back
        // must keep the whole disc inside [-16..31] on both axes. This is the property the entire change
        // rests on, so it is checked exhaustively rather than at a handful of points.
        for (int cx = -24; cx <= 40; cx++) {
            for (int cz = -24; cz <= 40; cz++) {
                for (float radius = 0.5F; radius <= 40.0F; radius += 0.5F) {
                    final float fitted = ZONE.fitRadius(cx, cz, radius, 0.0F);
                    if (fitted < 0) {
                        // Only ever for a centre that is itself outside, and only with a positive
                        // minRadius - which this call does not use.
                        fail("fitRadius returned " + fitted + " for minRadius 0 at " + cx + "," + cz);
                    }
                    assertTrue(fitted <= radius, "fitRadius grew the radius at " + cx + "," + cz);
                    if (!ZONE.contains(cx, cz)) {
                        assertEquals(0.0F, fitted, 1e-6F, "a centre outside the zone must get no room");
                        continue;
                    }
                    assertTrue(cx - fitted >= ZONE.minX(), "left the -X wall at " + cx + "," + cz);
                    assertTrue(cx + fitted <= ZONE.maxX(), "left the +X wall at " + cx + "," + cz);
                    assertTrue(cz - fitted >= ZONE.minZ(), "left the -Z wall at " + cx + "," + cz);
                    assertTrue(cz + fitted <= ZONE.maxZ(), "left the +Z wall at " + cx + "," + cz);
                }
            }
        }
    }

    @Test
    void aRadiusThatAlreadyFitsIsLeftAlone() {
        // The common case, and the reason 99% of canopies stay bit-identical: no clamping when there is
        // room to spare.
        assertEquals(10.0F, ZONE.fitRadius(8, 8, 10.0F, 1.5F), 1e-6F);
        assertEquals(23.0F, ZONE.fitRadius(8, 8, 23.0F, 1.5F), 1e-6F);
    }

    @Test
    void fitRadiusReturnsNegativeOnlyWhenEvenTheMinimumDoesNotFit() {
        // 2 blocks of room, 1.5 asked for as a minimum: shrink, do not skip.
        assertEquals(2.0F, ZONE.fitRadius(29, 8, 10.0F, 1.5F), 1e-6F);
        // Exactly the minimum still fits and is handed back unshrunk.
        assertEquals(1.5F, ZONE.fitRadius(29, 8, 1.5F, 1.5F), 1e-6F);
        // One block of room is less than the 1.5 the feature insists on: skip.
        assertTrue(ZONE.fitRadius(30, 8, 10.0F, 1.5F) < 0);
        // A centre outside the zone can never satisfy a positive minimum.
        assertTrue(ZONE.fitRadius(64, 8, 10.0F, 1.5F) < 0);
        // ... but a zero minimum is satisfiable everywhere inside, however tight.
        assertEquals(0.0F, ZONE.fitRadius(31, 31, 10.0F, 0.0F), 1e-6F);
    }

    @Test
    void unboundedClampsNothing() {
        assertEquals(1000.0F, WriteZone.UNBOUNDED.fitRadius(0, 0, 1000.0F, 1.5F), 1e-6F);
        assertEquals(1000.0F, WriteZone.UNBOUNDED.fitRadius(30_000_000, -30_000_000, 1000.0F, 1.5F), 1e-6F);
        assertTrue(WriteZone.UNBOUNDED.headroom(30_000_000, 0) >= Float.MAX_VALUE / 2);
        assertTrue(WriteZone.UNBOUNDED.isUnbounded());
        assertFalse(ZONE.isUnbounded());
    }

    // ---------------------------------------------------------------- fitSegment

    @Test
    void aFittedSegmentOnlyEverGetsShorterAndKeepsItsDirection() {
        // A radial fan of 64 branches of every length, as the tree features build them. For each: the
        // result must be start + t*(end-start) with t in [0,1] - same heading, no more length - and the
        // whole capsule must fit.
        final Vector3f start = new Vector3f(0, 0, 0);
        final float radius = 3.0F;
        for (int i = 0; i < 64; i++) {
            final double angle = i / 64.0 * Math.PI * 2;
            for (float length = 1.0F; length <= 60.0F; length += 1.0F) {
                final Vector3f end = new Vector3f(
                        (float) Math.cos(angle) * length,
                        length * 0.5F,
                        (float) Math.sin(angle) * length
                );
                final Vector3f fitted = ZONE.fitSegment(start, end, ORIGIN, radius);

                final float lenOriginal = new Vector3f(end).sub(start).length();
                final float lenFitted = new Vector3f(fitted).sub(start).length();
                assertTrue(lenFitted <= lenOriginal + 1e-3F, "fitSegment made the branch longer");

                if (lenFitted > 1e-4F) {
                    // Direction preserved: the cross product of the two deltas is zero and they point
                    // the same way.
                    final Vector3f a = new Vector3f(end).sub(start).normalize();
                    final Vector3f b = new Vector3f(fitted).sub(start).normalize();
                    assertEquals(1.0F, a.dot(b), 1e-3F, "fitSegment re-aimed the branch");
                }

                // The capsule around the fitted segment fits: both endpoints have at least `radius` of
                // headroom, and the zone is convex so everything between them does too.
                // (int) truncates towards zero, i.e. towards the origin, so it can only ever add
                // headroom - the assertion stays exact.
                assertTrue(ZONE.headroom(ORIGIN.getX() + (int) fitted.x(), ORIGIN.getZ() + (int) fitted.z())
                        >= radius - 1e-3F, "the fitted endpoint has no room for the branch");
            }
        }
    }

    @Test
    void aSegmentThatAlreadyFitsIsLeftAlone() {
        final Vector3f start = new Vector3f(0, 0, 0);
        final Vector3f end = new Vector3f(5, 10, 5);
        assertEquals(end, ZONE.fitSegment(start, end, ORIGIN, 2.0F));
    }

    @Test
    void aSegmentIsShortenedExactlyToTheWall() {
        // Origin at x=8, so the +X wall (31) is 23 blocks away; with a capsule radius of 3 the tip may
        // reach x=28, i.e. 20 blocks out.
        final Vector3f start = new Vector3f(0, 0, 0);
        final Vector3f end = new Vector3f(40, 0, 0);
        final Vector3f fitted = ZONE.fitSegment(start, end, ORIGIN, 3.0F);
        assertEquals(20.0F, fitted.x(), 1e-3F);
        assertEquals(0.0F, fitted.z(), 1e-6F);
    }

    @Test
    void aSegmentWhoseStartIsAlreadyOutOfRoomCollapsesToItsStart() {
        // Start 1 block from the wall with a 3-block capsule: there is nowhere legal to go, so nothing
        // is drawn rather than something wrong.
        final Vector3f start = new Vector3f(22, 0, 0);
        final Vector3f end = new Vector3f(40, 0, 0);
        assertEquals(start, ZONE.fitSegment(start, end, ORIGIN, 3.0F));
    }

    @Test
    void fitSegmentNeverAliasesItsInputs() {
        // The features hand in spline points they keep using afterwards; returning the same object would
        // let a later scale/rotate mutate the fitted result (or vice versa).
        final Vector3f start = new Vector3f(0, 0, 0);
        final Vector3f end = new Vector3f(5, 10, 5);
        assertNotSame(end, ZONE.fitSegment(start, end, ORIGIN, 2.0F));
        assertNotSame(start, ZONE.fitSegment(start, end, ORIGIN, 60.0F));
    }

    @Test
    void unboundedNeverShortensASegment() {
        final Vector3f start = new Vector3f(0, 0, 0);
        final Vector3f end = new Vector3f(500, 100, -500);
        assertEquals(end, WriteZone.UNBOUNDED.fitSegment(start, end, ORIGIN, 40.0F));
    }

    @Test
    void aCapsuleWiderThanTheZoneDrawsNothing() {
        // 48 blocks wide, so a radius-40 capsule has no legal centre anywhere: the honest answer is the
        // start point, not a segment that is wrong everywhere.
        final Vector3f start = new Vector3f(0, 0, 0);
        final Vector3f end = new Vector3f(10, 0, 0);
        assertEquals(start, ZONE.fitSegment(start, end, ORIGIN, 40.0F));
    }

    // ---------------------------------------------------------------- clipping helpers

    @Test
    void containsMatchesTheInclusiveBand() {
        assertTrue(ZONE.contains(-16, -16));
        assertTrue(ZONE.contains(31, 31));
        assertFalse(ZONE.contains(-17, 0));
        assertFalse(ZONE.contains(0, 32));
        assertTrue(ZONE.contains(new BlockPos(0, 4000, 0)), "the zone must ignore Y entirely");
        assertTrue(ZONE.contains(new BlockPos(0, -4000, 0)), "the zone must ignore Y entirely");
    }

    @Test
    void clampAndDisjointAgreeWithContains() {
        assertEquals(-16, ZONE.clampX(-100));
        assertEquals(31, ZONE.clampX(100));
        assertEquals(0, ZONE.clampX(0));
        assertTrue(ZONE.isDisjoint(40, 0, 50, 10));
        assertTrue(ZONE.isDisjoint(0, -100, 10, -20));
        assertFalse(ZONE.isDisjoint(20, 20, 50, 50));
    }

    @Test
    void toBoundingBoxKeepsTheHorizontalBand() {
        assertEquals(-16, ZONE.toBoundingBox().minX());
        assertEquals(31, ZONE.toBoundingBox().maxZ());
        assertEquals(-64, ZONE.toBoundingBox(-64, 320).minY());
        assertEquals(320, ZONE.toBoundingBox(-64, 320).maxY());
        assertEquals(-16, ZONE.toBoundingBox(-64, 320).minX());
    }
}
