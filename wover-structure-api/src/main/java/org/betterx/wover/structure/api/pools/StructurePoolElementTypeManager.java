package org.betterx.wover.structure.api.pools;

import org.betterx.wover.structure.impl.pools.StructurePoolElementTypeManagerImpl;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;

public class StructurePoolElementTypeManager {
    public static <P extends StructurePoolElement> StructurePoolElementType<P> register(
            ResourceLocation location,
            Codec<P> codec
    ) {
        return StructurePoolElementTypeManagerImpl.register(location, codec);
    }
}
