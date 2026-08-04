package de.ambertation.wover.structure.impl;

import de.ambertation.wover.structure.api.StructureKey;
import de.ambertation.wover.structure.api.StructureTypeKey;
import de.ambertation.wover.structure.api.builders.StructureBuilder;
import de.ambertation.wover.structure.impl.builders.StructureBuilderImpl;

import net.minecraft.data.worldgen.BootstrapContext;
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
    public StructureBuilder<S> bootstrap(BootstrapContext<Structure> context) {
        return new StructureBuilderImpl<>(this, context);
    }
}
