package de.ambertation.wover.feature.impl.features;

import de.ambertation.wover.feature.api.features.config.TemplateFeatureConfig;
import de.ambertation.wover.structure.api.StructureNBT;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FeatureTemplateImpl extends StructureNBT implements TemplateFeatureConfig.FeatureTemplate {
    public static final Codec<TemplateFeatureConfig.FeatureTemplate> CODEC =
            RecordCodecBuilder.create((instance) ->
                    instance.group(
                                    Identifier.CODEC
                                            .fieldOf("location")
                                            .forGetter((cfg) -> cfg.getLocation()),
                                    Codec
                                            .INT
                                            .fieldOf("offset_y")
                                            .orElse(0)
                                            .forGetter((cfg) -> cfg.getOffsetY())
                            )
                            .apply(instance, FeatureTemplateImpl::new)
            );
    public final int offsetY;

    protected FeatureTemplateImpl(Identifier location, int offsetY) {
        super(location);
        this.offsetY = offsetY;
    }

    // 26.1 registry loading is async + parallel - createTemplate() is reached from
    // WithTemplatesImpl's ConfiguredFeatureKey.bootstrap(), which can run concurrently across
    // worker threads for different configured_feature entries. A plain HashMap here would race.
    private static final Map<String, FeatureTemplateImpl> READER_CACHE = new ConcurrentHashMap<>();

    public static TemplateFeatureConfig.FeatureTemplate createTemplate(
            Identifier location
    ) {
        return createTemplate(location, 0);
    }

    public static TemplateFeatureConfig.FeatureTemplate createTemplate(
            Identifier location,
            int offsetY
    ) {
        String key = location.toString() + "::" + offsetY;
        return READER_CACHE.computeIfAbsent(key, r -> new FeatureTemplateImpl(location, offsetY));
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

    @Override
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

    public boolean canGenerate(LevelAccessor level, BlockPos pos, Rotation rotation) {
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

    @Override
    public boolean loaded() {
        return structure != null;
    }

    @Override
    public int getOffsetY() {
        return offsetY;
    }

    @Override
    public Identifier getLocation() {
        return location;
    }
}
