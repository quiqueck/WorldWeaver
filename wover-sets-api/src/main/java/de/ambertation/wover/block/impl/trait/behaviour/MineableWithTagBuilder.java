package de.ambertation.wover.block.impl.trait.behaviour;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.AbstractBlockTraitBuilder;
import de.ambertation.wover.block.api.trait.BlockTraitKey;
import de.ambertation.wover.block.api.trait.GenericBlockTrait;
import de.ambertation.wover.block.api.trait.behaviour.MineableWithTagTrait;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverBlock;
import de.ambertation.wover.tag.api.predefined.MineableTags;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.Nullable;

public class MineableWithTagBuilder extends AbstractBlockTraitBuilder<Block, GenericBlockTrait> implements MineableWithTagTrait.Builder {
    public static final MineableWithTagTrait.Builder BUILDER = new MineableWithTagBuilder();

    private MineableWithTagBuilder() {
        super(BlockTraitKey.of(LibWoverBlock.C, "mineable_with"));
    }

    public @Nullable MineableWithTagTrait needsPickAxe() {
        if (!ModCore.isDatagen()) return null;
        return new Trait(MineableTags.PICKAXE);
    }

    public @Nullable MineableWithTagTrait needsAxe() {
        if (!ModCore.isDatagen()) return null;
        return new Trait(MineableTags.AXE);
    }

    public @Nullable MineableWithTagTrait needsHoe() {
        if (!ModCore.isDatagen()) return null;
        return new Trait(MineableTags.HOE);
    }

    public @Nullable MineableWithTagTrait needsShovel() {
        if (!ModCore.isDatagen()) return null;
        return new Trait(MineableTags.SHOVEL);
    }

    public @Nullable MineableWithTagTrait needsShears() {
        if (!ModCore.isDatagen()) return null;
        return new Trait(MineableTags.SHEARS);
    }

    public @Nullable MineableWithTagTrait needsSword() {
        if (!ModCore.isDatagen()) return null;
        return new Trait(MineableTags.SWORD);
    }

    public @Nullable MineableWithTagTrait needsHammer() {
        if (!ModCore.isDatagen()) return null;
        return new Trait(MineableTags.HAMMER);
    }

    public @Nullable MineableWithTagTrait needsNetheriteTool() {
        if (!ModCore.isDatagen()) return null;
        return new Trait(MineableTags.NEEDS_NETHERITE_TOOL);
    }

    public @Nullable MineableWithTagTrait needsGoldTool() {
        if (!ModCore.isDatagen()) return null;
        return new Trait(MineableTags.NEEDS_GOLD_TOOL);
    }

    public @Nullable MineableWithTagTrait needsWoodTool() {
        if (!ModCore.isDatagen()) return null;
        return new Trait(MineableTags.NEEDS_WOOD_TOOL);
    }

    class Trait extends BlockTraitImpl<Block, GenericBlockTrait> implements MineableWithTagTrait {
        public final TagKey<Block> mineableTag;

        Trait(TagKey<Block> mineableTag) {
            this.mineableTag = mineableTag;
        }

        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            definition.addTags(this.mineableTag);
        }

        @Override
        public TagKey<Block> mineableTag() {
            return this.mineableTag;
        }
    }
}
