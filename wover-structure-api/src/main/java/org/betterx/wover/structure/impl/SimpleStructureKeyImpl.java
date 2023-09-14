package org.betterx.wover.structure.impl;

import org.betterx.wover.structure.api.StructureKey;
import org.betterx.wover.structure.api.StructureTypeKey;
import org.betterx.wover.structure.api.builders.StructureBuilder;
import org.betterx.wover.structure.impl.builders.StructureBuilderImpl;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import org.jetbrains.annotations.NotNull;

public class SimpleStructureKeyImpl<S extends Structure>
        extends StructureKeyImpl<S, StructureBuilder<S>, StructureKey.Simple<S>>
        implements StructureKey.Simple<S> {
    @NotNull
    public final StructureTypeKey<S> typeKey;

    public SimpleStructureKeyImpl(@NotNull ResourceLocation loc, @NotNull StructureTypeKey<S> type) {
        super(loc);
        this.typeKey = type;
    }

    @Override
    public StructureType<S> type() {
        return typeKey.type;
    }

    @Override
    public StructureBuilder<S> bootstrap(BootstapContext<Structure> context) {
        return new StructureBuilderImpl<>(this, context);
    }
}
