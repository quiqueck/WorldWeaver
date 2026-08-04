package de.ambertation.wover.pottable.api.trait;

import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockTraitKey;
import de.ambertation.wover.block.api.trait.GenericBlockTrait;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverPottable;
import de.ambertation.wover.pottable.api.PottableSoil;
import de.ambertation.wover.pottable.api.PottableSoilRegistry;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

import java.util.function.BiPredicate;

/**
 * Marks a block as usable soil for a {@link de.ambertation.wover.pottable.api.PottablePlant}
 * (e.g. inside a flower pot).
 * <p>
 * Attach with {@code .addTrait(PottableSoilBlockTrait.DEFAULT)} at the block's normal
 * registration site. Blocks carrying this trait are picked up automatically by
 * {@link de.ambertation.wover.pottable.api.datagen.WoverPottableSoilRegistryProvider} -
 * no custom datagen code is needed.
 */
public class PottableSoilBlockTrait extends BlockTraitImpl<Block, GenericBlockTrait> implements GenericBlockTrait {
    /**
     * The unique key identifying this trait.
     */
    public static final BlockTraitKey KEY = BlockTraitKey.ofUnique(LibWoverPottable.C, "pottable_soil");
    /**
     * The single, shared instance of this trait. There is only ever one variant, so this constant is the only
     * instance you need.
     */
    public static final PottableSoilBlockTrait DEFAULT = new PottableSoilBlockTrait();

    private PottableSoilBlockTrait() {
    }

    @Override
    public BlockTraitKey key() {
        return KEY;
    }

    /**
     * A block can only ever have a single {@link PottableSoil} entry, so only the most recently added trait
     * is kept.
     * <p>
     * This trait is a stateless marker with a single {@link #DEFAULT} instance, so adding it twice cannot
     * change the resulting entry - unlike {@link PottablePlantBlockTrait#keepLatestOnly() its plant
     * counterpart}, where a duplicate silently published the earlier, less restrictive soil set. It is
     * declared here anyway so both pottable traits share one rule ("a block is pottable soil, or it is
     * not"), and so the invariant does not silently depend on {@link #bootstrapPottableSoils} happening to
     * ignore the trait list's length.
     *
     * @return always {@code true}
     */
    @Override
    public boolean keepLatestOnly() {
        return true;
    }

    @Override
    public GenericBlockTrait forRuntime() {
        return this;
    }

    /**
     * Registers a {@link PottableSoil} for every block of the given mod that carries
     * this trait.
     *
     * @param modCore The mod whose blocks should be scanned.
     * @param ctx     The bootstrap context to register into.
     * @param filter  A filter to restrict which blocks are considered.
     */
    public static void bootstrapPottableSoils(
            ModCore modCore,
            BootstrapContext<PottableSoil> ctx,
            BiPredicate<ResourceKey<Block>, Block> filter
    ) {
        BlockRegistry
                .forMod(modCore)
                .allEntries()
                .filter(e -> filter.test(e.getKey(), e.getValue()))
                .forEach(e -> {
                    var runtimeTraits = BlockTrait.<Block, GenericBlockTrait>getRuntimeTraits(e.getValue(), KEY);
                    if (runtimeTraits == null) return;
                    PottableSoilRegistry.register(
                            ctx,
                            PottableSoilRegistry.createKey(e.getKey().location()),
                            e.getKey()
                    );
                });
    }
}
