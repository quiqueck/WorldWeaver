package de.ambertation.wover.client.api;

import de.ambertation.wover.entrypoint.LibWoverItem;

import net.fabricmc.loader.api.FabricLoader;

import org.jetbrains.annotations.ApiStatus;

/**
 * Loader-neutral holder that discovers and invokes every {@code wover.client.traits}
 * {@link WoverClientTraitEntrypoint} exactly once, collecting all client-trait registrations before wover walks
 * blocks/items. This is the trait-side analogue of wover's {@code wover.datapack.registry} bootstrap.
 * <p>
 * {@link #ensureRegistered()} is called from wover's client entrypoints (client launch) and from the datagen
 * model walk (datagen launch), so registrations are present wherever wover consumes them regardless of which
 * launch is active. Only the entrypoint <em>discovery</em> lives here (in common code); the actual client
 * registries the entrypoints populate live in the client source set.
 */
@ApiStatus.Internal
public final class ClientTraitBootstrap {
    private static boolean loaded = false;

    private ClientTraitBootstrap() {
    }

    /**
     * Invokes every {@code wover.client.traits} entrypoint once (collect-all-then-apply). Idempotent: subsequent
     * calls are no-ops. Safe to call from either the client or the datagen launch.
     */
    public static synchronized void ensureRegistered() {
        if (loaded) return;
        loaded = true;

        LibWoverItem.C.LOG.verbose("Processing wover.client.traits Entrypoints");
        FabricLoader.getInstance().getEntrypoints("wover.client.traits", WoverClientTraitEntrypoint.class)
                    .forEach(entrypoint -> {
                        LibWoverItem.C.LOG.verbose("    - Processing Entrypoint: {}", entrypoint.getClass().getName());
                        entrypoint.registerClientTraits();
                    });
    }
}
