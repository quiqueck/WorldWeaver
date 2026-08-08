package de.ambertation.wover.test.api.gametest;

import com.mojang.authlib.GameProfile;
import io.netty.channel.embedded.EmbeddedChannel;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

/**
 * Mock {@link ServerPlayer}s for GameTests, shared by BetterEnd's and BetterNether's testmods so the
 * traps below only need to be worked around once.
 *
 * <h2>Why not {@code GameTestHelper}'s own mock players</h2>
 * <ul>
 *   <li>{@code makeMockServerPlayerInLevel()} builds an anonymous subclass whose {@code gameMode()} is
 *       hard-coded to {@code CREATIVE}, so {@code isCreative()} is permanently true no matter what
 *       {@code setGameMode}/{@code updatePlayerAbilities} are told. Creative changes block-breaking
 *       and damage behaviour outright, and short-circuits mixins that special-case creative players -
 *       useless for anything that needs to genuinely exercise survival mechanics. It IS registered with
 *       the {@code PlayerList} though, so it is what {@link #inLevel} is built on.</li>
 *   <li>{@code makeMockServerPlayer(GameType)} honours the requested mode but never registers the
 *       player with the {@code PlayerList}: {@code connection} stays null, and {@code addEffect}/
 *       {@code lookAt} NPE trying to send packets down it.</li>
 * </ul>
 * {@link #survival} closes both gaps: a survival-mode subclass, registered with the {@code PlayerList}
 * over a real (embedded-channel) connection, with the client-effect-sync hooks suppressed since they
 * exist only to notify a client this test double does not have.
 *
 * <h2>Equipment does not apply itself</h2>
 * A mock player never runs {@code LivingEntity#detectEquipmentUpdates}, so putting an item in a slot
 * grants none of its attribute modifiers and fires none of its enchantment
 * {@code EnchantmentAttributeEffect}s, no matter how long the test idles. Tests that need those must
 * apply them at the vanilla call site explicitly (e.g. {@code stack.forEachModifier(slot, ...)} ->
 * {@code getAttributes().getInstance(a).addTransientModifier(m)}), or exercise the mechanic on a real
 * mob instead (spawned via {@code helper.spawnWithNoFreeWill(...)}).
 */
public final class MockPlayers {
    private MockPlayers() {}

    /**
     * A survival-mode player standing at {@code relativePos} within the test structure.
     * <p>
     * Registered with the {@code PlayerList} over an embedded connection, the way
     * {@code makeMockServerPlayerInLevel} registers its own player, but with a subclass whose
     * {@code gameMode()} is survival and which is not permanently invulnerable.
     * <p>
     * Without a connection, anything that reaches {@code ServerPlayer#isInvulnerableTo} NPEs on
     * {@code connection.hasClientLoaded()} - which is on the path of every hit the player takes, so a
     * connection-less player cannot be used as an attack target at all. And without the
     * {@code isInvulnerableTo} override below, a mock player's client never finishes "loading", and
     * vanilla treats a not-yet-loaded client as invulnerable - {@code hurtServer} would silently return
     * false and no post-attack enchantment effect would ever run.
     */
    public static ServerPlayer survival(GameTestHelper helper, BlockPos relativePos) {
        final GameProfile profile = new GameProfile(UUID.randomUUID(), "test-survival-player");
        final SurvivalTestPlayer player = new SurvivalTestPlayer(helper, profile);

        // Adding the Connection to an EmbeddedChannel's pipeline fires channelActive, which is what
        // binds Connection#channel. Without it every packet send NPEs on a null channel.
        final Connection connection = new Connection(PacketFlow.SERVERBOUND);
        new EmbeddedChannel(connection);

        helper.getLevel()
              .getServer()
              .getPlayerList()
              .placeNewPlayer(
                      connection,
                      player,
                      CommonListenerCookie.createInitial(profile, false)
              );
        GameType.SURVIVAL.updatePlayerAbilities(player.getAbilities());
        player.onUpdateAbilities();

        final BlockPos abs = helper.absolutePos(relativePos);
        // teleportTo, not snapTo: only teleportTo moves the player's chunk-loading ticket, and a
        // player sitting in an unloaded chunk is never ticked.
        player.teleportTo(
                helper.getLevel(),
                abs.getX() + 0.5, abs.getY(), abs.getZ() + 0.5,
                Set.of(),
                0.0f, 0.0f,
                false
        );
        return player;
    }

    /**
     * A player that is actually registered with the {@code PlayerList} and standing in the structure,
     * built on {@code GameTestHelper#makeMockServerPlayerInLevel}.
     * <p>
     * Required by anything that looks players up through the level rather than by direct reference -
     * e.g. an advancement trigger that scans {@code level.getEntitiesOfClass(ServerPlayer.class, ...)}
     * around a converted block will never see a hand-constructed player no matter where it stands.
     * <p>
     * This variant is stuck in creative mode (see the class notes), which is irrelevant for
     * level-lookup-driven triggers but rules it out for anything that needs real survival mechanics -
     * use {@link #survival} for those.
     */
    @SuppressWarnings("removal")
    public static ServerPlayer inLevel(GameTestHelper helper, BlockPos relativePos) {
        final ServerPlayer player = helper.makeMockServerPlayerInLevel();
        final BlockPos abs = helper.absolutePos(relativePos);
        player.teleportTo(
                helper.getLevel(),
                abs.getX() + 0.5, abs.getY(), abs.getZ() + 0.5,
                Set.of(),
                0.0f, 0.0f,
                false
        );
        return player;
    }

    /**
     * Points {@code player}'s eyes at {@code target}.
     * <p>
     * {@code ServerPlayer#lookAt} cannot be used on a connection-less player: it pushes a teleport
     * packet down the connection. Works for both {@link #survival} (which does have a connection) and
     * any other mock player, since the rotation here is computed directly and applied with
     * {@code snapTo}, which is purely local state.
     */
    public static void lookAt(ServerPlayer player, Vec3 target) {
        final Vec3 eye = player.getEyePosition();
        final double dx = target.x - eye.x;
        final double dy = target.y - eye.y;
        final double dz = target.z - eye.z;
        final double horizontal = Math.sqrt(dx * dx + dz * dz);

        final float yRot = Mth.wrapDegrees((float) (Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0f);
        final float xRot = Mth.wrapDegrees((float) (-(Mth.atan2(dy, horizontal) * (180.0 / Math.PI))));

        player.snapTo(player.getX(), player.getY(), player.getZ(), yRot, xRot);
        player.setYHeadRot(yRot);
    }

    private static final class SurvivalTestPlayer extends ServerPlayer {
        private SurvivalTestPlayer(GameTestHelper helper, GameProfile profile) {
            super(
                    helper.getLevel().getServer(),
                    helper.getLevel(),
                    profile,
                    ClientInformation.createDefault()
            );
        }

        @Override
        public GameType gameMode() {
            return GameType.SURVIVAL;
        }

        @Override
        public boolean isInvulnerableTo(ServerLevel level, DamageSource source) {
            return false;
        }

        // The three hooks below only exist to push effect updates down a client connection. This
        // player has a real (embedded) connection, but no client on the other end ever reads it, so
        // suppressing them avoids doing pointless packet work; the effect itself is still stored on
        // the entity either way.
        @Override
        protected void onEffectAdded(MobEffectInstance instance, Entity source) {
        }

        @Override
        protected void onEffectUpdated(MobEffectInstance instance, boolean forced, Entity source) {
        }

        @Override
        protected void onEffectsRemoved(Collection<MobEffectInstance> instances) {
        }
    }
}
