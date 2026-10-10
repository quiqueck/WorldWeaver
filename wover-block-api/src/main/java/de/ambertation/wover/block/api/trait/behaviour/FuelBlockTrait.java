package de.ambertation.wover.block.api.trait.behaviour;

import de.ambertation.wover.block.api.trait.BlockTraitKey;
import de.ambertation.wover.block.api.trait.GenericBlockTrait;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.entrypoint.LibWoverBlock;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProvider;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;

/**
 * A behaviour trait that makes the block's item a furnace fuel with a fixed burn time.
 * <p>
 * This mirrors the (now-removed) {@code NetherBlocks.addFuel(source, result)} helper, which registered a
 * fixed burn time via Fabric's (now also removed) {@code FuelValueEvents.BUILD}. 26.3 replaced both the
 * Fabric event and vanilla's static burn-time map with a data-driven {@code minecraft:cooking_fuel} item
 * component ({@link CookingFuel}) whose {@code burnTime}/{@code speedMultiplier} are {@link ResourceKey}s
 * into the new number provider datapack registries ({@code minecraft:context_int_provider} for the burn time, {@code minecraft:context_float_provider} for the speed multiplier; a single {@code minecraft:number_provider} registry in the 26.3 snapshots), applied to {@code Item.Properties} at
 * item-construction time (see {@code BlockDefinition#buildAndRegister()}) rather than as a post-registration
 * event.
 * <p>
 * <b>Burn-time source:</b> BetterNether registered every slab/stairs/roof/button/plate/etc. fuel with a
 * hard-coded {@code 40} ticks (the {@code addFuel} body); {@link #DEFAULT_TICKS} / {@link #withDefault()}
 * still emit that value. The {@link DefaultFuelTicks} tiers are wired to vanilla's own built-in
 * {@code cooking/time_*} number providers, which happen to carry exactly these tick values already (e.g.
 * {@code cooking/time_wood_items_large} = 200, {@code cooking/time_hanging_signs} = 800) - so no new
 * registry entries need to be bootstrapped for the presets this trait ships with.
 * <p>
 * <b>Not verifiable by datagen:</b> furnace fuel is a runtime-only registration and is <em>not</em>
 * reflected in any golden datapack file (the {@code fuel=} column in the registrations golden reads the raw
 * {@link #ticks} value for informational purposes only). Per user decision 6, the caller (Phase 5) is
 * responsible for <em>which</em> blocks get this trait: the old {@code addFuel} guarded on
 * {@code source.ignitedByLava()}; the trait itself is unconditional, so the guard becomes an explicit
 * per-site {@code .addTrait(...)} decision.
 */
public class FuelBlockTrait extends BlockTraitImpl<Block, GenericBlockTrait> implements GenericBlockTrait {
    public static final BlockTraitKey KEY = BlockTraitKey.ofUnique(LibWoverBlock.C, "fuel");

    /**
     * The default cook-speed multiplier provider vanilla uses for every fuel: {@code 1.0} in a regular
     * furnace, {@code 2.0} in a blast furnace/smoker (so fuel is consumed twice as fast to match the
     * doubled cook speed).
     */
    private static final ResourceKey<ContextFloatProvider> SPEED_DEFAULT =
            ContextFloatProviders.COOKING_DEFAULT_SPEED_MULTIPLIER;

    /**
     * The burn time (in ticks).
     */
    public static final int DEFAULT_TICKS = 200;

    public enum DefaultFuelTicks {
        BASE(1, 1, "cooking/time_wood_items_large"),
        LOGS(3, 2, "cooking/time_wood_blocks"),
        SLABS(3, 4, "cooking/time_wood_slabs"),
        HALF(1, 2, "cooking/time_wood_items_extra_small"),
        QUADRUPEL(4, 1, "cooking/time_hanging_signs"),
        /**
         * A lump of coal: 1600 ticks. For a burnable mineral drop that is meant to trade like coal
         * rather than like wood - every wood tier above tops out at 800.
         */
        COAL(8, 1, "cooking/time_coal"),
        /** A block of coal: 16000 ticks, the nine-lump block rate. */
        COAL_BLOCK(80, 1, "cooking/time_coal_block");

        public final FuelBlockTrait trait;

        DefaultFuelTicks(int mul, int div, String vanillaBurnTimeProvider) {
            this.trait = new FuelBlockTrait(
                    (DEFAULT_TICKS * mul) / div,
                    ResourceKey.create(Registries.CONTEXT_INT_PROVIDER, Identifier.withDefaultNamespace(vanillaBurnTimeProvider))
            );
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
     * A fuel trait using one of the preset {@link DefaultFuelTicks} tiers, each backed by a vanilla
     * {@code cooking/time_*} number provider carrying that exact tick value.
     *
     * @param ticks the burn time in ticks; must match one of {@link DefaultFuelTicks}' values
     * @return the fuel trait instance
     * @throws IllegalArgumentException if {@code ticks} doesn't match a known preset - 26.3's fuel value
     *                                   must reference a registered {@code ContextIntProvider} and this trait
     *                                   does not (yet) bootstrap custom ones
     */
    public static FuelBlockTrait withTicks(int ticks) {
        for (DefaultFuelTicks preset : DefaultFuelTicks.values()) {
            if (preset.trait.ticks == ticks) return preset.trait;
        }
        throw new IllegalArgumentException(
                "No vanilla ContextIntProvider is known for " + ticks
                        + " fuel ticks; use one of the DefaultFuelTicks presets");
    }

    public final int ticks;
    public final ResourceKey<ContextIntProvider> burnTime;

    private FuelBlockTrait(int ticks, ResourceKey<ContextIntProvider> burnTime) {
        this.ticks = ticks;
        this.burnTime = burnTime;
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
     * The {@code minecraft:cooking_fuel} item component this trait contributes to the block's item.
     *
     * @return the component data
     */
    public CookingFuel asCookingFuelComponent() {
        return new CookingFuel(burnTime, SPEED_DEFAULT);
    }
}