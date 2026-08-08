package de.ambertation.wover.testmod.feature.datagen;

import de.ambertation.wover.block.api.predicate.BlockPredicates;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverRegistryContentProvider;
import de.ambertation.wover.feature.api.configured.FeatureContentManager;
import de.ambertation.wover.feature.api.configured.FeatureKey;
import de.ambertation.wover.feature.api.configured.configurators.AsPillar;
import de.ambertation.wover.feature.api.features.PillarFeature;
import de.ambertation.wover.testmod.entrypoint.TestModWoverFeature;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.util.valueproviders.BiasedToBottomInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * Bootstraps the {@code minecraft:feature} datapack registry (26.1: {@code minecraft:configured_feature}).
 * <p>
 * Renamed from {@code ConfiguredFeaturesProvider}: 26.3 deleted {@code ConfiguredFeature} and renamed the
 * registry, so the generated output moved from {@code worldgen/configured_feature/} to
 * {@code worldgen/feature/}.
 */
public class FeatureContentProvider extends WoverRegistryContentProvider<Feature> {
    public FeatureContentProvider(
            ModCore modCore
    ) {
        super(modCore, "Features", Registries.FEATURE);
    }

    @Override
    protected void bootstrap(BootstrapContext<Feature> context) {
        TestModWoverFeature.CONFIGURED_REDSTONE_BLOCK
                .bootstrap(context)
                .block(Blocks.REDSTONE_BLOCK)
                .register();

        // Intentionally kept on the deprecated wover:random_patch shim: this provider bootstraps the
        // FEATURE registry, whereas the replacement scatter(...) API is placement-based (produces a
        // PLACED feature). Keeping this here (and patch_basalt_stalactite below) preserves compile+runtime
        // coverage of the vendored random_patch shim until it is removed. Placement-based usages were migrated
        // to FeaturePlacementBuilder.scatter(...) in PlacedFeatureProvider (inline_feature_all).
        TestModWoverFeature.TEST_RANDOM_SIMPLE
                .bootstrap(context)
                .block(Blocks.AMETHYST_BLOCK)
                .inlinePlace()
                .isEmpty()
                .inRandomPatch()
                .register();

        FeatureKey<AsPillar> TEST_KEY = FeatureContentManager.pillar(
                modCore.id("test")
        );
        TEST_KEY.bootstrap(context)
                .allowedPlacement(BlockPredicate.ONLY_IN_AIR_OR_WATER_PREDICATE)
                .maxHeight(UniformInt.of(4, 8))
                .minHeight(3)
                .direction(Direction.UP)
                .transformer(PillarFeature.KnownTransformers.SIZE_INCREASE)
                .blockState(Blocks.LAPIS_BLOCK)
                .register();

        FeatureContentManager
                .pillar(modCore.id("patch_basalt_stalactite"))
                .bootstrap(context)
                .transformer(PillarFeature.KnownTransformers.SIZE_DECREASE)
                .direction(Direction.DOWN)
                .blockState(Blocks.SMOOTH_BASALT)
                .maxHeight(BiasedToBottomInt.of(4, 11))
                .inlinePlace()
                .isEmptyAndUnder(BlockPredicates.ONLY_NETHER_GROUND_AND_BASALT)
                .inRandomPatch()
                .register();
    }
}
