package de.ambertation.wover.feature.impl.configured;

import de.ambertation.wover.feature.api.configured.FeatureKey;
import de.ambertation.wover.feature.api.configured.configurators.ForSimpleBlock;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SimpleBlockFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class ForSimpleBlockImpl extends FeatureConfiguratorImpl implements ForSimpleBlock {
    private BlockStateProvider provider;

    ForSimpleBlockImpl(
            @Nullable BootstrapContext<Feature> ctx,
            @Nullable ResourceKey<Feature> featureKey
    ) {
        super(ctx, featureKey);
    }


    @Override
    public ForSimpleBlock block(BlockStateProvider provider) {
        this.provider = provider;
        return this;
    }


    @Override
    public ForSimpleBlock block(Block block) {
        return block(BlockStateProvider.simple(block));
    }


    @Override
    public ForSimpleBlock block(BlockState state) {
        return block(BlockStateProvider.simple(state));
    }

    /**
     * Creates the {@link SimpleBlockFeature} for this builder.
     * <p>
     * 26.3 folded {@code SimpleBlockConfiguration} into {@link SimpleBlockFeature}, so the provider is a
     * constructor argument of the feature now.
     *
     * @return the feature
     */
    @Override
    protected @NotNull Feature createFeature() {
        if (this.provider == null) {
            this.throwStateError("No block state provider set.");
        }

        return new SimpleBlockFeature(provider);
    }

    public static class Key extends FeatureKey<ForSimpleBlock> {
        public Key(Identifier id) {
            super(id);
        }

        @Override
        public ForSimpleBlock bootstrap(@NotNull BootstrapContext<Feature> ctx) {
            return new ForSimpleBlockImpl(ctx, key);
        }
    }
}
