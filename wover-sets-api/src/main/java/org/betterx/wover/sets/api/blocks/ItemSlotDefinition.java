package org.betterx.wover.sets.api.blocks;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.item.api.ItemDefinition;
import org.betterx.wover.item.api.ItemRegistry;
import org.betterx.wover.item.api.client.trait.ItemModelTrait;
import org.betterx.wover.item.api.trait.ItemRecipeTrait;
import org.betterx.wover.item.api.trait.ItemTraitLookup;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemSlotDefinition extends SlotDefinition {
    protected ItemSlotDefinition(SlotType slot) {
        super(slot);
    }

    public void createItemDefinition(BlockSet<?> set, Consumer<ItemDefinition<?, ?>> itemDefinitionConsumer) {
        ItemDefinition<?, ? extends ItemDefinition<?, ?>> definition = startItemDefinition(
                ItemRegistry.forMod(set.C),
                this.getName(set)
        );

        if (definition == null) return;

        this.addSlotSpecificDefinitions(set, definition);
        set.addCommonItemDefinitions(this.slot, definition);

        definition.addTrait(this.buildRecipe(set, definition));
        if (ModCore.isClient()) definition.addTrait(this.buildModel(set, definition));

        itemDefinitionConsumer.accept(definition);
    }

    protected @Nullable ItemDefinition<?, ?> startItemDefinition(
            @NotNull ItemRegistry registry,
            @NotNull String name
    ) {
        return registry.defineDefaultItem(name);
    }

    protected void addSlotSpecificDefinitions(BlockSet<?> set, ItemDefinition<?, ?> def) {
    }

    @Environment(EnvType.CLIENT)
    protected ItemModelTrait buildModel(BlockSet<?> set, ItemTraitLookup traitLookup) {
        return null;
    }

    protected ItemRecipeTrait buildRecipe(BlockSet<?> set, ItemTraitLookup traitLookup) {
        return null;
    }
}
