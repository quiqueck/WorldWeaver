package org.betterx.wover.structure.api.processors;

import org.betterx.wover.structure.impl.processors.StructureProcessorBuilderImpl;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import org.jetbrains.annotations.NotNull;

public class StructureProcessorKey {
    /**
     * The key for the {@link StructureProcessorList} you can use to reference it.
     */
    @NotNull
    public final ResourceKey<StructureProcessorList> key;

    public StructureProcessorBuilder bootstrap(@NotNull BootstapContext<StructureProcessorList> context) {
        return new StructureProcessorBuilderImpl(key, context);
    }

    StructureProcessorKey(@NotNull ResourceLocation location) {
        this.key = ResourceKey.create(Registries.PROCESSOR_LIST, location);
    }
}
