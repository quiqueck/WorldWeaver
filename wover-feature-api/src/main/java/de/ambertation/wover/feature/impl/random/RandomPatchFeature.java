package de.ambertation.wover.feature.impl.random;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import com.mojang.serialization.Codec;

import java.util.stream.Stream;

/**
 * Re-implementation of Mojang's {@code RandomPatchFeature} + {@code RandomPatchConfiguration}, both removed
 * from vanilla in Minecraft 26.1 (the {@code minecraft:random_patch} feature type no longer exists).
 * WorldWeaver still exposes {@code random_patch} features through its public API and committed datapack data,
 * so the feature type is provided by the mod itself under {@code wover:random_patch}.
 * <p>
 * 26.3 collapsed {@code ConfiguredFeature} into {@link Feature}, so the separate configuration record is gone
 * and its four fields live on the feature itself. The codec keys are still the vanilla ones
 * ({@code tries}, {@code xz_spread}, {@code y_spread}, {@code feature}); they are no longer nested inside a
 * {@code config} object, because 26.3 dispatches feature types on a {@link MapCodec} directly.
 * <p>
 * The placement algorithm is a faithful port of the vanilla behaviour: it tries to place the inner
 * {@link PlacedFeature} a number of times at random offsets around the origin.
 */
public record RandomPatchFeature(
        int tries,
        int xzSpread,
        int ySpread,
        Holder<PlacedFeature> feature
) implements Feature {
    /**
     * The codec for this feature.
     */
    public static final MapCodec<RandomPatchFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    ExtraCodecs.POSITIVE_INT.fieldOf("tries").orElse(128).forGetter(cfg -> cfg.tries),
                    Codec.intRange(0, 1024).fieldOf("xz_spread").orElse(7).forGetter(cfg -> cfg.xzSpread),
                    Codec.intRange(0, 256).fieldOf("y_spread").orElse(3).forGetter(cfg -> cfg.ySpread),
                    PlacedFeature.CODEC.fieldOf("feature").forGetter(cfg -> cfg.feature)
            )
            .apply(instance, RandomPatchFeature::new));

    /**
     * The codec that serializes this feature.
     *
     * @return {@link #CODEC}
     */
    @Override
    public MapCodec<RandomPatchFeature> codec() {
        return CODEC;
    }

    /**
     * The feature this patch scatters. 26.3 walks the feature graph through this method (for example to
     * collect a biome's bone-meal features), so the inner feature must be reported here.
     *
     * @return the features of the inner {@link PlacedFeature}
     */
    @Override
    public Stream<Holder<Feature>> getSubFeatures() {
        return feature.value().getFeatures();
    }

    /**
     * Tries to place the inner feature {@link #tries()} times at random offsets around the origin.
     *
     * @param level     the level to place in
     * @param generator the chunk generator
     * @param random    the random source
     * @param origin    the position to scatter around
     * @return {@code true} if at least one placement succeeded
     */
    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        final int xzSpread = this.xzSpread + 1;
        final int ySpread = this.ySpread + 1;

        int placed = 0;
        for (int i = 0; i < tries; i++) {
            mutable.setWithOffset(
                    origin,
                    random.nextInt(xzSpread) - random.nextInt(xzSpread),
                    random.nextInt(ySpread) - random.nextInt(ySpread),
                    random.nextInt(xzSpread) - random.nextInt(xzSpread)
            );
            if (feature.value().place(level, generator, random, mutable)) {
                placed++;
            }
        }

        return placed > 0;
    }
}
