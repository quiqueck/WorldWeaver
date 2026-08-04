# wover-recipe-api

Java API for building crafting, cooking, smithing and stonecutting recipes with a fluent builder, for hooking
into brewing-stand recipes, and for generating recipe JSON at datagen time. It also lets `wover-block-api` and
`wover-item-api` block/item definitions carry a "recipe trait" so their recipe is generated automatically, and
extends the vanilla `RecipeManager` so mods can register extra recipes at runtime without a backing datapack
file.

- **Gradle artifact:** `de.ambertation:worldweaver` (single artifact; this module ships inside it as the Fabric mod `wover-recipe`)
- **Depends on:** `wover-core-api`, `wover-item-api`, `wover-block-api`, `wover-event-api`, `wover-tag-api`,
  `wover-datagen-api`
- **Java packages:**
  - `de.ambertation.wover.recipe.api`
  - `de.ambertation.wover.potions.api`
  - `de.ambertation.wover.item.api.trait` (adds `ItemRecipeTrait` to the item-trait package owned by
    `wover-item-api`)
  - `de.ambertation.wover.block.api.trait` (adds `BlockRecipeTrait` to the block-trait package owned by
    `wover-block-api`)
  - `de.ambertation.wover.datagen.api.provider` (adds `WoverRecipeProvider` to the datagen module's package)

## For Datapack Developers

Recipes produced by this module are ordinary vanilla recipe JSON — `wover-recipe-api` does not invent a new
schema. Every recipe generated through `RecipeBuilder` (or a `WoverRecipeProvider`) ends up at:

```
data/<namespace>/recipe/<path>.json
```

with the standard vanilla `type` + type-specific fields. The builders in this module map onto the following
vanilla recipe types (as used by Minecraft 1.21.x):

| Builder | Vanilla `type` | Notes |
|---|---|---|
| `RecipeBuilder.crafting(...)` (shaped, default) | `minecraft:crafting_shaped` | `pattern` + `key`, produced by `.shape(...)` and `.addMaterial(...)` |
| `RecipeBuilder.crafting(...).shapeless()` | `minecraft:crafting_shapeless` | `ingredients`, one per `.addMaterial(...)` call |
| `RecipeBuilder.smelting(...)` | `minecraft:smelting` | `ingredient`, `experience`, `cookingtime` |
| `RecipeBuilder.blasting(...)` / `.enableBlastFurnace()` | `minecraft:blasting` | same fields, default cooking time is halved |
| `RecipeBuilder.smoker(...)` / `.enableSmoker()` | `minecraft:smoking` | same fields, default cooking time is halved |
| `RecipeBuilder.campfire(...)` / `.enableCampfire()` | `minecraft:campfire_cooking` | same fields, default cooking time is tripled |
| `RecipeBuilder.smithing(...)` | `minecraft:smithing_transform` | `template`, `base`, `addition`, `result` |
| `RecipeBuilder.stonecutting(...)` | `minecraft:stonecutting` | `ingredient`, `result` (id + count) |

A shaped recipe (`crafting_shaped`) looks like this:

```json
{
  "type": "minecraft:crafting_shaped",
  "category": "misc",
  "group": "my_group",
  "show_notification": true,
  "pattern": [
    "III",
    "ICI",
    "III"
  ],
  "key": {
    "I": { "item": "minecraft:iron_block" },
    "C": { "item": "minecraft:coal_block" }
  },
  "result": {
    "id": "minecraft:diamond",
    "count": 1
  }
}
```

A cooking recipe (`smelting`/`blasting`/`smoking`/`campfire_cooking`) looks like this:

```json
{
  "type": "minecraft:smelting",
  "category": "misc",
  "ingredient": { "item": "minecraft:iron_ore" },
  "result": { "id": "minecraft:iron_ingot" },
  "experience": 0.7,
  "cookingtime": 200
}
```

### Recipe-unlock advancements

Whenever a recipe is built with `shouldUnlockAdvancements` left at its default of `true` (see
`BaseUnlockableRecipeBuilder`), an ordinary recipe-unlock advancement is written alongside it, exactly the way
vanilla's own datagen `RecipeProvider` does: a `minecraft:recipe_unlocked` reward pointing at the recipe, with an
`inventory_changed`-style trigger for every criterion added via `.unlockedBy(...)`/`.unlocks(...)`. This is not a
WoVer-specific format — it's the same file vanilla writes at `data/<namespace>/advancement/<path>.json`.

### Brewing recipes are not datapack-driven

Unlike crafting/cooking/smithing/stonecutting recipes, vanilla Minecraft does **not** read brewing-stand recipes
(potion mixes, container conversions, valid containers) from any datapack JSON format — they are hard-coded in
`PotionBrewing#bootstrap`. `wover-recipe-api` mirrors that: `PotionManager.BOOTSTRAP_POTIONS` /
`OnBootstrapPotions` (see below) is a Java-code hook into that same bootstrap step, not a file format. If you are
hand-authoring a datapack, there is no brewing-recipe file to write for this module.

### WoVer-specific extension: recipes without a JSON file

Two mechanisms in this module add recipes to a running game without them ever existing as a JSON file on disk:

- **`RecipeBuilder.BOOTSTRAP_RECIPES`** — a runtime event, fired while the vanilla `RecipeManager` applies newly
  loaded recipes. Subscribers build recipes with the same `RecipeBuilder` API used for datagen, and they are
  merged into the manager's recipe map in memory (see `RecipeManagerMixin`). This is useful for recipes that
  depend on runtime state, or for mods that want to skip generating/shipping recipe JSON entirely.
- **`BlockRecipeTrait` / `ItemRecipeTrait`** — see "Recipe traits" below; these run at *datagen* time (they
  generate real JSON files), not at runtime.

## For Mod Developers

### Building a recipe with `RecipeBuilder`

`RecipeBuilder`'s static factory methods return a fluent, type-specific builder. Configure it, then call
`.build(context)` to write it — either inside a `WoverRecipeProvider` (datagen) or an `OnBootstrapRecipes`
subscriber (runtime); both are handed a matching `RecipeBuilder.Context`.

```java
RecipeBuilder.crafting(C.mk("test_recipe"), Items.BEDROCK)
             .addMaterial('D', Items.DIAMOND)
             .addMaterial('B', Items.BASALT)
             .shape(" D ", "DBD", " D ")
             .outputCount(2)
             .showNotification()
             .build(context);
```

Factory methods, one per recipe kind:

| Method | Returns | Vanilla type |
|---|---|---|
| `RecipeBuilder.crafting(id, output)` | `CraftingRecipeBuilder` | shaped (default) or shapeless (`.shapeless()`) |
| `RecipeBuilder.stonecutting(id, output)` | `StonecutterRecipeBuilder` | stonecutting |
| `RecipeBuilder.smithing(id, output)` | `SmithingRecipeBuilder` | smithing transform |
| `RecipeBuilder.cooking(id, output)` | `CookingRecipeBuilder`, no device enabled | — use `.enableSmelter()` etc. |
| `RecipeBuilder.smelting(id, output)` | `CookingRecipeBuilder`, furnace enabled | smelting |
| `RecipeBuilder.blasting(id, output)` | `CookingRecipeBuilder`, blast furnace + furnace enabled | blasting + smelting |
| `RecipeBuilder.smoker(id, output)` | `CookingRecipeBuilder`, smoker enabled | smoking |
| `RecipeBuilder.campfire(id, output)` | `CookingRecipeBuilder`, campfire enabled | campfire_cooking |
| `RecipeBuilder.cookableFood(id, output)` | `CookingRecipeBuilder`, campfire + smoker enabled | campfire_cooking + smoking |
| `RecipeBuilder.copySmithingTemplate(id, costLevel, template, center)` | `CraftingRecipeBuilder` | shaped, ready-made "duplicate smithing template" recipe |

A `CookingRecipeBuilder` can target more than one device at once — call the matching `enableX()`/`disableX()`
methods to adjust which of them get a recipe written for them; each enabled device produces its own recipe file,
suffixed with `_smelting`/`_blasting`/`_smoker`/`_campfire`. `cookingTime(...)` sets the *base* (furnace) time;
blast furnace and smoker default to half of it, campfire to triple it, matching vanilla's own ratios.

`BaseUnlockableRecipeBuilder` (implemented by `CraftingRecipeBuilder`, `SmithingRecipeBuilder`,
`StonecutterRecipeBuilder`) adds an unlock criterion automatically for every material passed to
`.addMaterial(...)`/`.input(...)`/`.base(...)`/`.addon(...)`/`.template(...)`; call `.unlockedBy(...)` /
`.unlocks(...)` to add further criteria, or `.shouldUnlockAdvancements(false)` to suppress the advancement
entirely.

`RecipeMaterial` lets a single value stand in for a tag, one or more items, one or more item stacks, or a vanilla
`Ingredient` wherever a builder accepts one (e.g. `CraftingRecipeBuilder#addMaterial(char, RecipeMaterial)`,
`CookingRecipeBuilder#input(RecipeMaterial)`), including deferred variants (`RecipeMaterial.ofDeferredTag(...)`
etc.) for values that aren't available yet when the material is created.

### Generating recipes during datagen with `WoverRecipeProvider`

Subclass `WoverRecipeProvider`, implement `bootstrap(...)`, and register it on a `PackBuilder` from a
`WoverDataGenEntryPoint`:

```java
public class TestRecipeProvider extends WoverRecipeProvider {
    public TestRecipeProvider(ModCore modCore) {
        super(modCore, "recipes");
    }

    @Override
    protected void bootstrap(RecipeBuilder.Context context) {
        RecipeBuilder.crafting(modCore.mk("test_recipe"), Items.BEDROCK)
                     .addMaterial('D', Items.DIAMOND)
                     .addMaterial('B', Items.BASALT)
                     .shape(" D ", "DBD", " D ")
                     .outputCount(2)
                     .showNotification()
                     .build(context);
    }
}

public class TestModWoverRecipeDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        globalPack.addProvider(TestRecipeProvider::new);
    }

    @Override
    protected ModCore modCore() {
        return TestModWoverRecipe.C;
    }
}
```

### Adding recipes at runtime with `BOOTSTRAP_RECIPES`

Subscribe to `RecipeBuilder.BOOTSTRAP_RECIPES` (typically from `onInitialize()`) to inject recipes without a
backing JSON file, using the exact same builder API:

```java
RecipeBuilder.BOOTSTRAP_RECIPES.subscribe((ctx) -> {
    RecipeBuilder.crafting(C.mk("test_diamoan_recipe"), Items.DIAMOND)
                 .addMaterial('C', Items.COAL_BLOCK)
                 .addMaterial('I', Items.IRON_BLOCK)
                 .shape("III", "ICI", "III")
                 .outputCount(1)
                 .showNotification()
                 .build(ctx);
});
```

### Recipe traits: auto-generating recipes for registered blocks/items

`BlockRecipeTrait` (`de.ambertation.wover.block.api.trait`) and `ItemRecipeTrait` (`de.ambertation.wover.item.api.trait`)
let a `BlockDefinition`/`ItemDefinition` (from `wover-block-api`/`wover-item-api`) carry its own recipe factory,
so the recipe is generated automatically instead of being written by hand in a separate provider:

```java
def.addTrait(
        BlockRecipeTraitBuilder.BUILDER.with((key, block, context) ->
                RecipeBuilder.crafting(key.location(), block)
                             .addMaterial('#', Items.IRON_INGOT)
                             .shape("###", "# #", "###")
                             .build(context)
        )
);
```

The factory only runs during datagen (`Builder#with` returns `null` outside of a datagen environment, so the
trait is simply absent from the runtime block). Every block/item carrying the trait has its recipe built
automatically by `AutoRecipeProvider`, an internal `WoverRecipeProvider` this module registers as an auto
provider for *every* mod (see `LibWoverRecipe`) — nothing needs to be registered manually. If you need more
control, `BlockRecipeTrait.bootstrapRecipes(modCore, context)` / `ItemRecipeTrait.bootstrapRecipes(modCore, context)`
(optionally with a `BiPredicate` filter) can be called directly from your own `WoverRecipeProvider`.

### Block-family recipe shortcuts: `RecipeBuilder.Templates`

For mods generating a full family of variants from a base block (stairs, slabs, walls, buttons, pressure plates,
roof tiles, ...), `RecipeBuilder.Templates` bundles the common shapes so you don't have to redefine them:

```java
RecipeBuilder.Templates templates = new RecipeBuilder.Templates(context, C);
templates.makeStairsRecipe(baseBlock, stairsBlock);   // crafting + stonecutting
templates.makeSlabRecipe(baseBlock, slabBlock);       // crafting + stonecutting
templates.makeWallRecipe(baseBlock, wallBlock);       // crafting + stonecutting
templates.makeButtonRecipe(baseBlock, buttonBlock);
templates.makePlateRecipe(baseBlock, plateBlock);
templates.makeRoofRecipe(baseBlock, roofBlock);
```

Each `make...Recipe` call both builds and immediately writes its recipe(s) to the `context` passed to the
`Templates` constructor.

### Brewing recipes with `PotionManager`

Register a new potion, then subscribe to `PotionManager.BOOTSTRAP_POTIONS` to add mixes/containers to vanilla's
own `PotionBrewing.Builder` — the same builder instance vanilla's `PotionBrewing#bootstrap` populates, so any
method available on it in vanilla (adding a mix, a container-conversion recipe, or a valid container item) can be
called from here:

```java
public static final Holder<Potion> MY_POTION =
        PotionManager.registerPotion(MyMod.C, "my_potion", MyEffects.MY_EFFECT, 3600);

PotionManager.BOOTSTRAP_POTIONS.subscribe((PotionBrewing.Builder builder) -> {
    // e.g. builder.addMix(Potions.WATER, MyItems.MY_REAGENT, MY_POTION.value());
});
```

Because vanilla does not expose brewing recipes through a datapack file, this Java hook is the only way to add
brewing-stand recipes for this module — there is no JSON counterpart to generate during datagen.
