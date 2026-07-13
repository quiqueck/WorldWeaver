# WorldWeaver (WoVer) Wiki

WorldWeaver, or **WoVer** for short, is a Fabric library mod for Minecraft that turns common world-generation and
modding tasks into **datapack-driven** interfaces, while still giving mod developers a full Java API to build on
top of. It's the spiritual successor to BCLib.

This wiki is split by audience:

- **[Datapack Developer Guide](datapack-developer-guide.md)** — you're authoring a resource/datapack (JSON files,
  maybe with a small `fabric.mod.json` shell) and want to use WoVer's extra registries and JSON formats without
  writing Java.
- **[Mod Developer Guide](mod-developer-guide.md)** — you're writing a Fabric mod in Java/Kotlin and want to depend
  on WoVer's Gradle modules to build blocks, items, biomes, features, structures, recipes, etc. programmatically
  (and/or generate the datapack JSON for you via datagen).

Each module also has its own detailed reference page under [`modules/`](modules/), written for both audiences.

## Modules, in dependency order

WoVer is split into small Gradle modules so mods can depend on only what they need. This is also the order in
which they should be upgraded/understood (each depends only on modules above it) — see the repo's
[Notes.md](../../Notes.md) for the authoritative dependency graph.

| Module | Artifact | Depends on | What it's for | Datapack surface? |
|---|---|---|---|---|
| [common-api](modules/common-api.md) | `wover-common-api` | *(none)* | Shared interfaces implemented by other WoVer modules (mixins into vanilla classes) | No |
| [core-api](modules/core-api.md) | `wover-core-api` | common | Mod entrypoint plumbing, config system, datapack-registry helpers | No (infrastructure) |
| [math-api](modules/math-api.md) | `wover-math-api` | core | Noise (OpenSimplex, Voronoi), random and math helpers | No |
| [datagen-api](modules/datagen-api.md) | `wover-datagen-api` | core | Shared machinery every other module uses to generate its datapack JSON at build time | No (build-time tool) |
| [event-api](modules/event-api.md) | `wover-event-api` | core | Generic event bus + `WorldState`/`WorldLifecycle` world-loading events | No |
| [ui-api](modules/ui-api.md) | `wover-ui-api` | core, event | Client update checker + config-screen helpers | No |
| [tag-api](modules/tag-api.md) | `wover-tag-api` | core, datagen, event | Tag helpers + predefined common tags | **Yes** — `tags/<registry>/*.json` |
| [item-api](modules/item-api.md) | `wover-item-api` | core, tag, event | Item/creative-tab/enchantment builders | **Yes** — enchantment, loot, item-tag JSON |
| [block-api](modules/block-api.md) | `wover-block-api` | core, tag, item | Block builders, loot tables, POI, block models | **Yes** — loot table JSON, tags |
| [recipe-api](modules/recipe-api.md) | `wover-recipe-api` | core, event, block, item | Crafting/cooking/smithing recipe builders, brewing | **Yes** — `recipe/*.json` |
| [sets-api](modules/sets-api.md) | `wover-sets-api` | core, block, item, recipe | High-level "material set" builder (wood/stone-type block+item+recipe families) | **Yes** (generated) |
| [preset-api](modules/preset-api.md) | `wover-preset-api` | core, tag, event | World Preset ("Create World" screen) registration | **Yes** — `world_preset/*.json` + WoVer's `world_preset_info` |
| [surface-api](modules/surface-api.md) | `wover-surface-api` | common, datagen, core, math, event | Injectable surface rules merged into any chunk generator | **Yes** — `wover/worldgen/surface_rules/*.json` |
| [structure-api](modules/structure-api.md) | `wover-structure-api` | core, math, event, block | Structure / structure-set / jigsaw pool registration | **Yes** — `structure/*.json`, `structure_set/*.json` |
| [feature-api](modules/feature-api.md) | `wover-feature-api` | core, event, surface, block, structure | Configured/placed feature registration | **Yes** — `configured_feature/*.json`, `placed_feature/*.json` |
| [biome-api](modules/biome-api.md) | `wover-biome-api` | core, event, feature | Biome registration + runtime biome modification | **Yes** — `worldgen/biome/*.json`, `BiomeData` |
| [generator-api](modules/generator-api.md) | `wover-generator-api` | core, event, surface, biome, preset, ui, tag | Custom Nether/End-style `ChunkGenerator`/`BiomeSource` | **Yes** — `dimension`, `chunk_generator`, `biome_source` JSON |
| [pottable-api](modules/pottable-api.md) | `wover-pottable-api` | core, block, tag, event, datagen | Registry for pottable plants/soils (flower pots) | **Yes** — `wover/pottable_plant`, `wover/pottable_soil` |

> **Note:** `wover-pottable-api` currently only stores registry *data* — nothing in the current codebase wires that
> data into vanilla's flower-pot mechanic yet. See its [module page](modules/pottable-api.md) for details.

## Installing WoVer in your project

See the root [README.md](../../README.md) for the Gradle snippet to add the WoVer Maven repository and a
module dependency, plus the recommended `fabric.mod.json` version-range entries.

## AI agent skills

If you're an AI coding agent (or configuring one), see [`public/skills/`](../skills/) for two ready-to-use skill
definitions: authoring WoVer-flavored datapacks, and building a WoVer-based Fabric mod.
