package org.betterx.wover.generator.api.biomesource;

import org.betterx.wover.biome.api.BiomeKey;
import org.betterx.wover.biome.api.builder.BiomeBootstrapContext;
import org.betterx.wover.biome.api.builder.BiomeBuilder;
import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.api.data.BiomeGenerationDataContainer;
import org.betterx.wover.generator.impl.biomesource.builder.WoverBiomeKeyImpl;
import org.betterx.wover.generator.impl.biomesource.builder.WrappedWoverBiomeKeyImpl;
import org.betterx.wover.tag.api.predefined.CommonBiomeTags;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Extends {@link BiomeBuilder} with the extra placement data that WoVer's own {@link BiomeSource}
 * implementations (and, indirectly, {@link WoverBiomePicker}) understand: an {@link #edge(ResourceKey) edge}
 * biome, a {@link #parent(ResourceKey) parent} biome for sub-biome placement, and the
 * {@link #terrainHeight(float)}/{@link #genChance(float)}/{@link #edgeSize(int)}/{@link #vertical(boolean)}
 * tuning values. The resulting data is written into a {@link WoverBiomeData} entry (the {@code wover:wover_data}
 * {@link BiomeData} type) instead of a plain {@link BiomeData}.
 * <p>
 * Use {@link #biomeKey(ResourceLocation)} to declare a completely new Biome (mirroring
 * {@link org.betterx.wover.biome.api.BiomeManager#vanilla(ResourceLocation)}), or {@link #wrappedKey(ResourceKey)}
 * to attach this extra data to a Biome that already exists (mirroring
 * {@link org.betterx.wover.biome.api.BiomeManager#wrapped(ResourceKey)}).
 *
 * @param <B> the concrete builder type returned by the fluent setters
 */
public interface WoverBiomeBuilder<B extends BiomeBuilder<B>> {

    /**
     * Sets the edge biome that generates at the border of this biome.
     * <p>
     * The edge biome is picked with a size of {@link #edgeSize(int)} instead of the regular biome size.
     *
     * @param edge the key of the edge biome
     * @return this builder, for chaining
     */
    B edge(ResourceKey<Biome> edge);

    /**
     * Marks this biome as a sub-biome (alternative) of {@code parent}.
     * <p>
     * A sub-biome is only ever picked in place of its parent, with a chance of {@link #genChance(float)}
     * relative to the parent and its other sub-biomes; it never generates as a top-level pick on its own.
     *
     * @param parent the key of the parent biome
     * @return this builder, for chaining
     */
    B parent(ResourceKey<Biome> parent);

    /**
     * Marks this biome as a sub-biome (alternative) of {@code parent}.
     * <p>
     * Convenience overload of {@link #parent(ResourceKey)} taking a {@link BiomeKey} instead of a raw
     * {@link ResourceKey}.
     *
     * @param parent the key of the parent biome
     * @return this builder, for chaining
     */
    B parent(BiomeKey<?> parent);

    /**
     * Sets the terrain height hint used when placing this biome.
     *
     * @param height the terrain height
     * @return this builder, for chaining
     */
    B terrainHeight(float height);

    /**
     * Sets the relative weight this biome (or, if {@link #parent(ResourceKey) parent} is set, this
     * sub-biome) is picked with.
     *
     * @param weight the generation chance/weight
     * @return this builder, for chaining
     */
    B genChance(float weight);

    /**
     * Sets the size of the {@link #edge(ResourceKey) edge} biome border, in the same units as the
     * BiomeSource's biome size.
     *
     * @param size the edge size
     * @return this builder, for chaining
     */
    B edgeSize(int size);

    /**
     * Sets whether the edge biome should be generated as a vertical (height-based) transition instead of a
     * horizontal one.
     *
     * @param vertical {@code true} to use a vertical edge transition
     * @return this builder, for chaining
     */
    B vertical(boolean vertical);

    /**
     * Creates a key that attaches WoVer's extra placement data ({@link WoverBiomeData}) to an
     * <em>already existing</em> Biome (vanilla or from another mod), without redefining the Biome itself.
     * <p>
     * Mirrors {@link org.betterx.wover.biome.api.BiomeManager#wrapped(ResourceKey)}, but with the
     * {@link Wrapped} builder that also exposes edge/parent placement.
     *
     * @param key the key of the existing Biome
     * @return a new {@link BiomeKey} for a {@link Wrapped} builder
     */
    static BiomeKey<Wrapped> wrappedKey(@NotNull ResourceKey<Biome> key) {
        return new WrappedWoverBiomeKeyImpl(key.location());
    }

    /**
     * Creates a key for a completely new, WoVer-aware Biome.
     * <p>
     * Mirrors {@link org.betterx.wover.biome.api.BiomeManager#vanilla(ResourceLocation)}, but with the
     * {@link WoverBiome} builder that also exposes edge/parent placement.
     *
     * @param location the id of the new Biome
     * @return a new {@link BiomeKey} for a {@link WoverBiome} builder
     */
    static BiomeKey<WoverBiome> biomeKey(@NotNull ResourceLocation location) {
        return new WoverBiomeKeyImpl(location);
    }

    /**
     * Builder returned by {@link #wrappedKey(ResourceKey)}. Attaches WoVer's extra placement data to an
     * existing Biome without touching the underlying vanilla {@link Biome} definition.
     */
    abstract class Wrapped extends BiomeBuilder<Wrapped> implements WoverBiomeBuilder<Wrapped> {
        protected Wrapped(
                BiomeBootstrapContext context,
                BiomeKey<WoverBiomeBuilder.Wrapped> key
        ) {
            super(context, key);
        }

        /**
         * Marks this Biome as intended for the Nether by setting {@link BiomeTags#IS_NETHER} as its
         * intended placement tag.
         *
         * @return this builder, for chaining
         */
        public WoverBiomeBuilder.Wrapped isNetherBiome() {
            return this.intendedPlacement(BiomeTags.IS_NETHER);
        }

        /**
         * Marks this Biome as an End highland biome by setting
         * {@link CommonBiomeTags#IS_END_HIGHLAND} as its intended placement tag.
         *
         * @return this builder, for chaining
         */
        public WoverBiomeBuilder.Wrapped isEndHighlandBiome() {
            return this.intendedPlacement(CommonBiomeTags.IS_END_HIGHLAND);
        }

        /**
         * Marks this Biome as an End midland biome (a height-based transition biome next to {@code parent})
         * by setting {@link CommonBiomeTags#IS_END_MIDLAND} as its intended placement tag and
         * {@code parent} as its {@link #parent(BiomeKey) parent}.
         *
         * @param parent the highland biome this midland biome transitions from
         * @return this builder, for chaining
         */
        public WoverBiomeBuilder.Wrapped isEndMidlandBiome(BiomeKey<?> parent) {
            this.parent(parent);
            return this.intendedPlacement(CommonBiomeTags.IS_END_MIDLAND);
        }

        /**
         * Marks this Biome as an End center-island biome by setting
         * {@link CommonBiomeTags#IS_END_CENTER} as its intended placement tag.
         *
         * @return this builder, for chaining
         */
        public WoverBiomeBuilder.Wrapped isEndCenterIslandBiome() {
            return this.intendedPlacement(CommonBiomeTags.IS_END_CENTER);
        }

        /**
         * Marks this Biome as an End barrens biome (a transition biome next to {@code parent}) by setting
         * {@link CommonBiomeTags#IS_END_BARRENS} as its intended placement tag and {@code parent} as its
         * {@link #parent(BiomeKey) parent}.
         *
         * @param parent the highland biome this barrens biome transitions from
         * @return this builder, for chaining
         */
        public WoverBiomeBuilder.Wrapped isEndBarrensBiome(BiomeKey<?> parent) {
            this.parent(parent);
            return this.intendedPlacement(CommonBiomeTags.IS_END_BARRENS);
        }

        /**
         * Marks this Biome as a small End island biome by setting
         * {@link CommonBiomeTags#IS_SMALL_END_ISLAND} as its intended placement tag.
         *
         * @return this builder, for chaining
         */
        public WoverBiomeBuilder.Wrapped isEndSmallIslandBiome() {
            return this.intendedPlacement(CommonBiomeTags.IS_SMALL_END_ISLAND);
        }
    }

    /**
     * Builder returned by {@link #biomeKey(ResourceLocation)}. Defines a completely new, vanilla-style
     * Biome with WoVer's extra placement data (edge/parent/terrain height/gen chance).
     */
    abstract class WoverBiome extends AbstractWoverBiomeBuilder<WoverBiome> {
        protected WoverBiome(BiomeBootstrapContext context, BiomeKey<WoverBiome> key) {
            super(context, key);
        }
    }

    /**
     * Shared implementation of {@link WoverBiomeBuilder} on top of {@link BiomeBuilder.VanillaBuilder},
     * backing {@link WoverBiome}. Writes its fields into a {@link WoverBiomeData} entry instead of the base
     * {@link BiomeData} when {@link #registerBiomeData(BootstrapContext)} is called.
     *
     * @param <T> the concrete builder type
     */
    abstract class AbstractWoverBiomeBuilder<T extends AbstractWoverBiomeBuilder<T>> extends BiomeBuilder.VanillaBuilder<T> implements WoverBiomeBuilder<T> {
        protected float terrainHeight;
        protected float genChance;
        protected int edgeSize;
        protected boolean vertical;
        protected @Nullable ResourceKey<Biome> edge;
        protected @Nullable ResourceKey<Biome> parent;

        protected AbstractWoverBiomeBuilder(
                BiomeBootstrapContext context,
                BiomeKey<T> key
        ) {
            super(context, key);
            this.genChance = 1.0f;
            this.edgeSize = 0;
            this.terrainHeight = 0.1f;
            this.vertical = false;
        }

        /**
         * Registers a {@link WoverBiomeData} entry (instead of a plain {@link BiomeData}) carrying the
         * edge/parent/terrain height/gen chance values collected by this builder.
         *
         * @param dataContext the context to register the {@link BiomeData} with
         */
        @Override
        public void registerBiomeData(BootstrapContext<BiomeData> dataContext) {
            dataContext.register(
                    key.dataKey,
                    new WoverBiomeData(
                            fogDensity, key.key, new BiomeGenerationDataContainer(parameters, intendedPlacement),
                            terrainHeight, genChance, edgeSize, vertical, edge, parent
                    )
            );
        }

        @Override
        public T edge(ResourceKey<Biome> edge) {
            this.edge = edge;
            return (T) this;
        }

        @Override
        public T parent(@Nullable ResourceKey<Biome> parent) {
            this.parent = parent;
            return (T) this;
        }

        @Override
        public T parent(@Nullable BiomeKey<?> parent) {
            this.parent = parent == null ? null : parent.key;
            return (T) this;
        }

        @Override
        public T terrainHeight(float height) {
            this.terrainHeight = height;
            return (T) this;
        }

        @Override
        public T genChance(float weight) {
            this.genChance = weight;
            return (T) this;
        }

        @Override
        public T edgeSize(int size) {
            this.edgeSize = size;
            return (T) this;
        }

        @Override
        public T vertical(boolean vertical) {
            this.vertical = vertical;
            return (T) this;
        }
    }
}
