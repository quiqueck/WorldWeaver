package de.ambertation.wover.block.api.trait;

import net.minecraft.world.level.block.Block;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Identifies and creates instances of a particular {@link BlockTrait}. Builders are typically exposed as
 * static constants (e.g. {@code FlammableBlockBuilder.BUILDER}) so mod code can add the trait via
 * {@link de.ambertation.wover.block.api.BlockDefinition#addTrait(WithDefault)} or look up its runtime traits
 * via {@link #getRuntimeTraits(Block)}.
 *
 * @param <B> The type of {@link Block} the built traits apply to
 * @param <R> The runtime trait type produced by the built traits
 */
public interface BlockTraitBuilder<B extends Block, R extends RuntimeBlockTrait<B, R>> {
    /**
     * A {@link BlockTraitBuilder} that can create a single default trait instance.
     *
     * @param <B> The type of {@link Block} the built trait applies to
     * @param <R> The runtime trait type produced by the built trait
     */
    interface WithDefault<B extends Block, R extends RuntimeBlockTrait<B, R>> extends BlockTraitBuilder<B, R> {
        /**
         * Creates the default trait instance.
         *
         * @return The default trait, or {@code null} if this builder has none
         */
        @Nullable BlockTrait<?, ?> withDefault();
    }

    /**
     * A {@link BlockTraitBuilder} that can create a set of default trait instances at once.
     *
     * @param <B> The type of {@link Block} the built traits apply to
     * @param <R> The runtime trait type produced by the built traits
     */
    interface WithDefaults<B extends Block, R extends RuntimeBlockTrait<B, R>> extends BlockTraitBuilder<B, R> {
        /**
         * Creates the default trait instances.
         *
         * @return The default traits, or {@code null} if this builder has none
         */
        @Nullable List<BlockTrait<?, ?>> withDefault();
    }

    /**
     * Gets the unique key identifying traits created by this builder.
     *
     * @return The trait key
     */
    @NotNull BlockTraitKey key();

    /**
     * Gets every runtime trait attached to {@code block} that was created by this builder.
     *
     * @param block The block to inspect
     * @return The matching runtime traits, or {@code null} if there are none
     */
    List<R> getRuntimeTraits(B block);
}
