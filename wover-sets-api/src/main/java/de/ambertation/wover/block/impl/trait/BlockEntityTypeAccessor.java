package de.ambertation.wover.block.impl.trait;

import net.minecraft.world.level.block.Block;

import java.util.Set;

public interface BlockEntityTypeAccessor {
    Set<Block> wover_getValidSet();
    void wover_setValidSet(Set<Block> validSet);
}
