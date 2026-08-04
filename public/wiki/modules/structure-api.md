# wover-structure-api

Java API for vanilla `Structure`s, `StructureSet`s, jigsaw `StructureTemplatePool`s and
`StructureProcessorList`s. `wover-structure-api` does not invent a new datapack format for any of the four
vanilla worldgen registries — they stay plain vanilla JSON. On top of that it provides a fluent builder API
so mod code doesn't have to hand-write structure JSON or fight vanilla's generic-heavy Java types, plus one
ready-made custom `Structure` (`RandomNbtStructure`) for the common "spawn a random `.nbt` template
somewhere valid" use case that vanilla has no equivalent of.

- **Gradle artifact:** `de.ambertation:worldweaver` (single artifact; this module ships inside it as the Fabric mod `wover-structure`)
- **Depends on:** `wover-core-api`, `wover-math-api`, `wover-event-api`, `wover-block-api`
- **Java packages:**
  - `de.ambertation.wover.structure.api`
  - `de.ambertation.wover.structure.api.builders`
  - `de.ambertation.wover.structure.api.pools`
  - `de.ambertation.wover.structure.api.processors`
  - `de.ambertation.wover.structure.api.sets`
  - `de.ambertation.wover.structure.api.structures`
  - `de.ambertation.wover.structure.api.structures.nbt`
  - `de.ambertation.wover.datagen.api.provider.multi` (adds `WoverStructureProvider`, the multi-registry
    provider used below, to the datagen module's package)

## For Datapack Developers

Four separate vanilla registries work together to put a structure into the world. WoVer doesn't change any
of their file formats — the sections below only document how the pieces fit together, and where WoVer adds
a convenience on top (its own `RandomNbtStructure` type).

### `Structure` — `data/<namespace>/worldgen/structure/<path>.json`

Unmodified vanilla format. Every structure has a `type` (e.g. `minecraft:jigsaw`), a `biomes` tag/list, a
`step` (a `GenerationStep.Decoration`, e.g. `surface_structures`), and further fields depending on `type`:

```json
{
  "type": "minecraft:jigsaw",
  "biomes": "#mymod:has_structure/my_structure",
  "step": "surface_structures",
  "start_pool": "mymod:my_structure/start",
  "size": 5,
  "max_distance_from_center": 80,
  "use_expansion_hack": false,
  "project_start_to_heightmap": "WORLD_SURFACE_WG",
  "terrain_adaptation": "beard_thin"
}
```

WoVer's `JigsawBuilder`/`RandomNbtBuilder`/`StructureBuilder` (see below) build exactly this JSON through
their setters — `biomeTag(...)` → `biomes`, `step(...)` → `step`, `adjustment(...)` → `terrain_adaptation`,
etc.

#### WoVer's own type: `wover:random_nbt_structure`

Registered internally as `StructureManager.RANDOM_NBT_STRUCTURE_TYPE`, this structure type picks one of
several `.nbt` templates at random (weighted) and places it once a placement strategy finds a valid
position. It is backed by `RandomNbtStructure.simpleRandomCodec(...)`, which adds these fields on top of the
standard structure settings (`biomes`, `step`, `terrain_adaptation`, ...):

```json
{
  "type": "wover:random_nbt_structure",
  "biomes": "#mymod:has_structure/my_random_structure",
  "step": "surface_structures",
  "placement": "nether_surface_flat_2",
  "keep_air": false,
  "configs": {
    "items": [
      { "value": { "location": "mymod:structures/small_ruin", "offset_y": 0 }, "weight": 3.0 },
      { "value": { "location": "mymod:structures/large_ruin", "offset_y": -1 }, "weight": 1.0 }
    ]
  }
}
```

- `placement` — one of the `StructurePlacement` values below.
- `keep_air` (bool, default `false`) — if `true`, air blocks in the `.nbt` template overwrite existing
  blocks instead of being skipped.
- `configs` — a `RandomizedWeightedList` (a WoVer `wover-core-api` type; see `RandomizedWeightedList.buildCodec`)
  of `RandomNbtStructureElement`s: each `items` entry has a `value` (`location` of the `.nbt` template plus a
  vertical `offset_y` applied to the found generation point) and a `weight` used to randomly pick between
  elements.

`StructurePlacement` values (`de.ambertation.wover.structure.api.structures.StructurePlacement`):

| Value | Behavior |
|---|---|
| `surface` | Center of the chunk, on the overworld world-surface heightmap. |
| `lava` | On top of a Nether lava surface found near sea level. |
| `nether_ceil` | Hanging from the Nether's bedrock ceiling. |
| `nether_surface` | On the Nether floor. |
| `nether_surface_flat_0` / `_flat_2` / `_flat_4` | Like `nether_surface`, but rejects placements where the four corners of the structure's bounding box differ in height by more than 0/2/4 blocks. |
| `floor` / `ceil` (legacy names) | Kept for backwards compatibility; both currently resolve to the Nether-floor search. |

### `StructureSet` — `data/<namespace>/worldgen/structure_set/<path>.json`

Unmodified vanilla format. A structure only generates if it's referenced by a `StructureSet`, which also
carries the placement strategy (how often/where in the world):

```json
{
  "structures": [
    { "structure": "mymod:my_structure", "weight": 1 }
  ],
  "placement": {
    "type": "minecraft:random_spread",
    "salt": 1234567,
    "spacing": 32,
    "separation": 8,
    "spread_type": "linear"
  }
}
```

`StructureSetBuilder` (see below) covers both vanilla placement types: `randomPlacement()`/
`randomPlacement(spacing, separation)` for `minecraft:random_spread`, and `concentricPlacement()` for
`minecraft:concentric_rings` (used by vanilla for strongholds).

### `StructureTemplatePool` — `data/<namespace>/worldgen/template_pool/<path>.json`

Unmodified vanilla format. Jigsaw structures (`start_pool` above) pull their pieces from pools:

```json
{
  "fallback": "minecraft:empty",
  "elements": [
    {
      "element": {
        "element_type": "minecraft:single_pool_element",
        "location": "mymod:my_structure/house",
        "processors": "mymod:my_processor",
        "projection": "rigid"
      },
      "weight": 1
    }
  ]
}
```

`StructurePoolBuilder.startSingle(...)`/`startSingleEnd(...)`/`startLegacySingle(...)` build
`single_pool_element`/`single_pool_element` variant/`legacy_single_pool_element` entries respectively;
`addFeature(...)` builds a `feature_pool_element`; `addEmptyElement(weight)` an `empty_pool_element`. The
`projection` shown on every element in a given pool comes from the pool-level
`StructurePoolBuilder.projection(...)` call (vanilla applies one projection uniformly to all of a pool's
elements), not from a per-element setting.

### `StructureProcessorList` — `data/<namespace>/worldgen/processor_list/<path>.json`

Unmodified vanilla format. A list of `StructureProcessor`s (most commonly `minecraft:rule`, a list of
block-swap rules) applied when a pool element is placed:

```json
{
  "processors": [
    {
      "processor_type": "minecraft:rule",
      "rules": [
        {
          "input_predicate": { "predicate_type": "minecraft:random_block_match", "block": "minecraft:red_glazed_terracotta", "probability": 0.33 },
          "output_state": { "Name": "minecraft:red_concrete" }
        }
      ]
    }
  ]
}
```

`StructureProcessorBuilder.startRule().startProcessor()...endProcessor().endRule()` builds exactly this
shape; `add(StructureProcessor)` lets you drop in any other vanilla processor type directly.

## For Mod Developers

All four registries are datapack registries bootstrapped through a `BootstrapContext`, same as vanilla
worldgen. WoVer wraps each one behind a small "key" type (`StructureKey`, `StructureSetKey`,
`StructurePoolKey`, `StructureProcessorKey`) that you create once as a `static final` field and later call
`.bootstrap(context)` on to get a builder.

### Registering everything (data generator, recommended)

Subclass [`WoverStructureProvider`](../../../wover-structure-api/src/main/java/de/ambertation/wover/datagen/api/provider/multi/WoverStructureProvider.java)
(`de.ambertation.wover.datagen.api.provider.multi.WoverStructureProvider`) — a `WoverMultiProvider` that
bundles the four registry providers (structures, sets, pools, processors) plus a biome-tag provider into
one class:

```java
public class MyStructureKeys {
    public static final StructureKey.Jigsaw MY_STRUCTURE =
            StructureKeys.jigsaw(MyMod.C.id("my_structure"))
                         .biomeTag(TagManager.BIOMES.makeStructureTag(MyMod.C, "my_structure"))
                         .step(GenerationStep.Decoration.SURFACE_STRUCTURES);

    public static final StructureSetKey MY_STRUCTURE_SET =
            StructureKeys.set(MyMod.C.id("my_structure_set"));

    public static final StructurePoolKey MY_POOL_START = StructureKeys.pool(MyMod.C.id("start"));
    public static final StructurePoolKey MY_POOL_TERMINAL = StructureKeys.pool(MyMod.C.id("terminal"));
    public static final StructureProcessorKey MY_PROCESSOR =
            StructureKeys.processor(MyMod.C.id("my_processor"));
}

public class MyStructureProvider extends WoverStructureProvider {
    public MyStructureProvider(ModCore modCore) {
        super(modCore);
    }

    @Override
    protected void bootstrapSturctures(BootstrapContext<Structure> context) {
        MyStructureKeys.MY_STRUCTURE
                .bootstrap(context)
                .startPool(MyStructureKeys.MY_POOL_START)
                .maxDepth(5)
                .projectStartToHeightmap(Heightmap.Types.MOTION_BLOCKING)
                .adjustment(TerrainAdjustment.BEARD_BOX)
                .register();
    }

    @Override
    protected void bootstrapSets(BootstrapContext<StructureSet> context) {
        MyStructureKeys.MY_STRUCTURE_SET
                .bootstrap(context)
                .addStructure(MyStructureKeys.MY_STRUCTURE)
                .randomPlacement(32, 8)
                .register();
    }

    @Override
    protected void bootstrapPools(BootstrapContext<StructureTemplatePool> context) {
        MyStructureKeys.MY_POOL_START
                .bootstrap(context)
                .terminator(MyStructureKeys.MY_POOL_TERMINAL)
                .startSingle(MyMod.C.id("house"))
                .endElement()
                .projection(StructureTemplatePool.Projection.TERRAIN_MATCHING)
                .register();

        MyStructureKeys.MY_POOL_TERMINAL
                .bootstrap(context)
                .startSingle(MyMod.C.id("terminator"))
                .processor(MyStructureKeys.MY_PROCESSOR)
                .endElement()
                .register();
    }

    @Override
    protected void bootstrapProcessors(BootstrapContext<StructureProcessorList> context) {
        MyStructureKeys.MY_PROCESSOR
                .bootstrap(context)
                .startRule()
                .startProcessor()
                .inputPredicateRandom(Blocks.RED_GLAZED_TERRACOTTA, 0.33f)
                .outputState(Blocks.RED_CONCRETE)
                .endProcessor()
                .endRule()
                .register();
    }

    @Override
    protected void prepareBiomeTags(TagBootstrapContext<Biome> context) {
        context.add(MyStructureKeys.MY_STRUCTURE.biomeTag(), Biomes.PLAINS, Biomes.SAVANNA);
    }
}
```

Register it as a *multi* provider (not `addRegistryProvider`) from your datagen entrypoint:

```java
public class MyModDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        globalPack.addMultiProvider(MyStructureProvider::new);
    }

    @Override
    protected ModCore modCore() {
        return MyMod.C;
    }
}
```

`biomeTag()` on a `StructureKey` also works as a getter with no argument set: it lazily creates (and
remembers) a structure-tag `TagKey` from the structure's own id if you never explicitly call
`biomeTag(TagKey)`, so `MY_STRUCTURE.biomeTag()` is always safe to call from `prepareBiomeTags`.

If you only need one of the four registries (e.g. just structures, referencing a pool defined elsewhere),
you can instead subclass the single-registry providers `WoverStructureProvider`, `WoverStructureSetProvider`,
`WoverStructurePoolProvider` or `WoverStructureProcessorProvider` from `de.ambertation.wover.datagen.api.provider`
(see the `wover-datagen-api` wiki page) and register each with `PackBuilder#addRegistryProvider`.

### `StructureKey` variants

`StructureKeys`/`StructureManager` create three kinds of `StructureKey`, matched to the structure/builder
type:

| Factory | Key type | Builder | Use for |
|---|---|---|---|
| `StructureKeys.jigsaw(location)` | `StructureKey.Jigsaw` | `JigsawBuilder` | `JigsawStructure` (`minecraft:jigsaw`) |
| `StructureKeys.randomNbt(location)` | `StructureKey.RandomNbt` | `RandomNbtBuilder` | WoVer's `RandomNbtStructure` |
| `StructureKeys.structure(location, factory)` / `(location, factory, codec)` / `(location, typeKey)` | `StructureKey.Simple<S>` | `StructureBuilder<S>` | Your own custom `Structure` subclass |

For a custom `Structure` subclass, `StructureKeys.structure(location, YourStructure::new)` registers a
`StructureType` for you (assuming `YourStructure` has no extra fields, via `Structure.simpleCodec`); use the
`(location, factory, codec)` overload if it needs custom serialization. Your subclass's `type()` override
should then return `YourKey.type()` (see the test mod's `TestStructure`, which returns
`TestModWoverStructure.TEST_STRUCTURE.type()`).

Every `StructureKey` also has `getHolder(...)` overloads (`HolderGetter`, `HolderLookup.Provider`,
`BootstrapContext`, `RegistryAccess`) to resolve the registered `Holder<Structure>` at various points in the
loading lifecycle, and `step(GenerationStep.Decoration)`/`step()` to get/set the generation step (defaults
to `SURFACE_STRUCTURES`).

### Builders in detail

- **`BaseStructureBuilder`** (shared by all three): `register()` (adds to the active `BootstrapContext`),
  `directHolder()` (builds an unregistered, inline `Holder`, useful for structures that only exist as part
  of another structure's data), `adjustment(TerrainAdjustment)` (defaults to `TerrainAdjustment.NONE`).
- **`JigsawBuilder`**: `startPool(...)` (required — accepts a `Holder`, `ResourceKey` or `StructurePoolKey`),
  `maxDepth` (default `6`), `startHeight` (default constant `0`), `maxDistanceFromCenter` (default `80`),
  `projectStartToHeightmap`, `startJigsawName`, `useExpansionHack` (default `false`),
  `addAliasBinding(s)`, `liquidSettings`, `dimensionPadding`.
- **`RandomNbtBuilder`**: `addElement(Identifier elementId, int yOffset, double weight)` (call
  repeatedly to add candidates), `placement(StructurePlacement)` (default `SURFACE`), `keepAir(boolean)`
  (default `false`).
- **`StructureBuilder<S>`**: no extra methods beyond `BaseStructureBuilder` — used when your custom
  `Structure` needs nothing more than the base settings.

### `StructureSetBuilder` in detail

`addStructure(ResourceKey<Structure>, weight)` / `addStructure(StructureKey, weight)` (both also have a
no-weight overload defaulting to `1`) add structures to the set. Exactly one placement must be set:

- `randomPlacement()` returns a `RandomSpreadStructurePlacementBuilder` (`spacing` default `32`,
  `separation` default `8`, `spreadType` default `LINEAR`) — call `.finishPlacement()` to apply it, or use
  the `randomPlacement(spacing, separation)` shorthand.
- `concentricPlacement()` returns a `ConcentricRingsStructurePlacementBuilder` (`distance` default `32`,
  `spread` default `3`, `count` default `128`, `preferredBiomes` default `BiomeTags.STRONGHOLD_BIASED_TO`).
- `setPlacement(StructurePlacement)` accepts any pre-built vanilla placement directly.

Both placement builders share `locateOffset`, `frequencyReductionMethod`, `frequency` (default `1.0`), and
`exclusionZone` from the common `StructurePlacementBuilder` base, and default `salt` to the (absolute)
hash code of the set's own id so different sets don't share correlated randomness by accident.

`StructureSetManager.bootstrap(structureKey, context)` is a one-line shortcut for the common case of "one
set containing exactly one structure, with the set id equal to the structure id":
`createKey(structure).bootstrap(context).addStructure(structure)` — chain a placement call and `.register()`
onto the result.

### `StructurePoolBuilder` in detail

`add(Function<Projection, StructurePoolElement>, weight)` is the generic escape hatch; the common cases have
dedicated methods: `addFeature(feature, weight)`, `addEmptyElement(weight)`, and the `start*` family that
returns a nested `ElementBuilder`:

```java
MY_POOL_START.bootstrap(context)
             .startSingle(MyMod.C.id("street"))       // or startSingleEnd(...) / startLegacySingle(...)
             .processor(MY_PROCESSOR)                  // optional, defaults to the empty processor list
             .weight(1)                                 // optional, defaults to 1
             .liquidSettingsOverride(LiquidSettings.IGNORE_WATERLOGGING) // optional
             .endElement()                              // back to the StructurePoolBuilder
             .projection(StructureTemplatePool.Projection.TERRAIN_MATCHING)
             .terminator(MY_POOL_TERMINAL)               // or emptyTerminator(), the default if unset
             .register();
```

`startSingle` builds a `SinglePoolElement`, `startSingleEnd` an End-specific variant exempt from the End's
"skip empty chunks" rule, and `startLegacySingle` a `LegacySinglePoolElement` for pre-liquid-settings data.

### `StructureProcessorBuilder` in detail

`add(StructureProcessor)` accepts any pre-built vanilla processor. `startRule()` builds a `RuleProcessor`
inline, nesting one or more `startProcessor()` rules:

```java
MY_PROCESSOR.bootstrap(context)
            .startRule()
              .startProcessor()
                .inputPredicate(Blocks.OAK_PLANKS)          // or inputPredicateRandom(block, chance)
                .locationPredicate(Blocks.AIR)               // or locationAlways(), locationPredicateRandom(...)
                .outputState(Blocks.SPRUCE_PLANKS)           // or outputState(BlockState)
              .endProcessor()
              .startProcessor()   // sibling rule, same RuleProcessor
                // ...
              .endProcessor()
            .endRule()
            .register();
```

### Placing `.nbt` templates without a `Structure` (`StructureNBT`)

For simple, non-worldgen use cases (e.g. a `/mymod place` command, or a feature you build entirely at
runtime) `de.ambertation.wover.structure.api.StructureNBT` loads and caches a `.nbt` file from
`data/<namespace>/structure/<path>.nbt` on the classpath, without going through the
`Structure`/`StructureSet` machinery at all:

```java
StructureNBT nbt = StructureNBT.create(MyMod.C.id("small_ruin"));
nbt.generateCentered(serverLevel, pos, StructureNBT.getRandomRotation(random), StructureNBT.getRandomMirror(random));
```

`generateAt(...)` places the template's raw origin at `pos` instead of centering it;
`getBoundingBox`/`getCenteredBoundingBox` compute the box a placement would occupy without placing anything;
`createResourcesFrom(Identifier folder, int recursionDepth)` walks a whole folder (recursively, or one
level with `recursionDepth = 1`) and returns a `StructureNBT` for every `.nbt` file found — useful for a mod
that ships a large, self-discovering library of small structures.

### `StructureUtils`

`StructureUtils.isValidBiome(Structure.GenerationContext)` (tests at height `5` in the middle of the chunk)
and the `(context, yPos)` overload are small helpers for implementing `findGenerationPoint` in a fully
custom `Structure` subclass, running the context's `validBiome()` predicate at a specific height.

## Real usage

See `wover-structure-api/src/testmod` (`TestModWoverStructure`, key declarations; `TestStructure`, a
minimal custom `Structure` subclass) and `src/testmodDatagen` (`StructureProvider`/
`TestModWoverStructureDatagen`, a complete `WoverStructureProvider` registering a simple structure, a
jigsaw structure, a `RandomNbtStructure`, their sets, pools and a processor) for complete, compiling
examples of everything above.
