package de.ambertation.wover.core.api.registry;

import de.ambertation.wover.core.impl.registry.CustomBootstrapContextImpl;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for a custom, cached context object that is passed around while bootstrapping a datapack registry.
 * <p>
 * Unlike a plain {@link BootstrapContext}, a {@code CustomBootstrapContext} instance is cached and reused between
 * bootstrap calls for the same registry as long as the underlying {@link HolderGetter} did not change. This makes
 * it a good place to store additional state (for example convenience accessors or caches) that would otherwise
 * have to be recomputed on every bootstrap call. Use {@link #initContext} to obtain a (possibly cached) instance
 * and {@link #invalidateContext} to force a fresh instance to be created on the next call.
 *
 * @param <T> The type of the registry-elements the context is used for.
 * @param <C> The concrete subclass, so {@link #onBootstrapContextChange(CustomBootstrapContext)} can pass back a
 *            correctly typed instance.
 */
public abstract class CustomBootstrapContext<T, C extends CustomBootstrapContext<T, C>> implements LookupProvider {
    /**
     * The current lookup context, used to resolve {@link net.minecraft.core.Holder}s for other registries.
     * Set by {@link #initContext} whenever the context is (re-)used.
     */
    protected BootstrapContext<?> lookupContext;

    /**
     * Sets the {@link #lookupContext} that is used to resolve elements from other registries.
     *
     * @param lookupContext The new lookup context.
     */
    @ApiStatus.Internal
    public final void setLookupContext(BootstrapContext<?> lookupContext) {
        this.lookupContext = lookupContext;
    }

    @Override
    public <S> @Nullable HolderGetter<S> lookup(@NotNull ResourceKey<? extends Registry<? extends S>> registryKey) {
        if (lookupContext == null) return null;
        return lookupContext.lookup(registryKey);
    }

    /**
     * Called whenever a new (non-cached) context instance was created, for example because the underlying
     * {@link HolderGetter} changed. Implementations should use this to (re-)initialize any state that is derived
     * from the lookup context.
     *
     * @param bootstrapContext The newly created context instance (identical to {@code this}).
     */
    public abstract void onBootstrapContextChange(C bootstrapContext);

    /**
     * Returns a (possibly cached) context instance for the given registry.
     * <p>
     * If the {@link HolderGetter} for {@code registryKey} did not change since the last call for the same
     * registry, the previously created instance is returned. Otherwise, a new instance is created with
     * {@code contextSupplier} and {@link #onBootstrapContextChange(CustomBootstrapContext)} is called on it.
     *
     * @param lookupContext   The current {@link BootstrapContext}, or {@code null} to just return the cached
     *                        instance (if any) without updating it.
     * @param registryKey     The key of the registry the context is used for.
     * @param contextSupplier A factory used to create a new context instance when needed.
     * @param <B>             The type of the elements of the registry that is currently being bootstrapped.
     * @param <T>             The type of the elements of the registry the context is used for.
     * @param <C>             The concrete type of the context.
     * @return The (possibly cached) context instance, or {@code null} if none exists yet and {@code lookupContext}
     * was {@code null}.
     */
    public static <B, T, C extends CustomBootstrapContext<T, C>> @Nullable C initContext(
            @Nullable BootstrapContext<B> lookupContext,
            @NotNull ResourceKey<Registry<T>> registryKey,
            @NotNull Supplier<C> contextSupplier
    ) {
        return CustomBootstrapContextImpl.initContext(lookupContext, registryKey, contextSupplier);
    }

    /**
     * Discards the cached context instance for the given registry, forcing a new instance to be created on the
     * next call to {@link #initContext}.
     * <p>
     * This is rarely needed: {@link #initContext} already drops the cached instance whenever the
     * {@link HolderGetter} changes, which is the normal per-datapack-load boundary. Never call this from a
     * phase that can run more than once for the same datapack load (tag loading, for example, re-runs on every
     * {@code /reload}) - any state the context accumulated is gone afterwards, and a phase that has no
     * {@link BootstrapContext} of its own cannot rebuild it.
     *
     * @param registryKey The key of the registry whose cached context should be invalidated.
     * @param <T>         The type of the elements of the registry.
     */
    public static <T> void invalidateContext(@NotNull ResourceKey<Registry<T>> registryKey) {
        CustomBootstrapContextImpl.finalize(registryKey);
    }
}
