package de.ambertation.wover.generator.impl.client;

import de.ambertation.wover.client.api.WoverClientTraitEntrypoint;
import de.ambertation.wover.generator.api.biomesource.end.WoverEndConfig;
import de.ambertation.wover.generator.api.biomesource.nether.WoverNetherConfig;
import de.ambertation.wover.generator.api.client.biomesource.client.ClientBiomeSourceConfigScreenRegistry;
import de.ambertation.wover.generator.impl.biomesource.end.WoverEndBiomeSource;
import de.ambertation.wover.generator.impl.biomesource.nether.WoverNetherBiomeSource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Registers the panel factories for wover's own {@link WoverEndBiomeSource}/{@link WoverNetherBiomeSource}
 * against {@link ClientBiomeSourceConfigScreenRegistry}. Invoked through the {@code wover.client.traits}
 * entrypoint, the same way third-party biome sources would register their own panels.
 */
@Environment(EnvType.CLIENT)
public class WoverBiomeSourceConfigScreenFactories implements WoverClientTraitEntrypoint {
    @Override
    public void registerClientTraits() {
        ClientBiomeSourceConfigScreenRegistry.<WoverEndBiomeSource, WoverEndConfig>register(
                WoverEndBiomeSource.class,
                (parent, config) -> new EndConfigPage(config)
        );
        ClientBiomeSourceConfigScreenRegistry.<WoverNetherBiomeSource, WoverNetherConfig>register(
                WoverNetherBiomeSource.class,
                (parent, config) -> new NetherConfigPage(config)
        );
    }
}
