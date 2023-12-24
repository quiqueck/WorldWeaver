package org.betterx.wover.testmod.generator.datagen;

import org.betterx.wover.biome.api.builder.BiomeBootstrapContext;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.multi.WoverBiomeProvider;
import org.betterx.wover.testmod.entrypoint.TestModWoverWorldGenerator;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;

public class BiomeProvider extends WoverBiomeProvider {
    /**
     * Creates a new instance of {@link WoverBiomeProvider}.
     *
     * @param modCore The {@link ModCore} of the Mod.
     */
    public BiomeProvider(ModCore modCore) {
        super(modCore);
    }

    @Override
    protected void bootstrap(BiomeBootstrapContext context) {
        TestModWoverWorldGenerator
                .NETHER_TEST_BIOME
                .bootstrap(context)
                .isNetherBiome()
                .addNetherClimate(2.0f, 0.0f)
                .startSurface()
                .chancedFloor(Blocks.FARMLAND.defaultBlockState(), Blocks.DIRT.defaultBlockState())
                //.subsurface(Blocks.DIAMOND_BLOCK, 3)
                .finishSurface()
                .genChance(2.0f)
                .fogDensity(8.0f)
                .feature(TestModWoverWorldGenerator.TEST_VEGETATION_PLACED)
                .feature(TestModWoverWorldGenerator.TEST_SCATTERED_PLACED)
                .spawn(EntityType.CAMEL, 1, 2, 6)
                .spawn(EntityType.DONKEY, 2, 1, 3)
                .register();

        TestModWoverWorldGenerator
                .NETHER_MAIN_BIOME
                .bootstrap(context)
                .isNetherBiome()
                .surface(Blocks.WHITE_CONCRETE)
                .register();

        TestModWoverWorldGenerator
                .NETHER_SUB_BIOME
                .bootstrap(context)
                .isNetherBiome()
                .surface(Blocks.GRAY_CONCRETE)
                .parent(TestModWoverWorldGenerator.NETHER_MAIN_BIOME)
                .register();

        TestModWoverWorldGenerator
                .NETHER_WASTE_SUB_BIOME
                .bootstrap(context)
                .isNetherBiome()
                .surface(Blocks.ORANGE_CONCRETE)
                .parent(Biomes.NETHER_WASTES)
                .register();
    }
}
