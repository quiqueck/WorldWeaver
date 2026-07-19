package org.betterx.wover.structure.impl.pools;

import org.betterx.wover.entrypoint.LibWoverStructure;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;

import org.jetbrains.annotations.ApiStatus;

public class StructurePoolElementTypeManagerImpl {
    public static final StructurePoolElementType<SingleEndPoolElement> END = registerLegacy(
            LibWoverStructure.C.id("single_end_pool_element"), SingleEndPoolElement.CODEC);


    public static <P extends StructurePoolElement> StructurePoolElementType<P> register(
            ResourceLocation location,
            MapCodec<P> codec
    ) {
        return Registry.register(BuiltInRegistries.STRUCTURE_POOL_ELEMENT, location, () -> codec);
    }

    public static <P extends StructurePoolElement> StructurePoolElementType<P> registerLegacy(
            ResourceLocation location,
            MapCodec<P> codec
    ) {
        final StructurePoolElementType<P> res = register(location, codec);
        return res;
    }

    @ApiStatus.Internal
    public static void ensureStaticallyLoaded() {
        // NO-OP
    }
}
