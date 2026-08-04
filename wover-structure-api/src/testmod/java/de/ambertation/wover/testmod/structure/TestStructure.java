package de.ambertation.wover.testmod.structure;

import de.ambertation.wover.entrypoint.LibWoverStructure;
import de.ambertation.wover.testmod.entrypoint.TestModWoverStructure;

import net.minecraft.core.BlockPos;
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
        BlockPos pos = new BlockPos(generationContext.chunkPos().getMiddleBlockX(), 100,  generationContext.chunkPos().getMiddleBlockZ()) ;
        return Optional.of(new Structure.GenerationStub(pos, builder -> {}));
        //return Optional.empty();
    }

    @Override
    public @NotNull StructureType<?> type() {
        return TestModWoverStructure.TEST_STRUCTURE.type();
    }
}
