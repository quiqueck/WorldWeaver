package org.betterx.wover.structure.api.processors;

import org.betterx.wover.structure.impl.processors.StructureProcessorBuilderImpl;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import org.jetbrains.annotations.NotNull;

/**
 * A wrapper around {@link ResourceKey} that identifies a {@link StructureProcessorList}. Create one with
 * {@link StructureProcessorManager#createKey(ResourceLocation)} (or the
 * {@link org.betterx.wover.structure.api.StructureKeys#processor(ResourceLocation)} alias), then call
 * {@link #bootstrap(BootstrapContext)} to start building the processor list.
 */
public class StructureProcessorKey {
    /**
     * The key for the {@link StructureProcessorList} you can use to reference it.
     */
    @NotNull
    public final ResourceKey<StructureProcessorList> key;

    /**
     * Creates a {@link StructureProcessorBuilder} to build and register the {@link StructureProcessorList}
     * for this key with the given {@link BootstrapContext}.
     *
     * @param context The bootstrap context to register the processor list with
     * @return The builder
     */
    public StructureProcessorBuilder bootstrap(@NotNull BootstrapContext<StructureProcessorList> context) {
        return new StructureProcessorBuilderImpl(key, context);
    }

    StructureProcessorKey(@NotNull ResourceLocation location) {
        this.key = ResourceKey.create(Registries.PROCESSOR_LIST, location);
    }
}
