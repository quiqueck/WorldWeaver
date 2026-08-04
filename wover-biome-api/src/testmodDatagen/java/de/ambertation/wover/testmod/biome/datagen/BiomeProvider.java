package de.ambertation.wover.testmod.biome.datagen;

import de.ambertation.wover.biome.api.builder.BiomeBootstrapContext;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.multi.WoverBiomeProvider;
import de.ambertation.wover.tag.api.predefined.CommonBiomeTags;
import de.ambertation.wover.testmod.entrypoint.TestModWoverBiome;

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
