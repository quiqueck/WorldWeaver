package org.betterx.wover.generator.mixin.generator;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGenerators;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkGenerators.class)
public class ChunkGeneratorsMixin {
    @Inject(method = "bootstrap", at = @At(value = "HEAD"))
    private static void wover_bootstrap(
            Registry<Codec<? extends ChunkGenerator>> registry,
            CallbackInfoReturnable<Codec<? extends ChunkGenerator>> cir
    ) {
//        Registry.register(registry, WoverChunkGenerator.ID, WoverChunkGenerator.CODEC);
//        Registry.register(registry, WoverChunkGenerator.LEGACY_ID, LegacyHelper.wrap(WoverChunkGenerator.CODEC));
    }
}

