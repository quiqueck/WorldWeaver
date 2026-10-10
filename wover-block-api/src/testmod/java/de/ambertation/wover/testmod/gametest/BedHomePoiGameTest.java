package de.ambertation.wover.testmod.gametest;

import de.ambertation.wover.tag.api.predefined.CommonPoiTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Regression guard for GitHub issues #26 / #33 ("villagers sleep on the wrong half of the bed").
 * <p>
 * {@code PoiManagerImpl.updateStates()} adds every state of every block in {@code wover:poi/home}
 * (which contains {@code #wover:beds}) to {@code PoiTypes.TYPE_BY_STATE}. Vanilla only registers the
 * <em>head</em> half of a bed as a {@code minecraft:home} POI; if the foot half gets registered too, a
 * villager can claim the foot as its home, sleeps there (head on the wrong block), and the head half
 * stays a second, separately claimable POI for the same bed. Fixed in {@code bfe4c4ed}.
 */
public class BedHomePoiGameTest {
    @GameTest
    public void bedFootIsNeverAHomePoi(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        // Static mapping: no FOOT state of any bed-like block may resolve to a POI type, while the HEAD
        // states of tagged beds must still be HOME (keeps the test from passing vacuously).
        int checkedHeads = 0;
        for (Block block : BuiltInRegistries.BLOCK) {
            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                if (!state.hasProperty(BedBlock.PART)) continue;
                if (state.getValue(BedBlock.PART) == BedPart.FOOT) {
                    PoiTypes.forState(state).ifPresent(type -> failures.add(
                            "foot state " + state + " is registered as POI " + type.getRegisteredName()
                    ));
                }
            }
        }
        for (var holder : helper.getLevel()
                                .registryAccess()
                                .lookupOrThrow(Registries.BLOCK)
                                .getTagOrEmpty(CommonPoiTags.HOME)) {
            for (BlockState state : holder.value().getStateDefinition().getPossibleStates()) {
                if (!state.hasProperty(BedBlock.PART) || state.getValue(BedBlock.PART) != BedPart.HEAD) continue;
                checkedHeads++;
                if (!PoiTypes.forState(state).map(t -> t.is(PoiTypes.HOME)).orElse(false)) {
                    failures.add("head state " + state + " is not registered as minecraft:home");
                }
            }
        }
        if (checkedHeads == 0) {
            failures.add("wover:poi/home contained no bed head states - test would be vacuous");
        }

        // Live check: a placed bed must produce exactly one HOME POI, at the head.
        final BlockPos foot = new BlockPos(1, 1, 1);
        final BlockPos head = foot.relative(Direction.NORTH);
        final BlockState bed = Blocks.BED.red().defaultBlockState().setValue(BedBlock.FACING, Direction.NORTH);
        helper.setBlock(foot, bed.setValue(BedBlock.PART, BedPart.FOOT));
        helper.setBlock(head, bed.setValue(BedBlock.PART, BedPart.HEAD));

        final PoiManager poi = helper.getLevel().getPoiManager();
        if (!poi.existsAtPosition(PoiTypes.HOME, helper.absolutePos(head))) {
            failures.add("placed bed: no minecraft:home POI at the head half");
        }
        poi.getType(helper.absolutePos(foot)).ifPresent(type -> failures.add(
                "placed bed: foot half is a POI of type " + type.getRegisteredName()
        ));

        if (!failures.isEmpty()) {
            helper.fail(Component.literal("Bed HOME POI regression:\n - " + String.join("\n - ", failures)));
            return;
        }
        helper.succeed();
    }

    /**
     * Worlds saved by the pre-{@code aef00b77} versions still contain HOME records at bed feet, in
     * sections flagged {@code Valid} - which vanilla never re-checks (quiqueck/BCLib#231). Simulates
     * those records and drives the chunk-load consistency check that is supposed to drop them.
     */
    @GameTest
    public void staleBedFootHomeRecordsAreRemoved(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        final ServerLevel level = helper.getLevel();
        final PoiManager poi = level.getPoiManager();
        final Holder<PoiType> home = BuiltInRegistries.POINT_OF_INTEREST_TYPE.getOrThrow(PoiTypes.HOME);
        final Holder<PoiType> meeting = BuiltInRegistries.POINT_OF_INTEREST_TYPE.getOrThrow(PoiTypes.MEETING);

        final BlockPos foot = helper.absolutePos(new BlockPos(1, 1, 1));
        final BlockPos head = foot.relative(Direction.NORTH);
        final BlockPos air = helper.absolutePos(new BlockPos(3, 1, 1));
        final BlockPos other = helper.absolutePos(new BlockPos(3, 1, 3));
        final BlockState bed = Blocks.BED.red().defaultBlockState().setValue(BedBlock.FACING, Direction.NORTH);
        helper.setBlock(new BlockPos(1, 1, 1), bed.setValue(BedBlock.PART, BedPart.FOOT));
        helper.setBlock(new BlockPos(1, 1, 0), bed.setValue(BedBlock.PART, BedPart.HEAD));
        helper.setBlock(new BlockPos(3, 1, 1), Blocks.AIR.defaultBlockState());
        helper.setBlock(new BlockPos(3, 1, 3), Blocks.STONE.defaultBlockState());

        // What an affected world looks like: HOME at the foot of an existing bed, HOME left behind where
        // a bed was broken, plus a non-HOME record that doesn't match its block (must stay untouched).
        poi.add(foot, home);
        poi.add(air, home);
        poi.add(other, meeting);

        final Set<SectionPos> sections = new LinkedHashSet<>();
        for (BlockPos pos : List.of(foot, head, air, other)) sections.add(SectionPos.of(pos));
        for (SectionPos section : sections) {
            poi.checkConsistencyWithBlocks(
                    section,
                    level.getChunk(section.x(), section.z()).getSection(level.getSectionIndexFromSectionY(section.y()))
            );
        }

        poi.getType(foot).ifPresent(type -> failures.add(
                "stale foot record of type " + type.getRegisteredName() + " survived the consistency check"
        ));
        poi.getType(air).ifPresent(type -> failures.add(
                "stale record on a broken bed of type " + type.getRegisteredName() + " survived the consistency check"
        ));
        if (!poi.existsAtPosition(PoiTypes.HOME, head)) {
            failures.add("valid HOME record at the head half was removed");
        }
        if (!poi.existsAtPosition(PoiTypes.MEETING, other)) {
            failures.add("non-HOME record was removed - cleanup must stay scoped to HOME");
        } else {
            poi.remove(other);
        }

        if (!failures.isEmpty()) {
            helper.fail(Component.literal("Stale bed HOME POI cleanup:\n - " + String.join("\n - ", failures)));
            return;
        }
        helper.succeed();
    }
}
