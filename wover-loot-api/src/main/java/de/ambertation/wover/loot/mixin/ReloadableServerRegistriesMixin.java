package de.ambertation.wover.loot.mixin;

import de.ambertation.wover.loot.impl.LootAdditionsImpl;
import de.ambertation.wover.loot.impl.LootTableAppendersImpl;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
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
 * 26.3 loads loot tables as a datapack registry: {@code ReloadableServerRegistries.reload(...)} hands
 * {@code RegistryDataLoader.RELOADABLE_REGISTRIES} to the loader and then calls
 * {@code createAndValidateFullContext(layers, lookupWithUpdatedTags, loaded)}, which concatenates the two
 * lookups and runs {@code validateLootRegistries} on the result. Injecting at the {@code HEAD} of that method
 * is the last moment at which the freshly loaded {@code minecraft:loot_table} registry is reachable and
 * nothing has looked at it yet - in particular, additions made here are still validated by vanilla, so a
 * malformed pool fails at load time with a proper problem report rather than silently at loot-roll time.
 * <p>
 * The registry itself is frozen by then, which is fine: nothing is added to or removed from it, only the
 * {@link net.minecraft.world.level.storage.loot.LootTable} objects it holds are extended in place - and those
 * objects are re-created from the datapacks on every reload, which is what makes the whole mechanism
 * idempotent (see {@link LootTableAppendersImpl}).
 * <p>
 * Verified against 26.3-snapshot-5:
 * {@code private static ReloadableServerRegistries$LoadResult createAndValidateFullContext(
 * LayeredRegistryAccess, HolderLookup$Provider, RegistryAccess$Frozen)} - the only method of that name, and
 * {@code public static CompletableFuture<ReloadableServerRegistries$LoadResult> reload(LayeredRegistryAccess,
 * List, ResourceManager, Executor)} - likewise the only one.
 */
@Mixin(ReloadableServerRegistries.class)
public class ReloadableServerRegistriesMixin {
    /**
     * The {@link ResourceManager} the datapack additions have to be read from is only reachable here:
     * {@code reload} takes it, hands it to {@code RegistryDataLoader.load(...)} and never passes it on.
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
            LayeredRegistryAccess<RegistryLayer> layers,
            HolderLookup.Provider lookupWithUpdatedTags,
            RegistryAccess.Frozen loaded,
            CallbackInfoReturnable<ReloadableServerRegistries.LoadResult> cir
    ) {
        // Decoding has to happen here rather than at reload() HEAD: an addition may reference the loot tables
        // and predicates of this very reload, which do not exist until `loaded` does.
        LootAdditionsImpl.parse(layers, lookupWithUpdatedTags, loaded);
        LootTableAppendersImpl.applyAll(lookupWithUpdatedTags, loaded);
    }
}
