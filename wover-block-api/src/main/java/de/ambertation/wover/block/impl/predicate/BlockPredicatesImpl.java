package de.ambertation.wover.block.impl.predicate;

import de.ambertation.wover.block.api.predicate.IsFullShape;
import de.ambertation.wover.entrypoint.LibWoverBlock;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;

public class BlockPredicatesImpl {
    public static final BlockPredicateType<IsFullShape> FULL_SHAPE = register(
            LibWoverBlock.C.id("full_shape"),
            IsFullShape.CODEC
    );

    public static <P extends BlockPredicate> BlockPredicateType<P> register(
            ResourceLocation location,
            MapCodec<P> codec
    ) {
        return Registry.register(BuiltInRegistries.BLOCK_PREDICATE_TYPE, location, () -> codec);
    }

    public static void ensureStaticInitialization() {

    }
}
