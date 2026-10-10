package de.ambertation.wover.surface.impl;

import de.ambertation.wover.surface.api.AssignedSurfaceRule;
import de.ambertation.wover.surface.api.Conditions;
import de.ambertation.wover.surface.api.SurfaceRuleBuilder;
import de.ambertation.wover.surface.api.SurfaceRuleRegistry;
import de.ambertation.wover.surface.api.conditions.NoiseCondition;
import de.ambertation.wover.util.PriorityLinkedList;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.material.VanillaMaterialConditions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.material.MaterialRules;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import net.minecraft.world.level.levelgen.placement.CaveSurface;

import org.jetbrains.annotations.NotNull;

public class SurfaceRuleBuilderImpl<T extends BaseSurfaceRuleBuilder<T>> implements BaseSurfaceRuleBuilder<T> {
    /**
     * Minecraft 26.3 deleted the {@code SurfaceRules.ON_FLOOR} / {@code ON_CEILING} constants and moved
     * them into the new {@code minecraft:worldgen/material_condition} <b>datapack</b> registry, keyed by
     * {@link VanillaMaterialConditions#ON_FLOOR} / {@link VanillaMaterialConditions#ON_CEILING}.
     * <p>
     * We deliberately do <b>not</b> reference those keys through
     * {@link MaterialRules#getCondition(HolderGetter, ResourceKey)}, for two reasons:
     * <ul>
     * <li>Resolving a key needs a {@code HolderGetter<MaterialCondition>}, which the builder simply does
     * not have at {@link #surface(BlockState)} / {@link #floor(BlockState)} / {@link #ceil(BlockState)}
     * time - those methods build their rule eagerly and are routinely called outside any bootstrap.</li>
     * <li>A holder reference would make every rule we emit depend on a vanilla data file that a
     * datapack can override or remove, silently changing our rules. Emitting the condition inline
     * keeps a WorldWeaver surface rule self-contained.</li>
     * </ul>
     * The inline form is value-identical to vanilla's: {@code data/minecraft/worldgen/material_condition/on_floor.json}
     * is exactly {@code {"type":"minecraft:stone_depth","add_surface_depth":false,"offset":0,"secondary_depth_range":0,"surface_type":"floor"}},
     * which is what {@link MaterialRules#stoneDepthCheck(int, boolean, CaveSurface)} produces here, so
     * neither the generated JSON nor the generated terrain changes.
     */
    private static final MaterialCondition ON_FLOOR
            = MaterialRules.stoneDepthCheck(0, false, CaveSurface.FLOOR);

    /**
     * The ceiling counterpart of {@link #ON_FLOOR}, equivalent to
     * {@link VanillaMaterialConditions#ON_CEILING}. See {@link #ON_FLOOR} for why this is inlined.
     */
    private static final MaterialCondition ON_CEILING
            = MaterialRules.stoneDepthCheck(0, false, CaveSurface.CEILING);

    private final PriorityLinkedList<MaterialRule> rules;
    protected ResourceKey<Biome> biomeKey;
    protected int sortPriority;


    public SurfaceRuleBuilderImpl() {
        this.biomeKey = null;
        this.sortPriority = PriorityLinkedList.DEFAULT_PRIORITY;
        this.rules = new PriorityLinkedList<>();
    }

    /**
     * Restricts surface to only one biome.
     *
     * @param biomeKey {@link ResourceKey} for the {@link Biome}.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public T biome(ResourceKey<Biome> biomeKey) {
        this.biomeKey = biomeKey;
        return (T) this;
    }

    /**
     * Restricts surface to only one biome.
     *
     * @param biomeHolder {@link Holder} for the {@link Biome}.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public T biome(Holder<Biome> biomeHolder) {
        this.biomeKey = biomeHolder.unwrapKey().orElseThrow();
        return (T) this;
    }

    /**
     * The {@link ResourceKey} for the biome filter
     *
     * @return {@link ResourceKey} for the {@link Biome}.
     */
    public ResourceKey<Biome> biomeKey() {
        return biomeKey;
    }

    /**
     * Set biome surface with specified {@link BlockState}. Example - block of grass in the Overworld biomes
     * The rule is added with priority {@link #TOP_SURFACE_PRIORITY}.
     *
     * @param state {@link BlockState} for the ground cover.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public T surface(BlockState state) {
        MaterialRule rule = MaterialRules.state(state);
        rule = MaterialRules.ifTrue(ON_FLOOR, rule);
        rules.add(rule, TOP_SURFACE_PRIORITY);

        return (T) this;
    }

    /**
     * Set biome subsurface with specified {@link BlockState}. Example - dirt in the Overworld biomes.
     * The rule is added with priority {@link #SUB_SURFACE_PRIORITY}.
     *
     * @param state {@link BlockState} for the subterrain layer.
     * @param depth block layer depth.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public T subsurface(BlockState state, int depth) {
        MaterialRule rule = MaterialRules.state(state);
        rule = MaterialRules.ifTrue(MaterialRules.stoneDepthCheck(depth, false, CaveSurface.FLOOR), rule);
        rules.add(rule, SUB_SURFACE_PRIORITY);

        return (T) this;
    }

    /**
     * Set biome filler with specified {@link BlockState}. Example - stone in the Overworld biomes.
     * The rule is added with priority {@link #FILLER_PRIORITY}.
     *
     * @param state {@link BlockState} for filling.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public T filler(BlockState state) {
        rules.add(MaterialRules.state(state), FILLER_PRIORITY);
        return (T) this;
    }

    /**
     * Set biome floor with specified {@link BlockState}. Example - underside of a gravel floor.
     * The rule is added with priority {@link #FLOOR_PRIORITY}.
     *
     * @param state {@link BlockState} for the ground cover.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public T floor(BlockState state) {
        MaterialRule rule = MaterialRules.state(state);
        rule = MaterialRules.ifTrue(ON_FLOOR, rule);
        rules.add(rule, FLOOR_PRIORITY);
        return (T) this;
    }

    /**
     * Set biome floor material with specified {@link BlockState} and height.
     * The rule is added with priority {@link #BELOW_FLOOR_PRIORITY}.
     *
     * @param state  {@link BlockState} for the subterrain layer.
     * @param height block layer height.
     * @param noise  The noise object that is applied
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public T belowFloor(BlockState state, int height, NoiseCondition noise) {

        MaterialRule rule = MaterialRules.state(state);
        rule = MaterialRules.ifTrue(
                MaterialRules.stoneDepthCheck(
                        height,
                        false,
                        CaveSurface.FLOOR
                ),
                MaterialRules.ifTrue(noise, rule)
        );


        rules.add(rule, BELOW_FLOOR_PRIORITY);
        return (T) this;
    }

    /**
     * Set biome floor material with specified {@link BlockState} and height.
     * The rule is added with priority {@link #BELOW_FLOOR_PRIORITY}.
     *
     * @param state  {@link BlockState} for the subterrain layer.
     * @param height block layer height.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public T belowFloor(BlockState state, int height) {
        MaterialRule rule = MaterialRules.state(state);
        rule = MaterialRules.ifTrue(
                MaterialRules.stoneDepthCheck(
                        height,
                        false,
                        CaveSurface.FLOOR
                ), rule);

        rules.add(rule, BELOW_FLOOR_PRIORITY);
        return (T) this;
    }

    /**
     * Set biome ceiling with specified {@link BlockState}. Example - block of sandstone in the Overworld desert in air pockets.
     * The rule is added with priority {@link #CEILING_PRIORITY}.
     *
     * @param state {@link BlockState} for the ground cover.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public T ceil(BlockState state) {
        MaterialRule rule = MaterialRules.state(state);
        rule = MaterialRules.ifTrue(ON_CEILING, rule);
        rules.add(rule, CEILING_PRIORITY);
        return (T) this;
    }

    /**
     * Set biome ceiling material with specified {@link BlockState} and height. Example - sandstone in the Overworld deserts.
     * The rule is added with priority {@link #ABOVE_CEILING_PRIORITY}.
     *
     * @param state  {@link BlockState} for the subterrain layer.
     * @param height block layer height.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public T aboveCeil(BlockState state, int height) {
        MaterialRule rule = MaterialRules.state(state);
        rule = MaterialRules.ifTrue(MaterialRules.stoneDepthCheck(height, false, CaveSurface.CEILING), rule);

        rules.add(rule, ABOVE_CEILING_PRIORITY);
        return (T) this;
    }

    /**
     * Will cover steep areas (with large terrain angle). Example - Overworld mountains.
     * The rule is added with priority {@link #STEEP_SURFACE_PRIORITY}.
     *
     * @param state {@link BlockState} for the steep layer.
     * @param depth layer depth
     * @return
     */
    public T steep(BlockState state, int depth) {
        MaterialRule rule = MaterialRules.state(state);
        rule = MaterialRules.ifTrue(MaterialRules.stoneDepthCheck(depth, false, CaveSurface.FLOOR), rule);
        rule = MaterialRules.ifTrue(MaterialRules.steep(), rule);

        rules.add(rule, STEEP_SURFACE_PRIORITY);
        return (T) this;
    }

    /**
     * Allows to add custom rule.
     *
     * @param priority rule priority, lower values = higher priority (rule will be applied before others).
     * @param rule     custom {@link MaterialRule}.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public T rule(MaterialRule rule, int priority) {
        rules.add(rule, priority);
        return (T) this;
    }

    /**
     * Allows to add custom rule with default priority {@link PriorityLinkedList#DEFAULT_PRIORITY}.
     *
     * @param rule custom {@link MaterialRule}.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public T rule(MaterialRule rule) {
        rules.add(rule);
        return (T) this;
    }

    /**
     * Set biome floor with specified {@link BlockState} and the
     * {@link Conditions#DOUBLE_BLOCK_SURFACE_NOISE}.
     * The rule is added with priority {@link #FLOOR_PRIORITY}.
     *
     * @param surfaceBlockA {@link BlockState} for the ground cover.
     * @param surfaceBlockB {@link BlockState} for the alternative ground cover.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public T chancedFloor(BlockState surfaceBlockA, BlockState surfaceBlockB) {
        return chancedFloor(surfaceBlockA, surfaceBlockB, Conditions.DOUBLE_BLOCK_SURFACE_NOISE);
    }

    /**
     * Set biome floor with specified {@link BlockState} and the given Noise Function.
     * The rule is added with priority {@link #FLOOR_PRIORITY}.
     *
     * @param surfaceBlockA {@link BlockState} for the ground cover.
     * @param surfaceBlockB {@link BlockState} for the alternative ground cover.
     * @param noise         The {@link NoiseCondition}
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public T chancedFloor(BlockState surfaceBlockA, BlockState surfaceBlockB, NoiseCondition noise) {
        MaterialRule rule =
                MaterialRules.ifTrue(
                        ON_FLOOR,
                        MaterialRules.sequence(
                                MaterialRules.ifTrue(
                                        noise,
                                        MaterialRules.state(
                                                surfaceBlockA)
                                ),
                                MaterialRules.state(surfaceBlockB)
                        )
                );

        rules.add(rule, FLOOR_PRIORITY);
        return (T) this;
    }

    public T chancedFloor(BlockState surfaceBlockA, MaterialRule surfaceBlockB, NoiseCondition noise) {
        MaterialRule rule =
                MaterialRules.ifTrue(
                        ON_FLOOR,
                        MaterialRules.sequence(
                                MaterialRules.ifTrue(
                                        noise,
                                        MaterialRules.state(
                                                surfaceBlockA)
                                ),
                                surfaceBlockB
                        )
                );
        rules.add(rule, FLOOR_PRIORITY);
        return (T) this;
    }

    /**
     * Change the sorting order of this rule. Higher priority rules are applied first if there are multiple rules for a
     * specific biome. By default the Priority is set to {@link PriorityLinkedList#DEFAULT_PRIORITY}
     *
     * @param priority The priority to register the rule with.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public T sortPriority(int priority) {
        this.sortPriority = priority;
        return (T) this;
    }

    /**
     * Finalise rule building process.
     * <p>
     * Since Minecraft 26.3 {@link MaterialRules#isBiome(HolderGetter, ResourceKey...)} resolves its biome
     * keys into a {@link net.minecraft.core.HolderSet} instead of storing the raw keys, so building the
     * biome filter now needs a biome lookup. Inside a datapack registry bootstrap you get one from the
     * {@link BootstrapContext} ({@code ctx.lookup(Registries.BIOME)}); at runtime from the
     * {@link net.minecraft.core.RegistryAccess}.
     *
     * @param biomes The lookup used to resolve {@link #biomeKey()}. Only consulted when a biome filter
     *               was actually set.
     * @return {@link MaterialRule}.
     */
    public MaterialRule build(HolderGetter<Biome> biomes) {
        MaterialRule rule = getRuleSource();
        if (biomeKey != null) {
            rule = MaterialRules.ifTrue(MaterialRules.isBiome(biomes, biomeKey), rule);
        }
        return rule;
    }

    @NotNull
    protected MaterialRule getRuleSource() {
        if (rules.size() == 1) {
            return rules.get(0);
        }
        MaterialRule[] ruleArray = rules.toArray(new MaterialRule[rules.size()]);
        MaterialRule rule = MaterialRules.sequence(ruleArray);
        return rule;
    }


    public static class StandalonBuilder extends SurfaceRuleBuilderImpl<SurfaceRuleBuilder> implements SurfaceRuleBuilder {
        /**
         * Register rule in the {@link SurfaceRuleRegistry} with the currently set sort priority (see {@link #sortPriority}).
         *
         * @param ctx The {@link BootstrapContext} to register the rule with.
         * @param key The {@link ResourceKey} to register the rule with.
         * @return The {@link Holder} for the registry item.
         * @see SurfaceRuleRegistry#register(BootstrapContext, ResourceKey, ResourceKey, MaterialRule, int)
         */

        public Holder<AssignedSurfaceRule> register(
                @NotNull BootstrapContext<AssignedSurfaceRule> ctx,
                @NotNull ResourceKey<AssignedSurfaceRule> key
        ) {
            if (biomeKey == null) {
                throw new IllegalStateException("Biome key is not set for surface rule '" + key.identifier() + "'");
            }

            return SurfaceRuleRegistryImpl.register(ctx, key, biomeKey, getRuleSource(), sortPriority);
        }
    }
}
