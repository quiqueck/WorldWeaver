package de.ambertation.wover.datagen.api.provider;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverDataProvider;
import de.ambertation.wover.datagen.api.WoverRegistryContentProvider;
import de.ambertation.wover.datagen.api.WoverTagProvider;
import de.ambertation.wover.preset.api.WorldPresetTags;
import de.ambertation.wover.preset.api.context.WorldPresetBootstrapContext;
import de.ambertation.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import java.util.concurrent.CompletableFuture;

/**
 * A provider to easily set up {@link WorldPreset}s
 */
public abstract class WoverWorldPresetProvider
        extends WoverRegistryContentProvider<WorldPreset>
        implements WoverDataProvider.Secondary<TagsProvider<WorldPreset>> {
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
    protected final void bootstrap(BootstrapContext<WorldPreset> context) {
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
     * Returns the {@link TagsProvider} for the {@link WorldPreset}s.
     *
     * @param output           The output to write the data to.
     * @param registriesFuture A future sent from the Fabric DataGen API
     * @return A new {@link TagsProvider}
     */
    public TagsProvider<WorldPreset> getSecondaryProvider(
            FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        return new WorldPresetTagProvider(modCore).getProvider(output, registriesFuture);
    }

    private class WorldPresetTagProvider extends WoverTagProvider<WorldPreset, TagBootstrapContext<WorldPreset>> {
        public WorldPresetTagProvider(ModCore modCore) {
            super(modCore, WorldPresetTags.TAGS);
        }

        @Override
        public void prepareTags(TagBootstrapContext<WorldPreset> provider) {
            WoverWorldPresetProvider.this.prepareTags(provider);
        }
    }
}
