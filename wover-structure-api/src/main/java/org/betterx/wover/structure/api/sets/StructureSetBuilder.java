package org.betterx.wover.structure.api.sets;

import org.betterx.wover.structure.api.StructureKey;
import org.betterx.wover.structure.api.builders.BaseStructureBuilder;
import org.betterx.wover.util.Pair;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StructureSetBuilder {
    @NotNull
    private final ResourceKey<StructureSet> key;
    @NotNull
    private final BootstapContext<StructureSet> context;

    private final List<Pair<ResourceKey<Structure>, Integer>> structures = new LinkedList<>();
    private StructurePlacement placement;


    StructureSetBuilder(@NotNull ResourceKey<StructureSet> key, @NotNull BootstapContext<StructureSet> context) {
        this.key = key;
        this.context = context;
    }

    public StructureSetBuilder addStructure(ResourceKey<Structure> structure, int weight) {
        structures.add(new Pair<>(structure, weight));
        return this;
    }

    public StructureSetBuilder addStructure(ResourceKey<Structure> structure) {
        return addStructure(structure, 1);
    }

    public <S extends Structure, T extends BaseStructureBuilder<S, T>> StructureSetBuilder addStructure(
            StructureKey<S, T> structure,
            int weight
    ) {
        return addStructure(structure.key, weight);
    }

    public <S extends Structure, T extends BaseStructureBuilder<S, T>> StructureSetBuilder addStructure(
            StructureKey<S, T> structure
    ) {
        return addStructure(structure, 1);
    }

    public RandomSpreadStructurePlacementBuilder randomPlacement() {
        return new RandomSpreadStructurePlacementBuilder(key);
    }

    public ConcentricRingsStructurePlacementBuilder concentricPlacement() {
        return new ConcentricRingsStructurePlacementBuilder(context, key);
    }

    public StructureSetBuilder setPlacement(StructurePlacement p) {
        this.placement = p;
        return this;
    }


    /**
     * Registers the {@link StructureSet} with the currently active
     * {@link net.minecraft.data.worldgen.BootstapContext}.
     * <p>
     * Will fail if either the key of this Feature or the {@link net.minecraft.data.worldgen.BootstapContext}
     * are null.
     *
     * @return the holder
     */
    public Holder<StructureSet> register() {
        return context.register(key, build());
    }

    /**
     * Creates an unnamed {@link Holder} for this {@link StructureSetBuilder}.
     * <p>
     * This method is useful, if you want to create an anonymous {@link StructureSet}
     * that is directly inlined
     *
     * @return the holder
     */
    public Holder<StructureSet> directHolder() {
        return Holder.direct(build());
    }

    private StructureSet build() {
        if (structures.isEmpty()) {
            throw new IllegalStateException("StructureSet " + key.location() + " must contain at least one structure");
        }
        if (placement == null) {
            throw new IllegalStateException("StructureSet " + key.location() + " define a placement");
        }

        final HolderGetter<Structure> structureRegistry = context.lookup(Registries.STRUCTURE);

        if (structures.size() == 1) {
            final Holder<Structure> holder = structureRegistry.getOrThrow(structures.get(0).first);
            return new StructureSet(holder, placement);
        } else {
            return new StructureSet(
                    structures.stream()
                              .map(p -> StructureSet.entry(structureRegistry.getOrThrow(p.first), p.second))
                              .toList(),
                    placement
            );
        }


    }

    public abstract class StructurePlacementBuilder<R extends StructurePlacementBuilder<R>> {
        protected Vec3i locateOffset;
        protected StructurePlacement.FrequencyReductionMethod frequencyReductionMethod;
        protected float frequency;
        protected int salt;
        protected Optional<StructurePlacement.ExclusionZone> exclusionZone;

        public abstract StructureSetBuilder setPlacement();

        @NotNull
        public R locateOffset(Vec3i offset) {
            this.locateOffset = offset;
            return (R) this;
        }

        @NotNull
        public R frequencyReductionMethod(@NotNull StructurePlacement.FrequencyReductionMethod method) {
            this.frequencyReductionMethod = method;
            return (R) this;
        }

        @NotNull
        public R frequency(float frequency) {
            this.frequency = frequency;
            return (R) this;
        }

        @NotNull
        public R salt(int salt) {
            this.salt = salt;
            return (R) this;
        }

        @NotNull
        public R exclusionZone(@Nullable StructurePlacement.ExclusionZone exclusionZone) {
            this.exclusionZone = exclusionZone == null ? Optional.empty() : Optional.of(exclusionZone);
            return (R) this;
        }

        protected StructurePlacementBuilder(@NotNull ResourceKey<StructureSet> baseKey) {
            locateOffset = Vec3i.ZERO;
            frequencyReductionMethod = StructurePlacement.FrequencyReductionMethod.DEFAULT;
            frequency = 1.0f;
            exclusionZone = Optional.empty();

            salt = baseKey.location().hashCode();
        }
    }

    public class RandomSpreadStructurePlacementBuilder extends StructurePlacementBuilder<RandomSpreadStructurePlacementBuilder> {
        protected int spacing;
        protected int separation;
        protected RandomSpreadType spreadType;

        RandomSpreadStructurePlacementBuilder(@NotNull ResourceKey<StructureSet> baseKey) {
            super(baseKey);
            spacing = 32;
            separation = 8;
            spreadType = RandomSpreadType.LINEAR;
        }

        @NotNull
        public RandomSpreadStructurePlacementBuilder spacing(int spacing) {
            this.spacing = spacing;
            return this;
        }

        @NotNull
        public RandomSpreadStructurePlacementBuilder separation(int separation) {
            this.separation = separation;
            return this;
        }

        @NotNull
        public RandomSpreadStructurePlacementBuilder spreadType(@NotNull RandomSpreadType spreadType) {
            this.spreadType = spreadType;
            return this;
        }

        @Override
        public StructureSetBuilder setPlacement() {
            return StructureSetBuilder.this.setPlacement(new RandomSpreadStructurePlacement(
                    locateOffset,
                    frequencyReductionMethod,
                    frequency,
                    salt,
                    exclusionZone,
                    spacing,
                    separation,
                    spreadType
            ));
        }
    }

    public class ConcentricRingsStructurePlacementBuilder extends StructurePlacementBuilder<ConcentricRingsStructurePlacementBuilder> {
        protected int distance;
        protected int spread;
        protected int count;
        @NotNull
        protected TagKey<Biome> preferredBiomes;
        @NotNull
        private final BootstapContext<StructureSet> context;

        ConcentricRingsStructurePlacementBuilder(
                @NotNull BootstapContext<StructureSet> context,
                @NotNull ResourceKey<StructureSet> baseKey
        ) {
            super(baseKey);
            this.context = context;
            distance = 32;
            spread = 3;
            count = 128;
            preferredBiomes = BiomeTags.STRONGHOLD_BIASED_TO;
        }

        @NotNull
        public ConcentricRingsStructurePlacementBuilder distance(int distance) {
            this.distance = distance;
            return this;
        }

        @NotNull
        public ConcentricRingsStructurePlacementBuilder spread(int spread) {
            this.spread = spread;
            return this;
        }

        @NotNull
        public ConcentricRingsStructurePlacementBuilder count(int count) {
            this.count = count;
            return this;
        }

        @NotNull
        public ConcentricRingsStructurePlacementBuilder preferredBiomes(@NotNull TagKey<Biome> preferredBiomes) {
            this.preferredBiomes = preferredBiomes;
            return this;
        }

        @Override
        public StructureSetBuilder setPlacement() {
            return StructureSetBuilder.this.setPlacement(new ConcentricRingsStructurePlacement(
                    locateOffset,
                    frequencyReductionMethod,
                    frequency,
                    salt,
                    exclusionZone,
                    distance,
                    spread,
                    count,
                    context.lookup(Registries.BIOME).getOrThrow(preferredBiomes)
            ));
        }
    }
}
