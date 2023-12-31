package org.betterx.wover.feature.api.configured.configurators;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.SimpleBlockFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

/**
 * Configuration of a simple block-placing Feature ({@link SimpleBlockFeature}).
 * <p>
 * This builder configures a {@link SimpleBlockFeature}.
 */
public interface ForSimpleBlock extends FeatureConfigurator<SimpleBlockConfiguration, SimpleBlockFeature> {
    /**
     * Sets the block state provider for this configuration.
     *
     * @param provider the provider
     * @return this builder
     */
    ForSimpleBlock block(BlockStateProvider provider);
    /**
     * Sets the block that should be generated by this feature (in its default state).
     *
     * @param block the block
     * @return this builder
     */
    ForSimpleBlock block(Block block);
    /**
     * Sets the state for the block that should be generated by this feature.
     *
     * @param state the block state
     * @return this builder
     */
    ForSimpleBlock block(BlockState state);
}
