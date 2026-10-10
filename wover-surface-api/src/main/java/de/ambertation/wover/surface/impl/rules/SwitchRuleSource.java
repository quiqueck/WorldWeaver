package de.ambertation.wover.surface.impl.rules;

import de.ambertation.wover.surface.api.conditions.SurfaceRulesContext;
import de.ambertation.wover.surface.api.noise.NumericProvider;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;
import net.minecraft.world.level.levelgen.material.rule.RuleEvaluator;

import java.util.List;
import org.jetbrains.annotations.NotNull;

//
public record SwitchRuleSource(NumericProvider selector, List<MaterialRule> collection) implements MaterialRule {
    public static final MapCodec<SwitchRuleSource> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance
                    .group(
                            NumericProvider.CODEC.fieldOf("selector").forGetter(SwitchRuleSource::selector),
                            MaterialRule.CODEC.listOf().fieldOf("collection").forGetter(SwitchRuleSource::collection)
                    )
                    .apply(
                            instance,
                            SwitchRuleSource::new
                    ));

    @Override
    public @NotNull MapCodec<? extends MaterialRule> codec() {
        return CODEC;
    }

    @Override
    public RuleEvaluator compile(MaterialRuleContext context) {
        // 26.3 compiles a rule once per chunk instead of applying it per block, so every candidate is
        // compiled up front and the selector only picks which of the compiled evaluators runs.
        final RuleEvaluator[] compiled = new RuleEvaluator[collection.size()];
        for (int i = 0; i < compiled.length; i++) {
            compiled[i] = collection.get(i).compile(context);
        }
        final SurfaceRulesContext ctx = SurfaceRulesContext.class.cast(context);

        return (x, y, z) -> {
            int nr = Math.max(0, selector.getNumber(ctx)) % compiled.length;
            return compiled[nr].tryApply(x, y, z);
        };
    }
}
