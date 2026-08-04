package de.ambertation.wover.block.api;

import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

/**
 * The standard {@link BlockDefinition} implementation for custom block classes, used by
 * {@link BlockRegistry#defineDefaultBlock(String, BlockFactory)} and
 * {@link BlockRegistry#defineDefaultBlockWithProps(String, java.util.function.Function)}.
 * <p>
 * It adds no behavior on top of {@link BlockDefinition} - {@link #beforeBuild()} and
 * {@link #beforeRegister(Block)} are both no-ops.
 *
 * @param <B> The type of {@link Block} this definition creates
 * @author Quiqueck
 * @since 21.6.0
 */
public class DefaultBlockDefinition<B extends Block> extends BlockDefinition<B, DefaultBlockDefinition<B>> {
    protected DefaultBlockDefinition(
            BlockRegistry registry,
            String blockName,
            BlockDefinition.BlockFactory<B, DefaultBlockDefinition<B>> blockFactory
    ) {
        super(registry, blockName, blockFactory);
    }

    @Override
    protected void beforeBuild() {

    }

    @Override
    protected @NotNull B beforeRegister(@NotNull B block) {
        return block;
    }

    /**
     * Factory used to create the {@link Block} instance for a {@link DefaultBlockDefinition}.
     *
     * @param <B> The type of {@link Block} to create
     */
    public interface BlockFactory<B extends Block> extends BlockDefinition.BlockFactory<B, DefaultBlockDefinition<B>> {
    }
}
