package de.ambertation.wover.block.api.trait.behaviour;

import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockTraitBuilder;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.List;

/**
 * A {@link BlockTrait} that attaches one or more arbitrary {@link TagKey}s to a block at build time.
 * <p>
 * Unlike {@link MineableWithTagTrait}, this trait does not restrict which tags may be used — it is a generic
 * way to have a block added to any block tag as part of its trait configuration, without calling
 * {@link de.ambertation.wover.block.api.BlockDefinition#addTags} directly.
 */
public interface BlockTagTrait extends BlockTrait<Block, BlockTagTrait> {
    /**
     * Builds {@link BlockTagTrait} instances.
     */
    interface Builder extends BlockTraitBuilder<Block, BlockTagTrait> {
        /**
         * Creates a trait that adds a single block tag.
         *
         * @param blockTag the tag to add
         * @return the new trait
         */
        BlockTagTrait with(TagKey<Block> blockTag);

        /**
         * Creates a trait that adds several block tags at once.
         *
         * @param blockTags the tags to add; the first entry becomes {@link #tagKey()}
         * @return the new trait
         */
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
