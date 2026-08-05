package de.ambertation.wover.feature.impl.random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * Re-implementation of Mojang's {@code RandomPatchConfiguration}, which was removed from vanilla in
 * Minecraft 26.1 (the {@code minecraft:random_patch} feature type no longer exists). WorldWeaver still
 * exposes {@code random_patch} configured features through its public API and committed datapack data, so
 * the feature type and its configuration are provided by the mod itself.
 * <p>
 * The record layout and codec keys are identical to the vanilla type so that previously generated data
 * ({@code tries}, {@code xz_spread}, {@code y_spread}, {@code feature}) still decodes unchanged.
 */
public record RandomPatchConfiguration(
        int tries,
        int xzSpread,
        int ySpread,
        Holder<PlacedFeature> feature
) implements FeatureConfiguration {
    public static final Codec<RandomPatchConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    ExtraCodecs.POSITIVE_INT.fieldOf("tries").orElse(128).forGetter(cfg -> cfg.tries),
                    Codec.intRange(0, 1024).fieldOf("xz_spread").orElse(7).forGetter(cfg -> cfg.xzSpread),
                    Codec.intRange(0, 256).fieldOf("y_spread").orElse(3).forGetter(cfg -> cfg.ySpread),
                    PlacedFeature.CODEC.fieldOf("feature").forGetter(cfg -> cfg.feature)
            )
            .apply(instance, RandomPatchConfiguration::new));
}
