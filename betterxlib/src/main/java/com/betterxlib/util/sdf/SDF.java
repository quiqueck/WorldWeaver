package com.betterxlib.util.sdf;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

import java.util.function.Function;

/**
 * Signed Distance Function interface for procedural shape generation.
 * <p>
 * SDFs define shapes mathematically by returning the distance from any
 * point to the nearest surface of the shape. Points inside have negative
 * distance, points outside have positive distance.
 * <p>
 * Example usage:
 * <pre>{@code
 * // Create a sphere
 * SDF sphere = SDF.sphere(5.0f);
 *
 * // Union with a box
 * SDF combined = sphere.union(SDF.box(3.0f, 3.0f, 3.0f));
 *
 * // Fill in the world
 * combined.fill(level, BlockPos.ZERO, Blocks.STONE.defaultBlockState());
 * }</pre>
 */
@FunctionalInterface
public interface SDF {

    /**
     * Evaluate the signed distance at a point.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @return signed distance (negative = inside, positive = outside)
     */
    float distance(float x, float y, float z);

    /**
     * Evaluate the signed distance at a point.
     *
     * @param pos the position
     * @return signed distance
     */
    default float distance(Vector3f pos) {
        return distance(pos.x, pos.y, pos.z);
    }

    // Primitive shapes

    /**
     * Create a sphere SDF.
     *
     * @param radius the sphere radius
     * @return a sphere SDF
     */
    static SDF sphere(float radius) {
        return (x, y, z) -> (float) Math.sqrt(x * x + y * y + z * z) - radius;
    }

    /**
     * Create a box SDF.
     *
     * @param halfX half-width on X axis
     * @param halfY half-width on Y axis
     * @param halfZ half-width on Z axis
     * @return a box SDF
     */
    static SDF box(float halfX, float halfY, float halfZ) {
        return (x, y, z) -> {
            float dx = Math.abs(x) - halfX;
            float dy = Math.abs(y) - halfY;
            float dz = Math.abs(z) - halfZ;
            float outside = (float) Math.sqrt(
                Math.max(0, dx) * Math.max(0, dx) +
                Math.max(0, dy) * Math.max(0, dy) +
                Math.max(0, dz) * Math.max(0, dz)
            );
            float inside = Math.min(0, Math.max(dx, Math.max(dy, dz)));
            return outside + inside;
        };
    }

    /**
     * Create a cylinder SDF along the Y axis.
     *
     * @param radius the cylinder radius
     * @param height the half-height
     * @return a cylinder SDF
     */
    static SDF cylinder(float radius, float height) {
        return (x, y, z) -> {
            float d = (float) Math.sqrt(x * x + z * z) - radius;
            float dy = Math.abs(y) - height;
            return Math.min(Math.max(d, dy), 0.0f) +
                   (float) Math.sqrt(Math.max(d, 0) * Math.max(d, 0) + Math.max(dy, 0) * Math.max(dy, 0));
        };
    }

    /**
     * Create a capsule SDF (cylinder with spherical caps).
     *
     * @param radius the capsule radius
     * @param height the half-height of the cylindrical section
     * @return a capsule SDF
     */
    static SDF capsule(float radius, float height) {
        return (x, y, z) -> {
            float py = Math.abs(y) - height;
            py = Math.max(py, 0);
            return (float) Math.sqrt(x * x + py * py + z * z) - radius;
        };
    }

    /**
     * Create a torus SDF.
     *
     * @param majorRadius the major (ring) radius
     * @param minorRadius the minor (tube) radius
     * @return a torus SDF
     */
    static SDF torus(float majorRadius, float minorRadius) {
        return (x, y, z) -> {
            float q = (float) Math.sqrt(x * x + z * z) - majorRadius;
            return (float) Math.sqrt(q * q + y * y) - minorRadius;
        };
    }

    /**
     * Create a plane SDF at y=0.
     *
     * @return a plane SDF
     */
    static SDF plane() {
        return (x, y, z) -> y;
    }

    // Operations

    /**
     * Union of two SDFs (additive combination).
     *
     * @param other the other SDF
     * @return the union SDF
     */
    default SDF union(SDF other) {
        return (x, y, z) -> Math.min(distance(x, y, z), other.distance(x, y, z));
    }

    /**
     * Intersection of two SDFs.
     *
     * @param other the other SDF
     * @return the intersection SDF
     */
    default SDF intersection(SDF other) {
        return (x, y, z) -> Math.max(distance(x, y, z), other.distance(x, y, z));
    }

    /**
     * Subtraction of another SDF from this one.
     *
     * @param other the SDF to subtract
     * @return the difference SDF
     */
    default SDF subtract(SDF other) {
        return (x, y, z) -> Math.max(distance(x, y, z), -other.distance(x, y, z));
    }

    /**
     * Smooth union of two SDFs.
     *
     * @param other the other SDF
     * @param k the smoothing factor (0 = sharp, higher = smoother)
     * @return the smooth union SDF
     */
    default SDF smoothUnion(SDF other, float k) {
        return (x, y, z) -> {
            float d1 = distance(x, y, z);
            float d2 = other.distance(x, y, z);
            float h = Math.max(k - Math.abs(d1 - d2), 0) / k;
            return Math.min(d1, d2) - h * h * h * k / 6.0f;
        };
    }

    // Transformations

    /**
     * Translate the SDF.
     *
     * @param tx translation on X
     * @param ty translation on Y
     * @param tz translation on Z
     * @return the translated SDF
     */
    default SDF translate(float tx, float ty, float tz) {
        return (x, y, z) -> distance(x - tx, y - ty, z - tz);
    }

    /**
     * Scale the SDF uniformly.
     *
     * @param s the scale factor
     * @return the scaled SDF
     */
    default SDF scale(float s) {
        return (x, y, z) -> distance(x / s, y / s, z / s) * s;
    }

    /**
     * Round the edges of the SDF.
     *
     * @param radius the rounding radius
     * @return the rounded SDF
     */
    default SDF round(float radius) {
        return (x, y, z) -> distance(x, y, z) - radius;
    }

    /**
     * Make the SDF hollow with a shell thickness.
     *
     * @param thickness the shell thickness
     * @return the hollow SDF
     */
    default SDF shell(float thickness) {
        return (x, y, z) -> Math.abs(distance(x, y, z)) - thickness;
    }

    // World filling

    /**
     * Fill blocks in the world where the SDF is negative.
     *
     * @param level the world
     * @param center the center position
     * @param state the block state to place
     * @param radius the maximum radius to check
     */
    default void fill(LevelAccessor level, BlockPos center, BlockState state, int radius) {
        fill(level, center, pos -> state, radius);
    }

    /**
     * Fill blocks in the world where the SDF is negative.
     *
     * @param level the world
     * @param center the center position
     * @param stateFunction function to determine block state at each position
     * @param radius the maximum radius to check
     */
    default void fill(LevelAccessor level, BlockPos center, Function<BlockPos, BlockState> stateFunction, int radius) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (distance(dx, dy, dz) <= 0) {
                        mutable.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                        BlockState state = stateFunction.apply(mutable);
                        if (state != null) {
                            level.setBlock(mutable, state, Block.UPDATE_ALL);
                        }
                    }
                }
            }
        }
    }
}
