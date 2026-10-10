package de.ambertation.wover.entrypoint;

import de.ambertation.wover.core.api.registry.DatapackRegistryEntrypoint;
import de.ambertation.wover.preset.impl.WorldPresetInfoRegistryImpl;

/**
 * Registers the {@code wover/world_preset_info} datapack registry.
 * <p>
 * This must not wait for the {@code main} entrypoint: the list of world registries is captured once, in
 * the static initializer of {@code RegistryDataLoader}, and that class can be loaded by another mod's
 * {@code main} entrypoint before ours runs (TerraBlender does so through Fabric's
 * {@code DynamicRegistries.register}). This entrypoint is pulled in from that very static initializer,
 * so the registry is always part of the list.
 */
public class PresetDatapackRegistryEntrypoint implements DatapackRegistryEntrypoint {
    @Override
    public void registerDatapackRegistries() {
        WorldPresetInfoRegistryImpl.initialize();
    }
}
