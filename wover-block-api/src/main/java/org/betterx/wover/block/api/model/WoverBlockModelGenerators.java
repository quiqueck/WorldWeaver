package org.betterx.wover.block.api.model;

import org.betterx.wover.block.impl.ModelProviderExclusions;
import org.betterx.wover.entrypoint.LibWoverBlock;

import net.minecraft.client.data.models.BlockModelGenerators;
import static net.minecraft.client.data.models.BlockModelGenerators.*;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.core.Direction;
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
import com.google.gson.JsonParser;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

/**
 * Client-side helper that wraps vanilla's {@link BlockModelGenerators} with convenience methods for the
 * most common blockstate/model shapes (full cubes, stairs, walls, fences, slabs, pillars, doors, signs,
 * chests, ...), plus a few extra shapes not covered by vanilla (obsidian-style random rotation, particle-only
 * models, top/side/bottom covers).
 * <p>
 * Passed to {@link org.betterx.wover.datagen.api.provider.WoverModelProvider#bootstrapBlockStateModels}.
 */
@Environment(EnvType.CLIENT)
public class WoverBlockModelGenerators {
    /**
     * Vanilla's cross-shaped model parent ({@code block/cross}).
     */
    public static final ResourceLocation CROSS = ResourceLocation.withDefaultNamespace("block/cross");
    /**
     * Vanilla's plain cube model parent ({@code block/cube}).
     */
    public static final ResourceLocation CUBE = ResourceLocation.withDefaultNamespace("block/cube");
    /**
     * Vanilla's cube-with-one-texture model parent ({@code block/cube_all}).
     */
    public static final ResourceLocation CUBE_ALL = ResourceLocation.withDefaultNamespace("block/cube_all");
    /**
     * The model parent used by {@link #COMPOSTER_MODEL} ({@code wover:block/composter}).
     */
    public static final ResourceLocation COMPOSTER = LibWoverBlock.C.id("block/composter");
    private static final ResourceLocation LADDER = ResourceLocation.withDefaultNamespace("block/ladder");

    /**
     * Model template for composter-shaped blocks (side/bottom/top textures), used by {@link #createComposter(Block)}.
     */
    public static final ModelTemplate COMPOSTER_MODEL = new ModelTemplate(
            Optional.of(COMPOSTER),
            Optional.empty(),
            TextureSlot.SIDE,
            TextureSlot.BOTTOM,
            TextureSlot.TOP
    );
    /**
     * Model template for ladder-shaped blocks (particle/texture textures), used by {@link #createLadder(Block)}.
     */
    public static final ModelTemplate LADDER_MODEL = new ModelTemplate(
            Optional.of(LADDER),
            Optional.empty(),
            TextureSlot.PARTICLE,
            TextureSlot.TEXTURE
    );
    /**
     * The wrapped vanilla generator that this class delegates to.
     */
    public final BlockModelGenerators vanillaGenerator;

    /**
     * Every block whose item model was registered through {@link #delegateItemModel} or
     * {@link #createFlatItem} on this instance, so callers (like a mod's {@code ItemModelProvider})
     * can check whether a block's item model was already provided during block-state generation before
     * generating a fallback - unlike catching the "already has a model" exception from a second
     * registration attempt, this doesn't risk the fallback silently overwriting the real one first (the
     * underlying registration map always overwrites, then reports the conflict too late to undo it).
     */
    private final Set<Block> itemModelDelegatedBlocks = new HashSet<>();

    /**
     * @param path
     * @return
     */
    public static ResourceLocation vanilla(String path) {
        return ResourceLocation.withDefaultNamespace("block/" + path);
    }

    /**
     * Checks whether {@code block}'s item model was already registered through {@link #delegateItemModel}
     * or {@link #createFlatItem} on this instance.
     *
     * @param block The block to check
     * @return {@code true} if the block's item model was already generated
     */
    public boolean hasItemModel(Block block) {
        return itemModelDelegatedBlocks.contains(block);
    }

    /**
     * Marks {@code block}'s item model as already provided, for callers that generate it by going
     * straight through {@link #vanillaGenerator} (e.g. {@code vanillaGenerator.createDoor(block)}, which
     * registers its own flat item model internally) instead of through {@link #delegateItemModel} or
     * {@link #createFlatItem}, and so wouldn't otherwise show up in {@link #hasItemModel}.
     *
     * @param block The block whose item model was already provided
     */
    public void markItemModelProvided(Block block) {
        itemModelDelegatedBlocks.add(block);
    }

    /**
     * Excludes {@code block} from vanilla's block-model validation, for blocks whose blockstate/model
     * files are provided as hand-authored static assets rather than generated here. Without this, the
     * vanilla {@code ModelProvider} fails datagen for any registered block it never saw a model for.
     * <p>
     * This is the trait-driven replacement for the old central "ignore" list in a mod's model provider:
     * a block that renders from static assets carries a model trait (e.g.
     * {@code ModelTraitLibrary.externalModel()}) that calls this instead of emitting a blockstate.
     *
     * @param block The block to exclude from block-model validation
     */
    public void excludeBlockFromValidation(Block block) {
        ModelProviderExclusions.excludeFromBlockModelValidation(block);
    }

    /**
     * Wraps a vanilla {@link BlockModelGenerators} instance.
     *
     * @param vanillaGenerator The vanilla generator to delegate to
     */
    public WoverBlockModelGenerators(
            BlockModelGenerators vanillaGenerator
    ) {
        this.vanillaGenerator = vanillaGenerator;
    }

    /**
     * Creates all 16 rotation variants for an obsidian-like block that should appear with a randomized
     * orientation, mirroring vanilla's obsidian/crying-obsidian model generation.
     *
     * @param generators    The generator to emit models through (usually {@code this})
     * @param obsidianBlock The block to generate the variants for
     */
    public void createObsidianVariants(WoverBlockModelGenerators generators, Block obsidianBlock) {
        var model = generators.getTextureModels(obsidianBlock, TexturedModel.CUBE.get(obsidianBlock));
        var template = model.getTemplate();
        var modelLocation = template.create(obsidianBlock, model.getMapping(), generators.vanillaGenerator.modelOutput);
        final VariantMutator[] rotations = {
                NOP,
                Y_ROT_90,
                Y_ROT_180,
                BlockModelGenerators.Y_ROT_270
        };

        final Variant[] variants = new Variant[16];
        int idx = 0;
        for (VariantMutator rotation : rotations) {
            for (VariantMutator rotationY : rotations) {
                variants[idx] = BlockModelGenerators.plainModel(modelLocation);
                if (rotation != NOP)
                    variants[idx] = rotation.apply(variants[idx]);

                if (rotationY != NOP)
                    variants[idx] = rotationY.apply(variants[idx]);

                idx++;
            }
        }

        generators.acceptBlockState(MultiVariantGenerator.dispatch(
                obsidianBlock,
                BlockModelGenerators.variants(variants)
        ));
    }

    /**
     * Emits a blockstate definition. Thin wrapper around the vanilla generator's {@code blockStateOutput}.
     *
     * @param blockStateGenerator The blockstate definition to emit
     */
    public void acceptBlockState(BlockModelDefinitionGenerator blockStateGenerator) {
        this.vanillaGenerator.blockStateOutput.accept(blockStateGenerator);
    }

    /**
     * Emits a model file. Thin wrapper around the vanilla generator's {@code modelOutput}.
     *
     * @param id    The resource location the model should be written to
     * @param model The model definition to emit
     */
    public void acceptModelOutput(ResourceLocation id, ModelInstance model) {
        this.vanillaGenerator.modelOutput.accept(id, model);
    }

    /**
     * Registers a simple item model for the block that uses the block's own texture.
     *
     * @param block The block whose item model to generate
     */
    public void delegateItemModel(Block block) {
        this.vanillaGenerator.registerSimpleItemModel(block, TextureMapping.getBlockTexture(block));
        itemModelDelegatedBlocks.add(block);
    }

    /**
     * Registers a simple item model for the block that references an existing model.
     *
     * @param block            The block whose item model to generate
     * @param resourceLocation The model the item should reference
     */
    public void delegateItemModel(Block block, ResourceLocation resourceLocation) {
        this.vanillaGenerator.registerSimpleItemModel(block, resourceLocation);
        itemModelDelegatedBlocks.add(block);
    }

    /**
     * Looks up the vanilla {@link TexturedModel} registered for {@code block} in
     * {@link BlockModelGenerators#TEXTURED_MODELS}, falling back to {@code defaultModel} if none is registered.
     *
     * @param block        The block to look up
     * @param defaultModel The model to use if none is registered for the block
     * @return The resolved textured model
     */
    public TexturedModel getTextureModels(Block block, TexturedModel defaultModel) {
        return BlockModelGenerators.TEXTURED_MODELS.getOrDefault(block, defaultModel);
    }

    /**
     * Starts a fluent {@link Builder} for {@code block}, using its registered {@link TexturedModel} (or a
     * plain cube model as fallback).
     *
     * @param block The block to build models for
     * @return A new builder
     */
    public Builder modelFor(Block block) {
        final TexturedModel texturedModel = this.getTextureModels(block, TexturedModel.CUBE.get(block));
        return modelFor(texturedModel);
    }

    /**
     * Starts a fluent {@link Builder} using an explicit {@link TexturedModel} and its default mapping.
     *
     * @param texturedModel The textured model to build with
     * @return A new builder
     */
    public Builder modelFor(TexturedModel texturedModel) {
        return new Builder(texturedModel, texturedModel.getMapping());
    }

    /**
     * Starts a fluent {@link Builder} using an explicit {@link TexturedModel} and a custom texture mapping.
     *
     * @param texturedModel  The textured model to build with
     * @param textureMapping The texture mapping to use instead of the model's own mapping
     * @return A new builder
     */
    public Builder modelFor(TexturedModel texturedModel, TextureMapping textureMapping) {
        return new Builder(texturedModel, textureMapping);
    }

    /**
     * Starts a fluent {@link Builder} for {@code block}, overriding its texture mapping.
     *
     * @param block                  The block to build models for
     * @param textureMappingOverride The texture mapping to use instead of the block's registered mapping
     * @return A new builder
     */
    public Builder modelFor(Block block, TextureMapping textureMappingOverride) {
        final TexturedModel texturedModel = this.getTextureModels(block, TexturedModel.CUBE.get(block));
        return new Builder(texturedModel, textureMappingOverride);
    }

    /**
     * Creates a single-entry {@link TextureMapping}.
     *
     * @param slotA     The texture slot
     * @param locationA The texture to map the slot to
     * @return A new texture mapping
     */
    public static TextureMapping textureMappingOf(
            TextureSlot slotA,
            ResourceLocation locationA
    ) {
        return new TextureMapping().put(slotA, locationA);
    }

    /**
     * Creates a two-entry {@link TextureMapping}.
     *
     * @param slotA     The first texture slot
     * @param locationA The texture to map the first slot to
     * @param slotB     The second texture slot
     * @param locationB The texture to map the second slot to
     * @return A new texture mapping
     */
    public static TextureMapping textureMappingOf(
            TextureSlot slotA,
            ResourceLocation locationA,
            TextureSlot slotB,
            ResourceLocation locationB
    ) {
        return textureMappingOf(slotA, locationA).put(slotB, locationB);
    }

    /**
     * Generates a bookshelf-style model (column texture mapping from the shelf and plank textures) and its
     * blockstate.
     *
     * @param shelf  The bookshelf block
     * @param planks The plank block whose texture is used for the shelf's top/bottom
     */
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
        delegateItemModel(shelf, resourceLocation);
    }

    /**
     * Generates a plain full-cube model (using {@link ModelTemplates#CUBE_ALL}) and blockstate for the block.
     *
     * @param block The block to generate the model for
     */
    public void createFullBlock(Block block) {
        var textureMapping = new TextureMapping()
                .put(TextureSlot.ALL, TextureMapping.getBlockTexture(block))
                .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(block));

        ResourceLocation resourceLocation = ModelTemplates.CUBE_ALL.create(
                block,
                textureMapping,
                vanillaGenerator.modelOutput
        );
        acceptBlockState(vanillaGenerator.createSimpleBlock(
                block,
                BlockModelGenerators.plainVariant(resourceLocation)
        ));
        delegateItemModel(block, resourceLocation);
    }

    private static final PropertyDispatch<VariantMutator> ROTATION_HORIZONTAL_FACING = PropertyDispatch
            .modify(BlockStateProperties.HORIZONTAL_FACING)
            .select(Direction.EAST, Y_ROT_90)
            .select(Direction.SOUTH, Y_ROT_180)
            .select(Direction.WEST, Y_ROT_270)
            .select(Direction.NORTH, NOP);


    /**
     * Generates a ladder-style model (using {@link #LADDER_MODEL}) and blockstate, rotated per
     * {@link net.minecraft.world.level.block.state.properties.BlockStateProperties#HORIZONTAL_FACING}, plus
     * a flat item model.
     *
     * @param ladderBlock The block to generate the model for
     */
    public void createLadder(Block ladderBlock) {
        //vanillaGenerator.createNonTemplateHorizontalBlock(ladderBlock);
        var mapping = new TextureMapping().put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(ladderBlock))
                                          .put(TextureSlot.TEXTURE, TextureMapping.getBlockTexture(ladderBlock));
        var ladderModel = LADDER_MODEL.create(
                ladderBlock,
                mapping,
                vanillaGenerator.modelOutput
        );

        acceptBlockState(MultiVariantGenerator.dispatch(ladderBlock, BlockModelGenerators.plainVariant(ladderModel))
                                              .with(ROTATION_HORIZONTAL_FACING));

        //createInventoryModel(fenceBlock, ModelTemplates.FENCE_INVENTORY, mapping);

        vanillaGenerator.registerSimpleFlatItemModel(ladderBlock);
        itemModelDelegatedBlocks.add(ladderBlock);
    }

    /**
     * Generates the multipart blockstate and models for a vanilla iron-bars-style block (post/cap/side
     * variants driven by the {@code NORTH}/{@code EAST}/{@code SOUTH}/{@code WEST} connection properties)
     * plus a flat item model. The 6 suffixed model files ({@code _post}, {@code _post_ends}, {@code _cap},
     * {@code _cap_alt}, {@code _side}, {@code _side_alt}) are generated with the same element geometry as
     * vanilla's own {@code minecraft:block/iron_bars_*} models, textured with {@code barsBlock}'s own
     * block texture (there's no reusable vanilla {@link ModelTemplate} for this shape to parent from,
     * since vanilla ships its iron bars models as complete/standalone files rather than datagenning them).
     *
     * @param barsBlock The block to generate the blockstate for
     */
    public void createBars(
            Block barsBlock
    ) {
        final var barsModel = ModelLocationUtils.getModelLocation(barsBlock);
        final var texture = TextureMapping.getBlockTexture(barsBlock);

        acceptModelOutput(barsModel.withSuffix("_post"), BarsModels.post(texture));
        acceptModelOutput(barsModel.withSuffix("_post_ends"), BarsModels.postEnds(texture));
        acceptModelOutput(barsModel.withSuffix("_cap"), BarsModels.cap(texture));
        acceptModelOutput(barsModel.withSuffix("_cap_alt"), BarsModels.capAlt(texture));
        acceptModelOutput(barsModel.withSuffix("_side"), BarsModels.side(texture));
        acceptModelOutput(barsModel.withSuffix("_side_alt"), BarsModels.sideAlt(texture));

        MultiVariant postVariant = plainVariant(barsModel.withSuffix("_post"));
        MultiVariant postEndsVariant = plainVariant(barsModel.withSuffix("_post_ends"));

        MultiVariant capVariant = plainVariant(barsModel.withSuffix("_cap"));
        MultiVariant sideVariant = plainVariant(barsModel.withSuffix("_side"));

        MultiVariant altCapVariant = plainVariant(barsModel.withSuffix("_cap_alt"));
        MultiVariant altSideVariant = plainVariant(barsModel.withSuffix("_side_alt"));

        this.acceptBlockState(
                MultiPartGenerator.multiPart(barsBlock)
                                  .with(postEndsVariant)
                                  .with(
                                          condition()
                                                  .term(BlockStateProperties.NORTH, false)
                                                  .term(BlockStateProperties.EAST, false)
                                                  .term(BlockStateProperties.SOUTH, false)
                                                  .term(BlockStateProperties.WEST, false),
                                          postVariant
                                  )
                                  .with(
                                          condition()
                                                  .term(BlockStateProperties.NORTH, true)
                                                  .term(BlockStateProperties.EAST, false)
                                                  .term(BlockStateProperties.SOUTH, false)
                                                  .term(BlockStateProperties.WEST, false),
                                          capVariant
                                  )
                                  .with(
                                          condition()
                                                  .term(BlockStateProperties.NORTH, false)
                                                  .term(BlockStateProperties.EAST, true)
                                                  .term(BlockStateProperties.SOUTH, false)
                                                  .term(BlockStateProperties.WEST, false),
                                          capVariant.with(Y_ROT_90)
                                  )
                                  .with(
                                          condition()
                                                  .term(BlockStateProperties.NORTH, false)
                                                  .term(BlockStateProperties.EAST, false)
                                                  .term(BlockStateProperties.SOUTH, true)
                                                  .term(BlockStateProperties.WEST, false),
                                          altCapVariant
                                  )
                                  .with(
                                          condition()
                                                  .term(BlockStateProperties.NORTH, false)
                                                  .term(BlockStateProperties.EAST, false)
                                                  .term(BlockStateProperties.SOUTH, false)
                                                  .term(BlockStateProperties.WEST, true),
                                          altCapVariant.with(Y_ROT_90)
                                  )
                                  .with(condition().term(BlockStateProperties.NORTH, true), sideVariant)
                                  .with(condition().term(BlockStateProperties.EAST, true), sideVariant.with(Y_ROT_90))
                                  .with(condition().term(BlockStateProperties.SOUTH, true), altSideVariant)
                                  .with(
                                          condition().term(BlockStateProperties.WEST, true),
                                          altSideVariant.with(Y_ROT_90)
                                  )
        );
        this.createFlatItem(barsBlock);
    }

    /**
     * Raw (non-templated) model JSON for the 6 shapes an iron-bars-style block needs, mirroring vanilla's
     * own {@code minecraft:block/iron_bars_*} element geometry with {@code texture} substituted for every
     * face. Kept as literal element geometry (rather than a {@link ModelTemplate} parenting the vanilla
     * model) because vanilla's iron bars models don't use {@code #slot}-style texture placeholders that a
     * child model could override.
     */
    private static final class BarsModels {
        private static ModelInstance raw(String elementsJson, ResourceLocation texture) {
            final String tex = texture.toString();
            return () -> JsonParser.parseString(elementsJson.replace("%TEX%", tex));
        }

        private static ModelInstance post(ResourceLocation texture) {
            return raw(
                    """
                            {"ambientocclusion": false, "textures": {"particle": "%TEX%", "bars": "%TEX%"}, "elements": [
                            {"from":[8,0,7],"to":[8,16,9],"faces":{"west":{"uv":[7,0,9,16],"texture":"#bars"},"east":{"uv":[9,0,7,16],"texture":"#bars"}}},
                            {"from":[7,0,8],"to":[9,16,8],"faces":{"north":{"uv":[7,0,9,16],"texture":"#bars"},"south":{"uv":[9,0,7,16],"texture":"#bars"}}}
                            ]}""",
                    texture
            );
        }

        private static ModelInstance postEnds(ResourceLocation texture) {
            return raw(
                    """
                            {"ambientocclusion": false, "textures": {"particle": "%TEX%", "edge": "%TEX%"}, "elements": [
                            {"from":[7,0.001,7],"to":[9,0.001,9],"faces":{"down":{"uv":[7,7,9,9],"texture":"#edge"},"up":{"uv":[7,7,9,9],"texture":"#edge"}}},
                            {"from":[7,15.999,7],"to":[9,15.999,9],"faces":{"down":{"uv":[7,7,9,9],"texture":"#edge"},"up":{"uv":[7,7,9,9],"texture":"#edge"}}}
                            ]}""",
                    texture
            );
        }

        private static ModelInstance cap(ResourceLocation texture) {
            return raw(
                    """
                            {"ambientocclusion": false, "textures": {"particle": "%TEX%", "bars": "%TEX%", "edge": "%TEX%"}, "elements": [
                            {"from":[8,0,8],"to":[8,16,9],"faces":{"west":{"uv":[8,0,7,16],"texture":"#bars"},"east":{"uv":[7,0,8,16],"texture":"#bars"}}},
                            {"from":[7,0,9],"to":[9,16,9],"faces":{"north":{"uv":[9,0,7,16],"texture":"#bars"},"south":{"uv":[7,0,9,16],"texture":"#bars"}}}
                            ]}""",
                    texture
            );
        }

        private static ModelInstance capAlt(ResourceLocation texture) {
            return raw(
                    """
                            {"ambientocclusion": false, "textures": {"particle": "%TEX%", "bars": "%TEX%", "edge": "%TEX%"}, "elements": [
                            {"from":[8,0,7],"to":[8,16,8],"faces":{"west":{"uv":[8,0,9,16],"texture":"#bars"},"east":{"uv":[9,0,8,16],"texture":"#bars"}}},
                            {"from":[7,0,7],"to":[9,16,7],"faces":{"north":{"uv":[7,0,9,16],"texture":"#bars"},"south":{"uv":[9,0,7,16],"texture":"#bars"}}}
                            ]}""",
                    texture
            );
        }

        private static ModelInstance side(ResourceLocation texture) {
            return raw(
                    """
                            {"ambientocclusion": false, "textures": {"particle": "%TEX%", "bars": "%TEX%", "edge": "%TEX%"}, "elements": [
                            {"from":[8,0,0],"to":[8,16,8],"faces":{"west":{"uv":[16,0,8,16],"texture":"#bars"},"east":{"uv":[8,0,16,16],"texture":"#bars"}}},
                            {"from":[7,0,0],"to":[9,16,7],"faces":{"north":{"uv":[7,0,9,16],"texture":"#edge","cullface":"north"}}},
                            {"from":[7,0.001,0],"to":[9,0.001,7],"faces":{"down":{"uv":[9,0,7,7],"texture":"#edge"},"up":{"uv":[7,0,9,7],"texture":"#edge"}}},
                            {"from":[7,15.999,0],"to":[9,15.999,7],"faces":{"down":{"uv":[9,0,7,7],"texture":"#edge"},"up":{"uv":[7,0,9,7],"texture":"#edge"}}}
                            ]}""",
                    texture
            );
        }

        private static ModelInstance sideAlt(ResourceLocation texture) {
            return raw(
                    """
                            {"ambientocclusion": false, "textures": {"particle": "%TEX%", "bars": "%TEX%", "edge": "%TEX%"}, "elements": [
                            {"from":[8,0,8],"to":[8,16,16],"faces":{"west":{"uv":[8,0,0,16],"texture":"#bars"},"east":{"uv":[0,0,8,16],"texture":"#bars"}}},
                            {"from":[7,0,9],"to":[9,16,16],"faces":{"south":{"uv":[7,0,9,16],"texture":"#edge","cullface":"south"},"down":{"uv":[9,9,7,16],"texture":"#edge"},"up":{"uv":[7,9,9,16],"texture":"#edge"}}},
                            {"from":[7,0.001,9],"to":[9,0.001,16],"faces":{"down":{"uv":[9,9,7,16],"texture":"#edge"},"up":{"uv":[7,9,9,16],"texture":"#edge"}}},
                            {"from":[7,15.999,9],"to":[9,15.999,16],"faces":{"down":{"uv":[9,9,7,16],"texture":"#edge"},"up":{"uv":[7,9,9,16],"texture":"#edge"}}}
                            ]}""",
                    texture
            );
        }
    }

    private final Map<ResourceLocation, ResourceLocation> PARTICLE_ONLY_MODELS = Maps.newHashMap();

    /**
     * Creates (and caches) a particle-only model for {@code block}, useful for blocks like signs or chests
     * whose actual model is rendered by a block-entity renderer but still need a texture for break
     * particles.
     *
     * @param block The block to create the particle model for
     * @return The resource location of the generated particle-only model
     */
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

    /**
     * Generates the particle-only-based blockstates for a standing/wall sign pair and a flat item model
     * for the standing sign.
     *
     * @param baseBlock     The block whose texture is used for the sign's particle model (e.g. the plank block)
     * @param signBlock     The standing sign block
     * @param wallSignBlock The wall sign block
     */
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
        itemModelDelegatedBlocks.add(signBlock);
    }

    /**
     * Generates the particle-only-based blockstates for a hanging/wall-hanging sign pair and a flat item
     * model for the hanging sign.
     *
     * @param baseBlock            The block whose texture is used for the sign's particle model
     * @param hangingSignBlock     The hanging sign block
     * @param wallHangingSignBlock The wall hanging sign block
     */
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
        itemModelDelegatedBlocks.add(hangingSignBlock);
    }

    /**
     * Generates the open/closed blockstate variants for a barrel-style block, driven by the vanilla
     * {@code OPEN} property, plus axis rotation.
     *
     * @param barrelBlock The block to generate the blockstate for
     */
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

    /**
     * Generates the multipart blockstate for a composter-style block, layering in the vanilla composter's
     * content-level overlay textures (using {@link #COMPOSTER_MODEL}) for
     * {@code LEVEL_COMPOSTER} 1 through 8.
     *
     * @param composterBlock The block to generate the blockstate for
     */
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
        delegateItemModel(composterBlock, location);
    }

    /**
     * Generates a {@link ModelTemplates#CUBE_BOTTOM_TOP} model (top/side textures from {@code coverBlock},
     * bottom texture from {@code bottomBlock}) and its blockstate.
     *
     * @param bottomBlock  The block whose texture is used for the bottom face
     * @param coverBlock   The block that owns the generated model/blockstate, and whose texture is used for
     *                     the top/side faces
     * @param withVariants If {@code true}, generates four randomly-rotated top variants instead of a single one
     */
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
        delegateItemModel(coverBlock, location);
    }

    /**
     * Generates the cube model and blockstate for {@code block}, using its registered {@link TexturedModel}
     * (or a plain cube as fallback). Does not generate an item model - see
     * {@link #createCubeModelWithFlatItem(Block)}.
     *
     * @param block The block to generate the model for
     */
    public void createCubeModel(Block block) {
        final var model = TexturedModel.CUBE.get(block);
        final TextureMapping mapping = this.getTextureModels(block, model).getMapping();
        final var location = model.getTemplate().create(block, mapping, vanillaGenerator.modelOutput);
        acceptBlockState(BlockModelGenerators.createSimpleBlock(block, BlockModelGenerators.plainVariant(location)));
    }

    /**
     * Convenience for the common "plain cube blockstate + matching flat item" pair, which
     * would otherwise require a {@link #createCubeModel(Block)} + {@link #createFlatItem(Block)}
     * call pair at every use site.
     */
    public void createCubeModelWithFlatItem(Block block) {
        createCubeModel(block);
        createFlatItem(block);
    }

    /**
     * Same as {@link #createCubeModelWithFlatItem(Block)}, but lets the item icon reference a
     * texture other than the block's own (e.g. a dedicated small-item render).
     */
    public void createCubeModelWithFlatItem(Block block, @Nullable ResourceLocation itemTexture) {
        createCubeModel(block);
        createFlatItem(block, itemTexture);
    }

    /**
     * Creates a single-variant block model from an arbitrary {@link ModelTemplate} and
     * {@link TextureMapping}. Useful for addon block classes that just need a single,
     * non-blockstate-dependent model (e.g. an unshaded cube, or a tinted cube) without
     * having to duplicate the {@code acceptBlockState(createSimpleBlock(...))} boilerplate.
     *
     * @param block    the block to generate the model for
     * @param template the model template (parent model) to use
     * @param mapping  the texture mapping to apply to the template
     * @return the {@link ResourceLocation} of the generated model
     */
    public ResourceLocation createSimpleTemplatedBlock(Block block, ModelTemplate template, TextureMapping mapping) {
        final var location = template.create(block, mapping, vanillaGenerator.modelOutput);
        acceptBlockState(BlockModelGenerators.createSimpleBlock(block, BlockModelGenerators.plainVariant(location)));
        return location;
    }

    /**
     * Generates the up/down blockstate and models for a pressure-plate-style block from an explicit texture.
     *
     * @param plateBlock      The block to generate the blockstate for
     * @param textureLocation The texture to use for the plate
     */
    public void createPressurePlate(Block plateBlock, ResourceLocation textureLocation) {
        createPressurePlate(plateBlock, new TextureMapping().put(TextureSlot.TEXTURE, textureLocation));
    }

    /**
     * Generates the up/down blockstate and models for a pressure-plate-style block, reusing the registered
     * (or cube) texture of {@code materialBlock}.
     *
     * @param materialBlock The block whose texture should be used for the plate
     * @param plateBlock    The block to generate the blockstate for
     */
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
        delegateItemModel(plateBlock, locations.get(0));
    }


    /**
     * Generates the pressed/unpressed blockstate and models (plus an item model) for a button-style block
     * from an explicit texture.
     *
     * @param buttonBlock     The block to generate the blockstate for
     * @param textureLocation The texture to use for the button
     */
    public void createButton(Block buttonBlock, ResourceLocation textureLocation) {
        createButton(buttonBlock, new TextureMapping().put(TextureSlot.TEXTURE, textureLocation));
    }

    /**
     * Generates the pressed/unpressed blockstate and models (plus an item model) for a button-style block,
     * reusing the registered (or cube) texture of {@code materialBlock}.
     *
     * @param materialBlock The block whose texture should be used for the button
     * @param buttonBlock   The block to generate the blockstate for
     */
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

    /**
     * Generates the post/side blockstate and models (plus an inventory model) for a fence-style block from
     * an explicit texture.
     *
     * @param fenceBlock      The block to generate the blockstate for
     * @param textureLocation The texture to use for the fence
     */
    public void createFence(Block fenceBlock, ResourceLocation textureLocation) {
        createFence(fenceBlock, new TextureMapping().put(TextureSlot.TEXTURE, textureLocation));
    }

    /**
     * Generates the post/side blockstate and models (plus an inventory model) for a fence-style block,
     * reusing the registered (or cube) texture of {@code materialBlock}.
     *
     * @param materialBlock The block whose texture should be used for the fence
     * @param fenceBlock    The block to generate the blockstate for
     */
    public void createFence(Block materialBlock, Block fenceBlock) {
        createFence(
                fenceBlock, this
                        .getTextureModels(fenceBlock, TexturedModel.CUBE.get(materialBlock))
                        .getMapping()
        );
    }

    /**
     * Generates the post/side blockstate and models (plus an inventory model) for a fence-style block from
     * an explicit texture mapping.
     *
     * @param fenceBlock The block to generate the blockstate for
     * @param mapping    The texture mapping to apply to the fence's model templates
     */
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

    /**
     * Generates the open/closed (and wall-adjacent) blockstate and models for a fence-gate-style block from
     * an explicit texture.
     *
     * @param gateBlock       The block to generate the blockstate for
     * @param textureLocation The texture to use for the gate
     */
    public void createFenceGate(Block gateBlock, ResourceLocation textureLocation) {
        createFenceGate(gateBlock, new TextureMapping().put(TextureSlot.TEXTURE, textureLocation));
    }

    /**
     * Generates the open/closed (and wall-adjacent) blockstate and models for a fence-gate-style block,
     * reusing the registered (or cube) texture of {@code materialBlock}.
     *
     * @param materialBlock The block whose texture should be used for the gate
     * @param gateBlock     The block to generate the blockstate for
     */
    public void createFenceGate(Block materialBlock, Block gateBlock) {
        createFenceGate(
                gateBlock, this
                        .getTextureModels(gateBlock, TexturedModel.CUBE.get(materialBlock))
                        .getMapping()
        );
    }

    /**
     * Generates the open/closed (and wall-adjacent) blockstate and models for a fence-gate-style block from
     * an explicit texture mapping.
     *
     * @param gateBlock The block to generate the blockstate for
     * @param mapping   The texture mapping to apply to the gate's model templates
     */
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
        delegateItemModel(gateBlock, locations.get(1));
    }

    /**
     * Generates the inner/straight/outer blockstate and models for a stairs-style block from explicit
     * top/side/bottom textures, plus an item model.
     *
     * @param stairBlock            The block to generate the blockstate for
     * @param topTextureLocation    The texture to use for the top face
     * @param sideTextureLocation   The texture to use for the side faces
     * @param bottomTextureLocation The texture to use for the bottom face
     */
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

    /**
     * Delegates to vanilla's {@link BlockModelGenerators#createOrientableTrapdoor(Block)} to generate the
     * blockstate/models for a trapdoor that has separate top/bottom-facing textures.
     *
     * @param trapdoorBlock The block to generate the blockstate for
     */
    public void createOrientableTrapdoor(Block trapdoorBlock) {
        vanillaGenerator.createOrientableTrapdoor(trapdoorBlock);
        itemModelDelegatedBlocks.add(trapdoorBlock);
    }

    /**
     * Delegates to vanilla's {@link BlockModelGenerators#createTrapdoor(Block)} to generate the
     * blockstate/models for a regular trapdoor.
     *
     * @param trapdoorBlock The block to generate the blockstate for
     */
    public void createTrapdoor(Block trapdoorBlock) {
        vanillaGenerator.createTrapdoor(trapdoorBlock);
        itemModelDelegatedBlocks.add(trapdoorBlock);
    }

    /**
     * Generates the inner/straight/outer blockstate and models for a stairs-style block, reusing the
     * registered (or cube) texture of {@code materialBlock}.
     *
     * @param materialBlock The block whose texture should be used for the stairs
     * @param stairBlock    The block to generate the blockstate for
     */
    public void createStairs(Block materialBlock, Block stairBlock) {
        createStairs(
                stairBlock, this
                        .getTextureModels(stairBlock, TexturedModel.CUBE.get(materialBlock))
                        .getMapping()
        );
    }

    /**
     * Generates the stairs blockstate directly from pre-existing straight/outer/inner model locations,
     * without creating new model files, plus an item model that references the straight model.
     *
     * @param stairBlock The block to generate the blockstate for
     * @param stair      The location of the straight-stairs model
     * @param outer      The location of the outer-corner model
     * @param inner      The location of the inner-corner model
     */
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

    /**
     * Generates the inner/straight/outer blockstate and models for a stairs-style block from an explicit
     * texture mapping, plus an item model.
     *
     * @param stairBlock The block to generate the blockstate for
     * @param mapping    The texture mapping to apply to the stairs' model templates
     */
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

    /**
     * Generates the post/low-side/tall-side blockstate and models (plus an inventory model) for a
     * wall-style block, reusing the registered (or cube) texture of {@code materialBlock}.
     *
     * @param materialBlock The block whose texture should be used for the wall
     * @param wallBlock     The block to generate the blockstate for
     */
    public void createWall(Block materialBlock, Block wallBlock) {
        createWall(
                wallBlock, this
                        .getTextureModels(wallBlock, TexturedModel.CUBE.get(materialBlock))
                        .getMapping()
        );
    }

    /**
     * Generates the post/low-side/tall-side blockstate and models (plus an inventory model) for a
     * wall-style block from an explicit texture mapping.
     *
     * @param wallBlock The block to generate the blockstate for
     * @param mapping   The texture mapping to apply to the wall's model templates
     */
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

    /**
     * Generates the bottom/top/double blockstate and models (plus an item model) for a slab-style block,
     * using a single texture (from {@code baseBlock}) for all faces.
     *
     * @param slabBlock The block to generate the blockstate for
     * @param baseBlock The full block whose texture, and whose existing model (for the double-slab state),
     *                  are reused
     */
    public void createSlab(Block slabBlock, Block baseBlock) {
        var res = TextureMapping.getBlockTexture(baseBlock);
        createSlab(
                slabBlock, baseBlock, new TextureMapping()
                        .put(TextureSlot.SIDE, res)
                        .put(TextureSlot.BOTTOM, res)
                        .put(TextureSlot.TOP, res)
        );
    }

    /**
     * Generates the bottom/top/double blockstate and models (plus an item model) for a slab-style block
     * from an explicit texture mapping.
     *
     * @param slabBlock The block to generate the blockstate for
     * @param baseBlock The full block whose existing model is reused for the double-slab state
     * @param mapping   The texture mapping to apply to the slab's model templates
     */
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

    /**
     * Generates the axial/horizontal blockstate and models (plus an item model) for a log-style block,
     * deriving side/end textures from the block's own texture with {@code _side}/{@code _top} suffixes.
     *
     * @param logBlock The block to generate the blockstate for
     */
    public void createLog(Block logBlock) {
        var res = TextureMapping.getBlockTexture(logBlock);
        createLog(
                logBlock, false, new TextureMapping()
                        .put(TextureSlot.SIDE, res.withSuffix("_side"))
                        .put(TextureSlot.END, res.withSuffix("_top"))
        );
    }

    /**
     * Generates the axial/horizontal blockstate and models (plus an item model) for a log-style block from
     * an explicit texture mapping.
     *
     * @param logBlock The block to generate the blockstate for
     * @param mapping  The texture mapping to apply to the log's model templates
     */
    public void createLog(
            Block logBlock,
            TextureMapping mapping
    ) {
        createLog(logBlock, false, mapping);
    }

    /**
     * Generates the axial/horizontal blockstate and models (plus an item model) for a log-style block,
     * optionally with a mirrored variant and/or additional weighted texture alternatives (e.g. mossy log
     * variants that should be randomly picked).
     *
     * @param logBlock            The block to generate the blockstate for
     * @param mirroredAlternative If {@code true}, adds a mirrored copy of every texture mapping as an
     *                            additional weighted variant
     * @param mapping             The primary texture mapping to apply to the log's model templates
     * @param alternatives        Additional texture mappings to add as equally-weighted variants
     */
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

    /**
     * Generates the axis-aligned-pillar blockstate and models (plus an item model) for a pillar-style block
     * (e.g. a quartz-pillar-like block, without a distinct horizontal texture), deriving side/end textures
     * from the block's own texture with {@code _side}/{@code _top} suffixes.
     *
     * @param pillarBlock The block to generate the blockstate for
     */
    public void createRotatedPillar(Block pillarBlock) {
        var res = TextureMapping.getBlockTexture(pillarBlock);
        createRotatedPillar(
                pillarBlock, new TextureMapping()
                        .put(TextureSlot.SIDE, res.withSuffix("_side"))
                        .put(TextureSlot.END, res.withSuffix("_top"))
        );
    }

    /**
     * Generates the axis-aligned-pillar blockstate and models (plus an item model) for a pillar-style block
     * from an explicit texture mapping.
     *
     * @param pillarBlock The block to generate the blockstate for
     * @param mapping     The texture mapping to apply to the pillar's model templates
     */
    public void createRotatedPillar(Block pillarBlock, TextureMapping mapping) {
        createRotatedPillar(pillarBlock, false, mapping);
    }

    /**
     * Generates the axis-aligned-pillar blockstate and models (plus an item model) for a pillar-style block,
     * optionally with a mirrored variant and/or additional weighted texture alternatives.
     *
     * @param pillarBlock         The block to generate the blockstate for
     * @param mirroredAlternative If {@code true}, adds a mirrored copy of every texture mapping as an
     *                            additional weighted variant
     * @param mapping             The primary texture mapping to apply to the pillar's model templates
     * @param alternatives        Additional texture mappings to add as equally-weighted variants
     */
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

    private static final ModelTemplate CHAIN_TEMPLATE = new ModelTemplate(
            Optional.of(ResourceLocation.withDefaultNamespace("block/chain")),
            Optional.empty(),
            TextureSlot.ALL
    );

    /**
     * Generates the axis-aligned-pillar blockstate and model (plus an item model) for a chain-style block,
     * reusing vanilla's own thin X-cross {@code minecraft:block/chain} shape as the model's parent (chains
     * aren't a cube shape, so {@link ModelTemplates#CUBE_COLUMN} doesn't apply here).
     *
     * @param chainBlock The block to generate the blockstate for
     * @param texture    The single texture used on every face of the chain shape
     */
    public void createChainModel(Block chainBlock, ResourceLocation texture) {
        final var mapping = new TextureMapping().put(TextureSlot.ALL, texture);
        final var model = CHAIN_TEMPLATE.create(chainBlock, mapping, vanillaGenerator.modelOutput);
        vanillaGenerator.createAxisAlignedPillarBlockCustomModel(
                chainBlock,
                BlockModelGenerators.plainVariant(model)
        );
        delegateItemModel(chainBlock, model);
    }

    private void createInventoryModel(Block wallBlock, ModelTemplate inventoryModel, TextureMapping mapping) {
        delegateItemModel(wallBlock, inventoryModel.create(wallBlock, mapping, vanillaGenerator.modelOutput));
    }


    /**
     * Generates a simple blockstate for a chest-style block that reuses {@code materialBlock}'s
     * {@link #particleOnlyModel(Block) particle-only model} (the actual chest is rendered by a block-entity
     * renderer, so this model is only used for break particles and inventory fallback).
     *
     * @param materialBlock The block whose texture is used for the particle model
     * @param chestBlock    The block to generate the blockstate for
     */
    public void createChest(Block materialBlock, Block chestBlock) {
        final var baseModel = particleOnlyModel(materialBlock);
        acceptBlockState(BlockModelGenerators.createSimpleBlock(
                chestBlock,
                BlockModelGenerators.plainVariant(baseModel)
        ));
    }

    /**
     * Generates an item model for {@code block}'s item from an arbitrary {@link ModelTemplate} and
     * {@link TextureMapping}. Does nothing if the block has no item ({@link Items#AIR}).
     *
     * @param block    The block whose item model to generate
     * @param template The model template (parent model) to use
     * @param mapping  The texture mapping to apply to the template
     */
    public final void createItemModel(Block block, ModelTemplate template, TextureMapping mapping) {
        Item item = block.asItem();
        if (item != Items.AIR) {
            vanillaGenerator.registerSimpleItemModel(
                    block,
                    template.create(ModelLocationUtils.getModelLocation(item), mapping, vanillaGenerator.modelOutput)
            );
            itemModelDelegatedBlocks.add(block);
        }
    }

    /**
     * Registers a simple flat (2D, {@code item/generated}-parented) item model for the block, using the
     * block's own texture.
     *
     * @param block The block whose item model to generate
     */
    public void createFlatItem(Block block) {
        vanillaGenerator.registerSimpleFlatItemModel(block);
        itemModelDelegatedBlocks.add(block);
    }


    /**
     * Generates a wall-style inventory item model from an explicit texture, for blocks whose blockstate was
     * created without going through {@link #createWall(Block, TextureMapping)} (e.g. custom wall block
     * classes).
     *
     * @param block           The block whose item model to generate
     * @param textureLocation The texture to use for the wall item model
     */
    public void createWallItem(Block block, ResourceLocation textureLocation) {
        createInventoryModel(
                block,
                ModelTemplates.WALL_INVENTORY,
                new TextureMapping().put(TextureSlot.WALL, textureLocation)
        );
    }

    /**
     * Registers a flat (2D, {@code item/generated}-parented) item model for the block, either from an
     * explicit texture, or (if {@code itemLocation} is {@code null}) falling back to
     * {@link #createFlatItem(Block)}. Does nothing if the block has no item ({@link Items#AIR}).
     *
     * @param block        The block whose item model to generate
     * @param itemLocation The texture to use for the item model, or {@code null} to use the block's own texture
     */
    public void createFlatItem(Block block, @Nullable ResourceLocation itemLocation) {
        if (itemLocation == null) {
            this.createFlatItem(block);
            return;
        }
        final var item = block.asItem();
        if (item != Items.AIR) {
            final var modelLocation = ModelTemplates.FLAT_ITEM.create(
                    ModelLocationUtils.getModelLocation(item),
                    TextureMapping.layer0(itemLocation),
                    vanillaGenerator.modelOutput
            );
            delegateItemModel(block, modelLocation);
        }
    }

    /**
     * Creates a blockstate that randomly picks between the four Y-axis rotations (0/90/180/270 degrees) of
     * a single model, e.g. for blocks that should look less repetitive when placed in bulk.
     *
     * @param block The block to generate the blockstate for
     * @param model The model to rotate
     * @return The generated blockstate definition
     */
    public static MultiVariantGenerator randomTopModelVariant(Block block, ResourceLocation model) {
        return MultiVariantGenerator
                .dispatch(
                        block,
                        BlockModelGenerators.variants(
                                NOP.apply(BlockModelGenerators.plainModel(model)),
                                Y_ROT_90.apply(BlockModelGenerators.plainModel(model)),
                                Y_ROT_180.apply(BlockModelGenerators.plainModel(model)),
                                BlockModelGenerators.Y_ROT_270.apply(BlockModelGenerators.plainModel(model))
                        )
                );
    }

    /**
     * Gets the underlying model-output consumer that model files are emitted through.
     *
     * @return The model output consumer
     */
    public BiConsumer<ResourceLocation, ModelInstance> modelOutput() {
        return vanillaGenerator.modelOutput;
    }

    /**
     * Fluent helper, obtained from {@link #modelFor(Block)} and its overloads, for generating a related set
     * of blockstates/models (full block, slab, door, custom fence/fence-gate, ...) that share the same
     * {@link TexturedModel}/{@link TextureMapping}.
     */
    public class Builder {
        private ResourceLocation fullBlockLocation;
        private final TexturedModel model;
        private final TextureMapping mapping;
        private final Map<ModelTemplate, ResourceLocation> models = Maps.newHashMap();

        private Builder(TexturedModel model, TextureMapping mapping) {
            this.model = model;
            this.mapping = mapping;
        }

        /**
         * Generates the full-cube model and blockstate for {@code fullBlock}. Must be called before
         * {@link #createSlab(Block)}, which reuses the generated model for the double-slab state.
         *
         * @param fullBlock The block to generate the blockstate for
         * @return This builder, for chaining
         */
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

        /**
         * Delegates to vanilla's {@link BlockModelGenerators#createDoor(Block)} to generate the
         * blockstate/models for a door.
         *
         * @param doorBlock The block to generate the blockstate for
         * @return This builder, for chaining
         */
        public Builder createDoor(Block doorBlock) {
            vanillaGenerator.createDoor(doorBlock);
            itemModelDelegatedBlocks.add(doorBlock);
            return this;
        }

        /**
         * Generates the post/side (per-direction) blockstate and models (plus an inventory model) for a
         * "custom fence" - a fence whose sides are modeled per-direction rather than mirrored, using this
         * builder's texture mapping with a custom particle texture.
         *
         * @param fenceBlock The block to generate the blockstate for
         * @return This builder, for chaining
         */
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

        /**
         * Generates the open/closed (and wall-adjacent) blockstate and models for a "custom fence gate" -
         * a fence gate matched with a {@link #createCustomFence(Block) custom fence} - using this builder's
         * texture mapping with a custom particle texture.
         *
         * @param gateBlock The block to generate the blockstate for
         * @return This builder, for chaining
         */
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
            itemModelDelegatedBlocks.add(block);
        }

        /**
         * Generates the bottom/top/double blockstate and models (plus an item model) for a slab matched with
         * this builder's full block, reusing the full block's model for the double-slab state.
         *
         * @param slabBlock The block to generate the blockstate for
         * @return This builder, for chaining
         * @throws IllegalStateException if {@link #createFullBlock(Block)} was not called first
         */
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
