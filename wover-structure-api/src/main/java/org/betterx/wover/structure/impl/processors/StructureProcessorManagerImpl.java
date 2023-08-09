package org.betterx.wover.structure.impl.processors;

import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;
import org.betterx.wover.events.impl.EventImpl;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.Optional;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StructureProcessorManagerImpl {
    public static final EventImpl<OnBootstrapRegistry<StructureProcessorList>> BOOTSTRAP_STRUCTURE_PROCESSORS =
            new EventImpl<>("BOOTSTRAP_STRUCTURE_PROCESSORS");

    @Nullable
    public static Holder<StructureProcessorList> getHolder(
            @Nullable HolderGetter<StructureProcessorList> getter,
            @NotNull ResourceKey<StructureProcessorList> key
    ) {
        if (getter == null) return null;

        final Optional<Holder.Reference<StructureProcessorList>> h = getter.get(key);
        return h.orElse(null);
    }

    @ApiStatus.Internal
    public static void initialize() {
        DatapackRegistryBuilder.addBootstrap(
                Registries.PROCESSOR_LIST,
                StructureProcessorManagerImpl::onBootstrap
        );
    }

    private static void onBootstrap(BootstapContext<StructureProcessorList> context) {
        BOOTSTRAP_STRUCTURE_PROCESSORS.emit(c -> c.bootstrap(context));
    }
}
