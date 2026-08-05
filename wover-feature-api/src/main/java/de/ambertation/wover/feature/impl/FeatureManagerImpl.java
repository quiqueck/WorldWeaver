package de.ambertation.wover.feature.impl;

import de.ambertation.wover.entrypoint.LibWoverFeature;
import de.ambertation.wover.feature.api.features.*;
import de.ambertation.wover.feature.api.features.config.*;
import de.ambertation.wover.feature.impl.random.RandomPatchConfiguration;
import de.ambertation.wover.feature.impl.random.RandomPatchFeature;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.function.Function;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class FeatureManagerImpl {


    public static <C extends FeatureConfiguration, F extends Feature<C>> F register(
            @NotNull Identifier id,
            @NotNull F feature
    ) {
        return register(createKey(id), feature);
    }

    public static <C extends FeatureConfiguration, F extends Feature<C>> F register(
            @NotNull ResourceKey<Feature<?>> key,
            @NotNull F feature
    ) {
        Registry.register(BuiltInRegistries.FEATURE, key, feature);
        return feature;
    }

    private static <C extends FeatureConfiguration, F extends Feature<C>> F registerWithLegacy(
            @NotNull Identifier id,
            @NotNull Function<Codec<C>, F> feature,
            Codec<C> codec
    ) {
        final var key = createKey(id);
        F res = register(key, feature.apply(codec));
        return res;
    }

    @NotNull
    public static ResourceKey<Feature<?>> createKey(Identifier location) {
        return ResourceKey.create(
                BuiltInRegistries.FEATURE.key(),
                location
        );
    }

    public static final Feature<PlaceFacingBlockConfig> PLACE_BLOCK = registerWithLegacy(
            LibWoverFeature.C.id("place_block"),
            PlaceBlockFeature::new,
            PlaceFacingBlockConfig.CODEC
    );


    public static final Feature<NoneFeatureConfiguration> MARK_POSTPROCESSING = registerWithLegacy(
            LibWoverFeature.C.id("mark_postprocessing"),
            (codec) -> new MarkPostProcessingFeature(),
            null
    );

    public static final Feature<SequenceFeatureConfig> SEQUENCE = registerWithLegacy(
            LibWoverFeature.C.id("sequence"),
            (codec) -> new SequenceFeature(),
            null
    );

    public static final Feature<ConditionFeatureConfig> CONDITION = registerWithLegacy(
            LibWoverFeature.C.id("condition"),
            codec -> new ConditionFeature(),
            null
    );

    public static final Feature<PillarFeatureConfig> PILLAR = registerWithLegacy(
            LibWoverFeature.C.id("pillar"),
            codec -> new PillarFeature(),
            null
    );

    public static final Feature<TemplateFeatureConfig> TEMPLATE = registerWithLegacy(
            LibWoverFeature.C.id("template"),
            TemplateFeature::new,
            TemplateFeatureConfig.CODEC
    );

    // Minecraft 26.1 removed the vanilla "minecraft:random_patch" feature type. WorldWeaver still relies on
    // it for its public RandomPatch/WeightedBlockPatch API, so the mod re-registers a compatible
    // implementation under its own namespace ("wover:random_patch").
    public static final RandomPatchFeature RANDOM_PATCH = registerWithLegacy(
            LibWoverFeature.C.id("random_patch"),
            RandomPatchFeature::new,
            RandomPatchConfiguration.CODEC
    );

    @ApiStatus.Internal
    public static void ensureStaticInitialization() {
        // no-op
    }


}
