package org.betterx.wover.sets.api.blocks.types;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.client.model.ModelTraitLibrary;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.trait.BlockRecipeTrait;
import org.betterx.wover.block.api.trait.BlockTraitLookup;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.recipe.api.RecipeTraitLibrary;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.SlotType;
import org.betterx.wover.sets.api.blocks.WoodenBlockSet;
import org.betterx.wover.sets.api.blocks.WoodenSlotDefinition;

import net.minecraft.world.level.block.RotatedPillarBlock;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.NotNull;

public class Log extends WoodenSlotDefinition {
    public static final byte STRIPABLE_FLAG = 0x01;
    public static final byte MIRRORED_TEXTURE_FLAG = 0x02;
    protected final byte flags;

    protected final String[] alternativeTextureSuffixe;

    public Log(boolean stripable) {
        this(stripable, false);
    }

    public Log(boolean stripable, boolean mirroredTexture, String... alternativeTextureSuffixe) {
        this(stripable ? SlotType.LOG : SlotType.STRIPPED_LOG, stripable, mirroredTexture, alternativeTextureSuffixe);
    }

    public Log(SlotType slot, boolean stripable, boolean mirroredTexture, String... alternativeTextureSuffixe) {
        super(slot);

        byte flags = 0;
        if (stripable) flags |= STRIPABLE_FLAG; // Set the stripable flag
        if (mirroredTexture) flags |= MIRRORED_TEXTURE_FLAG; // Set the mirrored texture flag
        this.flags = flags;

        this.alternativeTextureSuffixe = alternativeTextureSuffixe;
    }

    @Override
    protected BlockDefinition<?, ?> startBlockDefinition(
            BlockRegistry registry,
            @NotNull BlockSet<?> set,
            String name
    ) {
        return registry.defineDefaultBlockWithProps(name, RotatedPillarBlock::new);
    }

    @Override
    protected void addWoodSlotSpecificDefinitions(WoodenBlockSet<?> set, BlockDefinition<?, ?> def) {
        if ((this.flags & STRIPABLE_FLAG) != 0) { // Is Stripable?
            def.addTrait(BlockTraits.LOG_BLOCK.with((oldState) -> set
                    .getBlockWithFallback(SlotType.STRIPPED_LOG, this.slot)
                    .defaultBlockState()
            ));
        } else {
            def.addTrait(BlockTraits.LOG_BLOCK);
        }
        def.addTags(set.logsBlocksTag)
           .addItemTags(set.logsItemTag);
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        return ModelTraitLibrary.log(
                (this.flags & MIRRORED_TEXTURE_FLAG) != 0,
                this.alternativeTextureSuffixe
        );
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        final var stripable = (this.flags & STRIPABLE_FLAG) != 0;

        // The Recipe is built before the block was created, so we need to defer the read
        // of the material until the recipe is actually created
        return RecipeTraitLibrary.log(set.recipeMaterial(stripable ? SlotType.BARK : SlotType.STRIPPED_BARK));
    }
}
