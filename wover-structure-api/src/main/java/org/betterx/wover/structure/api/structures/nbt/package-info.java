/**
 * {@link org.betterx.wover.structure.api.structures.nbt.RandomNbtStructure} — a
 * {@link net.minecraft.world.level.levelgen.structure.Structure} that, at generation time, picks one of
 * several {@code .nbt} templates ({@link org.betterx.wover.structure.api.structures.nbt.RandomNbtStructureElement})
 * at random (weighted) and places it using a
 * {@link org.betterx.wover.structure.api.structures.StructurePlacement} strategy — together with the
 * {@link net.minecraft.world.level.levelgen.structure.pieces.StructurePiece} implementation
 * ({@link org.betterx.wover.structure.api.structures.nbt.RandomNbtStructurePiece}) it generates.
 * <p>
 * Build one via {@link org.betterx.wover.structure.api.builders.RandomNbtBuilder}, created through
 * {@link org.betterx.wover.structure.api.StructureManager#randomNbt(net.minecraft.resources.ResourceLocation)}.
 */
package org.betterx.wover.structure.api.structures.nbt;
