package de.ambertation.wover.datagen.api.provider.multi;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.PackBuilder;
import de.ambertation.wover.datagen.api.WoverMultiProvider;
import de.ambertation.wover.datagen.api.WoverTagProvider;
import de.ambertation.wover.datagen.api.provider.WoverStructurePoolProvider;
import de.ambertation.wover.datagen.api.provider.WoverStructureProcessorProvider;
import de.ambertation.wover.datagen.api.provider.WoverStructureSetProvider;
import de.ambertation.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;


/**
 * A {@link WoverMultiProvider} for {@link Structure}s, {@link StructureSet}s, {@link StructureTemplatePool}s and {@link StructureProcessorList}s.
 */
public abstract class WoverStructureProvider implements WoverMultiProvider {
    /**
     * The {@link ModCore} of the Mod.
     */
    protected final ModCore modCore;

    /**
     * Creates a new instance of {@link WoverStructureProvider}.
     *
     * @param modCore The {@link ModCore} of the Mod.
     */
    public WoverStructureProvider(ModCore modCore) {
        this.modCore = modCore;
    }

    /**
     * Called, when the Elements of the Registry need to be created and registered.
     *
     * @param context The context to add the elements to.
     */
    protected abstract void bootstrapSturctures(BootstrapContext<Structure> context);

    /**
     * Called, when the Elements of the Registry need to be created and registered.
     *
     * @param context The context to add the elements to.
     */
    protected abstract void bootstrapSets(BootstrapContext<StructureSet> context);

    /**
     * Called, when the Elements of the Registry need to be created and registered.
     *
     * @param context The context to add the elements to.
     */
    protected abstract void bootstrapPools(BootstrapContext<StructureTemplatePool> context);

    /**
     * Called, when the Elements of the Registry need to be created and registered.
     *
     * @param context The context to add the elements to.
     */
    protected abstract void bootstrapProcessors(BootstrapContext<StructureProcessorList> context);

    /**
     * Called, when the Tags need to be set up.
     *
     * @param context The context to add the tags to.
     */
    protected abstract void prepareBiomeTags(TagBootstrapContext<Biome> context);

    /**
     * Registers all providers
     *
     * @param pack The {@link PackBuilder} to register the providers to.
     */
    @Override
    public void registerAllProviders(PackBuilder pack) {
        pack.addRegistryProvider(modCore ->
                new de.ambertation.wover.datagen.api.provider.WoverStructureProvider(modCore) {
                    @Override
                    protected void bootstrap(BootstrapContext<Structure> context) {
                        bootstrapSturctures(context);
                    }
                }
        );

        pack.addRegistryProvider(modCore ->
                new WoverStructureSetProvider(modCore) {
                    @Override
                    protected void bootstrap(BootstrapContext<StructureSet> context) {
                        bootstrapSets(context);
                    }
                }
        );

        pack.addRegistryProvider(modCore ->
                new WoverStructurePoolProvider(modCore) {
                    @Override
                    protected void bootstrap(BootstrapContext<StructureTemplatePool> context) {
                        bootstrapPools(context);
                    }
                }
        );

        pack.addRegistryProvider(modCore ->
                new WoverStructureProcessorProvider(modCore) {
                    @Override
                    protected void bootstrap(BootstrapContext<StructureProcessorList> context) {
                        bootstrapProcessors(context);
                    }
                }
        );

        pack.addProvider(modCore ->
                new WoverTagProvider.ForBiomes(modCore) {
                    @Override
                    protected String getTitle() {
                        return "Structure Provider";
                    }

                    @Override
                    public void prepareTags(TagBootstrapContext<Biome> provider) {
                        prepareBiomeTags(provider);
                    }
                }
        );
    }
}
