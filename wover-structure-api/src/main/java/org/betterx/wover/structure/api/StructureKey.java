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

public abstract class StructureKey<S extends Structure, T extends BaseStructureBuilder<S, T>> {
    /**
     * The key for the {@link Structure} you can use to reference it.
     */
    @NotNull
    public final ResourceKey<Structure> key;

    @Nullable
    protected TagKey<Biome> biomeTag;
    
    @NotNull
    private GenerationStep.Decoration decoration;

    public StructureKey<S, T> setBiomeTag(@Nullable TagKey<Biome> biomeTag) {
        this.biomeTag = biomeTag;
        return this;
    }

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

    public abstract StructureType<S> type();

    public abstract T bootstrap(BootstapContext<Structure> context);
}
