package de.ambertation.wover.block.api.model;

import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockTraitKey;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.entrypoint.LibWoverBlock;

import net.minecraft.world.level.block.Block;

/**
 * The common, client-free runtime trait that attaches a block model to a block. Instead of storing a client
 * {@code ModelFactory} lambda (which would drag vanilla client datagen types into common code), a binding stores
 * only a {@link ModelKey} plus its common-safe {@code payload}. The client source set resolves the key to a
 * {@code ClientModelFactory} through {@code ClientBlockModelRegistry} while walking blocks at datagen time.
 * <p>
 * Bindings are produced by {@link ModelTraitLibrary} (and by third-party helpers) and attached like any other
 * {@link BlockTrait}. They share the {@link #MODEL_TRAIT_KEY} identity with the client-only escape-hatch trait
 * ({@code ClientBlockTraits.MODEL}), so a single datagen walk finds both a block's data-driven bindings and any
 * bespoke client lambdas, in attachment order.
 */
public final class BlockModelBinding extends BlockTraitImpl<Block, BlockModelBinding> implements BlockTrait<Block, BlockModelBinding> {
    /**
     * The shared trait key under which every block model trait (data-driven {@link BlockModelBinding} as well as
     * the client-only escape-hatch trait) is attached to a block, so they can be collected together by the
     * datagen walk.
     */
    public static final BlockTraitKey MODEL_TRAIT_KEY = BlockTraitKey.ofUnique(LibWoverBlock.C, "model");

    private final ModelKey<?> modelKey;
    private final Object payload;

    private BlockModelBinding(ModelKey<?> modelKey, Object payload) {
        this.modelKey = modelKey;
        this.payload = payload;
    }

    /**
     * Creates a binding pairing a model key with its payload.
     *
     * @param modelKey the model shape to generate
     * @param payload  the common-safe payload consumed by the shape's client factory
     * @param <P>      the payload type
     * @return the new binding
     */
    public static <P> BlockModelBinding of(ModelKey<P> modelKey, P payload) {
        return new BlockModelBinding(modelKey, payload);
    }

    /**
     * @return the model shape this binding requests
     */
    public ModelKey<?> modelKey() {
        return modelKey;
    }

    /**
     * @return the common-safe payload for the model shape
     */
    public Object payload() {
        return payload;
    }

    @Override
    public BlockTraitKey key() {
        return MODEL_TRAIT_KEY;
    }

    @Override
    public BlockModelBinding forRuntime() {
        return this;
    }
}
