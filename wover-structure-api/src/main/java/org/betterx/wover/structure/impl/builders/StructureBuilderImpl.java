package org.betterx.wover.structure.impl.builders;

import org.betterx.wover.core.api.ModCore;
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
        var res = type.structureFactory.create(buildSettings());
        if (ModCore.isDevEnvironment()) {
            if (res.type() == null) {
                throw new IllegalStateException("Structure type is null for " + key.key.location());
            }

            if (!res.type().equals(key.type())) {
                throw new IllegalStateException("Structure type is not the expected one for " + key.key.location());
            }
        }
        return res;
    }


}
