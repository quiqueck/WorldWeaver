package de.ambertation.wover.block.api.trait;


import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

/**
 * Implemented by a {@link Block} subclass that wants to expose the {@link RuntimeBlockTrait}s collected by
 * its {@link de.ambertation.wover.block.api.BlockDefinition} at runtime. WoVer never generates such a class
 * automatically - the block class must implement this interface itself (e.g. via a mixin) so
 * {@link de.ambertation.wover.block.api.BlockDefinition#build()} can attach the traits.
 *
 * @param <B> The concrete block type
 */
public interface BlockWithTraits<B extends Block> {
    /**
     * Called once by {@link de.ambertation.wover.block.api.BlockDefinition#build()} to attach the traits that
     * were configured for this block.
     *
     * @param traits The runtime traits, grouped by their {@link BlockTraitKey}
     */
    void wover_setTraits(@Nullable Map<BlockTraitKey, List<RuntimeBlockTrait<B, ?>>> traits);

    /**
     * Gets the runtime traits attached to this block.
     *
     * @return The runtime traits, grouped by their {@link BlockTraitKey}, or {@code null} if none were set
     */
    @Nullable Map<BlockTraitKey, List<RuntimeBlockTrait<B, ?>>> wover_traits();
}
