package de.ambertation.wover.testmod.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Fabric GameTest for {@code StripableBlockTrait}: "using an axe on a block registered with the trait turns it
 * into its stripped variant, keeping the pillar axis".
 * <p>
 * This covers the behaviour that used to live in {@code AxeItemMixin} and was rebuilt for 26.3 on top of the
 * {@code minecraft:block_transformer} item component (see
 * {@code de.ambertation.wover.block.impl.trait.behaviour.StripableBlockTransformers}). The redesign moved from
 * "inject into {@code AxeItem#getStripped}" to "append to the transformer list every axe already carries", and
 * that hinges on a chain of things a compile cannot check: the trait has to be collected, the Fabric
 * {@code DefaultItemComponentEvents.MODIFY} listener has to fire late enough to see registered blocks, the
 * items it selects have to actually be the axes, and the resulting entry has to survive as a real serializable
 * component. Any of those failing silently produces a log that simply does not strip - which is exactly what
 * this test detects.
 * <p>
 * The interaction is driven through {@link GameTestHelper#useBlock(BlockPos, net.minecraft.world.entity.player.Player)},
 * i.e. the real {@code BlockState#useItemOn} -> {@code ItemStack#useOn} -> {@code BlockTransformer#transformBlock}
 * path, not by calling the trait directly.
 */
public class StripableGameTest {
    // The testmod's TestWoodSet declares `new Log(true, ...)`, i.e. a strippable log, so the set expands into
    // this pair and the log carries a StripableBlockTrait.
    private static final String LOG = "wover-sets-testmod:wooden_log";
    private static final String STRIPPED_LOG = "wover-sets-testmod:wooden_stripped_log";

    // A set member that is deliberately NOT strippable. Guards against a transformer entry that matches
    // everything (e.g. a predicate built from the wrong block).
    private static final String PLANKS = "wover-sets-testmod:wooden_planks";

    @GameTest
    public void axeStripsBlocksCarryingTheStripableTrait(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        final Block log = BuiltInRegistries.BLOCK.getValue(Identifier.parse(LOG));
        final Block strippedLog = BuiltInRegistries.BLOCK.getValue(Identifier.parse(STRIPPED_LOG));
        final Block planks = BuiltInRegistries.BLOCK.getValue(Identifier.parse(PLANKS));

        // The transform is appended to the item's *default* components, so it has to be visible on a plain
        // stack. Checking this separately tells a failed strip apart from a missing component.
        final BlockTransformer transformer = Items.DIAMOND_AXE.components().get(DataComponents.BLOCK_TRANSFORMER);
        if (transformer == null) {
            failures.add("minecraft:diamond_axe carries no minecraft:block_transformer component at all");
        } else if (transformer.transforms().size()
                <= net.minecraft.world.item.component.BlockTransformerMappings.AXE.transforms().size()) {
            failures.add(
                    "minecraft:diamond_axe's block_transformer has "
                            + transformer.transforms().size()
                            + " entries, i.e. no more than vanilla's "
                            + net.minecraft.world.item.component.BlockTransformerMappings.AXE.transforms().size()
                            + " - StripableBlockTransformers never appended anything"
            );
        }

        // 1) the set's own strippable log, with a non-default axis so the property copy is exercised
        final BlockPos logPos = new BlockPos(1, 1, 1);
        helper.setBlock(logPos, log.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.X));
        strike(helper, logPos);

        final BlockState afterLog = helper.getBlockState(logPos);
        if (afterLog.getBlock() != strippedLog) {
            failures.add(LOG + ": axe left " + idOf(afterLog.getBlock()) + " instead of " + STRIPPED_LOG);
        } else if (afterLog.getValue(RotatedPillarBlock.AXIS) != Direction.Axis.X) {
            failures.add(
                    STRIPPED_LOG + ": axis was not carried over, got "
                            + afterLog.getValue(RotatedPillarBlock.AXIS) + " instead of X"
            );
        }

        // 2) vanilla's own strippables must keep working - we append to its list, we do not replace it
        final BlockPos vanillaPos = new BlockPos(3, 1, 1);
        helper.setBlock(vanillaPos, Blocks.OAK_LOG.defaultBlockState().setValue(
                RotatedPillarBlock.AXIS,
                Direction.Axis.Z
        ));
        strike(helper, vanillaPos);

        final BlockState afterVanilla = helper.getBlockState(vanillaPos);
        if (afterVanilla.getBlock() != Blocks.STRIPPED_OAK_LOG) {
            failures.add("minecraft:oak_log: axe left " + idOf(afterVanilla.getBlock())
                    + " instead of minecraft:stripped_oak_log");
        }

        // 3) a non-strippable set member must be left alone (keeps this test from passing vacuously if the
        //    predicate ever matched everything)
        final BlockPos planksPos = new BlockPos(5, 1, 1);
        helper.setBlock(planksPos, planks.defaultBlockState());
        strike(helper, planksPos);

        final Block afterPlanks = helper.getBlockState(planksPos).getBlock();
        if (afterPlanks != planks) {
            failures.add(PLANKS + ": is not strippable, but the axe turned it into " + idOf(afterPlanks));
        }

        if (!failures.isEmpty()) {
            helper.fail(Component.literal(
                    "StripableBlockTrait regression:\n - " + String.join("\n - ", failures)
            ));
            return;
        }

        helper.succeed();
    }

    private static void strike(GameTestHelper helper, BlockPos pos) {
        final var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND_AXE));
        helper.useBlock(pos, player);
    }

    private static String idOf(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).toString();
    }
}
