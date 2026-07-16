package org.betterx.wover.block.api.trait;

import net.minecraft.world.level.block.Block;

/**
 * A {@link BlockTrait} that applies to any {@link Block} rather than to a specific block subclass. Most
 * behaviour-style traits (e.g. {@link org.betterx.wover.block.api.trait.behaviour.FlammableBlockTrait}) use
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
         * {@link #withDefault()} without {@code FLAMMABLE} - for wood that does not burn, such as
         * anything growing in the nether. Identical in every other respect; both variants declare
         * {@code MINEABLE_WITH.needsAxe()}, so datagen output is unaffected.
         *
         * @return the traits, or {@code null} if none apply
         */
        java.util.List<BlockTrait<?, ?>> withFireResistance();
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
