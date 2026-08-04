/**
 * Ready-made {@link de.ambertation.wover.sets.api.blocks.SlotFromDefinition} constants for a full wood or stone
 * material family, for use with {@link de.ambertation.wover.sets.api.blocks.SlotMap#of}.
 *
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link de.ambertation.wover.sets.api.blocks.slots.WoodSlots} - every slot
 *   {@link de.ambertation.wover.sets.api.blocks.WoodenBlockSet#createDefaultDefinitions()} builds by default</li>
 *   <li>{@link de.ambertation.wover.sets.api.blocks.slots.StoneSlots} - source/slab/stairs/wall/pillar/button/plate
 *   slots, plus the tiles/brick/weathered/cracked/chiseled/polished stone-family variants</li>
 * </ul>
 *
 * <p>A set typically starts from one of these ({@code createDefaultDefinitions()} on
 * {@link de.ambertation.wover.sets.api.blocks.WoodenBlockSet} already does, for {@code WoodSlots}) and
 * adds/replaces/removes individual slots as needed; see this module's test mod ({@code TestStoneSet}) for a set
 * built directly from a hand-picked subset of {@code StoneSlots}.
 */
package de.ambertation.wover.sets.api.blocks.slots;
