package de.ambertation.wover.biome.api.builder;

import de.ambertation.wunderlib.ui.ColorHelper;
import de.ambertation.wover.biome.api.BiomeKey;
import de.ambertation.wover.biome.api.data.BiomeData;
import de.ambertation.wover.biome.impl.builder.BiomeSurfaceRuleBuilderImpl;
import de.ambertation.wover.feature.api.placed.BasePlacedFeatureKey;
import de.ambertation.wover.feature.api.placed.PlacedFeatureManager;
import de.ambertation.wover.structure.api.StructureKey;
import de.ambertation.wover.surface.api.AssignedSurfaceRule;
import de.ambertation.wover.tag.api.event.context.TagBootstrapContext;
import de.ambertation.wover.tag.api.predefined.CommonBiomeTags;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base fluent builder used to define the content of a Biome created through the {@link BiomeKey} API.
 * <p>
 * A {@link BiomeBuilder} is obtained by calling {@link BiomeKey#bootstrap(BiomeBootstrapContext)}, filled in
 * using the chainable setter methods on this class (and its subclasses), and finished with a call to
 * {@link #register()}. There are two concrete builder families:
 * <ul>
 *     <li>{@link Vanilla} — defines a completely new, vanilla-style {@link Biome}, created through
 *     {@link de.ambertation.wover.biome.api.BiomeManager#vanilla(net.minecraft.resources.ResourceLocation)}.</li>
 *     <li>{@link Wrapped} — only attaches {@link BiomeData} (fog density, climate parameters, intended
 *     placement) to an already existing Biome, created through
 *     {@link de.ambertation.wover.biome.api.BiomeManager#wrapped(ResourceKey)}.</li>
 * </ul>
 *
 * @param <B> The concrete builder type, used to return {@code this} with the correct type from every setter.
 */
public abstract class BiomeBuilder<B extends BiomeBuilder<B>> {
    /**
     * The key that this builder was created from.
     */
    public final BiomeKey<B> key;
    /**
     * The bootstrap context that this builder will register itself with when {@link #register()} is called.
     */
    public final BiomeBootstrapContext bootstrapContext;

    /**
     * Calculates the default sky color vanilla uses for a Biome with the given temperature.
     *
     * @param temperature The temperature of the Biome.
     * @return The sky color.
     */
    public static int calculateSkyColor(float temperature) {
        return OverworldBiomes.calculateSkyColor(temperature);
    }

    /**
     * The default water fog color.
     */
    public static int DEFAULT_WATER_FOG_COLOR = 0x050533;
    /**
     * The default water color.
     */
    public static int DEFAULT_WATER_COLOR = 0x3F76E4;
    /**
     * The default water color for Nether Biomes. Same as {@link #DEFAULT_WATER_COLOR}.
     */
    public static int DEFAULT_NETHER_WATER_COLOR = DEFAULT_WATER_COLOR;
    /**
     * The default water color for End Biomes. Same as {@link #DEFAULT_WATER_COLOR}.
     */
    public static int DEFAULT_END_WATER_COLOR = DEFAULT_WATER_COLOR;
    /**
     * The default water fog color for Nether Biomes.
     */
    public static int DEFAULT_NETHER_WATER_FOG_COLOR = 0x050533;
    /**
     * The default water fog color for End Biomes. Same as {@link #DEFAULT_NETHER_WATER_FOG_COLOR}.
     */
    public static int DEFAULT_END_WATER_FOG_COLOR = DEFAULT_NETHER_WATER_FOG_COLOR;
    /**
     * The default fog color.
     */
    public static int DEFAULT_FOG_COLOR = 0xC0D8FF;
    /**
     * The default fog color for End Biomes.
     */
    public static int DEFAULT_END_FOG_COLOR = 0xA080A0;
    /**
     * The default sky color for End Biomes.
     */
    public static int DEFAULT_END_SKY_COLOR = 0x000000;
    /**
     * The default temperature for Nether Biomes.
     */
    public static float DEFAULT_NETHER_TEMPERATURE = 2.0f;
    /**
     * The default temperature for End Biomes.
     */
    public static float DEFAULT_END_TEMPERATURE = 0.5f;
    /**
     * The default downfall/wetness for Nether Biomes.
     */
    public static float DEFAULT_NETHER_WETNESS = 0.0f;
    /**
     * The default downfall/wetness for End Biomes.
     */
    public static float DEFAULT_END_WETNESS = 0.5f;

    /**
     * The climate parameter points that were added using {@link #addClimate(Climate.ParameterPoint)}.
     */
    protected final List<Climate.ParameterPoint> parameters = new ArrayList<>(1);

    /**
     * The tag that was set as the intended placement of the Biome, either explicitly through
     * {@link #intendedPlacement(TagKey)} or implicitly through {@link #biomeTypeTag(TagKey)}.
     */
    protected @Nullable TagKey<Biome> intendedPlacement = null;
    /**
     * The fog density of the Biome, as set by {@link #fogDensity(float)}.
     */
    protected float fogDensity;
    /**
     * The Biome tags that were added using {@link #tag(TagKey[])}.
     */
    protected final List<TagKey<Biome>> biomeTags = new ArrayList<>(2);

    private @Nullable BiomeSurfaceRuleBuilderImpl<B> surfaceBuilder;


    /**
     * Creates a new builder instance.
     *
     * @param context The bootstrap context that this builder will register itself with.
     * @param key     The key that this builder was created from.
     */
    protected BiomeBuilder(BiomeBootstrapContext context, BiomeKey<B> key) {
        this.key = key;
        this.bootstrapContext = context;
        this.fogDensity = 1.0f;
    }

    /**
     * Adds a climate parameter point that determines where the Biome will be placed.
     *
     * @param point The parameter point to add.
     * @return This builder.
     */
    public B addClimate(Climate.ParameterPoint point) {
        parameters.add(point);
        return (B) this;
    }

    /**
     * Adds a climate parameter point suitable for a Nether-style Biome (0 continentalness, erosion, depth
     * and weirdness), using the given temperature, humidity and offset.
     *
     * @param temperature The temperature of the parameter point.
     * @param humidity    The humidity of the parameter point.
     * @param offset      The offset of the parameter point.
     * @return This builder.
     */
    public B addNetherClimate(float temperature, float humidity, float offset) {
        return addClimate(Climate.parameters(temperature, humidity, 0, 0, 0, 0, offset));
    }

    /**
     * Adds a climate parameter point suitable for a Nether-style Biome, using the given temperature and
     * humidity and an offset of {@code 0}.
     *
     * @param temperature The temperature of the parameter point.
     * @param humidity    The humidity of the parameter point.
     * @return This builder.
     * @see #addNetherClimate(float, float, float)
     */
    public B addNetherClimate(float temperature, float humidity) {
        return addNetherClimate(temperature, humidity, 0);
    }

    /**
     * Sets the fog density of the Biome.
     *
     * @param density The fog density.
     * @return This builder.
     */
    public B fogDensity(float density) {
        this.fogDensity = density;
        return (B) this;
    }

    /**
     * Adds the Biome tag associated with the passed {@link StructureKey} to this Biome, so the structure can
     * generate in it.
     *
     * @param structure The structure that should be able to generate in this Biome.
     * @return This builder.
     */
    public B structure(StructureKey<?, ?, ?> structure) {
        return tag(structure.biomeTag());
    }

    /**
     * Adds the passed Biome tag to this Biome. Intended to be used with a structure set's Biome tag, so the
     * matching structure can generate in this Biome.
     *
     * @param structureTag The Biome tag of the structure that should be able to generate in this Biome.
     * @return This builder.
     */
    public B structure(TagKey<Biome> structureTag) {
        return tag(structureTag);
    }

    /**
     * Adds a biome tag to the biome and sets it as the intended placement for this biome.
     * <p>
     * The intended placement is used to determine where a Biome is placed in a dimension.
     *
     * @param tag The tag to add and set as the intended placement.
     * @return The builder.
     */
    protected B biomeTypeTag(TagKey<Biome> tag) {
        if (intendedPlacement == null && tag != null) {
            intendedPlacement = tag;
        }
        return tag(tag);
    }

    /**
     * Adds the passed Biome tags to this Biome, skipping {@code null} entries and tags that were already
     * added.
     *
     * @param tags The tags to add.
     * @return This builder.
     */
    @SafeVarargs
    public final B tag(TagKey<Biome>... tags) {
        for (TagKey<Biome> biomeTag : tags) {
            if (biomeTag != null && !biomeTags.contains(biomeTag))
                biomeTags.add(biomeTag);
        }

        return (B) this;
    }

    /**
     * Starts defining the surface rule for this Biome.
     * <p>
     * The returned {@link BiomeSurfaceRuleBuilder} needs to be finished with a call to
     * {@link BiomeSurfaceRuleBuilder#finishSurface()} to return to this builder.
     *
     * @return The surface rule builder for this Biome.
     */
    public BiomeSurfaceRuleBuilder<B> startSurface() {
        surfaceBuilder = new BiomeSurfaceRuleBuilderImpl<>(key, (B) this);
        return surfaceBuilder;
    }

    /**
     * Sets the surface (top layer) of the Biome to the given state.
     *
     * @param state The state for the surface.
     * @return This builder.
     * @see BiomeSurfaceRuleBuilder#surface(BlockState)
     */
    public B surface(BlockState state) {
        return startSurface().surface(state).finishSurface();
    }

    /**
     * Sets the surface (top layer) of the Biome to the default state of the given block.
     *
     * @param block The block for the surface.
     * @return This builder.
     * @see BiomeSurfaceRuleBuilder#surface(Block)
     */
    public B surface(Block block) {
        return startSurface().surface(block).finishSurface();
    }

    /**
     * Sets the surface (top layer) and the subsurface (3 blocks deep) of the Biome.
     *
     * @param top   The state for the surface.
     * @param under The state for the subsurface.
     * @return This builder.
     * @see BiomeSurfaceRuleBuilder#surface(BlockState)
     * @see BiomeSurfaceRuleBuilder#subsurface(BlockState, int)
     */
    public B surface(BlockState top, BlockState under) {
        return startSurface().surface(top).subsurface(under, 3).finishSurface();
    }

    /**
     * Sets the surface (top layer) and the subsurface (3 blocks deep) of the Biome, using the default state
     * of the given blocks.
     *
     * @param top   The block for the surface.
     * @param under The block for the subsurface.
     * @return This builder.
     * @see BiomeSurfaceRuleBuilder#surface(Block)
     * @see BiomeSurfaceRuleBuilder#subsurface(Block, int)
     */
    public B surface(Block top, Block under) {
        return startSurface().surface(top).subsurface(under, 3).finishSurface();
    }

    /**
     * Sets the tag that is used to determine the intended placement of this Biome, without adding it as a
     * regular Biome tag.
     * <p>
     * Prefer {@link #biomeTypeTag(TagKey)} (or one of the convenience methods calling it, like
     * {@link VanillaBuilder#isNetherBiome()}) if the tag should also be added to the Biome.
     *
     * @param biome The tag to use as the intended placement.
     * @return This builder.
     */
    public B intendedPlacement(TagKey<Biome> biome) {
        this.intendedPlacement = biome;
        return (B) this;
    }

    /**
     * Registers this builder with the {@link #bootstrapContext} it was created from.
     * <p>
     * This needs to be the last call in the builder chain — once registered, the builder is used to populate
     * the Biome, {@link BiomeData}, surface rule and Biome-tag registries.
     */
    public void register() {
        bootstrapContext.register(this);
    }

    /**
     * For <b>internal</b> use only! Called to register the {@link Biome} defined by this builder.
     *
     * @param biomeContext The bootstrap context of the Biome registry.
     */
    public abstract void registerBiome(BootstrapContext<Biome> biomeContext);

    /**
     * For <b>internal</b> use only! Called to register the {@link BiomeData} defined by this builder.
     *
     * @param dataContext The bootstrap context of the {@link BiomeData} registry.
     */
    public abstract void registerBiomeData(BootstrapContext<BiomeData> dataContext);

    /**
     * For <b>internal</b> use only! Called to add this Biome to every tag that was added using
     * {@link #tag(TagKey[])} (or a method that forwards to it, like {@link #structure(TagKey)}).
     *
     * @param context The tag bootstrap context.
     */
    public void registerBiomeTags(TagBootstrapContext<Biome> context) {
        for (TagKey<Biome> biomeTag : biomeTags) {
            context.add(biomeTag, key.key);
        }
    }

    /**
     * For <b>internal</b> use only! Called to register the surface rule defined by {@link #startSurface()} (or
     * one of the {@link #surface} convenience methods), if any was defined.
     *
     * @param context The bootstrap context of the surface rule registry.
     */
    public void registerSurfaceRule(@NotNull BootstrapContext<AssignedSurfaceRule> context) {
        if (surfaceBuilder != null) {
            surfaceBuilder.register(context);
        }
    }

    /**
     * Base builder for a completely new, vanilla-style {@link Biome}, providing setters for climate,
     * terrain, ambience/fog colors, sounds, particles, mob spawns and features.
     * <p>
     * {@link Vanilla} is the concrete, instantiable version of this builder returned by
     * {@link de.ambertation.wover.biome.api.BiomeManager#vanilla(net.minecraft.resources.ResourceLocation)}.
     *
     * @param <B> The concrete builder type, used to return {@code this} with the correct type from every
     *            setter.
     */
    public abstract static class VanillaBuilder<B extends VanillaBuilder<B>> extends BiomeBuilder<B> {
        private Biome.TemperatureModifier temperatureModifier;
        private float downfall;
        private float temperature;
        private boolean hasPrecipitation;
        private final BiomeSpecialEffects.Builder fx = new BiomeSpecialEffects.Builder();
        private final BiomeGenerationSettings.Builder generationSettings;
        private final MobSpawnSettings.Builder mobSpawnSettings = new MobSpawnSettings.Builder();

        /**
         * Creates a new builder instance.
         * <p>
         * Initializes the Biome with vanilla's default fog/water/sky colors ({@link #DEFAULT_FOG_COLOR},
         * {@link #DEFAULT_WATER_FOG_COLOR}, {@link #DEFAULT_WATER_COLOR} and a sky color calculated with
         * {@link #calculateSkyColor(float)} from the default temperature of {@code 0.5}), no precipitation,
         * {@code 0} downfall and no temperature modifier.
         *
         * @param context The bootstrap context that this builder will register itself with.
         * @param key     The key that this builder was created from.
         */
        protected VanillaBuilder(BiomeBootstrapContext context, BiomeKey<B> key) {
            super(context, key);

            this.temperatureModifier = Biome.TemperatureModifier.NONE;
            this.downfall = 0.f;
            this.temperature = 0.5f;
            this.hasPrecipitation = false;

            generationSettings = new BiomeGenerationSettings.Builder(
                    bootstrapContext.lookup(Registries.PLACED_FEATURE),
                    bootstrapContext.lookup(Registries.CONFIGURED_CARVER)
            );

            fx.fogColor(DEFAULT_FOG_COLOR);
            fx.waterFogColor(DEFAULT_WATER_FOG_COLOR);
            fx.waterColor(DEFAULT_WATER_COLOR);
            fx.skyColor(calculateSkyColor(temperature));
        }


        /**
         * Sets whether the Biome has precipitation (rain/snow).
         *
         * @param bl {@code true} if the Biome should have precipitation.
         * @return This builder.
         */
        public B hasPrecipitation(boolean bl) {
            this.hasPrecipitation = bl;
            return (B) this;
        }

        /**
         * Sets the temperature of the Biome.
         *
         * @param f The temperature.
         * @return This builder.
         */
        public B temperature(float f) {
            this.temperature = f;
            return (B) this;
        }

        /**
         * Sets the downfall (wetness) of the Biome.
         *
         * @param f The downfall.
         * @return This builder.
         */
        public B downfall(float f) {
            this.downfall = f;
            return (B) this;
        }

        /**
         * Sets the {@link Biome.TemperatureModifier} of the Biome.
         *
         * @param temperatureModifier The temperature modifier.
         * @return This builder.
         */
        public B temperatureAdjustment(Biome.TemperatureModifier temperatureModifier) {
            this.temperatureModifier = temperatureModifier;
            return (B) this;
        }

        /**
         * Sets the {@link Biome.TemperatureModifier} of the Biome to {@link Biome.TemperatureModifier#FROZEN}.
         *
         * @return This builder.
         */
        public B temperatureFrozen() {
            return this.temperatureAdjustment(Biome.TemperatureModifier.FROZEN);
        }

        /**
         * Sets the {@link Biome.TemperatureModifier} of the Biome to {@link Biome.TemperatureModifier#NONE}.
         *
         * @return This builder.
         */
        public B temperatureRegular() {
            return this.temperatureAdjustment(Biome.TemperatureModifier.NONE);
        }

        /**
         * Adds a feature to the Biome, using the {@link GenerationStep.Decoration} configured on the
         * {@link BasePlacedFeatureKey} itself.
         *
         * @param feature The feature to add.
         * @return This builder.
         */
        public B feature(BasePlacedFeatureKey<?> feature) {
            generationSettings.addFeature(
                    feature.getDecoration(),
                    feature.getHolder(bootstrapContext.lookup(Registries.PLACED_FEATURE))
            );
            return (B) this;
        }

        /**
         * Adds a feature to the Biome.
         *
         * @param decoration The decoration step.
         * @param feature    The key of the {@link PlacedFeature}.
         * @return This builder.
         */
        public B feature(GenerationStep.Decoration decoration, ResourceKey<PlacedFeature> feature) {
            generationSettings.addFeature(
                    decoration,
                    PlacedFeatureManager.getHolder(bootstrapContext.lookup(Registries.PLACED_FEATURE), feature)
            );
            return (B) this;
        }

        /**
         * Adds a feature to the Biome.
         *
         * @param decoration The decoration step.
         * @param feature    The holder of the {@link PlacedFeature}.
         * @return This builder.
         */
        public B feature(GenerationStep.Decoration decoration, Holder<PlacedFeature> feature) {
            generationSettings.addFeature(decoration, feature);
            return (B) this;
        }

        /**
         * Will add features into biome, used for vanilla feature adding functions.
         *
         * @param featureAdd {@link Consumer} with {@link BiomeGenerationSettings.Builder}.
         * @return same builder.
         */
        public B feature(Consumer<BiomeGenerationSettings.Builder> featureAdd) {
            featureAdd.accept(generationSettings);
            return (B) this;
        }

        /**
         * Adds vanilla Mushrooms.
         *
         * @return same builder.
         */
        public B defaultMushrooms() {
            return feature(BiomeDefaultFeatures::addDefaultMushrooms);
        }

        /**
         * Adds vanilla Nether Ores.
         *
         * @return same builder.
         */
        public B netherDefaultOres() {
            return feature(BiomeDefaultFeatures::addNetherDefaultOres);
        }

        /**
         * Adds a carver to the Biome.
         *
         * @param carver The key of the {@link ConfiguredWorldCarver}.
         * @return This builder.
         */
        public B carver(ResourceKey<ConfiguredWorldCarver<?>> carver) {
            generationSettings.addCarver(
                    bootstrapContext.lookup(Registries.CONFIGURED_CARVER).getOrThrow(carver)
            );
            return (B) this;
        }

        /**
         * Adds a carver to the Biome.
         *
         * @param carver The holder of the {@link ConfiguredWorldCarver}.
         * @return This builder.
         */
        public B carver(Holder<ConfiguredWorldCarver<?>> carver) {
            generationSettings.addCarver(carver);
            return (B) this;
        }

        /**
         * Sets the fog color of the Biome.
         *
         * @param color The color.
         * @return This builder.
         */
        public B fogColor(int color) {
            fx.fogColor(color);
            return (B) this;
        }

        /**
         * Sets the fog color of the Biome.
         *
         * @param r The red component (0-255).
         * @param g The green component (0-255).
         * @param b The blue component (0-255).
         * @return This builder.
         */
        public B fogColor(int r, int g, int b) {
            fx.fogColor(ColorHelper.color(r, g, b));
            return (B) this;
        }

        /**
         * Sets the water color of the Biome.
         *
         * @param r The red component (0-255).
         * @param g The green component (0-255).
         * @param b The blue component (0-255).
         * @return This builder.
         */
        public B waterColor(int r, int g, int b) {
            return waterColor(ColorHelper.color(r, g, b));
        }

        /**
         * Sets the water color of the Biome.
         *
         * @param color The color.
         * @return This builder.
         */
        public B waterColor(int color) {
            fx.waterColor(color);
            return (B) this;
        }

        /**
         * Sets the underwater fog color of the Biome.
         *
         * @param r The red component (0-255).
         * @param g The green component (0-255).
         * @param b The blue component (0-255).
         * @return This builder.
         */
        public B waterFogColor(int r, int g, int b) {
            return waterFogColor(ColorHelper.color(r, g, b));
        }

        /**
         * Sets the underwater fog color of the Biome.
         *
         * @param color The color.
         * @return This builder.
         */
        public B waterFogColor(int color) {
            fx.waterFogColor(color);
            return (B) this;
        }

        /**
         * Sets the sky color of the Biome.
         *
         * @param r The red component (0-255).
         * @param g The green component (0-255).
         * @param b The blue component (0-255).
         * @return This builder.
         */
        public B skyColor(int r, int g, int b) {
            return skyColor(ColorHelper.color(r, g, b));
        }

        /**
         * Sets the sky color of the Biome.
         *
         * @param color The color.
         * @return This builder.
         */
        public B skyColor(int color) {
            fx.skyColor(color);
            return (B) this;
        }

        /**
         * Overrides the foliage (leaves) color of the Biome.
         *
         * @param r The red component (0-255).
         * @param g The green component (0-255).
         * @param b The blue component (0-255).
         * @return This builder.
         */
        public B foliageColorOverride(int r, int g, int b) {
            return foliageColorOverride(ColorHelper.color(r, g, b));
        }

        /**
         * Overrides the foliage (leaves) color of the Biome.
         *
         * @param color The color.
         * @return This builder.
         */
        public B foliageColorOverride(int color) {
            fx.foliageColorOverride(color);
            return (B) this;
        }

        /**
         * Overrides the grass color of the Biome.
         *
         * @param r The red component (0-255).
         * @param g The green component (0-255).
         * @param b The blue component (0-255).
         * @return This builder.
         */
        public B grassColorOverride(int r, int g, int b) {
            return grassColorOverride(ColorHelper.color(r, g, b));
        }

        /**
         * Overrides the grass color of the Biome.
         *
         * @param color The color.
         * @return This builder.
         */
        public B grassColorOverride(int color) {
            fx.grassColorOverride(color);
            return (B) this;
        }

        /**
         * Sets the {@link BiomeSpecialEffects.GrassColorModifier} that is applied on top of the grass color
         * of the Biome.
         *
         * @param grassColorModifier The grass color modifier.
         * @return This builder.
         */
        public B grassColorModifier(BiomeSpecialEffects.GrassColorModifier grassColorModifier) {
            fx.grassColorModifier(grassColorModifier);
            return (B) this;
        }

        /**
         * Sets both the water color and the underwater fog color of the Biome to the same value.
         *
         * @param color The color.
         * @return This builder.
         * @see #waterColor(int)
         * @see #waterFogColor(int)
         */
        public B waterAndFogColor(int color) {
            return waterColor(color).waterFogColor(color);
        }

        /**
         * Sets both the water color and the underwater fog color of the Biome to the same value.
         *
         * @param r The red component (0-255).
         * @param g The green component (0-255).
         * @param b The blue component (0-255).
         * @return This builder.
         * @see #waterAndFogColor(int)
         */
        public B waterAndFogColor(int r, int g, int b) {
            return waterAndFogColor(ColorHelper.color(r, g, b));
        }

        /**
         * Overrides both the grass and the foliage color of the Biome with the same value.
         *
         * @param r The red component (0-255).
         * @param g The green component (0-255).
         * @param b The blue component (0-255).
         * @return This builder.
         * @see #plantsColor(int)
         */
        public B plantsColor(int r, int g, int b) {
            return plantsColor(ColorHelper.color(r, g, b));
        }

        /**
         * Overrides both the grass and the foliage color of the Biome with the same value.
         *
         * @param color The color.
         * @return This builder.
         * @see #grassColorOverride(int)
         * @see #foliageColorOverride(int)
         */
        public B plantsColor(int color) {
            return grassColorOverride(color).foliageColorOverride(color);
        }


        /**
         * Adds ambient particles .
         *
         * @param particle    {@link ParticleOptions} particles (or {@link net.minecraft.core.particles.ParticleType}).
         * @param probability particle spawn probability, should have low value (example: 0.01F).
         * @return this builder.
         */
        public B particles(ParticleOptions particle, float probability) {
            particles(new AmbientParticleSettings(particle, probability));
            return (B) this;
        }

        /**
         * Sets the ambient particles of the Biome.
         *
         * @param ambientParticleSettings The particle settings.
         * @return This builder.
         */
        public B particles(AmbientParticleSettings ambientParticleSettings) {
            fx.ambientParticle(ambientParticleSettings);
            return (B) this;
        }


        /**
         * Sets the ambient loop sound of the Biome.
         *
         * @param holder The sound event.
         * @return This builder.
         */
        public B loop(Holder<SoundEvent> holder) {
            fx.ambientLoopSound(holder);
            return (B) this;
        }

        /**
         * Sets the ambient mood sound of the Biome.
         *
         * @param ambientMoodSettings The mood sound settings.
         * @return This builder.
         */
        public B mood(AmbientMoodSettings ambientMoodSettings) {
            fx.ambientMoodSound(ambientMoodSettings);
            return (B) this;
        }

        /**
         * Sets the ambient mood sound of the Biome, using vanilla's default cave-sound timings
         * (a tick delay of {@code 6000}, a block search extent of {@code 8} and a sound position offset of
         * {@code 2.0}).
         *
         * @param mood The sound event.
         * @return This builder.
         * @see #mood(Holder, int, int, float)
         */
        public B mood(Holder<SoundEvent> mood) {
            return mood(mood, 6000, 8, 2.0F);
        }

        /**
         * Sets the ambient mood sound of the Biome.
         *
         * @param mood                 The sound event.
         * @param tickDelay            The delay (in ticks) between two mood sounds.
         * @param blockSearchExtent    The radius (in blocks) that is searched for a valid position to play the
         *                             sound from.
         * @param soundPositionOffset  The random offset applied to the sound position.
         * @return This builder.
         */
        public B mood(Holder<SoundEvent> mood, int tickDelay, int blockSearchExtent, float soundPositionOffset) {
            return mood(new AmbientMoodSettings(mood, tickDelay, blockSearchExtent, soundPositionOffset));
        }

        /**
         * Sets the ambient additions sound of the Biome (a sound that randomly plays on top of the regular
         * ambience).
         *
         * @param ambientAdditionsSettings The additions sound settings.
         * @return This builder.
         */
        public B additions(AmbientAdditionsSettings ambientAdditionsSettings) {
            fx.ambientAdditionsSound(ambientAdditionsSettings);
            return (B) this;
        }

        /**
         * Sets the ambient additions sound of the Biome (a sound that randomly plays on top of the regular
         * ambience).
         *
         * @param additions The sound event.
         * @param intensity The chance (per tick) that the sound is played.
         * @return This builder.
         */
        public B additions(Holder<SoundEvent> additions, float intensity) {
            return additions(new AmbientAdditionsSettings(additions, intensity));
        }

        /**
         * Sets the ambient additions sound of the Biome, using vanilla's default intensity of
         * {@code 0.0111}.
         *
         * @param additions The sound event.
         * @return This builder.
         * @see #additions(Holder, float)
         */
        public B additions(Holder<SoundEvent> additions) {
            return additions(additions, 0.0111F);
        }

        /**
         * Sets the background music of the Biome.
         *
         * @param music The music, or {@code null} for no music.
         * @return This builder.
         */
        public B music(@Nullable Music music) {
            fx.backgroundMusic(music);
            return (B) this;
        }

        /**
         * Sets the background music of the Biome, using vanilla's default timings (a minimum delay of
         * {@code 600} ticks, a maximum delay of {@code 2400} ticks) and replacing any currently playing music.
         *
         * @param music The sound event.
         * @return This builder.
         * @see #music(Holder, int, int, boolean)
         */
        public B music(Holder<SoundEvent> music) {
            return music(music, 600, 2400, true);
        }

        /**
         * Sets the background music of the Biome.
         *
         * @param music               The sound event.
         * @param minDelay            The minimum delay (in ticks) before the music starts playing.
         * @param maxDelay            The maximum delay (in ticks) before the music starts playing.
         * @param replaceCurrentMusic Whether this music should interrupt music that is already playing.
         * @return This builder.
         */
        public B music(Holder<SoundEvent> music, int minDelay, int maxDelay, boolean replaceCurrentMusic) {
            return music(new Music(music, minDelay, maxDelay, replaceCurrentMusic));
        }

        /**
         * Marks this Biome as a Nether Biome by adding (and setting as intended placement) the
         * {@link BiomeTags#IS_NETHER} tag.
         *
         * @return This builder.
         * @see #biomeTypeTag(TagKey)
         */
        public final B isNetherBiome() {
            return biomeTypeTag(BiomeTags.IS_NETHER);
        }

        /**
         * Marks this Biome as an End Highland Biome by adding (and setting as intended placement) the
         * {@link CommonBiomeTags#IS_END_HIGHLAND} tag.
         *
         * @return This builder.
         * @see #biomeTypeTag(TagKey)
         */
        public final B isEndHighlandBiome() {
            return biomeTypeTag(CommonBiomeTags.IS_END_HIGHLAND);
        }

        /**
         * Marks this Biome as an End Midland Biome by adding (and setting as intended placement) the
         * {@link CommonBiomeTags#IS_END_MIDLAND} tag.
         *
         * @return This builder.
         * @see #biomeTypeTag(TagKey)
         */
        public final B isEndMidlandBiome() {
            return biomeTypeTag(CommonBiomeTags.IS_END_MIDLAND);
        }

        /**
         * Marks this Biome as an End Center Island Biome by adding (and setting as intended placement) the
         * {@link CommonBiomeTags#IS_END_CENTER} tag.
         *
         * @return This builder.
         * @see #biomeTypeTag(TagKey)
         */
        public final B isEndCenterIslandBiome() {
            return biomeTypeTag(CommonBiomeTags.IS_END_CENTER);
        }

        /**
         * Marks this Biome as an End Barrens Biome by adding (and setting as intended placement) the
         * {@link CommonBiomeTags#IS_END_BARRENS} tag.
         *
         * @return This builder.
         * @see #biomeTypeTag(TagKey)
         */
        public final B isEndBarrensBiome() {
            return biomeTypeTag(CommonBiomeTags.IS_END_BARRENS);
        }

        /**
         * Marks this Biome as a Small End Island Biome by adding (and setting as intended placement) the
         * {@link CommonBiomeTags#IS_SMALL_END_ISLAND} tag.
         *
         * @return This builder.
         * @see #biomeTypeTag(TagKey)
         */
        public final B isEndSmallIslandBiome() {
            return biomeTypeTag(CommonBiomeTags.IS_SMALL_END_ISLAND);
        }

        /**
         * Adds a mob spawn to the Biome.
         *
         * @param entityType    The entity type.
         * @param weight        The weight of this spawn entry.
         * @param minGroupCount The minimum number of mobs that spawn in a group.
         * @param maxGroupCount The maximum number of mobs that spawn in a group.
         * @return This builder.
         */
        public B spawn(EntityType<?> entityType, int weight, int minGroupCount, int maxGroupCount) {
            mobSpawnSettings.addSpawn(
                    entityType.getCategory(),
                    weight,
                    new MobSpawnSettings.SpawnerData(entityType, minGroupCount, maxGroupCount)
            );
            return (B) this;
        }

        /**
         * Adds a mob charge (used for the vanilla mob spawning "budget" system) to the Biome.
         *
         * @param entityType   The entity type.
         * @param energyBudget The energy budget assigned to this entity type.
         * @param charge       The charge of a single spawned entity.
         * @return This builder.
         */
        public B addMobCharge(EntityType<?> entityType, double energyBudget, double charge) {
            mobSpawnSettings.addMobCharge(entityType, energyBudget, charge);
            return (B) this;
        }

        /**
         * Sets the probability that a creature spawns in this Biome.
         *
         * @param p The probability.
         * @return This builder.
         */
        public B creatureGenerationProbability(float p) {
            mobSpawnSettings.creatureGenerationProbability(p);
            return (B) this;
        }

        /**
         * Registers this builder with the {@link #bootstrapContext} it was created from.
         * <p>
         * This needs to be the last call in the builder chain — once registered, the builder is used to
         * populate the Biome, {@link BiomeData}, surface rule and Biome-tag registries.
         */
        public void register() {
            bootstrapContext.register(this);
        }

        /**
         * For <b>internal</b> use only! Builds the {@link Biome} defined by this builder using
         * {@link #buildBiome()} and registers it.
         *
         * @param biomeContext The bootstrap context of the Biome registry.
         */
        public void registerBiome(BootstrapContext<Biome> biomeContext) {
            biomeContext.register(key.key, buildBiome());
        }

        /**
         * For <b>internal</b> use only! Called to register the {@link BiomeData} defined by this builder.
         *
         * @param dataContext The bootstrap context of the {@link BiomeData} registry.
         */
        public abstract void registerBiomeData(BootstrapContext<BiomeData> dataContext);


        /**
         * Builds the vanilla {@link Biome} instance from the values collected by this builder.
         *
         * @return The built Biome.
         */
        protected Biome buildBiome() {
            Biome.BiomeBuilder vanillaBuilder = new Biome.BiomeBuilder();

            vanillaBuilder.hasPrecipitation(hasPrecipitation);
            vanillaBuilder.downfall(downfall);
            vanillaBuilder.temperature(temperature);
            vanillaBuilder.temperatureAdjustment(temperatureModifier);

            vanillaBuilder.generationSettings(generationSettings.build());
            vanillaBuilder.specialEffects(fx.build());
            vanillaBuilder.mobSpawnSettings(mobSpawnSettings.build());

            return vanillaBuilder.build();
        }
    }

    /**
     * The concrete builder used to define a completely new, vanilla-style {@link Biome}.
     * <p>
     * Returned by {@link de.ambertation.wover.biome.api.BiomeManager#vanilla(net.minecraft.resources.ResourceLocation)}.
     */
    public abstract static class Vanilla extends VanillaBuilder<Vanilla> {
        /**
         * Creates a new builder instance.
         *
         * @param context The bootstrap context that this builder will register itself with.
         * @param key     The key that this builder was created from.
         */
        protected Vanilla(BiomeBootstrapContext context, BiomeKey<Vanilla> key) {
            super(context, key);
        }
    }

    /**
     * The concrete builder used to attach {@link BiomeData} (fog density, climate parameters, intended
     * placement, Biome tags) to an already existing {@link Biome}, without redefining the Biome itself.
     * <p>
     * Returned by {@link de.ambertation.wover.biome.api.BiomeManager#wrapped(ResourceKey)}.
     */
    public abstract static class Wrapped extends BiomeBuilder<Wrapped> {
        /**
         * Creates a new builder instance.
         *
         * @param context The bootstrap context that this builder will register itself with.
         * @param key     The key that this builder was created from.
         */
        protected Wrapped(BiomeBootstrapContext context, BiomeKey<Wrapped> key) {
            super(context, key);
        }
    }
}
