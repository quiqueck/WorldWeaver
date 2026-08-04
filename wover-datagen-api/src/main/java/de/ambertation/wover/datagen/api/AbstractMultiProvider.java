package de.ambertation.wover.datagen.api;

import de.ambertation.wover.core.api.ModCore;

import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;

/**
 * An abstract {@link WoverMultiProvider} that already adds commonly used fields, methods and constructors.
 */
public abstract class AbstractMultiProvider implements WoverMultiProvider {
    /**
     * The {@link ModCore} of the Mod.
     */
    protected final ModCore modCore;

    /**
     * The id of the provider. Every Provider (for the same Registry) needs a unique id.
     */
    protected final ResourceLocation providerId;

    /**
     * Creates a new instance of {@link AbstractMultiProvider} using a default provider id
     * (the string {@code "default"} in the mod's namespace).
     *
     * @param modCore The {@link ModCore} of the Mod.
     */
    public AbstractMultiProvider(@NotNull ModCore modCore) {
        this(modCore, modCore.id("default"));
    }

    /**
     * Creates a new instance of {@link AbstractMultiProvider}.
     *
     * @param modCore    The {@link ModCore} of the Mod.
     * @param providerId The id of the provider. Every Provider (for the same Registry)
     *                   needs a unique id.
     */
    public AbstractMultiProvider(@NotNull ModCore modCore, @NotNull ResourceLocation providerId) {
        this.modCore = modCore;
        this.providerId = providerId;
    }

    @Override
    public abstract void registerAllProviders(PackBuilder pack);
}
