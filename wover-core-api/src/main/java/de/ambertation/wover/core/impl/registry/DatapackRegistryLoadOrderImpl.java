package de.ambertation.wover.core.impl.registry;

import de.ambertation.wover.entrypoint.LibWoverCore;

import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.jetbrains.annotations.ApiStatus;

/**
 * Tracks how far each registry of the running datapack load has progressed, so one registry's
 * decoder can wait for another registry's elements.
 * <p>
 * See {@link de.ambertation.wover.core.api.registry.DatapackRegistryLoadOrder} for the why.
 */
@ApiStatus.Internal
public class DatapackRegistryLoadOrderImpl {
    /**
     * How long {@link #awaitElements(ResourceKey)} waits before giving up and letting the caller
     * proceed with whatever state the dependency has reached so far. Only ever reached if a
     * registry's load task died without completing its future, which would fail the whole load
     * anyway - the timeout just makes sure we fail with that error instead of hanging.
     */
    private static final long TIMEOUT_MS = 30_000;

    /**
     * One future per registry in the load that is currently running, completed once that registry
     * has registered all of its elements. Replaced wholesale by {@link #beginLoad(List)}; registries
     * that are not part of the running load are simply absent, which is what makes
     * {@link #awaitElements(ResourceKey)} a no-op outside of datapack loading.
     */
    private static volatile Map<ResourceKey<? extends Registry<?>>, CompletableFuture<Void>> PENDING = Map.of();

    @ApiStatus.Internal
    public static void beginLoad(List<RegistryDataLoader.RegistryData<?>> registriesToLoad) {
        final Map<ResourceKey<? extends Registry<?>>, CompletableFuture<Void>> pending = new HashMap<>();
        for (RegistryDataLoader.RegistryData<?> data : registriesToLoad) {
            pending.put(data.key(), new CompletableFuture<>());
        }
        PENDING = Map.copyOf(pending);
    }

    @ApiStatus.Internal
    public static void markElementsRegistered(ResourceKey<? extends Registry<?>> registryKey) {
        final CompletableFuture<Void> ready = PENDING.get(registryKey);
        if (ready != null) ready.complete(null);
    }

    public static void awaitElements(ResourceKey<? extends Registry<?>> registryKey) {
        final CompletableFuture<Void> ready = PENDING.get(registryKey);
        if (ready == null || ready.isDone()) return;

        try {
            // On a ForkJoinPool worker - which is where every registry load task runs - this goes
            // through ForkJoinPool.managedBlock, so the pool compensates for the parked thread
            // instead of losing a unit of parallelism. That is what keeps this safe even when
            // Util.backgroundExecutor() has a parallelism of one.
            ready.get(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            LibWoverCore.C.log.warn(
                    "Timed out waiting for " + registryKey.identifier()
                            + " to finish loading. Registries that depend on it may be incomplete."
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            LibWoverCore.C.log.warn("Failed waiting for " + registryKey.identifier() + " to load", e);
        }
    }
}
