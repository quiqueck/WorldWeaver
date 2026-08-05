package de.ambertation.wover.datagen.api.provider;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverDataProvider;
import de.ambertation.wover.datagen.api.WoverRegistryContentProvider;
import de.ambertation.wover.datagen.api.WoverTagProvider;
import de.ambertation.wover.preset.api.context.FlatLevelPresetBootstrapContext;
import de.ambertation.wover.preset.api.flat.FlatLevelPresetTags;
import de.ambertation.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import java.util.concurrent.CompletableFuture;

/**
 * A provider to easily set up {@link FlatLevelGeneratorPreset}s
 */
public abstract class WoverFlatLevelPresetProvider
        extends WoverRegistryContentProvider<FlatLevelGeneratorPreset>
        implements WoverDataProvider.Secondary<TagsProvider<FlatLevelGeneratorPreset>> {

    /**
     * Creates a new instance of {@link WoverRegistryContentProvider}.
     *
     * @param modCore The ModCore instance of the Mod that is providing this instance.
     * @param title   The title of the provider. Mainly used for logging.
     */
    public WoverFlatLevelPresetProvider(
            ModCore modCore,
            String title
    ) {
        super(modCore, title, Registries.FLAT_LEVEL_GENERATOR_PRESET);
    }


    /**
     * Called, when The Data needs to be serialized.
     *
     * @param output           The output to write the data to.
     * @param registriesFuture A future sent from the Fabric DataGen API
     * @return A new {@link DataProvider}
     */
    @Override
    public TagsProvider<FlatLevelGeneratorPreset> getSecondaryProvider(
            FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        return new FlatLevelTagProvider(modCore).getProvider(output, registriesFuture);
    }

    /**
     * Called, when the Elements of the Registry need to be created and registered.
     * <p>
     * This method provides an extended register Method that is specialized to create
     * {@link FlatLevelGeneratorPreset}.
     *
     * @param context The context to add the elements to.
     */
    protected abstract void bootstrap(FlatLevelPresetBootstrapContext context);

    /**
     * Called before the tags are written to disk.
     * <p>
     * This method is used to add elements to the tags. The {@link TagBootstrapContext}
     * provides the necessary methods to add elements.
     *
     * @param provider the {@link TagBootstrapContext} you can use to add elements to the tags
     */
    protected abstract void prepareTags(TagBootstrapContext<FlatLevelGeneratorPreset> provider);

    /**
     * Called, when the Elements of the Registry need to be created and registered.
     * <p>
     * This implementation just redirects to {@link #bootstrap(FlatLevelPresetBootstrapContext)}
     *
     * @param context The context to add the elements to.
     */
    @Override
    protected final void bootstrap(BootstrapContext<FlatLevelGeneratorPreset> context) {
        bootstrap(new FlatLevelPresetBootstrapContext(context));
    }

    private class FlatLevelTagProvider extends WoverTagProvider<FlatLevelGeneratorPreset, TagBootstrapContext<FlatLevelGeneratorPreset>> {
        public FlatLevelTagProvider(ModCore modCore) {
            super(modCore, FlatLevelPresetTags.TAGS);
        }

        @Override
        public void prepareTags(TagBootstrapContext<FlatLevelGeneratorPreset> provider) {
            WoverFlatLevelPresetProvider.this.prepareTags(provider);
        }
    }
}
