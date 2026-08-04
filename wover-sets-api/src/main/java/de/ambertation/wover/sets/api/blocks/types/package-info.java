/**
 * Ready-made {@link de.ambertation.wover.sets.api.blocks.SlotFactory} implementations, one class per common
 * block/item "role" a {@link de.ambertation.wover.sets.api.blocks.BlockSet} can build: planks, log/bark, slab,
 * stairs, wall, pillar, fence, fence gate, door, trapdoor, button, pressure plate, ladder, sign, hanging sign,
 * chest, barrel, bookshelf, composter, crafting table, and boat/chest boat.
 * <p>
 * Every class here follows the same pattern: build the block/item with the correct vanilla constructor, attach
 * the matching {@link de.ambertation.wover.block.api.trait.BlockTraits}/{@link de.ambertation.wover.item.api.trait.ItemTraits}
 * type trait, provide a model via {@link de.ambertation.wover.block.api.model.ModelTraitLibrary},
 * and provide an auto-generated recipe via {@link de.ambertation.wover.recipe.api.RecipeTraitLibrary} matching the
 * corresponding vanilla recipe. Instances of these classes are what
 * {@link de.ambertation.wover.sets.api.blocks.slots.WoodSlots}/{@link de.ambertation.wover.sets.api.blocks.slots.StoneSlots}
 * pre-assemble into ready-made {@link de.ambertation.wover.sets.api.blocks.SlotMap}s; most classes also expose extra
 * constructors (e.g. {@link de.ambertation.wover.sets.api.blocks.types.Slab#Slab(de.ambertation.wover.sets.api.blocks.SlotType,
 * de.ambertation.wover.sets.api.blocks.SlotType)}) for building variants under a different {@link de.ambertation.wover.sets.api.blocks.SlotType}
 * or cut from a different base block, used for the brick/tiles/weathered/... stone-family variants.
 *
 * @see de.ambertation.wover.sets.api.blocks
 */
package de.ambertation.wover.sets.api.blocks.types;
