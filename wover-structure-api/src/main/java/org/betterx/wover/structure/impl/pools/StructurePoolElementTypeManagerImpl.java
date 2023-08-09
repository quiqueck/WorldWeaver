package org.betterx.wover.structure.impl.pools;

import org.betterx.wover.entrypoint.WoverStructure;
import org.betterx.wover.legacy.api.LegacyHelper;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;

import org.jetbrains.annotations.ApiStatus;

public class StructurePoolElementTypeManagerImpl {
    public static final StructurePoolElementType<SingleEndPoolElement> END = registerLegacy(
            WoverStructure.C.id("single_end_pool_element"), SingleEndPoolElement.CODEC);


    public static <P extends StructurePoolElement> StructurePoolElementType<P> register(
            ResourceLocation location,
            Codec<P> codec
    ) {
        return Registry.register(BuiltInRegistries.STRUCTURE_POOL_ELEMENT, location, () -> codec);
    }

    public static <P extends StructurePoolElement> StructurePoolElementType<P> registerLegacy(
            ResourceLocation location,
            Codec<P> codec
    ) {
        final StructurePoolElementType<P> res = register(location, codec);
        register(LegacyHelper.BCLIB_CORE.convertNamespace(location), codec);
        return res;
    }

    @ApiStatus.Internal
    public static void ensureStaticallyLoaded() {
        // NO-OP
    }
}
