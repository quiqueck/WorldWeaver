package de.ambertation.wover.feature.impl;

import de.ambertation.wover.entrypoint.LibWoverFeature;
import de.ambertation.wover.feature.api.features.*;
import de.ambertation.wover.feature.impl.random.RandomPatchFeature;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Registers WorldWeaver's own {@link Feature} types.
 * <p>
 * 26.3 renamed {@code BuiltInRegistries.FEATURE} to {@code BuiltInRegistries.FEATURE_TYPE} and changed its
 * element type from {@code Feature<?>} to {@code MapCodec<? extends Feature>}: a {@link Feature} instance now
 * carries its own configuration, so what gets registered is the type's codec, not a stateless singleton.
 * ({@code Registries.FEATURE} is a different thing in 26.3 — the datapack registry of {@link Feature} values
 * that used to be called {@code CONFIGURED_FEATURE}.)
 */
public class FeatureManagerImpl {

    public static <F extends Feature> MapCodec<F> register(
            @NotNull Identifier id,
            @NotNull MapCodec<F> codec
    ) {
        return register(createKey(id), codec);
    }

    public static <F extends Feature> MapCodec<F> register(
            @NotNull ResourceKey<MapCodec<? extends Feature>> key,
            @NotNull MapCodec<F> codec
    ) {
        Registry.register(BuiltInRegistries.FEATURE_TYPE, key, codec);
        return codec;
    }

    @NotNull
    public static ResourceKey<MapCodec<? extends Feature>> createKey(Identifier location) {
        return ResourceKey.create(
                BuiltInRegistries.FEATURE_TYPE.key(),
                location
        );
    }

    public static final MapCodec<PlaceFacingBlockFeature> PLACE_BLOCK = register(
            LibWoverFeature.C.id("place_block"),
            PlaceFacingBlockFeature.CODEC
    );

    public static final MapCodec<MarkPostProcessingFeature> MARK_POSTPROCESSING = register(
            LibWoverFeature.C.id("mark_postprocessing"),
            MarkPostProcessingFeature.CODEC
    );

    public static final MapCodec<SequenceFeature> SEQUENCE = register(
            LibWoverFeature.C.id("sequence"),
            SequenceFeature.CODEC
    );

    public static final MapCodec<ConditionFeature> CONDITION = register(
            LibWoverFeature.C.id("condition"),
            ConditionFeature.CODEC
    );

    public static final MapCodec<PillarFeature> PILLAR = register(
            LibWoverFeature.C.id("pillar"),
            PillarFeature.CODEC
    );

    public static final MapCodec<TemplateFeature> TEMPLATE = register(
            LibWoverFeature.C.id("template"),
            TemplateFeature.CODEC
    );

    // Minecraft 26.1 removed the vanilla "minecraft:random_patch" feature type. WorldWeaver still relies on
    // it for its public RandomPatch/WeightedBlockPatch API, so the mod re-registers a compatible
    // implementation under its own namespace ("wover:random_patch").
    public static final MapCodec<RandomPatchFeature> RANDOM_PATCH = register(
            LibWoverFeature.C.id("random_patch"),
            RandomPatchFeature.CODEC
    );

    @ApiStatus.Internal
    public static void ensureStaticInitialization() {
        // no-op
    }
}
