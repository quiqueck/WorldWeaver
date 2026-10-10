package de.ambertation.wover.surface.impl;

import de.ambertation.wover.entrypoint.LibWoverSurface;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.Method;
import org.jetbrains.annotations.ApiStatus;

/**
 * Lets TerraBlender's End surface rules (Biomes O' Plenty, ...) apply to an End that does not use
 * vanilla's {@link TheEndBiomeSource}.
 * <p>
 * TerraBlender only adds the rules other mods registered for a dimension once that dimension's
 * {@link NoiseGeneratorSettings} carry a <i>rule category</i>
 * ({@code terrablender.api.MaterialRuleManager$RuleCategory} since TerraBlender 26.3, which renamed
 * {@code SurfaceRuleManager} to match vanilla's material rules), and it assigns that category in
 * {@code terrablender.util.LevelUtils#initializeBiomes}. The End only gets it when its dimension type is
 * tagged {@code terrablender:end_regions} or its biome source is exactly a {@link TheEndBiomeSource}.
 * TerraBlender 26.3 ships that tag with {@code minecraft:the_end} in it, so a default End is covered
 * either way; TerraBlender 26.2 had no such tag, and an End whose dimension type is not in it
 * (a datapack's own type, or a pack that replaces the tag) still loses every End surface rule once
 * WorldWeaver's End biome source is in place.
 * <p>
 * This assigns the END category ourselves. TerraBlender is an optional dependency, so it is only reached
 * through reflection, and every failure just leaves the settings untouched.
 * <p>
 * Timing: this runs from {@code BEFORE_CREATING_LEVELS} (the head of {@code MinecraftServer#createLevels}),
 * after TerraBlender's own {@code initializeBiomes} (which runs when the {@code MinecraftServer}
 * constructor returns). TerraBlender wraps the settings' {@code materialRule} holder lazily, on the first
 * call to {@code NoiseGeneratorSettings#materialRule()}, and in vanilla 26.3 the only caller of that is
 * {@code NoiseBasedChunkGenerator#buildTerrain}, i.e. chunk generation; WorldWeaver's own merge in
 * {@link SurfaceRuleUtil} reads and writes the field directly. {@code setRuleCategory} also drops any
 * wrapper TerraBlender has already cached, so the wrapper is always rebuilt around the rule as it stands
 * after our merge.
 */
@ApiStatus.Internal
final class TerraBlenderCompat {
    private static final String MOD_ID = "terrablender";
    private static final String SETTINGS_INTERFACE = "terrablender.worldgen.IExtendedNoiseGeneratorSettings";
    private static final String RULE_CATEGORY = "terrablender.api.MaterialRuleManager$RuleCategory";

    private static boolean initialized = false;
    private static Class<?> settingsInterface;
    private static Method setRuleCategory;
    private static Object endCategory;

    private TerraBlenderCompat() {
    }

    static void assignEndRuleCategory(
            ResourceKey<LevelStem> dimensionKey,
            NoiseGeneratorSettings settings,
            BiomeSource biomeSource
    ) {
        if (!LevelStem.END.equals(dimensionKey)) return;
        // TerraBlender handles this case on its own
        if (biomeSource instanceof TheEndBiomeSource) return;
        if (!init() || !settingsInterface.isInstance(settings)) return;

        try {
            setRuleCategory.invoke(settings, endCategory);
            LibWoverSurface.C.LOG.verbose(
                    "Assigned TerraBlender END surface rule category for {} using {}",
                    dimensionKey.identifier(),
                    biomeSource
            );
        } catch (ReflectiveOperationException | RuntimeException e) {
            LibWoverSurface.C.LOG.warn("Unable to enable TerraBlender surface rules for the End", e);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static synchronized boolean init() {
        if (initialized) return setRuleCategory != null;
        initialized = true;

        if (!FabricLoader.getInstance().isModLoaded(MOD_ID)) return false;
        try {
            final Class<?> categoryClass = Class.forName(RULE_CATEGORY);
            settingsInterface = Class.forName(SETTINGS_INTERFACE);
            endCategory = Enum.valueOf((Class<? extends Enum>) categoryClass, "END");
            setRuleCategory = settingsInterface.getMethod("setRuleCategory", categoryClass);
            return true;
        } catch (ReflectiveOperationException | RuntimeException e) {
            settingsInterface = null;
            setRuleCategory = null;
            LibWoverSurface.C.LOG.warn("TerraBlender is loaded, but its surface rule API was not found", e);
            return false;
        }
    }
}
