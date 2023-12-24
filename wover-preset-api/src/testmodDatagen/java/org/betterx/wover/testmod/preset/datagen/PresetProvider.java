package org.betterx.wover.testmod.preset.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverWorldPresetProvider;
import org.betterx.wover.preset.api.WorldPresetManager;
import org.betterx.wover.preset.api.WorldPresetTags;
import org.betterx.wover.preset.api.context.WorldPresetBootstrapContext;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;
import org.betterx.wover.testmod.entrypoint.TestModWoverWorldPreset;

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
