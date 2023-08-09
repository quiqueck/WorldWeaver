package org.betterx.wover.structure.impl;

import org.betterx.wover.structure.api.StructureTypeKey;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StructureTypeKeyImpl<S extends Structure> extends StructureTypeKey<S> {
    @Nullable
    public final ResourceKey<StructureType<?>> key;

    protected StructureTypeKeyImpl(
            @Nullable ResourceKey<StructureType<?>> key,
            @NotNull StructureType<S> type,
            @NotNull StructureFactory<S> structureFactory
    ) {
        super(type, structureFactory);
        this.key = key;
    }
}
