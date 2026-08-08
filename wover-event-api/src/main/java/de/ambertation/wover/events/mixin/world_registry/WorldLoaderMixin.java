package de.ambertation.wover.events.mixin.world_registry;

import de.ambertation.wover.events.api.types.OnRegistryReady;
import de.ambertation.wover.events.impl.WorldLifecycleImpl;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.resources.CloseableResourceManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

@Mixin(WorldLoader.class)
public class WorldLoaderMixin {
    // NOTE: since 26.1 RegistryDataLoader.load(...) and ReloadableServerResources.loadResources(...) are no
    // longer called directly from WorldLoader#load - that method now builds a CompletableFuture chain, so the
    // calls we need to hook live in the compiler-generated lambda bodies (lambda$load$1 / lambda$load$2) rather
    // than in "load" itself.
    //
    // Which lambda is which is not the source order: lambda$load$0 holds the world-registry load,
    // lambda$load$1 the DIMENSION_REGISTRIES load and lambda$load$2 the ReloadableServerResources load.
    // Verify with `javap -c` after a Minecraft update rather than assuming. Re-checked against
    // 26.3-snapshot-6: lambda$load$1 has exactly the parameter list below, unchanged from 26.2.
    //
    // 26.3 renamed RegistryLayer.WORLDGEN to RegistryLayer.WORLD (and WORLDGEN_REGISTRIES to
    // WORLD_REGISTRIES) when the RELOADABLE layer was added. The layer list is STATIC, WORLD, DIMENSIONS,
    // RELOADABLE, so WORLD sits in the slot WORLDGEN used to occupy and getAccessForLoading(DIMENSIONS)
    // still yields the same composite this event has always published.

    /**
     * Emits {@link OnRegistryReady.Stage#LOADING} once the world registries are loaded and before the
     * dimension registries are, so {@link de.ambertation.wover.state.api.WorldState} has a usable
     * RegistryAccess while {@code LevelStem}s (and with them our BiomeSources) are decoded.
     * <p>
     * The last parameter of this lambda is the result of the world-registry load that just finished, which
     * is exactly what we need. This used to run a second, complete {@code RegistryDataLoader.load(...)} here
     * to synthesize an equivalent - the client never needed that (see {@code CreateWorldScreenMixin}, which
     * emits the same stage from the context it already has), and the common path does not either.
     * <p>
     * Dropping that extra load also stops every registry bootstrap - and therefore every
     * {@code BOOTSTRAP_*} event - from firing a second time per world load.
     */
    @Inject(method = "lambda$load$1", at = @At("HEAD"))
    private static <D, R> void wover_captureWorldRegistries(
            List<?> worldContextRegistries,
            CloseableResourceManager resources,
            Executor backgroundExecutor,
            Pair<?, ?> packsAndResourceManager,
            WorldLoader.WorldDataSupplier<D> worldDataSupplier,
            LayeredRegistryAccess<RegistryLayer> initialLayers,
            List<?> staticLayerTags,
            WorldLoader.InitConfig config,
            Executor mainThreadExecutor,
            WorldLoader.ResultFactory<D, R> resultFactory,
            RegistryAccess.Frozen loadedWorldRegistries,
            CallbackInfoReturnable<CompletionStage<R>> cir
    ) {
        final RegistryAccess.Frozen frozen = initialLayers
                .replaceFrom(RegistryLayer.WORLD, loadedWorldRegistries)
                .getAccessForLoading(RegistryLayer.DIMENSIONS);

        WorldLifecycleImpl.WORLD_REGISTRY_READY.emit(frozen, OnRegistryReady.Stage.LOADING);
    }

    //this is the place a new Registry access gets first instantiated
    //either when a new Datapack was added to a world on the create-screen
    //or because we did start world loading
    @ModifyArg(method = "lambda$load$2", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/ReloadableServerResources;loadResources(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/core/LayeredRegistryAccess;Ljava/util/List;Lnet/minecraft/world/flag/FeatureFlagSet;Lnet/minecraft/commands/Commands$CommandSelection;Lnet/minecraft/server/permissions/PermissionSet;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"))
    private static LayeredRegistryAccess<RegistryLayer> wover_captureRegistry(LayeredRegistryAccess<RegistryLayer> layered) {
        WorldLifecycleImpl.WORLD_REGISTRY_READY.emit(
                layered.getAccessForLoading(RegistryLayer.RELOADABLE),
                OnRegistryReady.Stage.PREPARATION
        );
        return layered;
    }
}
