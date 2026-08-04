package de.ambertation.wover.core.api.registry;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceKey;

import java.util.function.Consumer;

/**
 * Entrypoint for registering datapack registries.
 * <p>
 * This is called from the <code>wover.datapack.registry</code> entrypoint. When called, you should register your
 * custom registries (using {@link DatapackRegistryBuilder#register(ResourceKey, Codec, Consumer, int)}) or add custom registry
 * bootstrap (using  {@link DatapackRegistryBuilder#addBootstrap(ResourceKey, Consumer, int)} or
 * {@link DatapackRegistryBuilder#addReadOnlyBootstrap(ResourceKey, Consumer, int)}
 * </p>
 * This is not intended to be used with vanilla registries!
 */
public interface DatapackRegistryEntrypoint {
    /**
     * Called when it is time to register custom registries of registry bootstrap code.
     */
    void registerDatapackRegistries();
}
