package org.betterx.wover.block.impl.trait.behaviour;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.trait.AbstractBlockTraitBuilder;
import org.betterx.wover.block.api.trait.BlockTraitKey;
import org.betterx.wover.block.api.trait.behaviour.BlockTagTrait;
import org.betterx.wover.block.impl.trait.BlockTraitImpl;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverSets;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public class BlockTagTraitBuilder extends AbstractBlockTraitBuilder<Block, BlockTagTrait> implements BlockTagTrait.Builder {
    public static final BlockTagTrait.Builder BUILDER = new BlockTagTraitBuilder();

    private BlockTagTraitBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "block_tag"));
    }

    @Override
    public BlockTagTrait with(TagKey<Block> blockTag) {
        if (!ModCore.isDatagen()) return null;
        return new TraitSingle(blockTag);
    }

    @Override
    public BlockTagTrait with(List<TagKey<Block>> blockTags) {
        if (!ModCore.isDatagen()) return null;
        return new Trait(blockTags);
    }

    public class TraitSingle extends BlockTraitImpl<Block, BlockTagTrait> implements BlockTagTrait {
        private final @NotNull TagKey<Block> tagKey;

        public TraitSingle(@NotNull TagKey<Block> tagKey) {
            this.tagKey = tagKey;
        }

        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            definition.addTags(tagKey);
        }

        @Override
        public TagKey<Block> tagKey() {
            return tagKey;
        }

        @Override
        public List<TagKey<Block>> tags() {
            return List.of(tagKey);
        }
    }

    public class Trait extends BlockTraitImpl<Block, BlockTagTrait> implements BlockTagTrait {
        private final List<TagKey<Block>> tags;

        public Trait(@NotNull List<TagKey<Block>> tags) {
            assert (!tags.isEmpty());
            this.tags = tags;
        }

        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            tags.forEach(definition::addTags);
        }

        @Override
        public @NotNull TagKey<Block> tagKey() {
            return tags.getFirst();
        }

        @Override
        public List<TagKey<Block>> tags() {
            return tags;
        }
    }
}
