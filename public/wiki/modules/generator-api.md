# wover-generator-api

Ties every other WoVer module together into two ready-made, fully datapack-configurable dimension generators
(`wover:betterx` Nether/End replacements) plus the building blocks (`WoverBiomeSource`, `WoverBiomePicker`,
`BiomeMap`) needed to build a completely custom, tag-driven `BiomeSource` of your own. This is the last module
in the dependency chain — it is what actually *places* the Biomes registered through `wover-biome-api` into a
dimension, using the climate parameters/tags/presets/surface rules contributed by the modules it depends on.

- **Gradle artifact:** `org.betterx:wover-generator-api`
- **Depends on:** `wover-core-api`, `wover-event-api`, `wover-surface-api`, `wover-biome-api`,
  `wover-preset-api`, `wover-ui-api`, `wover-tag-api`
- **Java packages:**
  - `org.betterx.wover.generator.api.biomesource` — `WoverBiomeSource` (base class), `WoverBiomePicker`,
    `WoverBiomeBuilder`, `WoverBiomeData`, `BiomeSourceManager`
  - `org.betterx.wover.generator.api.biomesource.end` — `WoverEndConfig`, `BiomeDecider` (extension point
    for the End biome source)
  - `org.betterx.wover.generator.api.biomesource.nether` — `WoverNetherConfig`
  - `org.betterx.wover.generator.api.chunkgenerator` — `ChunkGeneratorManager`
  - `org.betterx.wover.generator.api.map` — `BiomeMap`/`BiomeChunk`/`MapBuilderFunction`, the spatial biome
    distribution used by `WoverBiomeSource`
  - `org.betterx.wover.generator.api.preset` — `WorldPresets`, WoVer's built-in `WorldPreset`s and helpers to
    build a WoVer Nether/End `LevelStem`
  - `org.betterx.wover.generator.api.client.biomesource.client` *(client-only)* — `BiomeSourceConfigPanel`,
    `BiomeSourceWithConfigScreen`, the world-creation-screen settings panel extension point

## For Datapack Developers

### The `wover:betterx` `chunk_generator` type

WoVer registers one `chunk_generator` type, `wover:betterx`, used for both of its dimensions. It takes the
exact same fields as vanilla's `minecraft:noise` generator:

```json
{
  "type": "wover:betterx",
  "biome_source": { "...": "..." },
  "settings": "minecraft:end"
}
```

- `biome_source` (required) — any `biome_source`, typically one of the two types below.
- `settings` (required) — a `worldgen/noise_settings` reference, exactly like vanilla.

### `biome_source` types

Both of WoVer's `biome_source` types share the same shape: a `seed` (stable, filled in automatically by the
game) and an optional `config` object.

**`wover:nether_biome_source`** — places every Biome carrying the `minecraft:is_nether` tag, distributed by
a `WoverNetherConfig`:

```json
{
  "type": "wover:nether_biome_source",
  "seed": 0,
  "config": {
    "map_type": "hex",
    "biome_size": 256,
    "biome_size_vertical": 86,
    "use_vertical_biomes": true
  }
}
```

- `map_type` (optional, one of `vanilla`/`square`/`hex`, defaults to `hex`) — the spatial distribution
  algorithm. `vanilla` and `hex` both use a hex-grid map; `square` uses a square grid (BCLib 1.17 behavior).
- `biome_size` (optional, `1`-`8192`, defaults to `256`) — the horizontal biome size.
- `biome_size_vertical` (optional, `1`-`8192`, defaults to `86`) — the biome size used when biomes are also
  stacked vertically.
- `use_vertical_biomes` (optional, defaults to `true`) — whether biomes are additionally layered vertically
  once the world is taller than `1.5 * biome_size_vertical`.

**`wover:end_biome_source`** — places Biomes into five rings (center island, highlands, midlands, small
islands, barrens) based on `c:is_end_center`/`c:is_end_highland`/`c:is_end_midland`/`c:is_small_end_island`/
`c:is_end_barrens`/`minecraft:is_end`, distributed by a `WoverEndConfig`:

```json
{
  "type": "wover:end_biome_source",
  "seed": 0,
  "config": {
    "map_type": "hex",
    "generator_version": "vanilla",
    "with_void_biomes": true,
    "inner_void_radius_squared": 1048576,
    "center_biomes_size": 256,
    "void_biomes_size": 256,
    "land_biomes_size": 256,
    "barrens_biomes_size": 256
  }
}
```

- `map_type` (optional, one of `vanilla`/`square`/`hex`, defaults to `hex`) — same meaning as above, applied
  per ring.
- `generator_version` (optional, one of `vanilla`/`paulevs`, defaults to `vanilla`) — the algorithm used to
  decide which ring a position falls into (erosion-based, matching vanilla's own End, vs. BCLib's `paulevs`
  algorithm).
- `with_void_biomes` (optional, defaults to `true`) — whether small End islands generate at all outside the
  inner void radius.
- `inner_void_radius_squared` (optional, defaults to `1048576`, i.e. radius `1024`) — the squared radius (in
  blocks) of the center island ring.
- `center_biomes_size`/`void_biomes_size`/`land_biomes_size`/`barrens_biomes_size` (optional, `1`-`8192`,
  default to `256`) — the biome size of each ring.

This is a real (datagen-produced) `data/wover/worldgen/world_preset/normal.json`, showing both types wired
into a full preset next to a vanilla overworld:

```json
{
  "dimensions": {
    "minecraft:overworld": {
      "type": "minecraft:overworld",
      "generator": {
        "type": "minecraft:noise",
        "biome_source": { "type": "minecraft:multi_noise", "preset": "minecraft:overworld" },
        "settings": "minecraft:overworld"
      }
    },
    "minecraft:the_end": {
      "type": "minecraft:the_end",
      "generator": {
        "type": "wover:betterx",
        "biome_source": {
          "type": "wover:end_biome_source",
          "config": { "map_type": "hex", "generator_version": "vanilla", "with_void_biomes": true,
                       "inner_void_radius_squared": 1048576, "center_biomes_size": 256,
                       "void_biomes_size": 256, "land_biomes_size": 256, "barrens_biomes_size": 256 },
          "seed": 0
        },
        "settings": "minecraft:end"
      }
    },
    "minecraft:the_nether": {
      "type": "minecraft:the_nether",
      "generator": {
        "type": "wover:betterx",
        "biome_source": {
          "type": "wover:nether_biome_source",
          "config": { "map_type": "hex", "biome_size": 256, "biome_size_vertical": 86,
                       "use_vertical_biomes": true },
          "seed": 0
        },
        "settings": "minecraft:nether"
      }
    }
  }
}
```

Both `biome_source` types are also registered under the `bclib:` namespace (`bclib:nether_biome_source`/
`bclib:end_biome_source`) when `wover-core-api`'s legacy support is enabled, so existing BCLib-based worlds
keep loading.

### `BiomeData` — the `wover:wover_data` type

`wover-biome-api`'s `BiomeData` (`data/<namespace>/wover/worldgen/biome_data/<path>.json`) only carries fog
density and placement tags/climate parameters. `wover-generator-api` registers a second `BiomeData` type,
`wover:wover_data`, adding the fields `WoverBiomePicker` needs to place sub-biomes and edge biomes:

```json
{
  "type": "wover:wover_data",
  "biome": "wover-generator-testmod:nether_test_biome",
  "fogDensity": 8.0,
  "genChance": 2.0,
  "generation_data": {
    "intended_placement": "minecraft:is_nether",
    "parameter_points": [
      { "temperature": 2.0, "humidity": 0.0, "continentalness": 0.0, "erosion": 0.0,
        "depth": 0.0, "weirdness": 0.0, "offset": 0.0 }
    ]
  }
}
```

- `terrainHeight` (float, optional, defaults to `0.1`) — a terrain height hint.
- `genChance` (float, optional, defaults to `1.0`) — the relative weight this Biome (or, if `parent` is set,
  this sub-biome) is picked with.
- `edgeSize` (int, optional, defaults to `0`) — the size of the `edge` biome border.
- `vertical` (bool, optional, defaults to `false`) — whether the `edge` biome is a vertical (height-based)
  transition instead of a horizontal one.
- `edge` (`ResourceKey<Biome>`, optional) — a Biome that generates at the border of this Biome.
- `parent` (`ResourceKey<Biome>`, optional) — marks this Biome as a sub-biome (alternative) of another
  Biome: it is only ever picked in place of its parent (weighted by `genChance` against the parent and its
  other sub-biomes), never as a top-level pick on its own. This is how e.g. a rare variant of a common Nether
  biome is expressed. A real sub-biome entry (only overriding what differs from the defaults):

  ```json
  {
    "type": "wover:wover_data",
    "parent": "wover-generator-testmod:nether_main_biome",
    "biome": "wover-generator-testmod:nether_sub_biome",
    "generation_data": { "intended_placement": "minecraft:is_nether" }
  }
  ```

You will normally never hand-write these — they are produced by `WoverBiomeBuilder` through datagen (see
below).

### `data/<namespace>/config/biome_config.json` — excluding Biomes

Read (and merged across every namespace) by `BiomeSourceManager`/`ChunkGeneratorManager`'s underlying
`DatapackConfigs`, this optional file lets a datapack or mod exclude specific Biomes from WoVer's Nether/End
biome sources, or keep a Biome from being registered with Fabric API's `NetherBiomes`/`TheEndBiomes`:

```json
{
  "exclude": {
    "minecraft:is_nether": ["minecraft:crimson_forest"],
    "*:is_end": ["minecraft:unknown_biome"]
  },
  "no_fabric_register": ["minecraft:*", "c:*"]
}
```

- `exclude` (optional) — maps a Biome tag id to a list of Biome ids (wildcards like `minecraft:*` allowed)
  to exclude from that tag when a `WoverBiomeSource` places Biomes. The special keys `*:is_end`/`*:is_nether`
  exclude from every End/Nether placement tag at once (`c:is_end_center`, `c:is_end_highland`, ...,
  `minecraft:is_end` for `*:is_end`; just `minecraft:is_nether` for `*:is_nether`), which is otherwise
  awkward since a Biome can carry more than one of them.
- `no_fabric_register` (optional) — Biome ids (wildcards allowed) that should never be registered with
  Fabric API's biome-placement APIs, even if another mod (like TerraBlender) relies on them being present
  there.

## For Mod Developers

### Building a Nether/End replacement dimension

The pieces from earlier modules come together here: `wover-biome-api` defines *what* a Biome looks like,
`wover-tag-api`/`wover-preset-api` define *where* it can go and *which world preset* uses it, and this module
does the actual placement. Declare Biomes with `WoverBiomeBuilder` instead of plain `BiomeManager` when you
want edge/sub-biome placement on top of the usual `BiomeBuilder` fields:

```java
public static final BiomeKey<WoverBiomeBuilder.WoverBiome> NETHER_MAIN_BIOME =
        WoverBiomeBuilder.biomeKey(C.id("nether_main_biome"));
public static final BiomeKey<WoverBiomeBuilder.WoverBiome> NETHER_SUB_BIOME =
        WoverBiomeBuilder.biomeKey(C.id("nether_sub_biome"));
```

```java
public class BiomeProvider extends WoverBiomeProvider {
    public BiomeProvider(ModCore modCore) { super(modCore); }

    @Override
    protected void bootstrap(BiomeBootstrapContext context) {
        MyBiomeKeys.NETHER_MAIN_BIOME
                .bootstrap(context)
                .isNetherBiome()
                .addNetherClimate(2.0f, 0.0f)
                .surface(Blocks.WHITE_CONCRETE)
                .genChance(2.0f)
                .register();

        MyBiomeKeys.NETHER_SUB_BIOME
                .bootstrap(context)
                .isNetherBiome()
                .surface(Blocks.GRAY_CONCRETE)
                .parent(MyBiomeKeys.NETHER_MAIN_BIOME)   // only picked in place of NETHER_MAIN_BIOME
                .register();
    }
}
```

`WoverBiomeBuilder.biomeKey(location)` (new Biome) and `WoverBiomeBuilder.wrappedKey(existingBiomeKey)`
(attach WoVer placement data to an existing/vanilla Biome) mirror `BiomeManager.vanilla(...)`/`wrapped(...)`
from `wover-biome-api`, but return builders that also expose `edge(...)`/`parent(...)`/`terrainHeight(...)`/
`genChance(...)`/`edgeSize(...)`/`vertical(...)`, plus the `isNetherBiome()`/`isEndHighlandBiome()`/
`isEndMidlandBiome(parent)`/`isEndCenterIslandBiome()`/`isEndBarrensBiome(parent)`/`isEndSmallIslandBiome()`
shortcuts (the `Wrapped` variant) that set the right `intendedPlacement` tag from `wover-biome-api` in one
call. Any Biome carrying the matching tag (`minecraft:is_nether`, `c:is_end_highland`, ...) — including ones
from another mod that never touched this API — is automatically picked up the next time the dimension's
`WoverBiomeSource` rebuilds its pickers.

To suggest WoVer's default world preset (the one built from `WorldPresets.WOVER_WORLD`, using
`WoverNetherConfig.DEFAULT`/`WoverEndConfig.DEFAULT`) as the default in the world-creation screen:

```java
@Override
public void onInitialize() {
    WorldPresetManager.suggestDefault(WorldPresets.WOVER_WORLD, 2000);
}
```

### Building a fully custom world preset

To ship your own preset with different Nether/End settings (e.g. larger biomes, or the vanilla-style
generator instead of WoVer's), build it from a `WoverWorldPresetProvider` (see `wover-preset-api`) using
`WorldPresets.makeWoverNetherStem(...)`/`makeWoverEndStem(...)`:

```java
public class WorldPresetProvider extends WoverWorldPresetProvider {
    public WorldPresetProvider(ModCore modCore) { super(modCore, "My Presets"); }

    @Override
    protected void bootstrap(WorldPresetBootstrapContext ctx) {
        ctx.register(MyPresets.MY_WORLD, WorldPresetManager.of(Map.of(
                LevelStem.OVERWORLD, ctx.overworldStem,
                LevelStem.NETHER, WorldPresets.makeWoverNetherStem(ctx.netherContext, WoverNetherConfig.MINECRAFT_18_LARGE),
                LevelStem.END, WorldPresets.makeWoverEndStem(ctx.endContext, WoverEndConfig.MINECRAFT_20_LARGE)
        )));
    }
}
```

`WorldPresetBootstrapContext` (from `wover-preset-api`) hands you ready-made `netherContext`/`endContext`
(`StemContext`s carrying the builtin `DimensionType` and default `NoiseGeneratorSettings`) so you only need
to supply the `WoverNetherConfig`/`WoverEndConfig`. Every constant on `WoverNetherConfig`/`WoverEndConfig`
(`VANILLA`, `MINECRAFT_17`, `MINECRAFT_18`, `MINECRAFT_18_LARGE`, `MINECRAFT_18_AMPLIFIED`, `MINECRAFT_20*`
for the End) reproduces the exact behavior of a past BCLib/WoVer version; build a
`new WoverNetherConfig(mapType, biomeSize, biomeSizeVertical, useVerticalBiomes)`/
`new WoverEndConfig(mapType, generatorType, withVoidBiomes, innerVoidRadiusSquared, centerSize, voidSize,
landSize, barrensSize)` for anything else.

### Extending Biome placement with `BiomeDecider`

`BiomeDecider` (End only) is the hook `BetterEnd`-style mods use to inject a placement algorithm that
overrides the ring-based default (e.g. structure-shaped biome regions) without replacing
`WoverEndBiomeSource` outright:

```java
BiomeDecider.registerDecider(MyMod.C.id("my_decider"), new MyBiomeDecider(myPredicate));
```

Each `WoverEndBiomeSource` instance calls `createInstance(biomeSource)` on every registered decider that
`canProvideFor(source)`; the resulting instances get a chance to `suggestType(...)` a different placement tag
and then, if `canProvideBiome(suggestedType)`, to `provideBiome(...)` a Biome directly from their own
`BiomeMap` — see the class-level javadoc on `BiomeDecider` for the full callback sequence.
`registerHighPriorityDecider(...)` registers a decider that runs before the normally-registered ones.

### A fully custom `BiomeSource`

If tag-based placement is not enough, extend `WoverBiomeSource` directly instead of using
`WoverNetherBiomeSource`/`WoverEndBiomeSource`. You get `ReloadableBiomeSource`/
`BiomeSourceWithNoiseRelatedSettings`/`BiomeSourceWithSeed`/`MergeableBiomeSource` (from `wover-common-api`)
for free, and only need to implement:

- `acceptedTags()` — the Biome tags this source places Biomes for, in priority order.
- `fallbackBiome()` — the Biome to fall back to if a picker ends up empty.
- `toShortString()` — a short description used in log messages.
- `onInitMap(long newSeed)`/`onHeightChange(int newHeight)` — (re)build your `BiomeMap`(s) from a
  `MapBuilderFunction` (or a hand-rolled `BiomeMap`).
- `getNoiseBiome(int biomeX, int biomeY, int biomeZ, Climate.Sampler sampler)` (from `BiomeSource`) — use
  `WoverBiomePicker.PickableBiome#biome`/`#getSubBiome(random)`/`#getEdge()` off your map's picked Biome to
  decide the final result.

Register the new source's `MapCodec` with `BiomeSourceManager.register(location, codec)` so it becomes a
valid `biome_source` type, mirroring how `ChunkGeneratorManager.register(location, codec)` registers a new
`chunk_generator` type (only needed if you also replace the generator itself, not just the biome source).

### Exposing a settings panel in the world-creation screen

Implement `BiomeSourceWithConfigScreen<B, C>` (client-only, alongside `BiomeSourceWithConfig<B, C>` from
`wover-common-api`) to let players tweak your `BiomeSourceConfig` from the world-creation UI, the same way
WoVer's own Nether/End sources do:

```java
@Override
@Environment(EnvType.CLIENT)
public BiomeSourceConfigPanel<MySource, MyConfig> biomeSourceConfigPanel(@NotNull Screen parent) {
    return new MyConfigPage(config);
}
```

`BiomeSourceConfigPanel#getPanel()` builds the `LayoutComponent` UI (see `wover-ui-api`), and
`#updateSettings(ChunkGenerator)` applies the edited settings back to a `ChunkGenerator` — see
`BiomeSourceConfigPanel.DimensionUpdater` for how the world-creation screen wires that back into the
selected dimension.

## Relationship to other WoVer modules

- **`wover-biome-api`** — supplies the `Biome`/`BiomeData` registrations and `BiomeBuilder` this module's
  `WoverBiomeBuilder` extends, and the `intendedPlacement` tags (`minecraft:is_nether`, `c:is_end_highland`,
  ...) that drive which `WoverBiomePicker` a Biome ends up in.
- **`wover-tag-api`** — `CommonBiomeTags` (`IS_END_CENTER`, `IS_END_HIGHLAND`, `IS_END_MIDLAND`,
  `IS_END_BARRENS`, `IS_SMALL_END_ISLAND`) are the tags `WoverEndBiomeSource` places its five rings by.
- **`wover-surface-api`** — the surface rules attached to Biomes via `wover-biome-api` are injected into the
  dimension's `NoiseGeneratorSettings` when WoVer's `WoverChunkGenerator` is enforced for a dimension.
- **`wover-preset-api`** — `WorldPresetBootstrapContext`/`WoverWorldPresetProvider` are the datagen mechanics
  `WorldPresets`/`WorldPresetProvider` build on top of.
- **`wover-ui-api`** — `BiomeSourceConfigPanel#getPanel()` returns a `LayoutComponent` built with that
  module's layout system.
- **`wover-common-api`** — `WoverBiomeSource`/`WoverChunkGenerator` implement that module's
  `BiomeSourceWithConfig`/`MergeableBiomeSource`/`EnforceableChunkGenerator`/... interfaces so other mods
  (or WoVer itself) can manage them without a hard dependency on this module.

## Real usage

See `wover-generator-api/src/testmod` (`TestModWoverWorldGenerator` — `WoverBiomeBuilder` keys, suggesting
`WorldPresets.WOVER_WORLD` as the default preset) and `src/testmodDatagen` (`BiomeProvider` — a main biome, a
sub-biome via `.parent(...)`, and a sub-biome parented to a vanilla Biome) for complete, compiling examples.
`src/datagen/.../WorldPresetProvider.java` shows how WoVer's own `normal`/`large`/`amplified` presets are
built from `WorldPresets.makeWoverNetherStem`/`makeWoverEndStem`, including the legacy BCLib-namespaced
duplicates.
