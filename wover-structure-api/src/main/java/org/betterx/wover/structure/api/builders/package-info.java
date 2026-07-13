/**
 * Builders used to configure and register a {@link net.minecraft.world.level.levelgen.structure.Structure},
 * obtained from a {@link org.betterx.wover.structure.api.StructureKey} via
 * {@link org.betterx.wover.structure.api.StructureKey#bootstrap(net.minecraft.data.worldgen.BootstrapContext)}.
 * <p>
 * {@link org.betterx.wover.structure.api.builders.BaseStructureBuilder} is the common base interface
 * ({@link org.betterx.wover.structure.api.builders.StructureBuilder} for plain custom structures,
 * {@link org.betterx.wover.structure.api.builders.JigsawBuilder} for
 * {@link net.minecraft.world.level.levelgen.structure.structures.JigsawStructure}s, and
 * {@link org.betterx.wover.structure.api.builders.RandomNbtBuilder} for
 * {@link org.betterx.wover.structure.api.structures.nbt.RandomNbtStructure}s).
 */
package org.betterx.wover.structure.api.builders;
