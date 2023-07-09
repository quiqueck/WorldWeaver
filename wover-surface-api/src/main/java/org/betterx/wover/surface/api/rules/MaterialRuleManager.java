package org.betterx.wover.surface.api.rules;

import org.betterx.wover.surface.impl.rules.MaterialRuleRegistryImpl;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
    public static ResourceKey<Codec<? extends SurfaceRules.RuleSource>> register(
            ResourceLocation location,
            Codec<? extends SurfaceRules.RuleSource> rule
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
    public static ResourceKey<Codec<? extends SurfaceRules.RuleSource>> register(
            ResourceKey<Codec<? extends SurfaceRules.RuleSource>> key,
            Codec<? extends SurfaceRules.RuleSource> rule
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
    public static ResourceKey<Codec<? extends SurfaceRules.RuleSource>> createKey(ResourceLocation location) {
        return MaterialRuleRegistryImpl.createKey(location);
    }

    private MaterialRuleManager() {
    }
}
