# Upgrading Minecraft Version

Use the following order to upgrade the single packages (based on inter-dependencies):

* **wover-common** (no dependency)
* **wover-core** (depends on _wover-common_)
* **wover-math** (depends on _wover-core_)
* **wover-datagen** (depends on _wover-core_)
* **wover-event** (depends on _wover-core_)
* **wover-ui** (depends on _wover-core_ and _wover-event_)
* **wover-tag** (depends on _wover-core_, _wover-datagen_ and _wover-event_)
* **wover-item** (depends on _wover-core_, _wover-tag_ and _wover-event_)
* **wover-block** (depends on _wover-core_, _wover-tag_ and _wover-item_)
* **wover-recipe** (depends on _wover-core_, _wover-event-api_, _wover-block_ and _wover-item_)
* **wover-sets** (depends on _wover-core_, _wover-block_, _wover-item_ and _wover-recipe_)
* **wover-preset** (depends on _wover-core_, _wover-tag_ and _wover-event_)
* **wover-surface** (depends on _wover-common-api_, _wover-datagen-api_, _wover-core-api_, _wover-math-api_ and
  _wover-event-api_)
* **wover-structure** (depends on _wover-core-api_, _wover-math-api_, _wover-event-api_ and _wover-block-api_)
* **wover-feature** (depends on _wover-core-api_, _wover-event-api_, _wover-surface-api_, _wover-block-api_ and
  _wover-structure-api_)
* **wover-biome** (depends on _wover-core-api_, _wover-event-api_ and _wover-feature-api_)
* **wover-generator** (depends on _wover-core-api_, _wover-event-api_, _wover-surface-api_, _wover-biome-api_,
  _wover-preset-api_, _wover-ui-api_ and _wover-tag-api_)

### Event order for the wover-event test mods

grep "Emitting event:"

#### Server

```
(wover-events) (DEBUG) Emitting event: WORLD_FOLDER_READY (484b8cee, world)
(wover-events) (DEBUG) Emitting event: WORLD_REGISTRY_READY (84553a4, PREPARATION)
(wover-events) (DEBUG) Emitting event: BEFORE_LOADING_RESOURCES
(wover-events) (DEBUG) Emitting event: WORLD_REGISTRY_READY (11941b73, FINAL)
(wover-events) (DEBUG) Emitting event: RESOURCES_LOADED
(wover-events) (DEBUG) Emitting event: ON_DIMENSION_LOAD
(wover-events) (DEBUG) Emitting event: MINECRAFT_SERVER_READY
(wover-events) (DEBUG) Emitting event: BEFORE_CREATING_LEVELS
(wover-events) (DEBUG) Emitting event: SERVER_LEVEL_READY
(wover-events) (DEBUG) Emitting event: SERVER_LEVEL_READY
(wover-events) (DEBUG) Emitting event: SERVER_LEVEL_READY
```

#### Client

##### new World

```
(wover-events) (DEBUG) Emitting event: WORLD_REGISTRY_READY (2d4be5e0, PREPARATION)
(wover-events) (DEBUG) Emitting event: BEFORE_LOADING_RESOURCES
(wover-events) (DEBUG) Emitting event: WORLD_FOLDER_READY (21840920, New World (3))
(wover-events) (DEBUG) Emitting event: CREATED_NEW_WORLD_FOLDER
(wover-events) (DEBUG) Emitting event: WORLD_REGISTRY_READY (2039f7e8, LOADING)
(wover-events) (DEBUG) Emitting event: WORLD_REGISTRY_READY (65d0ad4f, FINAL)
(wover-events) (DEBUG) Emitting event: RESOURCES_LOADED
(wover-events) (DEBUG) Emitting event: ON_DIMENSION_LOAD
(wover-events) (DEBUG) Emitting event: MINECRAFT_SERVER_READY
(wover-events) (DEBUG) Emitting event: BEFORE_CREATING_LEVELS
(wover-events) (DEBUG) Emitting event: SERVER_LEVEL_READY
(wover-events) (DEBUG) Emitting event: SERVER_LEVEL_READY
(wover-events) (DEBUG) Emitting event: SERVER_LEVEL_READY
```

##### existing World

```
(wover-events) (DEBUG) Emitting event: WORLD_FOLDER_READY (26dea145, New World (3))
(wover-events) (DEBUG) Emitting event: BEFORE_CLIENT_LOAD_SCREEN
(wover-events) (DEBUG) Emitting event: WORLD_REGISTRY_READY (3899c28c, PREPARATION)
(wover-events) (DEBUG) Emitting event: BEFORE_LOADING_RESOURCES
(wover-events) (DEBUG) Emitting event: WORLD_REGISTRY_READY (2fbb4ee8, FINAL)
(wover-events) (DEBUG) Emitting event: RESOURCES_LOADED
(wover-events) (DEBUG) Emitting event: ON_DIMENSION_LOAD
(wover-events) (DEBUG) Emitting event: ALLOW_EXPERIMENTAL_WARNING_SCREEN
(wover-events) (DEBUG) Emitting event: MINECRAFT_SERVER_READY
(wover-events) (DEBUG) Emitting event: BEFORE_CREATING_LEVELS
(wover-events) (DEBUG) Emitting event: SERVER_LEVEL_READY
(wover-events) (DEBUG) Emitting event: SERVER_LEVEL_READY
(wover-events) (DEBUG) Emitting event: SERVER_LEVEL_READY
```