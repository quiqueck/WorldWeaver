package org.betterx.wover.structure.api.sets;

import org.betterx.wover.structure.api.StructureKey;
import org.betterx.wover.structure.api.builders.BaseStructureBuilder;
import org.betterx.wover.util.Pair;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
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

/**
 * A builder for {@link StructureSet}s. Created by calling
 * {@link StructureSetKey#bootstrap(BootstrapContext)} (or the {@link StructureSetManager#bootstrap}
 * shorthand, which also adds the structure).
 * <p>
 * A set needs at least one structure (added via {@link #addStructure}) and a placement (either
 * {@link #randomPlacement()}/{@link #randomPlacement(int, int)} for a
 * {@link RandomSpreadStructurePlacement}, {@link #concentricPlacement()} for a
 * {@link ConcentricRingsStructurePlacement}, or {@link #setPlacement(StructurePlacement)} for a
 * custom one) before it can be {@link #register() registered}.
 */
public class StructureSetBuilder {
    @NotNull
    private final ResourceKey<StructureSet> key;
    @NotNull
    private final BootstrapContext<StructureSet> context;

    private final List<Pair<ResourceKey<Structure>, Integer>> structures = new LinkedList<>();
    private StructurePlacement placement;


    StructureSetBuilder(@NotNull ResourceKey<StructureSet> key, @NotNull BootstrapContext<StructureSet> context) {
        this.key = key;
        this.context = context;
    }

    /**
     * Adds a {@link Structure} to this set with the given relative weight.
     *
     * @param structure The key of the structure to add
     * @param weight    The relative weight used when the game randomly picks which structure of the set
     *                  to generate at a given location
     * @return This builder instance, for chaining
     */
    public StructureSetBuilder addStructure(ResourceKey<Structure> structure, int weight) {
        structures.add(new Pair<>(structure, weight));
        return this;
    }

    /**
     * Adds a {@link Structure} to this set with a relative weight of {@code 1}.
     *
     * @param structure The key of the structure to add
     * @return This builder instance, for chaining
     */
    public StructureSetBuilder addStructure(ResourceKey<Structure> structure) {
        return addStructure(structure, 1);
    }

    /**
     * Alias for {@link #addStructure(ResourceKey, int)} that accepts a {@link StructureKey}.
     *
     * @param structure The structure to add
     * @param weight    The relative weight used when the game randomly picks which structure of the set
     *                  to generate at a given location
     * @param <S>       The {@link Structure} type
     * @param <T>       The Builder type
     * @param <K>       The {@link StructureKey} type
     * @return This builder instance, for chaining
     */
    public <S extends Structure, T extends BaseStructureBuilder<S, T>, K extends StructureKey<S, T, K>> StructureSetBuilder addStructure(
            K structure,
            int weight
    ) {
        return addStructure(structure.key(), weight);
    }

    /**
     * Alias for {@link #addStructure(ResourceKey)} that accepts a {@link StructureKey}.
     *
     * @param structure The structure to add
     * @param <S>       The {@link Structure} type
     * @param <T>       The Builder type
     * @param <K>       The {@link StructureKey} type
     * @return This builder instance, for chaining
     */
    public <S extends Structure, T extends BaseStructureBuilder<S, T>, K extends StructureKey<S, T, K>> StructureSetBuilder addStructure(
            K structure
    ) {
        return addStructure(structure, 1);
    }

    /**
     * Shorthand that creates and immediately finishes a {@link RandomSpreadStructurePlacementBuilder}
     * with the given spacing/separation. Equivalent to
     * {@code randomPlacement().spacing(spacing).separation(separation).finishPlacement()}.
     *
     * @param spacing    The average distance (in chunks) between two structures of this set
     * @param seperation The minimum distance (in chunks) between two structures of this set
     * @return This builder instance, for chaining
     */
    public StructureSetBuilder randomPlacement(int spacing, int seperation) {
        return randomPlacement().spacing(spacing).separation(seperation).finishPlacement();
    }

    /**
     * Starts building a {@link RandomSpreadStructurePlacement} for this set. Call
     * {@link RandomSpreadStructurePlacementBuilder#finishPlacement()} to apply it.
     *
     * @return A builder for the placement
     */
    public RandomSpreadStructurePlacementBuilder randomPlacement() {
        return new RandomSpreadStructurePlacementBuilder(key);
    }

    /**
     * Starts building a {@link ConcentricRingsStructurePlacement} for this set. Call
     * {@link ConcentricRingsStructurePlacementBuilder#finishPlacement()} to apply it.
     *
     * @return A builder for the placement
     */
    public ConcentricRingsStructurePlacementBuilder concentricPlacement() {
        return new ConcentricRingsStructurePlacementBuilder(context, key);
    }

    /**
     * Directly sets a pre-built {@link StructurePlacement} for this set, bypassing
     * {@link #randomPlacement()}/{@link #concentricPlacement()}.
     *
     * @param p The placement to use
     * @return This builder instance, for chaining
     */
    public StructureSetBuilder setPlacement(StructurePlacement p) {
        this.placement = p;
        return this;
    }


    /**
     * Registers the {@link StructureSet} with the currently active
     * {@link net.minecraft.data.worldgen.BootstrapContext}.
     * <p>
     * Will fail if either the key of this Feature or the {@link net.minecraft.data.worldgen.BootstrapContext}
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
            throw new IllegalStateException("StructureSet " + key.location() + " must define a placement");
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

    /**
     * Base builder for a {@link StructurePlacement}, shared by {@link RandomSpreadStructurePlacementBuilder}
     * and {@link ConcentricRingsStructurePlacementBuilder}. Holds the settings common to every
     * {@link StructurePlacement} subclass.
     *
     * @param <R> The concrete builder type, for chaining
     */
    public abstract class StructurePlacementBuilder<R extends StructurePlacementBuilder<R>> {
        protected Vec3i locateOffset;
        protected StructurePlacement.FrequencyReductionMethod frequencyReductionMethod;
        protected float frequency;
        protected int salt;
        protected Optional<StructurePlacement.ExclusionZone> exclusionZone;

        /**
         * Builds the {@link StructurePlacement} and applies it to the owning {@link StructureSetBuilder}
         * via {@link StructureSetBuilder#setPlacement(StructurePlacement)}.
         *
         * @return The owning {@link StructureSetBuilder}, for chaining
         */
        public abstract StructureSetBuilder finishPlacement();

        /**
         * Sets an offset applied to the located position. Defaults to {@link Vec3i#ZERO}.
         *
         * @param offset The offset to apply
         * @return This builder instance, for chaining
         */
        @NotNull
        public R locateOffset(Vec3i offset) {
            this.locateOffset = offset;
            return (R) this;
        }

        /**
         * Sets the {@link StructurePlacement.FrequencyReductionMethod} used together with
         * {@link #frequency(float)}. Defaults to {@link StructurePlacement.FrequencyReductionMethod#DEFAULT}.
         *
         * @param method The frequency reduction method to use
         * @return This builder instance, for chaining
         */
        @NotNull
        public R frequencyReductionMethod(@NotNull StructurePlacement.FrequencyReductionMethod method) {
            this.frequencyReductionMethod = method;
            return (R) this;
        }

        /**
         * Sets the fraction (0..1) of eligible locations that actually generate a structure. Defaults to
         * {@code 1.0} (every eligible location).
         *
         * @param frequency The frequency to use
         * @return This builder instance, for chaining
         */
        @NotNull
        public R frequency(float frequency) {
            this.frequency = frequency;
            return (R) this;
        }

        /**
         * Sets the salt used to seed the placement's random number generator, so that different structure
         * sets don't generate at correlated positions. Defaults to the (absolute value of the) hash code
         * of the set's {@link ResourceKey#location()}.
         *
         * @param salt The salt to use
         * @return This builder instance, for chaining
         */
        @NotNull
        public R salt(int salt) {
            this.salt = salt;
            return (R) this;
        }

        /**
         * Sets an {@link StructurePlacement.ExclusionZone} that prevents this set from generating too
         * close to another {@link StructureSet}. Unset ({@code null}) by default.
         *
         * @param exclusionZone The exclusion zone to use, or {@code null} to clear it
         * @return This builder instance, for chaining
         */
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

            salt = Math.abs(baseKey.location().hashCode());
        }
    }

    /**
     * A builder for a {@link RandomSpreadStructurePlacement} — structures are spread out randomly across
     * a grid of cells. Created via {@link StructureSetBuilder#randomPlacement()}.
     */
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

        /**
         * Sets the average distance (in chunks) between two structures of this set. Defaults to {@code 32}.
         *
         * @param spacing The spacing to use
         * @return This builder instance, for chaining
         */
        @NotNull
        public RandomSpreadStructurePlacementBuilder spacing(int spacing) {
            this.spacing = spacing;
            return this;
        }

        /**
         * Sets the minimum distance (in chunks) between two structures of this set. Must be smaller than
         * {@link #spacing(int)}. Defaults to {@code 8}.
         *
         * @param separation The separation to use
         * @return This builder instance, for chaining
         */
        @NotNull
        public RandomSpreadStructurePlacementBuilder separation(int separation) {
            this.separation = separation;
            return this;
        }

        /**
         * Sets the {@link RandomSpreadType} used to pick a position within a grid cell. Defaults to
         * {@link RandomSpreadType#LINEAR}.
         *
         * @param spreadType The spread type to use
         * @return This builder instance, for chaining
         */
        @NotNull
        public RandomSpreadStructurePlacementBuilder spreadType(@NotNull RandomSpreadType spreadType) {
            this.spreadType = spreadType;
            return this;
        }

        @Override
        public StructureSetBuilder finishPlacement() {
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

    /**
     * A builder for a {@link ConcentricRingsStructurePlacement} — structures are placed on concentric
     * rings around the world origin, biased towards {@link #preferredBiomes(TagKey)} (vanilla uses this
     * for strongholds). Created via {@link StructureSetBuilder#concentricPlacement()}.
     */
    public class ConcentricRingsStructurePlacementBuilder extends StructurePlacementBuilder<ConcentricRingsStructurePlacementBuilder> {
        protected int distance;
        protected int spread;
        protected int count;
        @NotNull
        protected TagKey<Biome> preferredBiomes;
        @NotNull
        private final BootstrapContext<StructureSet> context;

        ConcentricRingsStructurePlacementBuilder(
                @NotNull BootstrapContext<StructureSet> context,
                @NotNull ResourceKey<StructureSet> baseKey
        ) {
            super(baseKey);
            this.context = context;
            distance = 32;
            spread = 3;
            count = 128;
            preferredBiomes = BiomeTags.STRONGHOLD_BIASED_TO;
        }

        /**
         * Sets the distance (in chunks) between each ring. Defaults to {@code 32}.
         *
         * @param distance The distance to use
         * @return This builder instance, for chaining
         */
        @NotNull
        public ConcentricRingsStructurePlacementBuilder distance(int distance) {
            this.distance = distance;
            return this;
        }

        /**
         * Sets how much (in chunks) the location of a structure on a ring may randomly deviate from the
         * ring. Defaults to {@code 3}.
         *
         * @param spread The spread to use
         * @return This builder instance, for chaining
         */
        @NotNull
        public ConcentricRingsStructurePlacementBuilder spread(int spread) {
            this.spread = spread;
            return this;
        }

        /**
         * Sets the total number of structures placed across all rings. Defaults to {@code 128}.
         *
         * @param count The count to use
         * @return This builder instance, for chaining
         */
        @NotNull
        public ConcentricRingsStructurePlacementBuilder count(int count) {
            this.count = count;
            return this;
        }

        /**
         * Sets the biome tag rings are biased towards, resolved from the biome registry of the active
         * {@link BootstrapContext}. Defaults to {@link BiomeTags#STRONGHOLD_BIASED_TO}.
         *
         * @param preferredBiomes The preferred biome tag to use
         * @return This builder instance, for chaining
         */
        @NotNull
        public ConcentricRingsStructurePlacementBuilder preferredBiomes(@NotNull TagKey<Biome> preferredBiomes) {
            this.preferredBiomes = preferredBiomes;
            return this;
        }

        @Override
        public StructureSetBuilder finishPlacement() {
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
