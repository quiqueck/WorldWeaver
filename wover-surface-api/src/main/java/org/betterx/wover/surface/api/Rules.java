package org.betterx.wover.surface.api;

import org.betterx.wover.surface.api.noise.NumericProvider;
import org.betterx.wover.surface.impl.rules.SwitchRuleSource;

import net.minecraft.world.level.levelgen.SurfaceRules;

import java.util.List;

/**
 * Helper class for registering surface rules sources in the builtin
 * {@link net.minecraft.core.registries.BuiltInRegistries#MATERIAL_RULE} Registry.
 */
public class Rules {
    private Rules() {
    }

    /**
     * A rule that switches between a collection of rules based on a
     * {@link org.betterx.wover.surface.api.noise.NumericProvider}.
     * <p>
     * You can use this rule source in a .json file like this:
     * <pre class="json">{
     *  "type": "wover:switch_rule",
     *  "collection": [],
     *  "selector": {
     *    "type": "wover:rnd_int",
     *    "range": 2
     *  }
     * }</pre>
     * The {@code selector} is any valid {@link NumericProvider} In this case an instance
     * of {@link org.betterx.wover.surface.api.noise.NumericProviders#randomInt(int)}.
     *
     * @param provider   The provider that determines which rule to use. If the value returned by
     *                   the provider is larger than the number of elements in the collection,
     *                   it will be wrapped around using the modulo operator.
     * @param collection The collection of rules to switch between.
     * @return The rule source.
     */
    public static SurfaceRules.RuleSource switchRules(
            NumericProvider provider,
            List<SurfaceRules.RuleSource> collection
    ) {
        return new SwitchRuleSource(provider, collection);
    }

}
