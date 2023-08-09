package org.betterx.wover.structure.impl.processors;

import org.betterx.wover.structure.api.processors.StructureProcessorBuilder;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.LinkedList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class StructureProcessorBuilderImpl implements StructureProcessorBuilder {
    @NotNull
    private final ResourceKey<StructureProcessorList> key;
    @NotNull
    private final BootstapContext<StructureProcessorList> context;

    private final List<StructureProcessor> list = new LinkedList<>();

    public StructureProcessorBuilderImpl(
            @NotNull ResourceKey<StructureProcessorList> key,
            @NotNull BootstapContext<StructureProcessorList> context
    ) {
        this.key = key;
        this.context = context;
    }

    @Override
    public StructureProcessorBuilder add(@NotNull StructureProcessor processor) {
        list.add(processor);
        return this;
    }

    @Override
    public Holder<StructureProcessorList> register() {
        return context.register(key, build());
    }

    @Override
    public Holder<StructureProcessorList> directHolder() {
        return Holder.direct(build());
    }


    private StructureProcessorList build() {
        return new StructureProcessorList(list);
    }
}
