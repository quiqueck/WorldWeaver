package org.betterx.wover.biome.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(HolderSet.Named.class)
public interface HolderSetNamedAccessor<T> {
    @Accessor("contents")
    List<Holder<T>> wover_getContents();

    @Accessor("contents")
    void wover_setContents(List<Holder<T>> contents);
}
