/**
 * Registers a full set of tools and armor built from a shared material tier, one item and one recipe per piece.
 *
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link org.betterx.wover.complex.api.equipment.EquipmentSet} - subclassed to define a set; call
 *   {@code add(ToolSlot)}/{@code add(ArmorSlot)} for every piece the set should contain</li>
 *   <li>{@link org.betterx.wover.complex.api.equipment.ToolTier} / {@link org.betterx.wover.complex.api.equipment.ToolTiers} -
 *   per-material tool configuration (attack stats, mining tag, smithing template); ready-made vanilla tiers</li>
 *   <li>{@link org.betterx.wover.complex.api.equipment.ArmorTier} / {@link org.betterx.wover.complex.api.equipment.ArmorTiers} -
 *   per-material armor configuration (durability, smithing template); ready-made vanilla tiers</li>
 *   <li>{@link org.betterx.wover.complex.api.equipment.ToolSlot} / {@link org.betterx.wover.complex.api.equipment.ArmorSlot} -
 *   the pieces an {@code EquipmentSet} can register</li>
 *   <li>{@link org.betterx.wover.complex.api.equipment.ToolDescription} / {@link org.betterx.wover.complex.api.equipment.ArmorDescription} -
 *   the built-and-registered result of a single piece</li>
 * </ul>
 *
 * <p>Recipes are generated automatically for every registered piece: a plain crafting recipe by default, or a
 * smithing-transform recipe if the tier's {@code ToolValues}/{@code ArmorValues} for that slot supply a
 * {@code SmithingTemplateItem} and the set was constructed with a template base set to upgrade from.
 */
package org.betterx.wover.complex.api.equipment;
