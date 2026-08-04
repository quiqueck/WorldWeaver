/**
 * Builders used to configure and register a {@link net.minecraft.world.level.levelgen.structure.Structure},
 * obtained from a {@link de.ambertation.wover.structure.api.StructureKey} via
 * {@link de.ambertation.wover.structure.api.StructureKey#bootstrap(net.minecraft.data.worldgen.BootstrapContext)}.
 * <p>
 * {@link de.ambertation.wover.structure.api.builders.BaseStructureBuilder} is the common base interface
 * ({@link de.ambertation.wover.structure.api.builders.StructureBuilder} for plain custom structures,
 * {@link de.ambertation.wover.structure.api.builders.JigsawBuilder} for
 * {@link net.minecraft.world.level.levelgen.structure.structures.JigsawStructure}s, and
 * {@link de.ambertation.wover.structure.api.builders.RandomNbtBuilder} for
 * {@link de.ambertation.wover.structure.api.structures.nbt.RandomNbtStructure}s).
 */
package de.ambertation.wover.structure.api.builders;
