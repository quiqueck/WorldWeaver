package de.ambertation.wover.testmod.generator.datagen;

import de.ambertation.wover.biome.api.builder.BiomeBootstrapContext;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.multi.WoverBiomeProvider;
import de.ambertation.wover.testmod.entrypoint.TestModWoverWorldGenerator;

import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.DyeColor;
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
                .spawn(EntityTypes.CAMEL, 1, 2, 6)
                .spawn(EntityTypes.DONKEY, 2, 1, 3)
                .register();

        TestModWoverWorldGenerator
                .NETHER_MAIN_BIOME
                .bootstrap(context)
                .isNetherBiome()
                // 26.3 folded the 16 dyed variants of each block into a ColorCollection, so the flat
                // Blocks.WHITE_CONCRETE / GRAY_CONCRETE / ORANGE_CONCRETE constants are gone. Same blocks.
                .surface(Blocks.CONCRETE.pick(DyeColor.WHITE))
                .register();

        TestModWoverWorldGenerator
                .NETHER_SUB_BIOME
                .bootstrap(context)
                .isNetherBiome()
                .surface(Blocks.CONCRETE.pick(DyeColor.GRAY))
                .parent(TestModWoverWorldGenerator.NETHER_MAIN_BIOME)
                .register();

        TestModWoverWorldGenerator
                .NETHER_WASTE_SUB_BIOME
                .bootstrap(context)
                .isNetherBiome()
                .surface(Blocks.CONCRETE.pick(DyeColor.ORANGE))
                .parent(Biomes.NETHER_WASTES)
                .register();
    }
}
