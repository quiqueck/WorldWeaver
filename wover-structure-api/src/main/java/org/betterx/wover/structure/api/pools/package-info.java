/**
 * Java API for creating and registering {@link net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool}s
 * (jigsaw template pools).
 * <p>
 * {@link org.betterx.wover.structure.api.pools.StructurePoolManager} creates a
 * {@link org.betterx.wover.structure.api.pools.StructurePoolKey} — a wrapper around a
 * {@link net.minecraft.resources.ResourceKey} for the pool. Call
 * {@link org.betterx.wover.structure.api.pools.StructurePoolKey#bootstrap(net.minecraft.data.worldgen.BootstrapContext)}
 * to obtain a {@link org.betterx.wover.structure.api.pools.StructurePoolBuilder} and register the pool:
 * <pre class="java"> public static final StructurePoolKey MY_POOL = StructureKeys.pool(MyMod.C.id("my_pool"));
 *
 * // in a datagen BootstrapContext&lt;StructureTemplatePool&gt;:
 * MY_POOL.bootstrap(context)
 *        .startSingle(MyMod.C.id("house"))
 *        .endElement()
 *        .register();</pre>
 * {@link org.betterx.wover.structure.api.pools.StructurePoolElementTypeManager} is only needed if you
 * implement a custom {@link net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement}
 * subclass.
 */
package org.betterx.wover.structure.api.pools;
