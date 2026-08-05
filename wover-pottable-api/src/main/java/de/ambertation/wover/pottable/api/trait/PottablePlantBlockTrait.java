package de.ambertation.wover.pottable.api.trait;

import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockTraitKey;
import de.ambertation.wover.block.api.trait.GenericBlockTrait;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverPottable;
import de.ambertation.wover.pottable.api.PottablePlant;
import de.ambertation.wover.pottable.api.PottablePlantRegistry;

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
 * {@link de.ambertation.wover.pottable.api.datagen.WoverPottablePlantRegistryProvider} -
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

    /**
     * A block can only ever have a single {@link PottablePlant} entry, so only the most recently added
     * trait is kept.
     * <p>
     * The {@link net.minecraft.resources.ResourceKey} of a {@link PottablePlant} is derived <em>solely</em>
     * from the block's own id (see {@link #bootstrapPottablePlants}), so two traits on one block cannot
     * become two registry entries - they become two registrations under the <em>same</em> key. That is not
     * loud: {@code DatapackRegistryBuilder}'s {@code BootstrapContext} skips a key that is already bound and
     * returns the existing holder, so the <em>first</em> registration silently wins and every later one is
     * dropped. For this trait that is the worst possible tie-break, because
     * {@code .addTrait(any()).addTrait(withSoils(tag))} would keep the unrestricted {@code any()} entry and
     * quietly discard the narrower restriction the author added afterwards - the block ends up pottable on
     * <em>more</em> soils than asked for, with nothing logged.
     * <p>
     * Note {@link BlockTraitKey#ofUnique} does <em>not</em> prevent the situation: it only guarantees the key
     * <em>object</em> is created once globally, not that a trait carrying it is attached once per block. And
     * {@link BlockTrait#keepLatestOnly()} defaults to {@code false}. Declaring it {@code true} here makes the
     * combination well-defined at the point it is written - {@code BlockDefinition#addTrait} drops the
     * superseded trait, so the last call decides which soils apply.
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

                    // Register at most ONE entry per block. The PottablePlant's ResourceKey comes only from
                    // the block id, and the BootstrapContext keeps the FIRST registration for a key and
                    // silently ignores later ones - so looping and registering per trait would quietly
                    // publish the earliest trait instead of the intended one. keepLatestOnly() already
                    // collapses the traits at BlockDefinition#addTrait time, so this normally sees a single
                    // element; taking the last match keeps the outcome "latest wins" even if a trait ever
                    // reached the block through some other route.
                    PottablePlantBlockTrait plantTrait = null;
                    for (var trait : runtimeTraits) {
                        if (trait instanceof PottablePlantBlockTrait t) plantTrait = t;
                    }
                    if (plantTrait == null) return;

                    PottablePlantRegistry.register(
                            ctx,
                            PottablePlantRegistry.createKey(e.getKey().identifier()),
                            e.getKey(),
                            plantTrait.validSoils
                    );
                });
    }
}
