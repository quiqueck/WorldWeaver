package org.betterx.wover.tag.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverTagProvider;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;
import org.betterx.wover.tag.api.predefined.CommonBiomeTags;

import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.List;

public class BiomeTagProvider extends WoverTagProvider.ForBiomes {
    public BiomeTagProvider(ModCore modCore) {
        super(modCore, List.of(modCore.namespace, modCore.modId, "c", "minecraft"));
    }

    protected void prepareTags(TagBootstrapContext<Biome> ctx) {
        ctx.add(CommonBiomeTags.IS_END_CENTER, Biomes.THE_END);
        ctx.add(CommonBiomeTags.IS_END_HIGHLAND, Biomes.END_HIGHLANDS);
        ctx.add(CommonBiomeTags.IS_END_MIDLAND, Biomes.END_MIDLANDS);
        ctx.add(CommonBiomeTags.IS_END_BARRENS, Biomes.END_BARRENS);
        ctx.add(CommonBiomeTags.IS_SMALL_END_ISLAND, Biomes.SMALL_END_ISLANDS);

        ctx.add(
                BiomeTags.IS_END,
                CommonBiomeTags.IS_END_CENTER,
                CommonBiomeTags.IS_END_HIGHLAND,
                CommonBiomeTags.IS_END_MIDLAND,
                CommonBiomeTags.IS_END_BARRENS,
                CommonBiomeTags.IS_SMALL_END_ISLAND
        );
    }
}
