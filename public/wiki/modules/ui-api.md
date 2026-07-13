# wover-ui-api

A small, mostly internal module that backs WorldWeaver's own client UI: the first-launch welcome screen, the
mod-update checker/notifier, and the client-only config values those screens read and write. Its Java-facing public
API surface is intentionally tiny.

- **Gradle artifact:** `org.betterx:wover-ui-api`
- **Depends on:** `wover-core-api`, `wover-event-api`
- **Java packages:**
  - `org.betterx.wover.ui.api`
  - `org.betterx.wover.config.api.client`

Note: this module also contains an `org.betterx.wover.ui.impl` / `org.betterx.wover.config.impl` package (the actual
welcome/update screens, layout base class, and a cached-version-check config) and an
`org.betterx.wover.ui.mixin(.client)` package (currently registering zero mixins). Those are internal implementation
details of WorldWeaver's own UI, not a public API, and are not covered here.

## For Datapack Developers

`wover-ui-api` does not define any datapack-loaded JSON file format. The only resources it ships are its own
translation files (`assets/wover-ui/lang/en_us.json`, `de_de.json`) and a handful of textures/icons used by its
welcome/update screens. There is nothing under a `data/` folder, and no registry or config schema for datapacks to
provide. You can safely skip this module if you are only writing a datapack.

## For Mod Developers

Almost everything in this module is WorldWeaver's own internal UI plumbing (the welcome screen shown on first
launch, and the update-notification screen). The two pieces that are actually exposed as public API are the
mod-update checker and the client config it reads its settings from.

### Checking for mod updates: `VersionChecker`

[`VersionChecker`](../../wover-ui-api/src/main/java/org/betterx/wover/ui/api/VersionChecker.java) periodically asks
a WorldWeaver web service whether newer versions of the currently installed WorldWeaver-based mods are available. It
is what powers the "Mod Updates" screen you see on startup, but the checking logic itself is reusable:

```java
// Register your mod so VersionChecker considers it when the update service responds:
VersionChecker.registerMod(MyMod.C); // ModCore instance

// Somewhere on the client (e.g. from a ClientModInitializer):
VersionChecker.startCheck(ModCore.isClient());

// Later (after the background check completed), react to any updates that were found:
if (!VersionChecker.isEmpty()) {
    VersionChecker.forEachUpdate((modID, currentVersion, newVersion) -> {
        MyMod.C.log.info(modID + " has an update: " + currentVersion + " -> " + newVersion);
    });
}
```

Key points:

- `startCheck(boolean isClient)` only ever runs on the client, only runs once per game session (guarded by an
  internal `Thread` flag), and only actually contacts the web service if the cached result is older than
  `VersionChecker.WAIT_FOR_DAYS` (5) days — otherwise it just replays the last cached response.
- Only mods registered via `registerMod(ModCore)` are reported back through `forEachUpdate`; any other mod contained
  in the service's response is ignored.
- The check additionally respects the `check_for_new_versions` and `did_present_welcome_screen` client config values
  described below — it does nothing until the welcome screen has been shown at least once, and does nothing if the
  player disabled update checks.

`wover-ui-api` itself is the only module currently calling this API (its own `LibWoverUiClient` entrypoint wires it
up to WorldWeaver's world-lifecycle startup-screen hook); no other WorldWeaver module currently registers itself
with it.

### Client-only settings: `ClientConfigs`

[`ClientConfigs`](../../wover-ui-api/src/client/java/org/betterx/wover/config/api/client/ClientConfigs.java) exposes
the singleton [`ClientConfig`](../../wover-ui-api/src/client/java/org/betterx/wover/config/api/client/ClientConfig.java)
instance (registered the same way `wover-core-api`'s `MainConfig` is, see [core-api](core-api.md)):

```java
if (ClientConfigs.CLIENT.checkForNewVersions.get()) {
    // ...
}

ClientConfigs.CLIENT.forceBetterXPreset.set(false);
ClientConfigs.saveConfigs(); // persists every registered config, not just CLIENT
```

| Value | Purpose |
|---|---|
| `checkForNewVersions` | Whether `VersionChecker` should periodically check for mod updates. |
| `prefereModrinth` | Whether Modrinth (instead of CurseForge) should be preferred when both download links are available on the update screen. |
| `disableExperimentalWarning` | Whether the experimental-settings warning screen should be skipped when loading an existing world. |
| `forceBetterXPreset` | Whether the BetterX world preset should be forced as the default for newly created worlds. |
| `didPresentWelcomeScreen` | Internal, hidden-from-UI flag tracking whether the welcome screen was already shown. |

Note that this is a **different** `config.api` package than `wover-core-api`'s `org.betterx.wover.config.api`
(which holds `Configs`, `MainConfig`, `DatapackConfigs`) — this module's config package is `config.api.client` and
is client-only (`@Environment(EnvType.CLIENT)`), holding only the settings for WorldWeaver's own UI screens.

### What is *not* public API here

The actual welcome screen (`WelcomeScreen`), update screen (`UpdatesScreen`), their shared base class
(`WoverLayoutScreen`), the client-side driver (`VersionCheckerClient`), and the cached last-check-result config
(`CachedConfig`) all live under `org.betterx.wover.ui.impl(.client)` / `org.betterx.wover.config.impl` packages.
They are built entirely for WorldWeaver's own bundled UI (using `wunderlib`'s layout components) and are not
intended to be reused or extended by other mods — there is currently no supported way to add your own screen to
WorldWeaver's startup-screen flow from outside this module.
