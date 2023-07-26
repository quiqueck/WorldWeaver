package org.betterx.wover.biome.api.modification.predicates;

import org.betterx.wover.biome.impl.api.modification.predicates.BiomePredicateRegistryImpl;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BiomePredicate {
    static final class Context {
        @NotNull
        public final RegistryAccess registryAccess;
        @NotNull
        public final ResourceKey<Biome> biomeKey;
        @NotNull
        public final Biome biome;
        @NotNull
        public final Holder<Biome> biomeHolder;

        @NotNull
        public final Registry<Biome> biomes;
        @NotNull
        public final Registry<LevelStem> levelStems;
        @NotNull
        public final Registry<Structure> structures;
        @NotNull
        public final Registry<PlacedFeature> placedFeatures;
        @NotNull
        public final Registry<ConfiguredFeature<?, ?>> configuredFeatures;

        private Context(
                @NotNull RegistryAccess registryAccess,
                @NotNull Registry<Biome> biomes,
                @NotNull ResourceKey<Biome> biomeKey,
                @NotNull Biome biome
        ) {
            this.registryAccess = registryAccess;
            this.biomeKey = biomeKey;
            this.biomes = biomes;

            this.levelStems = registryAccess.registryOrThrow(Registries.LEVEL_STEM);
            this.structures = registryAccess.registryOrThrow(Registries.STRUCTURE);
            this.placedFeatures = registryAccess.registryOrThrow(Registries.PLACED_FEATURE);
            this.configuredFeatures = registryAccess.registryOrThrow(Registries.CONFIGURED_FEATURE);

            this.biome = biome;
            this.biomeHolder = biomes.getHolderOrThrow(biomeKey);
        }

        public static @Nullable Context of(RegistryAccess registryAccess, ResourceKey<Biome> biomeKey) {
            var biomes = registryAccess.registryOrThrow(Registries.BIOME);
            var biome = biomes.get(biomeKey);
            if (biome == null) return null;
            return new Context(registryAccess, biomes, biomeKey, biome);
        }
    }

    /**
     * Codec for a BiomePredicate that delegates to the
     * Codec returned by {@link #codec()}.
     */
    Codec<BiomePredicate> CODEC = BiomePredicateRegistryImpl
            .BIOME_PREDICATES.byNameCodec()
                             .dispatch(
                                     p -> p.codec().codec(),
                                     Function.identity()
                             );
    KeyDispatchDataCodec<? extends BiomePredicate> codec();
    boolean test(Context ctx);
}
