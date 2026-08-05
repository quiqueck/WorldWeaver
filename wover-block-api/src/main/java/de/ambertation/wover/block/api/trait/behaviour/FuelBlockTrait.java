package de.ambertation.wover.block.api.trait.behaviour;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.BlockTraitKey;
import de.ambertation.wover.block.api.trait.GenericBlockTrait;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.entrypoint.LibWoverBlock;

import net.minecraft.world.level.block.Block;

import net.fabricmc.fabric.api.registry.FuelValueEvents;

/**
 * A behaviour trait that registers the block's item as a furnace fuel with a fixed burn time.
 * <p>
 * This mirrors the (now-removed) {@code NetherBlocks.addFuel(source, result)} helper, which did
 * <pre>{@code FuelValueEvents.BUILD.register((builder, ctx) -> builder.add(result, 40));}</pre>
 * The trait performs exactly that registration (same registry: Fabric's {@link FuelValueEvents#BUILD},
 * same timing: at block-registration time via {@link #afterBlockRegistration}), so a block that carries
 * this trait becomes a valid furnace fuel identically to the old code path.
 * <p>
 * <b>Burn-time source:</b> BetterNether registered every slab/stairs/roof/button/plate/etc. fuel with a
 * hard-coded {@code 40} ticks (the {@code addFuel} body), which is what {@link #DEFAULT_TICKS} / {@link #withDefault()}
 * emit. Use {@link #withTicks(int)} for any other value.
 * <p>
 * <b>Not verifiable by datagen:</b> furnace fuel is a runtime-only Fabric registration and is <em>not</em>
 * reflected in any golden datapack file (the {@code fuel=} column in the registrations golden reads {@code ?}).
 * Equivalence with the old {@code addFuel} path therefore rests on the call being byte-identical, not on a
 * golden diff. Per user decision 6, the caller (Phase 5) is responsible for <em>which</em> blocks get this
 * trait: the old {@code addFuel} guarded on {@code source.ignitedByLava()}; the trait itself is
 * unconditional, so the guard becomes an explicit per-site {@code .addTrait(...)} decision.
 */
public class FuelBlockTrait extends BlockTraitImpl<Block, GenericBlockTrait> implements GenericBlockTrait {
    public static final BlockTraitKey KEY = BlockTraitKey.ofUnique(LibWoverBlock.C, "fuel");

    /**
     * The burn time (in ticks).
     */
    public static final int DEFAULT_TICKS = 200;

    public enum DefaultFuelTicks {
        BASE(1, 1),
        LOGS(3, 2),
        SLABS(3, 4),
        HALF(1, 2),
        QUADRUPEL(4, 1),
        /**
         * A lump of coal: 1600 ticks. For a burnable mineral drop that is meant to trade like coal
         * rather than like wood - every wood tier above tops out at 800.
         */
        COAL(8, 1),
        /** A block of coal: 16000 ticks, the nine-lump block rate. */
        COAL_BLOCK(80, 1);

        public final FuelBlockTrait trait;

        DefaultFuelTicks(int mul, int div) {
            this.trait = new FuelBlockTrait((DEFAULT_TICKS * mul) / div);
        }
    }

    /**
     * The fuel trait with the standard {@link #DEFAULT_TICKS} (200 tick) burn time.
     *
     * @return the default fuel trait instance
     */
    public static FuelBlockTrait withDefault() {
        return DefaultFuelTicks.BASE.trait;
    }

    /**
     * A fuel trait with a custom burn time.
     *
     * @param ticks the burn time in ticks (as passed to {@code FuelRegistryBuilder.add})
     * @return the fuel trait instance
     */
    public static FuelBlockTrait withTicks(int ticks) {
        return ticks == DEFAULT_TICKS ? DefaultFuelTicks.BASE.trait : new FuelBlockTrait(ticks);
    }

    public final int ticks;

    private FuelBlockTrait(int ticks) {
        this.ticks = ticks;
    }

    @Override
    public BlockTraitKey key() {
        return KEY;
    }

    @Override
    public GenericBlockTrait forRuntime() {
        return this;
    }

    @Override
    public void afterBlockRegistration(
            Block block,
            BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition
    ) {
        FuelValueEvents.BUILD.register((builder, fuelContext) -> builder.add(block, ticks));
    }
}