package org.betterx.wover.structure.api;

import org.betterx.wover.structure.api.builders.BaseStructureBuilder;
import org.betterx.wover.tag.api.TagManager;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import org.jetbrains.annotations.ApiStatus;
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
public abstract class StructureKey<S extends Structure, T extends BaseStructureBuilder<S, T>> {
    /**
     * The key for the {@link Structure} you can use to reference it.
     */
    @NotNull
    public final ResourceKey<Structure> key;

    @Nullable
    private TagKey<Biome> biomeTag;

    @NotNull
    private GenerationStep.Decoration decoration;

    /**
     * Define a BiomeTag that will be used to determine if the structure can spawn in a biome. You can
     * build a new Structure tag by using {@link TagManager#BIOMES#makeStructureTag(ResourceLocation)}.
     *
     * @param biomeTag The biome tag to set
     * @return The same instance for chaining
     */
    public StructureKey<S, T> setBiomeTag(@Nullable TagKey<Biome> biomeTag) {
        this.biomeTag = biomeTag;
        return this;
    }

    /**
     * Returns the BiomeTag that was set for this structure. If no tag was set, it will return
     * a new tag that is created from the structure key.
     *
     * @return The biome tag
     */
    @NotNull
    public TagKey<Biome> getBiomeTag() {
        if (biomeTag == null) {
            biomeTag = TagManager.BIOMES.makeStructureTag(key);
        }

        return biomeTag;
    }

    /**
     * Get the {@link GenerationStep.Decoration} for the {@link PlacedFeature}.
     *
     * @return The decoration
     */
    public GenerationStep.@NotNull Decoration getDecoration() {
        return decoration;
    }

    /**
     * Sets the {@link GenerationStep.Decoration} for the {@link Structure}.
     *
     * @param decoration The decoration to set
     * @return This {@link StructureKey} for chaining
     */
    public StructureKey<S, T> setDecoration(GenerationStep.Decoration decoration) {
        this.decoration = decoration;
        return this;
    }

    /**
     * For internal use only. Use {@link StructureManager} instead.
     * <p>
     * Creates a new {@link StructureKey} with the given {@link ResourceLocation}.
     *
     * @param structureId The structure id
     */
    @ApiStatus.Internal
    protected StructureKey(@NotNull ResourceLocation structureId) {
        this.key = ResourceKey.create(Registries.STRUCTURE, structureId);
        this.decoration = GenerationStep.Decoration.SURFACE_STRUCTURES;
    }

    /**
     * The {@link StructureType} for this {@link Structure}. The type is primarily needed internally
     * to register the structure. If you implement a new Structure class (that inherits from {@link Structure},
     * you will probably want to return this type from {@link Structure#type()}.
     */
    public abstract StructureType<S> type();

    /**
     * Creates a structure builder for the Type. You may use the builder to register the structure in a
     * {@link BootstapContext} or create an inline Holder for the Structure.
     *
     * @param context The bootstrap context
     * @return The builder
     */
    public abstract T bootstrap(BootstapContext<Structure> context);
}
