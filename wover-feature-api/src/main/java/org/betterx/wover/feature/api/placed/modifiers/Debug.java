package org.betterx.wover.feature.api.placed.modifiers;


import org.betterx.wover.entrypoint.WoverFeature;
import org.betterx.wover.feature.impl.placed.modifiers.PlacementModifiersImpl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

/**
 * Will re-emit the input position unaltered, and log the position to the info log.
 */
public class Debug extends PlacementModifier {
    /**
     * A default instance with the caption set to {@code "Placing at {}"}.
     */
    public static final Debug INSTANCE = new Debug("Placing at {}");
    /**
     * The codec for this placement modifier.
     */
    public static final Codec<Debug> CODEC = RecordCodecBuilder.create((instance) -> instance
            .group(
                    Codec.STRING
                            .fieldOf("caption")
                            .orElse("Placing at {}")
                            .forGetter(cfg -> cfg.caption)
            )
            .apply(instance, Debug::new));
    private final String caption;

    /**
     * Creates a new instance
     *
     * @param caption The caption to log the position with. The caption can contain a single
     *                placeholder {@code {}} which will be replaced with the position.
     */
    public Debug(String caption) {
        this.caption = caption;
    }


    /**
     * Calculates the output positions
     *
     * @param placementContext The placement context.
     * @param randomSource     The random source.
     * @param blockPos         The input position.
     * @return The stream of new positions.
     */
    @Override
    public @NotNull Stream<BlockPos> getPositions(
            PlacementContext placementContext,
            RandomSource randomSource,
            BlockPos blockPos
    ) {
        WoverFeature.C.log.info(caption, blockPos);
        return Stream.of(blockPos);
    }

    /**
     * The type of this placement modifier.
     *
     * @return the type of this placement modifier.
     */
    @Override
    public @NotNull PlacementModifierType<?> type() {
        return PlacementModifiersImpl.DEBUG;
    }
}
