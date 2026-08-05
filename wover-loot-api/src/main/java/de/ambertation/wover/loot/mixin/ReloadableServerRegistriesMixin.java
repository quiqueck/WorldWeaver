package de.ambertation.wover.loot.mixin;

import de.ambertation.wover.loot.impl.LootAdditionsImpl;
import de.ambertation.wover.loot.impl.LootTableAppendersImpl;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.packs.resources.ResourceManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Applies every {@link de.ambertation.wover.loot.api.LootTableAppender} while the reloadable registries are
 * being assembled.
 * <p>
 * {@code ReloadableServerRegistries.reload(...)} loads the loot data types into a fresh
 * {@code List<WritableRegistry<?>>} and then calls
 * {@code createAndValidateFullContext(context, loadingContextWithTags, newlyLoadedRegistries)}, which wraps
 * that list into the layered access and runs {@code validateLootRegistries} on the result. Injecting at the
 * {@code HEAD} of that method is the last moment at which the freshly loaded registries are reachable and
 * nothing has looked at them yet - in particular, additions made here are still validated by vanilla, so a
 * malformed pool fails at load time with a proper problem report rather than silently at loot-roll time.
 * <p>
 * {@code WritableRegistry<T> extends Registry<T> extends HolderLookup.RegistryLookup<T>}, so the raw list can
 * be wrapped into a {@link HolderLookup.Provider} the same way {@code createAndValidateFullContext} itself
 * wraps {@code contextLookupWithUpdatedTags} two lines later - {@link LootTableAppendersImpl} and
 * {@link LootAdditionsImpl} only ever need that Provider view, never the list itself.
 * <p>
 * Nothing is added to or removed from the registries here, only the
 * {@link net.minecraft.world.level.storage.loot.LootTable} objects one of them holds are extended in place -
 * and those objects are re-created from the datapacks on every reload, which is what makes the whole mechanism
 * idempotent (see {@link LootTableAppendersImpl}).
 * <p>
 * Verified against 26.1.2: {@code private static ReloadableServerRegistries$LoadResult
 * createAndValidateFullContext(LayeredRegistryAccess, HolderLookup$Provider, List)} - the only method of that
 * name.
 */
@Mixin(ReloadableServerRegistries.class)
public class ReloadableServerRegistriesMixin {
    /**
     * The {@link ResourceManager} the datapack additions have to be read from is only reachable here:
     * {@code reload} takes it, hands it to the loader and never passes it on.
     * {@code createAndValidateFullContext} runs from that future's {@code thenApplyAsync}, i.e. on a different
     * thread, so it is parked keyed by the {@code layers} instance both methods receive rather than in a
     * thread local.
     */
    @Inject(method = "reload", at = @At("HEAD"))
    private static void wover_captureResourceManager(
            LayeredRegistryAccess<RegistryLayer> layers,
            List<Registry.PendingTags<?>> pendingTags,
            ResourceManager resourceManager,
            Executor executor,
            CallbackInfoReturnable<CompletableFuture<ReloadableServerRegistries.LoadResult>> cir
    ) {
        LootAdditionsImpl.beginReload(layers, resourceManager);
    }

    @Inject(method = "createAndValidateFullContext", at = @At("HEAD"))
    private static void wover_appendLootTables(
            LayeredRegistryAccess<RegistryLayer> contextLayers,
            HolderLookup.Provider contextLookupWithUpdatedTags,
            List<WritableRegistry<?>> newRegistries,
            CallbackInfoReturnable<ReloadableServerRegistries.LoadResult> cir
    ) {
        // Decoding has to happen here rather than at reload() HEAD: an addition may reference the loot tables
        // of this very reload, which do not exist until this Provider does.
        final HolderLookup.Provider loaded = HolderLookup.Provider.create(
                newRegistries.stream().map(r -> (HolderLookup.RegistryLookup<?>) r)
        );
        LootAdditionsImpl.parse(contextLayers, contextLookupWithUpdatedTags, loaded);
        LootTableAppendersImpl.applyAll(contextLookupWithUpdatedTags, loaded);
    }
}
