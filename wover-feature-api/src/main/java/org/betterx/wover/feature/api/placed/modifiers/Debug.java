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

public class Debug extends PlacementModifier {
    public static final Debug INSTANCE = new Debug("Placing at {}");
    public static final Codec<Debug> CODEC = RecordCodecBuilder.create((instance) -> instance
            .group(
                    Codec.STRING
                            .fieldOf("caption")
                            .orElse("Placing at {}")
                            .forGetter(cfg -> cfg.caption)
            )
            .apply(instance, Debug::new));
    private final String caption;

    public Debug(String caption) {
        this.caption = caption;
    }

    @Override
    public @NotNull Stream<BlockPos> getPositions(
            PlacementContext placementContext,
            RandomSource randomSource,
            BlockPos blockPos
    ) {
        WoverFeature.C.log.info(caption, blockPos);
        return Stream.of(blockPos);
    }

    @Override
    public @NotNull PlacementModifierType<?> type() {
        return PlacementModifiersImpl.DEBUG;
    }
}
