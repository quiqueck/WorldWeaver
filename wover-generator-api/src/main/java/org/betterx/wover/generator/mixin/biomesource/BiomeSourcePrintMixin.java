package org.betterx.wover.generator.mixin.biomesource;

import org.betterx.wover.generator.impl.biomesource.BiomeSourceManagerImpl;

import net.minecraft.world.level.biome.BiomeSource;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(BiomeSource.class)
public class BiomeSourcePrintMixin {
    @Override
    public String toString() {
        return BiomeSourceManagerImpl.printBiomeSourceInfo((BiomeSource) (Object) this);
    }
}
