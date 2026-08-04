package de.ambertation.wover.block.mixin;

import de.ambertation.wover.block.api.trait.BlockTraitKey;
import de.ambertation.wover.block.api.trait.BlockWithTraits;
import de.ambertation.wover.block.api.trait.RuntimeBlockTrait;

import net.minecraft.world.level.block.Block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

@Mixin(Block.class)
public class BlockMixin<B extends Block> implements BlockWithTraits<B> {
    @Unique
    private @Nullable Map<BlockTraitKey, List<RuntimeBlockTrait<B, ?>>> wover_traits;


    @Override
    public void wover_setTraits(@Nullable Map<BlockTraitKey, List<RuntimeBlockTrait<B, ?>>> runtimeTraits) {
        this.wover_traits = runtimeTraits;
    }

    @Unique
    public @Nullable Map<BlockTraitKey, List<RuntimeBlockTrait<B, ?>>> wover_traits() {
        return wover_traits;
    }
}
