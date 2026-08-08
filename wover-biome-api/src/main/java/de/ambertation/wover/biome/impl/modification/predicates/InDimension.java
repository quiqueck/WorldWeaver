package de.ambertation.wover.biome.impl.modification.predicates;

import de.ambertation.wover.biome.api.modification.predicates.BiomePredicate;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;

import java.util.Optional;

public record InDimension(ResourceKey<LevelStem> dimensionKey) implements BiomePredicate {
    public static final InDimension OVERWORLD = new InDimension(LevelStem.OVERWORLD);
    public static final InDimension END = new InDimension(LevelStem.END);
    public static final InDimension NETHER = new InDimension(LevelStem.NETHER);
    public static final MapCodec<InDimension> CODEC = ResourceKey.codec(Registries.LEVEL_STEM)
            .xmap(InDimension::new, InDimension::dimensionKey)
            .fieldOf("dimension_key");


    @Override
    public MapCodec<? extends BiomePredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean test(Context ctx) {
        final Holder<LevelStem> dimension = ctx.levelStems.get(dimensionKey).orElse(null);
        if (dimension == null || !dimension.isBound()) return false;

        return dimension.value()
                        .generator()
                        .getBiomeSource()
                        .possibleBiomes()
                        .stream()
                        .map(Holder::unwrapKey)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .anyMatch(entry -> entry.equals(ctx.biomeKey));
    }
}
