package org.betterx.wover.structure.impl;

import org.betterx.wover.structure.api.StructureKey;
import org.betterx.wover.structure.api.builders.JigsawBuilder;
import org.betterx.wover.structure.impl.builders.JigsawBuilderImpl;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

import org.jetbrains.annotations.NotNull;

public class JigsawKeyImpl extends StructureKey<JigsawStructure, JigsawBuilder> {
    public JigsawKeyImpl(
            @NotNull ResourceLocation structureId
    ) {
        super(structureId);
    }

    @Override
    public JigsawBuilder bootstrap(BootstapContext<Structure> context) {
        return new JigsawBuilderImpl(this, context);
    }

    public StructureType<JigsawStructure> type() {
        return StructureType.JIGSAW;
    }
}
