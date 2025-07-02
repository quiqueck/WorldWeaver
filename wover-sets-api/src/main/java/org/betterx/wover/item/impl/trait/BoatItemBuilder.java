package org.betterx.wover.item.impl.trait;

import org.betterx.wover.block.api.client.trait.ClientBlockTraits;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverSets;
import org.betterx.wover.item.api.ItemDefinition;
import org.betterx.wover.item.api.trait.AbstractItemTraitBuilder;
import org.betterx.wover.item.api.trait.BoatItemTrait;
import org.betterx.wover.item.api.trait.ItemTrait;
import org.betterx.wover.item.api.trait.ItemTraitKey;
import org.betterx.wover.tag.api.predefined.CommonItemTags;

import net.minecraft.world.item.BoatItem;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class BoatItemBuilder extends AbstractItemTraitBuilder<BoatItem, BoatItemTrait> implements BoatItemTrait.Builder {
    public static final BoatItemBuilder BUILDER = new BoatItemBuilder();

    private BoatItemBuilder() {
        super(ItemTraitKey.ofUnique(LibWoverSets.C, "is_boat"));
    }

    public @Nullable List<ItemTrait<?, ?>> withDefault() {
        if (!ModCore.isDatagen()) return combine(ClientBlockTraits.BOAT_RENDERER.withDefault());
        return combine(new BoatItemBuilder.Trait(), ClientBlockTraits.BOAT_RENDERER.withDefault());
    }

    public class Trait extends ItemTraitImpl<BoatItem, BoatItemTrait> implements BoatItemTrait {
        @Override
        public ItemTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(ItemDefinition<BoatItem, ? extends ItemDefinition<BoatItem, ?>> definition) {
            definition.addTags(CommonItemTags.BOAT);
        }
    }
}
