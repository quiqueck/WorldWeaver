# wover-item-api

Java API for defining Minecraft items with a fluent builder pattern, plus creative-mode tabs, custom armor
materials, smithing templates, an item-trait system for composable behavior, and helpers for datapack-driven
enchantments. It wraps the vanilla `Item.Properties`/`Item.Builder`-style setup behind per-item-type configuration
classes (`ItemDefinition` and its subclasses) so mod code registers items, tools, armor, food, spawn eggs, boats
and smithing templates through one consistent API.

- **Gradle artifact:** `org.betterx:wover-item-api`
- **Depends on:** `wover-core-api`, `wover-tag-api`, `wover-event-api`
- **Java packages:**
  - `org.betterx.wover.item.api`
  - `org.betterx.wover.item.api.armor`
  - `org.betterx.wover.item.api.smithing`
  - `org.betterx.wover.item.api.trait`
  - `org.betterx.wover.item.api.client.trait` (client-only)
  - `org.betterx.wover.tabs.api`
  - `org.betterx.wover.tabs.api.interfaces`
  - `org.betterx.wover.enchantment.api`
  - `org.betterx.wover.datagen.api.provider` (adds `WoverEnchantmentProvider` and `WoverLootTableProvider` to the
    datagen module's package)

## For Datapack Developers

Most of what this module produces — items, tools, armor, spawn eggs, smithing templates, creative tabs — is
registered directly in Java code, not read from a datapack. Two parts of this module *are* datapack-driven,
however, and both use the standard vanilla file formats: WoVer does not invent a new JSON schema for either.

### Enchantments — `data/<namespace>/enchantment/<path>.json`

Enchantments in modern Minecraft are ordinary datapack registry entries. `EnchantmentKey`/`EnchantmentManager` are
just typed Java helpers around building the vanilla `Enchantment` object and registering it with a
`BootstrapContext<Enchantment>` — the resulting JSON is exactly the vanilla enchantment format (`description`,
`supported_items`, `slots`, `weight`, `max_level`, `min_cost`/`max_cost`, `effects`, etc.), whether it was produced
by a `WoverEnchantmentProvider` at datagen time or written by hand. If you're hand-authoring an enchantment file for
a mod that uses this module, you can ignore the Java API entirely and just write the file at
`data/<namespace>/enchantment/<path>.json` as usual.

### Loot tables — `data/<namespace>/loot_table/<path>.json`

`WoverLootTableProvider` generates standard `LootTable` JSON at `data/<namespace>/loot_table/<path>.json`. The only
difference from Fabric's own loot table datagen helper is that WoVer's `boostrap(...)` callback is handed a
`HolderLookup.Provider`, so loot tables that reference enchantments (e.g. via `EnchantmentLevelProvider` or the
`Silk Touch`/`Fortune` condition helpers) can look them up during datagen.

### Item tags — `data/<namespace>/tags/item/<path>.json`

Item tags for items registered through `ItemRegistry` are ordinary tag files handled by `wover-tag-api`'s datagen
machinery (see the `tag-api` wiki page). Two mechanisms feed tags into that machinery from this module:

- Tags passed to `.addTags(...)` on an `ItemDefinition` are collected and written automatically during datagen.
- An `Item` implementation can implement `ItemTagProvider` and its `registerItemTags(location, context)` is called
  automatically for every item registered through an `ItemRegistry`.

### Item models, translations, and armor/smithing-template assets

Item registration through this module does not generate models, textures, or `lang` entries — those still need
the usual resource-pack assets. `SmithingTemplateDefinition`/`SmithingTemplates` auto-generate the *translation
keys* used by a smithing template's tooltip text (`item.<namespace>.smithing_template.<path>.applies_to`,
`...ingredients`, `...base_slot_description`, `...additions_slot_description`) but you still supply the actual
translations. Likewise, `CustomArmorMaterial` auto-derives the armor's `EquipmentAsset` id from the material's
`ResourceLocation` unless you override it with `.assetId(...)`, but the equipment asset JSON and armor layer
textures themselves still need to be authored under `assets/<namespace>/equipment/<path>.json` /
`textures/entity/equipment/...` as usual.

## For Mod Developers

### Registering items with `ItemRegistry`

Get your mod's registry with `ItemRegistry.forMod(ModCore)` (one shared instance per `ModCore`), then use one of
the `define...` methods to get a fluent, type-specific configuration builder, and finish with `.buildAndRegister()`:

```java
public class TestItemRegistry {
    private static final ItemRegistry R = ItemRegistry.forMod(TestModWoverItem.C);

    public static EnchantedAxe ENCHANTED_AXE = R
            .defineDefaultItem("enchanted_axe", EnchantedAxe::new)
            .addTags(ItemTags.AXES, ItemTags.MINING_ENCHANTABLE, ItemTags.MINING_LOOT_ENCHANTABLE)
            .buildAndRegister();
}
```

`ItemRegistry` exposes one `define...`/`register...` pair per item shape, each returning a specialized
`ItemDefinition` subclass:

| Method | Returns | For |
|---|---|---|
| `defineDefaultItem(name[, factory])` | `DefaultItemDefinition<I>` / `VanillaItemDefinition` | Plain items |
| `defineToolItem(name[, factory])` | `ToolItemDefinition<I>` | Pickaxes, axes, hoes, shovels, swords (`.pickaxe(...)`, `.axe(...)`, `.sword(...)`, or generic `.tool(...)`) |
| `defineArmorItem(name[, factory])` | `ArmorItemDefinition<I>` | Armor (`.humanoidArmor(...)`, `.wolfArmor(...)`, `.horseArmor(...)`, `.trimMaterial(...)`) |
| `defineFoodItem(name[, factory])` | `FoodItemDefinition<I>` | Consumables (`.nutrition(...)`, `.saturationModifier(...)`, `.setEffects(...)`) |
| `defineDrinkItem(name[, factory])` | `DrinkItemDefinition<I>` | Consumables using `Consumables.defaultDrink()` instead of the eating animation |
| `defineSpawnEgg(name[, factory])` | `SpawnEggDefinition<I>` | Spawn eggs, with automatic dispenser behavior |
| `defineSmithingTemplate(name, factory)` | `SmithingTemplateDefinition<I>` | Smithing table upgrade templates |
| `defineBoatItem(name, factory, withChest)` | `BoatItemDefinition<I>` | Boats, with automatic entity type registration |

Every `ItemDefinition` subclass shares the base fluent API from `ItemDefinition` for things that aren't
type-specific: `.stacksTo(...)`, `.durability(...)`, `.rarity(...)`, `.fireResistant()`, `.enchantable(...)`,
`.repairable(...)`, `.equippable(...)`, `.component(...)`, `.attributes(...)`/`.addAttribute(...)`, `.addTags(...)`,
and `.addTrait(...)` (see below). Call `.build()` to just construct the item, or `.buildAndRegister()` to also
register it with the built-in `Item` registry and (if `.addTags(...)` was used) queue its tags for datagen.

If you need a custom `Item` subclass, pass a factory — a method reference to a constructor taking the definition
object works well, as in the example above (`EnchantedAxe::new`, where `EnchantedAxe(DefaultItemDefinition<EnchantedAxe> config)`
calls `super(..., config.getProperties())`).

### Item traits (`org.betterx.wover.item.api.trait`)

Traits are reusable, composable pieces of item configuration that can be attached to any `ItemDefinition` via
`.addTrait(...)`. A trait can configure the definition at build time (add tags/properties in `configure(...)`) and
optionally leave a `RuntimeItemTrait` attached to the built item (if the item class implements `ItemWithTraits`) so
the trait can be queried again later, e.g. `ItemTrait.hasRuntimeTrait(stack.getItem(), MY_TRAIT_KEY)`.

This module only defines the trait *machinery* (`ItemTrait`, `RuntimeItemTrait`, `ItemTraitKey`,
`AbstractItemTraitBuilder`, `ItemWithTraits`, `GenericItemTrait`) — concrete trait kinds like boat rendering
(`BoatRendererTrait`, client-only), boat behavior, elytra behavior, or fireproofing are added by other WoVer
modules (`wover-sets-api`, `wover-recipe-api`). A typical builder looks like this (from `wover-sets-api`):

```java
public class BoatItemBuilder extends AbstractItemTraitBuilder<BoatItem, BoatItemTrait>
        implements BoatItemTrait.Builder {
    public static final BoatItemBuilder BUILDER = new BoatItemBuilder();

    private BoatItemBuilder() {
        super(ItemTraitKey.ofUnique(LibWoverSets.C, "is_boat"));
    }

    public List<ItemTrait<?, ?>> with(boolean withChest) {
        return combine(new Trait(withChest), ClientBlockTraits.BOAT_RENDERER.with(withChest));
    }

    public class Trait extends ItemTraitImpl<BoatItem, BoatItemTrait> implements BoatItemTrait {
        // ... configure(definition) adds ItemTags.BOATS / CHEST_BOATS
    }
}
```

and is consumed as `definition.addTrait(ItemTraits.BOAT_ITEM.with(withChest))`.

### Creative tabs (`org.betterx.wover.tabs.api`)

`CreativeTabs.start(ModCore)` returns a builder that walks through: create tabs, populate them with items, then
register them with the game.

```java
CreativeTabs.start(C)
            .createItemOnlyTab(Items.WOODEN_AXE).buildAndAdd()
            .processRegistries()
            .registerAllTabs();
```

- `createTab(name)` starts a freely configurable tab (`.setIcon(...)`, `.setTitle(...)`, `.setPredicate(...)`),
  finished with `.buildAndAdd()`. `createBlockOnlyTab(icon)`/`createItemOnlyTab(icon)` are shortcuts that also set
  a `CreativeTabPredicate` (`BLOCKS`/`ITEMS`) and a default name (`"blocks"`/`"items"`).
- `.processRegistries()` scans every item registered through `ItemRegistry.forMod(C)` and adds each one to the
  first tab whose predicate accepts it. `.process(Stream<Item>)` does the same for an arbitrary item stream.
- `.registerAllTabs()` registers every configured tab with Minecraft's creative-mode tab registry, wiring in
  `ItemStackHelper.callItemStackSetupIfPossible(...)` for each displayed stack so items implementing
  `ItemWithCustomStack` (see below) get correctly set up in the creative inventory too.

### Enchantments (`org.betterx.wover.enchantment.api`)

`EnchantmentManager.createKey(ResourceLocation)` creates an `EnchantmentKey` — a typed reference you can hold as a
static field before the enchantment is actually registered. Register the enchantment from the
`EnchantmentManager.BOOTSTRAP_ENCHANTMENTS` event (fires during registry bootstrap, both in normal play and
datagen) or from a `WoverEnchantmentProvider` subclass during datagen:

```java
public static final EnchantmentKey BREAKER_ENCHANT = EnchantmentManager.createKey(C.mk("breaker_enchant"));

EnchantmentManager.BOOTSTRAP_ENCHANTMENTS.subscribe(context -> {
    HolderGetter<Item> itemGetter = context.lookup(Registries.ITEM);
    BREAKER_ENCHANT.register(context, Enchantment.enchantment(
            Enchantment.definition(
                    itemGetter.getOrThrow(ItemTags.MINING_ENCHANTABLE),
                    10, 5,
                    Enchantment.dynamicCost(20, 20), Enchantment.dynamicCost(120, 20),
                    1, EquipmentSlotGroup.MAINHAND
            )
    ).withEffect(EnchantmentEffectComponents.ATTRIBUTES, /* ... */));
});
```

or, as a datagen-only provider (registered from your `WoverDataGenEntryPoint`'s `onInitializeProviders`):

```java
public class TestEnchantmentProvider extends WoverEnchantmentProvider {
    public TestEnchantmentProvider(ModCore modCore) {
        super(modCore, "enchantments");
    }

    @Override
    protected void bootstrap(BootstrapContext<Enchantment> context) {
        TestModWoverItem.TEST_ENCHANT.register(context, Enchantment.enchantment(/* ... */));
    }
}
```

At runtime, `EnchantmentUtils` provides null-/registry-safe helpers that don't require you to already have a
`HolderLookup.Provider` in hand: `EnchantmentUtils.getEnchantment(level, key)`,
`getItemEnchantmentLevel(level, key, stack)`, and `enchantInWorld(stack, key, level, provider)` — used, for
example, to enchant an item stack the first time it's created:

```java
public class EnchantedAxe extends AxeItem implements ItemWithCustomStack {
    @Override
    public void setupItemStack(ItemStack stack, HolderLookup.Provider provider) {
        EnchantmentUtils.enchantInWorld(stack, Enchantments.SHARPNESS, 5, provider);
    }
}
```

`EnchantmentManager.registerEffectComponent(id, builderConfigurator)` registers a custom
`DataComponentType` for use as an enchantment effect component, for enchantments with bespoke effect data.

### `ItemStackHelper` / `ItemWithCustomStack`

`ItemStackHelper.callItemStackSetupIfPossible(stack[, provider])` checks whether `stack.getItem()` implements
`ItemWithCustomStack` (from `wover-common-api`) and, if a registry provider is available, calls its
`setupItemStack(stack, provider)`. This is how the `EnchantedAxe` example above gets its enchantment applied —
WoVer calls this helper for you in creative tab population; call it yourself anywhere else a fresh `ItemStack` for
such an item is created (e.g. loot generation, commands).

### Custom armor materials (`org.betterx.wover.item.api.armor`)

`CustomArmorMaterial.start(location)` builds an `ArmorMaterial` without hand-assembling the `EnumMap<ArmorType, Integer>`
defense table:

```java
ArmorMaterial mythrilMaterial = CustomArmorMaterial
        .start(C.id("mythril"))
        .defense(3, 6, 8, 3, 11)  // boots, leggings, chestplate, helmet, body
        .durability(500)
        .enchantmentValue(15)
        .equipSound(SoundEvents.ARMOR_EQUIP_DIAMOND)
        .toughness(2.0f)
        .knockbackResistance(0.1f)
        .repairIngredient(ItemTags.IRON_TOOL_MATERIALS) // or .createRepairIngredient() for a generated `.../repair` tag
        .build(); // or .buildAndRegister() for a Holder<ArmorMaterial>
```

`.build()` validates that every required property was set (defense for all `ArmorType`s, durability, equip sound,
repair ingredient, etc.) and throws `IllegalStateException` naming the missing property otherwise. Feed the result
into `ItemRegistry.defineArmorItem(...).humanoidArmor(material, type)`.

### Smithing templates (`org.betterx.wover.item.api.smithing`)

`SmithingTemplates` ships predefined empty-slot icon sets (`TOOLS`, `ARMOR`, `ARMOR_AND_TOOLS`, plus individual
`EMPTY_SLOT_*` constants) so you don't have to know the vanilla texture paths. Use it directly, or through
`ItemRegistry.defineSmithingTemplate(...)`:

```java
SmithingTemplateItem template = registry
        .defineSmithingTemplate("mythril_upgrade", SmithingTemplateDefinition::createSmithingTemplate)
        .baseSlotEmptyIcons(SmithingTemplates.ARMOR_AND_TOOLS)
        .additionalSlotEmptyIcons(List.of(SmithingTemplates.EMPTY_SLOT_INGOT))
        .buildAndRegister();
```

`beforeBuild()` throws `IllegalStateException` if either icon list is empty, so both must be set.

### Boats

`ItemRegistry.defineBoatItem(name, factory, withChest)` (or the two-arg overload using the default `BoatItem`
factory) registers both the boat item and its backing `EntityType` in one step —
`BoatItemDefinition.buildAndRegisterBoat()` returns a `BoatType(entityType, item)` pair. Pair this with
`ItemTraits.BOAT_ITEM` (from `wover-sets-api`) or the client-only `BoatRendererTrait` if you need default texture
resolution instead of a fully custom entity renderer; see that trait's Javadoc for the expected texture path
(`assets/<namespace>/textures/entity/boat/<path>.png` / `.../chest_boat/<path>.png`).

### Datagen entry points used here

`WoverEnchantmentProvider` and `WoverLootTableProvider` (both in `org.betterx.wover.datagen.api.provider`) plug
into the same `WoverDataGenEntryPoint`/`PackBuilder` machinery documented on the `datagen-api` wiki page:

```java
public class MyModDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        globalPack.addProvider(MyEnchantmentProvider::new);
        globalPack.addProvider(MyLootTableProvider::new);
    }

    @Override
    protected ModCore modCore() {
        return MyMod.C;
    }
}
```
