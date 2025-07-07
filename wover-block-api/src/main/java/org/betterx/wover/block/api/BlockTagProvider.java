package org.betterx.wover.block.api;

import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

/**
 * If a Block Object implements this interface and is registered with the BlockRegistry, #registerBlockTags will be called
 * during the tag bootstrap datagen process.
 */
//TODO: @Deprecated(forRemoval = true, since = "21.6.0")
public interface BlockTagProvider {
    /**
     * Register block tags for the given location.
     *
     * @param location The location of the block as it was registered with then BlockRegistry
     * @param context  The context to add tags to
     */
    void registerBlockTags(ResourceLocation location, TagBootstrapContext<Block> context);
}
