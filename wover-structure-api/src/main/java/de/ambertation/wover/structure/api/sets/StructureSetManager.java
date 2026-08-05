package de.ambertation.wover.structure.api.sets;

import de.ambertation.wover.events.api.Event;
import de.ambertation.wover.events.api.types.OnBootstrapRegistry;
import de.ambertation.wover.structure.api.StructureKey;
import de.ambertation.wover.structure.api.builders.BaseStructureBuilder;
import de.ambertation.wover.structure.impl.sets.StructureSetManagerImpl;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
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
     * Creates a {@link StructureSetKey} for the given {@link Identifier}.
     *
     * @param location The location of the {@link StructureSet}
     * @return The {@link StructureSetKey}
     */
    public static StructureSetKey createKey(
            Identifier location
    ) {
        return new StructureSetKey(location);
    }


    /**
     * Creates a {@link StructureSetKey} for the given {@link StructureKey}.
     *
     * @param structure The {@link StructureKey} to create the {@link StructureSetKey} for
     * @return The {@link StructureSetKey}
     */
    public static StructureSetKey createKey(
            StructureKey<?, ?, ?> structure
    ) {
        return createKey(structure.key().identifier());
    }

    /**
     * Creates a {@link StructureSetKey} for the given {@link Structure} and starts
     * the bootstrap process by adding the {@link Structure} to the {@link StructureSet}.
     * <p>
     * The ID of the set will be the same as the ID of the passed structure. This call is a
     * shortcut for {@code createKey(structure).bootstrap(context).addStructure(structure)}.
     *
     * @param structure The {@link Structure} to create the {@link StructureSetKey} for
     * @param context   The {@link BootstrapContext} to bootstrap the {@link StructureSet} with
     * @param <S>       The {@link Structure} type
     * @param <T>       The Builder Ttype
     * @param <R>       The {@link StructureKey} type
     * @return The {@link StructureSetBuilder} for the newly created set.
     */
    public static <
            S extends Structure,
            T extends BaseStructureBuilder<S, T>,
            R extends StructureKey<S, T, R>
            > StructureSetBuilder bootstrap(
            R structure,
            BootstrapContext<StructureSet> context
    ) {
        return createKey(structure).bootstrap(context).addStructure(structure);
    }

    /**
     * Gets the {@link Holder} for a {@link Structure} from a {@link HolderGetter}.
     *
     * @param getter the getter to get the holder from. You can get this getter from a
     *               {@link net.minecraft.data.worldgen.BootstrapContext} {@code ctx} by
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
     * Gets the {@link Holder} for a {@link StructureSet} from a {@link BootstrapContext}.
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
            @Nullable BootstrapContext<?> context,
            @NotNull ResourceKey<StructureSet> key
    ) {
        return StructureSetManagerImpl.getHolder(context.lookup(Registries.STRUCTURE_SET), key);
    }
}
