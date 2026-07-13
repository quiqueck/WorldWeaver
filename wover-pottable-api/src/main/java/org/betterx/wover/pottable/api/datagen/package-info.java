/**
 * Ready-made datagen providers that serialize the {@link org.betterx.wover.pottable.api.PottablePlant}/
 * {@link org.betterx.wover.pottable.api.PottableSoil} registry entries for every block carrying the matching
 * {@link org.betterx.wover.pottable.api.trait.PottablePlantBlockTrait}/
 * {@link org.betterx.wover.pottable.api.trait.PottableSoilBlockTrait}.
 * <p>
 * Add {@code globalPack.addRegistryProvider(WoverPottablePlantRegistryProvider::new)} and
 * {@code globalPack.addRegistryProvider(WoverPottableSoilRegistryProvider::new)} to your datagen entrypoint -
 * no custom provider class is needed for the common case of "every block with the trait becomes an entry".
 *
 * @see org.betterx.wover.pottable.api.trait
 */
package org.betterx.wover.pottable.api.datagen;
