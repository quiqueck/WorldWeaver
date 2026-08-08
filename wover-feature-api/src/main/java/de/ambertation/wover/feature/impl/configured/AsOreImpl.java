package de.ambertation.wover.feature.impl.configured;

import de.ambertation.wover.feature.api.configured.FeatureKey;
import de.ambertation.wover.feature.api.configured.configurators.AsOre;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.BlockReplacement;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

import java.util.LinkedList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AsOreImpl extends FeatureConfiguratorImpl implements AsOre {
    // 26.3: OreConfiguration is gone. Its TargetBlockState record became the top-level BlockReplacement, and
    // the vein size / air-exposure chance are fields on OreFeature itself.
    private final List<BlockReplacement> targetStates = new LinkedList<>();
    private int size = 6;
    private float discardChanceOnAirExposure = 0;

    AsOreImpl(
            @Nullable BootstrapContext<Feature> ctx,
            @Nullable ResourceKey<Feature> featureKey
    ) {
        super(ctx, featureKey);
    }

    @Override
    public AsOre add(Block containedIn, Block ore) {
        return this.add(containedIn, ore.defaultBlockState());
    }

    @Override
    public AsOre add(Block containedIn, BlockState ore) {
        return this.add(new BlockMatchTest(containedIn), ore);
    }

    @Override
    public AsOre add(RuleTest containedIn, Block ore) {
        return this.add(containedIn, ore.defaultBlockState());
    }

    @Override
    public AsOre add(RuleTest containedIn, BlockState ore) {
        targetStates.add(BlockReplacement.replace(
                containedIn,
                ore
        ));
        return this;
    }

    @Override
    public AsOre veinSize(int size) {
        this.size = size;
        return this;
    }

    @Override
    public AsOre discardChanceOnAirExposure(float chance) {
        this.discardChanceOnAirExposure = chance;
        return this;
    }

    @Override
    protected @NotNull Feature createFeature() {
        return new OreFeature(targetStates, size, discardChanceOnAirExposure);
    }

    public static class Key extends FeatureKey<AsOre> {
        public Key(Identifier id) {
            super(id);
        }

        @Override
        public AsOre bootstrap(@NotNull BootstrapContext<Feature> ctx) {
            return new AsOreImpl(ctx, key);
        }
    }
}
