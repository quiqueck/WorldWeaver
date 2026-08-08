package de.ambertation.wover.testmod.entrypoint;

import de.ambertation.wover.generator.impl.biomesource.nether.WoverNetherBiomeSource;
import de.ambertation.wover.generator.impl.chunkgenerator.WoverChunkGenerator;

import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

/**
 * "Does selecting a non-default WoVer world type (server.properties {@code level-type}) actually
 * stick" - i.e. does the overworld actually generate with the noise settings that preset specifies, not
 * silently falling back to the default one; and does WoVer's own biome injection (the testmod's nether
 * test biomes) still reach the shared BiomeSource regardless of which overworld preset was picked, since
 * preset selection is a whole-world setting but dimension biome injection is independent of it.
 * <p>
 * Checks the overworld's {@link NoiseGeneratorSettings} holder key, not its generator class:
 * {@code wover:normal}/{@code wover:large}/{@code wover:amplified} (see their own committed
 * {@code world_preset} JSON) all use plain vanilla {@code minecraft:noise} + {@code minecraft:multi_noise}
 * for the <b>overworld</b> specifically, differing only in the {@code settings} key
 * ({@code minecraft:overworld}/{@code large_biomes}/{@code amplified}) - WoVer's own generator
 * ({@code wover:betterx}, {@link WoverChunkGenerator}) is used for End/Nether in every preset, never the
 * overworld. An earlier version of this check asserted {@code instanceof WoverChunkGenerator} on the
 * overworld, which is simply the wrong thing to check for these presets and would always report a false
 * failure.
 * <p>
 * Separately, and unconditionally (regardless of which preset was selected): both End and Nether must
 * be on {@link WoverChunkGenerator}. This is the check that actually answers "was a fresh server created
 * with wover:betterx at all" - vanilla's own fallback preset for a truly absent {@code level-type} would
 * use vanilla's own End/Nether generators instead, which looks identical to a correctly-defaulted WoVer
 * server from the overworld's noise settings alone (both {@code wover:normal} and vanilla's default use
 * plain {@code minecraft:overworld} noise settings for the overworld) - so this is the one signal that
 * actually distinguishes "our default-preset injection ran" from "it silently didn't".
 * <p>
 * Deliberately not a {@code @GameTest}: {@code GameTestServer} hardcodes {@code WorldPresets.FLAT} for
 * every dimension it creates (see {@code CompatWorldgenBootCheck}'s class doc for the bytecode-level
 * confirmation), which would make "which preset got selected" unobservable - the overworld would always
 * be flat regardless of what {@code level-type} was requested. This instead boots a plain dedicated
 * server (the {@code ServerLifecycleEvents.SERVER_STARTED}-then-halt pattern used throughout this
 * framework) with a real {@code level-type} set in server.properties.
 * <p>
 * Gated behind {@value #EXPECTED_PRESET_PROPERTY} (only its own dedicated run passes it) - this class is
 * on the testmod's normal {@code main} entrypoint list, so without the guard it would also fire (and
 * halt the server) on an ordinary {@code testmodServer} dev run.
 */
public class WorldPresetStickBootCheck implements ModInitializer {
    private static final String EXPECTED_PRESET_PROPERTY = "wover.generator.expectedPreset";

    // Maps the selected world preset id (what server.properties' level-type actually says) to the
    // overworld NoiseGeneratorSettings id that preset's own committed JSON specifies - see this class's
    // own doc for why the settings key, not the generator class, is the real per-preset differentiator.
    private static final Map<String, String> EXPECTED_NOISE_SETTINGS = Map.of(
            "wover:normal", "minecraft:overworld",
            "wover:large", "minecraft:large_biomes",
            "wover:amplified", "minecraft:amplified"
    );

    @Override
    public void onInitialize() {
        final String expectedPresetRaw = System.getProperty(EXPECTED_PRESET_PROPERTY);
        if (expectedPresetRaw == null || expectedPresetRaw.isBlank()) {
            return;
        }

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            try {
                runCheck(server, expectedPresetRaw);
                System.out.println("[wover-generator] world-preset-stick-boot-check: PASS (" + expectedPresetRaw + ")");
                server.halt(false);
            } catch (Throwable t) {
                t.printStackTrace();
                System.err.println("[wover-generator] world-preset-stick-boot-check: FAIL - " + t.getMessage());
                // Runtime.halt(1), not server.halt(false): a clean shutdown would exit 0 regardless of
                // what the check found, same reasoning as every other boot-check in this framework.
                Runtime.getRuntime().halt(1);
            }
        });
    }

    private static void runCheck(MinecraftServer server, String expectedPresetId) {
        final List<String> failures = new ArrayList<>();

        final String expectedSettingsId = EXPECTED_NOISE_SETTINGS.get(expectedPresetId);
        if (expectedSettingsId == null) {
            failures.add("no known expected NoiseGeneratorSettings for preset " + expectedPresetId
                    + " - add it to EXPECTED_NOISE_SETTINGS");
        } else {
            final ServerLevel overworld = server.overworld();
            final ChunkGenerator generator = overworld.getChunkSource().getGenerator();
            if (!(generator instanceof NoiseBasedChunkGenerator noiseGenerator)) {
                failures.add("overworld generator is " + generator.getClass().getName()
                        + ", expected a NoiseBasedChunkGenerator");
            } else {
                final Holder<NoiseGeneratorSettings> settings = noiseGenerator.generatorSettings();
                final var actualId = settings.unwrapKey().map(k -> k.identifier().toString()).orElse(null);
                if (!expectedSettingsId.equals(actualId)) {
                    failures.add("overworld's noise settings are " + actualId + ", expected " + expectedSettingsId
                            + " (preset " + expectedPresetId + ") - the selected level-type was ignored or overridden");
                }
            }
        }

        final ServerLevel nether = server.getLevel(Level.NETHER);
        if (nether == null) {
            failures.add("minecraft:the_nether did not load at all");
        } else {
            final ChunkGenerator netherGenerator = nether.getChunkSource().getGenerator();
            if (!(netherGenerator instanceof WoverChunkGenerator)) {
                failures.add("nether generator is " + netherGenerator.getClass().getName()
                        + ", expected a WoverChunkGenerator (wover:betterx) - a fresh/default server should always "
                        + "get WoVer's own generator for the_nether, regardless of which overworld preset was picked");
            }
            final var netherBiomeSource = netherGenerator.getBiomeSource();
            if (!(netherBiomeSource instanceof WoverNetherBiomeSource)) {
                failures.add("nether BiomeSource is " + netherBiomeSource.getClass().getName()
                        + ", expected a WoverNetherBiomeSource");
            } else {
                boolean hasTestmodBiome = false;
                for (Holder<Biome> holder : netherBiomeSource.possibleBiomes()) {
                    final var key = holder.unwrapKey();
                    if (key.isPresent() && key.get().identifier().getNamespace().equals("wover-generator-testmod")) {
                        hasTestmodBiome = true;
                        break;
                    }
                }
                if (!hasTestmodBiome) {
                    failures.add("no wover-generator-testmod biome reachable in the_nether's BiomeSource - "
                            + "WoVer's own biome injection did not survive this preset selection");
                }
            }
        }

        final ServerLevel end = server.getLevel(Level.END);
        if (end == null) {
            failures.add("minecraft:the_end did not load at all");
        } else {
            final ChunkGenerator endGenerator = end.getChunkSource().getGenerator();
            if (!(endGenerator instanceof WoverChunkGenerator)) {
                failures.add("end generator is " + endGenerator.getClass().getName()
                        + ", expected a WoverChunkGenerator (wover:betterx) - a fresh/default server should always "
                        + "get WoVer's own generator for the_end, regardless of which overworld preset was picked");
            }
        }

        if (!failures.isEmpty()) {
            throw new IllegalStateException(String.join("; ", failures));
        }
    }
}
