# wover-core-api

Foundational module for WorldWeaver. It provides mod-entrypoint/config plumbing, a lightweight config system, and
helpers for registering both code-defined and datapack-backed registries. Almost every other WorldWeaver module
depends on `wover-core-api`.

- **Gradle artifact:** `org.betterx:wover-core-api`
- **Depends on:** `wover-common-api`
- **Java packages:**
  - `org.betterx.wover.core.api`
  - `org.betterx.wover.core.api.registry`
  - `org.betterx.wover.config.api`
  - `org.betterx.wover.legacy.api`

## For Datapack Developers

`wover-core-api` itself does not define any datapack-loaded JSON file format. It only ships the *infrastructure*
that other WorldWeaver modules use to add their own datapack-backed registries and JSON-driven configuration:

- [`DatapackRegistryBuilder`](../../wover-core-api/src/main/java/org/betterx/wover/core/api/registry/DatapackRegistryBuilder.java)
  lets Java code register new registries that are then populated from `data/<namespace>/<registry path>/*.json`
  files, the same way vanilla registries like `worldgen/biome` are. The actual JSON schema for such an entry is
  defined by whichever module registers the registry (e.g. `wover-biome-api`, `wover-structure-api`), not by
  `wover-core-api`.
- [`DatapackConfigs`](../../wover-core-api/src/main/java/org/betterx/wover/config/api/DatapackConfigs.java) can load
  arbitrary JSON files that mods or datapacks place under a `config/` folder in their resources (e.g.
  `data/<namespace>/config/<path>.json`), but it does not prescribe a schema for the file's contents — that is up
  to whoever calls it.

If you are only writing a datapack (no Java code), you can skip this module — check the wiki page of the module
that actually defines the registry/config you want to add data for.

## For Mod Developers

### Mod identity and logging: `ModCore`

[`ModCore`](../../wover-core-api/src/main/java/org/betterx/wover/core/api/ModCore.java) identifies your mod and
bundles a few helpers you will use throughout your codebase. Create (or fetch) the instance for your mod id with
`ModCore.create(String modId)` and store it, typically in your main entrypoint:

```java
public class MyMod implements ModInitializer {
    public static final ModCore C = ModCore.create("mymod");

    @Override
    public void onInitialize() {
        C.log.info("Hello from MyMod!");
    }
}
```

`ModCore` gives you:

| Member | Purpose |
|---|---|
| `ModCore.create(String modId)` / `ModCore.create(String modId, String namespace)` | Get the (cached, singleton) instance for a mod id. |
| `log` / `LOG` | A [`Logger`](../../wover-core-api/src/main/java/org/betterx/wover/core/api/Logger.java) (wraps `wunderlib`'s logger) named after your mod id. |
| `id(String name)` / `mk(String key)` | Build a `ResourceLocation` in your mod's namespace — prefer this over `ResourceLocation.fromNamespaceAndPath(...)`. |
| `addDatapack(String name, ResourcePackActivationType)` / `addDatapack(ModCore dependency)` | Register a built-in resource pack shipped inside your mod jar. |
| `isLoaded()` | Whether this mod is actually present (useful when `ModCore` is used to represent an *optional* dependency). |
| `ModCore.isDevEnvironment()` / `isDatagen()` / `isClient()` / `isServer()` | Static environment checks. |

[`IntegrationCore`](../../wover-core-api/src/main/java/org/betterx/wover/core/api/IntegrationCore.java) exposes
`ModCore` instances plus loaded-checks for a few mods WorldWeaver commonly integrates with (`MINECRAFT`,
`BETTER_END`, `BETTER_NETHER`, `RUNS_TERRABLENDER`, `RUNS_NULLSCAPE`), and a generic `IntegrationCore.hasMod(String)`
check for any other mod id.

### Config values: `Configs`

[`Configs`](../../wover-core-api/src/main/java/org/betterx/wover/config/api/Configs.java) is a small registry for
`de.ambertation.wunderlib.configs.AbstractConfig` instances (usually a `ConfigFile` subclass, see
[`MainConfig`](../../wover-core-api/src/main/java/org/betterx/wover/config/api/MainConfig.java) for WorldWeaver's
own example):

```java
public class MyConfig extends ConfigFile {
    public final BooleanValue enableFeature = new BooleanValue("general", "enable_feature", true);

    public MyConfig() {
        super(MyMod.C, "main");
    }
}

public static final MyConfig CONFIG = Configs.register(MyConfig::new);
```

Call `Configs.saveConfigs()` to persist every registered config, or look one up again with
`Configs.get(ResourceLocation)`.

### Reading JSON config files from datapacks: `DatapackConfigs`

If instead of a user-editable config you want to read JSON files that mods/datapacks contribute (and react to
resource reloads), use
[`DatapackConfigs.instance()`](../../wover-core-api/src/main/java/org/betterx/wover/config/api/DatapackConfigs.java):

```java
DatapackConfigs.instance().runForConfigPaths(
        resourceManager,
        List.of("mymod/settings.json"),
        (id, json) -> {
            // id.getNamespace() identifies the mod/datapack that provided the file
        },
        () -> { /* called once after all matches were processed */ }
);
```

### Registering registries: `BuiltInRegistryManager` and `DatapackRegistryBuilder`

For registries that are populated entirely from code (no datapack involvement), use
[`BuiltInRegistryManager`](../../wover-core-api/src/main/java/org/betterx/wover/core/api/registry/BuiltInRegistryManager.java):

```java
public static final Registry<MyThing> MY_REGISTRY =
        BuiltInRegistryManager.createRegistry(MyRegistries.MY_THING, registry -> DEFAULT_THING);

BuiltInRegistryManager.register(MY_REGISTRY, MyMod.C.id("example"), new MyThing());
```

For registries that should be populated from Datapack JSON files (like vanilla's worldgen registries), use
[`DatapackRegistryBuilder`](../../wover-core-api/src/main/java/org/betterx/wover/core/api/registry/DatapackRegistryBuilder.java)
from a `wover.datapack.registry` entrypoint (a class implementing
[`DatapackRegistryEntrypoint`](../../wover-core-api/src/main/java/org/betterx/wover/core/api/registry/DatapackRegistryEntrypoint.java),
declared in `fabric.mod.json`):

```java
public class MyDatapackRegistries implements DatapackRegistryEntrypoint {
    @Override
    public void registerDatapackRegistries() {
        DatapackRegistryBuilder.register(MyRegistries.MY_THING, MyThing.CODEC, bootstrap -> {
            bootstrap.register(MyThingKeys.EXAMPLE, new MyThing());
        });

        // React whenever any element of an existing registry (vanilla or custom) is (re-)loaded
        DatapackRegistryBuilder.onElementLoad(Registries.BIOME, (key, biome) -> {
            // ...
        });
    }
}
```

`fabric.mod.json`:

```json
{
  "entrypoints": {
    "wover.datapack.registry": [
      "com.example.mymod.MyDatapackRegistries"
    ]
  }
}
```

[`CustomBootstrapContext`](../../wover-core-api/src/main/java/org/betterx/wover/core/api/registry/CustomBootstrapContext.java)
is an optional base class for a cached, custom context object (instead of the plain `BootstrapContext` vanilla
gives you) to carry along extra state/caches between bootstrap calls for the same registry.

### Legacy interop

[`LegacyHelper`](../../wover-core-api/src/main/java/org/betterx/wover/legacy/api/LegacyHelper.java) offers
`wrap(Codec)`/`wrap(MapCodec)` to re-expose a codec under a plain interface for old code that expects one, plus
`ModCore` instances identifying the legacy BCLib/"Worlds Together" mods. `LegacyHelper.isLegacyEnabled()` currently
always returns `false` — legacy compatibility is not implemented yet.
