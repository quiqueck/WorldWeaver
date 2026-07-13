package org.betterx.wover.block.api.client.trait;

import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitBuilder;

import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.Block;

/**
 * A {@link BlockTrait} attaching the client-side render {@link Material}s (single/left/right chest halves) a
 * custom chest block needs to be drawn by a chest-style block entity renderer.
 */
public interface ChestRenderTrait extends BlockTrait<Block, ChestRenderTrait> {
    /**
     * The three chest-renderer materials needed to draw a (double) chest.
     *
     * @param single the single-chest material
     * @param left   the left half of a double chest
     * @param right  the right half of a double chest
     */
    record ChestMaterialSet(Material single, Material left, Material right) {
    }

    /**
     * Builds {@link ChestRenderTrait} instances.
     */
    interface Builder extends BlockTraitBuilder.WithDefault<Block, ChestRenderTrait> {

    }

    /**
     * @return the render materials for this chest
     */
    ChestMaterialSet getMaterial();
}
