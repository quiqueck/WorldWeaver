/**
 * API to modify certain aspects of biomes.
 * <p>
 * Modifications are managed by a Datapack backed Registry and can be generated at runtime or in
 * a Data Generator.  However, Mods should prefer to use a Data Generator and create a Datapack
 * instead of dynamically adding {@link org.betterx.wover.biome.api.modification.BiomeModification}s
 * at runtime.
 * <p>
 * Adding Modifications from a DataGenerator could look something likes this (if you use the WoVer DataGenerator-API):
 * <pre class="java"> public class ModificationProvider extends WoverRegistryContentProvider&lt;BiomeModification&gt; {
 *     public ModificationProvider(
 *             ModCore modCore
 *     ) {
 *         super(modCore, "Biome Modifications", BiomeModificationRegistry.BIOME_MODIFICATION_REGISTRY);
 *     }
 *
 *     &#64;Override
 *     protected void bootstrap(BootstapContext&lt;BiomeModification&gt; context) {
 *         var features = context.lookup(Registries.PLACED_FEATURE);
 *
 *         BiomeModification
 *                 .build(modCore.id("test_modification"))
 *                 .inBiomes(Biomes.BEACH, Biomes.MEADOW)
 *                 .addFeature(
 *                         GenerationStep.Decoration.VEGETAL_DECORATION,
 *                         features.getOrThrow(NetherPlacements.SMALL_BASALT_COLUMNS)
 *                 )
 *                 .register(context);
 *     }
 * }</pre>
 * This will add the {@code NetherPlacements.SMALL_BASALT_COLUMNS} feature to the {@code Biomes.BEACH} and
 * {@code Biomes.MEADOW} biomes in the {@code VEGETAL_DECORATION} step.
 * You can choose different predicates to determine which Biomes will be modified.
 * See {@link org.betterx.wover.biome.api.modification.predicates.BiomePredicate} for more options.
 * <p>
 * If you want to do the same at runtime (again, you should opt for the Data Generation whenever possible),
 * you can do achieve this by subscribing to the
 * {@link org.betterx.wover.biome.api.modification.BiomeModificationRegistry#BOOTSTRAP_BIOME_MODIFICATION_REGISTRY} event:
 * <pre class="java"> ModCore C = /&#42;&#42; ... &#42;&#42;/;
 * if (!ModCore.isDatagen()) {
 *     BiomeModificationRegistry.BOOTSTRAP_BIOME_MODIFICATION_REGISTRY.subscribe(ctx -> {
 *         var features = context.lookup(Registries.PLACED_FEATURE);
 *
 *         BiomeModification
 *                 .build(modCore.id("test_modification"))
 *                 .inBiomes(Biomes.BEACH, Biomes.MEADOW)
 *                 .addFeature(
 *                         GenerationStep.Decoration.VEGETAL_DECORATION,
 *                         features.getOrThrow(NetherPlacements.SMALL_BASALT_COLUMNS)
 *                 )
 *                 .register(context);
 *     });
 * }</pre>
 * The event will be fired whenever a Biome Modification Registry is loaded/initialized form a Datapack.
 * See {@link org.betterx.wover.biome.api.modification.BiomeModificationRegistry#BOOTSTRAP_BIOME_MODIFICATION_REGISTRY}
 * for more information.
 * <p>
 * You can also manually create json files in a datapack that will get
 * picked up when the BiomeModification Registry is loaded. The json files should be placed in
 * {@code data/<namespace>/wover/worldgen/biome_modifications/<modification_name>.json} and
 * should look like this:
 * <pre class="json"> {
 *   "features": [
 *     [],
 *     [],
 *     [],
 *     [],
 *     [],
 *     [],
 *     [],
 *     [],
 *     [],
 *     [
 *       "minecraft:small_basalt_columns"
 *     ]
 *   ],
 *   "predicate": {
 *     "type": "wover:or",
 *     "predicates": [
 *       {
 *         "type": "wover:is_biome",
 *         "biome_key": "minecraft:beach"
 *       },
 *       {
 *         "type": "wover:is_biome",
 *         "biome_key": "minecraft:plains"
 *       }
 *     ]
 *   }
 * }</pre>
 */
package org.betterx.wover.biome.api.modification;