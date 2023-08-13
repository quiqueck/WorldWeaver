package org.betterx.wover.datagen.api.provider;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverDataProvider;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;
import org.betterx.wover.datagen.api.WoverTagProvider;
import org.betterx.wover.preset.api.WorldPresetTags;
import org.betterx.wover.preset.api.context.WorldPresetBootstrapContext;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

import java.util.concurrent.CompletableFuture;

/**
 * A provider to easily set up {@link WorldPreset}s
 */
public abstract class WoverWorldPresetProvider
        extends WoverRegistryContentProvider<WorldPreset>
        implements WoverDataProvider.Secondary<FabricTagProvider<WorldPreset>> {
    /**
     * Creates a new instance of {@link WoverWorldPresetProvider}.
     *
     * @param modCore The ModCore instance of the Mod that is providing this instance.
     * @param title   The title of the provider. Mainly used for logging.
     */
    public WoverWorldPresetProvider(
            ModCore modCore,
            String title
    ) {
        super(modCore, title, Registries.WORLD_PRESET);
    }

    /**
     * Called, when the Elements of the Registry need to be created and registered.
     * <p>
     * This implementation just redirects to {@link #bootstrap(WorldPresetBootstrapContext)}
     *
     * @param context The context to add the elements to.
     */
    @Override
    protected final void bootstrap(BootstapContext<WorldPreset> context) {
        bootstrap(new WorldPresetBootstrapContext(context));
    }

    /**
     * Called, when the Elements of the Registry need to be created and registered.
     * <p>
     * This method provides additional contextual Data that is usefull for
     * when creating new {@link WorldPreset}s.
     *
     * @param context The context to add the elements to.
     */
    protected abstract void bootstrap(WorldPresetBootstrapContext context);
    /**
     * Called, when the Tags of the Registry need to be created and registered.
     *
     * @param provider The provider to add the tags to.
     */
    protected abstract void prepareTags(TagBootstrapContext<WorldPreset> provider);

    /**
     * Returns the {@link FabricTagProvider} for the {@link WorldPreset}s.
     *
     * @param output           The output to write the data to.
     * @param registriesFuture A future sent from the Fabric DataGen API
     * @return A new {@link FabricTagProvider}
     */
    public FabricTagProvider<WorldPreset> getSecondaryProvider(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        return new WorldPresetTagProvider(modCore).getProvider(output, registriesFuture);
    }

    private class WorldPresetTagProvider extends WoverTagProvider<WorldPreset, TagBootstrapContext<WorldPreset>> {
        public WorldPresetTagProvider(ModCore modCore) {
            super(modCore, WorldPresetTags.TAGS);
        }

        @Override
        protected void prepareTags(TagBootstrapContext<WorldPreset> provider) {
            WoverWorldPresetProvider.this.prepareTags(provider);
        }
    }
}
