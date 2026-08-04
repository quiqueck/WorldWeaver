package de.ambertation.wover.structure.api.pools;

import de.ambertation.wover.structure.api.processors.StructureProcessorKey;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

/**
 * A builder for {@link StructureTemplatePool}s. Created by calling
 * {@link StructurePoolKey#bootstrap(net.minecraft.data.worldgen.BootstrapContext)}.
 * <p>
 * A pool needs at least one element (added via {@link #add(Function, int)}, {@link #addFeature},
 * {@link #startSingle(ResourceLocation)}/{@link #startSingleEnd(ResourceLocation)}/
 * {@link #startLegacySingle(ResourceLocation)}, or {@link #addEmptyElement(int)}) before it can be
 * {@link #register() registered}. If no {@link #terminator(StructurePoolKey) terminator} is set, one is
 * created automatically ({@link #emptyTerminator()}).
 */
public interface StructurePoolBuilder {
    /**
     * Registers the {@link StructureTemplatePool} with the currently active
     * {@link net.minecraft.data.worldgen.BootstrapContext}.
     * <p>
     * Will fail if either the key of this Feature or the {@link net.minecraft.data.worldgen.BootstrapContext}
     * are null.
     *
     * @return the holder
     */
    @NotNull
    Holder<StructureTemplatePool> register();

    /**
     * Creates an unnamed {@link Holder} for this {@link StructurePoolBuilder}.
     * <p>
     * This method is useful, if you want to create an anonymous {@link StructureTemplatePool}
     * that is directly inlined
     *
     * @return the holder
     */
    @NotNull
    Holder<StructureTemplatePool> directHolder();

    /**
     * Adds a custom {@link StructurePoolElement} to the pool. Prefer {@link #startSingle(ResourceLocation)}
     * or {@link #addFeature} for the common cases.
     *
     * @param element A factory that creates the element from this pool's {@link StructureTemplatePool.Projection}
     * @param weight  The relative weight used when randomly selecting between multiple elements
     * @return This builder instance, for chaining
     */
    @NotNull
    StructurePoolBuilder add(
            @NotNull Function<StructureTemplatePool.Projection, ? extends StructurePoolElement> element,
            int weight
    );

    /**
     * Adds a {@link PlacedFeature} element to the pool, resolving the holder from the placed feature
     * registry of the active {@link net.minecraft.data.worldgen.BootstrapContext}.
     *
     * @param feature The key of the feature to add
     * @param weight  The relative weight used when randomly selecting between multiple elements
     * @return This builder instance, for chaining
     */
    @NotNull
    StructurePoolBuilder addFeature(@NotNull ResourceKey<PlacedFeature> feature, int weight);

    /**
     * Adds a {@link PlacedFeature} element to the pool.
     *
     * @param feature The holder of the feature to add
     * @param weight  The relative weight used when randomly selecting between multiple elements
     * @return This builder instance, for chaining
     */
    @NotNull
    StructurePoolBuilder addFeature(@NotNull Holder<PlacedFeature> feature, int weight);

    /**
     * Sets the {@link StructureTemplatePool.Projection} used to place elements added to this pool.
     * Defaults to {@link StructureTemplatePool.Projection#RIGID}.
     *
     * @param projection The projection to use
     * @return This builder instance, for chaining
     */
    @NotNull
    StructurePoolBuilder projection(@NotNull StructureTemplatePool.Projection projection);

    /**
     * Sets the pool that is used when the jigsaw generator can not fit another piece from this pool.
     *
     * @param terminator The holder of the terminator pool
     * @return This builder instance, for chaining
     */
    @NotNull
    StructurePoolBuilder terminator(@NotNull Holder<StructureTemplatePool> terminator);

    /**
     * Sets the terminator pool, resolving the holder from the template pool registry of the active
     * {@link net.minecraft.data.worldgen.BootstrapContext}. See {@link #terminator(Holder)}.
     *
     * @param terminator The key of the terminator pool
     * @return This builder instance, for chaining
     */
    @NotNull
    StructurePoolBuilder terminator(@NotNull ResourceKey<StructureTemplatePool> terminator);

    /**
     * Alias for {@link #terminator(ResourceKey)} that accepts a {@link StructurePoolKey}.
     *
     * @param terminator The key of the terminator pool
     * @return This builder instance, for chaining
     */
    @NotNull
    StructurePoolBuilder terminator(@NotNull StructurePoolKey terminator);

    /**
     * Sets vanilla's {@code minecraft:empty} pool as the terminator. This is the default used by
     * {@link #register()}/{@link #directHolder()} if no terminator was explicitly set.
     *
     * @return This builder instance, for chaining
     */
    @NotNull
    StructurePoolBuilder emptyTerminator();

    /**
     * Starts building a {@link net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement}
     * that places the {@code .nbt} template at {@code nbtLocation}. Call {@link ElementBuilder#endElement()}
     * to add the finished element to this pool.
     *
     * @param nbtLocation The location of the {@code .nbt} template
     * @return A builder for the new element
     */
    @NotNull
    ElementBuilder startSingle(@NotNull ResourceLocation nbtLocation);

    /**
     * Like {@link #startSingle(ResourceLocation)}, but creates an End-specific
     * {@code SingleEndPoolElement} that is exempt from the End's "empty chunk" generation rules.
     *
     * @param nbtLocation The location of the {@code .nbt} template
     * @return A builder for the new element
     */
    @NotNull
    ElementBuilder startSingleEnd(@NotNull ResourceLocation nbtLocation);

    /**
     * Like {@link #startSingle(ResourceLocation)}, but creates a legacy
     * {@link net.minecraft.world.level.levelgen.structure.pools.LegacySinglePoolElement} (used by
     * structures that predate liquid settings on template pool elements).
     *
     * @param nbtLocation The location of the {@code .nbt} template
     * @return A builder for the new element
     */
    @NotNull
    ElementBuilder startLegacySingle(@NotNull ResourceLocation nbtLocation);

    /**
     * Adds an empty ({@link StructurePoolElement#empty()}) element to the pool.
     *
     * @param weight The relative weight used when randomly selecting between multiple elements
     * @return This builder instance, for chaining
     */
    @NotNull
    StructurePoolBuilder addEmptyElement(int weight);

    /**
     * A builder for a single {@link StructurePoolElement} (created via {@link #startSingle},
     * {@link #startSingleEnd} or {@link #startLegacySingle}) that is added back to the owning
     * {@link StructurePoolBuilder} via {@link #endElement()}.
     */
    interface ElementBuilder {
        /**
         * Sets the {@link StructureProcessorList} applied to this element when it is placed.
         *
         * @param processor The holder of the processor list
         * @return This element builder, for chaining
         */
        @NotNull
        ElementBuilder processor(@NotNull Holder<StructureProcessorList> processor);

        /**
         * Sets the processor list, resolving the holder from the processor list registry of the active
         * {@link net.minecraft.data.worldgen.BootstrapContext}. See {@link #processor(Holder)}.
         *
         * @param processor The key of the processor list
         * @return This element builder, for chaining
         */
        @NotNull
        ElementBuilder processor(@NotNull ResourceKey<StructureProcessorList> processor);

        /**
         * Alias for {@link #processor(ResourceKey)} that accepts a {@link StructureProcessorKey}.
         *
         * @param processor The key of the processor list
         * @return This element builder, for chaining
         */
        @NotNull
        ElementBuilder processor(@NotNull StructureProcessorKey processor);

        /**
         * Sets vanilla's {@code minecraft:empty} processor list on this element. This is the default
         * applied by {@link #endElement()} if no processor was explicitly set.
         *
         * @return This element builder, for chaining
         */
        @NotNull
        ElementBuilder emptyProcessor();

        /**
         * Sets the relative weight used when randomly selecting between multiple elements. Defaults to
         * {@code 1}.
         *
         * @param weight The weight to use
         * @return This element builder, for chaining
         */
        @NotNull
        ElementBuilder weight(int weight);

        /**
         * Overrides the {@link LiquidSettings} used when placing this element. Unset by default (uses
         * the pool's/structure's default liquid settings).
         *
         * @param value The liquid settings to use
         * @return This element builder, for chaining
         */
        @NotNull
        ElementBuilder liquidSettingsOverride(LiquidSettings value);

        /**
         * Finishes this element and adds it to the owning {@link StructurePoolBuilder}.
         *
         * @return The owning {@link StructurePoolBuilder}, for chaining
         */
        @NotNull
        StructurePoolBuilder endElement();
    }
}
