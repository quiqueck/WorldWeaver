package de.ambertation.wover.feature.impl.random;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * Re-implementation of Mojang's {@code RandomPatchFeature}, removed from vanilla in Minecraft 26.1.
 * The placement algorithm is a faithful port of the vanilla behaviour: it tries to place the inner
 * {@link PlacedFeature} a number of times at random offsets around the origin.
 *
 * @see RandomPatchConfiguration
 */
public class RandomPatchFeature extends Feature<RandomPatchConfiguration> {
    public RandomPatchFeature(Codec<RandomPatchConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<RandomPatchConfiguration> context) {
        final RandomPatchConfiguration config = context.config();
        final RandomSource random = context.random();
        final BlockPos origin = context.origin();
        final WorldGenLevel level = context.level();
        final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        final int xzSpread = config.xzSpread() + 1;
        final int ySpread = config.ySpread() + 1;

        int placed = 0;
        for (int i = 0; i < config.tries(); i++) {
            mutable.setWithOffset(
                    origin,
                    random.nextInt(xzSpread) - random.nextInt(xzSpread),
                    random.nextInt(ySpread) - random.nextInt(ySpread),
                    random.nextInt(xzSpread) - random.nextInt(xzSpread)
            );
            if (config.feature().value().place(level, context.chunkGenerator(), random, mutable)) {
                placed++;
            }
        }

        return placed > 0;
    }
}
