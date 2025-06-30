package org.betterx.wover.sets.api.blocks;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.trait.BlockRecipeGeneratorTrait;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class SlotDefinition {
    public final SlotType slot;

    protected SlotDefinition(SlotType slot) {
        this.slot = slot;
    }

    public String getName(BlockSet<?> set) {
        return set.baseName + "_" + slot.suffix();
    }

    public void createBlockDefinition(BlockSet<?> set, Consumer<BlockDefinition<?, ?>> blockDefinitionConsumer) {
        var definition = BlockRegistry
                .forMod(set.C)
                .defineDefaultBlock(this.getName(set))
                .addTrait(BlockRecipeGeneratorTrait.BUILDER.with(
                        (key, block, context) -> this.buildRecipe(set, key, block, context)
                ));

        this.addSlotSpecificDefinitions(set, definition);
        set.addCommonBlockDefinitions(this.slot, definition);

        blockDefinitionConsumer.accept(definition);
    }

    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
    }

    protected void buildRecipe(BlockSet<?> set, ResourceKey<Block> key, Block block, RecipeBuilder.Context context) {

    }
}
