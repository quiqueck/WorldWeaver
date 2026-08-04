/**
 * Ready-made datagen providers that serialize the {@link de.ambertation.wover.pottable.api.PottablePlant}/
 * {@link de.ambertation.wover.pottable.api.PottableSoil} registry entries for every block carrying the matching
 * {@link de.ambertation.wover.pottable.api.trait.PottablePlantBlockTrait}/
 * {@link de.ambertation.wover.pottable.api.trait.PottableSoilBlockTrait}.
 * <p>
 * Add {@code globalPack.addRegistryProvider(WoverPottablePlantRegistryProvider::new)} and
 * {@code globalPack.addRegistryProvider(WoverPottableSoilRegistryProvider::new)} to your datagen entrypoint -
 * no custom provider class is needed for the common case of "every block with the trait becomes an entry".
 *
 * @see de.ambertation.wover.pottable.api.trait
 */
package de.ambertation.wover.pottable.api.datagen;
