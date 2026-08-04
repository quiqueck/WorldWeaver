package de.ambertation.wover.testmod.preset.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.WoverWorldPresetProvider;
import de.ambertation.wover.preset.api.WorldPresetManager;
import de.ambertation.wover.preset.api.WorldPresetTags;
import de.ambertation.wover.preset.api.context.WorldPresetBootstrapContext;
import de.ambertation.wover.tag.api.event.context.TagBootstrapContext;
import de.ambertation.wover.testmod.entrypoint.TestModWoverWorldPreset;

import net.minecraft.world.level.levelgen.presets.WorldPreset;

public class PresetProvider extends WoverWorldPresetProvider {


    /**
     * Creates a new instance of {@link WoverWorldPresetProvider}.
     *
     * @param modCore The ModCore instance of the Mod that is providing this instance.
     */
    public PresetProvider(ModCore modCore) {
        super(modCore, "Test WorldPresets");
    }

    @Override
    protected void bootstrap(WorldPresetBootstrapContext context) {
        var preset = WorldPresetManager.fromStems(
                context.netherStem,
                context.overworldStem,
                context.endStem
        );
        context.register(TestModWoverWorldPreset.NETHER_START, preset);
    }

    @Override
    protected void prepareTags(TagBootstrapContext<WorldPreset> provider) {
        provider.add(WorldPresetTags.NORMAL, TestModWoverWorldPreset.NETHER_START);
    }
}
