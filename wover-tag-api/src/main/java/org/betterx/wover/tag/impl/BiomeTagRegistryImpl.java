package org.betterx.wover.tag.impl;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.tag.api.BiomeTagRegistry;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public class BiomeTagRegistryImpl extends TagRegistryImpl<Biome, TagBootstrapContext<Biome>> implements BiomeTagRegistry {
    BiomeTagRegistryImpl(String directory, LocationProvider<Biome> locationProvider) {
        super(Registries.BIOME, directory, locationProvider);
    }

    public TagKey<Biome> makeStructureTag(ModCore mod, String name) {
        return makeTag(mod, "has_structure/" + name);
    }

    @Override
    public TagBootstrapContext<Biome> createBootstrapContext(boolean initAll) {
        return new TagBootstrapContextImpl<>(this, initAll);
    }

//    public void apply(Map<ResourceLocation, List<TagLoader.EntryWithSource>> tagsMap) {
//        super.apply(tagsMap);
//    }
}
