package de.ambertation.wover.block.api.trait.behaviour;

import de.ambertation.wover.block.api.trait.GenericBlockTrait;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;

import org.jetbrains.annotations.Nullable;

/**
 * Controls whether a block is a <em>motion blocking</em> block, i.e. whether it participates in
 * {@code minecraft:blocks_motion} and the tags derived from it.
 * <p>
 * <b>Why this trait exists.</b> Up to 26.1 "does this block suffocate / block the view / count for the
 * {@code MOTION_BLOCKING} heightmap" was <em>computed</em> by the engine from the block's collision shape:
 * <pre>
 * // 26.1 BlockBehaviour.Properties
 * isSuffocating = (state, level, pos) -&gt; state.blocksMotion() &amp;&amp; state.isCollisionShapeFullBlock(level, pos);
 * // 26.1 BlockStateBase
 * blocksMotion() = block != COBWEB &amp;&amp; block != BAMBOO_SAPLING &amp;&amp; isSolid();
 * // 26.1 Heightmap.Types
 * MOTION_BLOCKING           = blocksMotion() || !getFluidState().isEmpty()
 * MOTION_BLOCKING_NO_LEAVES = (blocksMotion() || !getFluidState().isEmpty()) &amp;&amp; !(block instanceof LeavesBlock)
 * </pre>
 * 26.3 replaced every one of those {@code blocksMotion()} calls with a <em>data</em> lookup:
 * <pre>
 * isSuffocating             = state.is(BlockTags.CAUSES_SUFFOCATION) &amp;&amp; state.isCollisionShapeFullBlock(level, pos)
 * MOTION_BLOCKING           = state.is(BlockTags.BLOCKS_MOTION_IN_HEIGHTMAP) || !getFluidState().isEmpty()
 * MOTION_BLOCKING_NO_LEAVES = state.is(BlockTags.BLOCKS_MOTION_IN_HEIGHTMAP_NO_LEAVES) || !getFluidState().isEmpty()
 * </pre>
 * and {@code BlockStateBase.blocksMotion()} was deleted. All four of those tags resolve back to two
 * hand-enumerated, <em>vanilla-only</em> source tags:
 * <pre>
 * causes_suffocation              = #blocks_motion
 * blocks_motion_in_heightmap      = #blocks_motion
 * blocks_motion                   = #blocks_motion_no_leaves + #leaves
 * blocks_motion_in_heightmap_no_leaves = #blocks_motion_no_leaves
 * blocks_motion_no_leaves         = &lt;340 hand-written vanilla entries&gt;
 * </pre>
 * Consequently <b>every modded block silently stopped suffocating, stopped blocking the view and stopped
 * counting for the {@code MOTION_BLOCKING}/{@code OCEAN_FLOOR} heightmaps in 26.3</b>, because nothing puts
 * it into {@code minecraft:blocks_motion_no_leaves}.
 * <p>
 * <b>How WoVer restores it.</b> Because the old behaviour was <em>derived</em> rather than authored, WoVer
 * derives it again instead of asking every block author to opt in: {@link
 * de.ambertation.wover.datagen.api.provider.AutoMotionBlockingTagProvider} walks every block registered in
 * the mod's namespace during datagen and emits
 * <ul>
 *     <li>{@link BlockTags#BLOCKS_MOTION_NO_LEAVES} for a solid block, and</li>
 *     <li>{@link BlockTags#BLOCKS_MOTION} for a solid {@link LeavesBlock} (leaves must stay out of the
 *     {@code _NO_LEAVES} variant, exactly as 26.1's {@code !(block instanceof LeavesBlock)} guard did),</li>
 * </ul>
 * using {@code BlockState#isSolid()} - the very same {@code calculateSolid()} value that 26.1's
 * {@code blocksMotion()} was built on, and which 26.3 still computes (as {@code legacySolid}) - as the
 * criterion. The result is a byte-for-byte reproduction of the 26.1 rule.
 * <p>
 * <b>This trait is the override.</b> It is only needed when the derived answer is wrong:
 * <ul>
 *     <li>{@link Builder#never()} - opt <em>out</em>. Use for a solid block that must not block motion
 *     (e.g. one that should stay invisible to the {@code MOTION_BLOCKING} heightmap).</li>
 *     <li>{@link Builder#always()} - opt <em>in</em> for a block the shape heuristic misses, most notably a
 *     {@link Block#hasDynamicShape() dynamic-shape} block (those get no shape cache, so
 *     {@code isSolid()} is always {@code false} for them - vanilla lists e.g. shulker boxes by hand for the
 *     same reason).</li>
 *     <li>{@link Builder#leaves()} - opt in as leaves, i.e. into {@code #blocks_motion} but not into
 *     {@code #blocks_motion_no_leaves}, for a leaf-like block that does not extend {@link LeavesBlock}.</li>
 * </ul>
 * <b>Note that suffocation itself does not need an opt-out here.</b> The vanilla predicate is
 * {@code is(CAUSES_SUFFOCATION) && isCollisionShapeFullBlock(...)}, so a solid-but-not-full block never
 * suffocates regardless of the tag; and a full block that must not suffocate says so the vanilla way, on its
 * properties: {@code .isSuffocating(Blocks::never).isViewBlocking(Blocks::never)} (this is exactly how
 * vanilla glass and leaves - both of which <em>are</em> in {@code blocks_motion_no_leaves}/{@code leaves} -
 * avoid suffocating). Use {@link Builder#never()} only when the block must leave the motion/heightmap tags
 * altogether.
 */
public interface MotionBlockingTrait extends GenericBlockTrait {
    /**
     * Builds {@link MotionBlockingTrait} instances.
     */
    interface Builder extends GenericBlockTrait.Builder {
        /**
         * Forces the block into {@link BlockTags#BLOCKS_MOTION_NO_LEAVES} even if it is not detected as
         * solid (e.g. a {@link Block#hasDynamicShape() dynamic-shape} block, which never gets a shape
         * cache and therefore always reports {@code isSolid() == false}).
         *
         * @return the trait, or {@code null} outside of datagen
         */
        @Nullable MotionBlockingTrait always();

        /**
         * Forces the block into {@link BlockTags#BLOCKS_MOTION} but <em>not</em> into
         * {@link BlockTags#BLOCKS_MOTION_NO_LEAVES}: the block blocks motion and suffocates, but stays out
         * of the {@code MOTION_BLOCKING_NO_LEAVES} heightmap. This is the leaves treatment, for a leaf-like
         * block that does not extend {@link LeavesBlock}.
         *
         * @return the trait, or {@code null} outside of datagen
         */
        @Nullable MotionBlockingTrait leaves();

        /**
         * Opts the block out of the automatic motion-blocking tags entirely - it is added to none of them,
         * whatever its shape says.
         *
         * @return the trait, or {@code null} outside of datagen
         */
        @Nullable MotionBlockingTrait never();
    }

    /**
     * The tag this trait puts the block into.
     *
     * @return {@link BlockTags#BLOCKS_MOTION_NO_LEAVES}, {@link BlockTags#BLOCKS_MOTION}, or {@code null}
     * when the block opts out
     */
    @Nullable TagKey<Block> motionTag();
}
