package org.betterx.wover.generator.mixin.biomesource;

import org.betterx.wover.biome.impl.data.BiomeDataRegistryImpl;
import org.betterx.wover.generator.api.biomesource.WoverBiomeData;
import org.betterx.wover.generator.impl.biomesource.end.TheEndBiomesHelper;
import org.betterx.wover.tag.api.predefined.CommonBiomeTags;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import net.fabricmc.fabric.api.biome.v1.TheEndBiomes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TheEndBiomes.class, remap = false)
public class TheEndBiomesMixin {
    @Inject(method = "addBarrensBiome", at = @At("HEAD"))
    private static void bcl_registerBarrens(
            ResourceKey<Biome> highlands,
            ResourceKey<Biome> barrens,
            double weight,
            CallbackInfo ci
    ) {
        TheEndBiomesHelper.add(CommonBiomeTags.IS_END_BARRENS, barrens);
    }

    @Inject(method = "addMidlandsBiome", at = @At("HEAD"))
    private static void bcl_registerMidlands(
            ResourceKey<Biome> highlands,
            ResourceKey<Biome> midlands,
            double weight,
            CallbackInfo ci
    ) {
        BiomeDataRegistryImpl.getFromRegistryOrTemp(
                highlands,
                (key) -> WoverBiomeData.withEdge(key, midlands)
        );

        TheEndBiomesHelper.add(CommonBiomeTags.IS_END_MIDLAND, midlands);
    }

    @Inject(method = "addSmallIslandsBiome", at = @At("HEAD"))
    private static void bcl_registerSmallIslands(
            ResourceKey<Biome> biome, double weight, CallbackInfo ci
    ) {
        TheEndBiomesHelper.add(CommonBiomeTags.IS_SMALL_END_ISLAND, biome);
    }

    @Inject(method = "addHighlandsBiome", at = @At("HEAD"))
    private static void bcl_registerHighlands(
            ResourceKey<Biome> biome, double weight, CallbackInfo ci
    ) {
        TheEndBiomesHelper.add(CommonBiomeTags.IS_END_HIGHLAND, biome);
    }

    @Inject(method = "addMainIslandBiome", at = @At("HEAD"))
    private static void bcl_registerMainIsnalnd(
            ResourceKey<Biome> biome, double weight, CallbackInfo ci
    ) {
        TheEndBiomesHelper.add(CommonBiomeTags.IS_END_CENTER, biome);
    }
}
