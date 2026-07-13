package org.betterx.wover.generator.api.biomesource;

import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.entrypoint.LibWoverWorldGenerator;
import org.betterx.wover.state.api.WorldState;
import org.betterx.wover.util.RandomizedWeightedList;

import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.WorldgenRandom;

import java.util.*;
import java.util.function.BiConsumer;
import org.jetbrains.annotations.Nullable;

/**
 * Picks a {@link Biome} for a single "slot" (e.g. one {@link net.minecraft.tags.TagKey} in
 * {@link WoverBiomeSource#acceptedTags()}) of a {@link WoverBiomeSource}, weighted by
 * {@link BiomeData#genChance()}.
 * <p>
 * Biomes are added with {@link #addBiome(BiomeData)}; call {@link #rebuild()} once all biomes were added to
 * build the internal weighted search tree used by {@link #getBiome(WorldgenRandom)}. Each accepted
 * {@link BiomeData} is wrapped in a {@link PickableBiome}, which additionally resolves the
 * {@link WoverBiomeData#edge}/{@link WoverBiomeData#parent} relationships (edge biomes and sub-biomes) if
 * the underlying data is a {@link WoverBiomeData}.
 */
public class WoverBiomePicker {
    private final Map<BiomeData, PickableBiome> registeredBiomes = new HashMap<>();
    /**
     * The Biome registry used to resolve {@link BiomeData#biomeKey} into {@link Holder}s.
     */
    public final HolderGetter<Biome> biomeRegistry;
    private final Set<PickableBiome> biomes = new HashSet<>();
    /**
     * The Biome used whenever no other Biome could be picked (e.g. because no biomes were added yet).
     */
    public final PickableBiome fallbackBiome;
    private RandomizedWeightedList<PickableBiome>.SearchTree tree;

    /**
     * Creates a new picker, resolving the Biome registry from {@link WorldState#allStageRegistryAccess()}.
     *
     * @param fallbackBiome the Biome to use whenever no other Biome could be picked
     */
    public WoverBiomePicker(ResourceKey<Biome> fallbackBiome) {
        this(
                WorldState.allStageRegistryAccess() == null
                        ? null
                        : WorldState.allStageRegistryAccess().lookup(Registries.BIOME).orElse(null),
                fallbackBiome
        );
    }

    /**
     * Creates a new picker.
     *
     * @param biomeRegistry the Biome registry used to resolve {@link BiomeData#biomeKey} into
     *                      {@link Holder}s
     * @param fallbackBiome the Biome to use whenever no other Biome could be picked
     */
    public WoverBiomePicker(HolderLookup<Biome> biomeRegistry, ResourceKey<Biome> fallbackBiome) {
        this.biomeRegistry = biomeRegistry;
        this.fallbackBiome = create(BiomeData.tempOf(fallbackBiome));
    }

    /**
     * Enumerates the sub-biomes of {@code sourceBiome}, i.e. every {@link WoverBiomeData} in the
     * {@link BiomeData} registry whose {@link WoverBiomeData#parent} is {@code sourceBiome}.
     *
     * @param sourceBiome  the Biome to find sub-biomes (alternatives) for
     * @param consumeChild called with each matching sub-biome and its {@link WoverBiomeData#genChance}
     */
    public static void consumeSubBiomesForSource(
            BiomeData sourceBiome,
            BiConsumer<BiomeData, Float> consumeChild
    ) {
        final Registry<BiomeData> reg = WoverBiomeData.getDataRegistry("biome alternatives", sourceBiome.biomeKey);

        for (Map.Entry<ResourceKey<BiomeData>, BiomeData> entry : reg.entrySet()) {
            if (entry.getValue() instanceof WoverBiomeData b
                    && sourceBiome.isSame(b.parent)
            ) {
                consumeChild.accept(b, b.genChance);
            }
        }
    }

    private boolean isAllowed(BiomeData biomeData) {
        if (biomeData == null) return false;
        return true;
    }

    private BiomeData nullIfNotAllowed(BiomeData biomeData) {
        return isAllowed(biomeData) ? biomeData : null;
    }

    private PickableBiome create(BiomeData biomeData) {
        if (biomeData == null) return null;
        PickableBiome e = registeredBiomes.get(biomeData);

        //the current instance is only a temporary object which we can replace with
        //real one now
        if (e != null && e.biomeData.isTemp() && !biomeData.isTemp()) {
            registeredBiomes.remove(e);
            e = null;
        }

        if (e != null) return e;
        return new PickableBiome(biomeData);
    }

    /**
     * Adds a Biome to this picker.
     *
     * @param biome the Biome's data. Ignored if {@code null}.
     */
    public void addBiome(BiomeData biome) {
        if (isAllowed(biome))
            biomes.add(create(biome));
    }

    /**
     * Picks a random Biome from this picker's search tree, weighted by {@link BiomeData#genChance()}.
     * <p>
     * {@link #rebuild()} must have been called at least once before this method is used.
     *
     * @param random the random source to pick with
     * @return the picked Biome
     */
    public PickableBiome getBiome(WorldgenRandom random) {
        return tree.getRandomValue(random);
    }

    /**
     * Checks whether any Biome was added to this picker with {@link #addBiome(BiomeData)}.
     *
     * @return {@code true} if no Biome was added yet
     */
    public boolean isEmpty() {
        return biomes.isEmpty();
    }

    /**
     * Rebuilds the weighted search tree used by {@link #getBiome(WorldgenRandom)} from the Biomes added so
     * far, resolving sub-biomes ({@link WoverBiomeData#parent}) transitively along the way. Falls back to
     * {@link #fallbackBiome} if no valid Biome was added.
     */
    public void rebuild() {
        final RandomizedWeightedList<PickableBiome> list = new RandomizedWeightedList<>();

        biomes.forEach(biome -> {
            if (biome.isValid)
                list.add(biome, biome.biomeData.genChance());
        });

        //no Biomes? Make sure we add at least one, otherwise bad things will happen
        if (list.isEmpty()) {
            list.add(fallbackBiome, 1);
        }

        //make sure we load all subBiomes as well
        if (WorldState.allStageRegistryAccess() != null) {
            ;
            final int beforeSize = registeredBiomes.size();
            final ArrayList<PickableBiome> beforeList = new ArrayList<>(registeredBiomes.values());
            for (PickableBiome builtBiome : beforeList) {
                consumeSubBiomesForSource(
                        builtBiome.biomeData,
                        (biomeData, weight) -> builtBiome.subbiomes.add(create(biomeData), weight)
                );
            }

            if (registeredBiomes.size() != beforeSize) {
                LibWoverWorldGenerator.C.log.verbose("Added " + (registeredBiomes.size() - beforeSize) + " Biomes");

                for (PickableBiome builtBiome : new ArrayList<>(registeredBiomes.values())) {
                    if (!beforeList.contains(builtBiome)) {
                        LibWoverWorldGenerator.C.log.verbose(" - " + builtBiome.biomeData.biomeKey.location() + ", subbiomes=" + builtBiome.subbiomes.size());
                    }
                }
            }
        }

        //we do not actually want the list, but the search tree for it
        tree = list.buildSearchTree();
    }

    /**
     * A single Biome registered with this picker, resolved to its {@link Holder} and (if the underlying
     * {@link BiomeData} is a {@link WoverBiomeData}) its edge/parent relationships.
     */
    public class PickableBiome {
        /**
         * The data of the wrapped Biome.
         */
        public final BiomeData biomeData;
        /**
         * The resolved Biome, or {@code null} if {@link #biomeRegistry} was {@code null}.
         */
        public final Holder<Biome> biome;

        private final RandomizedWeightedList<PickableBiome> subbiomes;
        /**
         * The edge biome that generates at the border of this Biome, or {@code null} if this Biome has no
         * edge.
         */
        public final PickableBiome edge;
        /**
         * The parent biome this Biome is a sub-biome (alternative) of, or {@code null} if this Biome is not
         * a sub-biome.
         */
        public final PickableBiome parent;
        /**
         * Whether {@link #biome} could be resolved and is bound.
         */
        public final boolean isValid;
        /**
         * The size of the {@link #edge} biome border.
         */
        public final int edgeSize;
        /**
         * Whether the {@link #edge} biome is a vertical (height-based) transition.
         */
        public final boolean isVertical;

        private PickableBiome(BiomeData biomeData) {
            registeredBiomes.put(biomeData, this);

            this.biomeData = biomeData;

            this.biome = (biomeRegistry != null) ? biomeRegistry.getOrThrow(biomeData.biomeKey) : null;
            this.isValid = biome != null && biome.isBound();

            this.subbiomes = new RandomizedWeightedList<>();

            if (biomeData instanceof WoverBiomeData wData) {
                subbiomes.add(this, wData.genChance);
                edge = create(nullIfNotAllowed(wData.getEdgeData()));
                parent = create(wData.getParentData());
                edgeSize = wData.edgeSize;
                isVertical = wData.vertical;
            } else {
                subbiomes.add(this, 1.0f);
                edge = null;
                parent = null;
                edgeSize = 0;
                isVertical = false;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PickableBiome entry = (PickableBiome) o;
            return biomeData.equals(entry.biomeData);
        }

        @Override
        public int hashCode() {
            return Objects.hash(biomeData);
        }

        /**
         * Picks a random sub-biome of this Biome (or this Biome itself), weighted by
         * {@link WoverBiomeData#genChance}.
         *
         * @param random the random source to pick with
         * @return the picked Biome
         */
        public PickableBiome getSubBiome(WorldgenRandom random) {
            return subbiomes.getRandomValue(random);
        }

        /**
         * @return {@link #edge}.
         */
        public PickableBiome getEdge() {
            return edge;
        }

        /**
         * @return {@link #parent}.
         */
        public PickableBiome getParentBiome() {
            return parent;
        }

        /**
         * Checks whether {@code e} wraps the same Biome as this instance.
         *
         * @param e the Biome to compare against
         * @return {@code true} if both wrap the same Biome
         */
        public boolean isSame(PickableBiome e) {
            return biomeData.isSame(e.biomeData);
        }

        @Override
        public String toString() {
            return "PickableBiome{" +
                    "key=" + biomeData.biomeKey.location() +
                    ", alternatives=" + subbiomes.size() +
                    ", edge=" + (edge != null ? edge.biomeData.biomeKey.location() : "null") +
                    ", parent=" + (parent != null ? parent.biomeData.biomeKey.location() : "null") +
                    ", isValid=" + isValid +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "BiomePicker{" +
                "biomes=" + biomes.size() + " (" + registeredBiomes.size() + ")" +
                ", biomeRegistry=" + biomeRegistry +
                ", type=" + super.toString() +
                '}';
    }


    /**
     * Looks up the Biome that was already generated for a block position, without triggering biome
     * generation for the containing chunk if it does not exist yet.
     *
     * @param world   the world to look up the Biome in
     * @param testPos the block position to look up
     * @return the Biome at {@code testPos}, or {@code null} if the containing chunk was not generated up to
     * {@link ChunkStatus#BIOMES} yet
     */
    public static @Nullable Holder<Biome> getBiomeAt(WorldGenLevel world, BlockPos testPos) {
        final ChunkPos chunkPos = new ChunkPos(testPos);
        final ChunkAccess chunk = world.getChunkSource().getChunk(chunkPos.x, chunkPos.z, ChunkStatus.BIOMES, false);
        if (chunk != null) {
            return chunk.getBiomeFabric(testPos);
        } else {
            return null;
        }
    }
}
