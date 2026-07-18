# Automated Testing

WorldWeaver has two complementary layers of automated tests. Both run headless (no display) and are
safe for CI and for coding agents to run when checking for regressions.

| Layer | Tool | Location | Run with | Good for |
|-------|------|----------|----------|----------|
| Unit tests | JUnit 5 (+ [`fabric-loader-junit`]) | `<module>/src/test/java` | `./gradlew test` | Pure logic (math, hashing), datagen golden-file diffs, anything that does **not** need a running world |
| In-game tests | [Fabric GameTest] | `<module>/src/testmod/java/.../gametest` | `./gradlew gametestAll` | Anything needing a live server/world: event firing order, surface-rule injection, world-preset/datapack loading, trait resolution |

[`fabric-loader-junit`]: https://fabricmc.net/wiki/tutorial:junit
[Fabric GameTest]: https://docs.fabricmc.net/develop/automatic-testing

These are **separate** from the manual `testmod` / `testmodClient` runs you use while developing — those
still exist and are unchanged. The tests below are the automated regression guards.

## Running

```bash
# All JUnit unit tests, every module (fast, no game boot)
./gradlew test

# A single module's unit tests
./gradlew :wover-math-api:test

# All headless GameTests, every module that declares one (boots a dedicated server per module)
./gradlew gametestAll

# A single module's GameTest suite
./gradlew :wover-block-api:runWover-block-api-testmodGametest
```

GameTest runs write a JUnit-style report to `<module>/build/gametest/report.xml` and the process exits
non-zero if any `@GameTest` fails. JUnit runs write to `<module>/build/test-results/test/*.xml`.

> Use JDK 21 to run Gradle (the wrapper is Gradle 8.12, which does not support JDK 24+):
> `export JAVA_HOME=$(/usr/libexec/java_home -v 21)`

## What is covered (regression guards)

The three primary concerns, plus broader validation:

1. **Event system** (`wover-event-api`) — every `WorldLifecycle` event fires exactly once, in the
   documented order, and subscriber priority ordering is honoured.
2. **Traits system** (`wover-block-api`) — block-trait property/state resolution and `build()`
   call-order precedence stay unaltered, and datagen output does not drift.
3. **World preset & surface rules** (`wover-preset-api`, `wover-surface-api`) — custom datapack files
   are loaded and processed, and custom surface rules get injected into the world registry.

**Every** module has its own isolated test, so during a Minecraft version port you can validate one
library at a time (in the dependency order of `Notes.md`) without porting the whole tree first.

### Coverage matrix (all 18 libraries)

| Module | Test | Kind | Asserts |
|--------|------|------|---------|
| `wover-common-api` | `CustomRegistryDataKeyTest` | JUnit | `DataKey` equals/hashCode value semantics (the custom-data lookup contract) |
| `wover-core-api` | `PriorityLinkedListTest` | JUnit | Priority/tie ordering of the list the event-subscriber system uses |
| `wover-math-api` | `MathHelperTest`, `NoiseDeterminismTest` | JUnit | floor/seed/length contracts; OpenSimplex/Voronoi reproducibility |
| `wover-event-api` | `EventOrderGameTest` | GameTest | Lifecycle events fire once, in order; subscriber priority ordering |
| `wover-surface-api` | `SurfaceRuleGameTest` | GameTest | Datapack-JSON surface rules loaded + programmatic rule injected |
| `wover-ui-api` | `VersionCheckerModelTest` | JUnit | Version-check Gson wire model (de)serialization round-trip |
| `wover-datagen-api` | `PackBuilderRedirectTest` | JUnit | Auto-provider redirector bookkeeping in PackBuilderImpl |
| `wover-tag-api` | `TagInjectionGameTest` | GameTest | Runtime tag injection (no committed JSON) binds members; non-member excluded |
| `wover-preset-api` | `WorldPresetGameTest` | GameTest | Datapack-JSON preset loaded + programmatic preset injected |
| `wover-block-api` | `CallOrderGameTest`, `TraitPropertyGameTest` | GameTest | `build()` call-order precedence; FlammableBlockTrait + property setters |
| `wover-item-api` | `ItemGameTest` | GameTest | Item registration + custom-stack trait enchants the stack |
| `wover-recipe-api` | `RecipeGameTest` | GameTest | Datapack + programmatic recipes present in the RecipeManager |
| `wover-sets-api` | `SetGameTest` | GameTest | A WoodenBlockSet expands into and registers its member family |
| `wover-structure-api` | `StructureGameTest` | GameTest | Structures/sets (datapack) + custom structure-type (programmatic) |
| `wover-feature-api` | `FeatureGameTest` | GameTest | Configured/placed features (datapack) + feature-types (programmatic) |
| `wover-biome-api` | `BiomeGameTest` | GameTest | Biome + custom BiomeData registry + biome-modifications (datapack + injected) |
| `wover-generator-api` | `GeneratorGameTest` | GameTest | World-presets/noise (datapack) + chunk-generator/biome-source codecs |
| `wover-pottable-api` | `PottableGameTest` | GameTest | pottable_plant / pottable_soil datapack registries resolve at runtime |
| Datagen, ~11 modules | `tools/verify-datagen.sh` | Golden-file | Regenerated datagen matches committed output (no drift, no new files) |

Every GameTest that checks datapack-loaded entries also includes a negative case (a plausible-but-absent
id) so a registry that silently accepted everything would still fail the test.

## Continuous integration

`.github/workflows/tests.yml` runs on push and PR:

- **Unit + GameTest** job: `./gradlew test` then `./gradlew gametestAll`, uploading the XML reports.
- **Datagen golden-file** job: `xvfb-run -a bash tools/verify-datagen.sh --main`.

Both use JDK 21. A red build means a real regression in one of the guarded behaviours.

## Adding tests

- **Unit test**: drop a `*Test.java` into `<module>/src/test/java`. No further wiring — `./gradlew test`
  finds it. Add `Bootstrap.bootStrap()` (via `fabric-loader-junit`) only if you touch Minecraft
  registries.
- **GameTest**: add a class with `@GameTest`-annotated methods under
  `<module>/src/testmod/java/.../gametest`, then register it in that testmod's
  `src/testmod/resources/fabric.mod.json` under the `fabric-gametest` entrypoint. The
  `run<Module>-testmodGametest` task and its inclusion in `gametestAll` are created automatically the
  moment that entrypoint is present.
