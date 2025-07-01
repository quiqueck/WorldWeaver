package org.betterx.wover.sets.api.blocks;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.trait.BlockRecipeTrait;
import org.betterx.wover.core.api.ModCore;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

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
        var definition = startBlockDefinition(BlockRegistry.forMod(set.C), this.getName(set));

        this.addSlotSpecificDefinitions(set, definition);
        set.addCommonBlockDefinitions(this.slot, definition);

        definition.addTrait(this.buildRecipe(set));
        if (ModCore.isClient()) definition.addTrait(this.buildModel(set));

        blockDefinitionConsumer.accept(definition);
    }

    protected BlockDefinition<?, ?> startBlockDefinition(BlockRegistry registry, String name) {
        return registry.defineDefaultBlock(name);
    }

    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
    }

    @Environment(EnvType.CLIENT)
    protected BlockModelTrait buildModel(BlockSet<?> set) {
        return null;
    }

    protected BlockRecipeTrait buildRecipe(BlockSet<?> set) {
        return null;
    }
}
