package org.betterx.wover.block.api.client.trait;

import org.betterx.wover.block.api.client.ChestRenderTrait;
import org.betterx.wover.block.impl.client.trait.BlockModelTraitBuilder;
import org.betterx.wover.block.impl.client.trait.ChestRenderTraitBuilder;
import org.betterx.wover.item.api.client.trait.BoatRendererTrait;
import org.betterx.wover.item.impl.client.trait.BoatRendererTraitBuilder;

public class ClientBlockTraits {
    public static final BlockModelTrait.Builder MODEL = BlockModelTraitBuilder.BUILDER;
    public static final BoatRendererTrait.Builder BOAT_RENDERER = BoatRendererTraitBuilder.BUILDER;
    public static final ChestRenderTrait.Builder CHEST_RENDERER = ChestRenderTraitBuilder.BUILDER;
}
