package de.ambertation.wover.block.impl.trait.behaviour;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.AbstractBlockTraitBuilder;
import de.ambertation.wover.block.api.trait.BlockTraitKey;
import de.ambertation.wover.block.api.trait.GenericBlockTrait;
import de.ambertation.wover.block.api.trait.behaviour.MotionBlockingTrait;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverBlock;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.Nullable;

/**
 * Builds the three {@link MotionBlockingTrait} overrides. See {@link MotionBlockingTrait} for what each of
 * them means and why the trait is an override rather than the primary mechanism.
 */
public class MotionBlockingBuilder extends AbstractBlockTraitBuilder<Block, GenericBlockTrait> implements MotionBlockingTrait.Builder {
    public static final MotionBlockingTrait.Builder BUILDER = new MotionBlockingBuilder();

    /**
     * The key {@link de.ambertation.wover.datagen.api.provider.AutoMotionBlockingTagProvider} looks the
     * trait up under.
     */
    public static final BlockTraitKey KEY = BlockTraitKey.ofUnique(LibWoverBlock.C, "motion_blocking");

    private final Trait ALWAYS = new Trait(BlockTags.BLOCKS_MOTION_NO_LEAVES);
    private final Trait LEAVES = new Trait(BlockTags.BLOCKS_MOTION);
    private final Trait NEVER = new Trait(null);

    private MotionBlockingBuilder() {
        super(KEY);
    }

    @Override
    public @Nullable MotionBlockingTrait always() {
        if (!ModCore.isDatagen()) return null;
        return ALWAYS;
    }

    @Override
    public @Nullable MotionBlockingTrait leaves() {
        if (!ModCore.isDatagen()) return null;
        return LEAVES;
    }

    @Override
    public @Nullable MotionBlockingTrait never() {
        if (!ModCore.isDatagen()) return null;
        return NEVER;
    }

    private final class Trait extends BlockTraitImpl<Block, GenericBlockTrait> implements MotionBlockingTrait {
        private final @Nullable TagKey<Block> motionTag;

        private Trait(@Nullable TagKey<Block> motionTag) {
            this.motionTag = motionTag;
        }

        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        /**
         * The trait is looked up on the finished block by
         * {@link de.ambertation.wover.datagen.api.provider.AutoMotionBlockingTagProvider}, so - unlike the
         * other tag traits - it has to survive as a runtime trait instead of just calling
         * {@link BlockDefinition#addTags} here. Emitting the tag from the provider (rather than from
         * {@code configure()}) also keeps the "one block, one decision" invariant: the provider sees both
         * the derived answer and the override and writes exactly one of them.
         */
        @Override
        public GenericBlockTrait forRuntime() {
            return this;
        }

        /**
         * Only the most recent override for a block counts, so a definition that inherits e.g.
         * {@code always()} from a shared bundle can still cancel it with a later {@code never()}.
         */
        @Override
        public boolean keepLatestOnly() {
            return true;
        }

        @Override
        public @Nullable TagKey<Block> motionTag() {
            return this.motionTag;
        }
    }
}
