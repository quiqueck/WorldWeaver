package org.betterx.wover.tag.datagen;

import org.betterx.wover.datagen.api.WoverTagProvider;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;
import org.betterx.wover.tag.api.predefined.CommonBiomeTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import java.util.concurrent.CompletableFuture;

public class BiomeTagProvider extends WoverTagProvider.ForBiomes {
    public BiomeTagProvider(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        super(output, registriesFuture);
    }

    protected void prepareTags(TagBootstrapContext<Biome> ctx) {
        ctx.add(CommonBiomeTags.IS_END_CENTER, Biomes.THE_END);
        ctx.add(CommonBiomeTags.IS_END_HIGHLAND, Biomes.END_HIGHLANDS);
        ctx.add(CommonBiomeTags.IS_END_MIDLAND, Biomes.END_MIDLANDS);
        ctx.add(CommonBiomeTags.IS_END_BARRENS, Biomes.END_BARRENS);
        ctx.add(CommonBiomeTags.IS_SMALL_END_ISLAND, Biomes.SMALL_END_ISLANDS);
    }
}
