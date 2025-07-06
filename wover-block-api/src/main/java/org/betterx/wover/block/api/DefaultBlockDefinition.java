package org.betterx.wover.block.api;

import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

/**
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

    public interface BlockFactory<B extends Block> extends BlockDefinition.BlockFactory<B, DefaultBlockDefinition<B>> {
    }
}
