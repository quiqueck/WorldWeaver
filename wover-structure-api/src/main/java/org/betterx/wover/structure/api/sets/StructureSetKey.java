package org.betterx.wover.structure.api.sets;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import org.jetbrains.annotations.NotNull;

public class StructureSetKey {
    /**
     * The key for the {@link StructureSet} you can use to reference it.
     */
    @NotNull
    public final ResourceKey<StructureSet> key;


    public StructureSetBuilder bootstrap(@NotNull BootstapContext<StructureSet> context) {
        return new StructureSetBuilder(key, context);
    }

    StructureSetKey(@NotNull ResourceLocation location) {
        this.key = ResourceKey.create(Registries.STRUCTURE_SET, location);
    }
}
