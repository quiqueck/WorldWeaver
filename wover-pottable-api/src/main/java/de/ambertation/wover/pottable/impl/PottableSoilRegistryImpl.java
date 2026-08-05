package de.ambertation.wover.pottable.impl;

import de.ambertation.wover.core.api.registry.DatapackRegistryBuilder;
import de.ambertation.wover.pottable.api.PottableSoil;
import de.ambertation.wover.pottable.api.PottableSoilRegistry;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class PottableSoilRegistryImpl {
    private static boolean didInit = false;

    @ApiStatus.Internal
    public static void initialize() {
        if (didInit) return;
        didInit = true;

        DatapackRegistryBuilder.register(
                PottableSoilRegistry.POTTABLE_SOIL_REGISTRY,
                PottableSoilImpl.CODEC,
                ctx -> {}
        );
    }

    public static ResourceKey<PottableSoil> createKey(Identifier id) {
        return ResourceKey.create(PottableSoilRegistry.POTTABLE_SOIL_REGISTRY, id);
    }

    @ApiStatus.Internal
    public static Holder<PottableSoil> register(
            @NotNull BootstrapContext<PottableSoil> ctx,
            @NotNull ResourceKey<PottableSoil> key,
            @NotNull ResourceKey<Block> block
    ) {
        return ctx.register(key, new PottableSoilImpl(block));
    }
}
