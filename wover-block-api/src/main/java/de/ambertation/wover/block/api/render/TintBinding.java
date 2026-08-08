package de.ambertation.wover.block.api.render;

import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockTraitKey;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The common, client-free runtime trait that gives a block a tint colour. Instead of storing a client
 * {@code BlockTintSource} (which would drag client-only types into common code), a binding stores only a
 * {@link TinterKey} plus its common-safe {@code payload}; the client source set resolves the key to a
 * {@code TintSourceFactory} through {@code ClientTinterRegistry}.
 * <p>
 * The resolved {@code BlockTintSource} drives both halves of a block's colour:
 * <ul>
 *     <li>the <b>in-world</b> block colour, registered against {@code BlockColors} at client init;</li>
 *     <li>the <b>item</b> tint, baked as a constant into the generated item model - but only when
 *     {@link #tintItemModel()} is set, because a block whose item has its own fully coloured texture must not
 *     have a tint multiplied over it.</li>
 * </ul>
 * This replaces the former {@code CustomColorProvider} interface: a block no longer implements anything, it
 * declares a binding like any other trait.
 * <p>
 * <b>Client-gated, not datagen-gated.</b> Unlike the model bindings, this is genuinely read at runtime - the
 * in-world colour walk happens on every client launch - so the gate is {@code ModCore.isClient()} alone. On a
 * dedicated server nothing is constructed or attached, since colour is purely visual.
 */
public final class TintBinding extends BlockTraitImpl<Block, TintBinding> implements BlockTrait<Block, TintBinding> {
    /** The trait key every tint binding is attached under. */
    public static final BlockTraitKey TINT_KEY = BlockTraitKey.ofUnique(LibWoverBlock.C, "tint");

    private final @NotNull TinterKey<?> tinterKey;
    private final @Nullable Object payload;
    private final boolean tintItemModel;
    private final @Nullable Function<Block, BlockState> itemSampleState;

    private TintBinding(
            @NotNull TinterKey<?> tinterKey,
            @Nullable Object payload,
            boolean tintItemModel,
            @Nullable Function<Block, BlockState> itemSampleState
    ) {
        this.tinterKey = tinterKey;
        this.payload = payload;
        this.tintItemModel = tintItemModel;
        this.itemSampleState = itemSampleState;
    }

    /**
     * Creates a binding pairing a tinter key with its payload.
     * <p>
     * The payload arrives as a {@link Supplier} and is resolved <em>after</em> the environment gate, so on a
     * dedicated server - where the binding is discarded - the payload object is never constructed at all. Pass a
     * shared constant where several blocks want the same tint, so the payload is allocated once rather than once
     * per block.
     *
     * @param tinterKey       the tint shape to use
     * @param payload         supplies the common-safe payload consumed by the shape's client factory
     * @param tintItemModel   whether the generated item model should carry this colour as a constant tint
     * @param itemSampleState resolves the state to sample the item tint from, or {@code null} for the block's
     *                        default state. Only consulted when {@code tintItemModel} is set
     * @param <P>             the payload type
     * @return the new binding, or {@code null} outside a client environment
     */
    public static <P> TintBinding of(
            @NotNull TinterKey<P> tinterKey,
            @NotNull Supplier<P> payload,
            boolean tintItemModel,
            @Nullable Function<Block, BlockState> itemSampleState
    ) {
        if (!ModCore.isClient()) return null;
        return new TintBinding(tinterKey, payload.get(), tintItemModel, itemSampleState);
    }

    /**
     * @return the tint shape this binding requests
     */
    public @NotNull TinterKey<?> tinterKey() {
        return tinterKey;
    }

    /**
     * @return the common-safe payload for the tint shape
     */
    public @Nullable Object payload() {
        return payload;
    }

    /**
     * @return whether the generated item model should carry this colour as a constant tint
     */
    public boolean tintItemModel() {
        return tintItemModel;
    }

    /**
     * @return the state to sample the item tint from for {@code block} - the block's default state unless the
     * binding names another one
     */
    public BlockState itemSampleState(Block block) {
        return itemSampleState == null ? block.defaultBlockState() : itemSampleState.apply(block);
    }

    @Override
    public BlockTraitKey key() {
        return TINT_KEY;
    }

    @Override
    public TintBinding forRuntime() {
        return this;
    }

    @Override
    public boolean keepLatestOnly() {
        return true;
    }
}
