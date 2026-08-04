package de.ambertation.wover.item.impl.trait;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.item.api.render.ItemRenderTraits;
import de.ambertation.wover.entrypoint.LibWoverSets;
import de.ambertation.wover.item.api.ItemDefinition;
import de.ambertation.wover.item.api.trait.AbstractItemTraitBuilder;
import de.ambertation.wover.item.api.trait.BoatItemTrait;
import de.ambertation.wover.item.api.trait.ItemTrait;
import de.ambertation.wover.item.api.trait.ItemTraitKey;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BoatItem;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class BoatItemBuilder extends AbstractItemTraitBuilder<BoatItem, BoatItemTrait> implements BoatItemTrait.Builder {
    public static final BoatItemBuilder BUILDER = new BoatItemBuilder();

    private BoatItemBuilder() {
        super(ItemTraitKey.ofUnique(LibWoverSets.C, "is_boat"));
    }

    public @Nullable List<ItemTrait<?, ?>> withDefault() {
        if (!ModCore.isDatagen()) return combine(ItemRenderTraits.BOAT_RENDERER.withDefault());
        return combine(new BoatItemBuilder.Trait(false, false), ItemRenderTraits.BOAT_RENDERER.with(false, false));
    }

    public @Nullable List<ItemTrait<?, ?>> with(boolean withChest, boolean isRaft) {
        if (!ModCore.isDatagen()) return combine(ItemRenderTraits.BOAT_RENDERER.with(withChest, isRaft));
        return combine(
                new BoatItemBuilder.Trait(withChest, isRaft),
                ItemRenderTraits.BOAT_RENDERER.with(withChest, isRaft)
        );
    }

    public class Trait extends ItemTraitImpl<BoatItem, BoatItemTrait> implements BoatItemTrait {
        private final boolean withChest;
        private final boolean isRaft;

        Trait(boolean withChest, boolean isRaft) {
            this.withChest = withChest;
            this.isRaft = isRaft;
        }

        @Override
        public ItemTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(ItemDefinition<BoatItem, ? extends ItemDefinition<BoatItem, ?>> definition) {
            // rafts are part of the vanilla "boats" tag as well (see minecraft:bamboo_raft)
            definition.addTags(ItemTags.BOATS);
            if (withChest) {
                definition.addTags(ItemTags.CHEST_BOATS);
            }
        }

        @Override
        public boolean withChest() {
            return withChest;
        }

        @Override
        public boolean isRaft() {
            return isRaft;
        }
    }
}
