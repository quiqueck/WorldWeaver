package org.betterx.wover.block.api.trait;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverBlock;
import org.betterx.wover.tag.api.predefined.MineableTags;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.Nullable;

public class MineableWithTrait extends BlockTrait<Block, BlockTrait.VoidRuntime<Block>> {
    public static final MineableWithTrait.Builder BUILDER = new MineableWithTrait.Builder();

    public static class Builder extends BlockTrait.TraitBuilder {
        private Builder() {
            super(BlockTraitKey.of(LibWoverBlock.C, "mineable_with"));
        }

        public @Nullable MineableWithTrait needsPickAxe() {
            if (!ModCore.isDatagen()) return null;
            return new MineableWithTrait(MineableTags.PICKAXE);
        }

        public @Nullable MineableWithTrait needsAxe() {
            if (!ModCore.isDatagen()) return null;
            return new MineableWithTrait(MineableTags.AXE);
        }

        public @Nullable MineableWithTrait needsHoe() {
            if (!ModCore.isDatagen()) return null;
            return new MineableWithTrait(MineableTags.HOE);
        }

        public @Nullable MineableWithTrait needsShovel() {
            if (!ModCore.isDatagen()) return null;
            return new MineableWithTrait(MineableTags.SHOVEL);
        }

        public @Nullable MineableWithTrait needsShears() {
            if (!ModCore.isDatagen()) return null;
            return new MineableWithTrait(MineableTags.SHEARS);
        }

        public @Nullable MineableWithTrait needsSword() {
            if (!ModCore.isDatagen()) return null;
            return new MineableWithTrait(MineableTags.SWORD);
        }

        public @Nullable MineableWithTrait needsHammer() {
            if (!ModCore.isDatagen()) return null;
            return new MineableWithTrait(MineableTags.HAMMER);
        }

        public @Nullable MineableWithTrait needsNetheriteTool() {
            if (!ModCore.isDatagen()) return null;
            return new MineableWithTrait(MineableTags.NEEDS_NETHERITE_TOOL);
        }

        public @Nullable MineableWithTrait needsGoldTool() {
            if (!ModCore.isDatagen()) return null;
            return new MineableWithTrait(MineableTags.NEEDS_GOLD_TOOL);
        }

        public @Nullable MineableWithTrait needsWoodTool() {
            if (!ModCore.isDatagen()) return null;
            return new MineableWithTrait(MineableTags.NEEDS_WOOD_TOOL);
        }
    }

    public final TagKey<Block> mineableTag;

    MineableWithTrait(TagKey<Block> mineableTag) {
        super(BUILDER.ID);
        this.mineableTag = mineableTag;
    }

    @Override
    public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
        definition.addTags(this.mineableTag);
    }
}
