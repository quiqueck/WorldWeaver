<div align="center">

![WorldWeaver](./public/img/header.png)

# WorldWeaver (WoVer)

**A Fabric library for data-driven Minecraft world generation, and the successor of BCLib.**

[![Modrinth downloads](https://img.shields.io/modrinth/dt/worldweaver?logo=modrinth&label=Modrinth&color=1bd96a&style=for-the-badge)](https://modrinth.com/mod/worldweaver)
[![CurseForge downloads](https://img.shields.io/curseforge/dt/1037172?logo=curseforge&label=CurseForge&color=f16436&style=for-the-badge)](https://www.curseforge.com/minecraft/mc-mods/worldweaver)
[![Fabric for Minecraft](https://img.shields.io/badge/Fabric-1.21.6--8_%7C_26.x-62b47a?style=for-the-badge)](https://modrinth.com/mod/worldweaver/versions)

</div>

**WorldWeaver**, or *WoVer* for short, is a Fabric utility mod for Minecraft that aims to streamline common modding
tasks, particularly those related to world creation. As a successor to BCLib, WoVer is intended to eventually take its
place. Embracing Minecraft's data-driven world generation methodology, it aims to increase the value of data elements
(i.e. ResourcePacks, DataPacks, etc.) in modding, and to provide a more flexible and powerful way to create and modify
worlds.
In addition, WoVer is designed to integrate seamlessly with the vanilla game, other mods and world generation data
packs.

## Download

The latest Fabric release for each supported Minecraft version (the badges update automatically):

| Minecraft | Modrinth | CurseForge |
|:--|:--|:--|
| **26.3** | [![Modrinth 26.3](https://img.shields.io/badge/dynamic/json?url=https%3A%2F%2Fapi.modrinth.com%2Fv2%2Fproject%2Fworldweaver%2Fversion%3Fgame_versions%3D%255B%252226.3%2522%255D%26loaders%3D%255B%2522fabric%2522%255D&query=%24%5B0%5D.version_number&label=Modrinth&color=1bd96a&logo=modrinth&logoColor=white)](https://modrinth.com/mod/worldweaver/versions?g=26.3&l=fabric) | [![CurseForge 26.3](https://img.shields.io/badge/dynamic/json?url=https%3A%2F%2Fapi.modrinth.com%2Fv2%2Fproject%2Fworldweaver%2Fversion%3Fgame_versions%3D%255B%252226.3%2522%255D%26loaders%3D%255B%2522fabric%2522%255D&query=%24%5B0%5D.version_number&label=CurseForge&color=f16436&logo=curseforge&logoColor=white)](https://www.curseforge.com/minecraft/mc-mods/worldweaver/files/all?version=26.3&gameVersionTypeId=4) |
| **26.2** | [![Modrinth 26.2](https://img.shields.io/badge/dynamic/json?url=https%3A%2F%2Fapi.modrinth.com%2Fv2%2Fproject%2Fworldweaver%2Fversion%3Fgame_versions%3D%255B%252226.2%2522%255D%26loaders%3D%255B%2522fabric%2522%255D&query=%24%5B0%5D.version_number&label=Modrinth&color=1bd96a&logo=modrinth&logoColor=white)](https://modrinth.com/mod/worldweaver/versions?g=26.2&l=fabric) | [![CurseForge 26.2](https://img.shields.io/badge/dynamic/json?url=https%3A%2F%2Fapi.modrinth.com%2Fv2%2Fproject%2Fworldweaver%2Fversion%3Fgame_versions%3D%255B%252226.2%2522%255D%26loaders%3D%255B%2522fabric%2522%255D&query=%24%5B0%5D.version_number&label=CurseForge&color=f16436&logo=curseforge&logoColor=white)](https://www.curseforge.com/minecraft/mc-mods/worldweaver/files/all?version=26.2&gameVersionTypeId=4) |
| **26.1 – 26.1.2** | [![Modrinth 26.1 – 26.1.2](https://img.shields.io/badge/dynamic/json?url=https%3A%2F%2Fapi.modrinth.com%2Fv2%2Fproject%2Fworldweaver%2Fversion%3Fgame_versions%3D%255B%252226.1%2522%252C%252226.1.1%2522%252C%252226.1.2%2522%255D%26loaders%3D%255B%2522fabric%2522%255D&query=%24%5B0%5D.version_number&label=Modrinth&color=1bd96a&logo=modrinth&logoColor=white)](https://modrinth.com/mod/worldweaver/versions?g=26.1.2&l=fabric) | [![CurseForge 26.1 – 26.1.2](https://img.shields.io/badge/dynamic/json?url=https%3A%2F%2Fapi.modrinth.com%2Fv2%2Fproject%2Fworldweaver%2Fversion%3Fgame_versions%3D%255B%252226.1%2522%252C%252226.1.1%2522%252C%252226.1.2%2522%255D%26loaders%3D%255B%2522fabric%2522%255D&query=%24%5B0%5D.version_number&label=CurseForge&color=f16436&logo=curseforge&logoColor=white)](https://www.curseforge.com/minecraft/mc-mods/worldweaver/files/all?version=26.1.2&gameVersionTypeId=4) |
| **1.21.6 – 1.21.8** | [![Modrinth 1.21.6 – 1.21.8](https://img.shields.io/badge/dynamic/json?url=https%3A%2F%2Fapi.modrinth.com%2Fv2%2Fproject%2Fworldweaver%2Fversion%3Fgame_versions%3D%255B%25221.21.6%2522%252C%25221.21.7%2522%252C%25221.21.8%2522%255D%26loaders%3D%255B%2522fabric%2522%255D&query=%24%5B0%5D.version_number&label=Modrinth&color=1bd96a&logo=modrinth&logoColor=white)](https://modrinth.com/mod/worldweaver/versions?g=1.21.8&l=fabric) | [![CurseForge 1.21.6 – 1.21.8](https://img.shields.io/badge/dynamic/json?url=https%3A%2F%2Fapi.modrinth.com%2Fv2%2Fproject%2Fworldweaver%2Fversion%3Fgame_versions%3D%255B%25221.21.6%2522%252C%25221.21.7%2522%252C%25221.21.8%2522%255D%26loaders%3D%255B%2522fabric%2522%255D&query=%24%5B0%5D.version_number&label=CurseForge&color=f16436&logo=curseforge&logoColor=white)](https://www.curseforge.com/minecraft/mc-mods/worldweaver/files/all?version=1.21.8&gameVersionTypeId=4) |

**Required:** [Fabric API](https://modrinth.com/mod/fabric-api). **Optional:** [Mod Menu](https://modrinth.com/mod/modmenu).

## Modules

WorldWeaver is split into API modules that are bundled into one mod jar:

| Module | Purpose |
|:--|:--|
| `wover-core-api` | common functionality and the `ModCore` entry point |
| `wover-biome-api` | registering biomes and placing them in the Overworld, Nether and End |
| `wover-generator-api`, `wover-preset-api` | WorldWeaver's chunk generators and world presets |
| `wover-surface-api` | data-driven surface rules |
| `wover-feature-api`, `wover-structure-api` | features, configured features and structures |
| `wover-block-api`, `wover-item-api`, `wover-sets-api` | blocks, items and complete material sets (woods, stones) |
| `wover-recipe-api`, `wover-loot-api`, `wover-tag-api` | recipes, loot tables and tags |
| `wover-pottable-api` | a datapack-driven registry of pottable plants and soils |
| `wover-portal-api` | data-driven portals: frame shapes, ignition and linking between dimensions |
| `wover-story-api` | data-driven story beats: conversations on screens and buttons, and persistent characters with relationships and a memory |
| `wover-datagen-api` | data generation helpers |
| `wover-event-api` | events in the lifecycle of a Minecraft world |
| `wover-ui-api`, `wover-math-api`, `wover-common-api` | shared UI, math and common interfaces |
| `wover-test-api` | GameTest support for mods built on WorldWeaver |

## Compatibility

Every release runs the GameTest suites of the biome and generator APIs on a dedicated server next to these world
generation mods, and checks that their biomes still generate in their dimensions and that WorldWeaver's generator stays
in charge. The results are listed under *Validation* in each release's changelog.

<!-- mod-task:verified:start -->

| Mod | Verified version (Minecraft 26.3) | Get it |
|:--|:--|:--|
| [![Verified with Biomes O' Plenty](.github/assets/badges/verified-biomesoplenty.svg)](https://modrinth.com/mod/biomes-o-plenty) | `26.3.0.0.13` | [![Modrinth](https://img.shields.io/badge/Modrinth-1bd96a?logo=modrinth&logoColor=white)](https://modrinth.com/mod/biomes-o-plenty) [![CurseForge](https://img.shields.io/badge/CurseForge-f16436?logo=curseforge&logoColor=white)](https://www.curseforge.com/minecraft/mc-mods/biomes-o-plenty) |
| [![Verified with Terralith](.github/assets/badges/verified-terralith.svg)](https://modrinth.com/mod/terralith) | `2.6.5` | [![Modrinth](https://img.shields.io/badge/Modrinth-1bd96a?logo=modrinth&logoColor=white)](https://modrinth.com/mod/terralith) [![CurseForge](https://img.shields.io/badge/CurseForge-f16436?logo=curseforge&logoColor=white)](https://www.curseforge.com/minecraft/mc-mods/terralith) |

<!-- mod-task:verified:end -->

## Add WorldWeaver to your project

Add our maven repository and the dependency to your `build.gradle`:

```groovy
repositories {
    maven { url 'https://maven.ambertation.de/releases' }
}

dependencies {
    // Minecraft 26.x (Loom 1.17+ no longer has the mod* configurations)
    implementation "de.ambertation:worldweaver:${project.wover_version}"
    // Minecraft 1.21.x:
    // modImplementation "de.ambertation:worldweaver:${project.wover_version}"
}
```

You should also add a dependency to `fabric.mod.json`. WorldWeaver uses semantic versioning, so adding the
dependency as follows should respect that and ensure that your mod is not loaded with an incompatible version of
WorldWeaver:

<!-- mod-task:deps:start -->

```json
"depends": {
  "wover": "26.302.x"
},
"breaks": {
  "wover": "<26.302.2"
}
```

In this example `26.302.2` is the WorldWeaver version you are building against.

<!-- mod-task:deps:end -->

## Building from source

You need JDK 25 for the 26.x branches (JDK 21 for 1.21.x).

```bash
git clone https://github.com/quiqueck/WorldWeaver.git
cd WorldWeaver
./gradlew build
```

The mod jar is written to `build/libs`. Each Minecraft version has its own release branch: `release/26.3`, `release/26.2`, `release/26.1`, `release/1.21.6`.

## License

WorldWeaver is developed by [Quiqueck](https://github.com/quiqueck) and licensed under the [MIT License](LICENSE),
with the exception of the assets and the Story API module (see below).

**Assets (2026-10-30).** The source code is licensed under the [MIT License](LICENSE).
Assets (textures, images, models, music and sounds) are not part of the code. They remain the
property of their creators and are All Rights Reserved, see [LICENSE.ASSETS](LICENSE.ASSETS)
for the terms. As long as they stay unmodified and bundled with the official mod release, you may use them in modpacks and on
servers (commercial and non-commercial ones). You may also show them in videos, streams and screenshots.

**Story API (2026-10-30).** The `wover-story-api` module is *not* MIT licensed. It is All Rights Reserved,
see [wover-story-api/LICENSE](wover-story-api/LICENSE). You may use the official, unmodified release by Team BetterX in
modpacks and on servers (commercial and non-commercial ones), show it in videos, streams and screenshots, and write mods, wover-packs and data-packs
that depend on its API. You may not re-upload it, with or without changes.

## AI Usage

The ideas and the design of our APIs are human-made: we come up with the concepts, guide every design decision and implement the code. We use agentic AI as a tool for the following tasks:

- tooling and automation,
- code review,
- proofreading and writing documentation (code comments, this README and the wiki pages),
- boilerplate work, including migrating the codebase to new Minecraft versions,
- many of our automated tests.

Everything the AI generates is reviewed and, where needed, edited by us before it is released. We also playtest every release ourselves.
