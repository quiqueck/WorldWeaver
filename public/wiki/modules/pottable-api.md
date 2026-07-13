# wover-pottable-api

Java API for a datapack-driven registry of plants and soils that can be combined inside a flower pot. It provides
two small, symmetric registries — `PottablePlantRegistry` for the plant blocks, `PottableSoilRegistry` for the
soil blocks they can be potted on — plus a block-trait/datagen pair that lets a mod tag its own blocks and have
the registry entries generated automatically, without writing a custom datagen provider.

- **Gradle artifact:** `org.betterx:wover-pottable-api`
- **Depends on:** `wover-core-api`, `wover-block-api`, `wover-tag-api`, `wover-event-api`, `wover-datagen-api`
- **Java packages:**
  - `org.betterx.wover.pottable.api`
  - `org.betterx.wover.pottable.api.datagen`
  - `org.betterx.wover.pottable.api.trait`

Note: this module only maintains the *registry data* (which blocks are pottable plants/soils, and which soils a
given plant accepts). It does not itself contain any mixin or code that wires this data into vanilla's flower pot
block/item — nothing in this module or elsewhere in this repository currently reads from
`PottablePlantRegistry`/`PottableSoilRegistry` at runtime to place blocks into an actual in-world pot. Treat it as
a shared, datapack-extensible source of truth that a mod (or a future WoVer module) can query when implementing
the actual potting mechanic.

## For Datapack Developers

Both registries are ordinary Minecraft datapack registries (created via WoVer's `DatapackRegistryBuilder`, the
same mechanism used across the WoVer modules), so their entries are plain JSON files:

```
data/<namespace>/wover/pottable_plant/<name>.json
data/<namespace>/wover/pottable_soil/<name>.json
```

A `pottable_soil` entry only references the soil block itself:

```json
{
  "block": "minecraft:dirt"
}
```

A `pottable_plant` entry references the plant block and, optionally, a block tag restricting which soils it may
be potted on. If `valid_soils` is omitted, the plant can be potted on **any** registered `PottableSoil`:

```json
{
  "block": "mymod:my_sapling",
  "valid_soils": "mymod:pottable_soils/saplings"
}
```

These files are ordinary datapack registry entries, so they follow the usual datapack overriding rules: a
higher-priority pack can replace or add entries at the same path, and mods that register their pottable
blocks/soils through this module's block-trait/datagen mechanism (see below) still just produce these same JSON
files at build time — nothing about the format changes depending on how the entry was authored.

## For Mod Developers

There are two ways to register pottable plants/soils in code: call the registries directly from a datagen
`BootstrapContext`, or (the recommended, no-boilerplate way) tag your blocks with a trait and let this module's
datagen providers do it for you.

### Registering directly with `PottablePlantRegistry`/`PottableSoilRegistry`

[`PottablePlantRegistry`](../../wover-pottable-api/src/main/java/org/betterx/wover/pottable/api/PottablePlantRegistry.java)
and
[`PottableSoilRegistry`](../../wover-pottable-api/src/main/java/org/betterx/wover/pottable/api/PottableSoilRegistry.java)
expose static `register(...)` overloads, called from inside a
`BootstrapContext<PottablePlant>`/`BootstrapContext<PottableSoil>` (e.g. your own `WoverRegistryContentProvider`):

```java
// pottable on any registered soil
PottablePlantRegistry.register(
        context,
        PottablePlantRegistry.createKey(MyMod.C.id("my_sapling")),
        MyBlocks.MY_SAPLING
);

// pottable only on soils matching a tag
PottablePlantRegistry.register(
        context,
        PottablePlantRegistry.createKey(MyMod.C.id("my_flower")),
        MyBlocks.MY_FLOWER,
        MyTags.MY_SOILS
);

// register a soil block
PottableSoilRegistry.register(
        context,
        PottableSoilRegistry.createKey(MyMod.C.id("my_soil")),
        MyBlocks.MY_SOIL
);
```

Both `register` overloads accepting a `Block` resolve it to its `ResourceKey<Block>` via
`block.builtInRegistryHolder().key()`; there's also a lowest-level overload on `PottablePlantRegistry` that takes
a `ResourceKey<Block>` and an `Optional<TagKey<Block>>` directly, if you need it.

The returned [`PottablePlant`](../../wover-pottable-api/src/main/java/org/betterx/wover/pottable/api/PottablePlant.java)/
[`PottableSoil`](../../wover-pottable-api/src/main/java/org/betterx/wover/pottable/api/PottableSoil.java) wraps
just the block's `ResourceKey` (and, for plants, the optional soil tag); `PottablePlant#isValidSoil(Block)` checks
whether a given soil block matches the plant's `validSoils` tag (always `true` if the plant has no restriction).

### Registering via the block trait (recommended)

[`PottablePlantBlockTrait`](../../wover-pottable-api/src/main/java/org/betterx/wover/pottable/api/trait/PottablePlantBlockTrait.java)
and
[`PottableSoilBlockTrait`](../../wover-pottable-api/src/main/java/org/betterx/wover/pottable/api/trait/PottableSoilBlockTrait.java)
plug into `wover-block-api`'s trait system (`BlockDefinition#addTrait(...)`, see the `block-api` wiki page). Attach
them at the block's normal registration site:

```java
public class MyBlocks {
    private static final BlockRegistry R = BlockRegistry.forMod(MyMod.C);

    public static final SaplingBlock MY_SAPLING = R
            .defineDefaultBlockWithProps("my_sapling", MySaplingBlock::new)
            .addTrait(PottablePlantBlockTrait.any())
            .buildAndRegister();

    public static final FlowerBlock MY_FLOWER = R
            .defineDefaultBlockWithProps("my_flower", MyFlowerBlock::new)
            .addTrait(PottablePlantBlockTrait.withSoils(MyTags.MY_SOILS))
            .buildAndRegister();

    public static final Block MY_SOIL = R
            .defineDefaultBlock("my_soil")
            .addTrait(PottableSoilBlockTrait.DEFAULT)
            .buildAndRegister();
}
```

`PottablePlantBlockTrait.any()` returns a shared instance for plants pottable on any soil;
`PottablePlantBlockTrait.withSoils(TagKey<Block>)` returns a (cached) instance restricted to a soil tag.
`PottableSoilBlockTrait.DEFAULT` is the single, shared soil trait instance (there's no per-block configuration for
soils).

Then register the matching datagen provider(s) once, from your `WoverDataGenEntryPoint`:

```java
globalPack.addRegistryProvider(WoverPottablePlantRegistryProvider::new);
globalPack.addRegistryProvider(WoverPottableSoilRegistryProvider::new);
```

[`WoverPottablePlantRegistryProvider`](../../wover-pottable-api/src/main/java/org/betterx/wover/pottable/api/datagen/WoverPottablePlantRegistryProvider.java)
and
[`WoverPottableSoilRegistryProvider`](../../wover-pottable-api/src/main/java/org/betterx/wover/pottable/api/datagen/WoverPottableSoilRegistryProvider.java)
scan every block registered through your mod's `BlockRegistry` (via
`PottablePlantBlockTrait.bootstrapPottablePlants`/`PottableSoilBlockTrait.bootstrapPottableSoils`, which you can
also call yourself from a custom provider if you need a filter other than "every block with the trait") and
generate the `wover/pottable_plant`/`wover/pottable_soil` JSON entries for you — no custom provider class needed
for the common case.
