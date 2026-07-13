/**
 * Ready-made {@link org.betterx.wover.sets.api.blocks.SlotFactory} implementations, one class per common
 * block/item "role" a {@link org.betterx.wover.sets.api.blocks.BlockSet} can build: planks, log/bark, slab,
 * stairs, wall, pillar, fence, fence gate, door, trapdoor, button, pressure plate, ladder, sign, hanging sign,
 * chest, barrel, bookshelf, composter, crafting table, and boat/chest boat.
 * <p>
 * Every class here follows the same pattern: build the block/item with the correct vanilla constructor, attach
 * the matching {@link org.betterx.wover.block.api.trait.BlockTraits}/{@link org.betterx.wover.item.api.trait.ItemTraits}
 * type trait, provide a client-side model via {@link org.betterx.wover.block.api.client.model.ModelTraitLibrary},
 * and provide an auto-generated recipe via {@link org.betterx.wover.recipe.api.RecipeTraitLibrary} matching the
 * corresponding vanilla recipe. Instances of these classes are what
 * {@link org.betterx.wover.sets.api.blocks.slots.WoodSlots}/{@link org.betterx.wover.sets.api.blocks.slots.StoneSlots}
 * pre-assemble into ready-made {@link org.betterx.wover.sets.api.blocks.SlotMap}s; most classes also expose extra
 * constructors (e.g. {@link org.betterx.wover.sets.api.blocks.types.Slab#Slab(org.betterx.wover.sets.api.blocks.SlotType,
 * org.betterx.wover.sets.api.blocks.SlotType)}) for building variants under a different {@link org.betterx.wover.sets.api.blocks.SlotType}
 * or cut from a different base block, used for the brick/tiles/weathered/... stone-family variants.
 *
 * @see org.betterx.wover.sets.api.blocks
 */
package org.betterx.wover.sets.api.blocks.types;
