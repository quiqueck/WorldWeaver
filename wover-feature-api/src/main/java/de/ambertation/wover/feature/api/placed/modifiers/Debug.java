package de.ambertation.wover.feature.api.placed.modifiers;


import de.ambertation.wover.entrypoint.LibWoverFeature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

/**
 * Will re-emit the input position unaltered, and log the position to the info log.
 */
public class Debug implements PlacementModifier {
    /**
     * A default instance with the caption set to {@code "Placing at {}"}.
     */
    public static final Debug INSTANCE = new Debug("Placing at {}");
    /**
     * The codec for this placement modifier.
     */
    public static final MapCodec<Debug> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
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
     * @param consumer         Receives every emitted position.
     */
    @Override
    public void modify(
            PlacementContext placementContext,
            RandomSource randomSource,
            BlockPos blockPos,
            Consumer<BlockPos> consumer
    ) {
        LibWoverFeature.C.log.info(caption, blockPos);
        consumer.accept(blockPos);
    }

    /**
     * The {@link MapCodec} that serializes this placement modifier. 26.3 replaced the
     * {@code PlacementModifierType} indirection with the {@link MapCodec} itself.
     *
     * @return the codec of this placement modifier
     */
    @Override
    public @NotNull MapCodec<Debug> codec() {
        return CODEC;
    }
}
