package de.ambertation.wover.tag.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverTagProvider;
import de.ambertation.wover.tag.api.event.context.TagBootstrapContext;
import de.ambertation.wover.tag.api.predefined.CommonBiomeTags;

import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.List;
import java.util.Set;

public class BiomeTagProvider extends WoverTagProvider.ForBiomes {
    public BiomeTagProvider(ModCore modCore) {
        super(modCore, List.of(modCore.namespace, modCore.modId, "c", "minecraft"), Set.of(CommonBiomeTags.IS_END_LAND));
    }

    public void prepareTags(TagBootstrapContext<Biome> ctx) {
        ctx.add(CommonBiomeTags.IS_END_CENTER, Biomes.THE_END);
        ctx.add(CommonBiomeTags.IS_END_HIGHLAND, Biomes.END_HIGHLANDS);
        ctx.add(CommonBiomeTags.IS_END_MIDLAND, Biomes.END_MIDLANDS);
        ctx.add(CommonBiomeTags.IS_END_BARRENS, Biomes.END_BARRENS);
        ctx.add(CommonBiomeTags.IS_SMALL_END_ISLAND, Biomes.SMALL_END_ISLANDS);

        ctx.add(
                CommonBiomeTags.IS_END_HIGHLAND,
                CommonBiomeTags.IS_END_LAND
        );

        ctx.add(
                CommonBiomeTags.IS_END_MIDLAND,
                CommonBiomeTags.IS_END_LAND
        );

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
