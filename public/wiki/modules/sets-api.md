# wover-sets-api

Java API for defining a full "material set" — the common Minecraft pattern of a themed group of blocks and items
sharing a material, like a wood type (planks/logs/stairs/slabs/doors/boats/signs/...) or a stone type
(source/slab/stairs/wall/pillar/brick/tiles/...). It ties together `wover-block-api`, `wover-item-api`, and
`wover-recipe-api`: one `BlockSet` subclass configures every block/item a material needs, using ready-made
block-type factories that already know the right vanilla block class, block/item trait, client model, and
crafting/stonecutting recipe for each shape. The module also ships a parallel `EquipmentSet` API for tool/armor
materials (diamond-, netherite-, or custom-tier tool and armor pairs).

- **Gradle artifact:** `org.betterx:wover-sets-api`
- **Depends on:** `wover-core-api`, `wover-block-api`, `wover-item-api`, `wover-recipe-api`
- **Java packages:**
  - `org.betterx.wover.sets.api.blocks` — the `BlockSet`/`WoodenBlockSet` builders and slot machinery
  - `org.betterx.wover.sets.api.blocks.types` — one `SlotFactory` per block shape (planks, slab, stairs, door, chest, ...)
  - `org.betterx.wover.sets.api.blocks.slots` — ready-made `SlotMap`s for a full wood or stone family
  - `org.betterx.wover.sets.api.items` — support type for items registered as part of a set
  - `org.betterx.wover.complex.api.equipment` — the `EquipmentSet` builder for tool/armor materials
  - `org.betterx.wover.block.api.trait` (adds `BlockTraits`/`SignBlockDefinition`/`WallSignBlockDefinition` to the block module's package)
  - `org.betterx.wover.block.api.trait.behaviour` (adds `BlockTagTrait`/`LootTableTrait`/`MineableWithTagTrait`/`StripableBlockTrait`/`ValidForBlockEntityTypeTrait`)
  - `org.betterx.wover.block.api.trait.type` (adds `LogBlockTrait`/`BarkBlockTrait`)
  - `org.betterx.wover.item.api.trait` (adds `ItemTraits`/`BoatItemTrait`/`ElytraItemTrait` to the item module's package)
  - `org.betterx.wover.recipe.api` (adds `RecipeTraitLibrary` to the recipe module's package)
  - `org.betterx.wover.block.api.client.model` / `org.betterx.wover.block.api.client.trait` (client-only model traits)
  - `org.betterx.wover.item.api.client.model` / `org.betterx.wover.item.api.client.trait` (client-only, adds to the item module's client package)

## For Datapack Developers

`wover-sets-api` has no datapack-facing format of its own. Every block, item, tag, loot table, model, and recipe
a `BlockSet`/`EquipmentSet` produces is built through the ordinary `wover-block-api`/`wover-item-api`/
`wover-recipe-api` machinery, so the files that end up on disk are exactly the formats those modules' own wiki
pages already document:

| Content | File | Documented on |
|---|---|---|
| Block/item tags (e.g. a set's shared "logs" tag) | `data/<namespace>/tags/block\|item/<path>.json` | `tag-api` wiki page |
| Block loot tables | `data/<namespace>/loot_table/blocks/<path>.json` | `block-api` wiki page |
| Recipes (crafting/stonecutting/smithing) | `data/<namespace>/recipe/<path>.json` | `recipe-api` wiki page |
| Block/item models, blockstates | `assets/<namespace>/models/...`, `assets/<namespace>/blockstates/...` | `block-api` wiki page |

The only set-specific thing worth knowing as a datapack author is *how* these files get generated, since it
differs slightly per content type:

- **Recipes are automatic.** Every block-type factory in `org.betterx.wover.sets.api.blocks.types` attaches a
  `BlockRecipeTrait`/`ItemRecipeTrait` (built from `RecipeTraitLibrary`, matching vanilla's own recipe shapes) to
  the block/item it builds. `wover-recipe-api`'s `AutoRecipeProvider` picks these up for every mod automatically —
  a set's datagen entry point does not need to (and, in this module's test mod, does not) write any recipe code
  itself.
- **Models need one explicit call.** `BlockModelTrait`/`ItemModelTrait` (in `org.betterx.wover.block.api.client.trait`/
  `org.betterx.wover.item.api.client.trait`) are *not* auto-registered — a client-side `WoverModelProvider`
  subclass must call `BlockModelTrait.bootstrapModels(modCore, generator)` /
  `ItemModelTrait.bootstrapModels(modCore, itemModelGenerator)` once from its `bootstrapBlockStateModels`/
  `bootstrapItemModels` overrides (see the worked example below and this module's test mod
  `TestModelProvider`). Every model trait attached by the block-type factories then fires automatically.
- **Loot tables need one explicit call too.** A `WoverLootTableProvider` subclass must call
  `LootTableTrait.bootstrapLootTables(modCore, provider, biConsumer)` from its `boostrap(...)` override (see
  this module's test mod `TestLootProvider`); `org.betterx.wover.sets.api.blocks.types.Source` is the only
  block-type factory that attaches a `LootTableTrait` by default (`.dropSelf()`), so most set-built blocks fall
  back to whatever loot table your `BlockDefinition` configures separately.

Because every generated file is a plain vanilla (or `tag-api`) JSON file, overriding one from a lower- or
higher-priority datapack works exactly as described on those modules' wiki pages — nothing about a set changes
how the resulting files are read or merged.

## For Mod Developers

### Defining a wood set

[`WoodenBlockSet`](../../wover-sets-api/src/main/java/org/betterx/wover/sets/api/blocks/WoodenBlockSet.java)
already knows the default shape of a wood material: subclass it, call `super(...)` with a `ModCore`, base name,
and `MapColor`, and its `createDefaultDefinitions()` returns every slot from
[`WoodSlots`](../../wover-sets-api/src/main/java/org/betterx/wover/sets/api/blocks/slots/WoodSlots.java) (planks,
log/stripped log, bark/stripped bark, slab, stairs, fence, fence gate, door, trapdoor, button, pressure plate,
ladder, sign, hanging sign, chest, barrel, bookshelf, composter, crafting table). Override
`createDefaultDefinitions()` to add, remove, or replace individual slots — real usage, from this module's test
mod:

```java
public class TestWoodSet extends WoodenBlockSet<TestWoodSet> {
    public TestWoodSet() {
        super(TestModWoverSets.C, "wooden", MapColor.COLOR_MAGENTA);
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        SlotMap map = super.createDefaultDefinitions();
        map.replace(new Log(true, true, "_mossy"));   // override the default log with a mossy-textured variant
        map.replace(new Bark(true, true, "_mossy"));
        map.add(WoodSlots.WALL);                       // vanilla has no wooden walls; opt in explicitly
        return map;
    }
}
```

Building and registering the set is a single call, typically from `onInitialize()`:

```java
public class TestModWoverSets implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-sets-testmod");
    public static TestWoodSet woodBlockSet;

    @Override
    public void onInitialize() {
        woodBlockSet = new TestWoodSet().buildAndRegister();

        CreativeTabs.start(C)
                    .createTab("all")
                    .setIcon(woodBlockSet.getBlock(SlotType.LOG))
                    .buildAndAdd()
                    .processRegistries()
                    .registerAllTabs();
    }
}
```

`buildAndRegister()` walks every `SlotFactory` in the set's `SlotMap`, in order, building and registering each
block (via `BlockDefinition`) or item (via `ItemDefinition`, for slots like boats that only register an item).
Afterwards, retrieve individual blocks/items with
[`getBlock(SlotType)`](../../wover-sets-api/src/main/java/org/betterx/wover/sets/api/blocks/BlockSet.java)/`getItem(SlotType)`
(or `getBlockWithFallback(SlotType...)`, which falls back through several slots, then the set's base slot, then
vanilla stone) — [`SlotType`](../../wover-sets-api/src/main/java/org/betterx/wover/sets/api/blocks/SlotType.java)
is the key identifying each role (`SlotType.LOG`, `SlotType.SLAB`, `SlotType.CHEST`, ...).

### Defining a stone set

A plain [`BlockSet`](../../wover-sets-api/src/main/java/org/betterx/wover/sets/api/blocks/BlockSet.java) has no
default slots of its own (`createDefaultDefinitions()` returns an empty map) — pick constants from
[`StoneSlots`](../../wover-sets-api/src/main/java/org/betterx/wover/sets/api/blocks/slots/StoneSlots.java) and
combine them with `SlotMap.of(...)`:

```java
public class TestStoneSet extends BlockSet<TestStoneSet> {
    public TestStoneSet() {
        super(TestModWoverSets.C, "stony", SlotType.SOURCE);
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        return SlotMap.of(
                StoneSlots.SOURCE, StoneSlots.SLAB, StoneSlots.STAIRS, StoneSlots.WALL,

                StoneSlots.BRICK_SOURCE, StoneSlots.BRICK_SLAB,
                StoneSlots.BRICK_STAIRS, StoneSlots.BRICK_WALL,

                StoneSlots.WEATHERED_BRICK_SOURCE, StoneSlots.WEATHERED_SLAB,
                StoneSlots.WEATHERED_STAIRS, StoneSlots.WEATHERED_WALL
        );
    }
}
```

`StoneSlots` also has `TILES_*`, `CRACKED_*`, `CHISELED_*`, and `POLISHED_*` families built the same way — each
variant's recipe (crafting and/or stonecutting) is derived automatically from the slot it names as its source
(e.g. `BRICK_STAIRS` cuts from `BRICK_SOURCE`).

### Customizing individual slots

Every block-type factory in `org.betterx.wover.sets.api.blocks.types`
([`Planks`](../../wover-sets-api/src/main/java/org/betterx/wover/sets/api/blocks/types/Planks.java),
[`Slab`](../../wover-sets-api/src/main/java/org/betterx/wover/sets/api/blocks/types/Slab.java),
[`Stairs`](../../wover-sets-api/src/main/java/org/betterx/wover/sets/api/blocks/types/Stairs.java),
[`Door`](../../wover-sets-api/src/main/java/org/betterx/wover/sets/api/blocks/types/Door.java),
[`Chest`](../../wover-sets-api/src/main/java/org/betterx/wover/sets/api/blocks/types/Chest.java), ...) extends
[`SlotFromDefinition`](../../wover-sets-api/src/main/java/org/betterx/wover/sets/api/blocks/SlotFromDefinition.java)
(or its wood-only variant,
[`WoodenSlotFromDefinition`](../../wover-sets-api/src/main/java/org/betterx/wover/sets/api/blocks/WoodenSlotFromDefinition.java)),
which builds the block through `BlockDefinition` and lets a subclass override:

| Hook | Purpose |
|---|---|
| `startBlockDefinition(registry, set, name)` | picks the `Block` subclass/constructor |
| `addSlotSpecificDefinitions(set, def)` | adds traits/tags/properties before the block is built |
| `buildModel(set, traitLookup)` | returns the client model trait (see `ModelTraitLibrary`) |
| `buildRecipe(set, traitLookup)` | returns the auto-generated recipe trait (see `RecipeTraitLibrary`) |

Most constructors also take an explicit `SlotType` (and, for stone-family variants, a *source* `SlotType` to cut
the recipe/model from) — this is how `StoneSlots.BRICK_STAIRS` is built as
`new Stairs(SlotType.BRICK, SlotType.BRICK_STAIRS)`. To fully customize a shape beyond what a constructor
exposes, subclass the factory and override the relevant hook, then swap it in with
`SlotMap#replace(SlotFactory)` (as `TestWoodSet` does above for `Log`/`Bark`).

### Sign blocks and other multi-block slots

Signs are a block-set-specific complication: vanilla wants a standing/hanging block *and* a wall block, sharing
one `BlockItem`. [`SignBlockDefinition`](../../wover-sets-api/src/main/java/org/betterx/wover/block/api/trait/SignBlockDefinition.java)
(in `org.betterx.wover.block.api.trait`, contributed by this module) handles that pairing —
`org.betterx.wover.sets.api.blocks.types.Sign`/`HangingSign` build one, then override
`finalizeBlockDefinitions` to report *both* the primary and wall block back to the `BlockSet` under two different
`SlotType`s (`SlotType.SIGN` + `Sign.WALL_SIGN`, `SlotType.HANGING_SIGN` + `HangingSign.HANGING_WALL_SIGN`). This
is the pattern to follow for any custom slot that needs to register more than one block.

### The block/item trait libraries backing a set

Two collections tie a set's blocks/items to the trait system documented on the `block-api`/`item-api` wiki
pages:

- [`BlockTraits`](../../wover-sets-api/src/main/java/org/betterx/wover/block/api/trait/BlockTraits.java) adds
  every set-relevant block trait (`BlockTraits.SLAB_BLOCK`, `BlockTraits.DOOR_BLOCK`, `BlockTraits.LOG_BLOCK`,
  material traits `BlockTraits.WOOD_BLOCK`/`STONE_BLOCK`, ...) to the shared registry started by `wover-block-api`.
- [`ItemTraits`](../../wover-sets-api/src/main/java/org/betterx/wover/item/api/trait/ItemTraits.java) adds
  `ItemTraits.BOAT_ITEM`/`ItemTraits.ELYTRA_ITEM`/`ItemTraits.IS_FIREPROOF` to the equivalent item-side registry.

Both are consumed the normal way (`definition.addTrait(BlockTraits.DOOR_BLOCK)`), and both are what the
block-type factories in `org.betterx.wover.sets.api.blocks.types` use internally — using them directly is only
needed when hand-building a block/item outside the `BlockSet` machinery but still wanting one of these behaviors
(e.g. `BlockTraits.STRIPABLE` on a custom block, or `BlockTraits.MINEABLE_WITH.needsPickAxe()`).

### Recipe shapes: `RecipeTraitLibrary`

[`RecipeTraitLibrary`](../../wover-sets-api/src/main/java/org/betterx/wover/recipe/api/RecipeTraitLibrary.java)
(in `org.betterx.wover.recipe.api`, contributed by this module) is the collection of ready-made recipe shapes
every block-type factory pulls from — `RecipeTraitLibrary.slab(...)`, `.stairs(...)`, `.door(...)`, `.chest(...)`,
and so on, each matching the equivalent vanilla recipe (including stonecutting recipes where vanilla has one).
Every method takes one or more
[`RecipeMaterial`](../../wover-recipe-api/src/main/java/org/betterx/wover/recipe/api/RecipeMaterial.java)
arguments — usually `set.recipeBaseMaterial()` or `set.recipeMaterial(SlotType)`, which *defer* the actual block
lookup until the recipe is built (since the recipe trait is attached before every block in the set necessarily
exists yet). If a deferred material turns out invalid once the recipe is actually generated (e.g. the referenced
slot was never registered), the recipe is skipped with a log warning rather than failing datagen. Calling these
methods directly is only useful when hand-authoring a recipe trait for a block/item outside a `BlockSet`.

### Tool and armor materials with `EquipmentSet`

[`EquipmentSet`](../../wover-sets-api/src/main/java/org/betterx/wover/complex/api/equipment/EquipmentSet.java) is
the tool/armor equivalent of `BlockSet`: subclass it, call `add(ToolSlot)`/`add(ArmorSlot)` (or an overload with a
custom item factory/recipe override) from the constructor for every piece the material should have, backed by a
[`ToolTier`](../../wover-sets-api/src/main/java/org/betterx/wover/complex/api/equipment/ToolTier.java)/
[`ArmorTier`](../../wover-sets-api/src/main/java/org/betterx/wover/complex/api/equipment/ArmorTier.java) pair
(ready-made vanilla tiers live in
[`ToolTiers`](../../wover-sets-api/src/main/java/org/betterx/wover/complex/api/equipment/ToolTiers.java)/
[`ArmorTiers`](../../wover-sets-api/src/main/java/org/betterx/wover/complex/api/equipment/ArmorTiers.java)).
Real usage, from this module's test mod:

```java
public class TestEquipmentSet extends EquipmentSet {
    public static final TestEquipmentSet INSTANCE = new TestEquipmentSet();

    public TestEquipmentSet() {
        super(TestModWoverSets.C, "test_equipment_set", ToolTiers.DIAMOND_TOOL, ArmorTiers.TURTLE_ARMOR, Items.STONE);

        add(ToolSlot.PICKAXE_SLOT);
        add(ToolSlot.AXE_SLOT);
        add(ToolSlot.SHOVEL_SLOT);
        add(ToolSlot.HOE_SLOT);
        add(ToolSlot.SWORD_SLOT);

        add(ArmorSlot.HELMET_SLOT);
        add(ArmorSlot.CHESTPLATE_SLOT);
        add(ArmorSlot.LEGGINGS_SLOT);
        add(ArmorSlot.BOOTS_SLOT);
    }
}
```

Each `add(...)` call immediately builds and registers the item (using a sensible default `AxeItem`/`HoeItem`/
`ShovelItem`/`ShearsItem`/digger-`Item` for tools, a plain `Item` for armor, unless a custom factory is passed)
and its recipe — a plain crafting recipe by default, or a smithing-transform recipe if the tier's
`ToolValues`/`ArmorValues` for that slot specify a `SmithingTemplateItem` *and* the set was constructed with a
template base set to upgrade from (the constructor's optional `Supplier<EquipmentSet>` parameter, resolved
lazily so it can point at a set defined later in the same class). Retrieve the built items afterwards with
`get(ToolSlot)`/`get(ArmorSlot)`, or `getAll()`/`getTools()`/`getArmorPieces()`.

To build a custom tier from an existing one (e.g. a slightly-stronger variant of vanilla diamond), use
`ToolTier#copyWithOffset`/`ArmorTier#copyWithOffset`, which applies the same offset to every configured slot.
