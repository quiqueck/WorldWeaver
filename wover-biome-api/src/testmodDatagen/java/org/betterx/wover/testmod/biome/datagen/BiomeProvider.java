package org.betterx.wover.testmod.biome.datagen;

import org.betterx.wover.biome.api.builder.BiomeBootstrapContext;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.multi.WoverBiomeProvider;
import org.betterx.wover.tag.api.predefined.CommonBiomeTags;
import org.betterx.wover.testmod.entrypoint.TestModWoverBiome;

public class BiomeProvider extends WoverBiomeProvider {
    /**
     * Creates a new instance of {@link WoverBiomeProvider}.
     *
     * @param modCore The {@link ModCore} of the Mod.
     */
    public BiomeProvider(ModCore modCore) {
        super(modCore);
    }

    @Override
    protected void bootstrap(BiomeBootstrapContext context) {
        TestModWoverBiome.TEST_BIOME
                .bootstrap(context)
                .tag(CommonBiomeTags.IS_END_HIGHLAND)
                .register();
    }
}
