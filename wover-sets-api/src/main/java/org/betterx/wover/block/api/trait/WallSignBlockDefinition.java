package org.betterx.wover.block.api.trait;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.BlockRegistry;

import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

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
