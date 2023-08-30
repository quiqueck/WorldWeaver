package org.betterx.wover.testmod.generator.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.multi.WoverFeatureProvider;
import org.betterx.wover.testmod.entrypoint.WoverWorldGeneratorTestMod;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class FeatureProvider extends WoverFeatureProvider {
    /**
     * Creates a new instance of {@link WoverFeatureProvider}.
     *
     * @param modCore The {@link ModCore} of the Mod.
     */
    public FeatureProvider(ModCore modCore) {
        super(modCore);
    }

    @Override
    protected void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context) {
        WoverWorldGeneratorTestMod
                .TEST_VEGETATION
                .bootstrap(context)
                .add(Blocks.GRASS, 180)
                .addAllStatesFor(BlockStateProperties.AGE_7, Blocks.WHEAT, 20)
                .register();
    }

    @Override
    protected void bootstrapPlaced(BootstapContext<PlacedFeature> context) {
        WoverWorldGeneratorTestMod
                .TEST_VEGETATION_PLACED
                .place(context)
                .vanillaNetherGround(24)
                .register();

        WoverWorldGeneratorTestMod
                .TEST_SCATTERED_PLACED
                .inlineConfiguration(context)
                .randomBlock()
                .add(Blocks.MOSSY_COBBLESTONE, 1)
                .add(Blocks.COBBLESTONE, 4)
                .inlinePlace()
                .betterNetherGround(5)
                .register();
    }
}
