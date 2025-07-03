package org.betterx.wover.block.api.client.model;

import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.client.trait.ClientBlockTraits;
import org.betterx.wover.entrypoint.LibWoverSets;

import static net.minecraft.client.data.models.BlockModelGenerators.*;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.special.ChestSpecialRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Arrays;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ModelTraitLibrary {
    public static BlockModelTrait bark(
            Supplier<Block> logBlock,
            boolean mirroredTexture,
            String... alternativeTextureSuffixe
    ) {
        return ClientBlockTraits.MODEL.with((key, block, generator) -> {
            final var textureResource = TextureMapping.getBlockTexture(logBlock.get());
            final var textureMapping = new TextureMapping()
                    .put(TextureSlot.SIDE, textureResource.withSuffix("_side"))
                    .put(TextureSlot.END, textureResource.withSuffix("_side"));
            final var alternatives = Arrays.stream(alternativeTextureSuffixe).map(suffix -> new TextureMapping()
                                                   .put(TextureSlot.SIDE, textureResource.withSuffix("_side" + suffix))
                                                   .put(TextureSlot.END, textureResource.withSuffix("_side" + suffix)))
                                           .toArray(TextureMapping[]::new);


            generator.createRotatedPillar(block, mirroredTexture, textureMapping, alternatives);
        });
    }

    public static BlockModelTrait barrel() {
        return ClientBlockTraits.MODEL.with((key, block, generator) -> {
            final PropertyDispatch<VariantMutator> ROTATIONS_COLUMN_WITH_FACING =
                    PropertyDispatch
                            .modify(BlockStateProperties.FACING)
                            .select(Direction.DOWN, X_ROT_180)
                            .select(Direction.UP, NOP)
                            .select(Direction.NORTH, X_ROT_90)
                            .select(Direction.SOUTH, X_ROT_90.then(Y_ROT_180))
                            .select(Direction.WEST, X_ROT_90.then(Y_ROT_270))
                            .select(Direction.EAST, X_ROT_90.then(Y_ROT_90));
            ResourceLocation resourceLocation = TextureMapping.getBlockTexture(block, "_top_open");
            MultiVariant multiVariant = generator.vanillaGenerator.plainVariant(TexturedModel.CUBE_TOP_BOTTOM.create(
                    block,
                    generator.vanillaGenerator.modelOutput
            ));
            MultiVariant multiVariant2 = generator.vanillaGenerator.plainVariant(
                    TexturedModel.CUBE_TOP_BOTTOM
                            .get(block)
                            .updateTextures(textureMapping -> textureMapping.put(TextureSlot.TOP, resourceLocation))
                            .createWithSuffix(block, "_open", generator.vanillaGenerator.modelOutput)
            );
            generator.acceptBlockState(
                    MultiVariantGenerator.dispatch(block)
                                         .with(PropertyDispatch.initial(BlockStateProperties.OPEN)
                                                               .select(false, multiVariant)
                                                               .select(true, multiVariant2))
                                         .with(ROTATIONS_COLUMN_WITH_FACING)
            );
        });
    }

    public static BlockModelTrait bookshelf(Supplier<Block> planksMaterial) {
        return ClientBlockTraits.MODEL.with((key, block, generator) -> {
            generator.createBookshelf(block, planksMaterial.get());
        });
    }


    public static BlockModelTrait button(Supplier<Block> planksMaterial) {
        return ClientBlockTraits.MODEL.with((key, block, generator) -> {
            generator.createButton(planksMaterial.get(), block);
        });
    }

    public static ResourceLocation chestRendered = LibWoverSets.C.mk("wooden_chest");

    public static BlockModelTrait chest(Supplier<Block> planksMaterial) {
        return ClientBlockTraits.MODEL.with((key, chestBlock, generator) -> {
            final var planks = planksMaterial.get();

//            generator.vanillaGenerator.createParticleOnlyBlock(chestBlock, planks);
//            generator.createItemModel(chestBlock, ModelTemplates.CHEST_INVENTORY, TextureMapping.particle(planks));

            generator.vanillaGenerator.createParticleOnlyBlock(chestBlock, planks);
            Item chestItem = chestBlock.asItem();
            ResourceLocation itemModel = ModelTemplates.CHEST_INVENTORY.create(
                    chestItem,
                    TextureMapping.particle(planks),
                    generator.modelOutput()
            );
            ItemModel.Unbaked itemModelUnbaked = ItemModelUtils.specialModel(
                    itemModel,
                    new ChestSpecialRenderer.Unbaked(key.location())
            );
            generator.vanillaGenerator.itemModelOutput.accept(chestItem, itemModelUnbaked);
        });
    }

    public static BlockModelTrait log(boolean mirroredTexture, String... alternativeTextureSuffixe) {
        return ClientBlockTraits.MODEL.with((key, block, generator) -> {
            final var textureResource = TextureMapping.getBlockTexture(block);
            final var textureMapping = new TextureMapping()
                    .put(TextureSlot.SIDE, textureResource.withSuffix("_side"))
                    .put(TextureSlot.END, textureResource.withSuffix("_top"));
            final var alternatives = Arrays.stream(alternativeTextureSuffixe).map(suffix -> new TextureMapping()
                                                   .put(TextureSlot.SIDE, textureResource.withSuffix("_side" + suffix))
                                                   .put(TextureSlot.END, textureResource.withSuffix("_top")))
                                           .toArray(TextureMapping[]::new);

            generator.createLog(block, mirroredTexture, textureMapping, alternatives);
        });
    }

    public static BlockModelTrait craftingTable(Supplier<Block> planksMaterial) {
        return ClientBlockTraits.MODEL.with((key, carftingTableBlock, generator) -> {

            generator.vanillaGenerator.createCraftingTableLike(
                    carftingTableBlock,
                    planksMaterial.get(),
                    (block, mat) -> new TextureMapping()
                            .put(TextureSlot.UP, TextureMapping.getBlockTexture(block, "_top"))
                            .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(block, "_front"))
                            .put(TextureSlot.NORTH, TextureMapping.getBlockTexture(block, "_front"))
                            .put(TextureSlot.WEST, TextureMapping.getBlockTexture(block, "_front"))
                            .put(TextureSlot.EAST, TextureMapping.getBlockTexture(block, "_side"))
                            .put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(block, "_side"))
                            .put(TextureSlot.DOWN, TextureMapping.getBlockTexture(block, "_bottom"))
            );
        });
    }

    public static BlockModelTrait slab(Supplier<Block> planksMaterial) {
        return ClientBlockTraits.MODEL.with((key, block, generator) -> {
            generator.createSlab(block, planksMaterial.get());
        });
    }

    public static BlockModelTrait planks() {
        return ClientBlockTraits.MODEL.with((key, block, generator) -> {
            generator.createCubeModel(block);
        });
    }

    public static BlockModelTrait composter() {
        return ClientBlockTraits.MODEL.with((key, block, generator) -> {
            generator.createComposter(block);
        });
    }

    public static BlockModelTrait door() {
        return ClientBlockTraits.MODEL.with((key, block, generator) -> {
            generator.vanillaGenerator.createDoor(block);
        });
    }

    public static BlockModelTrait fence(Supplier<Block> planksMaterial) {
        return ClientBlockTraits.MODEL.with((key, fenceBlock, generator) -> {
            generator.createFence(planksMaterial.get(), fenceBlock);
        });
    }

    public static BlockModelTrait gate(Supplier<Block> planksMaterial) {
        return ClientBlockTraits.MODEL.with((key, fenceBlock, generator) -> {
            generator.createFenceGate(planksMaterial.get(), fenceBlock);
        });
    }

    public static BlockModelTrait hangingSign(Supplier<Block> logMaterial, Supplier<Block> wallSignBlock) {
        return ClientBlockTraits.MODEL.with((key, signBlock, generator) -> {
            generator.createHangingSign(logMaterial.get(), signBlock, wallSignBlock.get());
        });
    }

    public static BlockModelTrait sign(Supplier<Block> logMaterial, Supplier<Block> wallSignBlock) {
        return ClientBlockTraits.MODEL.with((key, signBlock, generator) -> {
            generator.createSign(logMaterial.get(), signBlock, wallSignBlock.get());
        });
    }

    public static BlockModelTrait ladder() {
        return ClientBlockTraits.MODEL.with((key, ladderBlock, generator) -> {
            generator.createLadder(ladderBlock);
        });
    }

    public static BlockModelTrait pressurePlate(Supplier<Block> planksMaterial) {
        return ClientBlockTraits.MODEL.with((key, plateBlock, generator) -> {
            generator.createPressurePlate(planksMaterial.get(), plateBlock);
        });
    }

    public static BlockModelTrait stairs(Supplier<Block> planksMaterial) {
        return ClientBlockTraits.MODEL.with((key, stairsBlock, generator) -> {
            generator.createStairs(planksMaterial.get(), stairsBlock);
        });
    }

    public static BlockModelTrait trapdoor() {
        return ClientBlockTraits.MODEL.with((key, trapdoorBlock, generator) -> {
            generator.createTrapdoor(trapdoorBlock);
        });
    }

    public static BlockModelTrait orientableTrapdoor() {
        return ClientBlockTraits.MODEL.with((key, trapdoorBlock, generator) -> {
            generator.createOrientableTrapdoor(trapdoorBlock);
        });
    }

    public static BlockModelTrait wall(Supplier<Block> sourceMaterial) {
        return ClientBlockTraits.MODEL.with((key, wallBlock, generator) -> {
            generator.createWall(sourceMaterial.get(), wallBlock);
        });
    }

    public static BlockModelTrait cube(Supplier<Block> sourceMaterial) {
        return ClientBlockTraits.MODEL.with((key, block, generator) -> {
            generator.createFullBlock(block);
        });
    }
}
