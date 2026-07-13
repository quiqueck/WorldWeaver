package org.betterx.wover.pottable.impl;

import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.pottable.api.PottablePlant;
import org.betterx.wover.pottable.api.PottablePlantRegistry;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class PottablePlantRegistryImpl {
    private static boolean didInit = false;

    @ApiStatus.Internal
    public static void initialize() {
        if (didInit) return;
        didInit = true;

        DatapackRegistryBuilder.register(
                PottablePlantRegistry.POTTABLE_PLANT_REGISTRY,
                PottablePlantImpl.CODEC,
                ctx -> {}
        );
    }

    public static ResourceKey<PottablePlant> createKey(ResourceLocation id) {
        return ResourceKey.create(PottablePlantRegistry.POTTABLE_PLANT_REGISTRY, id);
    }

    @ApiStatus.Internal
    public static Holder<PottablePlant> register(
            @NotNull BootstrapContext<PottablePlant> ctx,
            @NotNull ResourceKey<PottablePlant> key,
            @NotNull ResourceKey<Block> block,
            @NotNull Optional<TagKey<Block>> validSoils
    ) {
        return ctx.register(key, new PottablePlantImpl(block, validSoils));
    }
}
