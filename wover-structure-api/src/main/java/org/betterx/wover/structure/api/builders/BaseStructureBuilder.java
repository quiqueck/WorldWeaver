package org.betterx.wover.structure.api.builders;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;

/**
 * Base interface for all Structure builders. A builder is created by calling
 * {@link org.betterx.wover.structure.api.StructureKey#bootstrap(net.minecraft.data.worldgen.BootstrapContext)}
 * and lets you configure a {@link Structure} before either {@link #register() registering} it with the
 * active {@link net.minecraft.data.worldgen.BootstrapContext}, or wrapping it in an unnamed
 * {@link #directHolder() Holder}.
 *
 * @param <S> The {@link Structure} type
 * @param <R> The concrete builder type, for chaining
 */
public interface BaseStructureBuilder<S extends Structure, R extends BaseStructureBuilder<S, R>> {
    /**
     * Registers the {@link Structure} with the currently active
     * {@link net.minecraft.data.worldgen.BootstrapContext}.
     * <p>
     * Will fail if either the key of this Feature or the {@link net.minecraft.data.worldgen.BootstrapContext}
     * are null.
     *
     * @return the holder
     */
    Holder<Structure> register();

    /**
     * Creates an unnamed {@link Holder} for this {@link BaseStructureBuilder}.
     * <p>
     * This method is usefull, if you want to create an anonymous {@link Structure}
     * that is directly inlined
     *
     * @return the holder
     */
    Holder<Structure> directHolder();

    /**
     * Sets the {@link TerrainAdjustment} used to blend the structure's pieces with the surrounding terrain.
     * Defaults to {@link TerrainAdjustment#NONE} if never called.
     *
     * @param adjustment The terrain adjustment to use
     * @return This builder instance, for chaining
     */
    R adjustment(TerrainAdjustment adjustment);
}
