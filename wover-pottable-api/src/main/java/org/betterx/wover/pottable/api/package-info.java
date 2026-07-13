/**
 * Java API for a datapack-driven registry of plants and soils that can be combined inside a flower pot.
 * <p>
 * {@link org.betterx.wover.pottable.api.PottablePlantRegistry} and
 * {@link org.betterx.wover.pottable.api.PottableSoilRegistry} are the entry points, mirroring each other: each
 * registers a {@code data/<namespace>/wover/pottable_plant/<name>.json} (respectively
 * {@code data/<namespace>/wover/pottable_soil/<name>.json}) entry, wrapping a plain
 * {@link org.betterx.wover.pottable.api.PottablePlant} (respectively
 * {@link org.betterx.wover.pottable.api.PottableSoil}) that just references the block and, for plants, the
 * optional {@link net.minecraft.tags.TagKey} of soils it may be potted on:
 * <pre class="java"> // in a datagen BootstrapContext&lt;PottablePlant&gt;:
 * PottablePlantRegistry.register(context, PottablePlantRegistry.createKey(MyMod.C.id("my_sapling")), MyBlocks.MY_SAPLING);
 *
 * // in a datagen BootstrapContext&lt;PottableSoil&gt;:
 * PottableSoilRegistry.register(context, PottableSoilRegistry.createKey(MyMod.C.id("my_soil")), MyBlocks.MY_SOIL);</pre>
 * Most mods won't call these methods directly; see {@link org.betterx.wover.pottable.api.trait} and
 * {@link org.betterx.wover.pottable.api.datagen} for the block-trait-driven, no-boilerplate way of registering
 * entries for every block that should be pottable.
 *
 * @see org.betterx.wover.pottable.api.trait
 * @see org.betterx.wover.pottable.api.datagen
 */
package org.betterx.wover.pottable.api;
