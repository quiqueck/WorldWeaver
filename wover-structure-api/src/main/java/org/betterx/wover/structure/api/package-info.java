/**
 * Java API for creating and registering vanilla {@link net.minecraft.world.level.levelgen.structure.Structure}s.
 * <p>
 * {@link org.betterx.wover.structure.api.StructureManager} (and its shorter alias,
 * {@link org.betterx.wover.structure.api.StructureKeys}) is the main entry point. It creates a
 * {@link org.betterx.wover.structure.api.StructureKey} — a wrapper around a {@link net.minecraft.resources.ResourceKey}
 * that also remembers the structure's biome tag, generation step and {@link net.minecraft.world.level.levelgen.structure.StructureType}.
 * Call {@link org.betterx.wover.structure.api.StructureKey#bootstrap(net.minecraft.data.worldgen.BootstrapContext)}
 * to obtain a builder (see {@link org.betterx.wover.structure.api.builders}) and register the structure:
 * <pre class="java"> public static final StructureKey.Jigsaw MY_STRUCTURE =
 *         StructureKeys.jigsaw(MyMod.C.id("my_structure"))
 *                      .biomeTag(TagManager.BIOMES.makeStructureTag(MyMod.C, "my_structure"))
 *                      .step(GenerationStep.Decoration.SURFACE_STRUCTURES);
 *
 * // in a datagen BootstrapContext&lt;Structure&gt;:
 * MY_STRUCTURE.bootstrap(context)
 *             .startPool(MY_POOL)
 *             .register();</pre>
 * A {@link org.betterx.wover.structure.api.StructureKey} only defines the {@code Structure} itself. A
 * {@link net.minecraft.world.level.levelgen.structure.Structure} additionally needs a
 * {@link org.betterx.wover.structure.api.sets.StructureSetKey} (see {@link org.betterx.wover.structure.api.sets})
 * to actually place it in the world.
 * <p>
 * This package also provides {@link org.betterx.wover.structure.api.StructureNBT}, a lightweight helper to
 * load and place {@code .nbt} templates directly (bypassing the vanilla
 * {@link net.minecraft.world.level.levelgen.structure.Structure}/{@link net.minecraft.world.level.levelgen.structure.pieces.StructurePiece}
 * pipeline), and {@link org.betterx.wover.structure.api.StructureUtils} with small helpers used when
 * implementing a custom {@link net.minecraft.world.level.levelgen.structure.Structure}.
 *
 * @see org.betterx.wover.structure.api.builders
 * @see org.betterx.wover.structure.api.pools
 * @see org.betterx.wover.structure.api.processors
 * @see org.betterx.wover.structure.api.sets
 * @see org.betterx.wover.structure.api.structures
 */
package org.betterx.wover.structure.api;
