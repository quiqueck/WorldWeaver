package org.betterx.wover.datagen.api.provider;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverRegistryContentProvider;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import org.jetbrains.annotations.NotNull;

/**
 * A {@link WoverRegistryContentProvider} for {@link StructureProcessorList}s.
 */
public abstract class WoverStructureProcessorProvider extends WoverRegistryContentProvider<StructureProcessorList> {
    /**
     * Creates a new instance of {@link WoverRegistryContentProvider}.
     *
     * @param modCore The ModCore instance of the Mod that is providing this instance.
     */
    public WoverStructureProcessorProvider(
            @NotNull ModCore modCore
    ) {
        this(modCore, modCore.id("default"));
    }

    /**
     * Creates a new instance of {@link WoverRegistryContentProvider}.
     *
     * @param modCore    The ModCore instance of the Mod that is providing this instance.
     * @param providerId The id of the provider. Every Provider (for the same Registry)
     *                   needs a unique id.
     */
    public WoverStructureProcessorProvider(
            @NotNull ModCore modCore,
            @NotNull ResourceLocation providerId
    ) {
        super(modCore, providerId.toString() + " (Structure Processors)", Registries.PROCESSOR_LIST);
    }

    /**
     * Called, when the Elements of the Registry need to be created and registered.
     *
     * @param context The context to add the elements to.
     */
    @Override
    protected abstract void bootstrap(BootstapContext<StructureProcessorList> context);
}
