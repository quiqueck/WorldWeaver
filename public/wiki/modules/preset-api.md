# wover-preset-api

Java API for `WorldPreset`s (the entries in the "Create World" world-type list, e.g. Default/Superflat/Amplified/
Large Biomes) and `FlatLevelGeneratorPreset`s (the presets shown in the Superflat customization screen).
`wover-preset-api` does not invent a new datapack format for either registry — both stay plain vanilla worldgen
registries. On top of that it adds one small WoVer-only registry (`WorldPresetInfo`) that carries metadata vanilla
has no room for (sort order in the UI, "reuse another preset's dimension" overrides), plus Java helpers to build,
register, tag and (client-side) customize the UI for both registries without hand-writing JSON.

- **Gradle artifact:** `org.betterx:wover-preset-api`
- **Depends on:** `wover-core-api`, `wover-tag-api`, `wover-event-api`
- **Java packages:**
  - `org.betterx.wover.preset.api`
  - `org.betterx.wover.preset.api.context`
  - `org.betterx.wover.preset.api.event`
  - `org.betterx.wover.preset.api.flat`
  - `org.betterx.wover.preset.api.client` (client only)
  - `org.betterx.wover.datagen.api.provider` (adds `WoverWorldPresetProvider`/`WoverFlatLevelPresetProvider` to the
    datagen module's package)

## For Datapack Developers

### `WorldPreset` — `data/<namespace>/worldgen/world_preset/<path>.json`

This is the unmodified vanilla format (`WorldPreset.DIRECT_CODEC`): a map of dimension key to `LevelStem`. An
overworld entry is required; nether/end are optional.

```json
{
  "dimensions": {
    "minecraft:overworld": {
      "type": "minecraft:overworld",
      "generator": { "...": "a ChunkGenerator, e.g. minecraft:noise with a biome_source and settings" }
    },
    "minecraft:the_nether": {
      "type": "minecraft:the_nether",
      "generator": { "...": "..." }
    },
    "minecraft:the_end": {
      "type": "minecraft:the_end",
      "generator": { "...": "..." }
    }
  }
}
```

### `FlatLevelGeneratorPreset` — `data/<namespace>/worldgen/flat_level_generator_preset/<path>.json`

Also unmodified vanilla format (`FlatLevelGeneratorPreset.DIRECT_CODEC`/`FlatLevelGeneratorSettings.CODEC`):

```json
{
  "display": "minecraft:nether_bricks",
  "settings": {
    "biome": "minecraft:nether_wastes",
    "lakes": false,
    "features": false,
    "structure_overrides": ["minecraft:nether_complex"],
    "layers": [
      { "height": 12, "block": "minecraft:netherrack" },
      { "height": 2, "block": "minecraft:nether_bricks" }
    ]
  }
}
```

`structure_overrides` (a homogeneous tag/list of `StructureSet`s) and `biome` are both optional; `lakes` and
`features` default to `false`.

### WoVer's own registry: `WorldPresetInfo` — `data/<namespace>/wover/world_preset_info/<path>.json`

`WorldPresetInfoRegistry.WORLD_PRESET_INFO_REGISTRY` is a separate datapack registry (created with
`DatapackRegistryBuilder.createRegistryKey(...)`, registry id `wover:wover/world_preset_info`), so files live under
the `wover/world_preset_info` folder of any namespace, not under `worldgen/`. An entry describes metadata for a
`WorldPreset` **of the same id** — e.g. an info file at `data/mymod/wover/world_preset_info/my_preset.json`
describes the `mymod:my_preset` world preset. A preset with no matching info entry falls back to defaults
(sort order `1000`, no overrides). This is the JSON schema backing `WorldPresetInfoImpl.CODEC`:

```json
{
  "sort_order": 2000,
  "overworld_preset": "minecraft:normal",
  "nether_preset": "minecraft:normal",
  "end_preset": "minecraft:normal"
}
```

- `sort_order` (int, optional, default `1000`) — position in the "Create World" preset list; lower sorts first.
  This is the value vanilla's `WorldPresets.AMPLIFIED`/`LARGE_BIOMES`/... entries are given by WoVer's own
  `WorldPresetInfoProvider` (`NORMAL` → `1000`, `AMPLIFIED` → `2000`, `LARGE_BIOMES` → `3000`, `FLAT` → `11000`,
  `SINGLE_BIOME_SURFACE` → `12000`).
- `overworld_preset` / `nether_preset` / `end_preset` (`ResourceKey<WorldPreset>`, optional) — instead of using
  this preset's own overworld/nether/end dimension, tools that care about "which preset produced this dimension"
  (e.g. `wover-generator-api`) resolve the override chain and treat the referenced preset's dimension as
  authoritative. Vanilla's `FLAT` and `SINGLE_BIOME_SURFACE` presets set all three overrides to `minecraft:normal`
  since they don't have "real" nether/end dimensions of their own; `AMPLIFIED` overrides only nether/end.

### Tags

Both registries are taggable through `wover-tag-api` (see its wiki page for the general tag JSON format):

| Registry | Tag | Purpose |
|---|---|---|
| `WorldPreset` | `WorldPresetTags.NORMAL` (`minecraft:normal`, i.e. vanilla's `WorldPresetTags.NORMAL`) | Presets in this tag are shown as regular entries on the "Create World" screen. |
| `WorldPreset` | `WorldPresetTags.EXTENDED` (`minecraft:extended`) | All vanilla presets are in this tag as well. |
| `FlatLevelGeneratorPreset` | `FlatLevelPresetTags.VISIBLE` (`minecraft:visible`) | Presets in this tag show up on the Superflat customization screen. |

A preset that exists but isn't added to the relevant "visible/normal" tag is still a valid, loadable preset — it
just won't show up as a selectable option in the vanilla UI.

## For Mod Developers

### Registering a `WorldPreset` (data generator, recommended)

Subclass [`WoverWorldPresetProvider`](../../wover-preset-api/src/main/java/org/betterx/wover/datagen/api/provider/WoverWorldPresetProvider.java)
and implement `bootstrap(WorldPresetBootstrapContext)` and `prepareTags(TagBootstrapContext<WorldPreset>)`. The
context gives you ready-made default `LevelStem`s (`overworldStem`, `netherStem`, `endStem`) and registry lookups
(`noiseSettings`, `biomes`, `placedFeatures`, `structureSets`, `parameterLists`) so you rarely have to build a
`ChunkGenerator` from scratch:

```java
public class PresetProvider extends WoverWorldPresetProvider {
    public PresetProvider(ModCore modCore) {
        super(modCore, "My World Presets");
    }

    @Override
    protected void bootstrap(WorldPresetBootstrapContext context) {
        var preset = WorldPresetManager.fromStems(
                context.overworldStem,
                context.netherStem,
                context.endStem
        );
        context.register(MyMod.NETHER_START, preset);
    }

    @Override
    protected void prepareTags(TagBootstrapContext<WorldPreset> provider) {
        provider.add(WorldPresetTags.NORMAL, MyMod.NETHER_START);
    }
}
```

Register the provider from your datagen entrypoint:

```java
public class MyModDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        globalPack.addRegistryProvider(PresetProvider::new);
    }

    @Override
    protected ModCore modCore() {
        return MyMod.C;
    }
}
```

Create the preset's `ResourceKey` up front with `WorldPresetManager.createKey(ResourceLocation)`.

### Registering a `WorldPreset` at runtime (discouraged)

If you can't precompute the preset in datagen, subscribe to `WorldPresetManager.BOOTSTRAP_WORLD_PRESETS` (and tag
it via `WorldPresetTags.TAGS.bootstrapEvent()`), mirroring what the data generator path does:

```java
WorldPresetManager.BOOTSTRAP_WORLD_PRESETS.subscribe(ctx -> {
    var preset = WorldPresetManager.fromStems(ctx.overworldStem, ctx.netherStem, ctx.endStem);
    ctx.register(MyMod.END_START, preset);
});

WorldPresetTags.TAGS.bootstrapEvent().subscribe(ctx -> {
    ctx.add(WorldPresetTags.NORMAL, MyMod.END_START);
});
```

### Registering a `FlatLevelGeneratorPreset`

Same pattern, via [`WoverFlatLevelPresetProvider`](../../wover-preset-api/src/main/java/org/betterx/wover/datagen/api/provider/WoverFlatLevelPresetProvider.java)
for datagen, or `FlatLevelPresetManager.BOOTSTRAP_FLAT_LEVEL_PRESETS`/`FlatLevelPresetTags.TAGS.bootstrapEvent()` at
runtime. Both expose the same `register(...)` helper (icon, biome, allowed structure sets, decorations/lakes
flags, and the flat layers, bottom layer first):

```java
FlatLevelPresetManager.BOOTSTRAP_FLAT_LEVEL_PRESETS.subscribe(ctx -> {
    ctx.register(
            MyMod.FLAT_NETHER,
            Blocks.NETHER_BRICKS,        // icon
            Biomes.NETHER_WASTES,
            new HashSet<>(0),            // allowed structure sets
            false,                       // addDecorations
            false,                       // addLakes
            new FlatLayerInfo(12, Blocks.NETHERRACK),
            new FlatLayerInfo(2, Blocks.NETHER_BRICKS)
    );
});

FlatLevelPresetTags.TAGS.bootstrapEvent().subscribe(ctx -> {
    ctx.add(FlatLevelPresetTags.VISIBLE, MyMod.FLAT_NETHER);
});
```

`FlatLevelPresetManager.createKey(ResourceLocation)` creates the preset's `ResourceKey`.

### Registering `WorldPresetInfo` (sort order / dimension overrides)

Build one with [`WorldPresetInfoBuilder`](../../wover-preset-api/src/main/java/org/betterx/wover/preset/api/WorldPresetInfoBuilder.java)
inside a `BootstrapContext<WorldPresetInfo>` (e.g. a `WoverRegistryContentProvider<WorldPresetInfo>` data
generator registered against `WorldPresetInfoRegistry.WORLD_PRESET_INFO_REGISTRY`, the same way WoVer's own
`WorldPresetInfoProvider` seeds vanilla's presets):

```java
WorldPresetInfoBuilder.start(context)
        .order(2000)
        .netherOverride(WorldPresets.NORMAL)
        .endOverride(WorldPresets.NORMAL)
        .register(MyMod.AMPLIFIED_LIKE_PRESET);
```

`.build()` returns a `WorldPresetInfo` without registering it, if you need it standalone. Look one up at runtime
with `WorldPresetInfoRegistry.getFor(ResourceKey<WorldPreset>)` (also overloaded for `Holder<WorldPreset>` and
`WorldPreset`) — presets without a registered entry get a default `WorldPresetInfo`.

### Choosing/marking a default preset

`WorldPresetManager.suggestDefault(ResourceKey<WorldPreset> preset, int priority)` proposes a preset as the one
pre-selected on the "Create World" screen and used when the server generates a fresh `server.properties`. The
suggestion with the highest priority wins across all mods:

```java
WorldPresetManager.suggestDefault(MyMod.END_START, 1000);
```

### Other `WorldPresetManager` helpers

`WorldPresetManager` also has: `createKey(ResourceLocation)`, `get(RegistryAccess, ResourceKey<WorldPreset>)`,
`getDefault()`, `fromStems(overworld, nether, end)`, `of(Map<ResourceKey<LevelStem>, LevelStem>)`,
`withDimensions(WorldDimensions)`, and `getDimensions(Holder<WorldPreset>)`/`getDimension(Holder<WorldPreset>,
ResourceKey<LevelStem>)` to read the `LevelStem`s back out of a `WorldPreset` holder.

### Custom "Customize" screens (client only)

[`WorldPresetsUI`](../../wover-preset-api/src/client/java/org/betterx/wover/preset/api/client/WorldPresetsUI.java)
lets you register a `PresetEditor` (the screen shown for "Customize" on the Create World screen) for one of your
presets:

```java
WorldPresetsUI.registerCustomUI(MyMod.END_START, new PresetEditor() {
    @Override
    public Screen createEditScreen(CreateWorldScreen createWorldScreen, WorldCreationContext context) {
        return new MyPresetEditScreen(createWorldScreen);
    }
});
```

For providers that need to inspect the preset to decide on an editor (rather than binding to one fixed key), use
the `WorldPresetsUI.PresetEditorGetter` overload of `registerCustomUI` together with
`WorldPresetsUI.isKey(Holder<WorldPreset>, ResourceKey<WorldPreset>)`.

## Real usage

See `wover-preset-api/src/testmod` (`TestModWoverWorldPreset`, runtime registration + `suggestDefault`),
`src/testmodDatagen` (`PresetProvider`, datagen registration) and `src/testmodClient`
(`TestModWoverWorldPresetClient`, custom `PresetEditor`s) for complete, compiling examples of everything above.
