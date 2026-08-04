package de.ambertation.wover.datagen.api.provider;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverDataProvider;
import de.ambertation.wover.item.api.armor.EquipmentAssetRegistry;
import de.ambertation.wover.item.api.armor.EquipmentAssetSpec;

import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;

/**
 * Generates {@code assets/<namespace>/equipment/<path>.json} files for every {@link EquipmentAssetSpec}
 * registered through {@link EquipmentAssetRegistry} - typically populated by
 * {@link de.ambertation.wover.item.api.armor.CustomArmorMaterial.Builder}'s {@code humanoidEquipmentAsset()} /
 * {@code wingsEquipmentAsset(ResourceLocation)} / {@code equipmentAsset(Consumer)} methods.
 *
 * <p>This mirrors vanilla's own (client-only) {@code EquipmentAssetProvider} almost exactly - same
 * {@link PackOutput.Target#RESOURCE_PACK}/{@code "equipment"} path provider, same {@link EquipmentClientInfo#CODEC}
 * fed through {@link DataProvider#saveAll} - except the set of assets to write comes from whichever materials
 * opted in via {@link EquipmentAssetRegistry}, instead of a hardcoded vanilla bootstrap list.
 *
 * <p>{@link EquipmentClientInfo} is a client-only type ({@code net.minecraft.client.resources.model}), which is
 * why this class - unlike {@link EquipmentAssetRegistry} and {@link EquipmentAssetSpec} - lives in wover-item-api's
 * <b>client</b> sourceset: it must never be referenced from code that also loads on a dedicated server.
 *
 * <p>Only entries whose asset id's namespace matches this provider's {@link ModCore#namespace} are written, so
 * that registering this provider once per mod (e.g. {@code globalPack.addProvider(WoverEquipmentAssetProvider::new)}
 * in that mod's {@code WoverDataGenEntryPoint}) does not also emit equipment assets belonging to some other mod
 * that happens to share the same datagen run / classloader.
 */
public class WoverEquipmentAssetProvider implements WoverDataProvider<DataProvider> {
    protected final ModCore modCore;

    public WoverEquipmentAssetProvider(ModCore modCore) {
        this.modCore = modCore;
    }

    /**
     * Converts a common-side {@link EquipmentAssetSpec} into a real, client-only {@link EquipmentClientInfo}.
     */
    private static EquipmentClientInfo convert(EquipmentAssetSpec spec) {
        final EquipmentClientInfo.Builder builder = EquipmentClientInfo.builder();
        spec.layers().forEach((type, layers) -> {
            final EquipmentClientInfo.LayerType layerType = EquipmentClientInfo.LayerType.valueOf(type.name());
            final EquipmentClientInfo.Layer[] converted = layers
                    .stream()
                    .map(WoverEquipmentAssetProvider::convert)
                    .toArray(EquipmentClientInfo.Layer[]::new);
            builder.addLayers(layerType, converted);
        });
        return builder.build();
    }

    private static EquipmentClientInfo.Layer convert(EquipmentAssetSpec.Layer layer) {
        final Optional<EquipmentClientInfo.Dyeable> dyeable = layer.dyeable()
                ? Optional.of(new EquipmentClientInfo.Dyeable(Optional.empty()))
                : Optional.empty();
        return new EquipmentClientInfo.Layer(layer.texture(), dyeable, layer.usePlayerTexture());
    }

    @Override
    public DataProvider getProvider(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        return new Provider(output, modCore);
    }

    private static class Provider implements DataProvider {
        private final PackOutput.PathProvider pathProvider;
        private final ModCore modCore;

        private Provider(FabricDataOutput output, ModCore modCore) {
            this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "equipment");
            this.modCore = modCore;
        }

        @Override
        public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
            final Map<ResourceKey<EquipmentAsset>, EquipmentClientInfo> equipmentAssets = new HashMap<>();
            EquipmentAssetRegistry.entries().forEach((assetId, spec) -> {
                if (!assetId.location().getNamespace().equals(modCore.namespace)) return;
                equipmentAssets.put(assetId, convert(spec));
            });
            return DataProvider.saveAll(cache, EquipmentClientInfo.CODEC, pathProvider::json, equipmentAssets);
        }

        @Override
        public @NotNull String getName() {
            return "Equipment Assets (" + modCore.namespace + ")";
        }
    }
}
