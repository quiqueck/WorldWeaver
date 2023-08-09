package org.betterx.wover.structure.api.builders;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.Structure;

public interface StructureBuilder {
    /**
     * Registers the {@link Structure} with the currently active
     * {@link net.minecraft.data.worldgen.BootstapContext}.
     * <p>
     * Will fail if either the key of this Feature or the {@link net.minecraft.data.worldgen.BootstapContext}
     * are null.
     *
     * @return the holder
     */
    Holder<Structure> register();

    /**
     * Creates an unnamed {@link Holder} for this {@link StructureBuilder}.
     * <p>
     * This method is usefull, if you want to create an anonymous {@link Structure}
     * that is directly inlined
     *
     * @return the holder
     */
    Holder<Structure> directHolder();
}
