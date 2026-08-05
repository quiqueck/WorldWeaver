package de.ambertation.wover.feature.impl.placed.modifiers;

import de.ambertation.wover.entrypoint.LibWoverFeature;
import de.ambertation.wover.feature.api.placed.modifiers.*;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
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

    public static final PlacementModifierType<InBiome> IN_BIOME = registerLegacy(
            "in_biome",
            InBiome.CODEC
    );

    public static final PlacementModifierType<ExtendXYZ> EXTEND_XZ = register(
            "extend_xyz",
            ExtendXYZ.CODEC
    );

    public static final PlacementModifierType<EveryLayer> EVERY_LAYER = register(
            "every_layer",
            EveryLayer.CODEC
    );


    private static <P extends PlacementModifier> PlacementModifierType<P> registerLegacy(
            String path,
            MapCodec<P> codec
    ) {
        var id = LibWoverFeature.C.id(path);
        return register(id, codec);
    }

    private static <P extends PlacementModifier> PlacementModifierType<P> register(String path, MapCodec<P> codec) {
        var id = LibWoverFeature.C.id(path);
        return register(id, codec);
    }

    public static <P extends PlacementModifier> PlacementModifierType<P> register(
            Identifier location,
            MapCodec<P> codec
    ) {
        PlacementModifierType<P> res = Registry.register(
                BuiltInRegistries.PLACEMENT_MODIFIER_TYPE,
                location,
                () -> codec
        );
        return res;
    }

    @ApiStatus.Internal
    public static void ensureStaticInitialization() {

    }
}

