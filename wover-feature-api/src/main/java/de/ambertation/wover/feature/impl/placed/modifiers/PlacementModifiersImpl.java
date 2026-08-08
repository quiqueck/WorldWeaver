package de.ambertation.wover.feature.impl.placed.modifiers;

import de.ambertation.wover.entrypoint.LibWoverFeature;
import de.ambertation.wover.feature.api.placed.modifiers.*;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import org.jetbrains.annotations.ApiStatus;

/**
 * Registers WorldWeaver's {@link PlacementModifier} types.
 * <p>
 * 26.3 deleted {@code PlacementModifierType}: {@code BuiltInRegistries.PLACEMENT_MODIFIER_TYPE} is now a
 * {@code Registry<MapCodec<? extends PlacementModifier>>} and {@link PlacementModifier#codec()} returns the
 * {@link MapCodec} directly. The registered value is therefore the codec itself, so the constants below are
 * the codecs rather than a wrapper type. Registration order and ids are unchanged, so serialized data keeps
 * working.
 */
public class PlacementModifiersImpl {
    public static final MapCodec<Stencil> STENCIL = register(
            "stencil",
            Stencil.CODEC
    );
    public static final MapCodec<IsNextTo> IS_NEXT_TO = register(
            "is_next_to",
            IsNextTo.CODEC
    );
    public static final MapCodec<NoiseFilter> NOISE_FILTER = register(
            "noise_filter",
            NoiseFilter.CODEC
    );
    public static final MapCodec<Debug> DEBUG = register(
            "debug",
            Debug.CODEC
    );

    public static final MapCodec<Merge> FOR_ALL = register(
            "for_all",
            Merge.CODEC
    );

    public static final MapCodec<FindInDirection> SOLID_IN_DIR = register(
            "solid_in_dir",
            FindInDirection.CODEC
    );

    public static final MapCodec<All> ALL = register(
            "all",
            All.CODEC
    );

    public static final MapCodec<IsBasin> IS_BASIN = register(
            "is_basin",
            IsBasin.CODEC
    );

    public static final MapCodec<Is> IS = register(
            "is",
            Is.CODEC
    );

    public static final MapCodec<Offset> OFFSET = register(
            "offset",
            Offset.CODEC
    );

    public static final MapCodec<OffsetProvider> OFFSET_PROVIDER = register(
            "offset_provider",
            OffsetProvider.CODEC
    );

    public static final MapCodec<Extend> EXTEND = register(
            "extend",
            Extend.CODEC
    );

    public static final MapCodec<InBiome> IN_BIOME = register(
            "in_biome",
            InBiome.CODEC
    );

    public static final MapCodec<ExtendXYZ> EXTEND_XZ = register(
            "extend_xyz",
            ExtendXYZ.CODEC
    );

    public static final MapCodec<EveryLayer> EVERY_LAYER = register(
            "every_layer",
            EveryLayer.CODEC
    );

    private static <P extends PlacementModifier> MapCodec<P> register(String path, MapCodec<P> codec) {
        return register(LibWoverFeature.C.id(path), codec);
    }

    public static <P extends PlacementModifier> MapCodec<P> register(
            Identifier location,
            MapCodec<P> codec
    ) {
        Registry.register(
                BuiltInRegistries.PLACEMENT_MODIFIER_TYPE,
                location,
                codec
        );
        return codec;
    }

    @ApiStatus.Internal
    public static void ensureStaticInitialization() {

    }
}
