# wover-biome-api

Java API to define new Biomes and to modify existing ones (including vanilla Biomes) at runtime, plus a
Datapack backed registry (`BiomeData`) that attaches extra, non-vanilla data to a Biome. `wover-biome-api`
does not invent a new format for the vanilla `Biome` registry itself — Biomes it creates are plain
`data/<namespace>/worldgen/biome/*.json`, produced through datagen like any vanilla Biome.

- **Gradle artifact:** `org.betterx:wover-biome-api`
- **Depends on:** `wover-core-api`, `wover-event-api`, `wover-feature-api` (and, transitively,
  `wover-surface-api`, `wover-structure-api`, `wover-tag-api`)
- **Java packages:**
  - `org.betterx.wover.biome.api` — `BiomeManager` (main entry point), `BiomeKey` (handle for a Biome)
  - `org.betterx.wover.biome.api.builder` — `BiomeBuilder`/`BiomeBootstrapContext`/`BiomeSurfaceRuleBuilder`,
    the fluent builders used to fill in a Biome's content
  - `org.betterx.wover.biome.api.builder.event` — `OnBootstrapBiomes`, the subscriber type used to build
    Biomes at runtime
  - `org.betterx.wover.biome.api.data` — `BiomeData`/`BiomeDataRegistry`/`BiomeGenerationDataContainer`/
    `BiomeCodecRegistry`, the extra data attached to a Biome
  - `org.betterx.wover.biome.api.modification` — `BiomeModification`/`BiomeModificationRegistry`, runtime
    modification of existing Biomes
  - `org.betterx.wover.biome.api.modification.predicates` — `BiomePredicate`/`BiomePredicateRegistry`, the
    conditions a `BiomeModification` is applied under
  - `org.betterx.wover.datagen.api.provider` / `.provider.multi` — `WoverBiomeDataProvider`,
    `WoverSurfaceRuleProvider`, `WoverBiomeProvider` datagen provider base classes (see the `wover-datagen-api`
    wiki page for the general provider/`PackBuilder` mechanics)

## For Datapack Developers

### `Biome` — `data/<namespace>/worldgen/biome/<path>.json`

Unmodified vanilla format (`has_precipitation`, `temperature`, `downfall`, `effects`, `spawners`,
`spawn_costs`, `carvers`, `features`, ...). `wover-biome-api` only *generates* these files through its
builder API (see below) — it never changes what a valid Biome JSON looks like.

### `BiomeData` — `data/<namespace>/wover/worldgen/biome_data/<path>.json`

A `BiomeData` entry always uses the **same location** as the Biome it belongs to (`BiomeDataRegistry` derives
one key from the other via `BiomeDataRegistry.createKey(ResourceKey<Biome>)` /
`BiomeDataRegistry.createBiomeKey(ResourceKey<BiomeData>)`). Like vanilla's `Feature`/`PlacementModifier`
registries, the registry itself is dispatched on a `type` field (backed by `BiomeCodecRegistry`,
`wover/biome_codec`) so `BiomeData` subclasses can plug in their own fields; the base class is registered as
`wover:vanilla_data`. A minimal real (datagen-produced) example, only setting the required `biome` field and
leaving `fogDensity`/`generation_data` at their defaults:

```json
{
  "type": "wover:vanilla_data",
  "biome": "minecraft:savanna"
}
```

The full base `BiomeData.CODEC` (a `MapCodec`, dispatched under `wover:vanilla_data`) reads:

```json
{
  "type": "wover:vanilla_data",
  "fogDensity": 1.0,
  "biome": "minecraft:savanna",
  "generation_data": {
    "parameter_points": [],
    "intended_placement": "mymod:is_my_biome_type"
  }
}
```

- `fogDensity` (float, optional, defaults to `1.0`) — the fog density that was set on the builder with
  `BiomeBuilder#fogDensity(float)`.
- `biome` (`ResourceKey<Biome>`, required) — the Biome this data belongs to.
- `generation_data` (`BiomeGenerationDataContainer`, optional, defaults to empty) — `parameter_points` (list
  of vanilla `Climate.ParameterPoint`, added with `BiomeBuilder#addClimate(...)`/`#addNetherClimate(...)`)
  and `intended_placement` (an optional Biome `TagKey`, set with `BiomeBuilder#intendedPlacement(TagKey)` or
  implicitly by helpers like `VanillaBuilder#isNetherBiome()`).

You will normally never hand-write these files — they are produced by the `BiomeBuilder` API through
datagen (see below). `BiomeData` is a normal (extensible) Java class: a mod can subclass it to attach
completely custom fields, register the subclass's `type` id and codec with
`BiomeCodecRegistry.register(location, keyDispatchDataCodec)`, and build the codec itself with one of the
`BiomeData.codec(...)` factory overloads (`codec(factory)` for no extra fields, up to thirteen extra
`RecordCodecBuilder` fields via the `codec(p4, ..., p16, factory)` overloads) — the same "extra
`RecordCodecBuilder` fields on top of a common base" pattern vanilla uses for its own dispatch codecs.

### `BiomeModification` — `data/<namespace>/wover/worldgen/biome_modifications/<path>.json`

Modifications are collected in their own registry and applied to matching Biomes once the world (server) is
ready — they can target **any** Biome, including vanilla ones or Biomes added by another mod, not just ones
registered through this API:

```json
{
  "predicate": {
    "type": "wover:or",
    "predicates": [
      { "type": "wover:is_biome", "biome_key": "minecraft:beach" },
      { "type": "wover:is_biome", "biome_key": "minecraft:plains" }
    ]
  },
  "features": [
    [], [], [], [], [], [], [], [], [],
    ["minecraft:small_basalt_columns"]
  ],
  "spawns": [],
  "biome_tags": []
}
```

- `predicate` (required, `BiomePredicate.CODEC`) — decides which Biomes the modification applies to. Falls
  back to "matches every Biome" if omitted — almost never what you want.
- `features` (optional, defaults to `[]`) — a list indexed by `GenerationStep.Decoration` ordinal (11 vanilla
  steps, from `raw_generation` to `top_layer_modification`), each entry a list of `PlacedFeature` references
  to append to that decoration step.
- `spawns` (optional) — a weighted list (`WeightedList<MobSpawnSettings.SpawnerData>`) of extra mob spawns
  to add.
- `biome_tags` (optional) — extra Biome tags the matching Biomes should be added to.

`BiomePredicate` types registered by this module (dispatched through `BiomePredicateRegistry`,
`wover/biome_predicates`), all constructible from Java via the static factories on `BiomePredicate`:
`wover:or`/`wover:and`/`wover:not` (`in_biomes(keys...)`/`not_in_biomes(keys...)` are Java-only sugar built
from `wover:or`/`wover:not` around several `wover:is_biome` predicates rather than their own registered
type), `wover:all` (`always()` — matches everything, the default when no predicate is set), `wover:is_biome`,
`wover:in_dimension` (plus the `inOverworld()`/`inEnd()`/`inNether()` shortcuts), `wover:has_tag`,
`wover:spawns` (entity type), `wover:has_structure`, `wover:has_placed_feature`,
`wover:has_configured_feature`, `wover:is_namespace` (`isVanilla()`/`inNamespace(...)`, or wrapped in
`wover:not` for `notInNamespace(...)`), `wover:location_path_contains` (`pathContains(needle)`), and
`wover:config_is`, a config-file backed `hasConfig(...)` predicate (see `wover-core-api`'s `Configs`).

## For Mod Developers

### Registering a new Biome: `BiomeManager` + `BiomeKey` + `BiomeBuilder`

Declare a `BiomeKey` once (typically a `static final` field), then bootstrap it from a datagen provider (or,
less commonly, at runtime — see below):

```java
public class MyBiomeKeys {
    public static final BiomeKey<BiomeBuilder.Vanilla> MY_BIOME =
            BiomeManager.vanilla(MyMod.C.id("my_biome"));
}
```

```java
public class BiomeProvider extends WoverBiomeProvider {
    public BiomeProvider(ModCore modCore) {
        super(modCore);
    }

    @Override
    protected void bootstrap(BiomeBootstrapContext context) {
        MyBiomeKeys.MY_BIOME
                .bootstrap(context)
                .temperature(0.8f)
                .downfall(0.4f)
                .hasPrecipitation(true)
                .surface(Blocks.GRASS_BLOCK, Blocks.DIRT)
                .fogColor(BiomeBuilder.DEFAULT_FOG_COLOR)
                .waterAndFogColor(BiomeBuilder.DEFAULT_WATER_COLOR)
                .defaultMushrooms()
                .feature(GenerationStep.Decoration.VEGETAL_DECORATION, MyFeatureKeys.PLACED_MY_TREE)
                .spawn(EntityType.COW, 8, 4, 4)
                .tag(CommonBiomeTags.IS_END_HIGHLAND)
                .register();
    }
}
```

`BiomeManager.vanilla(location)` returns a `BiomeKey<BiomeBuilder.Vanilla>` for a **completely new**,
vanilla-style Biome. Calling `.bootstrap(context)` on it returns a `BiomeBuilder.Vanilla`, which exposes:

- **Terrain/climate**: `temperature(float)`, `downfall(float)`, `hasPrecipitation(boolean)`,
  `temperatureAdjustment(Biome.TemperatureModifier)` (plus `temperatureFrozen()`/`temperatureRegular()`
  shortcuts), `addClimate(Climate.ParameterPoint)`/`addNetherClimate(temperature, humidity[, offset])` (where
  the Biome is placed — actual placement into a `BiomeSource` is the job of `wover-generator-api`, not this
  module).
- **Surface**: `surface(state)`/`surface(block)`/`surface(top, under)` convenience overloads, or
  `startSurface()` for the full `BiomeSurfaceRuleBuilder<B>` (`surface`/`subsurface`/`filler`/`floor`/`ceil`/
  `steep`/... — the same builder documented on the `wover-surface-api` wiki page) finished with
  `.finishSurface()`.
- **Ambience**: `fogColor`/`waterColor`/`waterFogColor`/`skyColor`/`foliageColorOverride`/
  `grassColorOverride`/`grassColorModifier` (each with an `(int color)` and an `(int r, int g, int b)`
  overload), plus `waterAndFogColor(...)` and `plantsColor(...)` shortcuts that set two colors at once, and
  `particles(...)`/`loop(...)`/`mood(...)`/`additions(...)`/`music(...)` for ambient sound/particle settings.
  `DEFAULT_FOG_COLOR`, `DEFAULT_WATER_COLOR`, `DEFAULT_END_FOG_COLOR`, `DEFAULT_NETHER_TEMPERATURE`, etc. on
  `BiomeBuilder` provide vanilla's Nether/End default values to start from.
- **Features & carvers**: `feature(BasePlacedFeatureKey<?>)` / `feature(Decoration, ResourceKey<PlacedFeature>)`
  / `feature(Decoration, Holder<PlacedFeature>)` to add a `PlacedFeature` to a `GenerationStep.Decoration`
  bucket, `feature(Consumer<BiomeGenerationSettings.Builder>)` to run one of vanilla's
  `BiomeDefaultFeatures` helpers directly (`defaultMushrooms()`/`netherDefaultOres()` are built-in shortcuts
  for two of them), and `carver(ResourceKey<ConfiguredWorldCarver<?>>)`/`carver(Holder<...>)`.
- **Mob spawns**: `spawn(EntityType<?>, weight, minGroupCount, maxGroupCount)`, `addMobCharge(entityType,
  energyBudget, charge)`, `creatureGenerationProbability(float)`.
- **Tags**: `tag(TagKey<Biome>...)` adds arbitrary Biome tags; `structure(StructureKey<?,?,?>)`/
  `structure(TagKey<Biome>)` add a structure set's Biome tag so the structure can generate here;
  `intendedPlacement(TagKey<Biome>)` records (without adding as a plain tag) which tag identifies "this kind
  of Biome" for later lookup via `BiomeData#isIntendedFor(TagKey)`; convenience methods
  `isNetherBiome()`/`isEndHighlandBiome()`/`isEndMidlandBiome()`/`isEndCenterIslandBiome()`/
  `isEndBarrensBiome()`/`isEndSmallIslandBiome()` both add the matching `BiomeTags`/`CommonBiomeTags` entry
  **and** set it as the intended placement in one call.

Finish the chain with `.register()`. Behind the scenes, `.register()` hands the builder to the
`BiomeBootstrapContext` it was created from, which later calls it back once for each of the Biome,
`BiomeData`, surface-rule and Biome-tag registries — you never call `registerBiome`/`registerBiomeData`/
`registerSurfaceRule`/`registerBiomeTags` yourself.

### Attaching data to an existing Biome: `BiomeManager.wrapped(...)`

If you only need to attach `BiomeData` (fog density, climate parameters, intended placement, tags) to a
Biome that already exists — a vanilla one, or one from another mod — without redefining the Biome itself,
use `BiomeManager.wrapped(ResourceKey<Biome>)` instead of `vanilla(...)`:

```java
public static final BiomeKey<BiomeBuilder.Wrapped> WRAPPED_SAVANNA =
        BiomeManager.wrapped(Biomes.SAVANNA);
```

```java
WRAPPED_SAVANNA.bootstrap(context)
               .fogDensity(1.2f)
               .isEndHighlandBiome()
               .register();
```

`BiomeBuilder.Wrapped` only exposes the base `BiomeBuilder` setters (climate, fog density, surface rule,
tags, intended placement) — it does not touch the underlying vanilla `Biome` at all (its
`registerBiome(...)` is a no-op), and it skips writing a `BiomeData` file entirely when nothing was
customized (fog density left at `1.0` and no climate parameters added).

### Looking up `BiomeData` at runtime

```java
BiomeData data = BiomeManager.biomeData(new ResourceLocation("minecraft", "savanna"));
// or, if you already have a Holder<Biome>:
BiomeData data2 = BiomeManager.biomeDataForHolder(someBiomeHolder);
```

Both have an overload taking an explicit `HolderLookup.Provider`; the no-arg-registry-access versions use
`WorldState.registryAccess()` (see `wover-core-api`'s `WorldState`). `BiomeData` itself exposes
`biome()`/`biomeHolder()` to resolve the `Biome`/`Holder<Biome>` it belongs to, and
`isIntendedFor(TagKey<Biome>)` to test its `intendedPlacement`.

### Modifying existing Biomes at runtime: `BiomeModification`

Unlike `BiomeBuilder` (which only defines *new* Biomes or attaches data to a specific one),
`BiomeModification` adds features/spawns/tags to **every Biome matching a predicate**, applied once the
world (server) is ready — this is the tool to reach into vanilla Biomes (or another mod's) instead of
your own. Prefer generating the modification through a `WoverRegistryContentProvider<BiomeModification>`:

```java
public class ModificationProvider extends WoverRegistryContentProvider<BiomeModification> {
    public ModificationProvider(ModCore modCore) {
        super(modCore, "Biome Modifications", BiomeModificationRegistry.BIOME_MODIFICATION_REGISTRY);
    }

    @Override
    protected void bootstrap(BootstrapContext<BiomeModification> context) {
        var features = context.lookup(Registries.PLACED_FEATURE);

        BiomeModification
                .build(context, modCore.id("test_features"))
                .inBiomes(Biomes.BEACH, Biomes.MEADOW)
                .addFeature(
                        GenerationStep.Decoration.SURFACE_STRUCTURES,
                        features.getOrThrow(NetherPlacements.SMALL_BASALT_COLUMNS)
                )
                .register();

        BiomeModification
                .build(context, modCore.id("test_tags"))
                .inNether()
                .addToTag(BiomeTags.HAS_SWAMP_HUT)
                .register();
    }
}
```

`BiomeModification.build(context, location)` (or `build(context, ResourceKey<BiomeModification>)`) returns a
`BiomeModification.Builder`. Predicate helpers mirror the JSON predicate types 1:1:
`isBiome(key)`/`inBiomes(keys...)`/`notInBiomes(keys...)`, `inDimension(key)`/`inOverworld()`/`inEnd()`/
`inNether()`, `hasTag(tag)`, `spawns(entityType)`, `hasStructure(key)`, `hasPlacedFeature(key)`,
`hasConfiguredFeature(key)`, `isVanilla()`/`inNamespace(String|ModCore)`/`notInNamespace(String|ModCore)`,
`pathContains(needle)`, `hasConfig(configValue, targetValue)`, and the combinators `anyOf(...)`/`allOf(...)`/
`not(...)`. Content helpers: `addFeature(Decoration, ResourceKey<PlacedFeature>|Holder<PlacedFeature>)`,
`addFeature(BasePlacedFeatureKey<?>)` (uses the key's own `Decoration`), `addStructureSet(StructureKey<?,?,?>|
TagKey<Biome>)` (adds the structure's Biome tag), `addSpawn(entityType, weight, min, max)`, and
`addToTag(TagKey<Biome>)`. Finish with `.register()` (adds it to the passed `BootstrapContext`) or
`.directHolder()` for an unregistered `Holder.Direct`.

To instead create modifications at runtime (only if you truly cannot use datagen), subscribe to
`BiomeModificationRegistry.BOOTSTRAP_BIOME_MODIFICATION_REGISTRY`, which fires with the same kind of
`BootstrapContext` whenever the registry is (re-)loaded:

```java
if (!ModCore.isDatagen()) {
    BiomeModificationRegistry.BOOTSTRAP_BIOME_MODIFICATION_REGISTRY.subscribe(context -> {
        var features = context.lookup(Registries.PLACED_FEATURE);

        BiomeModification
                .build(context, MyMod.C.id("runtime_modification"))
                .isBiome(Biomes.MEADOW)
                .addFeature(
                        GenerationStep.Decoration.VEGETAL_DECORATION,
                        features.getOrThrow(NetherPlacements.SMALL_BASALT_COLUMNS)
                )
                .register();
    });
}
```

### Registering new Biomes at runtime instead of datagen

Just like `BiomeModification`, Biomes themselves can be built outside of datagen by subscribing to
`BiomeManager.BOOTSTRAP_BIOMES_WITH_DATA` (an `OnBootstrapBiomes` subscriber, fired whenever the internal
bootstrap context is (re-)created) and calling `.bootstrap(context)` on your `BiomeKey` from there — but as
with modifications, a `WoverBiomeProvider`/`WoverRegistryContentProvider` during datagen is the recommended
path; the JSON files it produces work without your mod code running at all.

### Datagen provider base classes

`org.betterx.wover.datagen.api.provider` (this module) ships three thin `WoverRegistryContentProvider`
subclasses that pre-bind the registry key, plus one combined `WoverMultiProvider` — register any of them
from your datagen entrypoint's `onInitializeProviders(PackBuilder)` the same way as any other
`wover-datagen-api` provider (see that module's wiki page for the general mechanics):

| Provider | Registry | Use for |
|---|---|---|
| `WoverBiomeDataProvider` | `BiomeDataRegistry.BIOME_DATA_REGISTRY` | Hand-writing `BiomeData` entries directly (bypassing `BiomeBuilder`). |
| `WoverSurfaceRuleProvider` | `SurfaceRuleRegistry.SURFACE_RULES_REGISTRY` | Hand-writing `AssignedSurfaceRule` entries for a Biome (see `wover-surface-api`). |
| `WoverBiomeProvider` (`.provider.multi`) | `Biome`, `BiomeData`, surface rules, Biome tags | The `BiomeBuilder` path shown above — one `bootstrap(BiomeBootstrapContext)` override feeds all four registries at once via `.addMultiProvider(...)`. |

Register `WoverBiomeProvider` with `PackBuilder#addMultiProvider(...)`, the others with
`PackBuilder#addRegistryProvider(...)`:

```java
public class MyModDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        globalPack.addRegistryProvider(ModificationProvider::new);
        globalPack.addRegistryProvider(BiomeDataProvider::new);
        globalPack.addMultiProvider(BiomeProvider::new);
    }

    @Override
    protected ModCore modCore() {
        return MyMod.C;
    }
}
```

## Relationship to other WoVer modules

- **`wover-feature-api`** — `BiomeBuilder#feature(...)`/`BiomeModification.Builder#addFeature(...)` take the
  `PlacedFeatureKey`/`Holder<PlacedFeature>` types documented on that module's wiki page; `addFeature
  (BasePlacedFeatureKey<?>)` specifically relies on `PlacedFeatureKey#setDecoration(...)`/`getDecoration()` to
  pick the right `GenerationStep.Decoration` bucket automatically.
- **`wover-surface-api`** — `BiomeBuilder#startSurface()` returns a `BiomeSurfaceRuleBuilder` built on top of
  that module's `BaseSurfaceRuleBuilder`/`AssignedSurfaceRule`; `WoverSurfaceRuleProvider` bootstraps the
  same `SurfaceRuleRegistry` documented there.
- **`wover-structure-api`** — `BiomeBuilder#structure(StructureKey<?,?,?>)` and
  `BiomeModification.Builder#addStructureSet(StructureKey<?,?,?>)` add the Biome tag associated with a
  `StructureKey`, so the structure set can generate in the Biome.
- **`wover-generator-api`** — actually *placing* the Biomes defined here into a dimension's `BiomeSource`
  (using the climate parameters/intended-placement tag from `BiomeGenerationDataContainer`) is that module's
  responsibility, not this one's.

## Real usage

See `wover-biome-api/src/testmod` (`TestModWoverBiome` — `BiomeKey` declaration and a runtime
`BiomeModification` registered from `BOOTSTRAP_BIOME_MODIFICATION_REGISTRY`) and `src/testmodDatagen`
(`BiomeProvider`, `BiomeDataProvider`, `ModificationProvider`, `TestModWoverBiomeDatagen`) for complete,
compiling examples of everything above.
