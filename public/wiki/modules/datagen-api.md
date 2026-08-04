# wover-datagen-api

Shared infrastructure for Minecraft's build-time data generation ("datagen"). This module wraps Fabric's
datagen API with abstractions for registering per-registry content and shipping it as one or more Datapacks.
Almost every other WorldWeaver module (`wover-tag-api`, `wover-biome-api`, `wover-structure-api`, `wover-recipe-api`,
`wover-preset-api`, `wover-surface-api`, ...) builds its own datagen providers on top of the classes in this module.

- **Gradle artifact:** `de.ambertation:worldweaver` (single artifact; this module ships inside it as the Fabric mod `wover-datagen`)
- **Depends on:** `wover-core-api`
- **Java packages:**
  - `de.ambertation.wover.datagen.api`
  - `de.ambertation.wover.datagen.api.provider`
  - `de.ambertation.wover.datagen.api.provider.multi`

## For Datapack Developers

`wover-datagen-api` has no datapack-facing surface of its own — it does not read or write any JSON at runtime.
It is exclusively the Java machinery mod authors use, at build time, to generate the JSON files that ship inside
a mod's Datapack (worldgen registry entries, tags, recipes, loot tables, and so on).

If you are a datapack author (not a mod developer), you can skip this page. The actual JSON formats produced by
mods that use this module are documented on the wiki pages of the modules that own them, for example the
`biome-api`, `tag-api`, `structure-api`, `recipe-api`, `preset-api`, and `surface-api` pages.

## For Mod Developers

### The datagen entrypoint

Datagen starts with a subclass of
[`WoverDataGenEntryPoint`](../../../wover-datagen-api/src/main/java/de/ambertation/wover/datagen/api/WoverDataGenEntryPoint.java),
registered as a `fabric-datagen` entrypoint in your `fabric.mod.json`:

```json
"entrypoints": {
  "fabric-datagen": ["com.example.mymod.datagen.MyModDatagen"]
}
```

A minimal implementation only needs to say which `ModCore` it belongs to and register its providers on the
global Datapack, for example (adapted from `wover-surface-api`'s own datagen entrypoint):

```java
public class MyModDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        globalPack.addRegistryProvider(MySurfaceRuleProvider::new);
    }

    @Override
    protected ModCore modCore() {
        return MyMod.C;
    }
}
```

You also need the usual Fabric Loom datagen run configuration in `build.gradle` (see the
[`de.ambertation.wover.datagen.api` package Javadoc](../../../wover-datagen-api/src/main/java/de/ambertation/wover/datagen/api/package-info.java)
for a full `sourceSets`/`loom.runs` snippet).

`WoverDataGenEntryPoint` can also manage additional, optional Datapacks (registered beforehand via
`ModCore#addDatapack`) — call
[`addDatapack(Identifier)`](../../../wover-datagen-api/src/main/java/de/ambertation/wover/datagen/api/WoverDataGenEntryPoint.java)
from `onInitializeProviders` to get a second `PackBuilder` for that pack, optionally with its own
`PackBuilder.DatapackBootstrap` callback.

### Registering providers with `PackBuilder`

[`PackBuilder`](../../../wover-datagen-api/src/main/java/de/ambertation/wover/datagen/api/PackBuilder.java)
is the object you attach content to, once per Datapack. It has three registration methods:

| Method | Registers |
|---|---|
| `addRegistryProvider(PackBuilder.RegistryFactory<T>)` | A [`WoverRegistryProvider<T>`](../../../wover-datagen-api/src/main/java/de/ambertation/wover/datagen/api/WoverRegistryProvider.java) that bootstraps and serializes elements of a dynamic `Registry<T>`. |
| `addProvider(PackBuilder.ProviderFactory<T>)` | Any other [`WoverDataProvider<T>`](../../../wover-datagen-api/src/main/java/de/ambertation/wover/datagen/api/WoverDataProvider.java) (tags, recipes, loot tables, models, ...). |
| `addMultiProvider(PackBuilder.MultiProviderFactory<T>)` | A [`WoverMultiProvider`](../../../wover-datagen-api/src/main/java/de/ambertation/wover/datagen/api/WoverMultiProvider.java) that internally registers several providers at once. |

Real usage, from `wover-tag-api`'s datagen entrypoint:

```java
public class WoverTagDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        globalPack.addProvider(BlockTagProvider::new);
        globalPack.addProvider(ItemTagProvider::new);
        globalPack.addProvider(BiomeTagProvider::new);
    }

    @Override
    protected ModCore modCore() {
        return LibWoverTag.C;
    }
}
```

### Registry providers

Two abstract base classes handle the common case of bootstrapping elements into a dynamic registry and then
serializing them to JSON:

| Class | Serializes |
|---|---|
| [`WoverFullRegistryProvider<T>`](../../../wover-datagen-api/src/main/java/de/ambertation/wover/datagen/api/WoverFullRegistryProvider.java) | **All** elements of the registry whose namespace passes a `Predicate<String>` (by default: just the mod's own namespace). |
| [`WoverRegistryContentProvider<T>`](../../../wover-datagen-api/src/main/java/de/ambertation/wover/datagen/api/WoverRegistryContentProvider.java) | **Only** the elements that were registered inside this particular provider's own `bootstrap(BootstrapContext<T>)` method — useful when you need several providers for the same registry, each feeding a different Datapack. |

Both extend [`WoverRegistryProvider<T>`](../../../wover-datagen-api/src/main/java/de/ambertation/wover/datagen/api/WoverRegistryProvider.java),
which you can also subclass directly for full control. The
[`de.ambertation.wover.datagen.api.provider`](../../../wover-datagen-api/src/main/java/de/ambertation/wover/datagen/api/provider)
package ships ready-made `WoverRegistryContentProvider` subclasses for the common vanilla worldgen registries so
you don't have to look up the `ResourceKey` yourself:

| Class | Registry |
|---|---|
| [`WoverBiomeOnlyProvider`](../../../wover-datagen-api/src/main/java/de/ambertation/wover/datagen/api/provider/WoverBiomeOnlyProvider.java) | `Registries.BIOME` |
| [`WoverConfiguredFeatureProvider`](../../../wover-datagen-api/src/main/java/de/ambertation/wover/datagen/api/provider/WoverConfiguredFeatureProvider.java) | `Registries.CONFIGURED_FEATURE` |
| [`WoverPlacedFeatureProvider`](../../../wover-datagen-api/src/main/java/de/ambertation/wover/datagen/api/provider/WoverPlacedFeatureProvider.java) | `Registries.PLACED_FEATURE` |
| [`WoverStructureProvider`](../../../wover-datagen-api/src/main/java/de/ambertation/wover/datagen/api/provider/WoverStructureProvider.java) | `Registries.STRUCTURE` |
| [`WoverStructureSetProvider`](../../../wover-datagen-api/src/main/java/de/ambertation/wover/datagen/api/provider/WoverStructureSetProvider.java) | `Registries.STRUCTURE_SET` |
| [`WoverStructurePoolProvider`](../../../wover-datagen-api/src/main/java/de/ambertation/wover/datagen/api/provider/WoverStructurePoolProvider.java) | `Registries.TEMPLATE_POOL` |
| [`WoverStructureProcessorProvider`](../../../wover-datagen-api/src/main/java/de/ambertation/wover/datagen/api/provider/WoverStructureProcessorProvider.java) | `Registries.PROCESSOR_LIST` |

Each takes a `ModCore` and an optional `Identifier providerId` (needed only when you register more than one
provider for the same registry), and you implement `bootstrap(BootstrapContext<T>)` to register your elements.

### Multi-registry providers

Some content naturally spans several registries at once — a feature needs both a `ConfiguredFeature` and a
`PlacedFeature`, for instance. Implement
[`WoverMultiProvider`](../../../wover-datagen-api/src/main/java/de/ambertation/wover/datagen/api/WoverMultiProvider.java)
(or extend the convenience base class
[`AbstractMultiProvider`](../../../wover-datagen-api/src/main/java/de/ambertation/wover/datagen/api/AbstractMultiProvider.java),
which already stores the `ModCore` and `providerId`) and register the individual registry providers from
`registerAllProviders(PackBuilder)`. The
[`de.ambertation.wover.datagen.api.provider.multi`](../../../wover-datagen-api/src/main/java/de/ambertation/wover/datagen/api/provider/multi)
package includes
[`WoverFeatureProvider`](../../../wover-datagen-api/src/main/java/de/ambertation/wover/datagen/api/provider/multi/WoverFeatureProvider.java)
as a ready-made example that wires up matching `WoverConfiguredFeatureProvider`/`WoverPlacedFeatureProvider`
pairs; `wover-biome-api`'s `WoverBiomeProvider` follows the same pattern for biomes, biome data, surface rules
and biome tags together.

### Auto providers

If you want a provider to be added automatically to **every** global Datapack (including those of other mods
using WorldWeaver), register a factory with the static method
[`WoverDataGenEntryPoint.registerAutoProvider(PackBuilder.ProviderFactory<T>)`](../../../wover-datagen-api/src/main/java/de/ambertation/wover/datagen/api/WoverDataGenEntryPoint.java)
during mod init. The created provider must implement the marker interface
[`WoverAutoProvider`](../../../wover-datagen-api/src/main/java/de/ambertation/wover/datagen/api/WoverAutoProvider.java)
(this is verified at runtime). Implementing the nested `WoverAutoProvider.WithRedirect` additionally lets your
provider inspect, wrap, or suppress every other provider before it is added to a pack — this is how
`wover-tag-api` and `wover-block-api` inject their automatic loot-table/tag providers into every mod's Datapack.

### Multiple return values per provider factory

A single `WoverDataProvider<T>` factory can additionally implement
[`WoverDataProvider.Secondary<T>`](../../../wover-datagen-api/src/main/java/de/ambertation/wover/datagen/api/WoverDataProvider.java)
and/or `WoverDataProvider.Tertiary<T>` to contribute up to two extra `DataProvider`s alongside its primary one;
`WoverDataGenEntryPoint` automatically detects and registers them.
