package org.betterx.wover.feature.impl.features;

import org.betterx.wover.structure.api.StructureNBT;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;

import com.google.common.collect.Maps;

import java.util.Map;

public class FeatureTemplate extends StructureNBT {
    public static final Codec<FeatureTemplate> CODEC =
            RecordCodecBuilder.create((instance) ->
                    instance.group(
                                    ResourceLocation.CODEC
                                            .fieldOf("location")
                                            .forGetter((cfg) -> cfg.location),
                                    Codec
                                            .INT
                                            .fieldOf("offset_y")
                                            .orElse(0)
                                            .forGetter((cfg) -> cfg.offsetY)
                            )
                            .apply(instance, FeatureTemplate::new)
            );
    public final int offsetY;

    protected FeatureTemplate(ResourceLocation location, int offsetY) {
        super(location);
        this.offsetY = offsetY;
    }

    private static final Map<String, FeatureTemplate> READER_CACHE = Maps.newHashMap();

    public static FeatureTemplate create(
            ResourceLocation location
    ) {
        return create(location, 0);
    }

    public static FeatureTemplate create(
            ResourceLocation location,
            int offsetY
    ) {
        String key = location.toString() + "::" + offsetY;
        return READER_CACHE.computeIfAbsent(key, r -> new FeatureTemplate(location, offsetY));
    }

    public boolean generateIfPlaceable(
            ServerLevelAccessor level,
            BlockPos pos,
            RandomSource random
    ) {
        return generateIfPlaceable(
                level,
                pos,
                getRandomRotation(random),
                getRandomMirror(random)
        );
    }

    public boolean generateIfPlaceable(
            ServerLevelAccessor level,
            BlockPos pos,
            Rotation r,
            Mirror m
    ) {
        if (canGenerate(level, pos, r)) {
            return generate(level, pos, r, m);
        }
        return false;
    }

    public boolean generate(ServerLevelAccessor level, BlockPos pos, Rotation r, Mirror m) {
        return generateCentered(level, pos.above(offsetY), r, m);
    }

    protected boolean canGenerate(LevelAccessor level, BlockPos pos, Rotation rotation) {
        if (containsBedrock(level, pos)) return false;
        return true;
    }

    private boolean containsBedrock(LevelAccessor level, BlockPos startPos) {
        for (int i = 0; i < this.structure.getSize().getY(); i += 2) {
            if (level.getBlockState(startPos.above(i)).is(Blocks.BEDROCK)) {
                return true;
            }
        }
        return false;
    }

    public boolean loaded() {
        return structure != null;
    }
}
