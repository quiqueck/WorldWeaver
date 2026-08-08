package de.ambertation.wover.datagen.api.provider;

import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.behaviour.MotionBlockingTrait;
import de.ambertation.wover.block.impl.trait.behaviour.MotionBlockingBuilder;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverAutoProvider;
import de.ambertation.wover.datagen.api.WoverTagProvider;
import de.ambertation.wover.entrypoint.LibWoverBlock;
import de.ambertation.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.PlainSignBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

/**
 * Restores 26.1's <em>motion blocking</em> behaviour for every block in this mod's namespace by emitting the
 * {@code minecraft:blocks_motion*} tags 26.3 now derives that behaviour from.
 * <p>
 * 26.3 turned {@code BlockStateBase#blocksMotion()} (a computed property) into a set of hand-enumerated,
 * vanilla-only block tags, which silently stopped every modded block from suffocating, from blocking the
 * view, and from counting for the {@code MOTION_BLOCKING}/{@code MOTION_BLOCKING_NO_LEAVES}/{@code
 * OCEAN_FLOOR} heightmaps. See {@link MotionBlockingTrait} for the full before/after of the vanilla code.
 * <p>
 * Rather than making every block author opt in - the property was never authored, it was derived, and ~130
 * BetterNether blocks alone would have needed the call, each omission a silent bug - this provider derives
 * it again, from the exact same value the old code used:
 * <pre>
 * 26.1: blocksMotion()           = block != COBWEB &amp;&amp; block != BAMBOO_SAPLING &amp;&amp; isSolid()
 * here: solid                    = any BlockState of the block reports isSolid()
 * </pre>
 * {@code isSolid()} is {@code calculateSolid()}'s cached result ({@code legacySolid}); 26.3 still computes
 * it, byte-identically to 26.1 (forceSolidOn / forceSolidOff / no shape cache / empty collision shape /
 * {@code bounds.getSize() >= 0.7291666666666666} / {@code bounds.getYsize() >= 1.0}).
 * <p>
 * The emitted tag is
 * <ul>
 *     <li>{@link BlockTags#BLOCKS_MOTION_NO_LEAVES} for a solid non-leaf block. Everything else follows
 *     from vanilla's own tag graph: {@code causes_suffocation = #blocks_motion = #blocks_motion_no_leaves +
 *     #leaves}, {@code blocks_motion_in_heightmap = #blocks_motion} and
 *     {@code blocks_motion_in_heightmap_no_leaves = #blocks_motion_no_leaves}, so this single entry
 *     restores suffocation, view blocking and all three heightmaps at once.</li>
 *     <li>{@link BlockTags#BLOCKS_MOTION} for a solid {@link LeavesBlock}, which is the 26.1 rule
 *     {@code MOTION_BLOCKING_NO_LEAVES = blocksMotion() && !(block instanceof LeavesBlock)} expressed as
 *     data: leaves block motion and suffocate, but stay out of {@code MOTION_BLOCKING_NO_LEAVES}.</li>
 * </ul>
 * A block can override the derived answer with {@link MotionBlockingTrait} ({@code always()} /
 * {@code leaves()} / {@code never()}); the override always wins and is the only thing written for that
 * block.
 * <p>
 * <b>This does not make anything newly suffocate that did not suffocate on 26.1.</b> Vanilla's suffocation
 * predicate keeps its geometry gate ({@code isCollisionShapeFullBlock}), and a block that opts out on its
 * properties ({@code .isSuffocating(Blocks::never).isViewBlocking(Blocks::never)}, which is how vanilla
 * glass and all leaves do it) still never suffocates - the tag only restores the {@code blocksMotion()} half
 * of the 26.1 expression.
 * <p>
 * <b>One deliberate exception to the {@code isSolid()} derivation:</b> standing and wall signs
 * ({@link net.minecraft.world.level.block.PlainSignBlock}) are excluded even though they are
 * {@code forceSolidOn}, because vanilla 26.3's own hand-written {@code blocks_motion_no_leaves} excludes
 * them too - only hanging signs ({@link net.minecraft.world.level.block.HangingSignBlock}, via
 * {@code #minecraft:all_hanging_signs}) are members. This diverges from 26.1 (where signs, being solid,
 * were motion-blocking) in favour of matching current vanilla exactly; a block author can still opt a sign
 * back in with {@link MotionBlockingTrait.Builder#always()}.
 * <p>
 * Registered automatically for every mod by {@link LibWoverBlock}.
 */
public class AutoMotionBlockingTagProvider extends WoverTagProvider.ForBlocks implements WoverAutoProvider {
    /**
     * Creates a new provider for the given mod. Only blocks in the mod's own namespace are considered.
     *
     * @param modCore The mod this provider generates the motion-blocking tags for
     */
    public AutoMotionBlockingTagProvider(ModCore modCore) {
        super(modCore);
    }

    @Override
    public void prepareTags(TagBootstrapContext<Block> context) {
        int solid = 0;
        int leaves = 0;
        int overridden = 0;

        for (var entry : BuiltInRegistries.BLOCK.entrySet()) {
            if (modIDs != null && !modIDs.contains(entry.getKey().identifier().getNamespace())) continue;

            final Block block = entry.getValue();
            if (override(block) != null) overridden++;
            final TagKey<Block> tag = motionTag(block);

            if (tag == null) continue;
            if (tag == BlockTags.BLOCKS_MOTION) leaves++;
            else solid++;
            context.add(block, tag);
        }

        LibWoverBlock.C.LOG.debug(
                "   AutoMotionBlockingTagProvider ({}): {} blocks_motion_no_leaves, {} blocks_motion (leaves), {} explicit overrides",
                modCore.namespace, solid, leaves, overridden
        );
    }

    /**
     * The single motion-blocking tag this provider writes for the block: the {@link MotionBlockingTrait}
     * override when one is attached, otherwise the answer derived from the block's shape (see the class
     * documentation for the full rule). {@code null} means "member of none of the motion tags".
     * <p>
     * Exposed so that the audit written by
     * {@link de.ambertation.wover.datagen.api.provider.BlockPropertiesProvider} can report the value
     * {@code isSuffocating}/{@code isViewBlocking} <em>will</em> have at runtime without duplicating the
     * derivation: block tags are unbound during datagen, so the vanilla predicate
     * ({@code state.is(BlockTags.CAUSES_SUFFOCATION) && state.isCollisionShapeFullBlock(...)}) cannot simply
     * be probed there. Sharing this one method keeps the audit and the emitted tags from ever drifting apart.
     *
     * @param block the block to classify
     * @return {@link BlockTags#BLOCKS_MOTION_NO_LEAVES}, {@link BlockTags#BLOCKS_MOTION}, or {@code null}
     */
    public static @Nullable TagKey<Block> motionTag(Block block) {
        final MotionBlockingTrait override = override(block);
        if (override != null) return override.motionTag();
        if (!isSolid(block)) return null;
        // Standing and wall signs (PlainSignBlock) are forceSolidOn like every other sign, but vanilla 26.3's
        // own hand-written blocks_motion_no_leaves excludes them - only hanging signs (HangingSignBlock, via
        // #minecraft:all_hanging_signs) are members. Match vanilla exactly rather than deriving from isSolid().
        if (block instanceof PlainSignBlock) return null;
        if (block instanceof LeavesBlock) return BlockTags.BLOCKS_MOTION;
        return BlockTags.BLOCKS_MOTION_NO_LEAVES;
    }

    /**
     * Whether the block ends up in {@code #minecraft:blocks_motion} (directly or through
     * {@code #minecraft:blocks_motion_no_leaves}), i.e. whether it is a member of
     * {@code #minecraft:causes_suffocation} - the tag half of vanilla's suffocation and view-blocking
     * predicate.
     *
     * @param block the block to classify
     * @return {@code true} when {@link #motionTag(Block)} puts it into one of the motion tags
     */
    public static boolean blocksMotion(Block block) {
        return motionTag(block) != null;
    }

    /**
     * The explicit {@link MotionBlockingTrait} attached to the block, if any.
     */
    private static @Nullable MotionBlockingTrait override(Block block) {
        return BlockTrait
                .runtimeTraits(block)
                .filter(t -> t.is(MotionBlockingBuilder.KEY))
                .filter(MotionBlockingTrait.class::isInstance)
                .map(MotionBlockingTrait.class::cast)
                .findFirst()
                .orElse(null);
    }

    /**
     * Whether any state of the block is solid. "Any" rather than "the default state" mirrors vanilla's own
     * per-<em>block</em> list: {@code #minecraft:doors} and {@code #minecraft:trapdoors} are wholesale
     * members of {@code blocks_motion_no_leaves} even though their open states are not solid. Suffocation
     * stays per-state correct anyway because vanilla still gates it on
     * {@code isCollisionShapeFullBlock(level, pos)}.
     */
    private static boolean isSolid(Block block) {
        for (BlockState state : block.getStateDefinition().getPossibleStates()) {
            if (state.isSolid()) return true;
        }
        return false;
    }
}
