package com.betterxlib.api.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

/**
 * Helper methods for rendering operations.
 */
public final class RenderHelper {

    /**
     * Full brightness light value for emissive rendering.
     */
    public static final int FULL_BRIGHT = LightTexture.pack(15, 15);

    /**
     * No overlay texture.
     */
    public static final int NO_OVERLAY = OverlayTexture.NO_OVERLAY;

    private RenderHelper() {}

    /**
     * Render a quad with full brightness (emissive).
     *
     * @param poseStack the pose stack
     * @param buffer the buffer source
     * @param texture the texture location
     * @param x1 first corner X
     * @param y1 first corner Y
     * @param z1 first corner Z
     * @param x2 second corner X
     * @param y2 second corner Y
     * @param z2 second corner Z
     * @param u0 texture U start
     * @param v0 texture V start
     * @param u1 texture U end
     * @param v1 texture V end
     * @param r red (0-1)
     * @param g green (0-1)
     * @param b blue (0-1)
     * @param a alpha (0-1)
     */
    public static void renderEmissiveQuad(
        PoseStack poseStack,
        MultiBufferSource buffer,
        ResourceLocation texture,
        float x1, float y1, float z1,
        float x2, float y2, float z2,
        float u0, float v0, float u1, float v1,
        float r, float g, float b, float a
    ) {
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(texture));
        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        // Calculate normal (assuming Y-up quad)
        float nx = 0, ny = 1, nz = 0;

        consumer.addVertex(matrix, x1, y1, z1)
            .setColor(r, g, b, a)
            .setUv(u0, v0)
            .setOverlay(NO_OVERLAY)
            .setLight(FULL_BRIGHT)
            .setNormal(normal, nx, ny, nz);

        consumer.addVertex(matrix, x2, y1, z1)
            .setColor(r, g, b, a)
            .setUv(u1, v0)
            .setOverlay(NO_OVERLAY)
            .setLight(FULL_BRIGHT)
            .setNormal(normal, nx, ny, nz);

        consumer.addVertex(matrix, x2, y2, z2)
            .setColor(r, g, b, a)
            .setUv(u1, v1)
            .setOverlay(NO_OVERLAY)
            .setLight(FULL_BRIGHT)
            .setNormal(normal, nx, ny, nz);

        consumer.addVertex(matrix, x1, y2, z2)
            .setColor(r, g, b, a)
            .setUv(u0, v1)
            .setOverlay(NO_OVERLAY)
            .setLight(FULL_BRIGHT)
            .setNormal(normal, nx, ny, nz);
    }

    /**
     * Convert a packed RGB color to separate components.
     *
     * @param color the packed color
     * @return array of [r, g, b] in range 0-1
     */
    public static float[] unpackColor(int color) {
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        return new float[]{r, g, b};
    }

    /**
     * Pack RGB components into a single int.
     *
     * @param r red (0-1)
     * @param g green (0-1)
     * @param b blue (0-1)
     * @return packed color
     */
    public static int packColor(float r, float g, float b) {
        int ri = (int) (r * 255) & 0xFF;
        int gi = (int) (g * 255) & 0xFF;
        int bi = (int) (b * 255) & 0xFF;
        return (ri << 16) | (gi << 8) | bi;
    }

    /**
     * Interpolate between two colors.
     *
     * @param color1 first color
     * @param color2 second color
     * @param t interpolation factor (0-1)
     * @return interpolated color
     */
    public static int lerpColor(int color1, int color2, float t) {
        float[] c1 = unpackColor(color1);
        float[] c2 = unpackColor(color2);
        return packColor(
            c1[0] + (c2[0] - c1[0]) * t,
            c1[1] + (c2[1] - c1[1]) * t,
            c1[2] + (c2[2] - c1[2]) * t
        );
    }
}
