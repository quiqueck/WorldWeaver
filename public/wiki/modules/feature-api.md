# wover-feature-api

Java API for vanilla `Feature`s and `PlacedFeature`s. `wover-feature-api` does not invent a new
datapack format for either registry — both stay plain vanilla JSON. On top of that it ships a set of extra
`Feature` types (block columns with per-height transforms, pillars, random-template placement, ...) and extra
`PlacementModifier`s that vanilla has no equivalent of, plus a fluent builder API so mod code doesn't have to
hand-write feature JSON or fight vanilla's `Feature`/`PlacementModifier` types.

> **Minecraft 26.3 reshaped this whole area.** `ConfiguredFeature<FC, F>` no longer exists: `Feature` is a
> plain, non-generic interface that carries its own configuration and its own `MapCodec`. The datapack
> registry `minecraft:configured_feature` became `minecraft:feature`
> (`data/<ns>/worldgen/configured_feature/` → `data/<ns>/worldgen/feature/`), the built-in registry of
> feature *types* moved from `BuiltInRegistries.FEATURE` to `BuiltInRegistries.FEATURE_TYPE` and now holds
> `MapCodec`s, and `FeatureConfiguration` is gone entirely. WorldWeaver followed that collapse through its own
> API — see the rename table at the end of this page.

- **Gradle artifact:** `de.ambertation:worldweaver` (single artifact; this module ships inside it as the Fabric mod `wover-feature`)
- **Depends on:** `wover-core-api`, `wover-event-api`, `wover-surface-api`, `wover-block-api`,
  `wover-structure-api`, `wover-math-api`
- **Java packages:**
  - `de.ambertation.wover.feature.api` — `FeatureManager` (registering custom feature *types*),
    `FeatureUtils` (placing a `Feature` outside the normal worldgen pipeline), `Features` (the `MapCodec`s of
    WoVer's own feature types)
  - `de.ambertation.wover.feature.api.configured` — `FeatureKey`/`FeatureContentManager`
  - `de.ambertation.wover.feature.api.configured.configurators` — one builder interface per feature
    type (vanilla and WoVer's own)
  - `de.ambertation.wover.feature.api.features` — WoVer's custom `Feature` implementations. Each one carries its
    own configuration; there is no separate `...config` package any more.
  - `de.ambertation.wover.feature.api.placed` — `PlacedFeatureKey`/`BoundPlacedFeatureKey`/
    `PlacedFeatureManager`/`FeaturePlacementBuilder`
  - `de.ambertation.wover.feature.api.placed.modifiers` — WoVer's custom `PlacementModifier`s

## For Datapack Developers

### `Feature` — `data/<namespace>/worldgen/feature/<path>.json`

Unmodified vanilla format: a `type` (e.g. `minecraft:simple_block` or one of WoVer's own types below) plus
that type's own fields. Because 26.3 dispatches feature types on a `MapCodec`, those fields sit **next to**
`type` — the `"config": { ... }` wrapper that 26.1 and earlier used is gone.

```json
{
  "type": "wover:pillar",
  "allowed_placement": { "type": "minecraft:matching_blocks", "blocks": ["minecraft:air", "minecraft:water"] },
  "direction": "up",
  "max_height": { "type": "minecraft:uniform", "min_inclusive": 4, "max_inclusive": 8 },
  "min_height": 3,
  "state": { "type": "minecraft:simple_state_provider", "state": { "Name": "minecraft:lapis_block" } },
  "transform": "size_increase"
}
```

### `PlacedFeature` — `data/<namespace>/worldgen/placed_feature/<path>.json`

Also unmodified vanilla format: a `feature` (`Feature` reference or inline definition) plus a
`placement` list of `PlacementModifier`s applied in order.

```json
{
  "feature": "mymod:my_feature",
  "placement": [
    { "type": "minecraft:count", "count": 4 },
    { "type": "minecraft:in_square" },
    { "type": "minecraft:heightmap", "heightmap": "MOTION_BLOCKING" },
    { "type": "minecraft:biome" }
  ]
}
```

Note that 26.3 renamed vanilla's random-offset modifier from `minecraft:random_offset` (with `xz_spread`
/`y_spread`) to `minecraft:offset` (with independent `x`/`y`/`z` providers). Existing data has to be updated.

### WoVer's `Feature` types

Registered in `de.ambertation.wover.feature.api.Features` / built by the matching configurator in
`de.ambertation.wover.feature.api.configured.configurators`. All are exposed as inline builders on
`FeatureContentManager` (see below) and configured through `FeatureContentManager.<name>(id)`.

| `type` id | `Feature` class | Configurator | Purpose |
|---|---|---|---|
| `wover:place_block` | `PlaceFacingBlockFeature` (base class `PlaceBlockFeature`) | `FacingBlock` | Places one weighted block/state that has a `FACING` property, trying each direction from a list until one survives. |
| `wover:mark_postprocessing` | `MarkPostProcessingFeature` | — (use `new MarkPostProcessingFeature()` directly) | Marks the chunk section at the position for postprocessing; no in-world change. |
| `wover:sequence` | `SequenceFeature` | `AsSequence` | Places a non-empty list of `PlacedFeature`s in order at the same origin. |
| `wover:condition` | `ConditionFeature` | via `FeatureContentManager.withFeature(id, new ConditionFeature(...))` / `WithFeature` | Tests a `PlacementModifier`/`filter` at the origin; places `filter_pass` if it matches, otherwise the optional `filter_fail`. |
| `wover:pillar` | `PillarFeature` | `AsPillar` | Grows a column of blocks in a direction until `allowedPlacement` rejects a position, transforming each block's state by height (see `KnownTransformers` below). |
| `wover:template` | `TemplateFeature` | `WithTemplates` | Picks a random `.nbt` template (weighted) and places it with a random rotation/mirror; templates are loaded from `data/<namespace>/structure/<path>.nbt`. |
| `wover:random_patch` | `RandomPatchFeature` | `RandomPatch`/`WeightedBlockPatch` *(deprecated)* | Vendored replacement for the `minecraft:random_patch` type vanilla removed in 26.1. Scatters an inner `PlacedFeature` `tries` times within `xz_spread`/`y_spread`. Scheduled for removal — express the same thing with `FeaturePlacementBuilder#scatter(...)` instead. |

Besides these, `de.ambertation.wover.feature.api.configured.configurators` also has builders for **plain
vanilla** feature types, so you rarely need to write vanilla feature JSON by hand either: `ForSimpleBlock`
(`minecraft:simple_block`), `AsOre` (`minecraft:ore`), `AsRandomSelect`/`AsMultiPlaceRandomSelect`
(`minecraft:random_selector`), `AsBlockColumn` (`minecraft:block_column`), `NetherForrestVegetation`
(`minecraft:random_neighbor_spread` — 26.3 deleted `minecraft:nether_forest_vegetation`, see the note below),
and `WithFeature` for any `Feature` not covered by a dedicated builder.

`PillarFeature.KnownTransformers` (the `transform` field of `wover:pillar`) — how the block state
changes with height along the pillar:

| Value | Effect |
|---|---|
| `size_decrease` | Sets `BlockProperties#SIZE` from `7` at the base down to `0` at the tip (needs a block with a `SIZE` property). |
| `size_increase` | Sets `BlockProperties#SIZE` from `0` at the base up to `7` at the tip. |
| `bottom_grow` | Sets `BlockProperties#BOTTOM` to `true` only at the tip. |
| `bottom` | Sets `BlockProperties#BOTTOM` to `true` only at the base. |
| `triple_shape_fill` | Sets `BlockProperties#TRIPLE_SHAPE` to `BOTTOM`/`MIDDLE`/`TOP` depending on position, and additionally requires the block above the pillar's tip to fail `allowed_placement` before the pillar is placed at all. |

Every transformer writes a WoVer block property, so `wover:pillar` only works with blocks that actually
declare it — pointing one at a plain vanilla block compiles and generates fine but throws when it is placed.

### WoVer's `PlacementModifier` types

All registered under the `wover:` namespace (a few are additionally registered under BCLib's legacy
namespace for datapack-compatibility, marked *legacy* below) in
`de.ambertation.wover.feature.impl.placed.modifiers.PlacementModifiersImpl`, and each has a matching builder
method on `FeaturePlacementBuilder` (see below) so you normally never write these by hand.

| `type` id | Class | Purpose |
|---|---|---|
| `wover:all` *(legacy)* | `All` | Emits one position for every block in a 16x16 area, offset (0..15, 0, 0..15) from the input. |
| `wover:debug` *(legacy)* | `Debug` | Re-emits the input unchanged, logging it with an optional `{}` caption. |
| `wover:every_layer` | `EveryLayer` | Emits one position on top of (or underneath) every terrain layer found at the xz-coordinate, from a `max`/`min` height range. |
| `wover:extend` *(legacy)* | `Extend` | Emits a column of positions from the input extending a random length in one `Direction`. |
| `wover:extend_xyz` | `ExtendXYZ` | Emits positions in a disc/square around the input with density falling off from `center_density` to `border_density`, optionally also extending vertically (`height_propagation`: `none`/`box_up`/`box_down`/`sphere_up`/`sphere_down`/`spikes_up`/`spikes_down`). |
| `wover:solid_in_dir` *(legacy)* | `FindInDirection` | Searches one or more directions for the first block matching `surface_predicate` within `dist` blocks. |
| `wover:in_biome` *(legacy)* | `InBiome` | Accepts the position only if its biome id is (or, if `negate`, is not) in a fixed list. |
| `wover:is` *(legacy)* | `Is` | Tests a `BlockPredicate` at the input position, optionally offset. |
| `wover:is_basin` *(legacy)* | `IsBasin` | Accepts the position if the block below and all four horizontal neighbors match a predicate (and, optionally, the block above matches a separate top predicate). |
| `wover:is_next_to` *(legacy)* | `IsNextTo` | Accepts the position if any of its four horizontal neighbors (at an optional offset) match a predicate. |
| `wover:for_all` *(legacy)* | `Merge` | Runs a non-empty list of sub-modifiers against the same input and concatenates their results. |
| `wover:noise_filter` *(legacy)* | `NoiseFilter` | Accepts the position if a named `NormalNoise` sampled at the (scaled) position falls in `(min_noise_level, max_noise_level)`. |
| `wover:offset` *(legacy)* | `Offset` | Moves the input by a fixed `Vec3i`. |
| `wover:offset_provider` | `OffsetProvider` | Moves the input by a randomized `Vec3iProvider` offset. |
| `wover:stencil` *(legacy)* | `Stencil` | Emits positions from a fixed 16x16 boolean pattern (WoVer's own hand-drawn default, or a custom one), optionally thinned by a `1-in-n` chance. |

## For Mod Developers

### Registering a `Feature` + `PlacedFeature` (data generator, recommended)

Both registries are datapack registries bootstrapped through a `BootstrapContext`, same as vanilla worldgen.
Declare `FeatureKey`/`PlacedFeatureKey` fields once, then bootstrap them from a
`WoverRegistryContentProvider` (see the `wover-datagen-api` wiki page):

```java
public class MyFeatureKeys {
    public static final FeatureKey<AsPillar> BASALT_PILLAR =
            FeatureContentManager.pillar(MyMod.C.id("basalt_pillar"));

    public static final PlacedFeatureKey PLACED_BASALT_PILLAR =
            PlacedFeatureManager.createKey(MyMod.C.id("basalt_pillar"));
}

public class MyFeatureContentProvider extends WoverRegistryContentProvider<Feature> {
    public MyFeatureContentProvider(ModCore modCore) {
        super(modCore, "Features", Registries.FEATURE);
    }

    @Override
    protected void bootstrap(BootstrapContext<Feature> context) {
        MyFeatureKeys.BASALT_PILLAR
                .bootstrap(context)
                .transformer(PillarFeature.KnownTransformers.SIZE_DECREASE)
                .direction(Direction.DOWN)
                .blockState(Blocks.SMOOTH_BASALT)
                .maxHeight(BiasedToBottomInt.of(4, 11))
                .allowedPlacement(BlockPredicate.ONLY_IN_AIR_PREDICATE)
                .register();
    }
}

public class PlacedFeaturesProvider extends WoverRegistryContentProvider<PlacedFeature> {
    public PlacedFeaturesProvider(ModCore modCore) {
        super(modCore, "Placed Features", Registries.PLACED_FEATURE);
    }

    @Override
    protected void bootstrap(BootstrapContext<PlacedFeature> context) {
        MyFeatureKeys.PLACED_BASALT_PILLAR
                .place(context, MyFeatureKeys.BASALT_PILLAR)
                .count(2)
                .squarePlacement()
                .onlyInBiome()
                .findSolidCeil(12)
                .register();
    }
}
```

Register both providers as regular (single-registry) providers from your datagen entrypoint:

```java
public class MyModDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        globalPack.addRegistryProvider(MyFeatureContentProvider::new);
        globalPack.addRegistryProvider(PlacedFeaturesProvider::new);
    }

    @Override
    protected ModCore modCore() {
        return MyMod.C;
    }
}
```

### `FeatureContentManager`

Creates a `FeatureKey<B>` for a specific feature type, where `B` is the matching
configurator interface: `simple(id)` → `ForSimpleBlock`, `ore(id)` → `AsOre`, `pillar(id)` → `AsPillar`,
`sequence(id)` → `AsSequence`, `blockColumn(id)` → `AsBlockColumn`, `templates(id)` → `WithTemplates`,
`randomPatch(id)` → `RandomPatch`, `netherForrestVegetation(id)`/`bonemealNetherForrest(id)` →
`NetherForrestVegetation`, `facingBlock(id)` → `FacingBlock`, `randomBlockPatch(id)`/`bonemeal(id)` →
`WeightedBlockPatch`, `randomBlock(id)` → `WeightedBlock`, `randomFeature(id)` → `AsRandomSelect`,
`multiPlaceRandomFeature(id)` → `AsMultiPlaceRandomSelect`, and `withFeature(id, feature)` → `WithFeature<F>`
for any custom `Feature`. Every key's `bootstrap(BootstrapContext)` returns the configurator to fill in and
`.register()` at the end.

`FeatureKey#getHolder(...)` (overloaded for `HolderGetter`, `BootstrapContext`, `RegistryAccess`)
resolves the registered `Holder<Feature>`, and `placeInWorld(...)` overloads place the
feature directly (outside the worldgen pipeline), forwarding to `FeatureUtils.placeInWorld`.

If you need to bootstrap outside of datagen, `FeatureContentManager.BOOTSTRAP_FEATURES` is the
runtime event (see the test mod for `PlacedFeatureManager.BOOTSTRAP_PLACED_FEATURES`, used the same way).

### `PlacedFeatureManager` and `FeaturePlacementBuilder`

`PlacedFeatureManager.createKey(location)` creates a plain `PlacedFeatureKey`, which you then call
`.place(context, <feature reference>)` on — accepting a `ResourceKey<Feature>`, a
`Holder<Feature>`, or a `FeatureKey<?>` directly.
`PlacedFeatureManager.createKey(location, featureKey)` (or the no-location overload, which reuses
the feature's own id) instead returns a `BoundPlacedFeatureKey`, permanently bound to one
`Feature`, whose `place(context)` needs no feature argument — this is what the example above uses.

Both return a `FeaturePlacementBuilder`, a fluent wrapper around vanilla's `PlacementModifier` list covering
every vanilla modifier (`count`, `squarePlacement`, `onlyInBiome`, `heightmap`/`heightmapWorldSurface`/
`heightmapOceanFloor`, `spreadHorizontal`/`spreadVertical`, `noiseBasedCount`, `randomHeight*`, ...) plus one
method per WoVer modifier from the table above (`stencil`/`stencilOneIn4`, `onEveryLayer*`/`underEveryLayer*`,
`extendXZ`/`extendXYZ`/`extendZigZagXZ`/`extendZigZagXYZ`, `is`/`isNextTo`/`isOn`/`isUnder`/`isEmptyAndOn*`/
`isEmptyAndUnder*`/`isFullShape`, `inBasinOf`/`inOpenBasinOf`, `findSolidFloor`/`findSolidCeil`/
`findSolidSurface`, `onWalls`, `noiseIn`/`noiseAbove`/`noiseBelow`, `offset(...)`, `debug(caption)`,
`scatter(...)`), plus convenience bundles that reproduce common vanilla/BetterNether/BCLib placement patterns
in one call: `vanillaNetherGround(countPerLayer)`, `betterNetherGround(count)`, `betterNetherCeiling(count)`,
`betterNetherOnWall(count)`, `betterNetherInWall(count)`. Finish with `.register()` (adds the `PlacedFeature`
to the active `BootstrapContext`) or `.directHolder()` (an unregistered, inline holder — useful for
`RandomPatch#featureToPlace(Holder)` or `AsSequence#add(Holder)`).

`PlacedFeatureKey#setDecoration(GenerationStep.Decoration)` sets which `GenerationStep.Decoration` the
feature is associated with when later added to a biome (default `VEGETAL_DECORATION` if never called);
`getDecoration()` reads it back — this is what
`de.ambertation.wover.biome.api.modification.BiomeModification.Builder#addFeature(BasePlacedFeatureKey)` (see
the `wover-biome-api` wiki page) uses to pick the right `GenerationStep.Decoration` bucket automatically when
you hand it a key instead of an explicit `(Decoration, Holder)` pair.

### Inline (anonymous) features

Many `PlacedFeature`s only exist to wrap one throwaway `Feature` (e.g. the feature placed inside a
`RandomPatch`). Two ways to build these without registering a separate `Feature`:

- From a `FeatureConfigurator`: configure it, then call `.inlinePlace()` instead of `.register()` to jump
  straight into a `FeaturePlacementBuilder`, or `.directHolder()` for an unregistered `Holder` you can pass
  elsewhere (e.g. `RandomPatch#featureToPlace(Holder)`).
- From a `PlacedFeatureKey`, call `.inlineConfiguration(context)` to get a
  `FeatureContentManager.InlineBuilder` (methods matching `FeatureContentManager`'s factory methods,
  e.g. `.simple()`, `.pillar()`, `.randomPatch()`, `.withFeature(feature)`) and chain straight through
  `.inlinePlace()` into placement, as the test mod's `inline_feature_all` does:

```java
INLINE_FEATURE_ALL.inlineConfiguration(context)
                  .simple()
                  .block(Blocks.COAL_BLOCK)
                  .inlinePlace()
                  .squarePlacement()
                  .scatter(96, 7, 3, BlockPredicate.ONLY_IN_AIR_PREDICATE)
                  .register();
```

(`scatter(tries, xzSpread, ySpread, filter)` is the placement-modifier replacement for the removed
`minecraft:random_patch` feature type: `count` + `offset` + an optional `block_predicate_filter` + `biome`.)

### Registering a custom `Feature` type

`FeatureManager.register(location, codec)` / `register(ResourceKey<MapCodec<? extends Feature>>, codec)`
register a new feature type in `BuiltInRegistries.FEATURE_TYPE`
(`FeatureManager.createKey(location)` creates the key without registering). What you register is the type's
`MapCodec`, not an instance: in 26.3 a `Feature` *is* a configured value, so it implements
`net.minecraft.world.level.levelgen.feature.Feature` with

- `MapCodec<? extends Feature> codec()` — the codec you registered,
- `boolean place(WorldGenLevel, ChunkGenerator, RandomSource, BlockPos)` — no more `FeaturePlaceContext`, and
- `Stream<Holder<Feature>> getSubFeatures()` — **override this if your feature wraps other features.** It
  defaults to an empty stream, and vanilla walks it to build the feature graph (a biome's bone-meal features,
  for instance), so a wrapper that does not override it silently hides everything nested inside it.

See `de.ambertation.wover.feature.api.features` for WoVer's own examples (`PlaceBlockFeature` is a useful base
class for "place one of several weighted blocks" features). If your feature should also be placeable outside
the normal worldgen pipeline (e.g. a sapling growing into a tree on bonemeal), implement
`de.ambertation.wover.feature.api.features.GrowableFeature` — `FeatureUtils.placeInWorld(feature, level, pos,
random, unchanged)` (also reachable via `FeatureKey#placeInWorld(...)`) will call `grow(...)` instead of the
normal `place(...)` whenever `unchanged` is `false`, unwrapping one level of `wover:random_patch` first if
present.

### 26.1 → 26.3 renames

Driven by the removal of `ConfiguredFeature`; every one of these old names referred to a type or concept that
no longer exists.

| 26.1 | 26.3 |
|---|---|
| `ConfiguredFeatureKey<B>` | `FeatureKey<B>` |
| `ConfiguredFeatureManager` | `FeatureContentManager` |
| `ConfiguredFeatureManager.BOOTSTRAP_CONFIGURED_FEATURES` | `FeatureContentManager.BOOTSTRAP_FEATURES` |
| `ConfiguredFeatureManager.configuration(id, feature)` | `FeatureContentManager.withFeature(id, feature)` |
| `PlacedConfiguredFeatureKey` | `BoundPlacedFeatureKey` |
| `WithConfiguration<F, FC>` (and its `configuration(FC)`) | `WithFeature<F>` (configuration removed) |
| `FeatureConfigurator<FC, F>`, `BasePatch<FC, F, W>`, `BaseWeightedBlock<FC, F, W>` | `FeatureConfigurator`, `BasePatch<W>`, `BaseWeightedBlock<W>` |
| `PlaceFacingBlockConfig` | `PlaceFacingBlockFeature` |
| `PillarFeatureConfig` (incl. `KnownTransformers`) | merged into `PillarFeature` |
| `SequenceFeatureConfig`, `ConditionFeatureConfig`, `TemplateFeatureConfig` | merged into `SequenceFeature`, `ConditionFeature`, `TemplateFeature` |
| `PlaceBlockFeatureConfig` | merged into `PlaceBlockFeature` |
| `GrowableFeature<FC>` with `grow(level, pos, random, config)` | `GrowableFeature` with `grow(level, pos, random)` |
| `FeatureManager.register(id, Feature<FC>)` | `FeatureManager.register(id, MapCodec<F>)` |
| `Features.*` of type `Feature<FC>` | `Features.*` of type `MapCodec<F>` |

## Real usage

See `wover-feature-api/src/testmod` (`TestModWoverFeature` — key declarations, inline configuration/
placement at runtime via `BOOTSTRAP_FEATURES`/`BOOTSTRAP_PLACED_FEATURES`, and hooking placed
features into biomes through `wover-biome-api`'s `BiomeModification`), `src/testmodDatagen`
(`FeatureContentProvider`, `PlacedFeatureProvider`, `ModificationProvider`,
`TestModWoverFeatureDatagen`), and the `FeatureGameTest` gametests for complete, compiling examples of
everything above.
