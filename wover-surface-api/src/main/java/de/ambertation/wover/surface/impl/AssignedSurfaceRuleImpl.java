package de.ambertation.wover.surface.impl;

import de.ambertation.wover.surface.api.AssignedSurfaceRule;
import de.ambertation.wover.util.PriorityLinkedList;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;

public class AssignedSurfaceRuleImpl extends AssignedSurfaceRule {
    public static final Codec<AssignedSurfaceRule> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    MaterialRule.CODEC.fieldOf("ruleSource").forGetter(o -> o.ruleSource),
                    Identifier.CODEC.fieldOf("biome").forGetter(o -> o.biomeID),
                    Codec.INT.fieldOf("priority").orElse(PriorityLinkedList.DEFAULT_PRIORITY).forGetter(o -> o.priority)
            )
            .apply(instance, AssignedSurfaceRuleImpl::new)
    );

    AssignedSurfaceRuleImpl(
            MaterialRule ruleSource,
            Identifier biomeID,
            int priority
    ) {
        super(ruleSource, biomeID, priority);
    }
}
