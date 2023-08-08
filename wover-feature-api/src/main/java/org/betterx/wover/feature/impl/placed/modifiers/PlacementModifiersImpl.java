package org.betterx.wover.feature.impl.placed.modifiers;

import org.betterx.wover.entrypoint.WoverFeature;
import org.betterx.wover.feature.api.placed.modifiers.*;
import org.betterx.wover.legacy.api.LegacyHelper;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import org.jetbrains.annotations.ApiStatus;

public class PlacementModifiersImpl {
    public static final PlacementModifierType<Stencil> STENCIL = registerLegacy(
            "stencil",
            Stencil.CODEC
    );
    public static final PlacementModifierType<IsNextTo> IS_NEXT_TO = registerLegacy(
            "is_next_to",
            IsNextTo.CODEC
    );
    public static final PlacementModifierType<NoiseFilter> NOISE_FILTER = registerLegacy(
            "noise_filter",
            NoiseFilter.CODEC
    );
    public static final PlacementModifierType<Debug> DEBUG = registerLegacy(
            "debug",
            Debug.CODEC
    );

    public static final PlacementModifierType<Merge> FOR_ALL = registerLegacy(
            "for_all",
            Merge.CODEC
    );

    public static final PlacementModifierType<FindInDirection> SOLID_IN_DIR = registerLegacy(
            "solid_in_dir",
            FindInDirection.CODEC
    );

    public static final PlacementModifierType<All> ALL = registerLegacy(
            "all",
            All.CODEC
    );

    public static final PlacementModifierType<IsBasin> IS_BASIN = registerLegacy(
            "is_basin",
            IsBasin.CODEC
    );

    public static final PlacementModifierType<Is> IS = registerLegacy(
            "is",
            Is.CODEC
    );

    public static final PlacementModifierType<Offset> OFFSET = registerLegacy(
            "offset",
            Offset.CODEC
    );

    public static final PlacementModifierType<OffsetProvider> OFFSET_PROVIDER = register(
            "offset_provider",
            OffsetProvider.CODEC
    );

    public static final PlacementModifierType<Extend> EXTEND = registerLegacy(
            "extend",
            Extend.CODEC
    );

    public static final PlacementModifierType<ExtendXZ> EXTEND_XZ = register(
            "extend_xz",
            ExtendXZ.CODEC
    );

    public static final PlacementModifierType<EveryLayer> EVERY_LAYER = register(
            "every_layer",
            EveryLayer.CODEC
    );

    private static final PlacementModifierType<EveryLayer> ON_EVERY_LAYER_LEGACY = register(
            LegacyHelper.BCLIB_CORE.id("on_every_layer"),
            EveryLayer.CODEC,
            false
    );
    private static final PlacementModifierType<EveryLayer> UNDER_EVERY_LAYER_LEGACY = register(
            LegacyHelper.BCLIB_CORE.id("under_every_layer"),
            EveryLayer.CODEC_LEGACY_UNDER,
            false
    );


    private static <P extends PlacementModifier> PlacementModifierType<P> registerLegacy(String path, Codec<P> codec) {
        var id = WoverFeature.C.id(path);
        return register(id, codec, true);
    }

    private static <P extends PlacementModifier> PlacementModifierType<P> register(String path, Codec<P> codec) {
        var id = WoverFeature.C.id(path);
        return register(id, codec, false);
    }

    public static <P extends PlacementModifier> PlacementModifierType<P> register(
            ResourceLocation location,
            Codec<P> codec,
            boolean withLegacyBCLib
    ) {
        PlacementModifierType<P> res = Registry.register(
                BuiltInRegistries.PLACEMENT_MODIFIER_TYPE,
                location,
                () -> codec
        );

        if (withLegacyBCLib) {
            Registry.<PlacementModifierType<?>, PlacementModifierType<P>>register(
                    BuiltInRegistries.PLACEMENT_MODIFIER_TYPE,
                    LegacyHelper.BCLIB_CORE.convertNamespace(location),
                    () -> codec
            );
        }
        return res;
    }

    @ApiStatus.Internal
    public static void ensureStaticInitialization() {

    }
}

