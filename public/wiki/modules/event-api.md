# wover-event-api

Generic event-bus module for WorldWeaver. It provides a small, reusable `Event<T>`/`Subscriber` system plus
`WorldLifecycle`, a fixed set of events fired while a Minecraft world is created/loaded, and `WorldState`, a
read-only snapshot of the currently active world (registries, storage access). Most other WorldWeaver modules
depend on `wover-event-api` to hook into world startup.

- **Gradle artifact:** `de.ambertation:worldweaver` (single artifact; this module ships inside it as the Fabric mod `wover-events`)
- **Depends on:** `wover-core-api`
- **Java packages:**
  - `de.ambertation.wover.events.api`
  - `de.ambertation.wover.events.api.types`
  - `de.ambertation.wover.state.api`

## For Datapack Developers

`wover-event-api` is pure Java — its `src/main/resources` folder contains no `data/` files at all (only
`assets/wover-events/icon.png` and the mod's own `fabric.mod.json`/mixin configs). There is no JSON schema to write
for this module; skip it unless you are writing Java code.

The one exception is [`WorldDatapackConfig`](../../../wover-event-api/src/main/java/de/ambertation/wover/state/api/WorldDatapackConfig.java),
which lets a *mod* register a `ConfigResource` that then gets merged from datapack JSON files contributed by other
packs — but the JSON schema/location for such a resource is defined by whichever mod registers it, not by
`wover-event-api` itself.

## For Mod Developers

### The `Event<T>` system

[`Event<T>`](../../../wover-event-api/src/main/java/de/ambertation/wover/events/api/Event.java) is a collection of
prioritized [`Subscriber`](../../../wover-event-api/src/main/java/de/ambertation/wover/events/api/Subscriber.java)s that
get called when the event is emitted:

| Member | Purpose |
|---|---|
| `subscribe(T subscriber)` / `subscribe(T subscriber, int priority)` | Add a subscriber. Higher priority runs first; ties are broken by insertion order. Default priority is `Event.DEFAULT_PRIORITY` (1000). |
| `subscribeReadOnly(T subscriber)` / `subscribeReadOnly(T subscriber, int priority)` | Add a subscriber that is guaranteed to run after every subscriber added with `subscribe(...)` (it is offset by `Event.MAX_READONLY_PRIORITY`). Use this for listeners that only read data and must not race with listeners that modify it. |

Most `Event<T>` fields you will use are already created for you — either by `WorldLifecycle` (see below) or by
another WorldWeaver module (e.g. `WorldPresetManager.BOOTSTRAP_WORLD_PRESETS`, `TagManager`'s bootstrap events).
`wover-event-api` itself only ships the `Event`/`Subscriber` interfaces; the concrete implementation used
throughout the codebase to back new events is `de.ambertation.wover.events.impl.EventImpl`, which other modules
instantiate internally and expose through a public `Event<T>`-typed field, for example:

```java
public class MyRegistryImpl {
    public static final EventImpl<OnBootstrapRegistry<MyThing>> BOOTSTRAP_MY_REGISTRY =
            new EventImpl<>("BOOTSTRAP_MY_REGISTRY");
}

public class MyRegistry {
    public static final Event<OnBootstrapRegistry<MyThing>> BOOTSTRAP_MY_REGISTRY =
            MyRegistryImpl.BOOTSTRAP_MY_REGISTRY;
}
```

A `Subscriber` is usually a `@FunctionalInterface` with one method whose signature carries whatever data the event
wants to pass along (see the interfaces in `de.ambertation.wover.events.api.types` below). There is also
[`ChainableSubscriber<R>`](../../../wover-event-api/src/main/java/de/ambertation/wover/events/api/ChainableSubscriber.java),
used by events where each subscriber transforms a value and passes it on to the next subscriber (currently only
`WorldLifecycle.ON_DIMENSION_LOAD`); the return value of the last subscriber in the chain becomes the event's
result.

### Subscribing to an event

Subscribing is a simple method reference or lambda matching the `Subscriber` interface's method signature. This is
how `wover-block-api`'s `PoiManagerImpl` reacts to `WorldLifecycle.BEFORE_CREATING_LEVELS`:

```java
import de.ambertation.wover.events.api.WorldLifecycle;
import static de.ambertation.wover.events.impl.AbstractEvent.SYSTEM_PRIORITY;

WorldLifecycle.BEFORE_CREATING_LEVELS.subscribe(
        PoiManagerImpl::finalizedWorldLoad,
        SYSTEM_PRIORITY - 1
);

private static void finalizedWorldLoad(
        LevelStorageSource.LevelStorageAccess levelStorageAccess,
        PackRepository packRepository,
        LayeredRegistryAccess<RegistryLayer> registries,
        WorldData worldData
) {
    PoiManagerImpl.updateStates();
}
```

`AbstractEvent.SYSTEM_PRIORITY` (100000000) is what WorldWeaver's own internal subscribers use so they consistently
run before mod code; you normally don't need it unless you have a similar ordering requirement.

> This page covers the common (server-side) events in `de.ambertation.wover.events.api`, which is what almost every
> mod hooks into. `wover-event-api` also ships a client-only counterpart,
> `de.ambertation.wover.events.api.client.ClientWorldLifecycle` (loading-screen, welcome-screen and
> experimental-warning-screen events), under `src/client` — see its javadoc if you need to hook into the client's
> world-creation UI flow.

### `WorldLifecycle`: world creation/loading events

[`WorldLifecycle`](../../../wover-event-api/src/main/java/de/ambertation/wover/events/api/WorldLifecycle.java) exposes the
events fired while a world is created or loaded, all on the logical server (for single-player, that's the
integrated server inside the client process). Based on the actual firing order recorded for this repo (see
`Notes.md`) and the mixins that emit each event, a dedicated-server session fires them in this order:

| Event | Subscriber interface | Fired | Notes |
|---|---|---|---|
| `WORLD_FOLDER_READY` | `OnFolderReady` | once access to the world's save folder is available | can be captured at several points internally, but only re-emitted if the `LevelStorageAccess` actually changed |
| `WORLD_REGISTRY_READY` | `OnRegistryReady` | every time a new `RegistryAccess` is captured | fired multiple times with different `OnRegistryReady.Stage` values (`PREPARATION`, `LOADING`, `FINAL`) — only `FINAL` is the registry actually used by the world |
| `CREATED_NEW_WORLD_FOLDER` | `CreatedNewWorldFolder` | once, only when a **new** world is being created (not when loading an existing one) | client-only flow; not fired on a dedicated server |
| `BEFORE_LOADING_RESOURCES` | `BeforeLoadingResources` | once, right before the reloadable server resources start loading | |
| `RESOURCES_LOADED` | `OnResourceLoad` | once during startup, and again on every later resource reload (e.g. `/reload`) | |
| `ON_DIMENSION_LOAD` | `OnDimensionLoad` (chainable) | once, when the `WorldStem` is created | subscribers can amend the dimensions that will be loaded; each subscriber receives the previous subscriber's result |
| `MINECRAFT_SERVER_READY` | `OnMinecraftServerReady` | once, right after the `MinecraftServer` instance exists | |
| `BEFORE_CREATING_LEVELS` | `BeforeCreatingLevels` | once, right before the game creates its `ServerLevel`s | all biomes/features/structures are guaranteed to be loaded at this point |
| `SERVER_LEVEL_READY` | `OnServerLevelReady` | once per `ServerLevel` (once for each dimension) | |

`WORLD_FOLDER_READY` and `WORLD_REGISTRY_READY` can each fire more than once and their relative order can differ
between client and server (e.g. when creating a new world from the client, `WORLD_REGISTRY_READY` fires with
`PREPARATION` before `WORLD_FOLDER_READY` even exists) — don't rely on a strict order between those two. Everything
from `BEFORE_LOADING_RESOURCES` through `BEFORE_CREATING_LEVELS` fires exactly once per world load, followed by one
`SERVER_LEVEL_READY` per loaded dimension.

Example — running code once the registries are final and ready to read:

```java
import de.ambertation.wover.events.api.WorldLifecycle;
import de.ambertation.wover.events.api.types.OnMinecraftServerReady;
import de.ambertation.wover.state.api.WorldState;

public class MyModInit {
    public static void init() {
        WorldLifecycle.MINECRAFT_SERVER_READY.subscribe(MyModInit::whenServerReady);
    }

    private static void whenServerReady(
            LevelStorageSource.LevelStorageAccess storageSource,
            PackRepository packRepository,
            WorldStem worldStem
    ) {
        var registryAccess = WorldState.registryAccess();
        // ... read registries, run one-time setup, etc.
    }
}
```

### `WorldState`: reading the current world

[`WorldState`](../../../wover-event-api/src/main/java/de/ambertation/wover/state/api/WorldState.java) is fed by the
`WorldLifecycle` events above and gives you read-only access to the currently active world without having to thread
a reference through your own code:

| Member | Purpose |
|---|---|
| `WorldState.registryAccess()` | The current `RegistryAccess`, set from `WORLD_REGISTRY_READY`. Only registries flagged `FINAL` are stored here. |
| `WorldState.allStageRegistryAccess()` | Like `registryAccess()`, but also includes registries from the `PREPARATION` stage — use this if you need registry access very early (e.g. during tag loading). |
| `WorldState.storageAccess()` | The current `LevelStorageSource.LevelStorageAccess`, set from `WORLD_FOLDER_READY`. |
| `WorldState.getBiomeID(Biome)` | Looks up a biome's `Identifier` via `allStageRegistryAccess()`; returns `null` (and logs an error) if it can't be resolved. |

### `WorldConfig`: per-world mod data

[`WorldConfig`](../../../wover-event-api/src/main/java/de/ambertation/wover/state/api/WorldConfig.java) lets a mod persist
its own NBT data inside a world's save folder (under `<world>/data/<modid>.nbt`):

```java
WorldConfig.registerMod(MyMod.C);

// later, once the world is loaded:
CompoundTag settings = WorldConfig.getCompoundTag(MyMod.C, "my.settings");
settings.putBoolean("enabled", true);
WorldConfig.saveFile(MyMod.C);
```

`WorldConfig.event(ModCore)` returns an `Event<OnWorldConfig>` that fires once the config for that mod has been
created or loaded from disk (with an `OnWorldConfig.State` of `CREATED`, `LOADED`, or `LOAD_FAILED`) — subscribe to
it if you need to react as soon as your mod's saved data becomes available.
