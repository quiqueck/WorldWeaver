package org.betterx.wover.generator.mixin.client;

import org.betterx.wover.generator.impl.chunkgenerator.ChunkGeneratorManagerImpl;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.storage.LevelStorageSource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(WorldSelectionList.WorldListEntry.class)
public class WorldListEntryMixin {
    @Inject(
            method = "recreateWorld",
            at = @At(value = "INVOKE", target = "Lcom/mojang/datafixers/util/Pair;getFirst()Ljava/lang/Object;"),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void wover_recreateWorld(
            CallbackInfo ci,
            LevelStorageSource.LevelStorageAccess levelStorageAccess,
            Pair<LevelSettings, WorldCreationContext> pair
    ) {
        ChunkGeneratorManagerImpl.onWorldReCreate(levelStorageAccess, pair.getSecond());
    }
}
