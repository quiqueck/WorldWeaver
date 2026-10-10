package de.ambertation.wover.test.api.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Checks that a mod's plants are washed away by flowing water and lava the way vanilla plants are.
 * <p>
 * Up to 26.2 {@code FlowingFluid#canHoldAnyFluid} let a fluid flow into (and break) every block that did
 * not {@code blocksMotion()}, so a modded plant without a collision shape was washed away for free. 26.3
 * replaced that with {@code state.is(BlockTags.WASHED_AWAY_BY_FLUIDS)}, a hand-enumerated vanilla tag that
 * lists every vanilla non-colliding plant (grass, flowers, saplings, crops, mushrooms, fungi, roots, vines,
 * lichen, seagrass, kelp, ...) but none of the colliding ones (cactus, bamboo, leaves). A modded plant is
 * therefore only washed away if its datagen puts it into the tag.
 * <p>
 * {@link #findTagMismatches} applies that vanilla rule to every plant of a namespace: a plant without a
 * collision shape must be in the tag, a colliding one must not. {@link LiquidBlockContainer}s
 * (waterloggable and underwater plants) are skipped - the fluid code asks them directly and never reads the
 * tag. {@link #washEach} then proves the mechanism in-world.
 */
public final class FluidWashSweep {
    /** Inner 6x6 of the 8x8 arena on two layers; the outer ring is a wall that keeps the fluids in. */
    private static final int MIN = 1;
    private static final int MAX = 6;
    private static final int[] LAYERS = {2, 5};
    public static final int CAPACITY = (MAX - MIN + 1) * (MAX - MIN + 1) * LAYERS.length;

    private FluidWashSweep() {}

    public static String id(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).toString();
    }

    /** Every block registered under {@code namespace} that {@code isPlant} accepts. */
    public static List<Block> plantsIn(String namespace, Predicate<Block> isPlant) {
        final List<Block> found = new ArrayList<>();
        for (Block block : BuiltInRegistries.BLOCK) {
            if (BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals(namespace) && isPlant.test(block)) {
                found.add(block);
            }
        }
        return found;
    }

    /** Whether vanilla's rule says {@code block} must be in {@code #minecraft:washed_away_by_fluids}. */
    public static boolean shouldBeWashedAway(GameTestHelper helper, Block block) {
        return block.defaultBlockState().getCollisionShape(helper.getLevel(), helper.absolutePos(BlockPos.ZERO)).isEmpty();
    }

    /** Whether the tag decides for {@code block} at all (see the class comment). */
    public static boolean isDecidedByTag(Block block) {
        return !(block instanceof LiquidBlockContainer) && !(block instanceof LiquidBlock);
    }

    /** One line per plant whose tag membership disagrees with {@link #shouldBeWashedAway}. */
    public static List<String> findTagMismatches(GameTestHelper helper, List<Block> plants) {
        final List<String> failures = new ArrayList<>();
        for (Block block : plants) {
            if (!isDecidedByTag(block)) continue;
            final boolean expected = shouldBeWashedAway(helper, block);
            final boolean tagged = block.defaultBlockState().is(BlockTags.WASHED_AWAY_BY_FLUIDS);
            if (expected && !tagged) {
                failures.add(id(block) + " has no collision shape but is not in #minecraft:washed_away_by_fluids");
            } else if (!expected && tagged) {
                failures.add(id(block) + " has a collision shape but is in #minecraft:washed_away_by_fluids");
            }
        }
        return failures;
    }

    /**
     * Plants each of {@code plants} on the first of {@code soils} it survives on, puts a source of {@code fluid}
     * directly above it and succeeds once the fluid has taken every plant's place. Plants that survive on none
     * of the soils are returned and not tested; at most {@link #CAPACITY} plants can be planted.
     * <p>
     * The source is placed without neighbour updates and only its own fluid tick is scheduled, so a plant
     * can only disappear by being washed away - never by popping off in a shape update first.
     */
    public static List<Block> washEach(GameTestHelper helper, Block fluid, List<Block> plants, List<Block> soils) {
        for (int layer : LAYERS) {
            for (int x = 0; x <= MAX + 1; x++) {
                for (int z = 0; z <= MAX + 1; z++) {
                    final boolean wall = x < MIN || x > MAX || z < MIN || z > MAX;
                    for (int y = layer - 1; y <= layer + 1; y++) {
                        final boolean solid = wall || y == layer - 1;
                        put(helper, new BlockPos(x, y, z), solid ? Blocks.STONE.defaultBlockState() : Blocks.AIR.defaultBlockState());
                    }
                }
            }
        }

        final List<Block> skipped = new ArrayList<>();
        final List<Block> planted = new ArrayList<>();
        final List<BlockPos> positions = new ArrayList<>();
        int slot = 0;
        for (Block block : plants) {
            if (slot >= CAPACITY) {
                throw helper.assertionException(Component.literal(
                        "more plants than the " + CAPACITY + " slots of one wash test - split the list"
                ));
            }
            final BlockPos rel = slot(slot);
            if (!plant(helper, rel, block, soils)) {
                put(helper, rel, Blocks.AIR.defaultBlockState());
                skipped.add(block);
                continue;
            }
            planted.add(block);
            positions.add(rel);
            slot++;
        }

        final Fluid type = fluid.defaultBlockState().getFluidState().getType();
        for (BlockPos rel : positions) {
            final BlockPos source = helper.absolutePos(rel.above());
            helper.getLevel().setBlock(source, fluid.defaultBlockState(), Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
            helper.getLevel().scheduleTick(source, type, type.getTickDelay(helper.getLevel()));
        }

        helper.succeedWhen(() -> {
            final List<String> failures = new ArrayList<>();
            for (int i = 0; i < planted.size(); i++) {
                final BlockState now = helper.getBlockState(positions.get(i));
                if (!now.getFluidState().getType().isSame(type)) {
                    failures.add(id(fluid) + " did not wash away " + id(planted.get(i)) + " (now " + now + ")");
                }
            }
            GameTestAssertions.failIfAny(helper, "Fluid wash regression", failures);
        });
        return skipped;
    }

    private static BlockPos slot(int i) {
        final int width = MAX - MIN + 1;
        final int perLayer = width * width;
        final int layer = LAYERS[i / perLayer];
        final int inLayer = i % perLayer;
        return new BlockPos(MIN + inLayer % width, layer, MIN + inLayer / width);
    }

    private static boolean plant(GameTestHelper helper, BlockPos rel, Block block, List<Block> soils) {
        for (Block soil : soils) {
            put(helper, rel.below(), soil.defaultBlockState());
            put(helper, rel, block.defaultBlockState());
            if (block.defaultBlockState().canSurvive(helper.getLevel(), helper.absolutePos(rel))) return true;
        }
        put(helper, rel.below(), Blocks.STONE.defaultBlockState());
        return false;
    }

    private static void put(GameTestHelper helper, BlockPos rel, BlockState state) {
        helper.getLevel().setBlock(
                helper.absolutePos(rel),
                state,
                Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_SUPPRESS_DROPS
        );
    }
}
