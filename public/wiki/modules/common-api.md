# wover-common-api

Lightweight, dependency-free interfaces shared across all WorldWeaver modules. Other WorldWeaver modules (and
mixins) implement these interfaces on vanilla classes to plug into WorldWeaver's world-generation lifecycle.

- **Gradle artifact:** `org.betterx:wover-common-api`
- **Depends on:** nothing (this is the root module)
- **Java packages:**
  - `org.betterx.wover.common.api`
  - `org.betterx.wover.common.generator.api.biomesource`
  - `org.betterx.wover.common.generator.api.chunkgenerator`
  - `org.betterx.wover.common.item.api`
  - `org.betterx.wover.common.registry.api`
  - `org.betterx.wover.common.surface.api`

## For Datapack Developers

This module has no datapack-facing surface — it only defines Java interfaces. You can skip it unless you are
also writing Java code.

## For Mod Developers

`wover-common-api` is almost never used directly. It exists so that other WorldWeaver modules (and your own mixins,
if you write them) can expose functionality on vanilla classes without a hard dependency on the rest of
WorldWeaver. You will typically only touch these interfaces if you are implementing your own
`BiomeSource`/`ChunkGenerator`, or if you want a custom `Item` to hook into WorldWeaver's item-stack setup.

### Custom BiomeSource / ChunkGenerator interop

If you write a custom `BiomeSource` or `ChunkGenerator`, implement the relevant interfaces so WorldWeaver can
manage it the same way it manages vanilla generators:

| Interface | Purpose |
|---|---|
| [`BiomeSourceWithSeed`](../../wover-common-api/src/main/java/org/betterx/wover/common/generator/api/biomesource/BiomeSourceWithSeed.java) | Receive the world seed. |
| [`BiomeSourceWithConfig<B,C>`](../../wover-common-api/src/main/java/org/betterx/wover/common/generator/api/biomesource/BiomeSourceWithConfig.java) + [`BiomeSourceConfig<B>`](../../wover-common-api/src/main/java/org/betterx/wover/common/generator/api/biomesource/BiomeSourceConfig.java) | Expose a config object so WorldWeaver can decide if a new config can be hot-applied or needs a chunk repair. |
| [`MergeableBiomeSource<B>`](../../wover-common-api/src/main/java/org/betterx/wover/common/generator/api/biomesource/MergeableBiomeSource.java) | Merge biome lists when another mod's generator is layered on top of yours. |
| [`ReloadableBiomeSource`](../../wover-common-api/src/main/java/org/betterx/wover/common/generator/api/biomesource/ReloadableBiomeSource.java) | React to datapack/resource reloads. |
| [`BiomeSourceWithNoiseRelatedSettings`](../../wover-common-api/src/main/java/org/betterx/wover/common/generator/api/biomesource/BiomeSourceWithNoiseRelatedSettings.java) / [`NoiseGeneratorSettingsProvider`](../../wover-common-api/src/main/java/org/betterx/wover/common/generator/api/biomesource/NoiseGeneratorSettingsProvider.java) | Read/expose the active `NoiseGeneratorSettings`. |
| [`EnforceableChunkGenerator<G>`](../../wover-common-api/src/main/java/org/betterx/wover/common/generator/api/chunkgenerator/EnforceableChunkGenerator.java) | Allow WorldWeaver to force-install this generator for a dimension. |
| [`RestorableBiomeSource<B>`](../../wover-common-api/src/main/java/org/betterx/wover/common/generator/api/chunkgenerator/RestorableBiomeSource.java) | Restore the original biome source after a merge. |
| [`RebuildableFeaturesPerStep<G>`](../../wover-common-api/src/main/java/org/betterx/wover/common/generator/api/chunkgenerator/RebuildableFeaturesPerStep.java) | Rebuild the per-step feature cache after features changed. |

These interfaces are implemented internally by WorldWeaver's own generators (see `wover-generator-api`) via mixins
into `NoiseBasedChunkGenerator`; most mod developers never implement them directly unless building a fully custom
world generator.

### Custom surface rules

If your mod already overwrites `SurfaceRules.RuleSource` (e.g. in `NoiseGeneratorSettings`), implement
[`SurfaceRuleProvider`](../../wover-common-api/src/main/java/org/betterx/wover/common/surface/api/SurfaceRuleProvider.java)
so WorldWeaver's `wover-surface-api` can merge its own rules (see [surface-api](surface-api.md)) into yours instead
of overwriting them.

### Custom Item stack setup

Implement [`ItemWithCustomStack`](../../wover-common-api/src/main/java/org/betterx/wover/common/item/api/ItemWithCustomStack.java)
on your `Item` class to run setup code whenever a new `ItemStack` is created via a WorldWeaver creative tab or
`/give`.

### Attaching custom data to a Registry

[`CustomRegistryData`](../../wover-common-api/src/main/java/org/betterx/wover/common/registry/api/CustomRegistryData.java)
is implemented (via mixin) by every `MappedRegistry`. It lets you attach arbitrary typed data to a registry using a
`DataKey<T>`:

```java
CustomRegistryData.DataKey<MyData> KEY = CustomRegistryData.createKey(ResourceLocation.fromNamespaceAndPath("mymod", "my_data"));

CustomRegistryData reg = (CustomRegistryData) myRegistry; // e.g. BuiltInRegistries.BLOCK
MyData data = reg.wover_computeDataIfAbsent(KEY, id -> new MyData());
```
