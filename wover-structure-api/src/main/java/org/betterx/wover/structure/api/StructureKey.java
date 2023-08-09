package org.betterx.wover.structure.api;

import org.betterx.wover.structure.api.builders.StructureBuilder;
import org.betterx.wover.tag.api.TagManager;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class StructureKey<T extends StructureBuilder, K extends StructureKey<T, K>> {
    /**
     * The key for the {@link Structure} you can use to reference it.
     */
    @NotNull
    public final ResourceKey<Structure> key;
    @Nullable
    protected TagKey<Biome> biomeTag;

    public abstract T bootstrap(BootstapContext<Structure> context);

    public K setBiomeTag(@Nullable TagKey<Biome> biomeTag) {
        this.biomeTag = biomeTag;
        return (K) this;
    }

    @NotNull
    public TagKey<Biome> getBiomeTag() {
        if (biomeTag == null) {
            biomeTag = TagManager.BIOMES.makeStructureTag(key);
        }

        return biomeTag;

    }

    StructureKey(@NotNull ResourceKey<Structure> key) {
        this.key = key;
    }
}
