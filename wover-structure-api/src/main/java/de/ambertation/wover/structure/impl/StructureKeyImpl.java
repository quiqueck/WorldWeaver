package de.ambertation.wover.structure.impl;

import de.ambertation.wover.structure.api.StructureKey;
import de.ambertation.wover.structure.api.StructureManager;
import de.ambertation.wover.structure.api.builders.BaseStructureBuilder;
import de.ambertation.wover.tag.api.TagManager;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
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

    @Nullable
    public Holder<Structure> getHolder(@Nullable HolderGetter<Structure> getter) {
        return StructureManagerImpl.getHolder(getter, key);
    }

    @Nullable
    public Holder<Structure> getHolder(@Nullable HolderLookup.Provider lookup) {
        return StructureManagerImpl.getHolder(lookup, key);
    }


    @Nullable
    public Holder<Structure> getHolder(@NotNull BootstrapContext<?> context) {
        return getHolder(context.lookup(Registries.STRUCTURE));
    }

    @Nullable
    public Holder<Structure> getHolder(@NotNull RegistryAccess access) {
        return getHolder(access.lookupOrThrow(Registries.STRUCTURE));
    }

    @Override
    public R biomeTag(@Nullable TagKey<Biome> biomeTag) {
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
