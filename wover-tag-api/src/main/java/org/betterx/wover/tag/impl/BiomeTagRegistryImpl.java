package org.betterx.wover.tag.impl;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.tag.api.BiomeTagRegistry;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;

public class BiomeTagRegistryImpl extends TagRegistryImpl<Biome, TagBootstrapContext<Biome>> implements BiomeTagRegistry {
    BiomeTagRegistryImpl(String directory, LocationProvider<Biome> locationProvider) {
        super(Registries.BIOME, directory, locationProvider);
    }

    @Override
    public TagKey<Biome> makeStructureTag(ModCore mod, String name) {
        return makeTag(mod, "has_structure/" + name);
    }

    @Override
    public TagKey<Biome> makeStructureTag(ResourceKey<Structure> baseStructure) {
        return makeTag(new ResourceLocation(
                baseStructure.location().getNamespace(),
                "has_structure/" + (baseStructure.location().getPath())
        ));
    }

    @Override
    public TagBootstrapContext<Biome> createBootstrapContext(boolean initAll) {
        return new TagBootstrapContextImpl<>(this, initAll);
    }

//    public void apply(Map<ResourceLocation, List<TagLoader.EntryWithSource>> tagsMap) {
//        super.apply(tagsMap);
//    }
}
