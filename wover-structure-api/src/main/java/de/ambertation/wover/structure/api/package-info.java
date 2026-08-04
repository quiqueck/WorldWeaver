/**
 * Java API for creating and registering vanilla {@link net.minecraft.world.level.levelgen.structure.Structure}s.
 * <p>
 * {@link de.ambertation.wover.structure.api.StructureManager} (and its shorter alias,
 * {@link de.ambertation.wover.structure.api.StructureKeys}) is the main entry point. It creates a
 * {@link de.ambertation.wover.structure.api.StructureKey} — a wrapper around a {@link net.minecraft.resources.ResourceKey}
 * that also remembers the structure's biome tag, generation step and {@link net.minecraft.world.level.levelgen.structure.StructureType}.
 * Call {@link de.ambertation.wover.structure.api.StructureKey#bootstrap(net.minecraft.data.worldgen.BootstrapContext)}
 * to obtain a builder (see {@link de.ambertation.wover.structure.api.builders}) and register the structure:
 * <pre class="java"> public static final StructureKey.Jigsaw MY_STRUCTURE =
 *         StructureKeys.jigsaw(MyMod.C.id("my_structure"))
 *                      .biomeTag(TagManager.BIOMES.makeStructureTag(MyMod.C, "my_structure"))
 *                      .step(GenerationStep.Decoration.SURFACE_STRUCTURES);
 *
 * // in a datagen BootstrapContext&lt;Structure&gt;:
 * MY_STRUCTURE.bootstrap(context)
 *             .startPool(MY_POOL)
 *             .register();</pre>
 * A {@link de.ambertation.wover.structure.api.StructureKey} only defines the {@code Structure} itself. A
 * {@link net.minecraft.world.level.levelgen.structure.Structure} additionally needs a
 * {@link de.ambertation.wover.structure.api.sets.StructureSetKey} (see {@link de.ambertation.wover.structure.api.sets})
 * to actually place it in the world.
 * <p>
 * This package also provides {@link de.ambertation.wover.structure.api.StructureNBT}, a lightweight helper to
 * load and place {@code .nbt} templates directly (bypassing the vanilla
 * {@link net.minecraft.world.level.levelgen.structure.Structure}/{@link net.minecraft.world.level.levelgen.structure.StructurePiece}
 * pipeline), and {@link de.ambertation.wover.structure.api.StructureUtils} with small helpers used when
 * implementing a custom {@link net.minecraft.world.level.levelgen.structure.Structure}.
 *
 * @see de.ambertation.wover.structure.api.builders
 * @see de.ambertation.wover.structure.api.pools
 * @see de.ambertation.wover.structure.api.processors
 * @see de.ambertation.wover.structure.api.sets
 * @see de.ambertation.wover.structure.api.structures
 */
package de.ambertation.wover.structure.api;
