package org.betterx.wover.testmod.block.datagen;

import org.betterx.wover.block.api.model.WoverBlockModelGenerators;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverModelProvider;
import org.betterx.wover.testmod.block.TestBlockRegistry;

import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.world.level.block.Blocks;

public class TestModelProvider extends WoverModelProvider {
    public TestModelProvider(ModCore modCore) {
        super(modCore);
    }

    @Override
    protected void bootstrapBlockStateModels(WoverBlockModelGenerators blockStateModelGenerator) {
        //TexturedModel texturedModel = blockStateModelGenerator.getTextureModels(Blocks.AMETHYST_BLOCK, TexturedModel.CUBE.get(block));
        blockStateModelGenerator.modelFor(Blocks.AMETHYST_BLOCK)
                                .createFullBlock(TestBlockRegistry.TEST_BLOCK)
                                .createDoor(TestBlockRegistry.TEST_DOOR);

        blockStateModelGenerator.createWall(Blocks.AMETHYST_BLOCK, TestBlockRegistry.TEST_WALL);

//        var fullBlock = texturedModel
//                .getTemplate()
//                .create(TestBlockRegistry.TEST_BLOCK, mappings, blockStateModelGenerator.vanillaGenerator.modelOutput);
//        blockStateModelGenerator.acceptBlockState(BlockModelGenerators.createSimpleBlock(TestBlockRegistry.TEST_BLOCK, fullBlock));
//
//        blockStateModelGenerator.vanillaGenerator.createDoor(TestBlockRegistry.TEST_DOOR);

//        ResourceLocation resourceLocation = ModelTemplates.WALL_POST.create(TestBlockRegistry.TEST_WALL, mappings, blockStateModelGenerator.modelOutput);
//        ResourceLocation resourceLocation2 = ModelTemplates.WALL_LOW_SIDE.create(TestBlockRegistry.TEST_WALL, mappings, blockStateModelGenerator.modelOutput);
//        ResourceLocation resourceLocation3 = ModelTemplates.WALL_TALL_SIDE.create(TestBlockRegistry.TEST_WALL, mappings, blockStateModelGenerator.modelOutput);
//        blockStateModelGenerator.acceptBlockState(BlockModelGenerators.createWall(TestBlockRegistry.TEST_WALL, resourceLocation, resourceLocation2, resourceLocation3));
//        ResourceLocation resourceLocation4 = ModelTemplates.WALL_INVENTORY.create(TestBlockRegistry.TEST_WALL, mappings, blockStateModelGenerator.modelOutput);
//        blockStateModelGenerator.delegateItemModel(TestBlockRegistry.TEST_WALL, resourceLocation4);
    }

    @Override
    protected void bootstrapItemModels(ItemModelGenerators itemModelGenerator) {

    }
}
