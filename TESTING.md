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

Other libraries (math, tag, recipe, …) are covered by unit tests and the cross-library datagen guard.

### Coverage matrix

| Concern / module | Test | Kind | Asserts |
|------------------|------|------|---------|
| Events (`wover-event-api`) | `EventOrderGameTest` | GameTest | Lifecycle events fire once, in order; subscriber priority ordering |
| Traits (`wover-block-api`) | `CallOrderGameTest` | GameTest | `build()` call-order precedence between chain/trait/ctor/copy |
| Traits (`wover-block-api`) | `TraitPropertyGameTest` | GameTest | FlammableBlockTrait registration + plain property setters survive |
| Surface rules (`wover-surface-api`) | `SurfaceRuleGameTest` | GameTest | Datapack-JSON rules loaded + programmatic rule injected |
| World presets (`wover-preset-api`) | `WorldPresetGameTest` | GameTest | Datapack-JSON preset loaded + programmatic preset injected |
| Math (`wover-math-api`) | `MathHelperTest`, `NoiseDeterminismTest` | JUnit | floor/seed/length contracts; noise reproducibility |
| Datagen, ~11 modules | `tools/verify-datagen.sh` | Golden-file | Regenerated datagen matches committed output (no drift, no new files) |

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
