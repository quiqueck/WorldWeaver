package de.ambertation.wover.feature.api.placed.modifiers;

import de.ambertation.wover.feature.impl.placed.modifiers.PlacementModifiersImpl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.List;
import java.util.Optional;

/**
 * Tests the {@link Biome} at the input position against a list of {@link Identifier}s.
 * <p>
 * The position is accepted if the biome at the input position matches (or, if {@code negate} is
 * {@code true}, does not match) one of the given biome ids.
 */
public class InBiome extends PlacementFilter {
    /**
     * Codec for this placement modifier.
     */
    public static final MapCodec<InBiome> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
            .group(
                    Codec.BOOL
                            .fieldOf("negate")
                            .orElse(false)
                            .forGetter(cfg -> cfg.negate),
                    Codec.list(Identifier.CODEC)
                         .fieldOf("biomes")
                         .forGetter(cfg -> cfg.biomeIDs)
            )
            .apply(instance, InBiome::new));

    /**
     * The ids of the biomes to test against.
     */
    public final List<Identifier> biomeIDs;
    /**
     * If {@code true}, the test result is inverted: the position is accepted if the biome is
     * <em>not</em> one of {@link #biomeIDs}.
     */
    public final boolean negate;

    /**
     * Constructs a new instance.
     *
     * @param negate   if {@code true}, the test result is inverted
     * @param biomeIDs the ids of the biomes to test against
     */
    protected InBiome(boolean negate, List<Identifier> biomeIDs) {
        this.biomeIDs = biomeIDs;
        this.negate = negate;
    }

    /**
     * Constructs a modifier that accepts positions whose biome matches one of the given ids.
     *
     * @param id the biome ids to match
     * @return a new instance
     */
    public static InBiome matchingID(Identifier... id) {
        return new InBiome(false, List.of(id));
    }

    /**
     * Constructs a modifier that accepts positions whose biome matches one of the given ids.
     *
     * @param ids the biome ids to match
     * @return a new instance
     */
    public static InBiome matchingID(List<Identifier> ids) {
        return new InBiome(false, ids);
    }

    /**
     * Constructs a modifier that accepts positions whose biome does not match any of the given ids.
     *
     * @param id the biome ids to reject
     * @return a new instance
     */
    public static InBiome notMatchingID(Identifier... id) {
        return new InBiome(true, List.of(id));
    }

    /**
     * Constructs a modifier that accepts positions whose biome does not match any of the given ids.
     *
     * @param ids the biome ids to reject
     * @return a new instance
     */
    public static InBiome notMatchingID(List<Identifier> ids) {
        return new InBiome(true, ids);
    }

    /**
     * Tests the biome at the input position against {@link #biomeIDs}.
     *
     * @param ctx    The placement context
     * @param random The random source
     * @param pos    The input position
     * @return {@code true} if the biome at {@code pos} matches (or, if {@link #negate} is {@code true},
     * does not match) one of {@link #biomeIDs}
     */
    @Override
    protected boolean shouldPlace(PlacementContext ctx, RandomSource random, BlockPos pos) {
        Holder<Biome> holder = ctx.getLevel().getBiome(pos);
        Optional<Identifier> biomeLocation = holder.unwrapKey().map(key -> key.identifier());
        if (biomeLocation.isPresent()) {
            boolean contains = biomeIDs.contains(biomeLocation.get());
            return negate != contains;
        }
        return false;
    }

    /**
     * Gets the type of this placement modifier.
     *
     * @return the type of this placement modifier
     */
    @Override
    public PlacementModifierType<InBiome> type() {
        return PlacementModifiersImpl.IN_BIOME;
    }
}
