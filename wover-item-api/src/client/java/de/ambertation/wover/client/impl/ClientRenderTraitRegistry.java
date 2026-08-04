package de.ambertation.wover.client.impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;

/**
 * Client-side registry of render-trait appliers. Each applier walks the block/item registries once and performs
 * the real Fabric render registration ({@code BlockRenderLayerMap} / {@code EntityRendererRegistry}) for blocks
 * or items carrying a render binding. Appliers are collected from the {@code wover.client.traits} entrypoint
 * (so third-party mods can add their own) and run once at client init via {@link #applyAll()}, after
 * {@code ClientTraitBootstrap.ensureRegistered()} has collected every registration.
 */
@Environment(EnvType.CLIENT)
@ApiStatus.Internal
public final class ClientRenderTraitRegistry {
    private static final List<Runnable> APPLIERS = new ArrayList<>();
    private static boolean applied = false;

    private ClientRenderTraitRegistry() {
    }

    /**
     * Registers an applier to be run once at client init.
     *
     * @param applier walks the registries and performs its render registration
     */
    public static void register(Runnable applier) {
        APPLIERS.add(applier);
    }

    /**
     * Runs every registered applier exactly once (idempotent). Call at client init after every
     * {@code wover.client.traits} entrypoint has been processed.
     */
    public static synchronized void applyAll() {
        if (applied) return;
        applied = true;
        for (Runnable applier : APPLIERS) {
            applier.run();
        }
    }
}
