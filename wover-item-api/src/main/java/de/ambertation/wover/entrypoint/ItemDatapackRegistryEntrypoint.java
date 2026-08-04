package de.ambertation.wover.entrypoint;

import de.ambertation.wover.core.api.registry.DatapackRegistryEntrypoint;
import de.ambertation.wover.enchantment.impl.EnchantmentManagerImpl;

public class ItemDatapackRegistryEntrypoint implements DatapackRegistryEntrypoint {
    @Override
    public void registerDatapackRegistries() {
        EnchantmentManagerImpl.initialize();
    }
}
