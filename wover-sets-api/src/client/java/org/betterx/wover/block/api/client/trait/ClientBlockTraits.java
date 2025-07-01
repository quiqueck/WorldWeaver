package org.betterx.wover.block.api.client.trait;

import org.betterx.wover.block.impl.client.trait.BlockModelTraitBuilder;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientBlockTraits {
    public static final BlockModelTrait.Builder MODEL = BlockModelTraitBuilder.BUILDER;
}
