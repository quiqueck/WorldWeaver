package org.betterx.wover.block.api.trait.behaviour;

import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitBuilder;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.List;

public interface BlockTagTrait extends BlockTrait<Block, BlockTagTrait> {
    interface Builder extends BlockTraitBuilder<Block, BlockTagTrait> {
        BlockTagTrait with(TagKey<Block> blockTag);
        BlockTagTrait with(List<TagKey<Block>> blockTags);
    }

    /**
     * Returns the primary tag key for this trait.
     * This is used for the main tag that the block will be associated with and is always the first tag in the list.
     *
     * @return The primary tag key.
     */
    TagKey<Block> tagKey();

    /**
     * Returns a list of all tag keys associated with this trait.
     * This includes the primary tag and any additional tags that the block may be associated with.
     *
     * @return A list of tag keys.
     */
    List<TagKey<Block>> tags();
}
