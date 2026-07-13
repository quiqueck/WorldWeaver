---
name: worldweaver-datapack-dev
description: Author datapack JSON for a Minecraft Fabric project that uses the WorldWeaver (WoVer) library — biomes, features, structures, surface rules, tags, recipes, world presets, custom dimensions, biome modifications. Use when creating/editing files under data/<namespace>/... in a project depending on any wover-*-api module, or when asked to add worldgen/recipe/tag content via JSON instead of Java.
---

# WorldWeaver Datapack Development

WoVer adds several of its *own* registries on top of vanilla Minecraft's, always loaded as ordinary datapack JSON
under `data/<namespace>/...`. This skill is a lookup table for those formats plus the vanilla formats they build
on. It assumes the target world/server already has the relevant `wover-*` mod jars installed — a datapack alone
cannot add WoVer content to a server that doesn't have the mod.

**Full reference:** [`public/wiki/datapack-developer-guide.md`](../../wiki/datapack-developer-guide.md) and the
per-module pages under [`public/wiki/modules/`](../../wiki/modules/) in the WorldWeaver repo — read the matching
module page before writing a non-trivial file of a type you haven't used before, it has the complete field list
and a worked example pulled from real generated JSON.

## Decision guide: which file do I need?

| I want to... | File(s) | Module doc |
|---|---|---|
| Add a crafting/smelting/smithing/stonecutting recipe | `data/<ns>/recipe/<path>.json` (vanilla format) | [recipe-api](../../wiki/modules/recipe-api.md) |
| Add or extend a tag (item/block/biome/...) | `data/<ns>/tags/<registry>/<path>.json` (vanilla format) | [tag-api](../../wiki/modules/tag-api.md) |
| Add a loot table for a block | `data/<ns>/loot_table/<path>.json` (vanilla format) | [block-api](../../wiki/modules/block-api.md) |
| Define a new biome | `data/<ns>/worldgen/biome/<path>.json` (vanilla format) | [biome-api](../../wiki/modules/biome-api.md) |
| Add fog/extra metadata to a biome you defined | `data/<ns>/wover/worldgen/biome_data/<path>.json` **(WoVer)** | [biome-api](../../wiki/modules/biome-api.md) |
| Add features/spawns to an **existing** (including vanilla) biome, without overwriting its file | `data/<ns>/wover/worldgen/biome_modifications/<path>.json` **(WoVer)** | [biome-api](../../wiki/modules/biome-api.md) |
| Define a configured/placed feature | `data/<ns>/worldgen/configured_feature/<path>.json`, `placed_feature/<path>.json` (vanilla + WoVer feature types like `wover:pillar`, `wover:place_block`, `wover:sequence`, `wover:condition`) | [feature-api](../../wiki/modules/feature-api.md) |
| Define a per-biome surface rule (what block appears at the surface) | `data/<ns>/wover/worldgen/surface_rules/<path>.json` **(WoVer)** — embeds a vanilla `SurfaceRules.RuleSource` plus a `biome` selector and `priority` | [surface-api](../../wiki/modules/surface-api.md) |
| Define a structure / structure set / jigsaw pool | `data/<ns>/worldgen/structure/<path>.json`, `structure_set/<path>.json`, `template_pool/<path>.json`, `processor_list/<path>.json` (vanilla + WoVer's `wover:random_nbt_structure` type) | [structure-api](../../wiki/modules/structure-api.md) |
| Add a world type to the "Create World" screen | `data/<ns>/worldgen/world_preset/<path>.json` (vanilla) **+** `data/<ns>/wover/world_preset_info/<path>.json` **(WoVer — controls sort order and per-dimension linkage)** | [preset-api](../../wiki/modules/preset-api.md) |
| Build a custom Nether/End-style dimension | `data/<ns>/dimension/<path>.json` using `"type": "wover:betterx"` chunk generator with `"wover:nether_biome_source"` / `"wover:end_biome_source"` | [generator-api](../../wiki/modules/generator-api.md) |
| Register a block as usable in a decorated pot | `data/<ns>/wover/pottable_plant/<path>.json`, `wover/pottable_soil/<path>.json` **(WoVer — currently inert, see caveat)** | [pottable-api](../../wiki/modules/pottable-api.md) |

## Conventions

- `<ns>` is your own mod's namespace (e.g. `mymod`), **not** `wover` or `minecraft`, unless you're intentionally
  overriding/extending WoVer's or vanilla's own files.
- WoVer's own registries live one level deeper under a `wover/` path segment to avoid colliding with vanilla
  folders of the same registry name (e.g. `wover/worldgen/biome_data/`, not `worldgen/biome_data/`) — always
  check the exact path in the module doc, this isn't perfectly uniform across modules.
- IDs referenced inside JSON (e.g. a `"biome"` field pointing at a biome, or a `"type"` field selecting a WoVer
  feature/condition/rule kind) use the standard `namespace:path` resource-location format.
- New blocks/items themselves are **not** fully datapack-definable in vanilla Minecraft — if your task needs a
  brand-new block or item (not just recipes/tags/loot for an existing one), that requires Java; see
  [`worldweaver-mod-dev`](../worldweaver-mod-dev/SKILL.md) instead (or in addition).

## Before writing a non-trivial file

1. Open the relevant module page under `public/wiki/modules/` — it has the full, verified field list and at least
   one real example JSON pulled from this repo's own generated datapack output, not a guess.
2. If a similar file already exists in the target project, prefer copying its structure over inventing one from
   scratch.
3. Double-check registry names and `"type"` values against the module doc rather than assuming vanilla naming —
   several WoVer types intentionally reuse vanilla's registry (e.g. custom `SurfaceRules.RuleSource`/
   `SurfaceRules.ConditionSource` types) under the `wover:` namespace.
