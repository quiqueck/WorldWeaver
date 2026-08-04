/**
 * Java API for creating and registering {@link net.minecraft.world.level.levelgen.structure.StructureSet}s
 * — the registry that actually places {@link net.minecraft.world.level.levelgen.structure.Structure}s in
 * the world (a {@code Structure} that is not referenced by any {@code StructureSet} never generates).
 * <p>
 * {@link de.ambertation.wover.structure.api.sets.StructureSetManager} creates a
 * {@link de.ambertation.wover.structure.api.sets.StructureSetKey} — a wrapper around a
 * {@link net.minecraft.resources.ResourceKey} for the set. Call
 * {@link de.ambertation.wover.structure.api.sets.StructureSetKey#bootstrap(net.minecraft.data.worldgen.BootstrapContext)}
 * to obtain a {@link de.ambertation.wover.structure.api.sets.StructureSetBuilder} and register the set:
 * <pre class="java"> public static final StructureSetKey MY_SET = StructureKeys.set(MyMod.C.id("my_set"));
 *
 * // in a datagen BootstrapContext&lt;StructureSet&gt;:
 * MY_SET.bootstrap(context)
 *       .addStructure(MY_STRUCTURE)
 *       .randomPlacement(32, 8)
 *       .register();</pre>
 * {@link de.ambertation.wover.structure.api.sets.StructureSetManager#bootstrap(de.ambertation.wover.structure.api.StructureKey, net.minecraft.data.worldgen.BootstrapContext)}
 * is a shorthand for the common case of a set containing a single structure, using the structure's own id
 * as the set's id.
 */
package de.ambertation.wover.structure.api.sets;
