package de.ambertation.wover.block.impl.client.render;

import de.ambertation.wover.block.api.render.RenderLayerBinding;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Client-side applier for {@link RenderLayerBinding}.
 * <p>
 * In 1.21.x this walked the block registry once at client init and mapped each block carrying a render-layer
 * binding to its non-solid render layer through Fabric's {@code BlockRenderLayerMap.putBlock(...)}. Minecraft
 * 26.1 removed both the vanilla {@code ItemBlockRenderTypes} registry and Fabric's {@code BlockRenderLayerMap}:
 * a block's render layer now lives in its block-model JSON ({@code "render_type"}) and is baked with the model,
 * so there is no longer any runtime hook to register it against. The applier is therefore a no-op; render layers
 * must be emitted into the model JSON during datagen instead. Registered via {@code wover.client.traits}.
 */
@Environment(EnvType.CLIENT)
public final class ClientBlockRenderBootstrap {
    private ClientBlockRenderBootstrap() {
    }

    /**
     * No-op. See the class documentation: 26.1 drives block render layers from the model JSON
     * ({@code "render_type"}) rather than a runtime registry, so there is nothing to apply at client init.
     */
    public static void applyRenderLayers() {
    }
}
