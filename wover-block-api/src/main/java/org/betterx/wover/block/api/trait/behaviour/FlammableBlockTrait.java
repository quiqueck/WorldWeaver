package org.betterx.wover.block.api.trait.behaviour;

import org.betterx.wover.block.api.trait.GenericBlockTrait;

public interface FlammableBlockTrait extends GenericBlockTrait {
    interface Builder extends GenericBlockTrait.BuilderWithDefault {
        FlammableBlockTrait withDefault();
        FlammableBlockTrait with(int burn, int speed);
    }

    int burn();
    int speed();
}
