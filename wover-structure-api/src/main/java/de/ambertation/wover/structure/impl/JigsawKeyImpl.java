package de.ambertation.wover.structure.impl;

import de.ambertation.wover.structure.api.StructureKey;
import de.ambertation.wover.structure.api.builders.JigsawBuilder;
import de.ambertation.wover.structure.impl.builders.JigsawBuilderImpl;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

import org.jetbrains.annotations.NotNull;

public class JigsawKeyImpl
        extends StructureKeyImpl<JigsawStructure, JigsawBuilder, StructureKey.Jigsaw>
        implements StructureKey.Jigsaw {
    public JigsawKeyImpl(
            @NotNull Identifier structureId
    ) {
        super(structureId);
    }

    @Override
    public JigsawBuilder bootstrap(BootstrapContext<Structure> context) {
        return new JigsawBuilderImpl(this, context);
    }

    public StructureType<JigsawStructure> type() {
        return StructureType.JIGSAW;
    }
}
