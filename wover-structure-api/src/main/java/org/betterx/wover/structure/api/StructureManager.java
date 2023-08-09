package org.betterx.wover.structure.api;

import org.betterx.wover.events.api.Event;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;
import org.betterx.wover.structure.api.builders.JigsawBuilder;
import org.betterx.wover.structure.api.builders.StructureBuilder;
import org.betterx.wover.structure.impl.JigsawKeyImpl;
import org.betterx.wover.structure.impl.StructureKeyImpl;
import org.betterx.wover.structure.impl.StructureManagerImpl;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StructureManager {
    private StructureManager() {
    }

    /**
     * The event that is fired when the Registry for a {@link StructureSet}
     * is being bootstrapped. In general, it is best to generate presets
     * in the data generator whenever possible (see WoverRegistryProvider)
     * for Details.
     */
    public static final Event<OnBootstrapRegistry<Structure>> BOOTSTRAP_STRUCTURES =
            StructureManagerImpl.BOOTSTRAP_STRUCTURES;

    /**
     * Creates a {@link StructureKey} for the given {@link ResourceLocation}.
     *
     * @param location The location of the {@link Structure}
     * @return The {@link StructureKey}
     */
    public static <S extends Structure> StructureKey<S, StructureBuilder<S>> structure(
            ResourceLocation location,
            @NotNull StructureTypeKey<S> type
    ) {
        return new StructureKeyImpl<>(location, type);
    }

    /**
     * Creates a {@link StructureKey} for the given {@link ResourceLocation}.
     *
     * @param location The location of the {@link Structure}
     * @return The {@link StructureKey}
     */
    public static <S extends Structure> StructureKey<S, StructureBuilder<S>> structure(
            ResourceLocation location,
            @NotNull StructureTypeKey.StructureFactory<S> structureFactory,
            @NotNull Codec<S> codec
    ) {
        return new StructureKeyImpl<>(location, registerType(location, structureFactory, codec));
    }

    /**
     * Creates a {@link StructureKey} for the given {@link ResourceLocation}.
     *
     * @param location The location of the {@link Structure}
     * @return The {@link StructureKey}
     */
    public static <S extends Structure> StructureKey<JigsawStructure, JigsawBuilder> jigsaw(ResourceLocation location) {
        return new JigsawKeyImpl(location);
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
    public static Holder<Structure> getHolder(
            @Nullable HolderGetter<Structure> getter,
            @NotNull ResourceKey<Structure> key
    ) {
        return StructureManagerImpl.getHolder(getter, key);
    }

    /**
     * Gets the {@link Holder} for a {@link Structure} from a {@link BootstapContext}.
     *
     * @param context the context to get registry containing the holder. When you need to
     *                get multiple holders at a time, you might want to use
     *                {@link #getHolder(HolderGetter, ResourceKey)} instead, as it will
     *                be slightly faster.
     * @param key     the key to get the holder for
     * @return the holder, or null if the holder is not present
     */
    @Nullable
    public static Holder<Structure> getHolder(
            @Nullable BootstapContext<?> context,
            @NotNull ResourceKey<Structure> key
    ) {
        return StructureManagerImpl.getHolder(context.lookup(Registries.STRUCTURE), key);
    }

    public static <S extends Structure> StructureTypeKey<S> registerType(
            @NotNull ResourceLocation location,
            @NotNull StructureTypeKey.StructureFactory<S> structureFactory,
            @NotNull Codec<S> codec
    ) {
        return StructureManagerImpl.registerType(location, structureFactory, codec);
    }
}
