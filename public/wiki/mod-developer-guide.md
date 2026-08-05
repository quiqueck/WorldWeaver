# Mod Developer Guide

This guide covers the parts of WoVer that are the same no matter which module(s) you use. For the API of a
specific module (blocks, biomes, features, ...) see its page under [`modules/`](modules/).

## 1. Add WoVer to your Gradle project

```groovy
repositories {
    maven { url 'https://maven.ambertation.de/releases' }
}

dependencies {
    // WoVer publishes a single artifact; every wover-* module is bundled inside it
    modImplementation "de.ambertation:worldweaver:${project.wover_version}"
}
```

Add a matching entry to your `fabric.mod.json` so Fabric Loader enforces a compatible WoVer version:

```json
"depends": {
  "wover": "26.100.x"
},
"breaks": {
  "wover": "<26.100.0"
}
```

There is one Gradle artifact, but each WoVer module is still its own independent mod at runtime (own
`fabric.mod.json`, own id — `wover-tag`, `wover-block`, `wover-biome`, ...), jar-in-jar'd into the aggregate.
So if your mod only touches one module you can narrow the Loader dependency to that module's id (e.g.
`"depends": { "wover-block": "26.100.x" }`) instead of the umbrella `wover` id — but the Gradle coordinate is
the same either way.

## 2. Module dependency order

WoVer modules depend on each other; you rarely need to think about this explicitly (Gradle/Fabric resolve it),
but it's useful to know when reading source or deciding which module owns a concept:

```
common → core → { math, datagen, event } → { ui, tag } → item → block → { recipe, preset }
    → sets                                                    → surface → structure → feature → biome → generator
                                                                                                          pottable
```

(See [Notes.md](../../Notes.md) in the repo root for the exact per-module dependency list, and the
[wiki index](README.md) for a one-line summary of what each module does.)

## 3. `ModCore` — identify your mod to WoVer

Most WoVer builder APIs take a `ModCore` (`de.ambertation.wover.core.api.ModCore`, from `wover-core-api`) so they
know which namespace to create `Identifier`s in and which logger to use. Create one instance per mod, in
your `ModInitializer`:

```java
public class MyMod implements ModInitializer {
    public static final ModCore C = ModCore.create("mymod", "mymod");

    @Override
    public void onInitialize() {
        // register blocks/items/biomes/... here, using C.id("thing_name") /
        // C.mk("thing_name") to build namespaced Identifiers
    }
}
```

## 4. Entrypoints, per module

WoVer follows the same `fabric.mod.json` entrypoint layout in every module, and your own mod should too when you
add datagen or client-only code:

| Entrypoint key | Purpose |
|---|---|
| `main` | Your `ModInitializer` — register blocks/items/biomes/recipes/etc. here (or in `BOOTSTRAP_*` events fired by the relevant module, see each module's page). |
| `client` | `ClientModInitializer` — client-only rendering/screens. |
| `fabric-datagen` | `DataGeneratorEntrypoint` — generates the JSON your `main` registrations imply (loot tables, tags, recipes, biome/feature/structure JSON, etc.). Each WoVer module that has a datapack surface offers `Wover*Provider` base classes for this — see its module page. |

## 5. Events and world lifecycle (`wover-event-api`)

Most "when should I do X" questions are answered by `wover-event-api`'s `WorldState`/`WorldLifecycle` events
(world folder ready → registries ready → resources loaded → server ready → level ready), and by each module's own
`BOOTSTRAP_*` registry events fired during registry bootstrap. See [event-api.md](modules/event-api.md) for the
full ordering and subscription examples — read this early, it underpins how every other module expects you to
register content.

## 6. Config (`wover-core-api` / `wover-ui-api`)

`wover-core-api`'s `Configs`/`DatapackConfigs` handle server/common config values and datapack-overridable config;
`wover-ui-api`'s `config.api.client` package adds a client-side config screen layer. See
[core-api.md](modules/core-api.md) and [ui-api.md](modules/ui-api.md).

## 7. Testmods as real examples

Every module ships a `src/testmod` (and often `src/testmodDatagen`, `src/testmodClient`) source set that's a
minimal, compiling, real usage example of that module's API — these were the primary source used to ground the
code samples in each module's wiki page. When in doubt about a call signature, check the matching module's
`src/testmod` directory in the repo.

## 8. Building custom generators/biome sources at the interface level

If you need to go below the builder APIs (e.g. write your own `BiomeSource`/`ChunkGenerator` from scratch instead
of using `wover-generator-api`'s), implement the low-level interfaces in `wover-common-api` so WoVer's
event/merge/repair machinery still recognizes your generator — see [common-api.md](modules/common-api.md).
