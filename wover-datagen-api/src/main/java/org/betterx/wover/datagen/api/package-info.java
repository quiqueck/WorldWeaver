/**
 * Provides helper classes to simplify the Data Generation process.
 * <p>
 * The classes are an extension of the Fabric Data Generator API. In order to start, you
 * need to set up a gradle task for Data Generation. This is done by adding the following
 * to your <b>build.gradle</b> file:
 * <pre class="gradle">sourceSets {
 *   main {
 *     resources {
 *       srcDirs += ['src/main/generated']
 *     }
 *   }
 * }
 *
 * loom {
 *   runs {
 *     datagenClient {
 *       inherit client
 *       name "Data Generation"
 *       vmArg "-Dfabric-api.datagen"
 *       vmArg "-Dfabric-api.datagen.output-dir=${file("src/main/generated")}"
 *       vmArg "-Dfabric-api.datagen.strict-validation"
 *       vmArg "-Dfabric-api.datagen.modid=betternether"
 *
 *       runDir "build/datagen"
 *     }
 *   }
 * }</pre>
 * <p>
 * You can then create a class that extends {@link org.betterx.wover.datagen.api.WoverDataGenEntryPoint}
 * to provide the Datasets for your mod. A simple Implementation from Wover's Surface API is shown below
 * <pre class="java"> public class WoverSurfaceDatagen extends WoverDataGenEntryPoint {
 *     &#64;Override
 *     protected void onInitializeProviders(PackBuilder globalPack) {
 *         globalPack.addRegistryProvider(NoiseRegistryProvider::new);
 *     }
 *
 *     &#64;Override
 *     protected ModCore modCore() {
 *         return WoverSurface.C;
 *     }
 * }</pre>
 * The class {@code NoiseRegistryProvider}  is a class that extends
 * {@link org.betterx.wover.datagen.api.WoverFullRegistryProvider}. A {@link org.betterx.wover.datagen.api.WoverRegistryProvider}
 * allows you to capture all or a specific subset of the entries in a {@link net.minecraft.core.Registry} and
 * serialize them to a JSON file. You can find more information about this in
 * {@link org.betterx.wover.datagen.api.WoverRegistryProvider}'s documentation.
 * <p>
 * Finally, you need to tell fabric about the new entry point. This is done by adding the following to your
 * <b>fabric.mod.json</b> file:
 * <pre class="json"> "entrypoints": {
 *   /&#42;...&#42;/
 *   "fabric-datagen": ["org.betterx.wover.surface.datagen.WoverSurfaceDatagen"]
 * }</pre>
 *
 * @see org.betterx.wover.datagen.api.WoverDataGenEntryPoint
 * @see org.betterx.wover.datagen.api.WoverRegistryProvider
 * @see <a href="https://fabricmc.net/wiki/tutorial:datagen_setup">Fabric Data Generation</a>
 */
package org.betterx.wover.datagen.api;