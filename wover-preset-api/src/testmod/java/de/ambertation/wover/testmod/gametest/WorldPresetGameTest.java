package de.ambertation.wover.testmod.gametest;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.FlatLevelGeneratorPresetTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Fabric GameTest for Concern 3 (world presets): custom world-preset datapack files get loaded, and
 * programmatically-registered presets get injected into the world.
 * <p>
 * The testmod contributes presets two ways, both of which must appear in the vanilla
 * {@link Registries#WORLD_PRESET} registry once the server is up:
 * <ul>
 *   <li>{@code wover:nether_start} - written as a committed datapack JSON under
 *       {@code src/testmod/generated/data/wover/worldgen/world_preset/} and loaded as data;</li>
 *   <li>{@code wover-preset-testmod:end_start} - registered programmatically via
 *       {@code WorldPresetManager.BOOTSTRAP_WORLD_PRESETS}.</li>
 * </ul>
 * A missing preset is a real regression in datapack loading or preset registration.
 */
public class WorldPresetGameTest {
    private static final String DATAPACK_PRESET = "wover:nether_start";
    private static final String INJECTED_PRESET = "wover-preset-testmod:end_start";
    private static final String FLAT_PRESET = "wover:nether";

    @GameTest
    public void customWorldPresetsAreLoadedAndInjected(GameTestHelper helper) {
        final Registry<WorldPreset> registry = helper
                .getLevel()
                .registryAccess()
                .lookupOrThrow(Registries.WORLD_PRESET);

        final List<String> failures = new ArrayList<>();

        if (!registry.containsKey(Identifier.parse(DATAPACK_PRESET))) {
            failures.add(DATAPACK_PRESET + ": expected world preset loaded from datapack JSON but it is missing");
        }
        if (!registry.containsKey(Identifier.parse(INJECTED_PRESET))) {
            failures.add(INJECTED_PRESET + ": expected world preset injected via BOOTSTRAP_WORLD_PRESETS but it is missing");
        }

        if (!failures.isEmpty()) {
            final List<String> present = new ArrayList<>();
            registry.keySet().forEach(k -> present.add(k.toString()));
            helper.fail(Component.literal(
                    "World-preset registry regression:\n - " + String.join("\n - ", failures)
                            + "\n\nPresets actually present: " + present
            ));
            return;
        }

        helper.succeed();
    }

    /**
     * Covers {@code FlatLevelPresetBootstrapContext#register(...)}, the flat-world counterpart of the
     * test above, which had no coverage at all.
     * <p>
     * That method is not a thin delegate: it resolves three registries through the context's
     * {@code lookup(...)} (structure sets, placed features and the biome), assembles a
     * {@link net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings} and pushes the layers
     * into it - so this asserts the whole result, not just that the key exists.
     * <p>
     * Note on layer order: {@code register} consumes {@code flatLayerInfos} bottom layer first, matching
     * vanilla, which reads {@code getLayersInfo()} bottom-up (see
     * {@code FlatLevelGeneratorSettings#updateLayers}) with index 0 at the lowest y. The testmod registers
     * {@code (12x netherrack, 2x nether_bricks)}, so the expectation below is netherrack at the bottom and
     * a nether-brick surface on top - i.e. the argument list in order. Until this was fixed the array was
     * iterated back-to-front and every flat preset generated inverted.
     */
    @GameTest
    public void flatLevelPresetIsRegisteredAndVisible(GameTestHelper helper) {
        final Registry<FlatLevelGeneratorPreset> registry = helper
                .getLevel()
                .registryAccess()
                .lookupOrThrow(Registries.FLAT_LEVEL_GENERATOR_PRESET);

        final List<String> failures = new ArrayList<>();

        final Holder.Reference<FlatLevelGeneratorPreset> holder = registry
                .get(Identifier.parse(FLAT_PRESET))
                .orElse(null);

        if (holder == null) {
            final List<String> present = new ArrayList<>();
            registry.keySet().forEach(k -> present.add(k.toString()));
            helper.fail(Component.literal(
                    FLAT_PRESET + ": expected flat level preset registered via "
                            + "BOOTSTRAP_FLAT_LEVEL_PRESETS but it is missing."
                            + "\n\nFlat level presets actually present: " + present
            ));
            return;
        }

        //the preset only shows up in the "Customize" list of a flat world if it is in this tag
        boolean visible = false;
        for (Holder<FlatLevelGeneratorPreset> tagged : registry.getTagOrEmpty(FlatLevelGeneratorPresetTags.VISIBLE)) {
            if (tagged.is(holder.key())) {
                visible = true;
                break;
            }
        }
        if (!visible) {
            failures.add(FLAT_PRESET + ": not in " + FlatLevelGeneratorPresetTags.VISIBLE.location()
                    + ", so it would never appear in the UI");
        }

        final FlatLevelGeneratorPreset preset = holder.value();
        if (!preset.displayItem().is(Items.NETHER_BRICKS.builtInRegistryHolder().key())) {
            failures.add("display item is " + preset.displayItem() + ", expected " + Items.NETHER_BRICKS);
        }

        //resolved through context.lookup(Registries.BIOME) while the registry was still loading
        if (!preset.settings().getBiome().is(Biomes.NETHER_WASTES)) {
            failures.add("biome is " + preset.settings().getBiome() + ", expected " + Biomes.NETHER_WASTES.identifier());
        }

        final List<FlatLayerInfo> layers = preset.settings().getLayersInfo();
        if (layers.size() != 2) {
            failures.add("expected 2 layers, got " + layers.size() + ": " + layers);
        } else {
            if (layers.get(0).getHeight() != 12 || !layers.get(0).getBlockState().is(Blocks.NETHERRACK)) {
                failures.add("bottom layer is " + layers.get(0) + ", expected 12x " + Blocks.NETHERRACK);
            }
            if (layers.get(1).getHeight() != 2 || !layers.get(1).getBlockState().is(Blocks.NETHER_BRICKS)) {
                failures.add("top layer is " + layers.get(1) + ", expected 2x " + Blocks.NETHER_BRICKS);
            }
        }

        if (!failures.isEmpty()) {
            helper.fail(Component.literal(
                    "Flat level preset regression for " + FLAT_PRESET + ":\n - " + String.join("\n - ", failures)
            ));
            return;
        }

        helper.succeed();
    }
}
