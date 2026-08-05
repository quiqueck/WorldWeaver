![](./public/img/header.png)

**WorldWeaver**, or *WoVer* for short, is a Fabric utility mod for Minecraft that aims to streamline common modding
tasks, particularly those related to world creation. As a successor to BCLib, WoVer is intended to eventually take its
place. Embracing Minecraft's data-driven world generation methodology, it aims to increase the value of data elements
(i.e. ResourcePacks, DataPacks, etc.) in modding, and to provide a more flexible and powerful way to create and modify
worlds.
In addition, WoVer is designed to integrate seamlessly with the vanilla game, other mods and world generation data
packs.

**Please note** that this mod is still in early development and is not yet ready for use. This means that:

- The API is not yet stable and may change significantly between versions.
- There may be game breaking bugs that cannot be undone and may destroy worlds.
- Documentation may be missing for some parts, especially those that are still under active development.

We will provide more information on how to add the mod to your project, as well as the format of our additional
datapack files, in the near future.

## Add WorldWeaver to your project:

You can easily include WorldWeaver into your own mod by adding the following to your `build.gradle`:

```
repositories {
    ...
    maven { url 'https://maven.ambertation.de/releases' }
}
```

```
dependencies {
    ...
    modImplementation "de.ambertation:worldweaver:${project.wover_version}"
}
```

You should also add a dependency to `fabric.mod.json`. WorldWeaver uses Semantic versioning, so adding the
dependency as follows should respect that and ensure that your mod is not loaded with an incompatible version of
WorldWeaver:

```
"depends": {
  ...
  "wover": "26.100.x"
},
"breaks": {
  "wover": "<26.100.0"
}
```

In this example `26.100.0` is the WorldWeaver Version you are building against.
