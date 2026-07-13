package org.betterx.wover.structure.api.builders;

import org.betterx.wover.structure.api.pools.StructurePoolKey;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import java.util.List;

/**
 * A {@link BaseStructureBuilder} for {@link JigsawStructure}s. Created via
 * {@link org.betterx.wover.structure.api.StructureKey.Jigsaw#bootstrap}.
 * <p>
 * At minimum, {@link #startPool(StructurePoolKey)} (or one of its overloads) must be called before
 * {@link #register()}/{@link #directHolder()} — all other settings have vanilla-equivalent defaults.
 */
public interface JigsawBuilder extends BaseStructureBuilder<JigsawStructure, JigsawBuilder> {
    /**
     * If set, the first jigsaw piece is projected onto the given {@link Heightmap.Types} of the starting
     * chunk before generation begins. Unset (empty) by default.
     *
     * @param value The heightmap type to project onto
     * @return This builder instance, for chaining
     */
    JigsawBuilder projectStartToHeightmap(Heightmap.Types value);

    /**
     * Sets the maximum distance (in blocks) the generated jigsaw pieces may spread from the starting
     * position. Defaults to {@code 80}.
     *
     * @param value The maximum distance from the center
     * @return This builder instance, for chaining
     */
    JigsawBuilder maxDistanceFromCenter(int value);

    /**
     * Restricts generation to start from a specific jigsaw block name within the {@link #startPool}
     * element, instead of any jigsaw block. Unset by default.
     *
     * @param value The name of the jigsaw block to start from
     * @return This builder instance, for chaining
     */
    JigsawBuilder startJigsawName(ResourceLocation value);

    /**
     * Enables or disables the "expansion hack" (a legacy vanilla workaround used by e.g. the Ancient
     * City). Defaults to {@code false}.
     *
     * @param value {@code true} to enable the expansion hack
     * @return This builder instance, for chaining
     */
    JigsawBuilder useExpansionHack(boolean value);

    /**
     * Sets the maximum recursion depth of the jigsaw generation. Defaults to {@code 6}.
     *
     * @param value The maximum depth
     * @return This builder instance, for chaining
     */
    JigsawBuilder maxDepth(int value);

    /**
     * Sets the {@link HeightProvider} used to pick the Y coordinate of the starting piece. Defaults to a
     * constant height of {@code 0}.
     *
     * @param value The height provider to use
     * @return This builder instance, for chaining
     */
    JigsawBuilder startHeight(HeightProvider value);

    /**
     * Sets the {@link StructureTemplatePool} generation starts from.
     *
     * @param pool The holder of the starting pool
     * @return This builder instance, for chaining
     */
    JigsawBuilder startPool(Holder<StructureTemplatePool> pool);

    /**
     * Sets the {@link StructureTemplatePool} generation starts from, resolving the holder from the
     * template pool registry of the active {@link net.minecraft.data.worldgen.BootstrapContext}.
     *
     * @param pool The key of the starting pool
     * @return This builder instance, for chaining
     */
    JigsawBuilder startPool(ResourceKey<StructureTemplatePool> pool);

    /**
     * Alias for {@link #startPool(ResourceKey)} that accepts a {@link StructurePoolKey}.
     *
     * @param pool The key of the starting pool
     * @return This builder instance, for chaining
     */
    JigsawBuilder startPool(StructurePoolKey pool);

    /**
     * Adds a list of {@link PoolAliasBinding}s, which remap references to one template pool onto another
     * while this structure generates. None are set by default.
     *
     * @param aliasBindings The alias bindings to add
     * @return This builder instance, for chaining
     */
    JigsawBuilder addAliasBindings(List<PoolAliasBinding> aliasBindings);

    /**
     * Adds a single {@link PoolAliasBinding}. See {@link #addAliasBindings(List)}.
     *
     * @param aliasBinding The alias binding to add
     * @return This builder instance, for chaining
     */
    JigsawBuilder addAliasBinding(PoolAliasBinding aliasBinding);

    /**
     * Sets the {@link LiquidSettings} that control how the structure interacts with liquids in the world.
     * Defaults to {@link JigsawStructure#DEFAULT_LIQUID_SETTINGS}.
     *
     * @param value The liquid settings to use
     * @return This builder instance, for chaining
     */
    JigsawBuilder liquidSettings(LiquidSettings value);

    /**
     * Sets the {@link DimensionPadding} that keeps generated pieces away from the top/bottom of the
     * world. Defaults to {@link JigsawStructure#DEFAULT_DIMENSION_PADDING}.
     *
     * @param value The dimension padding to use
     * @return This builder instance, for chaining
     */
    JigsawBuilder dimensionPadding(DimensionPadding value);
}
