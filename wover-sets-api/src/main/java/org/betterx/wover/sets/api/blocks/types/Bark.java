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

public class Bark extends WoodenSlotDefinition {
    protected final boolean mirroredTexture;
    protected final String[] alternativeTextureSuffixe;

    public Bark() {
        this(false);
    }

    public Bark(boolean mirroredTexture, String... alternativeTextureSuffixe) {
        this(SlotType.BARK, mirroredTexture, alternativeTextureSuffixe);
    }

    public Bark(SlotType slot, boolean mirroredTexture, String... alternativeTextureSuffixe) {
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
                .addTrait(BlockTraits.BARK_BLOCK.with(() -> set
                        .getBlockWithFallback(SlotType.STRIPPED_BARK, this.slot)
                        .defaultBlockState()
                ))
                .addTags(set.logsBlocksTag)
                .addItemTags(set.logsItemTag);
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected BlockModelTrait buildWoodModel(WoodenBlockSet<?> set) {
        return ModelTraitLibrary.bark(
                () -> set.getBlock(SlotType.LOG),
                this.mirroredTexture,
                this.alternativeTextureSuffixe
        );
    }

    @Override
    protected BlockRecipeTrait buildWoodRecipe(WoodenBlockSet<?> set) {
        // The Recipe is built before the block was created, so we need to defer the read
        // of the material until the recipe is actually created
        return RecipeTraitLibrary.bark(RecipeMaterial.ofDeferredItemLike(() -> set.getBlock(SlotType.LOG)));
    }
}
