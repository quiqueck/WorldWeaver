package de.ambertation.wover.core.api.registry;

import de.ambertation.wover.core.impl.registry.DatapackRegistryLoadOrderImpl;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

/**
 * Lets one datapack registry's element decoder wait for another datapack registry to finish
 * registering its elements.
 * <p>
 * Since Minecraft 26.1.2 {@code RegistryDataLoader} starts a load task for every registry at once
 * and joins them with {@code CompletableFuture.allOf}, so there is no ordering between registries.
 * That is fine for ordinary cross-registry references, which resolve through unbound
 * {@link net.minecraft.core.Holder.Reference}s that get bound whenever the other registry gets
 * around to registering them. It is <em>not</em> fine when decoding one registry reads a mutable
 * side table that another registry's elements write into - the decoder then captures whatever
 * happens to be in that table at the instant it runs.
 * <p>
 * {@link #awaitElements(ResourceKey)} turns that implicit race into an explicit dependency: the
 * caller blocks until every element of {@code registryKey} has been registered (datapack entries
 * plus any {@link DatapackRegistryBuilder#addBootstrap(ResourceKey, java.util.function.Consumer)}
 * entries), and returns immediately when that registry is not part of the load that is currently
 * running - so it costs nothing outside of datapack loading.
 * <p>
 * Only declare dependencies that are actually acyclic. A registry must never wait for one that
 * (directly or indirectly) waits for it.
 */
public class DatapackRegistryLoadOrder {
    private DatapackRegistryLoadOrder() {
    }

    /**
     * Blocks until the given registry has registered all of its elements in the datapack load that
     * is currently running.
     * <p>
     * Returns immediately if the registry is not part of the running load, if it has already
     * finished, or if no datapack load is running at all.
     *
     * @param registryKey The registry to wait for.
     */
    public static void awaitElements(ResourceKey<? extends Registry<?>> registryKey) {
        DatapackRegistryLoadOrderImpl.awaitElements(registryKey);
    }
}
