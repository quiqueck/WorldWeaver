package de.ambertation.wover.surface.api.rules;

import de.ambertation.wover.surface.impl.rules.MaterialRuleRegistryImpl;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.SurfaceRules;

import org.jetbrains.annotations.NotNull;

/**
 * A helper class for registering material rules in {@link net.minecraft.core.registries.BuiltInRegistries#MATERIAL_RULE}
 */
public class MaterialRuleManager {
    /**
     * Registers a new rule source.
     *
     * @param location The location of the rule source.
     * @param rule     The rule source.
     * @return The key for the rule source.
     */
    public static ResourceKey<MapCodec<? extends SurfaceRules.RuleSource>> register(
            Identifier location,
            MapCodec<? extends SurfaceRules.RuleSource> rule
    ) {
        return MaterialRuleRegistryImpl.register(MaterialRuleRegistryImpl.createKey(location), rule);
    }

    /**
     * Registers a new rule source.
     *
     * @param key  The key for the rule source.
     * @param rule The rule source.
     * @return The key for the rule source.
     */
    public static ResourceKey<MapCodec<? extends SurfaceRules.RuleSource>> register(
            ResourceKey<MapCodec<? extends SurfaceRules.RuleSource>> key,
            MapCodec<? extends SurfaceRules.RuleSource> rule
    ) {
        return MaterialRuleRegistryImpl.register(key, rule);
    }

    /**
     * Creates a {@link ResourceKey} for a new rule source.
     *
     * @param location The location of the rule source.
     * @return The key for the rule source.
     */
    @NotNull
    public static ResourceKey<MapCodec<? extends SurfaceRules.RuleSource>> createKey(Identifier location) {
        return MaterialRuleRegistryImpl.createKey(location);
    }

    private MaterialRuleManager() {
    }
}
