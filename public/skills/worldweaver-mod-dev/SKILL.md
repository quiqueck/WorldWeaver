---
name: worldweaver-mod-dev
description: Write Java for a Minecraft Fabric mod that depends on the WorldWeaver (WoVer) library — registering blocks, items, biomes, features, structures, recipes, tags, world presets, or custom dimensions via WoVer's builder APIs, plus generating their datapack JSON via datagen. Use when editing Java in a project that depends on any wover-*-api Gradle module, or when asked to add game content programmatically instead of by hand-writing JSON.
---

# WorldWeaver Mod Development

WoVer is a set of Fabric library modules (`wover-common-api`, `wover-core-api`, `wover-block-api`, ...) that give
you a fluent Java builder API for game content, and a matching `fabric-datagen` provider that emits the datapack
JSON for you. This skill is a cheat sheet of entry points; for full method-by-method docs and a worked example
per module, see [`public/wiki/mod-developer-guide.md`](../../wiki/mod-developer-guide.md) and the pages under
[`public/wiki/modules/`](../../wiki/modules/) in the WorldWeaver repo — **read the matching module page before
writing non-trivial registration code**, it has real, compiling examples pulled from this repo's own test mods.

## Setup

```groovy
// build.gradle
repositories { maven { url 'https://maven.ambertation.de/releases' } }
dependencies {
    modImplementation "org.betterx:wover-block-api:${wover_version}" // + whichever modules you need
}
```

```json
// fabric.mod.json
"depends": { "worldweaver": "21.7.x" }
```

Every builder API takes a `ModCore` — create exactly one per mod, in your `ModInitializer`:

```java
public static final ModCore C = ModCore.create("mymod", "mymod");
```

## Entry point cheat sheet

| Task | Start here | Module |
|---|---|---|
| Register a block | `BlockRegistry.forMod(C).defineXxx(...)` (see [block-api](../../wiki/modules/block-api.md) for the exact `defineXxx` variants) | `wover-block-api` |
| Register an item | `ItemRegistry.forMod(C).defineXxxItem(...)` (e.g. `defineArmorItem`, `defineBoatItem`, `defineSmithingTemplate`) | `wover-item-api` |
| Register a creative tab | `CreativeTabs` (`org.betterx.wover.tabs.api`) | `wover-item-api` |
| Create/use a tag | `TagManager` (`org.betterx.wover.tag.api`) | `wover-tag-api` |
| Register a recipe (crafting/cooking/smithing/stonecutting) | `RecipeBuilder.crafting(...)`, `.cooking(...)`, `.smithing(...)`, `.stonecutting(...)`, `.blasting(...)`, `.smoker(...)`, `.campfire(...)` | `wover-recipe-api` |
| Build a whole "material set" (wood/stone-type family of blocks+items+recipes) | `WoodenBlockSet`/`BlockSet`/`EquipmentSet` builders (`org.betterx.wover.sets.api.types`, `.equipment`) | `wover-sets-api` |
| Register a biome | `BiomeManager` + `BiomeBuilder` (`org.betterx.wover.biome.api`) | `wover-biome-api` |
| Modify an existing/vanilla biome at load time | `BiomeModificationRegistry` / `BiomeModification.Builder` | `wover-biome-api` |
| Register a configured/placed feature | `ConfiguredFeatureManager`, `PlacedFeatureManager`, `FeaturePlacementBuilder` | `wover-feature-api` |
| Register a structure / structure set / jigsaw pool | `StructureManager` + `StructureBuilder`/`JigsawBuilder`/`RandomNbtBuilder` | `wover-structure-api` |
| Register a per-biome surface rule | `SurfaceRuleRegistry` + `SurfaceRuleBuilder`, condition helpers in `Conditions`/`Rules` | `wover-surface-api` |
| Register a World Preset ("Create World" entry) | `WorldPresetManager` + `WorldPresetInfoBuilder` | `wover-preset-api` |
| Build a custom Nether/End-style dimension generator | `BiomeSourceManager`, `ChunkGeneratorManager`, `WoverBiomeBuilder` | `wover-generator-api` |
| Subscribe to a world-loading lifecycle stage | `WorldState`/`WorldLifecycle` (`org.betterx.wover.state.api` / `.events.api`) | `wover-event-api` |
| Register/read a config value | `Configs`/`DatapackConfigs` (server), `ClientConfigs` (client, from `wover-ui-api`) | `wover-core-api` / `wover-ui-api` |

## Datagen

Every module with a datapack surface ships a `Wover*Provider` base class for `fabric-datagen` (e.g.
`WoverRecipeProvider`, `WoverTagProvider`, `WoverModelProvider`, `WoverStructureProvider`, `WoverEnchantmentProvider`,
`WoverLootTableProvider`, `WoverWorldPresetProvider`). Register your `DataGeneratorEntrypoint` under the
`fabric-datagen` key in `fabric.mod.json`, then register providers through `PackBuilder`
(`org.betterx.wover.datagen.api`, from `wover-datagen-api`) — see [datagen-api.md](../../wiki/modules/datagen-api.md)
for the exact registration call and [core-api.md](../../wiki/modules/core-api.md) for how registry bootstrap
(`DatapackRegistryBuilder`) ties into it.

## Real examples in this repo

Every `wover-*-api` module ships a `src/testmod` (and often `src/testmodDatagen`, `src/testmodClient`) source set
that is a minimal, compiling, real usage example. When a method signature or usage pattern is unclear, check that
module's testmod directory before guessing.

## Low-level escape hatch

If you need to write a `BiomeSource`/`ChunkGenerator` completely from scratch instead of using
`wover-generator-api`'s builders, implement the interfaces in `wover-common-api`
(`BiomeSourceWithConfig`/`MergeableBiomeSource`/`EnforceableChunkGenerator`/etc.) so WoVer's event, merge, and
repair machinery still recognizes it — see [common-api.md](../../wiki/modules/common-api.md).
