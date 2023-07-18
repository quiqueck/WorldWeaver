package org.betterx.wover.tag.api;

import org.betterx.wover.tag.api.builder.TagBuilder;

import net.minecraft.world.level.block.Block;

/**
 * Provides block tags during DataGen.
 * <p>
 * When a Block implements this interface and is registered to the {@link net.minecraft.core.registries.BuiltInRegistries#BLOCK}
 * Registry, the {@code WoverDataGenEntryPoint} will automatically call
 * {@link #addBlockTags(TagBuilder)} to add ItemTags to the global pack.
 */
public interface BlockTagDataProvider {
    /**
     * Adds item tags to the global pack.
     * <p>
     * <b>Important:</b> This method is only called during DataGen.
     *
     * @param builder The builder you can use to configure your Tags.
     */
    void addBlockTags(TagBuilder<Block> builder);
}
