package de.ambertation.wover.block.api.client.model;

import de.ambertation.wover.block.api.model.ModelKey;
import de.ambertation.wover.block.api.model.WoverBlockModelGenerators;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * The client-side generator behind a {@link ModelKey}: produces a block's blockstate/model (and any item model)
 * from a common-safe payload at datagen time. Registered against its key in {@code ClientBlockModelRegistry} and
 * invoked by the datagen walk for every block carrying a matching {@code BlockModelBinding}.
 *
 * @param <P> the payload type of the {@link ModelKey} this factory is registered for
 */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface ClientModelFactory<P> {
    /**
     * Generates the blockstate/model (and any item model) for a single block.
     *
     * @param key       the registry key of the block
     * @param block     the block to generate models for
     * @param generator the model generator to use
     * @param payload   the common-safe payload stored on the block's binding
     */
    void provide(ResourceKey<Block> key, Block block, WoverBlockModelGenerators generator, P payload);
}
