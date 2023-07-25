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
    public static final PlacementModifierType<Stencil> STENCIL = register(
            "stencil",
            Stencil.CODEC
    );
    public static final PlacementModifierType<IsNextTo> IS_NEXT_TO = register(
            "is_next_to",
            IsNextTo.CODEC
    );
    public static final PlacementModifierType<NoiseFilter> NOISE_FILTER = register(
            "noise_filter",
            NoiseFilter.CODEC
    );
    public static final PlacementModifierType<Debug> DEBUG = register(
            "debug",
            Debug.CODEC
    );

    public static final PlacementModifierType<ForAll> FOR_ALL = register(
            "for_all",
            ForAll.CODEC
    );

    public static final PlacementModifierType<FindSolidInDirection> SOLID_IN_DIR = register(
            "solid_in_dir",
            FindSolidInDirection.CODEC
    );

    public static final PlacementModifierType<All> ALL = register(
            "all",
            All.CODEC
    );

    public static final PlacementModifierType<IsBasin> IS_BASIN = register(
            "is_basin",
            IsBasin.CODEC
    );

    public static final PlacementModifierType<Is> IS = register(
            "is",
            Is.CODEC
    );

    public static final PlacementModifierType<Offset> OFFSET = register(
            "offset",
            Offset.CODEC
    );

    public static final PlacementModifierType<Extend> EXTEND = register(
            "extend",
            Extend.CODEC
    );

    public static final PlacementModifierType<OnEveryLayer> ON_EVERY_LAYER = register(
            "on_every_layer",
            OnEveryLayer.CODEC
    );

    public static final PlacementModifierType<UnderEveryLayer> UNDER_EVERY_LAYER = register(
            "under_every_layer",
            UnderEveryLayer.CODEC
    );

    private static <P extends PlacementModifier> PlacementModifierType<P> register(String path, Codec<P> codec) {
        var id = WoverFeature.C.id(path);
        return register(id, codec, true);
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

