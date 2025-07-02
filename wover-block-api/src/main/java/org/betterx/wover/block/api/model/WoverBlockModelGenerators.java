package org.betterx.wover.block.api.model;

import org.betterx.wover.entrypoint.LibWoverBlock;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class WoverBlockModelGenerators {
    public static final ResourceLocation CROSS = ResourceLocation.withDefaultNamespace("block/cross");
    public static final ResourceLocation CUBE = ResourceLocation.withDefaultNamespace("block/cube");
    public static final ResourceLocation CUBE_ALL = ResourceLocation.withDefaultNamespace("block/cube_all");
    public static final ResourceLocation COMPOSTER = LibWoverBlock.C.id("block/composter");

    public static final ModelTemplate COMPOSTER_MODEL = new ModelTemplate(
            Optional.of(COMPOSTER),
            Optional.empty(),
            TextureSlot.SIDE,
            TextureSlot.BOTTOM,
            TextureSlot.TOP
    );
    public final BlockModelGenerators vanillaGenerator;

    public WoverBlockModelGenerators(
            BlockModelGenerators vanillaGenerator
    ) {
        this.vanillaGenerator = vanillaGenerator;
    }

    public void createObsidianVariants(WoverBlockModelGenerators generators, Block obsidianBlock) {
        var model = generators.getTextureModels(obsidianBlock, TexturedModel.CUBE.get(obsidianBlock));
        var template = model.getTemplate();
        var modelLocation = template.create(obsidianBlock, model.getMapping(), generators.vanillaGenerator.modelOutput);
        final VariantMutator[] rotations = {
                BlockModelGenerators.NOP,
                BlockModelGenerators.Y_ROT_90,
                BlockModelGenerators.Y_ROT_180,
                BlockModelGenerators.Y_ROT_270
        };

        final Variant[] variants = new Variant[16];
        int idx = 0;
        for (VariantMutator rotation : rotations) {
            for (VariantMutator rotationY : rotations) {
                variants[idx] = BlockModelGenerators.plainModel(modelLocation);
                if (rotation != BlockModelGenerators.NOP)
                    variants[idx] = rotation.apply(variants[idx]);

                if (rotationY != BlockModelGenerators.NOP)
                    variants[idx] = rotationY.apply(variants[idx]);

                idx++;
            }
        }

        generators.acceptBlockState(MultiVariantGenerator.dispatch(
                obsidianBlock,
                BlockModelGenerators.variants(variants)
        ));
    }

    public void acceptBlockState(BlockModelDefinitionGenerator blockStateGenerator) {
        this.vanillaGenerator.blockStateOutput.accept(blockStateGenerator);
    }

    public void acceptModelOutput(ResourceLocation id, ModelInstance model) {
        this.vanillaGenerator.modelOutput.accept(id, model);
    }

    public void delegateItemModel(Block block) {
        this.vanillaGenerator.registerSimpleItemModel(block, TextureMapping.getBlockTexture(block));
    }

    public void delegateItemModel(Block block, ResourceLocation resourceLocation) {
        this.vanillaGenerator.registerSimpleItemModel(block, resourceLocation);
    }

    public TexturedModel getTextureModels(Block block, TexturedModel defaultModel) {
        return BlockModelGenerators.TEXTURED_MODELS.getOrDefault(block, defaultModel);
    }

    public Builder modelFor(Block block) {
        final TexturedModel texturedModel = this.getTextureModels(block, TexturedModel.CUBE.get(block));
        return modelFor(texturedModel);
    }

    public Builder modelFor(TexturedModel texturedModel) {
        return new Builder(texturedModel, texturedModel.getMapping());
    }

    public Builder modelFor(TexturedModel texturedModel, TextureMapping textureMapping) {
        return new Builder(texturedModel, textureMapping);
    }

    public Builder modelFor(Block block, TextureMapping textureMappingOverride) {
        final TexturedModel texturedModel = this.getTextureModels(block, TexturedModel.CUBE.get(block));
        return new Builder(texturedModel, textureMappingOverride);
    }

    public static TextureMapping textureMappingOf(
            TextureSlot slotA,
            ResourceLocation locationA
    ) {
        return new TextureMapping().put(slotA, locationA);
    }

    public static TextureMapping textureMappingOf(
            TextureSlot slotA,
            ResourceLocation locationA,
            TextureSlot slotB,
            ResourceLocation locationB
    ) {
        return textureMappingOf(slotA, locationA).put(slotB, locationB);
    }

    public void createBookshelf(Block shelf, Block planks) {
        TextureMapping textureMapping = TextureMapping.column(
                TextureMapping.getBlockTexture(shelf),
                TextureMapping.getBlockTexture(planks)
        );
        ResourceLocation resourceLocation = ModelTemplates.CUBE_COLUMN.create(
                shelf,
                textureMapping,
                vanillaGenerator.modelOutput
        );
        acceptBlockState(vanillaGenerator.createSimpleBlock(
                shelf,
                BlockModelGenerators.plainVariant(resourceLocation)
        ));
    }

    public void createLadder(Block ladderBlock) {
        vanillaGenerator.createNonTemplateHorizontalBlock(ladderBlock);
        vanillaGenerator.registerSimpleFlatItemModel(ladderBlock);
    }

    private final Map<ResourceLocation, ResourceLocation> PARTICLE_ONLY_MODELS = Maps.newHashMap();

    public ResourceLocation particleOnlyModel(Block block) {
        var name = ModelLocationUtils.getModelLocation(block).withSuffix("_particles");
        if (name.getNamespace().equals("minecraft")) name = LibWoverBlock.C.mk(name.getPath());

        var textureName = TextureMapping.getBlockTexture(block);
        if (!name.getNamespace().equals("minecraft") && textureName.getPath().endsWith("_log"))
            textureName = textureName.withSuffix("_side");

        ResourceLocation finalName = name;
        ResourceLocation finalTextureName = textureName;
        return PARTICLE_ONLY_MODELS.computeIfAbsent(
                name, (n) -> ModelTemplates.PARTICLE_ONLY.create(
                        finalName,
                        new TextureMapping().put(TextureSlot.PARTICLE, finalTextureName),
                        vanillaGenerator.modelOutput
                )
        );
    }

    public void createSign(Block baseBlock, Block signBlock, Block wallSignBlock) {
        final ResourceLocation particleLocation = particleOnlyModel(baseBlock);

        acceptBlockState(BlockModelGenerators.createSimpleBlock(
                signBlock,
                BlockModelGenerators.plainVariant(particleLocation)
        ));
        acceptBlockState(BlockModelGenerators.createSimpleBlock(
                wallSignBlock,
                BlockModelGenerators.plainVariant(particleLocation)
        ));

        vanillaGenerator.registerSimpleFlatItemModel(signBlock.asItem());
    }

    public void createHangingSign(Block baseBlock, Block hangingSignBlock, Block wallHangingSignBlock) {
        ResourceLocation resourceLocation = particleOnlyModel(baseBlock);
        acceptBlockState(BlockModelGenerators.createSimpleBlock(
                hangingSignBlock,
                BlockModelGenerators.plainVariant(resourceLocation)
        ));
        acceptBlockState(BlockModelGenerators.createSimpleBlock(
                wallHangingSignBlock,
                BlockModelGenerators.plainVariant(resourceLocation)
        ));
        vanillaGenerator.registerSimpleFlatItemModel(hangingSignBlock.asItem());
    }

    public void createBarrel(Block barrelBlock) {
        ResourceLocation resourceLocation = TextureMapping.getBlockTexture(barrelBlock, "_top_open");
        MultiVariant closedVariant = BlockModelGenerators.plainVariant(
                TexturedModel.CUBE_TOP_BOTTOM.create(
                        barrelBlock,
                        this.vanillaGenerator.modelOutput
                )
        );
        MultiVariant openVariant = BlockModelGenerators.plainVariant(
                TexturedModel.CUBE_TOP_BOTTOM
                        .get(barrelBlock)
                        .updateTextures((textureMapping) -> {
                            textureMapping.put(TextureSlot.TOP, resourceLocation);
                        })
                        .createWithSuffix(
                                barrelBlock,
                                "_open",
                                this.vanillaGenerator.modelOutput
                        )
        );
        acceptBlockState(MultiVariantGenerator
                .dispatch(barrelBlock)
                .with(PropertyDispatch
                        .initial(BlockStateProperties.OPEN)
                        .select(false, closedVariant)
                        .select(true, openVariant)
                )
                .with(BlockModelGenerators.createRotatedPillar())
        );
    }

    public void createComposter(Block composterBlock) {
        var mapping = new TextureMapping()
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(composterBlock, "_side"))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(composterBlock, "_top"))
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(composterBlock, "_bottom"));
        var location = COMPOSTER_MODEL.create(composterBlock, mapping, vanillaGenerator.modelOutput);
        acceptBlockState(MultiPartGenerator
                .multiPart(composterBlock)
                .with(BlockModelGenerators.plainVariant(location))
                .with(
                        BlockModelGenerators.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 1),
                        BlockModelGenerators.plainVariant(TextureMapping.getBlockTexture(
                                Blocks.COMPOSTER,
                                "_contents1"
                        ))
                )
                .with(
                        BlockModelGenerators.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 2),
                        BlockModelGenerators.plainVariant(TextureMapping.getBlockTexture(
                                Blocks.COMPOSTER,
                                "_contents2"
                        ))
                )
                .with(
                        BlockModelGenerators.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 3),
                        BlockModelGenerators.plainVariant(TextureMapping.getBlockTexture(
                                Blocks.COMPOSTER,
                                "_contents3"
                        ))
                )
                .with(
                        BlockModelGenerators.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 4),
                        BlockModelGenerators.plainVariant(TextureMapping.getBlockTexture(
                                Blocks.COMPOSTER,
                                "_contents4"
                        ))
                )
                .with(
                        BlockModelGenerators.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 5),
                        BlockModelGenerators.plainVariant(TextureMapping.getBlockTexture(
                                Blocks.COMPOSTER,
                                "_contents5"
                        ))
                )
                .with(
                        BlockModelGenerators.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 6),
                        BlockModelGenerators.plainVariant(TextureMapping.getBlockTexture(
                                Blocks.COMPOSTER,
                                "_contents6"
                        ))
                )
                .with(
                        BlockModelGenerators.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 7),
                        BlockModelGenerators.plainVariant(TextureMapping.getBlockTexture(
                                Blocks.COMPOSTER,
                                "_contents7"
                        ))
                )
                .with(
                        BlockModelGenerators.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 8),
                        BlockModelGenerators.plainVariant(TextureMapping.getBlockTexture(
                                Blocks.COMPOSTER,
                                "_contents_ready"
                        ))
                ));
    }

    public void createBlockTopSideBottom(Block bottomBlock, Block coverBlock, boolean withVariants) {
        var mapping = new TextureMapping()
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(coverBlock, "_side"))
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(coverBlock, "_top"))
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(bottomBlock));
        var location = ModelTemplates.CUBE_BOTTOM_TOP.create(coverBlock, mapping, vanillaGenerator.modelOutput);

        if (withVariants) acceptBlockState(randomTopModelVariant(coverBlock, location));
        else acceptBlockState(BlockModelGenerators.createSimpleBlock(
                coverBlock,
                BlockModelGenerators.plainVariant(location)
        ));
    }

    public void createCubeModel(Block block) {
        final var model = TexturedModel.CUBE.get(block);
        final TextureMapping mapping = this.getTextureModels(block, model).getMapping();
        final var location = model.getTemplate().create(block, mapping, vanillaGenerator.modelOutput);
        acceptBlockState(BlockModelGenerators.createSimpleBlock(block, BlockModelGenerators.plainVariant(location)));
    }

    public void createPressurePlate(Block plateBlock, ResourceLocation textureLocation) {
        createPressurePlate(plateBlock, new TextureMapping().put(TextureSlot.TEXTURE, textureLocation));
    }

    public void createPressurePlate(Block materialBlock, Block plateBlock) {
        createPressurePlate(
                plateBlock, this
                        .getTextureModels(plateBlock, TexturedModel.CUBE.get(materialBlock))
                        .getMapping()
        );
    }

    private void createPressurePlate(Block plateBlock, TextureMapping mapping) {
        final List<ResourceLocation> locations = Stream.of(
                ModelTemplates.PRESSURE_PLATE_UP,
                ModelTemplates.PRESSURE_PLATE_DOWN
        ).map(template -> template.create(plateBlock, mapping, vanillaGenerator.modelOutput)).toList();

        acceptBlockState(BlockModelGenerators.createPressurePlate(
                plateBlock,
                BlockModelGenerators.plainVariant(locations.get(0)),
                BlockModelGenerators.plainVariant(locations.get(1))
        ));
    }


    public void createButton(Block buttonBlock, ResourceLocation textureLocation) {
        createButton(buttonBlock, new TextureMapping().put(TextureSlot.TEXTURE, textureLocation));
    }

    public void createButton(Block materialBlock, Block buttonBlock) {
        createButton(
                buttonBlock, this
                        .getTextureModels(buttonBlock, TexturedModel.CUBE.get(materialBlock))
                        .getMapping()
        );
    }

    private void createButton(Block buttonBlock, TextureMapping mapping) {
        final List<ResourceLocation> locations = Stream.of(
                ModelTemplates.BUTTON,
                ModelTemplates.BUTTON_PRESSED
        ).map(template -> template.create(buttonBlock, mapping, vanillaGenerator.modelOutput)).toList();

        acceptBlockState(BlockModelGenerators.createButton(
                buttonBlock,
                BlockModelGenerators.plainVariant(locations.get(0)),
                BlockModelGenerators.plainVariant(locations.get(1))
        ));
        createItemModel(buttonBlock, ModelTemplates.BUTTON_INVENTORY, mapping);
    }

    public void createFence(Block fenceBlock, ResourceLocation textureLocation) {
        createFence(fenceBlock, new TextureMapping().put(TextureSlot.TEXTURE, textureLocation));
    }

    public void createFence(Block materialBlock, Block fenceBlock) {
        createFence(
                fenceBlock, this
                        .getTextureModels(fenceBlock, TexturedModel.CUBE.get(materialBlock))
                        .getMapping()
        );
    }

    public void createFence(Block fenceBlock, TextureMapping mapping) {
        final List<ResourceLocation> locations = Stream.of(
                ModelTemplates.FENCE_POST,
                ModelTemplates.FENCE_SIDE
        ).map(template -> template.create(fenceBlock, mapping, vanillaGenerator.modelOutput)).toList();

        acceptBlockState(BlockModelGenerators.createFence(
                fenceBlock,
                BlockModelGenerators.plainVariant(locations.get(0)),
                BlockModelGenerators.plainVariant(locations.get(1))
        ));
        createInventoryModel(fenceBlock, ModelTemplates.FENCE_INVENTORY, mapping);
    }

    public void createFenceGate(Block gateBlock, ResourceLocation textureLocation) {
        createFence(gateBlock, new TextureMapping().put(TextureSlot.TEXTURE, textureLocation));
    }

    public void createFenceGate(Block materialBlock, Block gateBlock) {
        createFenceGate(
                gateBlock, this
                        .getTextureModels(gateBlock, TexturedModel.CUBE.get(materialBlock))
                        .getMapping()
        );
    }

    public void createFenceGate(Block gateBlock, TextureMapping mapping) {
        final List<ResourceLocation> locations = Stream.of(
                ModelTemplates.FENCE_GATE_OPEN,
                ModelTemplates.FENCE_GATE_CLOSED,
                ModelTemplates.FENCE_GATE_WALL_OPEN,
                ModelTemplates.FENCE_GATE_WALL_CLOSED
        ).map(template -> template.create(gateBlock, mapping, vanillaGenerator.modelOutput)).toList();

        acceptBlockState(BlockModelGenerators.createFenceGate(
                gateBlock,
                BlockModelGenerators.plainVariant(locations.get(0)),
                BlockModelGenerators.plainVariant(locations.get(1)),
                BlockModelGenerators.plainVariant(locations.get(2)),
                BlockModelGenerators.plainVariant(locations.get(3)),
                true
        ));
    }

    public void createStairs(
            Block stairBlock,
            ResourceLocation topTextureLocation,
            ResourceLocation sideTextureLocation,
            ResourceLocation bottomTextureLocation
    ) {
        createStairs(
                stairBlock, new TextureMapping()
                        .put(TextureSlot.TOP, topTextureLocation)
                        .put(TextureSlot.SIDE, sideTextureLocation)
                        .put(TextureSlot.BOTTOM, bottomTextureLocation)
        );
    }

    public void createStairs(Block materialBlock, Block stairBlock) {
        createStairs(
                stairBlock, this
                        .getTextureModels(stairBlock, TexturedModel.CUBE.get(materialBlock))
                        .getMapping()
        );
    }

    public void createStairsWithModels(
            Block stairBlock,
            ResourceLocation stair,
            ResourceLocation outer,
            ResourceLocation inner
    ) {
        acceptBlockState(BlockModelGenerators.createStairs(
                stairBlock,
                BlockModelGenerators.plainVariant(inner),
                BlockModelGenerators.plainVariant(stair),
                BlockModelGenerators.plainVariant(outer)
        ));
        delegateItemModel(stairBlock, stair);
    }

    public void createStairs(Block stairBlock, TextureMapping mapping) {
        final List<ResourceLocation> locations = Stream
                .of(
                        ModelTemplates.STAIRS_INNER,
                        ModelTemplates.STAIRS_STRAIGHT,
                        ModelTemplates.STAIRS_OUTER
                )
                .map(template -> template.create(stairBlock, mapping, vanillaGenerator.modelOutput)).toList();

        acceptBlockState(BlockModelGenerators.createStairs(
                stairBlock,
                BlockModelGenerators.plainVariant(locations.get(0)),
                BlockModelGenerators.plainVariant(locations.get(1)),
                BlockModelGenerators.plainVariant(locations.get(2))
        ));
        delegateItemModel(stairBlock, locations.get(1));
    }

    public void createWall(Block materialBlock, Block wallBlock) {
        createWall(
                wallBlock, this
                        .getTextureModels(wallBlock, TexturedModel.CUBE.get(materialBlock))
                        .getMapping()
        );
    }

    public void createWall(Block wallBlock, TextureMapping mapping) {
        final List<ResourceLocation> locations = Stream.of(
                ModelTemplates.WALL_POST,
                ModelTemplates.WALL_LOW_SIDE,
                ModelTemplates.WALL_TALL_SIDE
        ).map(template -> template.create(wallBlock, mapping, vanillaGenerator.modelOutput)).toList();

        acceptBlockState(BlockModelGenerators.createWall(
                wallBlock,
                BlockModelGenerators.plainVariant(locations.get(0)),
                BlockModelGenerators.plainVariant(locations.get(1)),
                BlockModelGenerators.plainVariant(locations.get(2))
        ));
        createInventoryModel(wallBlock, ModelTemplates.WALL_INVENTORY, mapping);
    }

    public void createSlab(Block slabBlock, Block baseBlock) {
        var res = TextureMapping.getBlockTexture(baseBlock);
        createSlab(
                slabBlock, baseBlock, new TextureMapping()
                        .put(TextureSlot.SIDE, res)
                        .put(TextureSlot.BOTTOM, res)
                        .put(TextureSlot.TOP, res)
        );
    }

    public void createSlab(Block slabBlock, Block baseBlock, TextureMapping mapping) {
        final var fullBlockLocation = ModelLocationUtils.getModelLocation(baseBlock);
        final List<ResourceLocation> locations = Stream.of(
                ModelTemplates.SLAB_BOTTOM,
                ModelTemplates.SLAB_TOP
        ).map(template -> template.create(slabBlock, mapping, vanillaGenerator.modelOutput)).toList();

        acceptBlockState(BlockModelGenerators.createSlab(
                slabBlock,
                BlockModelGenerators.plainVariant(locations.get(0)),
                BlockModelGenerators.plainVariant(locations.get(1)),
                BlockModelGenerators.plainVariant(fullBlockLocation)
        ));
        delegateItemModel(slabBlock, locations.get(0));
    }

    public void createLog(Block logBlock) {
        var res = TextureMapping.getBlockTexture(logBlock);
        createLog(
                logBlock, false, new TextureMapping()
                        .put(TextureSlot.SIDE, res.withSuffix("_side"))
                        .put(TextureSlot.END, res.withSuffix("_top"))
        );
    }

    public void createLog(
            Block logBlock,
            TextureMapping mapping
    ) {
        createLog(logBlock, false, mapping);
    }

    public void createLog(
            Block logBlock,
            boolean mirroredAlternative, TextureMapping mapping,
            TextureMapping... alternatives
    ) {
        if (alternatives.length > 0 || mirroredAlternative) {
            final var weightedList = WeightedList.<Variant>builder();
            final var weightedListHorizontal = WeightedList.<Variant>builder();

            BiConsumer<TextureMapping, Integer> addModels = (tex, idx) -> {
                final var suffix = idx == 0 ? "" : "_" + idx;
                weightedList.add(
                        BlockModelGenerators.plainModel(
                                ModelTemplates.CUBE_COLUMN.createWithSuffix(
                                        logBlock, suffix, tex,
                                        vanillaGenerator.modelOutput
                                )), 1
                );
                weightedListHorizontal.add(
                        BlockModelGenerators.plainModel(
                                ModelTemplates.CUBE_COLUMN_HORIZONTAL.createWithSuffix(
                                        logBlock, suffix, tex,
                                        vanillaGenerator.modelOutput
                                )), 1
                );
                if (mirroredAlternative) {
                    final var m = BlockModelGenerators.plainModel(
                            ModelTemplates.CUBE_COLUMN_MIRRORED.createWithSuffix(
                                    logBlock, suffix, tex,
                                    vanillaGenerator.modelOutput
                            ));
                    weightedList.add(m, 1);
                    weightedListHorizontal.add(m, 1);
                }
            };

            int count = 0;
            addModels.accept(mapping, count++);
            for (TextureMapping alt : alternatives) addModels.accept(alt, count++);

            final var variantList = weightedList.build();
            acceptBlockState(BlockModelGenerators.createRotatedPillarWithHorizontalVariant(
                    logBlock,
                    new MultiVariant(variantList),
                    new MultiVariant(weightedListHorizontal.build())
            ));
            delegateItemModel(logBlock, variantList.unwrap().getFirst().value().modelLocation());
        } else {
            ResourceLocation first = ModelTemplates.CUBE_COLUMN.create(logBlock, mapping, vanillaGenerator.modelOutput);
            acceptBlockState(BlockModelGenerators.createRotatedPillarWithHorizontalVariant(
                    logBlock,
                    BlockModelGenerators.plainVariant(first),
                    BlockModelGenerators.plainVariant(ModelTemplates.CUBE_COLUMN_HORIZONTAL.create(
                            logBlock, mapping, vanillaGenerator.modelOutput))
            ));
            delegateItemModel(logBlock, first);
        }
    }

    public void createRotatedPillar(Block pillarBlock) {
        var res = TextureMapping.getBlockTexture(pillarBlock);
        createRotatedPillar(
                pillarBlock, new TextureMapping()
                        .put(TextureSlot.SIDE, res.withSuffix("_side"))
                        .put(TextureSlot.END, res.withSuffix("_top"))
        );
    }

    public void createRotatedPillar(Block pillarBlock, TextureMapping mapping) {
        createRotatedPillar(pillarBlock, false, mapping);
    }

    public void createRotatedPillar(
            Block pillarBlock,
            boolean mirroredAlternative,
            TextureMapping mapping,
            TextureMapping... alternatives
    ) {
        if (alternatives.length > 0 || mirroredAlternative) {
            final var weightedList = WeightedList.<Variant>builder();

            BiConsumer<TextureMapping, Integer> addModels = (tex, idx) -> {
                final var suffix = idx == 0 ? "" : "_" + idx;
                weightedList.add(
                        BlockModelGenerators.plainModel(
                                ModelTemplates.CUBE_COLUMN.createWithSuffix(
                                        pillarBlock, suffix, tex, vanillaGenerator.modelOutput
                                )), 1
                );
                if (mirroredAlternative) {
                    weightedList.add(
                            BlockModelGenerators.plainModel(
                                    ModelTemplates.CUBE_COLUMN_MIRRORED.createWithSuffix(
                                            pillarBlock, suffix, tex, vanillaGenerator.modelOutput
                                    )), 1
                    );
                }
            };

            int count = 0;
            addModels.accept(mapping, count++);
            for (TextureMapping alt : alternatives) addModels.accept(alt, count++);

            final var variantList = weightedList.build();
            acceptBlockState(BlockModelGenerators.createAxisAlignedPillarBlock(
                    pillarBlock,
                    new MultiVariant(variantList)
            ));
            delegateItemModel(pillarBlock, variantList.unwrap().getFirst().value().modelLocation());
        } else {
            final var model = ModelTemplates.CUBE_COLUMN.create(pillarBlock, mapping, vanillaGenerator.modelOutput);
            acceptBlockState(BlockModelGenerators.createAxisAlignedPillarBlock(
                    pillarBlock,
                    BlockModelGenerators.plainVariant(model)
            ));
            delegateItemModel(pillarBlock, model);
        }
    }

    private void createInventoryModel(Block wallBlock, ModelTemplate inventoryModel, TextureMapping mapping) {
        delegateItemModel(wallBlock, inventoryModel.create(wallBlock, mapping, vanillaGenerator.modelOutput));
    }


    public void createChest(Block materialBlock, Block chestBlock) {
        final var baseModel = particleOnlyModel(materialBlock);
        acceptBlockState(BlockModelGenerators.createSimpleBlock(
                chestBlock,
                BlockModelGenerators.plainVariant(baseModel)
        ));
    }

    public final void createItemModel(Block block, ModelTemplate template, TextureMapping mapping) {
        Item item = block.asItem();
        if (item != Items.AIR) {
            vanillaGenerator.registerSimpleItemModel(
                    block,
                    template.create(ModelLocationUtils.getModelLocation(item), mapping, vanillaGenerator.modelOutput)
            );
        }
    }

    public void createFlatItem(Block block) {
        vanillaGenerator.registerSimpleFlatItemModel(block);
    }


    public void createWallItem(Block block, ResourceLocation textureLocation) {
        createInventoryModel(
                block,
                ModelTemplates.WALL_INVENTORY,
                new TextureMapping().put(TextureSlot.WALL, textureLocation)
        );
    }

    public void createFlatItem(Block block, @Nullable ResourceLocation itemLocation) {
        if (itemLocation == null) {
            this.createFlatItem(block);
            return;
        }
        final var item = block.asItem();
        if (item != Items.AIR) {
            ModelTemplates.FLAT_ITEM.create(
                    ModelLocationUtils.getModelLocation(item),
                    TextureMapping.layer0(itemLocation),
                    vanillaGenerator.modelOutput
            );
        }
    }

    public static MultiVariantGenerator randomTopModelVariant(Block block, ResourceLocation model) {
        return MultiVariantGenerator
                .dispatch(
                        block,
                        BlockModelGenerators.variants(
                                BlockModelGenerators.NOP.apply(BlockModelGenerators.plainModel(model)),
                                BlockModelGenerators.Y_ROT_90.apply(BlockModelGenerators.plainModel(model)),
                                BlockModelGenerators.Y_ROT_180.apply(BlockModelGenerators.plainModel(model)),
                                BlockModelGenerators.Y_ROT_270.apply(BlockModelGenerators.plainModel(model))
                        )
                );
    }

    public BiConsumer<ResourceLocation, ModelInstance> modelOutput() {
        return vanillaGenerator.modelOutput;
    }

    public class Builder {
        private ResourceLocation fullBlockLocation;
        private final TexturedModel model;
        private final TextureMapping mapping;
        private final Map<ModelTemplate, ResourceLocation> models = Maps.newHashMap();

        private Builder(TexturedModel model, TextureMapping mapping) {
            this.model = model;
            this.mapping = mapping;
        }

        public Builder createFullBlock(Block fullBlock) {
            this.fullBlockLocation = model
                    .getTemplate()
                    .create(fullBlock, mapping, vanillaGenerator.modelOutput);

            acceptBlockState(
                    BlockModelGenerators.createSimpleBlock(
                            fullBlock,
                            BlockModelGenerators.plainVariant(fullBlockLocation)
                    )
            );

            return this;
        }

        public Builder createDoor(Block doorBlock) {
            vanillaGenerator.createDoor(doorBlock);
            return this;
        }

        public Builder createCustomFence(Block fenceBlock) {
            final TextureMapping particles = TextureMapping.customParticle(fenceBlock);

            final List<ResourceLocation> locations = Stream.of(
                    ModelTemplates.CUSTOM_FENCE_POST,
                    ModelTemplates.CUSTOM_FENCE_SIDE_NORTH,
                    ModelTemplates.CUSTOM_FENCE_SIDE_EAST,
                    ModelTemplates.CUSTOM_FENCE_SIDE_SOUTH,
                    ModelTemplates.CUSTOM_FENCE_SIDE_WEST
            ).map(template -> template.create(fenceBlock, particles, vanillaGenerator.modelOutput)).toList();

            acceptBlockState(BlockModelGenerators.createCustomFence(
                    fenceBlock,
                    BlockModelGenerators.plainVariant(locations.get(0)),
                    BlockModelGenerators.plainVariant(locations.get(1)),
                    BlockModelGenerators.plainVariant(locations.get(2)),
                    BlockModelGenerators.plainVariant(locations.get(3)),
                    BlockModelGenerators.plainVariant(locations.get(4))
            ));
            createInventoryModel(fenceBlock, ModelTemplates.CUSTOM_FENCE_INVENTORY, particles);

            return this;
        }

        public Builder createCustomFenceGate(Block gateBlock) {
            final TextureMapping particles = TextureMapping.customParticle(gateBlock);

            final List<ResourceLocation> locations = Stream.of(
                    ModelTemplates.CUSTOM_FENCE_GATE_OPEN,
                    ModelTemplates.CUSTOM_FENCE_GATE_CLOSED,
                    ModelTemplates.CUSTOM_FENCE_GATE_WALL_OPEN,
                    ModelTemplates.CUSTOM_FENCE_GATE_WALL_CLOSED
            ).map(template -> template.create(gateBlock, particles, vanillaGenerator.modelOutput)).toList();

            acceptBlockState(BlockModelGenerators.createFenceGate(
                    gateBlock,
                    BlockModelGenerators.plainVariant(locations.get(0)),
                    BlockModelGenerators.plainVariant(locations.get(1)),
                    BlockModelGenerators.plainVariant(locations.get(2)),
                    BlockModelGenerators.plainVariant(locations.get(3)),
                    false
            ));

            return this;
        }


        private Builder createFullBlockVariant(Block block) {
            final TexturedModel texturedModel = getTextureModels(block, TexturedModel.CUBE.get(block));
            final ResourceLocation resourceLocation = texturedModel.create(block, vanillaGenerator.modelOutput);

            acceptBlockState(BlockModelGenerators.createSimpleBlock(
                    block,
                    BlockModelGenerators.plainVariant(resourceLocation)
            ));

            return this;
        }

        private void createTrapdoor(Block block, boolean hasOrientation) {
            if (!hasOrientation) {
                vanillaGenerator.createTrapdoor(block);
            } else {
                vanillaGenerator.createOrientableTrapdoor(block);
            }
        }

        public Builder createSlab(Block slabBlock) {
            if (this.fullBlockLocation == null) {
                throw new IllegalStateException("Please call createFullBlock before calling createSlab");
            } else {
                final List<ResourceLocation> locations = Stream.of(
                        ModelTemplates.SLAB_BOTTOM,
                        ModelTemplates.SLAB_TOP
                ).map(template -> this.computeModelIfAbsent(template, slabBlock)).toList();

                acceptBlockState(BlockModelGenerators.createSlab(
                        slabBlock,
                        BlockModelGenerators.plainVariant(locations.get(0)),
                        BlockModelGenerators.plainVariant(locations.get(1)),
                        BlockModelGenerators.plainVariant(this.fullBlockLocation)
                ));
                delegateItemModel(slabBlock, locations.get(0));

                return this;
            }
        }

        private ResourceLocation computeModelIfAbsent(ModelTemplate modelTemplate, Block block) {
            return this.models.computeIfAbsent(
                    modelTemplate,
                    (m) -> m.create(block, this.mapping, vanillaGenerator.modelOutput)
            );
        }

        private void createInventoryModel(Block wallBlock, ModelTemplate inventoryModel, TextureMapping mapping) {
            delegateItemModel(wallBlock, inventoryModel.create(wallBlock, mapping, vanillaGenerator.modelOutput));
        }
    }


}
