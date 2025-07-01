package org.betterx.wover.testmod.sets.datagen;

import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.model.WoverBlockModelGenerators;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverModelProvider;

import net.minecraft.client.data.models.ItemModelGenerators;

public class TestModelProvider extends WoverModelProvider {
    public TestModelProvider(ModCore modCore) {
        super(modCore);
    }

    @Override
    protected void bootstrapBlockStateModels(WoverBlockModelGenerators generator) {
        BlockModelTrait.bootstrapModels(modCore, generator);
    }

    @Override
    protected void bootstrapItemModels(ItemModelGenerators itemModelGenerator) {

    }
}
