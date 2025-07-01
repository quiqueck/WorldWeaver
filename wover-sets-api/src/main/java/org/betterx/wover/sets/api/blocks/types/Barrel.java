package org.betterx.wover.sets.api.blocks.types;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.client.trait.ClientBlockTraits;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.SlotDefinition;
import org.betterx.wover.sets.api.blocks.SlotType;

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
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class Barrel extends SlotDefinition {
    public Barrel() {
        super(SlotType.BARREL);
    }

    @Override
    protected BlockDefinition<?, ?> startBlockDefinition(BlockRegistry registry, String name) {
        return registry.defineDefaultBlockWithProps(name, BarrelBlock::new);
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        def.addTrait(BlockTraits.BARREL_BLOCK);
    }

    @Override
    protected BlockModelTrait buildModel(BlockSet<?> set) {
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
}
