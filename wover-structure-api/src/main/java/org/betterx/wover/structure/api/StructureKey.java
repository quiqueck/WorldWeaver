package org.betterx.wover.structure.api;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.structure.api.builders.BaseStructureBuilder;
import org.betterx.wover.structure.api.builders.JigsawBuilder;
import org.betterx.wover.structure.api.builders.RandomNbtBuilder;
import org.betterx.wover.structure.api.builders.StructureBuilder;
import org.betterx.wover.structure.api.structures.nbt.RandomNbtStructure;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a {@link Structure} that can be registered. This is a primarily a wrapper around
 * {@link ResourceKey} to make it easier to register structures. But it also provides a function
 * ({@link #bootstrap(BootstrapContext)}) to build and register a structure.
 *
 * @param <S> The {@link Structure} type
 * @param <T> The {@link BaseStructureBuilder} type
 */
public interface StructureKey<
        S extends Structure,
        T extends BaseStructureBuilder<S, T>,
        R extends StructureKey<S, T, R>
        > {
    /**
     * A {@link StructureKey} for a plain {@link Structure} subclass that only needs the generic
     * {@link StructureBuilder} to be registered (i.e. it does not need any additional builder methods
     * beyond {@link BaseStructureBuilder}).
     *
     * @param <S> The {@link Structure} type
     */
    interface Simple<S extends Structure> extends StructureKey<S, StructureBuilder<S>, Simple<S>> {
    }

    /**
     * A {@link StructureKey} for a {@link JigsawStructure}, bootstrapped with a {@link JigsawBuilder}.
     */
    interface Jigsaw extends StructureKey<JigsawStructure, JigsawBuilder, Jigsaw> {
    }

    /**
     * A {@link StructureKey} for a {@link RandomNbtStructure}, bootstrapped with a {@link RandomNbtBuilder}.
     */
    interface RandomNbt extends StructureKey<RandomNbtStructure, RandomNbtBuilder, RandomNbt> {
    }


    /**
     * Returns the {@link ResourceKey} for the {@link Structure}.
     *
     * @return The key
     */
    ResourceKey<Structure> key();

    /**
     * Gets the {@link Holder} for the {@link Structure} from the given getter.
     *
     * @param getter The getter to get the holder from or {@code null}
     * @return The holder for the {@link Structure} or {@code null} if it is not present
     */
    @Nullable
    Holder<Structure> getHolder(@Nullable HolderGetter<Structure> getter);

    /**
     * Gets the {@link Holder} for the {@link Structure} from the given lookup. This method
     * needs to lookup the Structure Registry first, and may be inefficient if used frequently.
     * If you need to look up many holders, you may want to consider to get the Registry Lookup
     * first and use {@link #getHolder(HolderGetter)} instead.
     *
     * @param lookup The lookup to get the holder from
     * @return The holder for the {@link Structure} or {@code null} if it is not present
     */
    @Nullable
    Holder<Structure> getHolder(@Nullable HolderLookup.Provider lookup);

    /**
     * Gets the {@link Holder} for the {@link Structure} from the given getter.
     *
     * <p>
     * This method internally looks up {@link Registries#STRUCTURE}. If you need to retrieve
     * a lot of holders, it is recommended to manually lookup the
     * Registry first and use {@link #getHolder(HolderGetter)} instead.
     *
     * @param context The {@link BootstrapContext} to get the holder from
     * @return The holder for the {@link Structure} or {@code null} if it is not present
     */
    @Nullable
    Holder<Structure> getHolder(@NotNull BootstrapContext<?> context);

    /**
     * Gets the {@link Holder} for the {@link Structure} from the given getter.
     *
     * <p>
     * This method internally looks up {@link Registries#STRUCTURE}. If you need to retrieve
     * a lot of holders, it is recommended to manually lookup the
     * Registry first and use {@link #getHolder(HolderGetter)} instead.
     *
     * @param access The {@link RegistryAccess} to get the holder from
     * @return The holder for the {@link Structure} or {@code null} if it is not present
     */
    @Nullable
    Holder<Structure> getHolder(@NotNull RegistryAccess access);

    /**
     * Define a BiomeTag that will be used to determine if the structure can spawn in a biome. You can
     * build a new Structure tag by using {@link org.betterx.wover.tag.api.BiomeTagRegistry#makeStructureTag(ModCore, String)} (ModCore, String)}
     * from {@link org.betterx.wover.tag.api.TagManager#BIOMES}.
     *
     * @param biomeTag The biome tag to set
     * @return The same instance for chaining
     */
    R biomeTag(@Nullable TagKey<Biome> biomeTag);

    /**
     * Returns the BiomeTag that was set for this structure. If no tag was set, it will return
     * a new tag that is created from the structure key.
     *
     * @return The biome tag
     */
    @NotNull
    TagKey<Biome> biomeTag();

    /**
     * Get the {@link GenerationStep.Decoration} for the {@link Structure}.
     *
     * @return The decoration
     */
    GenerationStep.@NotNull Decoration step();

    /**
     * Sets the {@link GenerationStep.Decoration} for the {@link Structure}.
     *
     * @param decoration The decoration to set
     * @return This {@link StructureKey} for chaining
     */
    R step(GenerationStep.Decoration decoration);

    /**
     * The {@link StructureType} for this {@link Structure}. The type is primarily needed internally
     * to register the structure. If you implement a new Structure class (that inherits from {@link Structure},
     * you will probably want to return this type from {@link Structure#type()}.
     */
    StructureType<S> type();
    /**
     * Creates a structure builder for the Type. You may use the builder to register the structure in a
     * {@link BootstrapContext} or create an inline Holder for the Structure.
     *
     * @param context The bootstrap context
     * @return The builder
     */
    T bootstrap(BootstrapContext<Structure> context);
}
