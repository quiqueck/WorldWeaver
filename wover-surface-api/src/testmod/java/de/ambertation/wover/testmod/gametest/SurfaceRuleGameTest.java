package de.ambertation.wover.testmod.gametest;

import de.ambertation.wover.common.surface.api.SurfaceRuleProvider;
import de.ambertation.wover.surface.api.AssignedSurfaceRule;
import de.ambertation.wover.surface.api.SurfaceRuleRegistry;
import de.ambertation.wover.surface.api.conditions.SurfaceRulesContext;
import de.ambertation.wover.surface.impl.SurfaceRuleUtil;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Fabric GameTest for Concern 3 (surface rules): "custom datapack files get loaded and processed" and
 * "custom rules get injected into the world".
 * <p>
 * The testmod contributes surface rules two ways, and both must end up in the world's
 * {@link SurfaceRuleRegistry#SURFACE_RULES_REGISTRY} once the server is up:
 * <ul>
 *   <li>as committed datapack JSON files under {@code src/testmod/generated/data/.../surface_rules/}
 *       (test-plains, test-desert, …) - these prove datapack loading + processing of the custom
 *       registry;</li>
 *   <li>programmatically via {@code SurfaceRuleRegistry.BOOTSTRAP_SURFACE_RULE_REGISTRY} (test-savana)
 *       - this proves rule <em>injection</em> at bootstrap.</li>
 * </ul>
 * This test reads the live registry back from {@code registryAccess()} and asserts every expected rule
 * is present. A missing rule is a real regression in datapack loading or rule injection.
 */
public class SurfaceRuleGameTest {
    // Rules loaded from the committed datapack JSON files (always-on testmod data).
    private static final List<String> DATAPACK_RULES = List.of(
            "wover-surface-testmod:test-plains",
            "wover-surface-testmod:test-desert",
            "wover-surface-testmod:test-flower-forrest",
            "wover-surface-testmod:test-plains-below",
            "wover-surface-testmod:test-beach"
    );

    // Rule injected programmatically at bootstrap (TestModWoverSurface, guarded by !isDatagen()).
    private static final String INJECTED_RULE = "wover-surface-testmod:test-savana";

    @GameTest
    public void customSurfaceRulesAreLoadedAndInjected(GameTestHelper helper) {
        final Registry<AssignedSurfaceRule> registry = helper
                .getLevel()
                .registryAccess()
                .lookupOrThrow(SurfaceRuleRegistry.SURFACE_RULES_REGISTRY);

        final List<String> failures = new ArrayList<>();

        for (String id : DATAPACK_RULES) {
            if (!registry.containsKey(Identifier.parse(id))) {
                failures.add(id + ": expected surface rule loaded from datapack JSON but it is missing");
            }
        }

        if (!registry.containsKey(Identifier.parse(INJECTED_RULE))) {
            failures.add(INJECTED_RULE + ": expected surface rule injected via BOOTSTRAP_SURFACE_RULE_REGISTRY but it is missing");
        }

        if (!failures.isEmpty()) {
            final List<String> present = new ArrayList<>();
            registry.keySet().forEach(k -> present.add(k.toString()));
            helper.fail(Component.literal(
                    "Surface-rule registry regression:\n - " + String.join("\n - ", failures)
                            + "\n\nRules actually present: " + present
            ));
            return;
        }

        helper.succeed();
    }

    /**
     * The block {@code test-plains} paints the plains surface with. It can only appear in a
     * {@link NoiseGeneratorSettings}' rule set through WorldWeaver's injection.
     */
    private static final String INJECTED_MARKER = "minecraft:acacia_planks";
    /** The block {@code test-plains-below} puts under the floor - it must survive the merge too. */
    private static final String INJECTED_MARKER_BELOW = "minecraft:oak_planks";

    /**
     * Second half of Concern 3: the collected rules must actually reach a dimension's generator settings.
     * <p>
     * The registry test above only proves the rules were <em>loaded</em>. Getting them into a world
     * additionally needs {@code NoiseGeneratorSettingsMixin} to apply and to write the merged rule source
     * back into {@link NoiseGeneratorSettings}. That mixin is exactly what this setup cannot check at
     * compile time - there is no refmap/annotation processor here, so a mixin whose {@code @Shadow} no
     * longer matches its target compiles happily and then silently does nothing. 26.3 renamed that very
     * field ({@code surfaceRule} -> {@code materialRule}) and changed its type to a {@link Holder}.
     * <p>
     * The gametest world is flat in every dimension ({@code FlatLevelSource}), so nothing in it goes
     * through the noise-based injection path. The test therefore drives that path itself: it takes the
     * registry's {@code minecraft:overworld} noise settings, copies the record so the live registry is
     * left alone, and runs {@link SurfaceRuleUtil#injectNoiseBasedSurfaceRules} against a biome source
     * that only contains {@link Biomes#PLAINS}.
     * <p>
     * Four separate things can fail this test, all of them ported for 26.3:
     * <ul>
     *   <li>{@code NoiseGeneratorSettingsMixin} not applying at all - the cast to
     *       {@link SurfaceRuleProvider} fails;</li>
     *   <li>its {@code @Shadow} of the renamed {@code materialRule} field not reading or not writing;</li>
     *   <li>{@code SurfaceRuleUtil}'s biome filter, which in 26.3 has to resolve the biome key through a
     *       {@code HolderGetter} instead of storing it raw;</li>
     *   <li>the rule/condition codecs - re-serializing exercises the <em>encode</em> direction of our
     *       custom {@code wover:*} sources, which merely loading the datapack never does.</li>
     * </ul>
     */
    @GameTest
    public void injectedSurfaceRulesReachTheGeneratorSettings(GameTestHelper helper) {
        final RegistryAccess access = helper.getLevel().getServer().registryAccess();
        final RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, access);

        final NoiseGeneratorSettings original = access
                .lookupOrThrow(Registries.NOISE_SETTINGS)
                .getOrThrow(NoiseGeneratorSettings.OVERWORLD)
                .value();

        // A copy of the record, so the injection below cannot leak into the running server.
        final NoiseGeneratorSettings settings = new NoiseGeneratorSettings(
                original.noiseSettings(),
                original.defaultBlock(),
                original.defaultFluid(),
                original.noiseRouter(),
                original.materialRule(),
                original.spawnTarget(),
                original.seaLevel(),
                original.disableMobGeneration(),
                original.aquifers(),
                original.useLegacyRandomSource(),
                original.debugFunctions()
        );

        if (!(((Object) settings) instanceof SurfaceRuleProvider provider)) {
            helper.fail(Component.literal(
                    "NoiseGeneratorSettings does not implement SurfaceRuleProvider - "
                            + "NoiseGeneratorSettingsMixin did not apply."
            ));
            return;
        }

        final MaterialRule before = provider.wover_getOriginalSurfaceRules();
        if (before == null) {
            helper.fail(Component.literal(
                    "wover_getOriginalSurfaceRules() returned null - the @Shadow of "
                            + "NoiseGeneratorSettings#materialRule is not reading the field."
            ));
            return;
        }
        if (encode(ops, before).contains(INJECTED_MARKER)) {
            helper.fail(Component.literal(
                    "The vanilla overworld surface rules already contain " + INJECTED_MARKER
                            + " - this test can no longer tell injection apart from vanilla content."
            ));
            return;
        }

        SurfaceRuleUtil.injectNoiseBasedSurfaceRules(
                LevelStem.OVERWORLD,
                Holder.direct(settings),
                new FixedBiomeSource(access.lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS))
        );

        final String after = encode(ops, provider.wover_getOriginalSurfaceRules());

        final List<String> failures = new ArrayList<>();
        if (!after.contains(INJECTED_MARKER)) {
            failures.add(INJECTED_MARKER + " (test-plains surface) is missing from the merged rules");
        }
        if (!after.contains(INJECTED_MARKER_BELOW)) {
            failures.add(INJECTED_MARKER_BELOW + " (test-plains-below) is missing from the merged rules");
        }
        if (!after.contains("minecraft:biome")) {
            failures.add("the injected rules are not wrapped in a biome condition");
        }

        if (!failures.isEmpty()) {
            helper.fail(Component.literal(
                    "Surface rules did not reach the generator settings:\n - "
                            + String.join("\n - ", failures)
                            + "\n\nMerged rules were: " + after
            ));
            return;
        }

        helper.succeed();
    }

    /**
     * Guards {@code SurfaceRulesContextAccessor}.
     * <p>
     * Nothing else in this module calls {@link SurfaceRulesContext} on a path a headless test reaches -
     * custom noise conditions only see a context while a noise-based generator is painting a surface,
     * and the gametest world is flat. Yet every accessor here is a 26.3 risk: the 26.3 release replaced
     * {@code SurfaceRules.Context} with {@link MaterialRuleContext}, and {@code getBiome()} in particular is
     * not a field accessor at all (the {@code biome} field is a lazily filled {@code Holder<Biome>}), so it
     * is served by vanilla's public method instead. If the mixin silently failed, every one of these would
     * throw {@link AbstractMethodError} the first time a mod's condition ran during world generation - far
     * away from anything that would point at the cause.
     * <p>
     * Reflecting over the interface catches that immediately: {@link Class#getMethod} only finds
     * <em>public</em> methods, so it proves both that the interface was injected and that each member is
     * actually callable.
     */
    @GameTest
    public void surfaceRuleContextImplementsTheWholeAccessorInterface(GameTestHelper helper) {
        final Class<?> context = MaterialRuleContext.class;
        final List<String> failures = new ArrayList<>();

        if (!SurfaceRulesContext.class.isAssignableFrom(context)) {
            helper.fail(Component.literal(
                    "MaterialRuleContext does not implement SurfaceRulesContext - "
                            + "SurfaceRulesContextAccessor did not apply."
            ));
            return;
        }

        for (Method declared : SurfaceRulesContext.class.getDeclaredMethods()) {
            try {
                final Method impl = context.getMethod(declared.getName(), declared.getParameterTypes());
                if (Modifier.isAbstract(impl.getModifiers())) {
                    failures.add(declared.getName() + "() is still abstract on MaterialRuleContext");
                } else if (!impl.getReturnType().equals(declared.getReturnType())) {
                    failures.add(declared.getName() + "() returns " + impl.getReturnType().getName()
                            + ", the API declares " + declared.getReturnType().getName());
                }
            } catch (NoSuchMethodException e) {
                failures.add(declared.getName() + "() has no public implementation on MaterialRuleContext");
            }
        }

        if (!failures.isEmpty()) {
            helper.fail(Component.literal(
                    "SurfaceRulesContext is not fully backed by MaterialRuleContext:\n - "
                            + String.join("\n - ", failures)
            ));
            return;
        }

        helper.succeed();
    }

    private static String encode(RegistryOps<JsonElement> ops, MaterialRule rules) {
        return MaterialRule.DIRECT_CODEC
                .encodeStart(ops, rules)
                .getOrThrow(msg -> new AssertionError("Could not re-serialize surface rules: " + msg))
                .toString();
    }
}
