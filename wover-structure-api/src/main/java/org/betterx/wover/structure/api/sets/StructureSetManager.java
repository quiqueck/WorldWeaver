package org.betterx.wover.structure.api.sets;

import org.betterx.wover.events.api.Event;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;
import org.betterx.wover.structure.impl.sets.StructureSetManagerImpl;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StructureSetManager {
    private StructureSetManager() {
    }

    /**
     * The event that is fired when the Registry for a {@link StructureSet}
     * is being bootstrapped. In general, it is best to generate presets
     * in the data generator whenever possible (see WoverRegistryProvider)
     * for Details.
     */
    public static final Event<OnBootstrapRegistry<StructureSet>> BOOTSTRAP_STRUCTURE_SETS =
            StructureSetManagerImpl.BOOTSTRAP_STRUCTURE_SETS;

    /**
     * Creates a {@link StructureSetKey} for the given {@link ResourceLocation}.
     *
     * @param location The location of the {@link StructureSet}
     * @return The {@link StructureSetKey}
     */
    public static StructureSetKey createKey(
            ResourceLocation location
    ) {
        return new StructureSetKey(location);
    }

    /**
     * Gets the {@link Holder} for a {@link Structure} from a {@link HolderGetter}.
     *
     * @param getter the getter to get the holder from. You can get this getter from a
     *               {@link net.minecraft.data.worldgen.BootstapContext} {@code ctx} by
     *               calling {@code ctx.lookup(Registries.STRUCTURE)}
     * @param key    the key to get the holder for
     * @return the holder, or null if the holder is not present
     */
    @Nullable
    public static Holder<StructureSet> getHolder(
            @Nullable HolderGetter<StructureSet> getter,
            @NotNull ResourceKey<StructureSet> key
    ) {
        return StructureSetManagerImpl.getHolder(getter, key);
    }

    /**
     * Gets the {@link Holder} for a {@link StructureSet} from a {@link BootstapContext}.
     *
     * @param context the context to get registry containing the holder. When you need to
     *                get multiple holders at a time, you might want to use
     *                {@link #getHolder(HolderGetter, ResourceKey)} instead, as it will
     *                be slightly faster.
     * @param key     the key to get the holder for
     * @return the holder, or null if the holder is not present
     */
    @Nullable
    public static Holder<StructureSet> getHolder(
            @Nullable BootstapContext<?> context,
            @NotNull ResourceKey<StructureSet> key
    ) {
        return StructureSetManagerImpl.getHolder(context.lookup(Registries.STRUCTURE_SET), key);
    }
}
