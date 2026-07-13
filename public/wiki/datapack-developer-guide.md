# Datapack Developer Guide

WoVer's whole design philosophy is: prefer a **datapack JSON file** over Java code wherever the game already
supports (or can be made to support) data-driven content. Several WoVer modules add their *own* registries,
loaded exactly like vanilla registries, sitting alongside vanilla's JSON in `data/<namespace>/...`.

This guide is a map of every JSON file type WoVer adds or extends. For the full field-by-field format of any one
of them, follow the link to that module's page — this page only orients you.

## Requirements

- The world (or server) needs the WoVer mod(s) that own a given registry installed — these are ordinary Fabric
  mods, so a pure datapack cannot add WoVer content to a vanilla server without the mod's jar present. Your
  datapack can, however, ship inside a mod's `resources` folder as usual, or as a standalone pack loaded by a
  server that has the WoVer mod(s) installed.
- Vanilla folder conventions apply throughout: `data/<namespace>/...`. WoVer's *own* registries additionally use a
  `wover/` path segment to avoid colliding with vanilla's folders (e.g. `data/<namespace>/wover/worldgen/biome_data/`
  instead of `data/<namespace>/worldgen/biome_data/`) — always check the exact path in the linked module page, it's
  not 100% uniform.

## JSON file map

| File | Format | Module |
|---|---|---|
| `data/<namespace>/tags/<registry>/<path>.json` | vanilla tag format, plus WoVer's predefined common tags (`c:`/`wover:` conventions) | [tag-api](modules/tag-api.md) |
| `data/<namespace>/enchantment/<path>.json` | vanilla enchantment format | [item-api](modules/item-api.md) |
| `data/<namespace>/loot_table/<path>.json` | vanilla loot table format | [item-api](modules/item-api.md), [block-api](modules/block-api.md) |
| `data/<namespace>/recipe/<path>.json` | vanilla recipe format (shaped/shapeless/smelting/smithing/stonecutting/...) | [recipe-api](modules/recipe-api.md) |
| `data/<namespace>/worldgen/world_preset/<path>.json` | vanilla World Preset ("Create World" screen) format | [preset-api](modules/preset-api.md) |
| `data/<namespace>/worldgen/flat_level_generator_preset/<path>.json` | vanilla superflat preset format | [preset-api](modules/preset-api.md) |
| `data/<namespace>/wover/world_preset_info/<path>.json` | **WoVer-only** — extra metadata (sort order, per-dimension preset linkage) for a `world_preset` | [preset-api](modules/preset-api.md) |
| `data/<namespace>/wover/worldgen/surface_rules/<path>.json` | **WoVer-only** — a per-biome `SurfaceRules.RuleSource` that gets merged into whichever chunk generator the biome is used with | [surface-api](modules/surface-api.md) |
| `data/<namespace>/worldgen/structure/<path>.json`, `structure_set/<path>.json`, `template_pool/<path>.json`, `processor_list/<path>.json` | vanilla structure formats, plus a WoVer `wover:random_nbt_structure` type | [structure-api](modules/structure-api.md) |
| `data/<namespace>/structure/<path>.nbt` | vanilla structure template, referenced by template pools and by WoVer's `wover:template` feature | [structure-api](modules/structure-api.md), [feature-api](modules/feature-api.md) |
| `data/<namespace>/worldgen/configured_feature/<path>.json`, `placed_feature/<path>.json` | vanilla feature formats, plus several WoVer feature types (`wover:place_block`, `wover:pillar`, `wover:sequence`, `wover:condition`, ...) and placement modifiers | [feature-api](modules/feature-api.md) |
| `data/<namespace>/worldgen/biome/<path>.json` | vanilla biome format | [biome-api](modules/biome-api.md) |
| `data/<namespace>/wover/worldgen/biome_data/<path>.json` | **WoVer-only** — extra per-biome metadata (fog density etc.) attached to a biome | [biome-api](modules/biome-api.md) |
| `data/<namespace>/wover/worldgen/biome_modifications/<path>.json` | **WoVer-only** — add features/spawns/etc. to an *existing* (including vanilla) biome at load time, without overwriting its file | [biome-api](modules/biome-api.md) |
| `data/<namespace>/worldgen/noise_settings/<path>.json` | vanilla noise settings; referenced by WoVer's `chunk_generator`/`biome_source` types | [generator-api](modules/generator-api.md) |
| `data/<namespace>/dimension/<path>.json` with `"type": "wover:betterx"` generator and `"wover:nether_biome_source"` / `"wover:end_biome_source"` biome sources | **WoVer-only** generator/biome-source types for building a Nether/End-style custom dimension | [generator-api](modules/generator-api.md) |
| `data/<namespace>/config/biome_config.json` | **WoVer-only** — exclude specific biomes from a WoVer biome source | [generator-api](modules/generator-api.md) |
| `data/<namespace>/wover/pottable_plant/<path>.json`, `wover/pottable_soil/<path>.json` | **WoVer-only** — registers a block as pottable in a decorated pot (see caveat below) | [pottable-api](modules/pottable-api.md) |

> **Caveat:** as of this writing, `wover-pottable-api`'s registries are not yet wired into vanilla's flower-pot
> mechanic by any other WoVer module — registering an entry currently has no in-game effect. See its
> [module page](modules/pottable-api.md) for details before relying on it.

## Recommended reading order for a datapack-only project

1. [tag-api](modules/tag-api.md) — tags are used everywhere else.
2. [biome-api](modules/biome-api.md) + [feature-api](modules/feature-api.md) + [structure-api](modules/structure-api.md) — the
   worldgen content itself.
3. [surface-api](modules/surface-api.md) — terrain surface blocks per biome.
4. [preset-api](modules/preset-api.md) + [generator-api](modules/generator-api.md) — only if you're assembling a whole custom
   dimension/world type, not just adding biomes/features to existing worlds.
5. [item-api](modules/item-api.md) / [block-api](modules/block-api.md) / [recipe-api](modules/recipe-api.md) — only relevant if you're
   also shipping new blocks/items (which normally still requires *some* Java, since blocks/items themselves aren't
   fully datapack-defined in vanilla Minecraft).

## What still needs Java

Blocks, Items, and Enchantment *effects* (as opposed to their JSON definition) cannot be created by a datapack
alone in vanilla Minecraft — WoVer's Java builder APIs ([block-api](modules/block-api.md), [item-api](modules/item-api.md),
[sets-api](modules/sets-api.md)) exist specifically to make defining those, and generating their accompanying JSON, fast
for a mod developer. If your project needs new blocks/items, you'll need a thin mod wrapper — see the
[Mod Developer Guide](mod-developer-guide.md).
