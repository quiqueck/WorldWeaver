package de.ambertation.wover.block.impl.client.model;

import de.ambertation.wover.block.api.client.model.ClientBlockModelRegistry;
import de.ambertation.wover.block.api.client.render.WoverBuiltinTinters;
import de.ambertation.wover.block.api.model.BlockModelKeys;
import de.ambertation.wover.block.impl.client.render.ClientBlockRenderBootstrap;
import de.ambertation.wover.client.api.WoverClientTraitEntrypoint;
import de.ambertation.wover.client.impl.ClientRenderTraitRegistry;

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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Arrays;

/**
 * Registers the built-in {@code wover-block-api} block model factories against their {@link BlockModelKeys}. The
 * bodies here are the client-only generator code that used to live in {@code ModelTraitLibrary.Impl}; it is now
 * keyed by {@link BlockModelKeys} so common code can attach a data-only {@code BlockModelBinding} and this
 * client source set supplies the behavior. Invoked through the {@code wover.client.traits} entrypoint.
 */
@Environment(EnvType.CLIENT)
public class WoverBlockModelFactories implements WoverClientTraitEntrypoint {
    @Override
    public void registerClientTraits() {
        ClientBlockModelRegistry.register(BlockModelKeys.BARK, (key, block, generator, payload) -> {
            final var logBlock = payload.logBlock();
            final var mirroredTexture = payload.mirroredTexture();
            final var alternativeTextureSuffixe = payload.alternativeTextureSuffixe();
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

        ClientBlockModelRegistry.register(BlockModelKeys.BARREL, (key, block, generator, payload) -> {
            final PropertyDispatch<VariantMutator> ROTATIONS_COLUMN_WITH_FACING =
                    PropertyDispatch
                            .modify(BlockStateProperties.FACING)
                            .select(Direction.DOWN, X_ROT_180)
                            .select(Direction.UP, NOP)
                            .select(Direction.NORTH, X_ROT_90)
                            .select(Direction.SOUTH, X_ROT_90.then(Y_ROT_180))
                            .select(Direction.WEST, X_ROT_90.then(Y_ROT_270))
                            .select(Direction.EAST, X_ROT_90.then(Y_ROT_90));
            ResourceLocation openTopTexture = TextureMapping.getBlockTexture(block, "_top_open");
            ResourceLocation closedModel = TexturedModel.CUBE_TOP_BOTTOM.create(
                    block,
                    generator.vanillaGenerator.modelOutput
            );
            MultiVariant multiVariant = generator.vanillaGenerator.plainVariant(closedModel);
            MultiVariant multiVariant2 = generator.vanillaGenerator.plainVariant(
                    TexturedModel.CUBE_TOP_BOTTOM
                            .get(block)
                            .updateTextures(textureMapping -> textureMapping.put(TextureSlot.TOP, openTopTexture))
                            .createWithSuffix(block, "_open", generator.vanillaGenerator.modelOutput)
            );
            generator.acceptBlockState(
                    MultiVariantGenerator.dispatch(block)
                                         .with(PropertyDispatch.initial(BlockStateProperties.OPEN)
                                                               .select(false, multiVariant)
                                                               .select(true, multiVariant2))
                                         .with(ROTATIONS_COLUMN_WITH_FACING)
            );
            generator.delegateItemModel(block, closedModel);
        });

        ClientBlockModelRegistry.register(BlockModelKeys.BOOKSHELF, (key, block, generator, planksMaterial) -> {
            generator.createBookshelf(block, planksMaterial.get());
        });

        ClientBlockModelRegistry.register(BlockModelKeys.CHISELED_BOOKSHELF, (key, block, generator, payload) -> {
            generator.createChiseledBookshelf(block);
        });

        ClientBlockModelRegistry.register(BlockModelKeys.PILLAR, (key, block, generator, payload) -> {
            generator.createRotatedPillar(block);
        });

        ClientBlockModelRegistry.register(BlockModelKeys.BUTTON, (key, block, generator, planksMaterial) -> {
            generator.createButton(planksMaterial.get(), block);
        });

        ClientBlockModelRegistry.register(BlockModelKeys.CHEST, (key, chestBlock, generator, planksMaterial) -> {
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
            generator.markItemModelProvided(chestBlock);
        });

        ClientBlockModelRegistry.register(BlockModelKeys.LOG, (key, block, generator, payload) -> {
            final var mirroredTexture = payload.mirroredTexture();
            final var alternativeTextureSuffixe = payload.alternativeTextureSuffixe();
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

        ClientBlockModelRegistry.register(BlockModelKeys.CRAFTING_TABLE, (key, carftingTableBlock, generator, planksMaterial) -> {

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
            // Mirrors vanilla's own crafting_table item, which reuses the block model directly
            // rather than getting a dedicated models/item/* file.
            generator.delegateItemModel(
                    carftingTableBlock,
                    ModelLocationUtils.getModelLocation(carftingTableBlock)
            );
        });

        ClientBlockModelRegistry.register(BlockModelKeys.SLAB, (key, block, generator, planksMaterial) -> {
            generator.createSlab(block, planksMaterial.get());
        });

        ClientBlockModelRegistry.register(BlockModelKeys.PLANKS, (key, block, generator, payload) -> {
            generator.createCubeModel(block);
            generator.delegateItemModel(block, ModelLocationUtils.getModelLocation(block));
        });

        ClientBlockModelRegistry.register(BlockModelKeys.COMPOSTER, (key, block, generator, payload) -> {
            generator.createComposter(block);
        });

        ClientBlockModelRegistry.register(BlockModelKeys.DOOR, (key, block, generator, payload) -> {
            generator.vanillaGenerator.createDoor(block);
            generator.markItemModelProvided(block);
        });

        ClientBlockModelRegistry.register(BlockModelKeys.FENCE, (key, fenceBlock, generator, planksMaterial) -> {
            generator.createFence(planksMaterial.get(), fenceBlock);
        });

        ClientBlockModelRegistry.register(BlockModelKeys.GATE, (key, fenceBlock, generator, planksMaterial) -> {
            generator.createFenceGate(planksMaterial.get(), fenceBlock);
        });

        ClientBlockModelRegistry.register(BlockModelKeys.HANGING_SIGN, (key, signBlock, generator, payload) -> {
            generator.createHangingSign(payload.logMaterial().get(), signBlock, payload.wallSignBlock().get());
        });

        ClientBlockModelRegistry.register(BlockModelKeys.SIGN, (key, signBlock, generator, payload) -> {
            generator.createSign(payload.logMaterial().get(), signBlock, payload.wallSignBlock().get());
        });

        ClientBlockModelRegistry.register(BlockModelKeys.LADDER, (key, ladderBlock, generator, payload) -> {
            generator.createLadder(ladderBlock);
        });

        ClientBlockModelRegistry.register(BlockModelKeys.PRESSURE_PLATE, (key, plateBlock, generator, planksMaterial) -> {
            generator.createPressurePlate(planksMaterial.get(), plateBlock);
        });

        ClientBlockModelRegistry.register(BlockModelKeys.CHAIN, (key, chainBlock, generator, payload) -> {
            generator.createChainModel(chainBlock, TextureMapping.getBlockTexture(chainBlock));
        });

        ClientBlockModelRegistry.register(BlockModelKeys.BARS, (key, barsBlock, generator, payload) -> {
            generator.createBars(barsBlock);
        });

        ClientBlockModelRegistry.register(BlockModelKeys.STAIRS, (key, stairsBlock, generator, planksMaterial) -> {
            generator.createStairs(planksMaterial.get(), stairsBlock);
        });

        ClientBlockModelRegistry.register(BlockModelKeys.TRAPDOOR, (key, trapdoorBlock, generator, payload) -> {
            generator.createTrapdoor(trapdoorBlock);
        });

        ClientBlockModelRegistry.register(BlockModelKeys.ORIENTABLE_TRAPDOOR, (key, trapdoorBlock, generator, payload) -> {
            generator.createOrientableTrapdoor(trapdoorBlock);
        });

        ClientBlockModelRegistry.register(BlockModelKeys.WALL, (key, wallBlock, generator, sourceMaterial) -> {
            generator.createWall(sourceMaterial.get(), wallBlock);
        });

        ClientBlockModelRegistry.register(BlockModelKeys.CUBE, (key, block, generator, payload) -> {
            generator.createFullBlock(block);
        });

        ClientBlockModelRegistry.register(BlockModelKeys.CUBE_WITH_FLAT_ITEM, (key, block, generator, payload) -> {
            generator.createCubeModelWithFlatItem(block);
        });

        ClientBlockModelRegistry.register(BlockModelKeys.CROSS_PLANT, (key, block, generator, payload) -> {
            generator.vanillaGenerator.createCrossBlock(block, PlantType.NOT_TINTED);
            generator.createFlatItem(block);
        });

        ClientBlockModelRegistry.register(BlockModelKeys.EXTERNAL_MODEL, (key, block, generator, payload) -> {
            generator.excludeBlockFromValidation(block);
            if (block.asItem() != Items.AIR) {
                generator.delegateItemModel(block, key.location().withPrefix("item/"));
            }
        });

        ClientBlockModelRegistry.register(BlockModelKeys.EXTERNAL_MODEL_DELEGATED_ITEM, (key, block, generator, payload) -> {
            generator.excludeBlockFromValidation(block);
            if (block.asItem() != Items.AIR) {
                generator.delegateItemModel(block);
            }
        });

        ClientBlockModelRegistry.register(BlockModelKeys.EXTERNAL_MODEL_DELEGATED_ITEM_LOCATION, (key, block, generator, itemModel) -> {
            generator.excludeBlockFromValidation(block);
            if (block.asItem() != Items.AIR) {
                generator.delegateItemModel(block, itemModel.get());
            }
        });

        ClientBlockModelRegistry.register(BlockModelKeys.EXTERNAL_MODEL_FLAT_ITEM, (key, block, generator, itemTexture) -> {
            generator.excludeBlockFromValidation(block);
            generator.createFlatItem(block, itemTexture == null ? null : itemTexture.get());
        });

        // Render-layer registration is applied (walking the block registry) once at client init.
        ClientRenderTraitRegistry.register(ClientBlockRenderBootstrap::applyRenderLayers);

        // The built-in tint shapes (TinterKeys). Registered here rather than in their own entrypoint so the
        // catalogue is present in both launches for the same reason the model factories are: the client walk
        // registers block colours, the datagen walk bakes item tints.
        WoverBuiltinTinters.register();
    }
}
