package org.betterx.wover.item.api.trait;

import org.betterx.wover.item.impl.trait.ItemTraitImpl;

public interface ItemTraitLookup {
    boolean hasTrait(ItemTraitImpl<?, ?> trait);

    boolean hasTrait(ItemTraitKey traitKey);

    boolean hasTrait(ItemTraitBuilder<?, ?> traitBuilder);
}