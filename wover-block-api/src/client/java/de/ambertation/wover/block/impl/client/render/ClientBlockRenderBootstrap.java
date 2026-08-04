package de.ambertation.wover.block.impl.client.render;

import de.ambertation.wover.block.api.render.RenderLayerBinding;
import de.ambertation.wover.block.api.trait.BlockTrait;

import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;

/**
 * Client-side applier for {@link RenderLayerBinding}. Walks the block registry once at client init and maps each
 * block carrying a render-layer binding to its non-solid render layer. This is the
 * {@code BlockRenderLayerMap.putBlock(...)} body the former {@code RenderLayerTrait.afterBlockRegistration(...)}
 * ran per block during registration; moving it to a single client-init pass keeps the mapping identical while
 * letting the binding stay common. Registered via {@code wover.client.traits}.
 */
@Environment(EnvType.CLIENT)
public final class ClientBlockRenderBootstrap {
    private ClientBlockRenderBootstrap() {
    }

    /**
     * Applies every registered block's render-layer binding to {@code BlockRenderLayerMap}.
     */
    public static void applyRenderLayers() {
        for (Block block : BuiltInRegistries.BLOCK) {
            BlockTrait.runtimeTraits(block)
                      .filter(trait -> trait.is(RenderLayerBinding.RENDER_LAYER_KEY))
                      .forEach(trait -> {
                          if (trait instanceof RenderLayerBinding binding) {
                              switch (binding.layer()) {
                                  case CUTOUT -> BlockRenderLayerMap.putBlock(block, ChunkSectionLayer.CUTOUT);
                                  case TRANSLUCENT -> BlockRenderLayerMap.putBlock(block, ChunkSectionLayer.TRANSLUCENT);
                              }
                          }
                      });
        }
    }
}
