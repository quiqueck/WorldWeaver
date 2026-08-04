/**
 * The core "material set" API: builds a themed group of blocks (and items) sharing a
 * {@link net.minecraft.world.level.block.state.properties.BlockSetType}, such as a wood or stone type with its
 * full family of slabs/stairs/walls/doors/... variants.
 *
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link de.ambertation.wover.sets.api.blocks.BlockSet} - the builder; subclassed per set, configured by
 *   overriding {@code createDefaultDefinitions()}</li>
 *   <li>{@link de.ambertation.wover.sets.api.blocks.WoodenBlockSet} - a {@code BlockSet} specialization that also
 *   derives a {@link net.minecraft.world.level.block.state.properties.WoodType} and maintains a shared logs tag</li>
 *   <li>{@link de.ambertation.wover.sets.api.blocks.SlotType} - identifies a role a block can play in a set (slab,
 *   stairs, log, chest, ...)</li>
 *   <li>{@link de.ambertation.wover.sets.api.blocks.SlotFactory} / {@link de.ambertation.wover.sets.api.blocks.SlotMap} -
 *   builds the block(s) for one slot; an ordered, slot-keyed collection of factories</li>
 *   <li>{@link de.ambertation.wover.sets.api.blocks.SlotFromDefinition} / {@link de.ambertation.wover.sets.api.blocks.WoodenSlotFromDefinition} -
 *   base classes building a slot's block via {@link de.ambertation.wover.block.api.BlockDefinition}</li>
 *   <li>{@link de.ambertation.wover.sets.api.blocks.ItemSlotFromDefinition} - variant for slots that register an
 *   item (e.g. boats) instead of, or in addition to, a block</li>
 *   <li>{@link de.ambertation.wover.sets.api.blocks.SlotFromBlock} - fills a slot with an already-existing block</li>
 * </ul>
 *
 * <p>The concrete block-type factories built on top of this package (planks, slab, stairs, door, chest, ...) live
 * in {@code de.ambertation.wover.sets.api.blocks.types}; ready-made {@link de.ambertation.wover.sets.api.blocks.SlotMap}s
 * for a full wood or stone set live in {@code de.ambertation.wover.sets.api.blocks.slots}.
 *
 * @see de.ambertation.wover.sets.api.blocks.types
 * @see de.ambertation.wover.sets.api.blocks.slots
 */
package de.ambertation.wover.sets.api.blocks;
