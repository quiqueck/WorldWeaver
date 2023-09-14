package org.betterx.wover.structure.impl;

import org.betterx.wover.structure.api.StructureKey;
import org.betterx.wover.structure.api.StructureManager;
import org.betterx.wover.structure.api.builders.BaseStructureBuilder;
import org.betterx.wover.tag.api.TagManager;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.Structure;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public abstract class StructureKeyImpl<
        S extends Structure,
        T extends BaseStructureBuilder<S, T>,
        R extends StructureKey<S, T, R>>
        implements StructureKey<S, T, R> {
    /**
     * The key for the {@link Structure} you can use to reference it.
     */
    @NotNull
    public final ResourceKey<Structure> key;

    @Nullable
    private TagKey<Biome> biomeTag;

    @NotNull
    private GenerationStep.Decoration decoration;


    @Override
    public final ResourceKey<Structure> key() {
        return this.key;
    }

    @Override
    public R setBiomeTag(@Nullable TagKey<Biome> biomeTag) {
        this.biomeTag = biomeTag;
        return (R) this;
    }


    @Override
    @NotNull
    public TagKey<Biome> biomeTag() {
        if (biomeTag == null) {
            biomeTag = TagManager.BIOMES.makeStructureTag(key);
        }

        return biomeTag;
    }


    @Override
    public GenerationStep.@NotNull Decoration step() {
        return decoration;
    }


    @Override
    public R step(GenerationStep.Decoration decoration) {
        this.decoration = decoration;
        return (R) this;
    }

    /**
     * For internal use only. Use {@link StructureManager} instead.
     * <p>
     * Creates a new {@link StructureKey} with the given {@link ResourceLocation}.
     *
     * @param structureId The structure id
     */
    @ApiStatus.Internal
    protected StructureKeyImpl(@NotNull ResourceLocation structureId) {
        this.key = ResourceKey.create(Registries.STRUCTURE, structureId);
        this.decoration = GenerationStep.Decoration.SURFACE_STRUCTURES;
    }

}
