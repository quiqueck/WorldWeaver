package de.ambertation.wover.feature.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import org.joml.Vector3f;

/**
 * The horizontal box a feature is allowed to touch while it is decorating a chunk.
 *
 * <h2>What the zone is</h2>
 * A {@code WorldGenRegion} has always dropped {@code setBlock} calls that land outside the chunks the
 * current {@code ChunkStep} declares it may write to - for {@code ChunkStatus.FEATURES} that is
 * {@code blockStateWriteRadius(1)}, i.e. the 3x3 chunks around the one being decorated. 26.3 added the
 * matching check on the <em>read</em> side: {@code getBlockState}, {@code getFluidState},
 * {@code getHeight} and {@code findBlocksIn} now call {@code warnIfReadOutsideWriteZone} and log
 * "Detected unsafe terrain read during worldgen ... (distance: 2, write radius: 1)".
 * <p>
 * That warning is not cosmetic. The region's chunk cache only guarantees the <em>dependency</em> status
 * at each distance, and {@code FEATURES} declares {@code CARVERS} at distance 1 but only
 * {@code STRUCTURE_STARTS} beyond it - so a read two chunks out returns a chunk with no terrain in it
 * yet. Whatever a feature decides from such a read is decided from empty space, and it is
 * non-deterministic to boot (it depends on how far a neighbouring chunk happened to get).
 * <p>
 * There is no per-feature or per-placed-feature radius to widen: {@code blockStateWriteRadius} is a
 * property of the {@code ChunkStep}, shared by every feature in the game, and neither vanilla nor wover
 * exposes a way for a feature to declare a larger one. Anything a feature wants to build has to be built
 * inside this box.
 *
 * <h2>Clipping versus fitting</h2>
 * {@link #contains(int, int)} / {@link #clip(BoundingBox)} / {@link #toBoundingBox()} <em>clip</em>: they
 * stop a feature from reading or writing outside the zone, which is what correctness requires, but a
 * structure larger than the room it has is then cut off on a flat plane. Foliage and branches end in a
 * wall.
 * <p>
 * {@link #headroom(int, int)}, {@link #fitRadius(int, int, float, float)} and
 * {@link #fitSegment(Vector3f, Vector3f, BlockPos, float)} are the other half: they let a feature
 * <em>fit</em> its geometry to the room it has - a smaller canopy, a shorter branch - so that the clip
 * never has anything left to cut. All three are pure arithmetic; they need no level and make no terrain
 * reads, which is what makes them unit-testable.
 *
 * <h2>Usage</h2>
 * Take the zone once per {@code place()} call. Outside worldgen (sapling growth in a live
 * {@code ServerLevel}, structure worlds, tests) there is no restriction and {@link #UNBOUNDED} is
 * returned, so call sites need no special casing: the fitting helpers then leave every radius and every
 * segment exactly as it was.
 */
public record WriteZone(int minX, int minZ, int maxX, int maxZ) {
    /**
     * The zone used whenever the level is not a {@link WorldGenRegion}: everything is allowed. Kept well
     * inside {@link Integer#MAX_VALUE} so that callers may still add a margin to the bounds without
     * overflowing.
     */
    public static final WriteZone UNBOUNDED = new WriteZone(
            Integer.MIN_VALUE / 2,
            Integer.MIN_VALUE / 2,
            Integer.MAX_VALUE / 2,
            Integer.MAX_VALUE / 2
    );

    /**
     * The zone the given level currently permits.
     * <p>
     * On 26.1 {@code WorldGenRegion} has no {@code isWithinWriteZone(BlockPos)} to probe with: its only
     * public write-side check is {@code ensureCanWrite(BlockPos)}, and that one calls
     * {@code Util.logAndPauseIfInIde(...)} when it fails, so it cannot be used as a quiet predicate - it
     * would spam the log and pause the game in a dev environment. {@code ensureCanWrite} itself, though,
     * compares the same {@code distanceX}/{@code distanceZ} against
     * {@code this.generatingStep.blockStateWriteRadius()}, so the radius is available directly rather
     * than needing to be probed: {@code generatingStep} is a private field on {@code WorldGenRegion}
     * widened here to {@code ChunkStep}, whose {@code blockStateWriteRadius()} is a public record
     * accessor.
     */
    public static WriteZone of(LevelAccessor level) {
        if (!(level instanceof WorldGenRegion region)) {
            return UNBOUNDED;
        }
        final int centerX = region.getCenter().x();
        final int centerZ = region.getCenter().z();
        // Clamped at zero because the field can legitimately be -1: ChunkStep.Builder defaults it to
        // that and only the steps that actually write blocks ever set it. 26.3's probing loop had that
        // floor implicitly - it starts at 0 and only ever grows - so without the clamp this is the one
        // place the two implementations would disagree, and it would disagree badly: a negative radius
        // makes minX greater than maxX, so contains() is false everywhere and a feature draws nothing
        // at all rather than being confined to the chunk it is decorating.
        final int radius = Math.max(0, region.generatingStep.blockStateWriteRadius());
        return new WriteZone(
                SectionPos.sectionToBlockCoord(centerX - radius),
                SectionPos.sectionToBlockCoord(centerZ - radius),
                SectionPos.sectionToBlockCoord(centerX + radius) + 15,
                SectionPos.sectionToBlockCoord(centerZ + radius) + 15
        );
    }

    /**
     * {@code true} for the {@link #UNBOUNDED} zone, i.e. whenever there is no restriction to honour.
     * Compared by value rather than by identity so that a zone deserialized or rebuilt from the same
     * four components behaves the same way.
     */
    public boolean isUnbounded() {
        return this.equals(UNBOUNDED);
    }

    public boolean contains(int x, int z) {
        return x >= minX && x <= maxX && z >= minZ && z <= maxZ;
    }

    public boolean contains(BlockPos pos) {
        return contains(pos.getX(), pos.getZ());
    }

    public int clampX(int x) {
        return Math.min(Math.max(x, minX), maxX);
    }

    public int clampZ(int z) {
        return Math.min(Math.max(z, minZ), maxZ);
    }

    /**
     * {@code true} when the given box has no overlap with this zone at all, i.e. clipping it would leave
     * nothing to do.
     */
    public boolean isDisjoint(int boxMinX, int boxMinZ, int boxMaxX, int boxMaxZ) {
        return boxMaxX < minX || boxMinX > maxX || boxMaxZ < minZ || boxMinZ > maxZ;
    }

    /**
     * Intersects a block-space box with this zone, keeping its Y range untouched.
     */
    public BoundingBox clip(BoundingBox box) {
        return new BoundingBox(
                clampX(box.minX()), box.minY(), clampZ(box.minZ()),
                clampX(box.maxX()), box.maxY(), clampZ(box.maxZ())
        );
    }

    /**
     * This zone as a Y-unbounded {@link BoundingBox}, for passing as the {@code writeBounds} of BCLib's
     * bounded {@code SDF.fillRecursive}/{@code SplineHelper.fillSpline} overloads - those only ever need
     * the horizontal clip, same as this zone.
     */
    public BoundingBox toBoundingBox() {
        return new BoundingBox(minX, Integer.MIN_VALUE / 2, minZ, maxX, Integer.MAX_VALUE / 2, maxZ);
    }

    /**
     * This zone as a {@link BoundingBox} with an explicit Y range.
     * <p>
     * Note that the Y clamp is <em>not</em> part of the write zone - the restriction 26.3 enforces is
     * purely horizontal. A box built this way therefore also cuts a feature off at the given build
     * heights, which is a separate decision the caller is making on purpose.
     */
    public BoundingBox toBoundingBox(int minY, int maxY) {
        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    /**
     * The largest radius a shape centred at {@code (cx, cz)} can have without leaving this zone.
     * <p>
     * Measured to the outermost block that still belongs to the zone, so a shape of exactly this radius
     * still writes its last column inside. Returns {@code 0} rather than a negative number when the
     * centre itself is outside - "no room" is as far as the answer usefully goes - and
     * {@code Float.MAX_VALUE / 2} for {@link #UNBOUNDED}.
     */
    public float headroom(int cx, int cz) {
        if (isUnbounded()) return Float.MAX_VALUE / 2;
        final float room = Math.min(
                Math.min((float) cx - minX, (float) maxX - cx),
                Math.min((float) cz - minZ, (float) maxZ - cz)
        );
        return Math.max(room, 0.0F);
    }

    /**
     * {@code radius} shrunk to whatever room a shape centred at {@code (cx, cz)} actually has.
     * <p>
     * Returns a negative value when even {@code minRadius} does not fit, so callers can distinguish
     * "draw it smaller" from "skip this one" with a single test:
     * <pre>
     *   float r = zone.fitRadius(cx, cz, crownR, 1.5F);
     *   if (r &lt; 0) continue;          // no room at all - skip this branch
     *   crown(world, pos, r, ...);
     * </pre>
     * The result is never larger than {@code radius}: a shape with room to spare is left alone, which is
     * what keeps the overwhelming majority of canopies bit-identical to what they were before.
     */
    public float fitRadius(int cx, int cz, float radius, float minRadius) {
        final float room = headroom(cx, cz);
        if (room < minRadius) return -1.0F;
        return Math.min(radius, room);
    }

    /**
     * Pulls {@code end} back along the segment from {@code start} until a capsule of the given radius
     * around the whole segment fits inside this zone.
     * <p>
     * {@code start} and {@code end} are offsets from {@code origin}, the convention BCLib's
     * {@code SplineHelper} uses. Returns a copy of {@code start} when even the start does not fit, and a
     * copy of {@code end} when nothing needs to change.
     * <p>
     * Length-only: the result is always {@code start + t * (end - start)} for some {@code t} in
     * {@code [0, 1]}, so the direction is never changed and a radial fan of branches keeps its angles.
     * Re-aiming a branch into the direction with the most room would destroy that fan, and the fan is
     * what makes these structures read as trees.
     */
    public Vector3f fitSegment(Vector3f start, Vector3f end, BlockPos origin, float radius) {
        if (isUnbounded()) return new Vector3f(end);

        // The set of centres a capsule of this radius may have is the zone shrunk by `radius` on every
        // side. It is a box, hence convex, so the valid part of the segment is the prefix [0, t] and a
        // plain slab clip finds t.
        final float loX = minX + radius;
        final float hiX = maxX - radius;
        final float loZ = minZ + radius;
        final float hiZ = maxZ - radius;

        final float sx = origin.getX() + start.x();
        final float sz = origin.getZ() + start.z();
        if (sx < loX || sx > hiX || sz < loZ || sz > hiZ) {
            return new Vector3f(start);
        }

        final float ex = origin.getX() + end.x();
        final float ez = origin.getZ() + end.z();
        float t = Math.min(1.0F, Math.min(axisLimit(sx, ex, loX, hiX), axisLimit(sz, ez, loZ, hiZ)));
        if (t >= 1.0F) return new Vector3f(end);
        if (t <= 0.0F) return new Vector3f(start);

        return new Vector3f(
                start.x() + (end.x() - start.x()) * t,
                start.y() + (end.y() - start.y()) * t,
                start.z() + (end.z() - start.z()) * t
        );
    }

    /**
     * How far along {@code s -> e} one may travel before leaving {@code [lo, hi]}, given that {@code s}
     * is already inside it. {@code 1} (i.e. "no limit from this axis") when the segment does not move.
     */
    private static float axisLimit(float s, float e, float lo, float hi) {
        final float d = e - s;
        if (d > 0.0F) return (hi - s) / d;
        if (d < 0.0F) return (lo - s) / d;
        return 1.0F;
    }
}
