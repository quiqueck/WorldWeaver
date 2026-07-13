package org.betterx.wover.block.api.trait;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.BlockRegistry;

import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * The wall variant of a sign, built and registered internally by {@link SignBlockDefinition}.
 * <p>
 * Not meant to be constructed directly by mod code; {@link SignBlockDefinition#buildAndRegisterWallSignBlock}
 * creates one for every sign it registers, copying over the loot table and description of the primary sign
 * block via {@link BlockDefinition#overrideLootTable}/{@link BlockDefinition#overrideDescription}.
 */
public final class WallSignBlockDefinition extends BlockDefinition<SignBlock, WallSignBlockDefinition> {
    WallSignBlockDefinition(
            BlockRegistry registry,
            String wallSignName,
            BlockFactory<SignBlock, WallSignBlockDefinition> wallSignFactory,
            BlockBehaviour templateBlock
    ) {
        super(registry, wallSignName, wallSignFactory, templateBlock);
    }

    @Override
    protected void beforeBuild() {

    }

    @Override
    protected SignBlock beforeRegister(SignBlock block) {
        return block;
    }
}
