package de.ambertation.wover.core.mixin.registry;

import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.RegistryLoadTask;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

// Exposes the private WritableRegistry that RegistryLoadTask builds into, so
// ResourceManagerRegistryLoadTaskMixin can hand it to DatapackRegistryBuilderImpl.bootstrap(...).
@Mixin(RegistryLoadTask.class)
public interface RegistryLoadTaskAccessor {
    @Accessor("registry")
    WritableRegistry<?> wover_getRegistry();
}
