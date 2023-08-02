package org.betterx.wover.feature.impl;

import org.betterx.wover.entrypoint.WoverFeature;
import org.betterx.wover.feature.api.features.*;
import org.betterx.wover.feature.api.features.config.*;
import org.betterx.wover.legacy.api.LegacyHelper;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.function.Function;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class FeatureManagerImpl {


    public static <C extends FeatureConfiguration, F extends Feature<C>> F register(
            @NotNull ResourceLocation id,
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
            @NotNull ResourceLocation id,
            @NotNull Function<Codec<C>, F> feature,
            Codec<C> codec
    ) {
        final var key = createKey(id);
        F res = register(key, feature.apply(codec));
        register(LegacyHelper.BCLIB_CORE.convertNamespace(key.location()), feature.apply(codec));
        return res;
    }

    @NotNull
    public static ResourceKey<Feature<?>> createKey(ResourceLocation location) {
        return ResourceKey.create(
                BuiltInRegistries.FEATURE.key(),
                location
        );
    }

    public static final Feature<PlaceFacingBlockConfig> PLACE_BLOCK = registerWithLegacy(
            WoverFeature.C.id("place_block"),
            PlaceBlockFeature::new,
            PlaceFacingBlockConfig.CODEC
    );


    public static final Feature<NoneFeatureConfiguration> MARK_POSTPROCESSING = registerWithLegacy(
            WoverFeature.C.id("mark_postprocessing"),
            (codec) -> new MarkPostProcessingFeature(),
            null
    );

    public static final Feature<SequenceFeatureConfig> SEQUENCE = registerWithLegacy(
            WoverFeature.C.id("sequence"),
            (codec) -> new SequenceFeature(),
            null
    );

    public static final Feature<ConditionFeatureConfig> CONDITION = registerWithLegacy(
            WoverFeature.C.id("condition"),
            codec -> new ConditionFeature(),
            null
    );

    public static final Feature<PillarFeatureConfig> PILLAR = registerWithLegacy(
            WoverFeature.C.id("pillar"),
            codec -> new PillarFeature(),
            null
    );

    public static final Feature<TemplateFeatureConfig> TEMPLATE = registerWithLegacy(
            WoverFeature.C.id("template"),
            TemplateFeature::new,
            TemplateFeatureConfig.CODEC
    );

    @ApiStatus.Internal
    public static void ensureStaticInitialization() {
        // no-op
    }


}
