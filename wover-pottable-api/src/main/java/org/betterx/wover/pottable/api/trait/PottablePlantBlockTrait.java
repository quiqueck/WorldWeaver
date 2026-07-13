package org.betterx.wover.pottable.api.trait;

import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitKey;
import org.betterx.wover.block.api.trait.GenericBlockTrait;
import org.betterx.wover.block.impl.trait.BlockTraitImpl;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverPottable;
import org.betterx.wover.pottable.api.PottablePlant;
import org.betterx.wover.pottable.api.PottablePlantRegistry;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;

/**
 * Marks a block as a plant that can be potted (e.g. inside a flower pot).
 * <p>
 * Attach with {@code .addTrait(PottablePlantBlockTrait.withSoils(tag))} (or
 * {@link #any()} if the plant may be potted on any registered soil) at the block's
 * normal registration site. Blocks carrying this trait are picked up automatically by
 * {@link org.betterx.wover.pottable.api.datagen.WoverPottablePlantRegistryProvider} -
 * no custom datagen code is needed.
 */
public class PottablePlantBlockTrait extends BlockTraitImpl<Block, GenericBlockTrait> implements GenericBlockTrait {
    /**
     * The unique key identifying this trait.
     */
    public static final BlockTraitKey KEY = BlockTraitKey.ofUnique(LibWoverPottable.C, "pottable_plant");

    private static final PottablePlantBlockTrait ANY = new PottablePlantBlockTrait(Optional.empty());
    private static final Map<TagKey<Block>, PottablePlantBlockTrait> TAG_CACHE = new HashMap<>();

    /**
     * The soils this plant can be potted on. If empty, the plant can be potted on any
     * registered soil.
     */
    public final Optional<TagKey<Block>> validSoils;

    /**
     * The plant can be potted on any registered soil.
     *
     * @return The trait instance.
     */
    public static PottablePlantBlockTrait any() {
        return ANY;
    }

    /**
     * The plant can only be potted on soils matching the given tag.
     *
     * @param validSoils The tag of valid soil blocks.
     * @return The trait instance.
     */
    public static PottablePlantBlockTrait withSoils(TagKey<Block> validSoils) {
        return TAG_CACHE.computeIfAbsent(validSoils, tag -> new PottablePlantBlockTrait(Optional.of(tag)));
    }

    private PottablePlantBlockTrait(Optional<TagKey<Block>> validSoils) {
        this.validSoils = validSoils;
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
     * Registers a {@link PottablePlant} for every block of the given mod that carries
     * this trait.
     *
     * @param modCore The mod whose blocks should be scanned.
     * @param ctx     The bootstrap context to register into.
     * @param filter  A filter to restrict which blocks are considered.
     */
    public static void bootstrapPottablePlants(
            ModCore modCore,
            BootstrapContext<PottablePlant> ctx,
            BiPredicate<ResourceKey<Block>, Block> filter
    ) {
        BlockRegistry
                .forMod(modCore)
                .allEntries()
                .filter(e -> filter.test(e.getKey(), e.getValue()))
                .forEach(e -> {
                    var runtimeTraits = BlockTrait.<Block, GenericBlockTrait>getRuntimeTraits(e.getValue(), KEY);
                    if (runtimeTraits == null) return;
                    runtimeTraits.forEach(trait -> {
                        if (!(trait instanceof PottablePlantBlockTrait plantTrait)) return;
                        PottablePlantRegistry.register(
                                ctx,
                                PottablePlantRegistry.createKey(e.getKey().location()),
                                e.getKey(),
                                plantTrait.validSoils
                        );
                    });
                });
    }
}
