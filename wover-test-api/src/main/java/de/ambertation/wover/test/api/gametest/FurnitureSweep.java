package de.ambertation.wover.test.api.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Generic sitting-furniture interaction, shared by BetterEnd and BetterNether so each mod's
 * {@code FurnitureParityGameTest} only needs to supply the block to test, not re-derive the interaction
 * itself. Deliberately independent of any concrete chair/barstool/taburet implementation (both mods'
 * sittable blocks come from BCLib, which this module does not and should not depend on) - the check
 * below works purely at the vanilla interaction/riding level that any "sit on right-click" block, from
 * either mod, is expected to implement: right-click the block and confirm the player ends up riding
 * something.
 * <p>
 * Enumerating <em>which</em> blocks a mod registers as furniture (per wood/material) is necessarily
 * mod-specific - it comes from that mod's own {@code WoodenComplexMaterial}/{@code SlotType} usage -
 * and stays the caller's job.
 */
public final class FurnitureSweep {
    private FurnitureSweep() {}

    /**
     * Places a survival {@link MockPlayers#survival mock player} next to the block already standing at
     * {@code relativePos} and right-clicks it. Returns {@code null} on success, or a failure message
     * naming {@code label} otherwise. Restores the player to not-riding before returning either way.
     */
    public static String assertPlayerCanSit(GameTestHelper helper, BlockPos relativePos, String label) {
        final BlockPos abs = helper.absolutePos(relativePos);
        final ServerPlayer player = MockPlayers.survival(helper, relativePos.above());

        final BlockState state = helper.getLevel().getBlockState(abs);
        final BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(abs), Direction.UP, abs, false);
        final InteractionResult result = state.useWithoutItem(helper.getLevel(), player, hit);

        final boolean seated = player.isPassenger();
        if (player.isPassenger()) {
            player.stopRiding();
        }
        player.discard();

        if (!seated) {
            return label + ": right-clicking " + state.getBlock() + " did not seat the player (result: "
                    + result + ")";
        }
        return null;
    }
}
