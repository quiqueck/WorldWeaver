package org.betterx.wover.pottable.api.trait;

import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitKey;
import org.betterx.wover.block.api.trait.GenericBlockTrait;
import org.betterx.wover.block.impl.trait.BlockTraitImpl;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverPottable;
import org.betterx.wover.pottable.api.PottableSoil;
import org.betterx.wover.pottable.api.PottableSoilRegistry;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

import java.util.function.BiPredicate;

/**
 * Marks a block as usable soil for a {@link org.betterx.wover.pottable.api.PottablePlant}
 * (e.g. inside a flower pot).
 * <p>
 * Attach with {@code .addTrait(PottableSoilBlockTrait.DEFAULT)} at the block's normal
 * registration site. Blocks carrying this trait are picked up automatically by
 * {@link org.betterx.wover.pottable.api.datagen.WoverPottableSoilRegistryProvider} -
 * no custom datagen code is needed.
 */
public class PottableSoilBlockTrait extends BlockTraitImpl<Block, GenericBlockTrait> implements GenericBlockTrait {
    public static final BlockTraitKey KEY = BlockTraitKey.ofUnique(LibWoverPottable.C, "pottable_soil");
    public static final PottableSoilBlockTrait DEFAULT = new PottableSoilBlockTrait();

    private PottableSoilBlockTrait() {
    }

    @Override
    public BlockTraitKey key() {
        return KEY;
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
