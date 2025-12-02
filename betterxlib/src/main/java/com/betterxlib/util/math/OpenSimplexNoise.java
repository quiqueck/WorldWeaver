package com.betterxlib.util.math;

/**
 * OpenSimplex noise implementation for procedural generation.
 * <p>
 * Based on the OpenSimplex noise algorithm which is a public domain
 * alternative to Perlin/Simplex noise.
 */
public class OpenSimplexNoise {
    private static final double STRETCH_CONSTANT_2D = -0.211324865405187;
    private static final double SQUISH_CONSTANT_2D = 0.366025403784439;
    private static final double STRETCH_CONSTANT_3D = -1.0 / 6.0;
    private static final double SQUISH_CONSTANT_3D = 1.0 / 3.0;

    private static final double NORM_CONSTANT_2D = 47.0;
    private static final double NORM_CONSTANT_3D = 103.0;

    private final short[] perm;
    private final short[] permGradIndex3D;

    private static final double[] GRADIENTS_2D = {
        5, 2, 2, 5,
        -5, 2, -2, 5,
        5, -2, 2, -5,
        -5, -2, -2, -5,
    };

    private static final double[] GRADIENTS_3D = {
        -11, 4, 4, -4, 11, 4, -4, 4, 11,
        11, 4, 4, 4, 11, 4, 4, 4, 11,
        -11, -4, 4, -4, -11, 4, -4, -4, 11,
        11, -4, 4, 4, -11, 4, 4, -4, 11,
        -11, 4, -4, -4, 11, -4, -4, 4, -11,
        11, 4, -4, 4, 11, -4, 4, 4, -11,
        -11, -4, -4, -4, -11, -4, -4, -4, -11,
        11, -4, -4, 4, -11, -4, 4, -4, -11,
    };

    public OpenSimplexNoise() {
        this(System.currentTimeMillis());
    }

    public OpenSimplexNoise(long seed) {
        perm = new short[256];
        permGradIndex3D = new short[256];
        short[] source = new short[256];
        for (short i = 0; i < 256; i++) {
            source[i] = i;
        }

        seed = seed * 6364136223846793005L + 1442695040888963407L;
        seed = seed * 6364136223846793005L + 1442695040888963407L;
        seed = seed * 6364136223846793005L + 1442695040888963407L;

        for (int i = 255; i >= 0; i--) {
            seed = seed * 6364136223846793005L + 1442695040888963407L;
            int r = (int) ((seed + 31) % (i + 1));
            if (r < 0) r += (i + 1);
            perm[i] = source[r];
            permGradIndex3D[i] = (short) ((perm[i] % (GRADIENTS_3D.length / 3)) * 3);
            source[r] = source[i];
        }
    }

    /**
     * Evaluate 2D OpenSimplex noise.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return noise value in range [-1, 1]
     */
    public double eval(double x, double y) {
        double stretchOffset = (x + y) * STRETCH_CONSTANT_2D;
        double xs = x + stretchOffset;
        double ys = y + stretchOffset;

        int xsb = fastFloor(xs);
        int ysb = fastFloor(ys);

        double squishOffset = (xsb + ysb) * SQUISH_CONSTANT_2D;
        double xb = xsb + squishOffset;
        double yb = ysb + squishOffset;

        double xins = xs - xsb;
        double yins = ys - ysb;

        double inSum = xins + yins;

        double dx0 = x - xb;
        double dy0 = y - yb;

        double value = 0;

        // Contribution (1,0)
        double dx1 = dx0 - 1 - SQUISH_CONSTANT_2D;
        double dy1 = dy0 - 0 - SQUISH_CONSTANT_2D;
        double attn1 = 2 - dx1 * dx1 - dy1 * dy1;
        if (attn1 > 0) {
            attn1 *= attn1;
            value += attn1 * attn1 * extrapolate(xsb + 1, ysb + 0, dx1, dy1);
        }

        // Contribution (0,1)
        double dx2 = dx0 - 0 - SQUISH_CONSTANT_2D;
        double dy2 = dy0 - 1 - SQUISH_CONSTANT_2D;
        double attn2 = 2 - dx2 * dx2 - dy2 * dy2;
        if (attn2 > 0) {
            attn2 *= attn2;
            value += attn2 * attn2 * extrapolate(xsb + 0, ysb + 1, dx2, dy2);
        }

        if (inSum <= 1) {
            double zins = 1 - inSum;
            if (zins > xins || zins > yins) {
                if (xins > yins) {
                    double dx3 = dx0 - 1 - 2 * SQUISH_CONSTANT_2D;
                    double dy3 = dy0 + 1 - 2 * SQUISH_CONSTANT_2D;
                    double attn3 = 2 - dx3 * dx3 - dy3 * dy3;
                    if (attn3 > 0) {
                        attn3 *= attn3;
                        value += attn3 * attn3 * extrapolate(xsb + 1, ysb - 1, dx3, dy3);
                    }
                } else {
                    double dx3 = dx0 + 1 - 2 * SQUISH_CONSTANT_2D;
                    double dy3 = dy0 - 1 - 2 * SQUISH_CONSTANT_2D;
                    double attn3 = 2 - dx3 * dx3 - dy3 * dy3;
                    if (attn3 > 0) {
                        attn3 *= attn3;
                        value += attn3 * attn3 * extrapolate(xsb - 1, ysb + 1, dx3, dy3);
                    }
                }
            } else {
                double dx3 = dx0;
                double dy3 = dy0;
                double attn3 = 2 - dx3 * dx3 - dy3 * dy3;
                if (attn3 > 0) {
                    attn3 *= attn3;
                    value += attn3 * attn3 * extrapolate(xsb, ysb, dx3, dy3);
                }
            }
        } else {
            double zins = 2 - inSum;
            if (zins < xins || zins < yins) {
                if (xins > yins) {
                    double dx3 = dx0 - 2 - 2 * SQUISH_CONSTANT_2D;
                    double dy3 = dy0 + 0 - 2 * SQUISH_CONSTANT_2D;
                    double attn3 = 2 - dx3 * dx3 - dy3 * dy3;
                    if (attn3 > 0) {
                        attn3 *= attn3;
                        value += attn3 * attn3 * extrapolate(xsb + 2, ysb + 0, dx3, dy3);
                    }
                } else {
                    double dx3 = dx0 + 0 - 2 * SQUISH_CONSTANT_2D;
                    double dy3 = dy0 - 2 - 2 * SQUISH_CONSTANT_2D;
                    double attn3 = 2 - dx3 * dx3 - dy3 * dy3;
                    if (attn3 > 0) {
                        attn3 *= attn3;
                        value += attn3 * attn3 * extrapolate(xsb + 0, ysb + 2, dx3, dy3);
                    }
                }
            } else {
                double dx3 = dx0 - 1 - 2 * SQUISH_CONSTANT_2D;
                double dy3 = dy0 - 1 - 2 * SQUISH_CONSTANT_2D;
                double attn3 = 2 - dx3 * dx3 - dy3 * dy3;
                if (attn3 > 0) {
                    attn3 *= attn3;
                    value += attn3 * attn3 * extrapolate(xsb + 1, ysb + 1, dx3, dy3);
                }
            }
        }

        return value / NORM_CONSTANT_2D;
    }

    /**
     * Evaluate 3D OpenSimplex noise.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param z z coordinate
     * @return noise value in range [-1, 1]
     */
    public double eval(double x, double y, double z) {
        double stretchOffset = (x + y + z) * STRETCH_CONSTANT_3D;
        double xs = x + stretchOffset;
        double ys = y + stretchOffset;
        double zs = z + stretchOffset;

        int xsb = fastFloor(xs);
        int ysb = fastFloor(ys);
        int zsb = fastFloor(zs);

        double squishOffset = (xsb + ysb + zsb) * SQUISH_CONSTANT_3D;
        double xb = xsb + squishOffset;
        double yb = ysb + squishOffset;
        double zb = zsb + squishOffset;

        double xins = xs - xsb;
        double yins = ys - ysb;
        double zins = zs - zsb;

        double inSum = xins + yins + zins;

        double dx0 = x - xb;
        double dy0 = y - yb;
        double dz0 = z - zb;

        double value = 0;

        // Simplified 3D noise - just evaluate closest 4 corners
        value += contribution3D(xsb, ysb, zsb, dx0, dy0, dz0);
        value += contribution3D(xsb + 1, ysb, zsb, dx0 - 1 - SQUISH_CONSTANT_3D, dy0 - SQUISH_CONSTANT_3D, dz0 - SQUISH_CONSTANT_3D);
        value += contribution3D(xsb, ysb + 1, zsb, dx0 - SQUISH_CONSTANT_3D, dy0 - 1 - SQUISH_CONSTANT_3D, dz0 - SQUISH_CONSTANT_3D);
        value += contribution3D(xsb, ysb, zsb + 1, dx0 - SQUISH_CONSTANT_3D, dy0 - SQUISH_CONSTANT_3D, dz0 - 1 - SQUISH_CONSTANT_3D);

        return value / NORM_CONSTANT_3D;
    }

    private double contribution3D(int xsb, int ysb, int zsb, double dx, double dy, double dz) {
        double attn = 2 - dx * dx - dy * dy - dz * dz;
        if (attn > 0) {
            attn *= attn;
            return attn * attn * extrapolate(xsb, ysb, zsb, dx, dy, dz);
        }
        return 0;
    }

    private double extrapolate(int xsb, int ysb, double dx, double dy) {
        int index = perm[(perm[xsb & 0xFF] + ysb) & 0xFF] & 0x0E;
        return GRADIENTS_2D[index] * dx + GRADIENTS_2D[index + 1] * dy;
    }

    private double extrapolate(int xsb, int ysb, int zsb, double dx, double dy, double dz) {
        int index = permGradIndex3D[(perm[(perm[xsb & 0xFF] + ysb) & 0xFF] + zsb) & 0xFF];
        return GRADIENTS_3D[index] * dx + GRADIENTS_3D[index + 1] * dy + GRADIENTS_3D[index + 2] * dz;
    }

    private static int fastFloor(double x) {
        int xi = (int) x;
        return x < xi ? xi - 1 : xi;
    }
}
