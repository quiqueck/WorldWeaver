# wover-math-api

A small, foundational library of math helpers used by WorldWeaver's world generation code: deterministic
seed hashing, a floor operation that behaves correctly for negative numbers, gradient noise (OpenSimplex),
cellular/Voronoi noise, a bounded-range random helper, and a three-axis (`Vec3i`) value provider. Other
WorldWeaver modules (`wover-generator-api`, `wover-surface-api`, `wover-feature-api`, ...) build their noise-driven
biome maps, surface rules and placement modifiers on top of these classes.

- **Gradle artifact:** `de.ambertation:worldweaver` (single artifact; this module ships inside it as the Fabric mod `wover-math`)
- **Depends on:** `wover-core-api`
- **Java packages:**
  - `de.ambertation.wover.math.api`
  - `de.ambertation.wover.math.api.noise`
  - `de.ambertation.wover.math.api.random`
  - `de.ambertation.wover.math.api.valueproviders`

## For Datapack Developers

`wover-math-api` has no datapack-facing surface of its own: its `src/main/resources` only contains the mod's
`fabric.mod.json`, an (empty) mixin config, and an access widener — there is no `data/` folder and this module
does not read or write any JSON at runtime. It is pure Java, consumed by other WorldWeaver modules and by mod
code directly. If you are a datapack author, you can skip this page.

One exception worth knowing about: [`Vec3iProvider`](../../../wover-math-api/src/main/java/de/ambertation/wover/math/api/valueproviders/Vec3iProvider.java)
exposes a `Codec`, which other modules embed into *their own* datapack-driven types (for example
`wover-feature-api`'s `offset` placement modifier). That JSON format is documented on the wiki page of the module
that owns it, not here.

## For Mod Developers

### Key types

| Class | Purpose |
|---|---|
| [`MathHelper`](../../../wover-math-api/src/main/java/de/ambertation/wover/math/api/MathHelper.java) | Static helpers: negative-safe `floor(double)`, `lengthSqr(x, y, z)`, and deterministic seed-hashing (`getSeed(...)`) from an `int`, or from a base seed plus 2D/3D coordinates. |
| [`OpenSimplexNoise`](../../../wover-math-api/src/main/java/de/ambertation/wover/math/api/noise/OpenSimplexNoise.java) | Deterministic 2D/3D/4D gradient (OpenSimplex) noise, seeded via a `long` seed or a custom permutation table. `eval(...)` returns a value normalized to roughly `[-1, 1]`. |
| [`VoronoiNoise`](../../../wover-math-api/src/main/java/de/ambertation/wover/math/api/noise/VoronoiNoise.java) | Cellular (Worley/Voronoi) noise over a seeded 3D grid of feature points: `sample(...)` returns the distance to the nearest point, `getRandom(...)` returns a `Random` seeded per-cell, `getPos(...)` locates the nearest (and an approximate second-nearest) feature point. |
| [`RandomHelper`](../../../wover-math-api/src/main/java/de/ambertation/wover/math/api/random/RandomHelper.java) | `inRange(RandomSource, min, max)` — draws a `float` uniformly from `[min, max)`. |
| [`Vec3iProvider`](../../../wover-math-api/src/main/java/de/ambertation/wover/math/api/valueproviders/Vec3iProvider.java) | A three-axis analogue of vanilla's `IntProvider`: samples x/y/z independently and combines them into a `Vec3i`. Ships a `Codec` (and a range-validating `codec(min, max)`) for embedding into other datapack-driven types. |

### Deterministic per-position seeds

`MathHelper.getSeed(...)` is the building block WorldWeaver uses to turn a world seed plus a position into a
reproducible, well-distributed seed, for example (from `wover-generator-api`'s `HexBiomeMap`):

```java
WorldgenRandom random = new WorldgenRandom(RandomSource.create(MathHelper.getSeed(seed, cx, cz)));
```

### Gradient noise

`OpenSimplexNoise` is typically created once per seed and then queried repeatedly:

```java
OpenSimplexNoise noise = new OpenSimplexNoise(random.nextInt());
double value = noise.eval(x, z); // roughly in [-1, 1]
```

### Bounded random values

`RandomHelper.inRange` is a shorthand for `min + random.nextFloat() * (max - min)`, used e.g. to jitter a
threshold (from `wover-surface-api`):

```java
float threshold = RandomHelper.inRange(random, 0.4F, 0.5F);
```

### A randomized 3D offset

`Vec3iProvider` lets a placement modifier or similar datapack-configurable type sample an independent, bounded
offset per axis (from `wover-feature-api`'s `OffsetProvider`):

```java
public static final MapCodec<OffsetProvider> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
        .group(
                Vec3iProvider.codec(-16, 16)
                             .fieldOf("offset")
                             .forGetter(cfg -> cfg.offset)
        )
        .apply(instance, OffsetProvider::new));

// later, when placing a feature:
BlockPos moved = blockPos.offset(offset.sample(randomSource));
```
