package org.betterx.wover.generator.mixin.biomesource;

import org.betterx.wover.generator.impl.biomesource.WoverBiomeSourceImpl;

import net.minecraft.world.level.biome.BiomeSource;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(BiomeSource.class)
public class BiomeSourcePrintMixin {
    @Override
    public String toString() {
        BiomeSource self = (BiomeSource) (Object) this;
        return "\n" + getClass().getSimpleName() + " (" + Integer.toHexString(hashCode()) + ")" +
                "\n    biomes     = " + self.possibleBiomes().size() +
                "\n    namespaces = " + WoverBiomeSourceImpl.getNamespaces(self.possibleBiomes());
    }
}
