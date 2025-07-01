package org.betterx.wover.sets.api.blocks.types;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.model.ModelTraitLibrary;
import org.betterx.wover.block.api.trait.BlockRecipeTrait;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.recipe.api.RecipeMaterial;
import org.betterx.wover.recipe.api.RecipeTraitLibrary;
import org.betterx.wover.sets.api.blocks.SlotType;
import org.betterx.wover.sets.api.blocks.WoodenBlockSet;
import org.betterx.wover.sets.api.blocks.WoodenSlotDefinition;

import net.minecraft.world.level.block.RotatedPillarBlock;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class Log extends WoodenSlotDefinition {
    protected final boolean mirroredTexture;
    protected final String[] alternativeTextureSuffixe;

    public Log() {
        this(false);
    }

    public Log(boolean mirroredTexture, String... alternativeTextureSuffixe) {
        this(SlotType.LOG, mirroredTexture, alternativeTextureSuffixe);
    }

    public Log(SlotType slot, boolean mirroredTexture, String... alternativeTextureSuffixe) {
        super(slot);
        this.mirroredTexture = mirroredTexture;
        this.alternativeTextureSuffixe = alternativeTextureSuffixe;
    }

    @Override
    protected BlockDefinition<?, ?> startBlockDefinition(BlockRegistry registry, String name) {
        return registry.defineDefaultBlockWithProps(name, RotatedPillarBlock::new);
    }

    @Override
    protected void addWoodSlotSpecificDefinitions(WoodenBlockSet<?> set, BlockDefinition<?, ?> def) {
        def
                .addTrait(BlockTraits.LOG_BLOCK.with(() -> set
                        .getBlockWithFallback(SlotType.STRIPPED_LOG, this.slot)
                        .defaultBlockState()
                ))
                .addTags(set.logsBlocksTag)
                .addItemTags(set.logsItemTag);
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected BlockModelTrait buildWoodModel(WoodenBlockSet<?> set) {
        return ModelTraitLibrary.log(
                this.mirroredTexture,
                this.alternativeTextureSuffixe
        );
    }

    @Override
    protected BlockRecipeTrait buildWoodRecipe(WoodenBlockSet<?> set) {
        // The Recipe is built before the block was created, so we need to defer the read
        // of the material until the recipe is actually created
        return RecipeTraitLibrary.log(RecipeMaterial.ofDeferredItemLike(() -> set.getBlock(SlotType.BARK)));
    }
}
