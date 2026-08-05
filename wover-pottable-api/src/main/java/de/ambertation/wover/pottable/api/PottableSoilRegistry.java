package de.ambertation.wover.pottable.api;

import de.ambertation.wover.core.api.registry.DatapackRegistryBuilder;
import de.ambertation.wover.entrypoint.LibWoverPottable;
import de.ambertation.wover.pottable.impl.PottableSoilRegistryImpl;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

/**
 * Provides a Datapack driven registry of blocks that can be used as soil for a
 * {@link PottablePlant} (e.g. inside a flower pot).
 * <p>
 * The Datapack files should be stored as {@code data/<namespace>/wover/pottable_soil/<name>.json}.
 *
 * @see PottablePlantRegistry
 */
public class PottableSoilRegistry {
    private PottableSoilRegistry() {
    }

    /**
     * The Key of the Registry. ({@code wover/pottable_soil})
     */
    public static final ResourceKey<Registry<PottableSoil>> POTTABLE_SOIL_REGISTRY =
            DatapackRegistryBuilder.createRegistryKey(LibWoverPottable.C.id("wover/pottable_soil"));

    /**
     * Creates a ResourceKey for a PottableSoil.
     *
     * @param id The ID of the PottableSoil
     * @return The ResourceKey
     */
    public static ResourceKey<PottableSoil> createKey(Identifier id) {
        return PottableSoilRegistryImpl.createKey(id);
    }

    /**
     * Registers a block as usable soil.
     *
     * @param ctx   The Bootstrap Context
     * @param key   The ResourceKey of the PottableSoil
     * @param block The soil block
     * @return A Holder for the PottableSoil
     */
    public static Holder<PottableSoil> register(
            @NotNull BootstrapContext<PottableSoil> ctx,
            @NotNull ResourceKey<PottableSoil> key,
            @NotNull ResourceKey<Block> block
    ) {
        return PottableSoilRegistryImpl.register(ctx, key, block);
    }

    /**
     * Registers a block as usable soil.
     *
     * @param ctx   The Bootstrap Context
     * @param key   The ResourceKey of the PottableSoil
     * @param block The soil block
     * @return A Holder for the PottableSoil
     */
    public static Holder<PottableSoil> register(
            @NotNull BootstrapContext<PottableSoil> ctx,
            @NotNull ResourceKey<PottableSoil> key,
            @NotNull Block block
    ) {
        return register(ctx, key, block.builtInRegistryHolder().key());
    }
}
