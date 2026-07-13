# wover-surface-api

Java API and datapack-driven registry for building `SurfaceRules.RuleSource` trees per-biome and injecting them into
**any** noise-based `ChunkGenerator` (vanilla's, another mod's, or your own) — without a mod having to overwrite
`NoiseGeneratorSettings#surfaceRule()` itself and risk clobbering rules another mod already installed.

- **Gradle artifact:** `org.betterx:wover-surface-api`
- **Depends on:** `wover-common-api`, `wover-datagen-api`, `wover-core-api`, `wover-math-api`, `wover-event-api`
- **Java packages:**
  - `org.betterx.wover.surface.api`
  - `org.betterx.wover.surface.api.conditions`
  - `org.betterx.wover.surface.api.noise`
  - `org.betterx.wover.surface.api.rules`

## How injection works

Vanilla stores a single `SurfaceRules.RuleSource` on `NoiseGeneratorSettings`, and `NoiseBasedChunkGenerator` uses
it for every biome in the dimension. If two mods both overwrote that field, the second one wins and the first
mod's surface rules silently disappear. `wover-surface-api` avoids this by keeping your rules in a separate,
datapack-loaded registry (keyed by biome, not by generator) and merging them into the *existing* rule tree once,
right before the level is created:

1. Rules are registered per-biome in the `AssignedSurfaceRule` registry (`wover/worldgen/surface_rules`), either
   from datapack JSON or from a `WoverRegistryContentProvider<AssignedSurfaceRule>` during datagen.
2. When a world is about to be created, WoVer walks every `LevelStem` in the `LEVEL_STEM` registry. For each stem
   whose `ChunkGenerator` implements
   [`InjectableSurfaceRules`](../../wover-common-api/src/main/java/org/betterx/wover/common/surface/api/InjectableSurfaceRules.java)
   (vanilla's `NoiseBasedChunkGenerator` does, via a WoVer mixin), it collects every registered rule whose biome is
   one of the biomes the generator's `BiomeSource` can produce, wraps them per-biome in
   `SurfaceRules.ifTrue(SurfaceRules.isBiome(...), sequence(...))`, and merges the result into the generator's
   `NoiseGeneratorSettings#surfaceRule()` — appending to the existing sequence (or wrapping it in one) rather than
   replacing it. In the Nether, the merge is careful to keep the existing roof/floor rules ahead of the injected
   per-biome rules.
3. The actual field write happens through
   [`SurfaceRuleProvider`](../../wover-common-api/src/main/java/org/betterx/wover/common/surface/api/SurfaceRuleProvider.java)
   (implemented on vanilla `NoiseGeneratorSettings` by a WoVer mixin). If your mod supplies its own
   `NoiseGeneratorSettings`-like object with a custom surface rule field, implement `SurfaceRuleProvider` yourself
   so WoVer merges into it instead of ignoring it — see the `wover-common-api` wiki page.

This is why the registry entries are called `AssignedSurfaceRule` rather than plain `RuleSource`s: every entry
carries the biome it applies to and a priority, so many mods can each contribute rules for the same biome and have
them combined predictably.

## For Datapack Developers

### The vanilla rule format (recap)

A `SurfaceRules.RuleSource` (as normally embedded in `data/<namespace>/worldgen/noise_settings/*.json` under
`"surface_rule"`) is a tree of condition/action nodes evaluated top-to-bottom for every column/block during
surface building:

- `"type": "minecraft:sequence"` — evaluate `"sequence": [...]` rules in order, use the first one that "applies".
- `"type": "minecraft:condition"` — `"if_true": <ConditionSource>`, `"then_run": <RuleSource>`.
- `"type": "minecraft:block"` — `"result_state": {"Name": "minecraft:stone"}`, a leaf rule that always applies.
- `"type": "minecraft:bandlands"` — the terracotta band generator used by badlands.
- Common `ConditionSource`s: `minecraft:biome` (`"biomes": [...]`), `minecraft:stone_depth` (with
  `surface_type: "floor"|"ceiling"`), `minecraft:y_above`/`minecraft:water`, `minecraft:steep`,
  `minecraft:hole`, `minecraft:noise_threshold`, `minecraft:vertical_gradient`, `minecraft:not`.

`wover-surface-api` does not replace this format — the `ruleSource` you write in its own JSON files (see below) is
exactly this vanilla `RuleSource` codec, plus a handful of extra `RuleSource`/`ConditionSource` types the module
registers into the vanilla `MATERIAL_RULE`/`MATERIAL_CONDITION` registries (see "Extra rule/condition types"
below).

### WoVer's surface-rule registry format

Instead of editing a whole `noise_settings` file (and fighting other mods for the `surface_rule` key), you add
files to a dedicated, datapack-loaded, per-biome registry:

```
data/<namespace>/wover/worldgen/surface_rules/<any_name>.json
```

Each file is one `AssignedSurfaceRule` with this shape (backed by the `AssignedSurfaceRuleImpl.CODEC`
`RecordCodecBuilder`):

```json
{
  "biome": "minecraft:plains",
  "priority": 1001,
  "ruleSource": {
    "type": "minecraft:condition",
    "if_true": {
      "type": "minecraft:stone_depth",
      "add_surface_depth": false,
      "offset": 0,
      "secondary_depth_range": 0,
      "surface_type": "floor"
    },
    "then_run": {
      "type": "minecraft:block",
      "result_state": { "Name": "minecraft:acacia_planks" }
    }
  }
}
```

- `biome` (required) — the `ResourceLocation` of the `Biome` this rule applies to. You can register several files
  for the same biome; all of them are collected and combined.
- `ruleSource` (required) — an ordinary `SurfaceRules.RuleSource`, using the vanilla codec (so any node type
  registered in `BuiltInRegistries.MATERIAL_RULE`/`MATERIAL_CONDITION`, vanilla or modded, works here).
- `priority` (optional, defaults to `PriorityLinkedList.DEFAULT_PRIORITY`, currently `1000`) — when a biome has
  multiple rules (possibly from different mods), they're sorted highest-priority-first into one
  `SurfaceRules.SequenceRuleSource` before injection.

At load, WoVer groups all entries by `biome`, sorts each biome's rules by `priority` (descending), wraps them as
`SurfaceRules.ifTrue(SurfaceRules.isBiome(<biome>), SurfaceRules.sequence(<sorted rules>))`, and merges that into
every dimension whose generator supports injection (see "How injection works" above) — you do not pick a
dimension or generator yourself, the rule follows the biome wherever it's used.

### Extra rule/condition types this module registers

These are additional `RuleSource`/`ConditionSource` node types you can reference by `"type"` from *any* surface
rule JSON (WoVer's registry format above, or a plain vanilla `noise_settings` file) once `wover-surface-api` is
present:

| `"type"` | Registered in | Fields |
|---|---|---|
| `wover:threshold_condition` | `MATERIAL_CONDITION` | `seed` (long), `threshold` (double, default `0`), `roughness` (`FloatProvider`, default `0`), `scale_x`, `scale_z` (double, default `0.1`) — true if a 2D `OpenSimplexNoise(seed)` sample at `(x*scale_x, z*scale_z)` plus a `roughness` sample exceeds `threshold`. |
| `wover:volume_threshold_condition` | `MATERIAL_CONDITION` | Same as above plus `scale_y` — evaluates the noise in 3D `(x*scale_x, y*scale_y, z*scale_z)`. |
| `wover:rough_noise_condition` | `MATERIAL_CONDITION` | `noise` (a `ResourceKey` into the vanilla `NOISE` registry, e.g. `minecraft:netherrack`), `roughness` (`FloatProvider`), `min_threshold`, `max_threshold` (double) — true if the referenced `NormalNoise` sample plus roughness falls in `[min_threshold, max_threshold]`. |
| `wover:switch_rule` | `MATERIAL_RULE` | `selector` (a `NumericProvider`, see below), `collection` (list of `RuleSource`) — picks `collection[selector.getNumber(ctx) % collection.size()]` and applies it. |

Example (`wover:switch_rule` alternating between two blocks per column, keyed by a random 0/1 provider):

```json
{
  "type": "wover:switch_rule",
  "selector": { "type": "wover:rnd_int", "range": 2 },
  "collection": [
    { "type": "minecraft:block", "result_state": { "Name": "minecraft:deepslate" } },
    { "type": "minecraft:block", "result_state": { "Name": "minecraft:blackstone" } }
  ]
}
```

### Numeric providers (`org.betterx.wover.core.api.registries` `wover/numeric_provider`)

`NumericProvider`s are small `int`-returning helpers usable anywhere a `wover:switch_rule` (or your own custom
rule) needs a selector. They live in their own registry (`NumericProviderRegistry`, in-code only, not
datapack-loaded) but are still referenced by `"type"` from JSON via their codec:

| `"type"` | Fields | Behavior |
|---|---|---|
| `wover:rnd_int` | `range` (int) | Returns a random `int` in `[0, range)`, seeded from `range` itself (deterministic per-range, not per-world-seed). |
| `wover:nether_noise` | *(none)* | Returns `0..5` derived from the same noise/random source as `Conditions.NETHER_VOLUME_NOISE`. |

### Custom noise parameters

If a `wover:rough_noise_condition` (or a vanilla `minecraft:noise_threshold` condition) references a noise key
that isn't one of vanilla's built-ins, it must be present in the `Registries.NOISE` registry. Mods normally do
this once during datagen (`WoverFullRegistryProvider<NormalNoise.NoiseParameters>`, see `NoiseParameterManager`
below); this module itself registers `wover:roughness_noise` this way.

## For Mod Developers

### Building and registering a surface rule: `SurfaceRuleBuilder`

[`SurfaceRuleBuilder`](../../wover-surface-api/src/main/java/org/betterx/wover/surface/api/SurfaceRuleBuilder.java)
gives you a fluent way to build a `RuleSource` for one biome and register it as an `AssignedSurfaceRule`, without
hand-writing `SurfaceRules.ifTrue(...)` trees. Typical layered-terrain usage (from the test mod's datagen):

```java
public class SurfaceRuleProvider extends WoverRegistryContentProvider<AssignedSurfaceRule> {
    public static final ResourceKey<AssignedSurfaceRule> TEST_PLAINS =
            SurfaceRuleRegistry.createKey(MyMod.C.id("test-plains"));

    public SurfaceRuleProvider(ModCore modCore) {
        super(modCore, "Test Surface Rules", SurfaceRuleRegistry.SURFACE_RULES_REGISTRY);
    }

    @Override
    protected void bootstrap(BootstrapContext<AssignedSurfaceRule> ctx) {
        SurfaceRuleBuilder.start()
                .biome(Biomes.PLAINS)
                .surface(Blocks.ACACIA_PLANKS.defaultBlockState())
                .sortPriority(1001)
                .register(ctx, TEST_PLAINS);
    }
}
```

Register the provider from your datagen entrypoint (see `wover-datagen-api`):

```java
public class MyModDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        globalPack.addRegistryProvider(SurfaceRuleProvider::new);
    }

    @Override
    protected ModCore modCore() {
        return MyMod.C;
    }
}
```

(`wover-biome-api` also ships a thin `WoverSurfaceRuleProvider` base class that pre-binds the registry key if you
don't need the generic `WoverRegistryContentProvider`.)

`SurfaceRuleBuilder` methods (inherited from `BaseSurfaceRuleBuilder`), each adding one rule at a fixed default
priority (highest number = applied first):

| Method | Priority constant | Purpose |
|---|---|---|
| `steep(state, depth)` | `STEEP_SURFACE_PRIORITY` (2900) | Cover steep terrain (e.g. mountainsides). |
| `surface(state\|block)` | `TOP_SURFACE_PRIORITY` (2800) | The top surface block. |
| `ceil(state)` | `CEILING_PRIORITY` (2700) | Block used for ceilings (air pockets/caves). |
| `subsurface(state\|block, depth)` | `SUB_SURFACE_PRIORITY` (2600) | Layer just under the surface (e.g. dirt). |
| `floor(state)` | `FLOOR_PRIORITY` (2500) | Floor surface (e.g. cave floors). |
| `chancedFloor(a, b[, noise])` | `FLOOR_PRIORITY` | Floor that alternates between two blocks/rule sources, using `Conditions.DOUBLE_BLOCK_SURFACE_NOISE` by default. |
| `belowFloor(state, height[, noise])` | `BELOW_FLOOR_PRIORITY` (2400) | Material below the floor for `height` blocks. |
| `aboveCeil(state, height)` | `ABOVE_CEILING_PRIORITY` (2300) | Material above a ceiling for `height` blocks. |
| `filler(state)` | `FILLER_PRIORITY` (900) | Fallback fill material, lower priority than everything above. |
| `rule(ruleSource[, priority])` | `PriorityLinkedList.DEFAULT_PRIORITY` (1000) by default | Add an arbitrary `SurfaceRules.RuleSource` (e.g. built with vanilla `SurfaceRules.*` helpers, `Rules.switchRules(...)`, or your own). |
| `sortPriority(priority)` | — | Overrides the priority the *whole builder's* rule set is registered with (used when calling `register`, orthogonal to the per-rule priorities above). |

Call `.build()` instead of `.register(ctx, key)` if you just want the assembled `RuleSource` without registering
it (e.g. to embed it directly in a `NoiseGeneratorSettings` you build yourself).

If you don't want the builder, register a hand-built `RuleSource` directly:

```java
SurfaceRuleRegistry.register(ctx, TEST_KEY, Biomes.DESERT,
        Rules.switchRules(NumericProviders.randomInt(2), List.of(
                SurfaceRules.state(Blocks.DEEPSLATE.defaultBlockState()),
                SurfaceRules.state(Blocks.BLACKSTONE.defaultBlockState())
        )),
        1500 /* priority, optional */);
```

### Registering at runtime instead of datagen

Datagen is preferred (see above), but if you must add rules at runtime, subscribe to
`SurfaceRuleRegistry.BOOTSTRAP_SURFACE_RULE_REGISTRY` — it fires whenever the registry is (re-)loaded from
datapacks, with the same kind of `BootstrapContext` datagen uses:

```java
if (!ModCore.isDatagen()) {
    var key = SurfaceRuleRegistry.createKey(MyMod.C.id("test-savanna"));
    SurfaceRuleRegistry.BOOTSTRAP_SURFACE_RULE_REGISTRY.subscribe(ctx -> {
        SurfaceRuleBuilder.start()
                .biome(Biomes.SAVANNA)
                .chancedFloor(Blocks.RED_TERRACOTTA.defaultBlockState(), Blocks.RED_CONCRETE.defaultBlockState())
                .register(ctx, key);
    });
}
```

### Custom noise-based conditions in Java

Beyond the JSON-configurable conditions, you can implement your own noise condition directly:

- Extend [`SurfaceNoiseCondition`](../../wover-surface-api/src/main/java/org/betterx/wover/surface/api/conditions/SurfaceNoiseCondition.java)
  for a condition evaluated once per X/Z column (lazily cached, like vanilla's `LazyXZCondition`).
- Extend [`VolumeNoiseCondition`](../../wover-surface-api/src/main/java/org/betterx/wover/surface/api/conditions/VolumeNoiseCondition.java)
  for a condition evaluated per 3D block position (like vanilla's `LazyCondition`).

Both only require implementing `boolean test(SurfaceRulesContext context)` (from the shared
[`NoiseCondition`](../../wover-surface-api/src/main/java/org/betterx/wover/surface/api/conditions/NoiseCondition.java)
interface); [`SurfaceRulesContext`](../../wover-surface-api/src/main/java/org/betterx/wover/surface/api/conditions/SurfaceRulesContext.java)
exposes the block position, current biome, chunk/noise chunk, stone depth above/below and the `RandomState`. Use
[`Conditions`](../../wover-surface-api/src/main/java/org/betterx/wover/surface/api/Conditions.java)'s
`threshold(...)`/`volumeThreshold(...)` factory methods (and its ready-made constants like
`Conditions.NETHER_VOLUME_NOISE`, `Conditions.FORREST_FLOOR_SURFACE_NOISE_A/B`) if you just need an
`OpenSimplexNoise`-backed threshold check rather than a fully custom condition.

If you want your condition/rule usable from JSON too, register its `MapCodec` with
[`ConditionManager`](../../wover-surface-api/src/main/java/org/betterx/wover/surface/api/conditions/ConditionManager.java)
(for `SurfaceRules.ConditionSource`s) or
[`MaterialRuleManager`](../../wover-surface-api/src/main/java/org/betterx/wover/surface/api/rules/MaterialRuleManager.java)
(for `SurfaceRules.RuleSource`s) — both are thin wrappers around vanilla's `BuiltInRegistries.MATERIAL_CONDITION`/
`MATERIAL_RULE`.

### Custom numeric providers

Implement [`NumericProvider`](../../wover-surface-api/src/main/java/org/betterx/wover/surface/api/noise/NumericProvider.java)
(`int getNumber(SurfaceRulesContext)` + a `pcodec()`) and register it with
[`NumericProviderRegistry.register(...)`](../../wover-surface-api/src/main/java/org/betterx/wover/surface/api/noise/NumericProviderRegistry.java)
if you want a custom selector for `Rules.switchRules(...)` or your own rule types. Built-ins live in
[`NumericProviders`](../../wover-surface-api/src/main/java/org/betterx/wover/surface/api/noise/NumericProviders.java)
(`randomInt(bound)`, `netherNoise()`).

### Custom `NormalNoise` parameters

[`NoiseParameterManager`](../../wover-surface-api/src/main/java/org/betterx/wover/surface/api/noise/NoiseParameterManager.java)
creates keys in / resolves instances from the vanilla `Registries.NOISE` registry:

```java
ResourceKey<NormalNoise.NoiseParameters> MY_NOISE = NoiseParameterManager.createKey(MyMod.C.id("my_noise"));
// ... register the actual NoiseParameters value during datagen, then at runtime:
NormalNoise noise = NoiseParameterManager.getOrCreateNoise(registryAccess, randomSource, MY_NOISE);
```

### Making a custom `BiomeSource`/`ChunkGenerator` support injection

`wover-surface-api` only injects into generators that implement
[`InjectableSurfaceRules`](../../wover-common-api/src/main/java/org/betterx/wover/common/surface/api/InjectableSurfaceRules.java)
and settings objects that implement `SurfaceRuleProvider` (both from `wover-common-api`). Vanilla's
`NoiseBasedChunkGenerator`/`NoiseGeneratorSettings` get these via WoVer's own mixins
(`NoiseBasedChunkGeneratorMixin`, `NoiseGeneratorSettingsMixin`); if you ship a fully custom `ChunkGenerator` or a
settings object that isn't `NoiseGeneratorSettings`, implement both interfaces yourself so WoVer's injection pass
(fired from `WorldLifecycle.BEFORE_CREATING_LEVELS` at priority 500) can find and merge into it — see the
`wover-common-api` wiki page for the interface contracts.
