package de.ambertation.wover.feature.impl.configured;

import de.ambertation.wover.feature.api.configured.ConfiguredFeatureKey;
import de.ambertation.wover.feature.api.configured.ConfiguredFeatureManager;
import de.ambertation.wover.feature.api.configured.configurators.WeightedBlockPatch;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import de.ambertation.wover.feature.impl.random.RandomPatchFeature;
import de.ambertation.wover.feature.impl.random.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// Compat shim for the vanilla-removed random_patch feature (see WeightedBlockPatch); intentionally implements
// the deprecated API.
@SuppressWarnings("removal")
public class WeightedBlockPatchImpl extends WeightedBaseBlockImpl<RandomPatchConfiguration, RandomPatchFeature, WeightedBlockPatch> implements WeightedBlockPatch {

    private BlockPredicate groundType = null;
    private boolean isEmpty = true;
    private int tries = 96;
    private int xzSpread = 7;
    private int ySpread = 3;

    WeightedBlockPatchImpl(
            @Nullable BootstrapContext<ConfiguredFeature<?, ?>> ctx,
            @Nullable ResourceKey<ConfiguredFeature<?, ?>> key
    ) {
        super(ctx, key);
    }


    @Override
    public WeightedBlockPatch isEmpty() {
        return this.isEmpty(true);
    }

    @Override
    public WeightedBlockPatch isEmpty(boolean value) {
        this.isEmpty = value;
        return this;
    }

    @Override
    public WeightedBlockPatch isOn(BlockPredicate predicate) {
        this.groundType = predicate;
        return this;
    }

    @Override
    public WeightedBlockPatch isEmptyAndOn(BlockPredicate predicate) {
        return this.isEmpty().isOn(predicate);
    }

    @Override
    public WeightedBlockPatch likeDefaultNetherVegetation() {
        return likeDefaultNetherVegetation(8, 4);
    }

    @Override
    public WeightedBlockPatch likeDefaultNetherVegetation(int xzSpread, int ySpread) {
        this.xzSpread = xzSpread;
        this.ySpread = ySpread;
        tries = xzSpread * xzSpread;
        return this;
    }


    @Override
    public WeightedBlockPatch tries(int v) {
        tries = v;
        return this;
    }

    @Override
    public WeightedBlockPatch spreadXZ(int v) {
        xzSpread = v;
        return this;
    }

    @Override
    public WeightedBlockPatch spreadY(int v) {
        ySpread = v;
        return this;
    }

    @Override
    public @NotNull RandomPatchConfiguration createConfiguration() {
        var blockFeature = ConfiguredFeatureManager
                .INLINE_BUILDER
                .simple()
                .block((new WeightedStateProvider(stateBuilder.build())))
                .inlinePlace();

        if (isEmpty) blockFeature.isEmpty();
        if (groundType != null) blockFeature.isOn(groundType);

        return new RandomPatchConfiguration(tries, xzSpread, ySpread, blockFeature.directHolder());
    }

    @Override
    protected @NotNull RandomPatchFeature getFeature() {
        return de.ambertation.wover.feature.impl.FeatureManagerImpl.RANDOM_PATCH;
    }


    public static class Key extends ConfiguredFeatureKey<WeightedBlockPatch> {
        public Key(Identifier id) {
            super(id);
        }

        @Override
        public WeightedBlockPatch bootstrap(@NotNull BootstrapContext<ConfiguredFeature<?, ?>> ctx) {
            return new WeightedBlockPatchImpl(ctx, key);
        }
    }

    public static class KeyBonemeal extends ConfiguredFeatureKey<WeightedBlockPatch> {
        public KeyBonemeal(Identifier id) {
            super(id);
        }

        @Override
        public WeightedBlockPatch bootstrap(@NotNull BootstrapContext<ConfiguredFeature<?, ?>> ctx) {
            return new WeightedBlockPatchImpl(ctx, key).likeDefaultBonemeal();
        }
    }
}
