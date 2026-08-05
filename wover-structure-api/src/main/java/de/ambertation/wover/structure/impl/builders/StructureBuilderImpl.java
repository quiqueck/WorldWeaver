package de.ambertation.wover.structure.impl.builders;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.structure.api.StructureKey;
import de.ambertation.wover.structure.api.StructureTypeKey;
import de.ambertation.wover.structure.api.builders.StructureBuilder;
import de.ambertation.wover.structure.impl.SimpleStructureKeyImpl;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.structure.Structure;

public class StructureBuilderImpl<S extends Structure>
        extends BaseStructureBuilderImpl<S, StructureBuilder<S>, StructureKey.Simple<S>>
        implements StructureBuilder<S> {
    private final StructureTypeKey<S> type;

    public StructureBuilderImpl(
            SimpleStructureKeyImpl<S> key,
            BootstrapContext<Structure> context
    ) {
        super(key, context);
        this.type = key.typeKey;
    }

    @Override
    protected Structure build() {
        var res = type.structureFactory.create(buildSettings());
        if (ModCore.isDevEnvironment()) {
            if (res.type() == null) {
                throw new IllegalStateException("Structure type is null for " + key.key().identifier());
            }

            if (!res.type().equals(key.type())) {
                throw new IllegalStateException("Structure type is not the expected one for " + key.key().identifier());
            }
        }
        return res;
    }


}
