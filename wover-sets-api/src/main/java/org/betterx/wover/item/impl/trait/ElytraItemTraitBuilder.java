package org.betterx.wover.item.impl.trait;

import org.betterx.wover.block.api.client.model.ModelTraitLibrary;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverSets;
import org.betterx.wover.item.api.ItemDefinition;
import org.betterx.wover.item.api.trait.AbstractItemTraitBuilder;
import org.betterx.wover.item.api.trait.ElytraItemTrait;
import org.betterx.wover.item.api.trait.ItemTrait;
import org.betterx.wover.item.api.trait.ItemTraitKey;

import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElytraItemTraitBuilder extends AbstractItemTraitBuilder<Item, ElytraItemTrait> implements ElytraItemTrait.Builder {
    public static final ElytraItemTraitBuilder BUILDER = new ElytraItemTraitBuilder();

    private ElytraItemTraitBuilder() {
        super(ItemTraitKey.ofUnique(LibWoverSets.C, "is_elytra"));
    }

    public @Nullable List<ItemTrait<?, ?>> withDefault() {
        return with(Items.PHANTOM_MEMBRANE);
    }

    public @Nullable List<ItemTrait<?, ?>> with(@NotNull Item repairedWith) {
        if (ModCore.isDatagen()) {
            return List.of(new Trait(repairedWith), ModelTraitLibrary.elytra());
        }
        return combine(new Trait(repairedWith));
    }

    public class Trait extends ItemTraitImpl<Item, ElytraItemTrait> implements ElytraItemTrait {
        private final @NotNull Item repairedWith;

        Trait(@NotNull Item repairedWith) {
            this.repairedWith = repairedWith;
        }

        @Override
        public ItemTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(ItemDefinition<Item, ? extends ItemDefinition<Item, ?>> definition) {
            definition.addTags(ItemTags.DURABILITY_ENCHANTABLE, ItemTags.EQUIPPABLE_ENCHANTABLE);

            definition.component(DataComponents.GLIDER, Unit.INSTANCE)
                      .component(
                              DataComponents.EQUIPPABLE,
                              Equippable.builder(EquipmentSlot.CHEST)
                                        .setEquipSound(SoundEvents.ARMOR_EQUIP_ELYTRA)
                                        .setAsset(
                                                EquipmentAssets.ELYTRA)
                                        .setDamageOnHurt(false)
                                        .build()
                      )
                      .repairable(this.repairedWith);

        }

        @Override
        public Item repairedWith() {
            return repairedWith;
        }
    }
}

