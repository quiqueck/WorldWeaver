package org.betterx.wover.testmod.structure;

import org.betterx.wover.testmod.entrypoint.WoverStructureTestMod;

import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class TestStructure extends Structure {

    public TestStructure(StructureSettings structureSettings) {
        super(structureSettings);
    }

    @Override
    protected @NotNull Optional<GenerationStub> findGenerationPoint(GenerationContext generationContext) {
        return Optional.empty();
    }

    @Override
    public @NotNull StructureType<?> type() {
        return WoverStructureTestMod.TEST_STRUCTURE.type();
    }
}
