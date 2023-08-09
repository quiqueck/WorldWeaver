package org.betterx.wover.structure.impl.builders;

import org.betterx.wover.structure.api.StructureTypeKey;
import org.betterx.wover.structure.api.builders.StructureBuilder;
import org.betterx.wover.structure.impl.StructureKeyImpl;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.levelgen.structure.Structure;

public class StructureBuilderImpl<S extends Structure> extends BaseStructureBuilderImpl<S, StructureBuilder<S>> implements StructureBuilder<S> {
    private final StructureTypeKey<S> type;

    public StructureBuilderImpl(
            StructureKeyImpl<S> key,
            BootstapContext<Structure> context
    ) {
        super(key, context);
        this.type = key.typeKey;
    }

    @Override
    protected Structure build() {
        return type.structureFactory.create(buildSettings());
    }
}
