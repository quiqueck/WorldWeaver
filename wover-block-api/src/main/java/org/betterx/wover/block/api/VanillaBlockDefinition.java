package org.betterx.wover.block.api;

import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

/**
 * The {@link BlockDefinition} implementation used for plain vanilla {@link Block} instances, obtained via
 * {@link BlockRegistry#defineDefaultBlock(String)}.
 *
 * @author Quiqueck
 * @since 21.6.0
 */
public final class VanillaBlockDefinition extends BlockDefinition<Block, VanillaBlockDefinition> {
    /**
     * The default factory, creating a plain {@link Block} from the configured properties.
     */
    public static final BlockFactory<Block, VanillaBlockDefinition> DEFAULT_FACTORY =
            config -> new Block(config.properties);

    VanillaBlockDefinition(BlockRegistry registry, String blockName) {
        super(registry, blockName, DEFAULT_FACTORY);
    }

    @Override
    protected void beforeBuild() {
    }

    @Override
    protected @NotNull Block beforeRegister(@NotNull Block block) {
        return block; // No modifications needed for vanilla blocks
    }
}
