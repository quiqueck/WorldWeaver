package org.betterx.wover.structure.api.sets;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import org.jetbrains.annotations.NotNull;

/**
 * A wrapper around {@link ResourceKey} that identifies a {@link StructureSet}. Create one with
 * {@link StructureSetManager#createKey(ResourceLocation)} (or the
 * {@link org.betterx.wover.structure.api.StructureKeys#set(ResourceLocation)} alias), then call
 * {@link #bootstrap(BootstrapContext)} to start building the set.
 */
public class StructureSetKey {
    /**
     * The key for the {@link StructureSet} you can use to reference it.
     */
    @NotNull
    public final ResourceKey<StructureSet> key;


    /**
     * Creates a {@link StructureSetBuilder} to build and register the {@link StructureSet} for this key
     * with the given {@link BootstrapContext}.
     *
     * @param context The bootstrap context to register the set with
     * @return The builder
     */
    public StructureSetBuilder bootstrap(@NotNull BootstrapContext<StructureSet> context) {
        return new StructureSetBuilder(key, context);
    }

    StructureSetKey(@NotNull ResourceLocation location) {
        this.key = ResourceKey.create(Registries.STRUCTURE_SET, location);
    }
}
