package de.ambertation.wover.block.api.trait;

import net.minecraft.world.level.block.Block;

/**
 * A {@link BlockTrait} that applies to any {@link Block} rather than to a specific block subclass. Most
 * behaviour-style traits (e.g. {@link de.ambertation.wover.block.api.trait.behaviour.FlammableBlockTrait}) use
 * this as their base, since they don't need any specific block subclass to function.
 */
public interface GenericBlockTrait extends BlockTrait<Block, GenericBlockTrait> {
    /**
     * A {@link BlockTraitBuilder.WithDefaults} for {@link GenericBlockTrait}s.
     */
    interface BuilderWithDefaults extends BlockTraitBuilder.WithDefaults<Block, GenericBlockTrait> {
    }

    /**
     * The wood material's builder. Wood is flammable by default, so it also offers a fire-resistant
     * variant for woods that must not burn.
     */
    interface WoodBuilderWithDefaults extends BuilderWithDefaults {
        /**
         * The non-flammable wood bundle for nether woods (user decision 6): {@link #withDefault()} minus the
         * {@code FLAMMABLE} trait, keeping the wood classification (mapColor/instrument/strength/sound) and the
         * axe mineable tag. Nether woods never burn, so this - not {@link #withDefault()} - is what a nether wood
         * block, plank, or stripped variant carries; furnace fuel (for planks and stripped variants) is added
         * separately as an explicit {@code FuelBlockTrait} at the registration site. Identical in every other
         * respect to {@link #withDefault()}; both variants declare {@code MINEABLE_WITH.needsAxe()}, so datagen
         * output is unaffected.
         *
         * @return the non-flammable wood traits, or {@code null} if none apply
         */
        java.util.List<BlockTrait<?, ?>> netherWood();
    }

    /**
     * A material builder that, alongside its full {@link #withDefault()} classification, offers a
     * <em>properties-free</em> variant that contributes only the material's mineable/tool tag(s) and mutates no
     * block property.
     * <p>
     * Used for blocks whose {@link #withDefault()} strength would be a regression on an intentionally tough (or
     * otherwise custom-strength) block - e.g. the obsidian brick/tile stairs and slabs (obsidian 50/1200) and the
     * netherite fire bowls, which want the pickaxe tag but must keep their own strength. This is the "task #32"
     * pattern BetterNether's {@code NetherMaterial.stoneTagOnly()}/{@code metalTagOnly()} stood in for.
     */
    interface TagOnlyBuilderWithDefaults extends BuilderWithDefaults {
        /**
         * The material's mineable/tool tag(s) only, with <b>no</b> property mutation (unlike
         * {@link #withDefault()}, which also forces instrument/strength/etc.). Like every tag trait it is
         * datagen-only, so the returned list is empty outside datagen.
         *
         * @return the tag-only classification traits
         */
        java.util.List<BlockTrait<?, ?>> tagOnly();
    }

    /**
     * The ore material's builder. In addition to {@link #withDefault()} (the classification: {@code c:ores},
     * the pickaxe-mineable tag and the {@code BaseOreBlock} constructor defaults), it offers
     * {@link #dropping(java.util.function.Supplier, int, int)}, which returns the same classification bundle
     * <b>with the vanilla ore-drop loot trait already attached</b>, so an ore block needs a single trait call
     * instead of {@code withDefault()} plus a separate {@code LOOT_TABLE.dropOre(...)}.
     */
    interface OreBuilderWithDefaults extends BuilderWithDefaults {
        /**
         * {@link #withDefault()} plus a {@code LOOT_TABLE.dropOre(drop, min, max)} loot trait: the ore
         * classification and its standard vanilla loot table (Silk Touch drops the block, otherwise a
         * fortune-boosted {@code min..max} of {@code drop}) as one bundle. Equivalent to adding
         * {@code ORE_BLOCK.withDefault()} and {@code BlockTraits.LOOT_TABLE.dropOre(drop, min, max)}
         * separately; the loot trait is dropped (like every datagen-only trait) outside datagen.
         *
         * @param drop supplies the item the ore drops (read lazily, so it may reference a not-yet-assigned
         *             registry field)
         * @param min  the minimum number of items to drop before the fortune bonus
         * @param max  the maximum number of items to drop before the fortune bonus
         * @return the classification traits plus the ore-loot trait
         */
        java.util.List<BlockTrait<?, ?>> dropping(
                java.util.function.Supplier<net.minecraft.world.item.Item> drop,
                int min,
                int max
        );
    }

    /**
     * A {@link BlockTraitBuilder.WithDefault} for {@link GenericBlockTrait}s.
     */
    interface BuilderWithDefault extends BlockTraitBuilder.WithDefault<Block, GenericBlockTrait> {
    }

    /**
     * A {@link BlockTraitBuilder} for {@link GenericBlockTrait}s.
     */
    interface Builder extends BlockTraitBuilder<Block, GenericBlockTrait> {
    }
}
