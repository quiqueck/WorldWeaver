package de.ambertation.wover.block.api.render;

import de.ambertation.wover.block.api.trait.BlockTrait;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Builder for {@link TintBinding}, exposed as {@code BlockRenderTraits.TINT} (and aliased as
 * {@code ClientBlockTraits.TINT}). Mirrors {@link RenderLayerBuilder}: the factories return {@code null} when
 * the binding is not needed, so they can be passed straight to {@code addTrait(...)} from common block-creation
 * code.
 * <p>
 * Every factory takes the payload as a {@link Supplier} so nothing is allocated on a dedicated server, where the
 * binding is discarded. Where several blocks share a colour, hold the payload in a shared constant and pass
 * {@code () -> THAT_CONSTANT} - or better, share the binding itself, since bindings are immutable and can be
 * attached to any number of blocks.
 */
public final class TintBuilder {
    /** Shared instance; also exposed on the client as {@code ClientBlockTraits.TINT}. */
    public static final TintBuilder BUILDER = new TintBuilder();

    private TintBuilder() {
    }

    /**
     * Tints the block in the world only, leaving its item model untinted. The right choice when the block's item
     * has its own fully coloured texture - multiplying a tint over that would double-colour it.
     *
     * @param key     the tint shape
     * @param payload supplies the payload for that shape
     * @param <P>     the payload type
     * @return the binding, or {@code null} outside a client environment
     */
    public <P> @Nullable BlockTrait<?, ?> world(@NotNull TinterKey<P> key, @NotNull Supplier<P> payload) {
        return TintBinding.of(key, payload, false, null);
    }

    /**
     * Tints the block in the world and bakes the colour at the block's default state into its generated item
     * model. The right choice when the item reuses the block's grayscale texture.
     *
     * @param key     the tint shape
     * @param payload supplies the payload for that shape
     * @param <P>     the payload type
     * @return the binding, or {@code null} outside a client environment
     */
    public <P> @Nullable BlockTrait<?, ?> worldAndItem(@NotNull TinterKey<P> key, @NotNull Supplier<P> payload) {
        return TintBinding.of(key, payload, true, null);
    }

    /**
     * As {@link #worldAndItem(TinterKey, Supplier)}, but samples the item tint at an explicit state.
     * <p>
     * Needed when a block's colour property defaults to one end of a gradient while the states that actually
     * generate cluster elsewhere: there the default state is a shade the player never sees in the world, so the
     * inventory icon would be tinted wrongly.
     *
     * @param key             the tint shape
     * @param payload         supplies the payload for that shape
     * @param itemSampleState resolves the state to sample, for the block the trait is attached to
     * @param <P>             the payload type
     * @return the binding, or {@code null} outside a client environment
     */
    public <P> @Nullable BlockTrait<?, ?> worldAndItem(
            @NotNull TinterKey<P> key,
            @NotNull Supplier<P> payload,
            @NotNull Function<Block, BlockState> itemSampleState
    ) {
        return TintBinding.of(key, payload, true, itemSampleState);
    }

    /**
     * Shorthand for a fixed colour tinting both the block and its item.
     *
     * @param argb the packed ARGB colour
     * @return the binding, or {@code null} outside a client environment
     */
    public @Nullable BlockTrait<?, ?> constColor(int argb) {
        return worldAndItem(TinterKeys.CONST_COLOR, () -> argb);
    }

    /**
     * Shorthand for a fixed colour tinting the block in the world only.
     *
     * @param argb the packed ARGB colour
     * @return the binding, or {@code null} outside a client environment
     */
    public @Nullable BlockTrait<?, ?> constColorWorldOnly(int argb) {
        return world(TinterKeys.CONST_COLOR, () -> argb);
    }
}
