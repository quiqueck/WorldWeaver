package org.betterx.wover.surface.api.rules;

import org.betterx.wover.entrypoint.WoverSurface;
import org.betterx.wover.surface.impl.rules.MaterialRuleRegistryImpl;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.SurfaceRules;

import org.jetbrains.annotations.NotNull;

public class RuleRegistry {
    public static ResourceKey<Codec<? extends SurfaceRules.RuleSource>> SWITCH_RULE
            = createKey(WoverSurface.C.id("switch_rule"));

    public static ResourceKey<Codec<? extends SurfaceRules.RuleSource>> register(
            ResourceLocation location,
            Codec<? extends SurfaceRules.RuleSource> rule
    ) {
        return MaterialRuleRegistryImpl.register(MaterialRuleRegistryImpl.createKey(location), rule);
    }

    public static ResourceKey<Codec<? extends SurfaceRules.RuleSource>> register(
            ResourceKey<Codec<? extends SurfaceRules.RuleSource>> key,
            Codec<? extends SurfaceRules.RuleSource> rule
    ) {
        return MaterialRuleRegistryImpl.register(key, rule);
    }

    @NotNull
    public static ResourceKey<Codec<? extends SurfaceRules.RuleSource>> createKey(ResourceLocation location) {
        return MaterialRuleRegistryImpl.createKey(location);
    }
}
