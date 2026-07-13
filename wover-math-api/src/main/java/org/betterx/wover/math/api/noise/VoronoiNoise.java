package org.betterx.wover.math.api.noise;

import org.betterx.wover.math.api.MathHelper;

import net.minecraft.core.BlockPos;

import java.util.Random;

/**
 * A 3D cellular ("Worley"/Voronoi) noise generator.
 * <p>
 * The 3D space is divided into unit cells, each containing one pseudo-random feature point. For
 * any sampled position, this class searches the surrounding 3x3x3 block of cells for the closest
 * feature point. This can be used to generate cell-like patterns (e.g. patchy terrain or biome
 * distributions) and, via {@link #getRandom(double, double, double)}, to derive a
 * position-dependent, deterministic {@link Random} instance seeded from the winning cell.
 * <p>
 * Note: this class is not thread-safe. All instances share a single static {@link Random} that
 * is reseeded on every call, so concurrent calls (e.g. from parallel chunk generation) can
 * interfere with each other.
 */
public class VoronoiNoise {
    private static final Random RANDOM = new Random();
    final int seed;

    /**
     * Creates a new Voronoi noise generator using a seed of {@code 0}.
     */
    public VoronoiNoise() {
        this(0);
    }

    /**
     * Creates a new Voronoi noise generator.
     *
     * @param seed the seed used to derive each cell's feature point
     */
    public VoronoiNoise(int seed) {
        this.seed = seed;
    }

    private int getSeed(int x, int y, int z) {
        int h = seed + x * 374761393 + y * 668265263 + z;
        h = (h ^ (h >> 13)) * 1274126177;
        return h ^ (h >> 16);
    }

    /**
     * Calculates the distance from the given position to the nearest feature point.
     *
     * @param x the x coordinate to sample
     * @param y the y coordinate to sample
     * @param z the z coordinate to sample
     * @return the (non-negative) Euclidean distance from {@code (x, y, z)} to the closest feature
     * point among the surrounding cells
     */
    public double sample(double x, double y, double z) {
        int ix = MathHelper.floor(x);
        int iy = MathHelper.floor(y);
        int iz = MathHelper.floor(z);

        float px = (float) (x - ix);
        float py = (float) (y - iy);
        float pz = (float) (z - iz);

        float d = 10;

        for (int pox = -1; pox < 2; pox++) {
            for (int poy = -1; poy < 2; poy++) {
                for (int poz = -1; poz < 2; poz++) {
                    RANDOM.setSeed(getSeed(pox + ix, poy + iy, poz + iz));
                    float pointX = pox + RANDOM.nextFloat();
                    float pointY = poy + RANDOM.nextFloat();
                    float pointZ = poz + RANDOM.nextFloat();
                    float d2 = MathHelper.lengthSqr(pointX - px, pointY - py, pointZ - pz);
                    if (d2 < d) {
                        d = d2;
                    }
                }
            }
        }

        return Math.sqrt(d);
    }

    /**
     * Finds the cell containing the position closest to {@code (x, y, z)} and returns a
     * {@link Random} instance seeded deterministically from that cell's coordinates.
     * <p>
     * Two positions that fall into the same nearest cell will always yield a {@link Random}
     * seeded the same way, which is useful for driving further randomized decisions (e.g. which
     * feature to place) that should be consistent for a whole Voronoi cell.
     *
     * @param x the x coordinate to sample
     * @param y the y coordinate to sample
     * @param z the z coordinate to sample
     * @return a {@link Random} seeded from the position of the nearest feature point. Note that
     * this returns the same shared, mutable instance on every call.
     */
    public Random getRandom(double x, double y, double z) {
        int ix = MathHelper.floor(x);
        int iy = MathHelper.floor(y);
        int iz = MathHelper.floor(z);

        float px = (float) (x - ix);
        float py = (float) (y - iy);
        float pz = (float) (z - iz);

        float d = 10;

        int posX = 0;
        int posY = 0;
        int posZ = 0;

        for (int pox = -1; pox < 2; pox++) {
            for (int poy = -1; poy < 2; poy++) {
                for (int poz = -1; poz < 2; poz++) {
                    RANDOM.setSeed(getSeed(pox + ix, poy + iy, poz + iz));
                    float pointX = pox + RANDOM.nextFloat();
                    float pointY = poy + RANDOM.nextFloat();
                    float pointZ = poz + RANDOM.nextFloat();
                    float d2 = MathHelper.lengthSqr(pointX - px, pointY - py, pointZ - pz);
                    if (d2 < d) {
                        d = d2;
                        posX = pox;
                        posY = poy;
                        posZ = poz;
                    }
                }
            }
        }

        posX += ix;
        posY += iy;
        posZ += iz;

        int seed = MathHelper.getSeed(posY, posX, posZ);
        RANDOM.setSeed(seed);

        return RANDOM;
    }

    /**
     * Finds the feature point closest to {@code (x, y, z)}, along with the feature point that was
     * the closest match just before it (an approximation of the second-nearest point), and
     * returns both as world-space positions scaled by {@code scale}.
     * <p>
     * This can be used to locate both the center of the Voronoi cell containing a position and an
     * approximate neighbouring cell center, e.g. to find the border between two cells.
     *
     * @param x     the x coordinate to sample
     * @param y     the y coordinate to sample
     * @param z     the z coordinate to sample
     * @param scale factor the resulting cell-local coordinates are multiplied by before being
     *              converted to a {@link BlockPos}
     * @return a two-element array: the position of the nearest feature point, followed by the
     * position of the previously-closest (approximate second-nearest) feature point
     */
    public BlockPos[] getPos(double x, double y, double z, double scale) {
        int ix = MathHelper.floor(x);
        int iy = MathHelper.floor(y);
        int iz = MathHelper.floor(z);

        float px = (float) (x - ix);
        float py = (float) (y - iy);
        float pz = (float) (z - iz);

        float d = 10;
        float selX = 0;
        float selY = 0;
        float selZ = 0;
        float selXPre = 0;
        float selYPre = 0;
        float selZPre = 0;

        for (int pox = -1; pox < 2; pox++) {
            for (int poy = -1; poy < 2; poy++) {
                for (int poz = -1; poz < 2; poz++) {
                    RANDOM.setSeed(getSeed(pox + ix, poy + iy, poz + iz));
                    float pointX = pox + RANDOM.nextFloat();
                    float pointY = poy + RANDOM.nextFloat();
                    float pointZ = poz + RANDOM.nextFloat();
                    float d2 = MathHelper.lengthSqr(pointX - px, pointY - py, pointZ - pz);
                    if (d2 < d) {
                        d = d2;
                        selXPre = selX;
                        selYPre = selY;
                        selZPre = selZ;
                        selX = pointX;
                        selY = pointY;
                        selZ = pointZ;
                    }
                }
            }
        }

        BlockPos p1 = new BlockPos(
                (int) ((ix + (double) selX) * scale),
                (int) ((iy + (double) selY) * scale),
                (int) ((iz + (double) selZ) * scale)
        );
        BlockPos p2 = new BlockPos(
                (int) ((ix + (double) selXPre) * scale),
                (int) ((iy + (double) selYPre) * scale),
                (int) ((iz + (double) selZPre) * scale)
        );
        return new BlockPos[]{p1, p2};
    }
}
