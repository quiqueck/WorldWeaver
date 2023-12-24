package org.betterx.wover.structure.api;

import org.betterx.wover.structure.api.builders.BaseStructureBuilder;
import org.betterx.wover.structure.api.builders.JigsawBuilder;
import org.betterx.wover.structure.api.builders.RandomNbtBuilder;
import org.betterx.wover.structure.api.builders.StructureBuilder;
import org.betterx.wover.structure.api.structures.nbt.RandomNbtStructure;

import net.minecraft.data.worldgen.BootstapContext;
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
 * ({@link #bootstrap(BootstapContext)}) to build and register a structure.
 *
 * @param <S> The {@link Structure} type
 * @param <T> The {@link BaseStructureBuilder} type
 */
public interface StructureKey<
        S extends Structure,
        T extends BaseStructureBuilder<S, T>,
        R extends StructureKey<S, T, R>
        > {
    interface Simple<S extends Structure> extends StructureKey<S, StructureBuilder<S>, Simple<S>> {
    }

    interface Jigsaw extends StructureKey<JigsawStructure, JigsawBuilder, Jigsaw> {
    }

    interface RandomNbt extends StructureKey<RandomNbtStructure, RandomNbtBuilder, RandomNbt> {
    }


    /**
     * Returns the {@link ResourceKey} for the {@link Structure}.
     *
     * @return The key
     */
    ResourceKey<Structure> key();

    /**
     * Define a BiomeTag that will be used to determine if the structure can spawn in a biome. You can
     * build a new Structure tag by using {@link org.betterx.wover.tag.api.BiomeTagRegistry#makeStructureTag(ModCore, String)}
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
     * {@link BootstapContext} or create an inline Holder for the Structure.
     *
     * @param context The bootstrap context
     * @return The builder
     */
    T bootstrap(BootstapContext<Structure> context);
}
