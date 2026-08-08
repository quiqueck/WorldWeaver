/**
 * Build and reference {@link net.minecraft.world.level.levelgen.feature.Feature}s of the
 * {@code minecraft:feature} datapack registry.
 * <p>
 * The package is still called {@code configured} for historical reasons: up to 26.1 these types built
 * {@code ConfiguredFeature}s. 26.3 deleted that class and folded it into
 * {@link net.minecraft.world.level.levelgen.feature.Feature}. The classes were renamed
 * ({@code ConfiguredFeatureKey} -&gt; {@code FeatureKey},
 * {@code ConfiguredFeatureManager} -&gt; {@code FeatureContentManager}) but the package was deliberately left
 * in place, so that the port is a rename and not also a package move.
 */
package de.ambertation.wover.feature.api.configured;
