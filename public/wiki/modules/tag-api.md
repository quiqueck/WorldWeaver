# wover-tag-api

Java API for creating, looking up and populating Minecraft tags, plus datagen helpers that serialize them to the
standard `data/<namespace>/tags/<registry>/<path>.json` files. It wraps vanilla/Fabric tag handling behind a small
set of `TagRegistry`s (one per taggable registry) so mod code never has to hand-build tag JSON or worry about
merging elements contributed by several mods into the same tag.

- **Gradle artifact:** `org.betterx:wover-tag-api`
- **Depends on:** `wover-core-api`, `wover-datagen-api`, `wover-event-api`
- **Java packages:**
  - `org.betterx.wover.tag.api`
  - `org.betterx.wover.tag.api.builder`
  - `org.betterx.wover.tag.api.event`
  - `org.betterx.wover.tag.api.event.context`
  - `org.betterx.wover.tag.api.predefined`
  - `org.betterx.wover.datagen.api` (adds `WoverTagProvider` to the datagen module's package)

## For Datapack Developers

Tags produced by this module are ordinary vanilla tag files — `wover-tag-api` does not invent a new file format.
Each tag lives at:

```
data/<namespace>/tags/<registry>/<path>.json
```

with the standard vanilla content:

```json
{
  "replace": false,
  "values": [
    "minecraft:dirt",
    "minecraft:grass_block",
    "#minecraft:logs",
    {
      "id": "othermod:maybe_present_block",
      "required": false
    }
  ]
}
```

- `replace` (bool) — if `true`, this file's `values` replace every tag of the same id loaded from packs with lower
  priority instead of merging with them.
- `values` — a list of either plain resource locations (elements), `#namespace:path` references to other tags, or
  `{"id": ..., "required": false}` objects for *optional* entries that are silently skipped if the referenced
  element/tag doesn't exist. WoVer's builder API (`add`/`addOptional`, see below) maps directly onto required vs.
  optional entries.

`registry` is the vanilla tag directory for the registry in question — e.g. `block`, `item`, `enchantment`,
`entity_type`, or `worldgen/biome` for biomes. WoVer resolves this automatically from
`Registries.tagsDirPath(...)` for the registries it wires up out of the box.

### Tags WoVer defines or reuses

`org.betterx.wover.tag.api.predefined` ships a set of ready-made `TagKey` constants so mods sharing WorldWeaver
don't reinvent the same conventions. They fall into three namespaces:

- **`wover:...`** — tags defined by WorldWeaver itself (no other convention/mod owns them), e.g.
  `wover:is_end/center`, `wover:is_end/land`, `wover:composters`, `wover:cauldrons`, `wover:beds`,
  `wover:surfaces/end/stones`, `wover:vegetation/leaves`, `wover:ores/end`, `wover:mineable/hammer`,
  `wover:needs_netherite_tool`, `wover:tools/hammers`, `wover:furnaces`, `wover:poi/workstation/<profession>`
  (villager workstation POI tags for every vanilla profession), `wover:poi/home`, `wover:poi/meeting`,
  `wover:poi/beehive`, `wover:poi/bee_nest`, `wover:poi/nether_portal`, `wover:poi/lodestone`,
  `wover:poi/lightning_rod`, and more — see `CommonBiomeTags`, `CommonBlockTags`, `CommonItemTags`, `CommonPoiTags`
  and `MineableTags` for the full list.
- **`c:...`** — the cross-mod "common" convention namespace (see the
  [Fabric wiki tags tutorial](https://fabricmc.net/wiki/tutorial:tags)), e.g. `c:barrels`, `c:chests`, `c:ores`,
  `c:budding_blocks`, `c:music_discs`, `c:ingots/iron`.
- **`fabric:...`** — Fabric's own tool-type convention tags, e.g. `fabric:axes`, `fabric:pickaxes`,
  `fabric:swords` (`ToolTags`).
- **`minecraft:...`** — a few vanilla tags are re-exported as constants for convenience (e.g.
  `MineableTags.PICKAXE` is just `BlockTags.MINEABLE_WITH_PICKAXE`, `CommonBlockTags.DRAGON_IMMUNE` is
  `BlockTags.DRAGON_IMMUNE`).

These are plain `TagKey` constants, so as a datapack author you populate them the normal way — add a
`data/<namespace>/tags/<registry>/<path>.json` for the tag id shown above (e.g. `data/wover/tags/block/beds.json`
for `wover:beds`), or add your elements as `values` entries to the existing file if you want to extend it.

### Villager POI and workstation tags

`CommonPoiTags` is the notable case where these tags actually drive vanilla behavior, not just convention: WoVer's
own datagen wires `wover:poi/workstation/<profession>` tags to the matching vanilla point-of-interest type for
every villager profession (armorer, butcher, cartographer, cleric, farmer, fisherman, fletcher, leatherworker,
librarian, mason, shepherd, toolsmith, weaponsmith), plus `wover:poi/home`, `wover:poi/meeting`, `wover:poi/beehive`,
`wover:poi/bee_nest`, `wover:poi/nether_portal` and `wover:poi/lodestone`. Adding a block to the relevant tag makes
villagers recognize it as that workstation/POI.

## For Mod Developers

### Getting a `TagKey` from a `TagRegistry`

[`TagManager`](../../wover-tag-api/src/main/java/org/betterx/wover/tag/api/TagManager.java) is the entry point. It
exposes a ready-made [`TagRegistry`](../../wover-tag-api/src/main/java/org/betterx/wover/tag/api/TagRegistry.java)
for each of the common taggable registries:

| Field | Registry |
|---|---|
| `TagManager.BLOCKS` | `Block` |
| `TagManager.ITEMS` | `Item` |
| `TagManager.BIOMES` | `Biome` (a [`BiomeTagRegistry`](../../wover-tag-api/src/main/java/org/betterx/wover/tag/api/BiomeTagRegistry.java), which additionally exposes `makeStructureTag(...)`) |
| `TagManager.ENCHANTMENTS` | `Enchantment` |
| `TagManager.ENTITY_TYPES` | `EntityType<?>` |

A `TagRegistry<T, P>` creates/looks up `TagKey<T>`s — it does not, by itself, add elements to a tag:

```java
TagKey<Block> myTag = TagManager.BLOCKS.makeTag(MyMod.C, "my_tag");   // <namespace>:my_tag
TagKey<Block> common = TagManager.BLOCKS.makeCommonTag("ores");        // c:ores
TagKey<Block> fabric = TagManager.BLOCKS.makeFabricTag("axes");        // fabric:axes
TagKey<Block> wover  = TagManager.BLOCKS.makeWorldWeaverTag("beds");   // wover:beds
```

Need a `TagRegistry` for a registry that isn't listed above? Create your own with
`TagManager.registerType(ResourceKey<? extends Registry<T>>)` (or one of its overloads that let you pick a custom
tag directory/`LocationProvider`).

### Adding elements: datagen (recommended)

Elements should be added during datagen whenever possible. Subclass
[`WoverTagProvider`](../../wover-tag-api/src/main/java/org/betterx/wover/datagen/api/WoverTagProvider.java) — its
nested convenience classes `ForBlocks`, `ForItems`, `ForBiomes`, `ForEnchantments` and `ForEntityTypes` already
bind to the matching built-in `TagRegistry`, so you only implement `prepareTags(...)`:

```java
public class BlockTagProvider extends WoverTagProvider.ForBlocks {
    public BlockTagProvider(ModCore modCore) {
        super(modCore);
    }

    public void prepareTags(TagBootstrapContext<Block> ctx) {
        ctx.add(CommonBlockTags.CHEST, Blocks.CHEST);
        ctx.addOptional(CommonPoiTags.FARMER_WORKSTATION, CommonBlockTags.COMPOSTER);
    }
}
```

Register the provider from your datagen entrypoint's `PackBuilder` (see the `wover-datagen-api` wiki page for the
full entrypoint setup):

```java
public class MyModDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        globalPack.addProvider(BlockTagProvider::new);
    }

    @Override
    protected ModCore modCore() {
        return MyMod.C;
    }
}
```

The `TagBootstrapContext<T>` passed to `prepareTags` (via its `TagBuilder<T>` superinterface) is what actually adds
elements to a tag:

| Method | Effect |
|---|---|
| `add(TagKey<T> tag, T... elements)` / `add(T element, TagKey<T>... tags)` | Add elements as **required** entries. |
| `add(TagKey<T> tag, TagKey<T>... tags)` | Nest other tags into `tag` as required. |
| `add(TagKey<T> tag, ResourceKey<T>... keys)` | Add elements by `ResourceKey` as required. |
| `addOptional(...)` | Same overloads as `add`, but produces `{"id": ..., "required": false}` entries. |

`WoverTagProvider`'s constructors optionally take a list of allowed mod ids (only elements/tags from those
namespaces get written — defaults to your own mod id/namespace) and a `Set<TagKey<T>>` of tags that should be
force-written even if empty (`asPlaceholder`). Only tags that are actually initialized (because something was
added to them, or because they're in the forced set, or `initAll()` was overridden to return `true`) get written
to disk at all.

### `BlockTagDataProvider` / `ItemTagDataProvider` — self-registering blocks and items

If a `Block` registered in the built-in `Block` registry, or an `Item` registered in the built-in `Item` registry,
implements
[`BlockTagDataProvider`](../../wover-tag-api/src/main/java/org/betterx/wover/tag/api/BlockTagDataProvider.java) or
[`ItemTagDataProvider`](../../wover-tag-api/src/main/java/org/betterx/wover/tag/api/ItemTagDataProvider.java), the
auto-provider machinery from `wover-datagen-api`/`wover-tag-api` calls its `addBlockTags(TagBuilder<Block>)` /
`addItemTags(ItemTagBuilder)` automatically during datagen — you don't need to write a `WoverTagProvider` yourself
just to tag your own blocks/items. `ItemTagBuilder` additionally offers `ItemLike`-based overloads of `add`/
`addOptional` so you can pass a `Block` where an `Item` tag element is expected.

### Adding elements at runtime (discouraged)

If you have no choice (e.g. you only find out at runtime which elements exist), subscribe to
`TagRegistry#bootstrapEvent()`. The subscriber receives the same kind of `TagBootstrapContext` datagen uses, but
the event only fires when tags are (re-)loaded from datapacks at runtime — it never fires during datagen:

```java
TagManager.BLOCKS.bootstrapEvent().subscribe(ctx -> {
    ctx.add(myTag, Blocks.DIRT);
});
```

### Checking tags at runtime

[`TagManager.isToolWithMineableTag(ItemStack stack, TagKey<Block> tag)`](../../wover-tag-api/src/main/java/org/betterx/wover/tag/api/TagManager.java)
checks whether an `ItemStack`'s `TOOL` data component has a rule that is correct-for-drops against the given
block-mineable tag — useful together with the `MineableTags` constants (`MineableTags.HAMMER`,
`MineableTags.NEEDS_NETHERITE_TOOL`, ...).
