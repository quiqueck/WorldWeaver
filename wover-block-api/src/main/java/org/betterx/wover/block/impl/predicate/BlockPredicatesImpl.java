package org.betterx.wover.block.impl.predicate;

import org.betterx.wover.block.api.predicate.IsFullShape;
import org.betterx.wover.entrypoint.WoverBlockAndItem;
import org.betterx.wover.legacy.api.LegacyHelper;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;

public class BlockPredicatesImpl {
    public static final BlockPredicateType<IsFullShape> FULL_SHAPE = register(
            WoverBlockAndItem.C.id("full_shape"),
            IsFullShape.CODEC
    );

    private static final BlockPredicateType<IsFullShape> FULL_SHAPE_LEGACY = register(
            LegacyHelper.BCLIB_CORE.id("full_shape"),
            IsFullShape.CODEC
    );

    public static <P extends BlockPredicate> BlockPredicateType<P> register(ResourceLocation location, Codec<P> codec) {
        return Registry.register(BuiltInRegistries.BLOCK_PREDICATE_TYPE, location, () -> codec);
    }

    public static void ensureStaticInitialization() {

    }
}
