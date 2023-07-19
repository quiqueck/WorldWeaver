package org.betterx.wover.tag.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverTagProvider;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;
import org.betterx.wover.tag.api.predefined.CommonBiomeTags;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public class BiomeTagProvider extends WoverTagProvider.ForBiomes {
    public BiomeTagProvider(ModCore modCore) {
        super();
    }

    protected void prepareTags(TagBootstrapContext<Biome> ctx) {
        ctx.add(CommonBiomeTags.IS_END_CENTER, Biomes.THE_END);
        ctx.add(CommonBiomeTags.IS_END_HIGHLAND, Biomes.END_HIGHLANDS);
        ctx.add(CommonBiomeTags.IS_END_MIDLAND, Biomes.END_MIDLANDS);
        ctx.add(CommonBiomeTags.IS_END_BARRENS, Biomes.END_BARRENS);
        ctx.add(CommonBiomeTags.IS_SMALL_END_ISLAND, Biomes.SMALL_END_ISLANDS);
    }
}
