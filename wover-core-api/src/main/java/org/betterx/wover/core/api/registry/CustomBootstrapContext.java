package org.betterx.wover.core.api.registry;

import org.betterx.wover.core.impl.registry.CustomBootstrapContextImpl;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CustomBootstrapContext<T, C extends CustomBootstrapContext<T, C>> {
    protected BootstapContext<?> lookupContext;

    @ApiStatus.Internal
    public final void setLookupContext(BootstapContext<?> lookupContext) {
        this.lookupContext = lookupContext;
    }

    public <S> @Nullable HolderGetter<S> lookup(@NotNull ResourceKey<? extends Registry<? extends S>> registryKey) {
        if (lookupContext == null) return null;
        return lookupContext.lookup(registryKey);
    }

    public abstract void onBootstrapContextChange(C bootstrapContext);

    public static <B, T, C extends CustomBootstrapContext<T, C>> @Nullable C initContext(
            @Nullable BootstapContext<B> lookupContext,
            @NotNull ResourceKey<Registry<T>> registryKey,
            @NotNull Supplier<C> contextSupplier
    ) {
        return CustomBootstrapContextImpl.initContext(lookupContext, registryKey, contextSupplier);
    }

    public static <T> void invalidateContext(@NotNull ResourceKey<Registry<T>> registryKey) {
        CustomBootstrapContextImpl.finalize(registryKey);
    }
}
