package de.ambertation.wover.preset.mixin;

import de.ambertation.wover.entrypoint.LibWoverWorldPreset;
import de.ambertation.wover.preset.impl.WorldPresetsManagerImpl;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Optional;

@Mixin(DedicatedServerProperties.WorldDimensionData.class)
public class WorldDimensionDataMixin {
    @Shadow
    @Final
    private String levelType;

    @Shadow
    @Final
    private static Map<String, ResourceKey<WorldPreset>> LEGACY_PRESET_NAMES;

    /**
     * Says which World Preset a dedicated server creates its new world with, and - when that is not our
     * default - how to get the default instead.
     * <p>
     * An explicit {@code level-type} is honoured on purpose (see {@link DedicatedServerPropertiesMixin}), but
     * hosting panels and vanilla-generated {@code server.properties} files already contain
     * {@code level-type=minecraft:normal}, which silently produces vanilla Nether and End generators. Modded
     * biomes still reach those through Fabric's biome API (see {@code BiomeSourceManagerImpl}), but our own
     * biome layout does not. This is only called when a new world is created, so the message appears exactly
     * once, at the point the choice becomes permanent.
     */
    @Inject(method = "create", at = @At("RETURN"))
    void wover_logSelectedPreset(HolderLookup.Provider registryAccess, CallbackInfoReturnable<WorldDimensions> cir) {
        final HolderLookup.RegistryLookup<WorldPreset> presets = registryAccess.lookupOrThrow(Registries.WORLD_PRESET);
        // same resolution as vanilla: an id, then a legacy name, then minecraft:normal
        final ResourceKey<WorldPreset> selected = Optional
                .ofNullable(Identifier.tryParse(this.levelType))
                .map(id -> ResourceKey.create(Registries.WORLD_PRESET, id))
                .or(() -> Optional.ofNullable(LEGACY_PRESET_NAMES.get(this.levelType)))
                .filter(key -> presets.get(key).isPresent())
                .orElse(WorldPresets.NORMAL);
        final ResourceKey<WorldPreset> preferred = WorldPresetsManagerImpl.getDefault();

        // Only a vanilla preset picked while a mod suggested its own is worth a warning: other mods' presets
        // (and our own large/amplified variants) are deliberate choices we cannot judge from here.
        final boolean vanillaInsteadOfDefault = !selected.equals(preferred)
                && !preferred.identifier().getNamespace().equals(Identifier.DEFAULT_NAMESPACE)
                && selected.identifier().getNamespace().equals(Identifier.DEFAULT_NAMESPACE);

        if (vanillaInsteadOfDefault) {
            LibWoverWorldPreset.C.log.warn(
                    "Creating new world with the vanilla World Preset {} (server.properties: level-type={}) "
                            + "instead of the default preset {}. Modded Nether and End biomes (for example from "
                            + "BetterNether or BetterEnd) will still appear, but placed by the vanilla generators: "
                            + "the biome layout of {} (biome sizes, vertical Nether biomes, edge and sub-biomes, "
                            + "End void and barrens biomes) is NOT used in this world. To use it, stop the "
                            + "server, set level-type={} in server.properties and create a new world; a world "
                            + "keeps the preset it was created with, so changing level-type later has no effect.",
                    selected.identifier(),
                    this.levelType,
                    preferred.identifier(),
                    preferred.identifier(),
                    preferred.identifier()
            );
        } else {
            LibWoverWorldPreset.C.log.info(
                    "Creating new world with World Preset {} (server.properties: level-type={}).",
                    selected.identifier(),
                    this.levelType
            );
        }
    }
}
