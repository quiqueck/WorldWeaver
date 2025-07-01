package org.betterx.wover.block.api.model;

import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.client.trait.ClientBlockTraits;

import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.function.Supplier;

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
