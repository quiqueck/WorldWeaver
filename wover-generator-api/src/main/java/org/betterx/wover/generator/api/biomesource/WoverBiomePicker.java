package org.betterx.wover.generator.api.biomesource;

import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.entrypoint.WoverWorldGenerator;
import org.betterx.wover.state.api.WorldState;
import org.betterx.wover.util.RandomizedWeightedList;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.WorldgenRandom;

import java.util.*;
import java.util.function.BiConsumer;

public class WoverBiomePicker {
    private final Map<BiomeData, PickableBiome> registeredBiomes = new HashMap<>();
    public final HolderGetter<Biome> biomeRegistry;
    private final Set<PickableBiome> biomes = new HashSet<>();
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
        this.fallbackBiome = create(BiomeData.tempOf(fallbackBiome));
    }

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
                WoverWorldGenerator.C.log.verbose("Added " + (registeredBiomes.size() - beforeSize) + " Biomes");

                for (PickableBiome builtBiome : new ArrayList<>(registeredBiomes.values())) {
                    if (!beforeList.contains(builtBiome)) {
                        WoverWorldGenerator.C.log.verbose(" - " + builtBiome.biomeData.biomeKey.location() + ", subbiomes=" + builtBiome.subbiomes.size());
                    }
                }
            }
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
