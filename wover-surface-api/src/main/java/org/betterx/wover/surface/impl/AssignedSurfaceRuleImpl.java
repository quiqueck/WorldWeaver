package org.betterx.wover.surface.impl;

import org.betterx.wover.surface.api.AssignedSurfaceRule;
import org.betterx.wover.util.PriorityLinkedList;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class AssignedSurfaceRuleImpl extends AssignedSurfaceRule {
    public static final Codec<AssignedSurfaceRule> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    SurfaceRules.RuleSource.CODEC.fieldOf("ruleSource").forGetter(o -> o.ruleSource),
                    ResourceLocation.CODEC.fieldOf("biome").forGetter(o -> o.biomeID),
                    Codec.INT.fieldOf("priority").orElse(PriorityLinkedList.DEFAULT_PRIORITY).forGetter(o -> o.priority)
            )
            .apply(instance, AssignedSurfaceRuleImpl::new)
    );

    AssignedSurfaceRuleImpl(
            SurfaceRules.RuleSource ruleSource,
            ResourceLocation biomeID,
            int priority
    ) {
        super(ruleSource, biomeID, priority);
    }
}
