package de.ambertation.wover.pottable.api;

import de.ambertation.wover.core.api.registry.DatapackRegistryBuilder;
import de.ambertation.wover.entrypoint.LibWoverPottable;
import de.ambertation.wover.pottable.impl.PottablePlantRegistryImpl;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;

/**
 * Provides a Datapack driven registry of plants that can be potted (e.g. inside a
 * flower pot).
 * <p>
 * The Datapack files should be stored as {@code data/<namespace>/wover/pottable_plant/<name>.json}.
 *
 * @see PottableSoilRegistry
 */
public class PottablePlantRegistry {
    private PottablePlantRegistry() {
    }

    /**
     * The Key of the Registry. ({@code wover/pottable_plant})
     */
    public static final ResourceKey<Registry<PottablePlant>> POTTABLE_PLANT_REGISTRY =
            DatapackRegistryBuilder.createRegistryKey(LibWoverPottable.C.id("wover/pottable_plant"));

    /**
     * Creates a ResourceKey for a PottablePlant.
     *
     * @param id The ID of the PottablePlant
     * @return The ResourceKey
     */
    public static ResourceKey<PottablePlant> createKey(ResourceLocation id) {
        return PottablePlantRegistryImpl.createKey(id);
    }

    /**
     * Registers a plant as pottable.
     *
     * @param ctx        The Bootstrap Context
     * @param key        The ResourceKey of the PottablePlant
     * @param block      The plant block
     * @param validSoils The soils this plant can be potted on. If empty, the plant can
     *                    be potted on any registered {@link PottableSoil}.
     * @return A Holder for the PottablePlant
     */
    public static Holder<PottablePlant> register(
            @NotNull BootstrapContext<PottablePlant> ctx,
            @NotNull ResourceKey<PottablePlant> key,
            @NotNull ResourceKey<Block> block,
            @NotNull Optional<TagKey<Block>> validSoils
    ) {
        return PottablePlantRegistryImpl.register(ctx, key, block, validSoils);
    }

    /**
     * Registers a plant as pottable, restricted to a specific soil tag.
     *
     * @param ctx        The Bootstrap Context
     * @param key        The ResourceKey of the PottablePlant
     * @param block      The plant block
     * @param validSoils The soils this plant can be potted on
     * @return A Holder for the PottablePlant
     */
    public static Holder<PottablePlant> register(
            @NotNull BootstrapContext<PottablePlant> ctx,
            @NotNull ResourceKey<PottablePlant> key,
            @NotNull Block block,
            @NotNull TagKey<Block> validSoils
    ) {
        return register(ctx, key, block.builtInRegistryHolder().key(), Optional.of(validSoils));
    }

    /**
     * Registers a plant as pottable on any registered {@link PottableSoil}.
     *
     * @param ctx   The Bootstrap Context
     * @param key   The ResourceKey of the PottablePlant
     * @param block The plant block
     * @return A Holder for the PottablePlant
     */
    public static Holder<PottablePlant> register(
            @NotNull BootstrapContext<PottablePlant> ctx,
            @NotNull ResourceKey<PottablePlant> key,
            @NotNull Block block
    ) {
        return register(ctx, key, block.builtInRegistryHolder().key(), Optional.empty());
    }
}
