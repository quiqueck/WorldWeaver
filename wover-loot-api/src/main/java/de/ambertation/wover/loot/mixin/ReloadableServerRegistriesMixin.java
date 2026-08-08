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
 * Verified against 1.21.8: {@code private static ReloadableServerRegistries$LoadResult
 * createAndValidateFullContext(LayeredRegistryAccess, HolderLookup$Provider, List)} - the only method of that
 * name - and {@code public static CompletableFuture<LoadResult> reload(LayeredRegistryAccess,
 * List<Registry$PendingTags<?>>, ResourceManager, Executor)}. Both signatures are byte-for-byte the ones the
 * 26.x branches inject into, which is why this module's plumbing is the same on both.
 *
 * <h2>1.21.x divergence: why this is not built on {@code fabric-loot-api-v3}</h2>
 * Unlike 26.3, this branch <em>does</em> still ship {@code fabric-loot-api-v3}, so
 * {@code LootTableEvents.MODIFY} is available and would be the obvious substrate. It is deliberately not used,
 * because it cannot carry three things this module's public API promises:
 * <ul>
 *     <li>{@link LootAdditionsImpl} needs the {@link net.minecraft.server.packs.resources.ResourceManager} of
 *     the reload in flight to read {@code data/*&#47;wover/loot_addition/}. {@code MODIFY} never sees one -
 *     Fabric fires it from {@code JsonDataLoaderMixin}, per already-decoded table.</li>
 *     <li>{@link de.ambertation.wover.loot.api.MutableLootTable#lootTable(net.minecraft.resources.ResourceKey)}
 *     resolves against the {@code loot_table} registry <em>mid-assembly</em>. {@code MODIFY} runs before that
 *     registry exists.</li>
 *     <li>{@link de.ambertation.wover.loot.api.MutableLootTable#addConditionToPool(int, net.minecraft.world.level.storage.loot.predicates.LootItemCondition)}
 *     addresses a pool by its index in the finished table. {@code FabricLootTableBuilder.modifyPools} hands
 *     out builders without a stable index, and its builder view cannot read back the pools a datapack
 *     provided.</li>
 * </ul>
 * Building on {@code MODIFY} would therefore mean weakening the API on this branch only - exactly the
 * cross-branch drift this module exists to remove. Sharing vanilla's own hook instead keeps the source
 * interface identical to 26.1/26.2/26.3.
 * <p>
 * The two do not collide. Fabric applies {@code MODIFY} while each loot table's JSON is decoded
 * ({@code JsonDataLoaderMixin}), strictly before {@code createAndValidateFullContext} runs, so a table this
 * class sees already carries whatever {@code MODIFY} listeners contributed - and that state is what
 * {@code LootTableAppendersImpl.ORIGINAL_POOLS} snapshots as "original". Fabric's own {@code LootPoolAccessor}
 * / {@code LootTableAccessor} mixins target the same fields from a different package, which Mixin allows.
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
