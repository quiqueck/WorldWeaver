package org.betterx.wover.block.api.client.model;

import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.client.trait.ClientBlockTraits;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverSets;
import org.betterx.wover.item.api.client.trait.ClientItemTraits;
import org.betterx.wover.item.api.client.trait.ItemModelTrait;

import static net.minecraft.client.data.models.BlockModelGenerators.*;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.conditional.Broken;
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

/**
 * A library of common {@link BlockModelTrait}/{@link ItemModelTrait} patterns. Every method here
 * is meant to be called unconditionally from common block/item registration code (like a normal
 * {@code BlockTraits} entry), so this class itself must stay free of any direct reference to
 * vanilla client-only datagen types - those live in {@link Impl}, which is only ever loaded when
 * {@link ModCore#isDatagen()} is {@code true}. Loading a class on a dedicated server that is
 * annotated {@code @Environment(CLIENT)} (or that transitively drags in vanilla client-only types)
 * always throws, regardless of whether the loading code path is actually reachable at runtime - see
 * {@link org.betterx.bclib.trait.block.PathBlockTrait} for the same pattern applied to a single trait.
 */
public class ModelTraitLibrary {
    public static BlockModelTrait bark(
            Supplier<Block> logBlock,
            boolean mirroredTexture,
            String... alternativeTextureSuffixe
    ) {
        return ModCore.isDatagen() ? Impl.bark(logBlock, mirroredTexture, alternativeTextureSuffixe) : null;
    }

    public static BlockModelTrait barrel() {
        return ModCore.isDatagen() ? Impl.barrel() : null;
    }

    public static BlockModelTrait bookshelf(Supplier<Block> planksMaterial) {
        return ModCore.isDatagen() ? Impl.bookshelf(planksMaterial) : null;
    }

    public static BlockModelTrait pillar() {
        return ModCore.isDatagen() ? Impl.pillar() : null;
    }

    public static BlockModelTrait button(Supplier<Block> planksMaterial) {
        return ModCore.isDatagen() ? Impl.button(planksMaterial) : null;
    }

    public static ResourceLocation chestRendered = LibWoverSets.C.mk("wooden_chest");

    public static BlockModelTrait chest(Supplier<Block> planksMaterial) {
        return ModCore.isDatagen() ? Impl.chest(planksMaterial) : null;
    }

    public static BlockModelTrait log(boolean mirroredTexture, String... alternativeTextureSuffixe) {
        return ModCore.isDatagen() ? Impl.log(mirroredTexture, alternativeTextureSuffixe) : null;
    }

    public static BlockModelTrait craftingTable(Supplier<Block> planksMaterial) {
        return ModCore.isDatagen() ? Impl.craftingTable(planksMaterial) : null;
    }

    public static BlockModelTrait slab(Supplier<Block> planksMaterial) {
        return ModCore.isDatagen() ? Impl.slab(planksMaterial) : null;
    }

    public static BlockModelTrait planks() {
        return ModCore.isDatagen() ? Impl.planks() : null;
    }

    public static BlockModelTrait composter() {
        return ModCore.isDatagen() ? Impl.composter() : null;
    }

    public static BlockModelTrait door() {
        return ModCore.isDatagen() ? Impl.door() : null;
    }

    public static BlockModelTrait fence(Supplier<Block> planksMaterial) {
        return ModCore.isDatagen() ? Impl.fence(planksMaterial) : null;
    }

    public static BlockModelTrait gate(Supplier<Block> planksMaterial) {
        return ModCore.isDatagen() ? Impl.gate(planksMaterial) : null;
    }

    public static BlockModelTrait hangingSign(Supplier<Block> logMaterial, Supplier<Block> wallSignBlock) {
        return ModCore.isDatagen() ? Impl.hangingSign(logMaterial, wallSignBlock) : null;
    }

    public static BlockModelTrait sign(Supplier<Block> logMaterial, Supplier<Block> wallSignBlock) {
        return ModCore.isDatagen() ? Impl.sign(logMaterial, wallSignBlock) : null;
    }

    public static BlockModelTrait ladder() {
        return ModCore.isDatagen() ? Impl.ladder() : null;
    }

    public static BlockModelTrait pressurePlate(Supplier<Block> planksMaterial) {
        return ModCore.isDatagen() ? Impl.pressurePlate(planksMaterial) : null;
    }

    public static BlockModelTrait stairs(Supplier<Block> planksMaterial) {
        return ModCore.isDatagen() ? Impl.stairs(planksMaterial) : null;
    }

    public static BlockModelTrait trapdoor() {
        return ModCore.isDatagen() ? Impl.trapdoor() : null;
    }

    public static BlockModelTrait orientableTrapdoor() {
        return ModCore.isDatagen() ? Impl.orientableTrapdoor() : null;
    }

    public static BlockModelTrait wall(Supplier<Block> sourceMaterial) {
        return ModCore.isDatagen() ? Impl.wall(sourceMaterial) : null;
    }

    public static BlockModelTrait cube() {
        return ModCore.isDatagen() ? Impl.cube() : null;
    }

    public static ItemModelTrait itemModel() {
        return ModCore.isDatagen() ? Impl.itemModel() : null;
    }

    public static ItemModelTrait itemModel(Supplier<Item> material) {
        return ModCore.isDatagen() ? Impl.itemModel(material) : null;
    }

    public static ItemModelTrait elytra() {
        return ModCore.isDatagen() ? Impl.elytra() : null;
    }

    @Environment(EnvType.CLIENT)
    private static class Impl {
        private static BlockModelTrait bark(
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

        private static BlockModelTrait barrel() {
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

        private static BlockModelTrait bookshelf(Supplier<Block> planksMaterial) {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                generator.createBookshelf(block, planksMaterial.get());
            });
        }


        private static BlockModelTrait pillar() {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                generator.createRotatedPillar(block);
            });
        }


        private static BlockModelTrait button(Supplier<Block> planksMaterial) {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                generator.createButton(planksMaterial.get(), block);
            });
        }

        private static BlockModelTrait chest(Supplier<Block> planksMaterial) {
            return ClientBlockTraits.MODEL.with((key, chestBlock, generator) -> {
                final var planks = planksMaterial.get();

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

        private static BlockModelTrait log(boolean mirroredTexture, String... alternativeTextureSuffixe) {
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

        private static BlockModelTrait craftingTable(Supplier<Block> planksMaterial) {
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

        private static BlockModelTrait slab(Supplier<Block> planksMaterial) {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                generator.createSlab(block, planksMaterial.get());
            });
        }

        private static BlockModelTrait planks() {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                generator.createCubeModel(block);
            });
        }

        private static BlockModelTrait composter() {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                generator.createComposter(block);
            });
        }

        private static BlockModelTrait door() {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                generator.vanillaGenerator.createDoor(block);
            });
        }

        private static BlockModelTrait fence(Supplier<Block> planksMaterial) {
            return ClientBlockTraits.MODEL.with((key, fenceBlock, generator) -> {
                generator.createFence(planksMaterial.get(), fenceBlock);
            });
        }

        private static BlockModelTrait gate(Supplier<Block> planksMaterial) {
            return ClientBlockTraits.MODEL.with((key, fenceBlock, generator) -> {
                generator.createFenceGate(planksMaterial.get(), fenceBlock);
            });
        }

        private static BlockModelTrait hangingSign(Supplier<Block> logMaterial, Supplier<Block> wallSignBlock) {
            return ClientBlockTraits.MODEL.with((key, signBlock, generator) -> {
                generator.createHangingSign(logMaterial.get(), signBlock, wallSignBlock.get());
            });
        }

        private static BlockModelTrait sign(Supplier<Block> logMaterial, Supplier<Block> wallSignBlock) {
            return ClientBlockTraits.MODEL.with((key, signBlock, generator) -> {
                generator.createSign(logMaterial.get(), signBlock, wallSignBlock.get());
            });
        }

        private static BlockModelTrait ladder() {
            return ClientBlockTraits.MODEL.with((key, ladderBlock, generator) -> {
                generator.createLadder(ladderBlock);
            });
        }

        private static BlockModelTrait pressurePlate(Supplier<Block> planksMaterial) {
            return ClientBlockTraits.MODEL.with((key, plateBlock, generator) -> {
                generator.createPressurePlate(planksMaterial.get(), plateBlock);
            });
        }

        private static BlockModelTrait stairs(Supplier<Block> planksMaterial) {
            return ClientBlockTraits.MODEL.with((key, stairsBlock, generator) -> {
                generator.createStairs(planksMaterial.get(), stairsBlock);
            });
        }

        private static BlockModelTrait trapdoor() {
            return ClientBlockTraits.MODEL.with((key, trapdoorBlock, generator) -> {
                generator.createTrapdoor(trapdoorBlock);
            });
        }

        private static BlockModelTrait orientableTrapdoor() {
            return ClientBlockTraits.MODEL.with((key, trapdoorBlock, generator) -> {
                generator.createOrientableTrapdoor(trapdoorBlock);
            });
        }

        private static BlockModelTrait wall(Supplier<Block> sourceMaterial) {
            return ClientBlockTraits.MODEL.with((key, wallBlock, generator) -> {
                generator.createWall(sourceMaterial.get(), wallBlock);
            });
        }

        private static BlockModelTrait cube() {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                generator.createFullBlock(block);
            });
        }

        private static ItemModelTrait itemModel() {
            return ClientItemTraits.MODEL.with((key, item, generator) -> {
                generator.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
            });
        }

        private static ItemModelTrait itemModel(Supplier<Item> material) {
            return ClientItemTraits.MODEL.with((key, item, generator) -> {
                final var modelLocation = ModelTemplates.FLAT_ITEM.create(
                        ModelLocationUtils.getModelLocation(item),
                        TextureMapping.layer0(ModelLocationUtils.getModelLocation(material.get())),
                        generator.modelOutput
                );
                generator.itemModelOutput.accept(item, ItemModelUtils.plainModel(modelLocation));
            });
        }

        private static ItemModelTrait elytra() {
            return ClientItemTraits.MODEL.with((key, elytra, generator) -> {

                ItemModel.Unbaked unbaked = ItemModelUtils.plainModel(generator.createFlatItemModel(
                        elytra,
                        ModelTemplates.FLAT_ITEM
                ));
                ItemModel.Unbaked unbaked2 = ItemModelUtils.plainModel(generator.createFlatItemModel(
                        elytra,
                        "_broken",
                        ModelTemplates.FLAT_ITEM
                ));
                generator.generateBooleanDispatch(elytra, new Broken(), unbaked2, unbaked);

            });
        }
    }
}
