package de.ambertation.wover.testmod.gametest;

import de.ambertation.wover.feature.api.FeatureUtils;
import de.ambertation.wover.feature.api.features.PillarFeature;
import de.ambertation.wover.feature.api.placed.modifiers.All;
import de.ambertation.wover.feature.api.placed.modifiers.Debug;
import de.ambertation.wover.feature.api.placed.modifiers.Extend;
import de.ambertation.wover.feature.api.placed.modifiers.Is;
import de.ambertation.wover.feature.api.placed.modifiers.Merge;
import de.ambertation.wover.feature.api.placed.modifiers.Offset;
import de.ambertation.wover.feature.api.placed.modifiers.Stencil;
import de.ambertation.wover.feature.impl.random.RandomPatchFeature;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SimpleBlockFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Fabric GameTests for wover-feature-api.
 * <p>
 * 26.3 collapsed the whole configured-feature concept: {@code ConfiguredFeature<FC, F>} is gone,
 * {@link Feature} is a non-generic interface that carries its own configuration and its own
 * {@link MapCodec}, the datapack registry {@code minecraft:configured_feature} became
 * {@code minecraft:feature} ({@code Registries.CONFIGURED_FEATURE} -&gt; {@link Registries#FEATURE}), and the
 * built-in registry of feature <em>types</em> moved from {@code BuiltInRegistries.FEATURE} to
 * {@link BuiltInRegistries#FEATURE_TYPE} with {@code MapCodec} as its element type. {@code PlacementModifier}
 * changed too: {@code getPositions(...) -> Stream<BlockPos>} became
 * {@code modify(..., Consumer<BlockPos>)} and {@code PlacementModifierType} was deleted.
 * <p>
 * The tests below guard each of those, in order of how silently they can break.
 */
public class FeatureGameTest {
    private static final String NS = "wover-feature-testmod";

    // Features loaded from the committed datapack JSON. 26.3 moved these from
    // data/<ns>/worldgen/configured_feature/*.json to data/<ns>/worldgen/feature/*.json.
    private static final List<String> DATAPACK_FEATURES = List.of(
            NS + ":test",
            NS + ":test_datagen",
            NS + ":random_spread",
            NS + ":patch_basalt_stalactite"
    );

    // Placed features loaded from the committed datapack JSON (worldgen/placed_feature/*.json).
    private static final List<String> DATAPACK_PLACED_FEATURES = List.of(
            NS + ":inline_feature",
            NS + ":inline_feature_all",
            NS + ":test_datagen",
            NS + ":vanilla_feature"
    );

    // Custom feature types registered programmatically into the built-in FEATURE_TYPE registry at class-init
    // (FeatureManagerImpl), under the production "wover" namespace - not from any datapack JSON.
    private static final List<String> PROGRAMMATIC_FEATURE_TYPES = List.of(
            "wover:pillar",
            "wover:place_block",
            "wover:sequence",
            "wover:condition",
            "wover:template",
            "wover:mark_postprocessing",
            "wover:random_patch"
    );

    // A plausible-but-absent feature id: never registered by the testmod. Its presence would mean the
    // containsKey check is meaningless, so this makes the test able to fail on a regression.
    private static final String ABSENT_FEATURE = NS + ":does_not_exist_feature";

    /**
     * The worldgen features the testmod declares must all be present in the live registries once the server
     * is up.
     * <p>
     * The testmod contributes feature content two distinct ways, and this test guards both:
     * <ul>
     *   <li><b>Datapack-loaded worldgen objects</b> — committed JSON under
     *       {@code src/testmod/generated/data/.../worldgen/} loaded into the dynamic registries
     *       {@link Registries#FEATURE} and {@link Registries#PLACED_FEATURE}. Their presence proves datapack
     *       loading of the (renamed) feature registry still works, and — because the JSON now carries the
     *       type's fields inlined rather than nested under {@code config} — that every WorldWeaver codec was
     *       ported to the flattened shape.</li>
     *   <li><b>Programmatically-registered feature types</b> — wover-feature-api registers its custom
     *       {@link Feature} codecs into {@link BuiltInRegistries#FEATURE_TYPE} at class-init time (via
     *       {@code FeatureManagerImpl}). <b>Careful:</b> in 26.1 this lived in {@code Registries.FEATURE};
     *       in 26.3 that constant means the datapack registry instead, so looking it up there would check the
     *       wrong registry entirely.</li>
     * </ul>
     */
    @GameTest
    public void featuresAreLoadedAndTypesRegistered(GameTestHelper helper) {
        final Registry<Feature> features = helper
                .getLevel()
                .registryAccess()
                .lookupOrThrow(Registries.FEATURE);
        final Registry<PlacedFeature> placedFeatures = helper
                .getLevel()
                .registryAccess()
                .lookupOrThrow(Registries.PLACED_FEATURE);
        final Registry<MapCodec<? extends Feature>> featureTypes = BuiltInRegistries.FEATURE_TYPE;

        final List<String> failures = new ArrayList<>();

        for (String id : DATAPACK_FEATURES) {
            if (!features.containsKey(Identifier.parse(id))) {
                failures.add(id + ": expected feature loaded from datapack JSON but it is missing");
            }
        }

        for (String id : DATAPACK_PLACED_FEATURES) {
            if (!placedFeatures.containsKey(Identifier.parse(id))) {
                failures.add(id + ": expected placed_feature loaded from datapack JSON but it is missing");
            }
        }

        for (String id : PROGRAMMATIC_FEATURE_TYPES) {
            if (!featureTypes.containsKey(Identifier.parse(id))) {
                failures.add(id + ": expected feature type registered programmatically at class-init but it is missing");
            }
        }

        // Negative guard: an id the testmod never registers must not be present.
        if (features.containsKey(Identifier.parse(ABSENT_FEATURE))) {
            failures.add(ABSENT_FEATURE + ": unexpectedly present - the containsKey assertions are not meaningful");
        }

        if (!failures.isEmpty()) {
            final List<String> present = new ArrayList<>();
            features.keySet().forEach(k -> present.add(k.toString()));
            helper.fail(Component.literal(
                    "Feature registry regression:\n - " + String.join("\n - ", failures)
                            + "\n\nFeatures actually present: " + present
            ));
            return;
        }

        helper.succeed();
    }

    /**
     * Guards the two things the {@code ConfiguredFeature} collapse newly demands of every WorldWeaver
     * feature that wraps another one, neither of which any other check can catch:
     * <ul>
     *   <li><b>The flattened codec.</b> 26.3 dispatches feature types on a {@link MapCodec}, so the type's
     *       fields sit next to {@code "type"} instead of inside a {@code "config"} object. Reading the
     *       committed {@code random_spread}/{@code patch_basalt_stalactite} JSON back and finding the
     *       expected field <em>values</em> proves the vendored {@code wover:random_patch} codec and the
     *       {@code wover:pillar} codec were ported to that shape - a codec that still expected a nested
     *       {@code config} would decode to defaults or fail.</li>
     *   <li><b>{@link Feature#getSubFeatures()}.</b> This is new in 26.3 and has a
     *       <em>default implementation that returns nothing</em>. Vanilla walks it to collect, among other
     *       things, a biome's bone-meal features, so a wrapping feature that does not override it silently
     *       drops everything nested inside it. Nothing fails to compile, nothing fails to load, and datagen
     *       output is identical - only the runtime feature graph is wrong.</li>
     * </ul>
     */
    @GameTest
    public void wrappingFeaturesDecodeFlatAndReportSubFeatures(GameTestHelper helper) {
        final Registry<Feature> features = helper
                .getLevel()
                .registryAccess()
                .lookupOrThrow(Registries.FEATURE);

        final List<String> failures = new ArrayList<>();

        // random_spread: wover:random_patch(tries=96, xz_spread=7, y_spread=3) around an inline
        // minecraft:simple_block of amethyst.
        final Feature randomSpread = features.getValue(Identifier.parse(NS + ":random_spread"));
        if (!(randomSpread instanceof RandomPatchFeature patch)) {
            failures.add(NS + ":random_spread decoded to "
                    + (randomSpread == null ? "null" : randomSpread.getClass().getSimpleName())
                    + " instead of RandomPatchFeature");
        } else {
            if (patch.tries() != 96 || patch.xzSpread() != 7 || patch.ySpread() != 3) {
                failures.add(NS + ":random_spread decoded to tries=" + patch.tries()
                        + ", xz_spread=" + patch.xzSpread() + ", y_spread=" + patch.ySpread()
                        + " instead of 96/7/3 - the flattened wover:random_patch codec does not match the"
                        + " committed JSON");
            }

            final List<Holder<Feature>> subs = patch.getSubFeatures().toList();
            if (subs.size() != 1) {
                failures.add(NS + ":random_spread reported " + subs.size()
                        + " sub-features instead of 1 - Feature#getSubFeatures() is not overridden, so the"
                        + " feature nested in the patch is invisible to the 26.3 feature graph");
            } else if (!(subs.getFirst().value() instanceof SimpleBlockFeature)) {
                failures.add(NS + ":random_spread reported a "
                        + subs.getFirst().value().getClass().getSimpleName()
                        + " sub-feature instead of the inline SimpleBlockFeature");
            }
        }

        // patch_basalt_stalactite: wover:random_patch around an inline wover:pillar. Also proves the
        // wover:pillar codec decodes its (now top-level) fields.
        final Feature stalactite = features.getValue(Identifier.parse(NS + ":patch_basalt_stalactite"));
        if (!(stalactite instanceof RandomPatchFeature stalactitePatch)) {
            failures.add(NS + ":patch_basalt_stalactite decoded to "
                    + (stalactite == null ? "null" : stalactite.getClass().getSimpleName())
                    + " instead of RandomPatchFeature");
        } else {
            final List<Holder<Feature>> subs = stalactitePatch.getSubFeatures().toList();
            if (subs.size() != 1 || !(subs.getFirst().value() instanceof PillarFeature)) {
                failures.add(NS + ":patch_basalt_stalactite reported " + subs.size()
                        + " sub-features and did not expose the inline wover:pillar");
            } else {
                final PillarFeature pillar = (PillarFeature) subs.getFirst().value();
                if (pillar.direction != Direction.DOWN
                        || pillar.transformer != PillarFeature.KnownTransformers.SIZE_DECREASE) {
                    failures.add(NS + ":patch_basalt_stalactite's pillar decoded to direction="
                            + pillar.direction + ", transform=" + pillar.transformer
                            + " instead of down/size_decrease - the flattened wover:pillar codec does not"
                            + " match the committed JSON");
                }
            }
        }

        if (!failures.isEmpty()) {
            helper.fail(Component.literal(
                    "Feature graph / codec regression:\n - " + String.join("\n - ", failures)
            ));
            return;
        }

        helper.succeed();
    }

    /**
     * Places features into the live world, which is the only check that exercises
     * {@code Feature#place(WorldGenLevel, ChunkGenerator, RandomSource, BlockPos)} — 26.3 replaced
     * {@code place(FeaturePlaceContext<FC>)}, so the whole "the feature is its own configuration" rework has
     * to be right end to end for a block to actually appear.
     * <p>
     * Both {@link FeatureUtils#placeInWorld} modes are covered, because they take different paths through
     * {@code FeatureContentManagerImpl}: {@code unchanged=true} places the patch itself, while
     * {@code unchanged=false} unwraps one {@code wover:random_patch} level first (the growth path) and
     * therefore places exactly one block at the origin.
     */
    @GameTest
    public void featuresPlaceBlocksInTheWorld(GameTestHelper helper) {
        final ServerLevel level = helper.getLevel();
        final ChunkGenerator generator = level.getChunkSource().getGenerator();
        final RandomSource random = RandomSource.create(1234L);
        final List<String> failures = new ArrayList<>();

        // A deliberately tiny patch, so everything it writes stays inside the test structure.
        final Holder<PlacedFeature> inner = PlacementUtils.inlinePlaced(
                new SimpleBlockFeature(BlockStateProvider.simple(Blocks.AMETHYST_BLOCK)),
                BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE)
        );
        final RandomPatchFeature patch = new RandomPatchFeature(24, 1, 0, inner);

        // 1) the patch itself: scatters within +-1 in xz around the origin.
        final BlockPos patchRel = new BlockPos(2, 2, 2);
        if (!FeatureUtils.placeInWorld(patch, level, helper.absolutePos(patchRel), random, generator, true)) {
            failures.add("placeInWorld(unchanged=true) reported that nothing was placed");
        }
        int placed = 0;
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (helper.getBlockState(patchRel.offset(x, 0, z)).is(Blocks.AMETHYST_BLOCK)) placed++;
            }
        }
        if (placed == 0) {
            failures.add("placeInWorld(unchanged=true) wrote no amethyst block around " + patchRel
                    + " - Feature#place never reached the inner simple_block");
        }

        // 2) unchanged=false unwraps the patch, so exactly the origin is written.
        final BlockPos singleRel = new BlockPos(5, 2, 5);
        if (!FeatureUtils.placeInWorld(patch, level, helper.absolutePos(singleRel), random, generator, false)) {
            failures.add("placeInWorld(unchanged=false) reported that nothing was placed");
        }
        if (!helper.getBlockState(singleRel).is(Blocks.AMETHYST_BLOCK)) {
            failures.add("placeInWorld(unchanged=false) did not write amethyst at " + singleRel
                    + " - the wover:random_patch unwrap in FeatureContentManagerImpl is broken");
        }

        if (!failures.isEmpty()) {
            helper.fail(Component.literal(
                    "Feature placement regression:\n - " + String.join("\n - ", failures)
            ));
            return;
        }

        helper.succeed();
    }

    /**
     * Guards the port of WorldWeaver's own {@code PlacementModifier}s from
     * {@code Stream<BlockPos> getPositions(...)} to {@code void modify(..., Consumer<BlockPos>)}.
     * <p>
     * This conversion is entirely mechanical and entirely silent when wrong: dropping the origin position,
     * forgetting to forward the consumer to a nested modifier, or returning early instead of continuing a
     * loop all compile fine and only show up as features that generate slightly differently. The expected
     * position counts below are the ones the 26.1 stream implementations produced.
     */
    @GameTest
    public void placementModifiersEmitTheSamePositions(GameTestHelper helper) {
        final ServerLevel level = helper.getLevel();
        final PlacementContext ctx = new PlacementContext(
                level,
                level.getChunkSource().getGenerator(),
                Optional.empty()
        );
        final RandomSource random = RandomSource.create(4321L);
        final List<String> failures = new ArrayList<>();

        helper.setBlock(new BlockPos(1, 3, 3), Blocks.STONE);
        final BlockPos air = helper.absolutePos(new BlockPos(1, 3, 1));
        final BlockPos solid = helper.absolutePos(new BlockPos(1, 3, 3));

        // Extend must emit the origin plus <length> offset positions.
        final List<BlockPos> extended = collect(new Extend(Direction.UP, ConstantInt.of(3)), ctx, random, air);
        if (extended.size() != 4 || !extended.getFirst().equals(air) || !extended.getLast().equals(air.above(3))) {
            failures.add("Extend(UP, 3) emitted " + extended + " instead of the origin plus 3 positions above");
        }

        // Merge must forward the consumer to every sub-modifier, in order.
        final List<BlockPos> merged = collect(
                new Merge(List.of(
                        new Extend(Direction.NORTH, ConstantInt.of(1)),
                        new Extend(Direction.SOUTH, ConstantInt.of(1))
                )),
                ctx, random, air
        );
        if (merged.size() != 4) {
            failures.add("Merge of two Extend(1) modifiers emitted " + merged.size()
                    + " positions instead of 4 - the consumer is not forwarded to the sub-modifiers");
        }

        // Offset / Debug re-emit exactly one position.
        final List<BlockPos> offset = collect(Offset.inDirection(Direction.UP), ctx, random, air);
        if (offset.size() != 1 || !offset.getFirst().equals(air.above())) {
            failures.add("Offset(UP) emitted " + offset + " instead of [" + air.above() + "]");
        }
        final List<BlockPos> debug = collect(new Debug("gametest {}"), ctx, random, air);
        if (debug.size() != 1 || !debug.getFirst().equals(air)) {
            failures.add("Debug emitted " + debug + " instead of the unaltered origin");
        }

        // All spans the 16x16 xz footprint minus one, exactly as the old IntStream.range(0, 16*16 - 1) did.
        final List<BlockPos> all = collect(All.simple(), ctx, random, air);
        if (all.size() != 16 * 16 - 1) {
            failures.add("All emitted " + all.size() + " positions instead of " + (16 * 16 - 1));
        }

        // Stencil emits one position per "true" cell of its 16x16 stencil.
        final List<BlockPos> stencil = collect(Stencil.all(), ctx, random, air);
        if (stencil.isEmpty() || stencil.size() >= 16 * 16) {
            failures.add("Stencil emitted " + stencil.size() + " positions, expected 0 < n < 256");
        }

        // PlacementFilter#modify is inherited from vanilla, but shouldPlace had to become public in 26.3.
        final List<BlockPos> isAir = collect(Is.simple(BlockPredicate.ONLY_IN_AIR_PREDICATE), ctx, random, air);
        if (isAir.size() != 1 || !isAir.getFirst().equals(air)) {
            failures.add("Is(air) emitted " + isAir + " for an air position instead of [" + air + "]");
        }
        final List<BlockPos> isSolid = collect(Is.simple(BlockPredicate.ONLY_IN_AIR_PREDICATE), ctx, random, solid);
        if (!isSolid.isEmpty()) {
            failures.add("Is(air) emitted " + isSolid + " for a stone position instead of nothing");
        }

        if (!failures.isEmpty()) {
            helper.fail(Component.literal(
                    "PlacementModifier regression:\n - " + String.join("\n - ", failures)
            ));
            return;
        }

        helper.succeed();
    }

    private static List<BlockPos> collect(
            PlacementModifier modifier,
            PlacementContext ctx,
            RandomSource random,
            BlockPos pos
    ) {
        final List<BlockPos> out = new ArrayList<>();
        modifier.modify(ctx, random, pos, out::add);
        return out;
    }
}
