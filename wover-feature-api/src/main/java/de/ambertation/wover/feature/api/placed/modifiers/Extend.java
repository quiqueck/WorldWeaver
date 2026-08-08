package de.ambertation.wover.feature.api.placed.modifiers;


import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;


/**
 * Extends the input position in the given direction by the given length. All extended positions
 * will be emitted. The input position will be emitted as well.
 * <p>
 * This will basically generate a colum of positions in the given direction.
 */
public class Extend implements PlacementModifier {
    /**
     * The codec for this placement modifier.
     */
    public static final MapCodec<Extend> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
            .group(
                    Direction.CODEC
                            .fieldOf("direction")
                            .orElse(Direction.DOWN)
                            .forGetter(cfg -> cfg.direction),
                    IntProviders.codec(0, 16)
                               .fieldOf("length")
                               .orElse(UniformInt.of(0, 3))
                               .forGetter(cfg -> cfg.length)
            )
            .apply(instance, Extend::new));

    private final Direction direction;
    private final IntProvider length;

    /**
     * Constructs a new extend placement modifier.
     *
     * @param direction The direction to extend in.
     * @param length    The length to extend by.
     */
    public Extend(Direction direction, IntProvider length) {
        this.direction = direction;
        this.length = length;
    }

    /**
     * Calculates the positions that this placement modifier will emit.
     *
     * @param placementContext The placement context.
     * @param random           The random source.
     * @param blockPos         The input position.
     * @param consumer         Receives every emitted position.
     */
    @Override
    public void modify(
            PlacementContext placementContext,
            RandomSource random,
            BlockPos blockPos,
            Consumer<BlockPos> consumer
    ) {
        final int count = length.sample(random);
        consumer.accept(blockPos);
        for (int y = 1; y < count + 1; y++) {
            consumer.accept(blockPos.relative(direction, y));
        }
    }

    /**
     * The {@link MapCodec} that serializes this placement modifier. 26.3 replaced the
     * {@code PlacementModifierType} indirection with the {@link MapCodec} itself.
     *
     * @return the codec of this placement modifier
     */
    @Override
    public @NotNull MapCodec<Extend> codec() {
        return CODEC;
    }
}