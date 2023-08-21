package org.betterx.wover.generator.api.biomesource;

import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.state.api.WorldState;
import org.betterx.wover.util.RandomizedWeightedList;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.WorldgenRandom;

import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WoverBiomePicker {
    private final Map<BiomeData, PickableBiome> registeredBiomes = new HashMap<>();
    public final HolderGetter<Biome> biomeRegistry;
    private final List<PickableBiome> biomes = Lists.newArrayList();
    public final PickableBiome fallbackBiome;
    private RandomizedWeightedList<PickableBiome>.SearchTree tree;

    public WoverBiomePicker(ResourceKey<Biome> fallbackBiome) {
        this(
                WorldState.allStageRegistryAccess() == null
                        ? null
                        : WorldState.allStageRegistryAccess().registry(Registries.BIOME).orElse(null),
                fallbackBiome
        );
    }

    public WoverBiomePicker(Registry<Biome> biomeRegistry, ResourceKey<Biome> fallbackBiome) {
        this(biomeRegistry != null ? biomeRegistry.asLookup() : null, fallbackBiome);
    }

    public WoverBiomePicker(HolderGetter<Biome> biomeRegistry, ResourceKey<Biome> fallbackBiome) {
        this.biomeRegistry = biomeRegistry;
        this.fallbackBiome = create(BiomeData.of(fallbackBiome));
    }

    private boolean isAllowed(BiomeData biomeData) {
        if (biomeData == null) return false;
        return biomeData.isPickable();
    }

    private BiomeData nullIfNotAllowed(BiomeData biomeData) {
        return isAllowed(biomeData) ? biomeData : null;
    }

    private PickableBiome create(BiomeData biomeData) {
        if (biomeData == null) return null;
        PickableBiome e = registeredBiomes.get(biomeData);
        if (e != null) return e;
        return new PickableBiome(biomeData);
    }

    public void addBiome(BiomeData biome) {
        if (isAllowed(biome))
            biomes.add(create(biome));
    }

    public PickableBiome getBiome(WorldgenRandom random) {
        return tree.getRandomValue(random);
    }

    public boolean isEmpty() {
        return biomes.isEmpty();
    }

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

        //we do not actually want the list, but the search tree for it
        tree = list.buildSearchTree();
    }

    public class PickableBiome {
        public final BiomeData biomeData;
        public final Holder<Biome> biome;

        private final RandomizedWeightedList<PickableBiome> subbiomes;
        public final PickableBiome edge;
        public final PickableBiome parent;
        public final boolean isValid;
        public final int edgeSize;
        public final boolean isVertical;

        private PickableBiome(BiomeData biomeData) {
            registeredBiomes.put(biomeData, this);

            this.biomeData = biomeData;

            this.biome = (biomeRegistry != null) ? biomeRegistry.getOrThrow(biomeData.biomeKey) : null;
            this.isValid = biome != null && biome.isBound();

            if (biomeData instanceof WoverBiomeData wData) {
                subbiomes = wData
                        .createBiomeAlternatives(WoverBiomePicker.this::isAllowed)
                        .map(WoverBiomePicker.this::create);

                edge = create(nullIfNotAllowed(wData.getEdgeData()));
                parent = create(wData.getParentData());
                edgeSize = wData.edgeSize;
                isVertical = wData.vertical;
            } else {
                subbiomes = RandomizedWeightedList.of(this);
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

        public PickableBiome getSubBiome(WorldgenRandom random) {
            return subbiomes.getRandomValue(random);
        }

        public PickableBiome getEdge() {
            return edge;
        }

        public PickableBiome getParentBiome() {
            return parent;
        }

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
}
