package de.ambertation.wover.testmod.sets.datagen;

import de.ambertation.wover.block.api.client.trait.BlockModelTrait;
import de.ambertation.wover.block.api.model.WoverBlockModelGenerators;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.WoverModelProvider;
import de.ambertation.wover.item.api.client.trait.ItemModelTrait;

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
        ItemModelTrait.bootstrapModels(modCore, itemModelGenerator);
    }
}
