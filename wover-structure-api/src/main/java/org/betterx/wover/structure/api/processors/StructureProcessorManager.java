package org.betterx.wover.structure.api.processors;

import org.betterx.wover.events.api.Event;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;
import org.betterx.wover.structure.api.pools.StructurePoolKey;
import org.betterx.wover.structure.api.sets.StructureSetKey;
import org.betterx.wover.structure.impl.processors.StructureProcessorManagerImpl;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StructureProcessorManager {
    private StructureProcessorManager() {
    }

    /**
     * The event that is fired when the Registry for a {@link StructureProcessorList}
     * is being bootstrapped. In general, it is best to generate presets
     * in the data generator whenever possible (see WoverRegistryProvider)
     * for Details.
     */
    public static final Event<OnBootstrapRegistry<StructureProcessorList>> BOOTSTRAP_STRUCTURE_PROCESSORS =
            StructureProcessorManagerImpl.BOOTSTRAP_STRUCTURE_PROCESSORS;

    /**
     * Creates a {@link StructurePoolKey} for the given {@link ResourceLocation}.
     *
     * @param location The location of the {@link StructureProcessorList}
     * @return The {@link StructureSetKey}
     */
    public static StructureProcessorKey createKey(
            ResourceLocation location
    ) {
        return new StructureProcessorKey(location);
    }

    /**
     * Gets the {@link Holder} for a {@link StructureProcessorList} from a {@link HolderGetter}.
     *
     * @param getter the getter to get the holder from. You can get this getter from a
     *               {@link net.minecraft.data.worldgen.BootstapContext} {@code ctx} by
     *               calling {@code ctx.lookup(Registries.STRUCTURE)}
     * @param key    the key to get the holder for
     * @return the holder, or null if the holder is not present
     */
    @Nullable
    public static Holder<StructureProcessorList> getHolder(
            @Nullable HolderGetter<StructureProcessorList> getter,
            @NotNull ResourceKey<StructureProcessorList> key
    ) {
        return StructureProcessorManagerImpl.getHolder(getter, key);
    }

    /**
     * Gets the {@link Holder} for a {@link StructureProcessorList} from a {@link BootstapContext}.
     *
     * @param context the context to get registry containing the holder. When you need to
     *                get multiple holders at a time, you might want to use
     *                {@link #getHolder(HolderGetter, ResourceKey)} instead, as it will
     *                be slightly faster.
     * @param key     the key to get the holder for
     * @return the holder, or null if the holder is not present
     */
    @Nullable
    public static Holder<StructureProcessorList> getHolder(
            @Nullable BootstapContext<?> context,
            @NotNull ResourceKey<StructureProcessorList> key
    ) {
        return StructureProcessorManagerImpl.getHolder(context.lookup(Registries.PROCESSOR_LIST), key);
    }
}
