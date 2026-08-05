package de.ambertation.wover.preset.api;

import de.ambertation.wover.core.api.registry.DatapackRegistryBuilder;
import de.ambertation.wover.entrypoint.LibWoverWorldPreset;
import de.ambertation.wover.events.api.Event;
import de.ambertation.wover.events.api.types.OnBootstrapRegistry;
import de.ambertation.wover.preset.impl.WorldPresetInfoImpl;
import de.ambertation.wover.preset.impl.WorldPresetInfoRegistryImpl;
import de.ambertation.wover.state.api.WorldState;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;

/**
 * Datapack registry and lookup methods for {@link WorldPresetInfo}.
 * <p>
 * {@link WorldPresetInfo} entries live in their own datapack registry
 * ({@link #WORLD_PRESET_INFO_REGISTRY}), keyed under the same location as the {@link WorldPreset} they
 * describe (see {@link #createKey(Identifier)}). Use {@link #getFor(WorldPreset)} (or one of its
 * overloads) to look up the info for a given preset; presets without a registered entry fall back to a
 * default {@link WorldPresetInfo}.
 */
public class WorldPresetInfoRegistry {
    /**
     * Fired when the {@link #WORLD_PRESET_INFO_REGISTRY} is being bootstrapped. Subscribe to this event
     * to register {@link WorldPresetInfo} entries at runtime; whenever possible, prefer generating them
     * with a data generator instead.
     */
    public static final Event<OnBootstrapRegistry<WorldPresetInfo>> BOOTSTRAP_WORLD_PRESET_INFO_REGISTRY
            = WorldPresetInfoRegistryImpl.BOOTSTRAP_WORLD_PRESET_INFO_REGISTRY;

    private WorldPresetInfoRegistry() {
    }

    /**
     * The datapack registry key for {@link WorldPresetInfo}s. Entries are loaded from
     * {@code data/<namespace>/wover/world_preset_info/<path>.json}.
     */
    public static final ResourceKey<Registry<WorldPresetInfo>> WORLD_PRESET_INFO_REGISTRY =
            DatapackRegistryBuilder.createRegistryKey(LibWoverWorldPreset.C.id("wover/world_preset_info"));

    /**
     * Creates a {@link ResourceKey} for a {@link WorldPresetInfo}.
     *
     * @param ruleID the location of the info entry
     * @return the key
     */
    public static ResourceKey<WorldPresetInfo> createKey(
            Identifier ruleID
    ) {
        return WorldPresetInfoRegistryImpl.createKey(ruleID);
    }

    /**
     * Creates a {@link ResourceKey} for the {@link WorldPresetInfo} describing the given
     * {@link WorldPreset}. The info entry is expected to be registered under the same location as
     * {@code ruleID}.
     *
     * @param ruleID the key of the {@link WorldPreset}
     * @return the key
     */
    public static ResourceKey<WorldPresetInfo> createKey(
            ResourceKey<WorldPreset> ruleID
    ) {
        return createKey(ruleID.identifier());
    }

    /**
     * Gets the {@link WorldPresetInfo} registered for the given {@link WorldPreset}.
     *
     * @param key the key of the preset, may be {@code null}
     * @return the registered {@link WorldPresetInfo}, or a default instance if {@code key} is
     *         {@code null} or has no registered info
     */
    public static @NotNull WorldPresetInfo getFor(
            ResourceKey<WorldPreset> key
    ) {
        if (key == null) return WorldPresetInfoImpl.DEFAULT;
        final Registry<WorldPresetInfo> infos = WorldState.allStageRegistryAccess()
                                                          .lookup(WORLD_PRESET_INFO_REGISTRY)
                                                          .orElse(null);
        if (infos == null) {
            LibWoverWorldPreset.C.LOG.error("WorldPresetInfoRegistry: Registry not read");
            return WorldPresetInfoImpl.DEFAULT;
        }
        final var info = infos.getValue(key.identifier());
        if (info == null) return WorldPresetInfoImpl.DEFAULT;
        return info;
    }

    /**
     * Gets the {@link WorldPresetInfo} registered for the given {@link WorldPreset}.
     *
     * @param holder the holder of the preset, may be {@code null}
     * @return the registered {@link WorldPresetInfo}, or a default instance if {@code holder} is
     *         {@code null}, unbound, or has no registered info
     */
    public static @NotNull WorldPresetInfo getFor(
            Holder<WorldPreset> holder
    ) {
        if (holder != null && holder.unwrapKey().isPresent()) return getFor(holder.unwrapKey().get());
        return WorldPresetInfoImpl.DEFAULT;
    }

    /**
     * Gets the {@link WorldPresetInfo} registered for the given {@link WorldPreset}.
     * <p>
     * This overload has to look up the preset's own {@link ResourceKey} in the
     * {@link net.minecraft.core.registries.Registries#WORLD_PRESET} registry first; prefer
     * {@link #getFor(ResourceKey)} or {@link #getFor(Holder)} if you already have the key/holder.
     *
     * @param preset the preset, may be {@code null}
     * @return the registered {@link WorldPresetInfo}, or a default instance if {@code preset} is
     *         {@code null}, not registered, or has no registered info
     */
    public static @NotNull WorldPresetInfo getFor(
            WorldPreset preset
    ) {
        if (preset == null) return WorldPresetInfoImpl.DEFAULT;

        final Registry<WorldPreset> presets = WorldState.allStageRegistryAccess()
                                                        .lookupOrThrow(Registries.WORLD_PRESET);
        final Optional<ResourceKey<WorldPreset>> key = presets.getResourceKey(preset);
        if (key.isPresent()) return getFor(key.get());
        return WorldPresetInfoImpl.DEFAULT;
    }
}
