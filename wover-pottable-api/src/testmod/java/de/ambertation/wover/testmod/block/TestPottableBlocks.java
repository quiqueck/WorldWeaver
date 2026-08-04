package de.ambertation.wover.testmod.block;

import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.pottable.api.trait.PottablePlantBlockTrait;
import de.ambertation.wover.pottable.api.trait.PottableSoilBlockTrait;
import de.ambertation.wover.testmod.entrypoint.TestModWoverPottable;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.ApiStatus;

/**
 * Blocks used to exercise the <em>trait-driven</em> half of the pottable-api.
 * <p>
 * The two datapack registries can be filled in three ways - a JSON file, a direct
 * {@code DatapackRegistryBuilder.addBootstrap}, or by attaching
 * {@link PottablePlantBlockTrait}/{@link PottableSoilBlockTrait} to a block and letting
 * {@code bootstrapPottablePlants}/{@code bootstrapPottableSoils} sweep them up. The third path is the one
 * the module actually advertises (it is what the {@code Wover*RegistryProvider} datagen providers use), so
 * these blocks exist to give it real coverage.
 * <p>
 * {@link #DOUBLE_TRAIT_PLANT} and {@link #DOUBLE_TRAIT_SOIL} deliberately attach their trait
 * <strong>twice</strong>. That combination is what
 * {@link PottablePlantBlockTrait#keepLatestOnly()} exists to make well-defined: a {@code PottablePlant}'s
 * registry key comes only from the block id, so two traits mean two registrations under one key - and the
 * bootstrap context keeps the first and silently ignores the rest, which used to publish the <em>earlier</em>,
 * less restrictive trait with nothing logged.
 */
public class TestPottableBlocks {
    private static final BlockRegistry R = BlockRegistry.forMod(TestModWoverPottable.C);

    private TestPottableBlocks() {
    }

    /**
     * A plain plant block carrying a single {@link PottablePlantBlockTrait#any()} trait - the ordinary case.
     */
    public static final Block TRAIT_PLANT = R
            .defineDefaultBlock("trait_plant")
            .addTrait(PottablePlantBlockTrait.any())
            .instabreak()
            .buildAndRegister();

    /**
     * A plain soil block carrying a single {@link PottableSoilBlockTrait#DEFAULT} trait - the ordinary case.
     */
    public static final Block TRAIT_SOIL = R
            .defineDefaultBlock("trait_soil")
            .addTrait(PottableSoilBlockTrait.DEFAULT)
            .instabreak()
            .buildAndRegister();

    /**
     * A plant block whose trait is attached twice, with <em>different</em> soil restrictions.
     * <p>
     * "Latest wins", so the surviving trait must be the {@link BlockTags#DIRT} one, not the
     * {@code any()} one added before it. Testing it this way round matters: if the de-duplication ever
     * regressed to "first wins" the block would silently become pottable on <em>any</em> soil, which is a
     * strictly wider permission than the author asked for.
     */
    public static final Block DOUBLE_TRAIT_PLANT = R
            .defineDefaultBlock("double_trait_plant")
            .addTrait(PottablePlantBlockTrait.any())
            .addTrait(PottablePlantBlockTrait.withSoils(BlockTags.DIRT))
            .instabreak()
            .buildAndRegister();

    /**
     * A soil block whose (stateless) trait is attached twice.
     */
    public static final Block DOUBLE_TRAIT_SOIL = R
            .defineDefaultBlock("double_trait_soil")
            .addTrait(PottableSoilBlockTrait.DEFAULT)
            .addTrait(PottableSoilBlockTrait.DEFAULT)
            .instabreak()
            .buildAndRegister();

    /**
     * Forces the static initializer to run, so the blocks above are registered.
     */
    @ApiStatus.Internal
    public static void ensureStaticallyLoaded() {
    }
}
