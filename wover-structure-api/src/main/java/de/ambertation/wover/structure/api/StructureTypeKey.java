package de.ambertation.wover.structure.api;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Wraps a registered {@link StructureType} for a custom {@link Structure} subclass together with the
 * {@link StructureFactory} used to instantiate it.
 * <p>
 * Instances are created with {@link StructureManager#registerType(Identifier, StructureFactory)} or
 * {@link StructureManager#registerType(Identifier, StructureFactory, MapCodec)}, or implicitly by
 * {@link StructureManager#structure(Identifier, StructureFactory)}.
 *
 * @param <S> The {@link Structure} type
 */
public class StructureTypeKey<S extends Structure> {
    /**
     * The registered {@link StructureType} for {@link S}.
     */
    @NotNull
    public final StructureType<S> type;

    /**
     * Creates a new instance of a custom {@link Structure} subclass from its {@link Structure.StructureSettings}.
     * This is typically a method reference to the {@link Structure} subclass's constructor.
     *
     * @param <S> The {@link Structure} type
     */
    public interface StructureFactory<S extends Structure> {
        /**
         * Creates a new {@link Structure} instance.
         *
         * @param structureSettings The base {@link Structure.StructureSettings} for the new instance
         * @return The new {@link Structure} instance
         */
        S create(Structure.StructureSettings structureSettings);
    }

    /**
     * For internal use only. Use {@link StructureManager#registerType(Identifier, StructureFactory, MapCodec)}
     * to explicitly register and create a key. Or {@link StructureManager#structure(Identifier, StructureFactory, MapCodec)}
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

    /**
     * The {@link StructureFactory} used to create new instances of {@link S}.
     */
    @NotNull
    public final StructureFactory<S> structureFactory;
}
