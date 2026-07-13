package org.betterx.wover.structure.api.pools;

import org.betterx.wover.structure.impl.pools.StructurePoolElementTypeManagerImpl;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;

/**
 * Registers custom {@link StructurePoolElementType}s, needed if you implement your own
 * {@link StructurePoolElement} subclass.
 */
public class StructurePoolElementTypeManager {
    /**
     * Registers a new {@link StructurePoolElementType} for the given {@link ResourceLocation}.
     *
     * @param location The location of the {@link StructurePoolElementType}
     * @param codec    The {@link MapCodec} used to (de)serialize the {@link StructurePoolElement}
     * @param <P>      The {@link StructurePoolElement} type
     * @return The registered {@link StructurePoolElementType}
     */
    public static <P extends StructurePoolElement> StructurePoolElementType<P> register(
            ResourceLocation location,
            MapCodec<P> codec
    ) {
        return StructurePoolElementTypeManagerImpl.register(location, codec);
    }
}
