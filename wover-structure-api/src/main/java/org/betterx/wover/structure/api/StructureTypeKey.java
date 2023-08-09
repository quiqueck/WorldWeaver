package org.betterx.wover.structure.api;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class StructureTypeKey<S extends Structure> {
    @NotNull
    public final StructureType<S> type;

    public interface StructureFactory<S extends Structure> {
        S create(Structure.StructureSettings structureSettings);
    }

    /**
     * For internal use only. Use {@link StructureManager#registerType(ResourceLocation, StructureFactory, Codec)}
     * to explicitly register am create key. Or {@link StructureManager#structure(ResourceLocation, StructureFactory, Codec)}
     * to implicitly register and create a key that will be used by a structure.
     *
     * @param type             the structure type
     * @param structureFactory the structure factory
     */
    @ApiStatus.Internal
    protected StructureTypeKey(
            StructureType<S> type,
            @NotNull StructureFactory<S> structureFactory
    ) {
        this.type = type;
        this.structureFactory = structureFactory;
    }

    @NotNull
    public final StructureFactory<S> structureFactory;
}
