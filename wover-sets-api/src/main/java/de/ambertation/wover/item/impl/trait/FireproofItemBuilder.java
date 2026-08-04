package de.ambertation.wover.item.impl.trait;

import de.ambertation.wover.entrypoint.LibWoverSets;
import de.ambertation.wover.item.api.ItemDefinition;
import de.ambertation.wover.item.api.trait.AbstractItemTraitBuilder;
import de.ambertation.wover.item.api.trait.GenericItemTrait;
import de.ambertation.wover.item.api.trait.ItemTrait;
import de.ambertation.wover.item.api.trait.ItemTraitKey;

import net.minecraft.world.item.Item;

import org.jetbrains.annotations.Nullable;

public class FireproofItemBuilder extends AbstractItemTraitBuilder.Generic implements GenericItemTrait.BuilderWithDefault {
    public static final FireproofItemBuilder BUILDER = new FireproofItemBuilder();
    private final Trait DEFAULT_TRAIT = new Trait();

    private FireproofItemBuilder() {
        super(ItemTraitKey.ofUnique(LibWoverSets.C, "is_fireproof"));
    }

    public @Nullable ItemTrait<?, ?> withDefault() {
        return DEFAULT_TRAIT;
    }


    public class Trait extends ItemTraitImpl.Generic {
        Trait() {
        }

        @Override
        public ItemTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(ItemDefinition<Item, ? extends ItemDefinition<Item, ?>> definition) {
            definition.fireResistant();
        }
    }
}
