package org.betterx.wover.block.api.model;

import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.client.trait.ClientBlockTraits;

import static net.minecraft.client.data.models.BlockModelGenerators.*;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.block.model.VariantMutator;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
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

    public static BlockModelTrait slab(Supplier<Block> sourceMaterial) {
        return ClientBlockTraits.MODEL.with((key, block, generator) -> {
            generator.createSlab(block, sourceMaterial.get());
        });
    }

    public static BlockModelTrait planks() {
        return ClientBlockTraits.MODEL.with((key, block, generator) -> {
            generator.createCubeModel(block);
        });
    }
}
