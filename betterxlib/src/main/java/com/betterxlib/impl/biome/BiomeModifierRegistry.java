package com.betterxlib.impl.biome;

import com.betterxlib.BetterXLib;
import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

/**
 * Registry for BetterXLib's custom BiomeModifier types.
 */
public final class BiomeModifierRegistry {

    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIERS =
        DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, BetterXLib.MOD_ID);

    // Register custom biome modifier types
    public static final Supplier<MapCodec<AddEndBiomeModifier>> ADD_END_BIOME =
        BIOME_MODIFIERS.register("add_end_biome", () -> AddEndBiomeModifier.CODEC);

    public static final Supplier<MapCodec<AddNetherBiomeModifier>> ADD_NETHER_BIOME =
        BIOME_MODIFIERS.register("add_nether_biome", () -> AddNetherBiomeModifier.CODEC);

    public static final Supplier<MapCodec<AddFeatureToBiomeModifier>> ADD_FEATURE_TO_BIOME =
        BIOME_MODIFIERS.register("add_feature_to_biome", () -> AddFeatureToBiomeModifier.CODEC);

    private BiomeModifierRegistry() {
        // Utility class
    }

    /**
     * Register the biome modifier types.
     *
     * @param modEventBus the mod event bus
     */
    public static void register(IEventBus modEventBus) {
        BIOME_MODIFIERS.register(modEventBus);
        BetterXLib.LOGGER.debug("Registered BetterXLib biome modifier types");
    }
}
