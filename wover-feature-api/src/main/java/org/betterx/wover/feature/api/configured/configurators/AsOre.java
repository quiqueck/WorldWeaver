package org.betterx.wover.feature.api.configured.configurators;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

/**
 * Places an ore ({@link OreFeature}).
 */
public interface AsOre extends FeatureConfigurator<OreConfiguration, OreFeature> {
    /**
     * Defines that an ore block should be placed in a surrounding block.
     *
     * @param containedIn The block that should surround the ore
     * @param ore         The ore block. The Feature will place the default
     *                    State of the Block
     * @return the same instance
     */
    AsOre add(Block containedIn, Block ore);

    /**
     * Defines that an ore block should be placed in a surrounding block.
     *
     * @param containedIn The block that should surround the ore
     * @param ore         The {@link BlockState} ore block
     * @return
     */
    AsOre add(Block containedIn, BlockState ore);
    /**
     * Defines that an ore block should be placed at a location that satisfies the
     * passed {@link RuleTest}.
     *
     * @param containedIn The {@link RuleTest} that should be satisfied
     * @param ore         The ore block. The Feature will place the default
     *                    State of the Block
     * @return the same instance
     */
    AsOre add(RuleTest containedIn, Block ore);

    /**
     * Defines that an ore block should be placed at a location that satisfies the
     * passed {@link RuleTest}.
     *
     * @param containedIn The {@link RuleTest} that should be satisfied
     * @param ore         The {@link BlockState} ore block
     * @return the same instance
     */
    AsOre add(RuleTest containedIn, BlockState ore);

    /**
     * Defines the size of the ore vein.
     *
     * @param size The size of the ore vein
     * @return the same instance
     */
    AsOre veinSize(int size);

    /**
     * How often should the vein be discarded if it is exposed to air.
     * <p>
     * If 0 the vein will never be discarded, and thus be visible on the surface.
     * If 1 the vein will always be discarded when in contact with air, and thus
     * never be visible on the surface.
     *
     * @param chance The chance of the vein being discarded
     * @return the same instance
     */
    AsOre discardChanceOnAirExposure(float chance);
}
