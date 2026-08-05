/**
 * {@link de.ambertation.wover.structure.api.structures.nbt.RandomNbtStructure} — a
 * {@link net.minecraft.world.level.levelgen.structure.Structure} that, at generation time, picks one of
 * several {@code .nbt} templates ({@link de.ambertation.wover.structure.api.structures.nbt.RandomNbtStructureElement})
 * at random (weighted) and places it using a
 * {@link de.ambertation.wover.structure.api.structures.StructurePlacement} strategy — together with the
 * {@link net.minecraft.world.level.levelgen.structure.StructurePiece} implementation
 * ({@link de.ambertation.wover.structure.api.structures.nbt.RandomNbtStructurePiece}) it generates.
 * <p>
 * Build one via {@link de.ambertation.wover.structure.api.builders.RandomNbtBuilder}, created through
 * {@link de.ambertation.wover.structure.api.StructureManager#randomNbt(net.minecraft.resources.Identifier)}.
 */
package de.ambertation.wover.structure.api.structures.nbt;
