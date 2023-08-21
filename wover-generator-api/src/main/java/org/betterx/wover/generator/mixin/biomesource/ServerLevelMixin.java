package org.betterx.wover.generator.mixin.biomesource;

import org.betterx.wover.common.generator.api.biomesource.BiomeSourceWithNoiseRelatedSettings;
import org.betterx.wover.common.generator.api.biomesource.BiomeSourceWithSeed;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WritableLevelData;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {
    protected ServerLevelMixin(
            WritableLevelData writableLevelData,
            ResourceKey<Level> resourceKey,
            RegistryAccess registryAccess,
            Holder<DimensionType> holder,
            Supplier<ProfilerFiller> supplier,
            boolean bl,
            boolean bl2,
            long l,
            int i
    ) {
        super(writableLevelData, resourceKey, registryAccess, holder, supplier, bl, bl2, l, i);
    }


    @Inject(method = "<init>*", at = @At("TAIL"))
    private void wover_onServerWorldInit(
            MinecraftServer minecraftServer,
            Executor executor,
            LevelStorageSource.LevelStorageAccess levelStorageAccess,
            ServerLevelData serverLevelData,
            ResourceKey resourceKey,
            LevelStem levelStem,
            ChunkProgressListener chunkProgressListener,
            boolean bl,
            long l,
            List list,
            boolean bl2,
            RandomSequences randomSequences,
            CallbackInfo ci
    ) {
        final ServerLevel level = ServerLevel.class.cast(this);

        if (levelStem.generator().getBiomeSource() instanceof BiomeSourceWithSeed source) {
            source.setSeed(level.getSeed());
        }

        if (levelStem.generator().getBiomeSource() instanceof BiomeSourceWithNoiseRelatedSettings bcl
                && levelStem.generator() instanceof NoiseBasedChunkGenerator noiseGenerator) {
            bcl.onLoadGeneratorSettings(noiseGenerator.generatorSettings().value());
        }
    }
}