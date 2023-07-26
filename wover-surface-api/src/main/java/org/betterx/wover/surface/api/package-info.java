/**
 * Surface Rule Registry and Builder
 * <p>
 * The Surface Rule Registry {@link org.betterx.wover.surface.api.SurfaceRuleRegistry} is used to load Surface Rules for a Biome from a
 * Datapck or register new Surface Rules dynamically from a Mod. However, Mods should
 * prefer to use a Data Generator and create a Datapack instead of dynamically adding SurfaceRules at runtime.
 * <p>
 * Adding Surface Rules from a DataGenerator could look something likes this (if you use the WoVer DataGenerator-API):
 * <pre class="java"> public class SurfaceRuleProvider extends WoverRegistryContentProvider&lt;AssignedSurfaceRule&gt; {
 *    public static final ResourceKey&lt;AssignedSurfaceRule&gt; TEST_MEADOW
 *            = /&#42;&#42; defined somewhere &#42;&#42;/
 *
 *    public SurfaceRuleProvider(ModCore modCore) {
 *        super(
 *                modCore,
 *                "Additional Surface Rules",
 *                SurfaceRuleRegistry.SURFACE_RULES_REGISTRY
 *        );
 *    }
 *
 *    &#64;Override
 *    protected void bootstrap(BootstapContext&lt;AssignedSurfaceRule&gt; ctx) {
 *        SurfaceRuleBuilder
 *                .start()
 *                .biome(Biomes.MEADOW)
 *                .surface(Blocks.LIME_CONCRETE.defaultBlockState())
 *                .steep(Blocks.ORANGE_CONCRETE.defaultBlockState(), 3)
 *                .register(ctx, TEST_MEADOW);
 *    }
 * }</pre>
 * This will automatically inject the new surface rules to all dimensions that contain the {@code Biomes.MEADOW} biome.
 * A biome can have multiple rules, all will be collected and only applied to the corresponding biome. Rules are sorted by
 * their priority, so the rule with the highest priority will be applied first.
 * <p>
 * If you want to do the same at runtime (again, you should opt for the Data Generation whenever possible),
 * you can do achieve this by subscribing to the {@link SurfaceRuleRegistry#BOOTSTRAP_SURFACE_RULE_REGISTRY} event:
 * <pre class="java"> ModCore C = /&#42;&#42; ... &#42;&#42;/;
 * if (!ModCore.isDatagen()) {
 *     var TEST_SAVANA = SurfaceRuleRegistry.createKey(C.id("test-savana"));
 *     SurfaceRuleRegistry.BOOTSTRAP_SURFACE_RULE_REGISTRY.subscribe(ctx -> {
 *         SurfaceRuleBuilder
 *             .start()
 *             .biome(Biomes.SAVANNA)
 *             .chancedFloor(
 *                 Blocks.RED_TERRACOTTA.defaultBlockState(),
 *                 Blocks.RED_CONCRETE.defaultBlockState()
 *             )
 *             .register(ctx, TEST_SAVANA);
 *     });
 * }</pre>
 * The event will be fired whenever a SurfaceRule Registry is loaded/initialized form a Datapack.
 * See {@link SurfaceRuleRegistry#BOOTSTRAP_SURFACE_RULE_REGISTRY} for more information.
 * <p>
 * If you prefere, you can also manually create josn files in a datapack that will get
 * picked up when the SurfaceRule Registry is loaded. The json files should be placed in
 * {@code data/<namespace>/wover/worldgen/surface_rules/<surface_rule_name>.json} and should look like this:
 * <pre class="json"> {
 *   "biome": "minecraft:plains",
 *   "priority": 1001,
 *   "ruleSource": {
 *     "type": "minecraft:condition",
 *     "if_true": {
 *       "type": "minecraft:stone_depth",
 *       "add_surface_depth": false,
 *       "offset": 0,
 *       "secondary_depth_range": 0,
 *       "surface_type": "floor"
 *     },
 *     "then_run": {
 *       "type": "minecraft:block",
 *       "result_state": {
 *         "Name": "minecraft:acacia_planks"
 *       }
 *     }
 *   }
 * }</pre>
 * <p><p>
 * The Surface Rule Builder {@link org.betterx.wover.surface.api.SurfaceRuleBuilder} directly interfaces with the Registry
 * and allows you to create Surface Rules from a simplified API.
 */
package org.betterx.wover.surface.api;