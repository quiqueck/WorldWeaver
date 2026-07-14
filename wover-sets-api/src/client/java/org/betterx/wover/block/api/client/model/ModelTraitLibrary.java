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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Arrays;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

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
    /**
     * A bark/stripped-bark model: a rotated pillar with matching side textures on every face, optionally
     * mirrored and with alternative texture-suffix variants. Returns {@code null} outside of a datagen
     * environment (see the class Javadoc).
     *
     * @param logBlock                  supplies the matching log block, whose texture is reused with a
     *                                  {@code "_side"} suffix
     * @param mirroredTexture           whether the side texture should be mirrored on alternate faces
     * @param alternativeTextureSuffixe extra texture suffix variants to generate alongside the default one
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait bark(
            Supplier<Block> logBlock,
            boolean mirroredTexture,
            String... alternativeTextureSuffixe
    ) {
        return ModCore.isDatagen() ? Impl.bark(logBlock, mirroredTexture, alternativeTextureSuffixe) : null;
    }

    /**
     * A vanilla-style barrel model (closed/open top variants, rotated by facing).
     *
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait barrel() {
        return ModCore.isDatagen() ? Impl.barrel() : null;
    }

    /**
     * A vanilla-style bookshelf model.
     *
     * @param planksMaterial supplies the planks block whose texture is used for the shelf's frame
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait bookshelf(Supplier<Block> planksMaterial) {
        return ModCore.isDatagen() ? Impl.bookshelf(planksMaterial) : null;
    }

    /**
     * A vanilla-style rotated pillar model.
     *
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait pillar() {
        return ModCore.isDatagen() ? Impl.pillar() : null;
    }

    /**
     * A vanilla-style button model.
     *
     * @param planksMaterial supplies the block whose texture is used for the button
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait button(Supplier<Block> planksMaterial) {
        return ModCore.isDatagen() ? Impl.button(planksMaterial) : null;
    }

    /** The chest-special-renderer id used for chests built through {@link #chest}. */
    public static ResourceLocation chestRendered = LibWoverSets.C.mk("wooden_chest");

    /**
     * A vanilla-style chest model: a particle-only block model plus an inventory item model wired to a
     * {@link net.minecraft.client.renderer.special.ChestSpecialRenderer}.
     *
     * @param planksMaterial supplies the block whose texture is used for the chest's particles
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait chest(Supplier<Block> planksMaterial) {
        return ModCore.isDatagen() ? Impl.chest(planksMaterial) : null;
    }

    /**
     * A log model: a rotated pillar with the block's own side/top textures, optionally mirrored and with
     * alternative texture-suffix variants.
     *
     * @param mirroredTexture           whether the side texture should be mirrored on alternate faces
     * @param alternativeTextureSuffixe extra texture suffix variants to generate alongside the default one
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait log(boolean mirroredTexture, String... alternativeTextureSuffixe) {
        return ModCore.isDatagen() ? Impl.log(mirroredTexture, alternativeTextureSuffixe) : null;
    }

    /**
     * A vanilla-style crafting-table-like model (distinct top/front/side/bottom textures).
     *
     * @param planksMaterial supplies the block whose texture is used for the bottom face
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait craftingTable(Supplier<Block> planksMaterial) {
        return ModCore.isDatagen() ? Impl.craftingTable(planksMaterial) : null;
    }

    /**
     * A vanilla-style slab model (bottom/top/double variants).
     *
     * @param planksMaterial supplies the block whose texture is used for the slab
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait slab(Supplier<Block> planksMaterial) {
        return ModCore.isDatagen() ? Impl.slab(planksMaterial) : null;
    }

    /**
     * A plain full-cube model using the block's own texture.
     *
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait planks() {
        return ModCore.isDatagen() ? Impl.planks() : null;
    }

    /**
     * A vanilla-style composter model.
     *
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait composter() {
        return ModCore.isDatagen() ? Impl.composter() : null;
    }

    /**
     * A vanilla-style door model.
     *
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait door() {
        return ModCore.isDatagen() ? Impl.door() : null;
    }

    /**
     * A vanilla-style fence model.
     *
     * @param planksMaterial supplies the block whose texture is used for the fence
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait fence(Supplier<Block> planksMaterial) {
        return ModCore.isDatagen() ? Impl.fence(planksMaterial) : null;
    }

    /**
     * A vanilla-style fence gate model.
     *
     * @param planksMaterial supplies the block whose texture is used for the gate
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait gate(Supplier<Block> planksMaterial) {
        return ModCore.isDatagen() ? Impl.gate(planksMaterial) : null;
    }

    /**
     * A vanilla-style hanging sign model.
     *
     * @param logMaterial   supplies the block whose texture is used for the sign's chain/log parts
     * @param wallSignBlock supplies this sign's wall variant
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait hangingSign(Supplier<Block> logMaterial, Supplier<Block> wallSignBlock) {
        return ModCore.isDatagen() ? Impl.hangingSign(logMaterial, wallSignBlock) : null;
    }

    /**
     * A vanilla-style (standing/wall) sign model.
     *
     * @param logMaterial   supplies the block whose texture is used for the sign's post
     * @param wallSignBlock supplies this sign's wall variant
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait sign(Supplier<Block> logMaterial, Supplier<Block> wallSignBlock) {
        return ModCore.isDatagen() ? Impl.sign(logMaterial, wallSignBlock) : null;
    }

    /**
     * A vanilla-style ladder model.
     *
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait ladder() {
        return ModCore.isDatagen() ? Impl.ladder() : null;
    }

    /**
     * A vanilla-style pressure plate model.
     *
     * @param planksMaterial supplies the block whose texture is used for the plate
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait pressurePlate(Supplier<Block> planksMaterial) {
        return ModCore.isDatagen() ? Impl.pressurePlate(planksMaterial) : null;
    }

    /**
     * A chain-style model (thin axis-aligned X-cross, reusing vanilla's own {@code minecraft:block/chain}
     * shape) using the block's own texture.
     *
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait chain() {
        return ModCore.isDatagen() ? Impl.chain() : null;
    }

    /**
     * A vanilla iron-bars-style model (post/cap/side multipart variants) using the block's own texture.
     *
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait bars() {
        return ModCore.isDatagen() ? Impl.bars() : null;
    }

    /**
     * A vanilla-style stairs model.
     *
     * @param planksMaterial supplies the block whose texture is used for the stairs
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait stairs(Supplier<Block> planksMaterial) {
        return ModCore.isDatagen() ? Impl.stairs(planksMaterial) : null;
    }

    /**
     * A vanilla-style (non-orientable) trapdoor model.
     *
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait trapdoor() {
        return ModCore.isDatagen() ? Impl.trapdoor() : null;
    }

    /**
     * A vanilla-style orientable trapdoor model (can be placed on the floor or ceiling).
     *
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait orientableTrapdoor() {
        return ModCore.isDatagen() ? Impl.orientableTrapdoor() : null;
    }

    /**
     * A vanilla-style wall model (post/side variants).
     *
     * @param sourceMaterial supplies the block whose texture is used for the wall
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait wall(Supplier<Block> sourceMaterial) {
        return ModCore.isDatagen() ? Impl.wall(sourceMaterial) : null;
    }

    /**
     * A plain full-cube model using the block's own texture (alias of {@link #planks()}, kept as a separate,
     * more general-purpose name).
     *
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait cube() {
        return ModCore.isDatagen() ? Impl.cube() : null;
    }

    /**
     * A plain full-cube model plus a matching flat item icon, both using the block's own registered
     * {@link net.minecraft.client.data.models.model.TexturedModel} texture (or a plain cube texture if none
     * is registered) - the default model shape for a simple, unremarkable block.
     *
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait cubeWithFlatItem() {
        return ModCore.isDatagen() ? Impl.cubeWithFlatItem() : null;
    }

    /**
     * A vanilla cross-shaped (untinted) plant model plus a matching flat item icon.
     *
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait crossPlant() {
        return ModCore.isDatagen() ? Impl.crossPlant() : null;
    }

    /**
     * A trait for a block whose blockstate/block model <em>and</em> item model are both provided as
     * hand-authored static assets. Generates nothing itself: it excludes the block from block-model
     * validation and wires the item-model-definition to the conventional static item model
     * ({@code <namespace>:item/<name>}). This is the trait-based replacement for a central "ignore"
     * entry in a model provider.
     *
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait externalModel() {
        return ModCore.isDatagen() ? Impl.externalModel() : null;
    }

    /**
     * Like {@link #externalModel()}, but the item-model-definition is derived from the block's own texture
     * ({@code registerSimpleItemModel}) rather than a conventional {@code item/<name>} model. For static
     * blocks whose inventory item should reuse the block texture directly.
     *
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait externalModelDelegatedItem() {
        return ModCore.isDatagen() ? Impl.externalModelDelegatedItem() : null;
    }

    /**
     * Like {@link #externalModel()}, but the item-model-definition points at an explicit model location
     * instead of the conventional {@code item/<name>} (e.g. a block's own multi-variant model file).
     *
     * @param itemModel supplies the model location the item should reference
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait externalModelDelegatedItem(Supplier<ResourceLocation> itemModel) {
        return ModCore.isDatagen() ? Impl.externalModelDelegatedItem(itemModel) : null;
    }

    /**
     * A trait for a block whose blockstate/block model is a hand-authored static asset, but whose item
     * model should be a generated flat icon. Excludes the block from block-model validation and generates
     * a flat item model from {@code itemTexture} (or the block's own texture if {@code null}).
     *
     * @param itemTexture supplies the texture for the flat item, or {@code null} for the block's own texture
     * @return the model trait, or {@code null} outside of datagen
     */
    public static BlockModelTrait externalModelFlatItem(@Nullable Supplier<ResourceLocation> itemTexture) {
        return ModCore.isDatagen() ? Impl.externalModelFlatItem(itemTexture) : null;
    }

    /**
     * A plain flat item model using the item's own texture.
     *
     * @return the model trait, or {@code null} outside of datagen
     */
    public static ItemModelTrait itemModel() {
        return ModCore.isDatagen() ? Impl.itemModel() : null;
    }

    /**
     * A plain flat item model, reusing another item's texture as the model's layer.
     *
     * @param material supplies the item whose texture should be used
     * @return the model trait, or {@code null} outside of datagen
     */
    public static ItemModelTrait itemModel(Supplier<Item> material) {
        return ModCore.isDatagen() ? Impl.itemModel(material) : null;
    }

    /**
     * A vanilla-style elytra item model, dispatching between the normal and broken texture based on the item's
     * durability.
     *
     * @return the model trait, or {@code null} outside of datagen
     */
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
                generator.markItemModelProvided(chestBlock);
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
                // Mirrors vanilla's own crafting_table item, which reuses the block model directly
                // rather than getting a dedicated models/item/* file.
                generator.delegateItemModel(
                        carftingTableBlock,
                        ModelLocationUtils.getModelLocation(carftingTableBlock)
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
                generator.delegateItemModel(block, ModelLocationUtils.getModelLocation(block));
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
                generator.markItemModelProvided(block);
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

        private static BlockModelTrait chain() {
            return ClientBlockTraits.MODEL.with((key, chainBlock, generator) -> {
                generator.createChainModel(chainBlock, TextureMapping.getBlockTexture(chainBlock));
            });
        }

        private static BlockModelTrait bars() {
            return ClientBlockTraits.MODEL.with((key, barsBlock, generator) -> {
                generator.createBars(barsBlock);
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

        private static BlockModelTrait cubeWithFlatItem() {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                generator.createCubeModelWithFlatItem(block);
            });
        }

        private static BlockModelTrait crossPlant() {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                generator.vanillaGenerator.createCrossBlock(block, PlantType.NOT_TINTED);
                generator.createFlatItem(block);
            });
        }

        private static BlockModelTrait externalModel() {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                generator.excludeBlockFromValidation(block);
                if (block.asItem() != Items.AIR) {
                    generator.delegateItemModel(block, key.location().withPrefix("item/"));
                }
            });
        }

        private static BlockModelTrait externalModelDelegatedItem() {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                generator.excludeBlockFromValidation(block);
                if (block.asItem() != Items.AIR) {
                    generator.delegateItemModel(block);
                }
            });
        }

        private static BlockModelTrait externalModelDelegatedItem(Supplier<ResourceLocation> itemModel) {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                generator.excludeBlockFromValidation(block);
                if (block.asItem() != Items.AIR) {
                    generator.delegateItemModel(block, itemModel.get());
                }
            });
        }

        private static BlockModelTrait externalModelFlatItem(Supplier<ResourceLocation> itemTexture) {
            return ClientBlockTraits.MODEL.with((key, block, generator) -> {
                generator.excludeBlockFromValidation(block);
                generator.createFlatItem(block, itemTexture == null ? null : itemTexture.get());
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
