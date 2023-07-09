package org.betterx.wover.surface.impl.rules;

import org.betterx.wover.entrypoint.WoverSurface;
import org.betterx.wover.legacy.api.LegacyHelper;
import org.betterx.wover.surface.api.rules.MaterialRuleManager;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.SurfaceRules;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class MaterialRuleRegistryImpl {
    public static ResourceKey<Codec<? extends SurfaceRules.RuleSource>> SWITCH_RULE
            = MaterialRuleManager.createKey(WoverSurface.C.id("switch_rule"));

    public static ResourceKey<Codec<? extends SurfaceRules.RuleSource>> register(
            ResourceKey<Codec<? extends SurfaceRules.RuleSource>> key,
            Codec<? extends SurfaceRules.RuleSource> rule
    ) {
        Registry.register(BuiltInRegistries.MATERIAL_RULE, key, rule);
        return key;
    }

    @NotNull
    public static ResourceKey<Codec<? extends SurfaceRules.RuleSource>> createKey(ResourceLocation location) {
        return ResourceKey.create(
                BuiltInRegistries.MATERIAL_RULE.key(),
                location
        );
    }

    @ApiStatus.Internal
    public static void bootstrap() {
        register(SWITCH_RULE, SwitchRuleSource.CODEC);

        Registry.register(
                BuiltInRegistries.MATERIAL_RULE,
                "bclib_switch_rule",
                LegacyHelper.wrap(SwitchRuleSource.CODEC)
        );
    }
}
