package de.ambertation.wover.surface.impl.rules;

import de.ambertation.wover.entrypoint.LibWoverSurface;
import de.ambertation.wover.surface.api.rules.MaterialRuleManager;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * 26.3 split the material-rule registries in two: {@code minecraft:material_rule_type}
 * ({@link BuiltInRegistries#MATERIAL_RULE_TYPE}) still holds the {@link MapCodec}s that dispatch a
 * serialized rule onto its implementation, while the name {@code minecraft:worldgen/material_rule}
 * ({@link net.minecraft.core.registries.Registries#MATERIAL_RULE}) was reused for a brand new
 * <i>datapack</i> registry holding the rule sources themselves.
 * <p>
 * This class registers codecs, so it belongs on the {@code _TYPE} side.
 */
public class MaterialRuleRegistryImpl {
    public static ResourceKey<MapCodec<? extends MaterialRule>> SWITCH_RULE
            = MaterialRuleManager.createKey(LibWoverSurface.C.id("switch_rule"));

    public static ResourceKey<MapCodec<? extends MaterialRule>> register(
            ResourceKey<MapCodec<? extends MaterialRule>> key,
            MapCodec<? extends MaterialRule> rule
    ) {
        Registry.register(BuiltInRegistries.MATERIAL_RULE_TYPE, key, rule);
        return key;
    }

    @NotNull
    public static ResourceKey<MapCodec<? extends MaterialRule>> createKey(Identifier location) {
        return ResourceKey.create(
                BuiltInRegistries.MATERIAL_RULE_TYPE.key(),
                location
        );
    }

    @ApiStatus.Internal
    public static void bootstrap() {
        register(SWITCH_RULE, SwitchRuleSource.CODEC);
    }
}
