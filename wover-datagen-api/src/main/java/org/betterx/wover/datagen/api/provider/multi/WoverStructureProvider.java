package org.betterx.wover.datagen.api.provider.multi;

import org.betterx.wover.datagen.api.PackBuilder;
import org.betterx.wover.datagen.api.WoverMultiProvider;
import org.betterx.wover.datagen.api.provider.WoverStructurePoolProvider;
import org.betterx.wover.datagen.api.provider.WoverStructureProcessorProvider;
import org.betterx.wover.datagen.api.provider.WoverStructureSetProvider;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;


/**
 * A {@link WoverMultiProvider} for {@link Structure}s, {@link StructureSet}s, {@link StructureTemplatePool}s and {@link StructureProcessorList}s.
 */
public abstract class WoverStructureProvider implements WoverMultiProvider {
    /**
     * Creates a new instance of {@link WoverStructureProvider}.
     */
    public WoverStructureProvider() {
    }

    /**
     * Called, when the Elements of the Registry need to be created and registered.
     *
     * @param context The context to add the elements to.
     */
    protected abstract void bootstrapSturctures(BootstapContext<Structure> context);

    /**
     * Called, when the Elements of the Registry need to be created and registered.
     *
     * @param context The context to add the elements to.
     */
    protected abstract void bootstrapSets(BootstapContext<StructureSet> context);

    /**
     * Called, when the Elements of the Registry need to be created and registered.
     *
     * @param context The context to add the elements to.
     */
    protected abstract void bootstrapPools(BootstapContext<StructureTemplatePool> context);

    /**
     * Called, when the Elements of the Registry need to be created and registered.
     *
     * @param context The context to add the elements to.
     */
    protected abstract void bootstrapPorcessors(BootstapContext<StructureProcessorList> context);

    /**
     * Registers all  providers
     *
     * @param pack The {@link PackBuilder} to register the providers to.
     */
    @Override
    public void registerAllProviders(PackBuilder pack) {
        pack.addRegistryProvider(modCore ->
                new org.betterx.wover.datagen.api.provider.WoverStructureProvider(modCore) {
                    @Override
                    protected void bootstrap(BootstapContext<Structure> context) {
                        bootstrapSturctures(context);
                    }
                }
        );

        pack.addRegistryProvider(modCore ->
                new WoverStructureSetProvider(modCore) {
                    @Override
                    protected void bootstrap(BootstapContext<StructureSet> context) {
                        bootstrapSets(context);
                    }
                }
        );

        pack.addRegistryProvider(modCore ->
                new WoverStructurePoolProvider(modCore) {
                    @Override
                    protected void bootstrap(BootstapContext<StructureTemplatePool> context) {
                        bootstrapPools(context);
                    }
                }
        );


        pack.addRegistryProvider(modCore ->
                new WoverStructureProcessorProvider(modCore) {
                    @Override
                    protected void bootstrap(BootstapContext<StructureProcessorList> context) {
                        bootstrapPorcessors(context);
                    }
                }
        );
    }
}
