package de.ambertation.wover.feature.api.placed.modifiers;

import de.ambertation.wover.math.api.valueproviders.Vec3iProvider;

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
 * Moves the input position by using a given offset provider.
 */
public class OffsetProvider implements PlacementModifier {
    /**
     * Codec for this placement modifier.
     */
    public static final MapCodec<OffsetProvider> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
            .group(
                    Vec3iProvider.codec(-16, 16)
                                 .fieldOf("offset")
                                 .forGetter(cfg -> cfg.offset)
            )
            .apply(instance, OffsetProvider::new));

    private final Vec3iProvider offset;


    /**
     * Creates a new instance.
     *
     * @param offset The offset provider to apply
     */
    public OffsetProvider(Vec3iProvider offset) {
        this.offset = offset;
    }

    /**
     * Calculates the positions that this placement modifier will emit.
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
        consumer.accept(blockPos.offset(offset.sample(randomSource)));
    }

    /**
     * The {@link MapCodec} that serializes this placement modifier. 26.3 replaced the
     * {@code PlacementModifierType} indirection with the {@link MapCodec} itself.
     *
     * @return the codec of this placement modifier
     */
    @Override
    public @NotNull MapCodec<OffsetProvider> codec() {
        return CODEC;
    }
}
