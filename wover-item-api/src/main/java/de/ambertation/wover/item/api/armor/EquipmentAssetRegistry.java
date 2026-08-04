package de.ambertation.wover.item.api.armor;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A common-side registry of {@link EquipmentAssetSpec}s that should be turned into generated
 * {@code assets/<namespace>/equipment/<path>.json} files at datagen time.
 *
 * <p>Entries are populated by {@link CustomArmorMaterial.Builder#build()} whenever an equipment
 * asset was declared on the builder, keyed by the material's {@code assetId}. They are consumed
 * client-side, by the equipment-asset datagen provider in wover-item-api's client sourceset, which
 * converts each {@link EquipmentAssetSpec} into a real (client-only) {@code EquipmentClientInfo}
 * and serializes it - mirroring vanilla's own {@code EquipmentAssetProvider}, but driven by
 * whichever materials opted in instead of a hardcoded list.
 *
 * <p>{@link EquipmentAsset} and {@link ResourceKey} are plain registry-key types (not client-only),
 * so this registry itself is safe to populate and read from common code and can be loaded on a
 * dedicated server without issue.
 */
public final class EquipmentAssetRegistry {
    private static final Map<ResourceKey<EquipmentAsset>, EquipmentAssetSpec> SPECS = new LinkedHashMap<>();

    private EquipmentAssetRegistry() {
    }

    /**
     * Registers an {@link EquipmentAssetSpec} for the given asset id.
     *
     * @param assetId The {@link ResourceKey} the spec should be generated under
     * @param spec    The spec describing the equipment asset's layers
     */
    public static void register(ResourceKey<EquipmentAsset> assetId, EquipmentAssetSpec spec) {
        SPECS.put(assetId, spec);
    }

    /**
     * Returns an unmodifiable view of every {@link EquipmentAssetSpec} registered so far, keyed by
     * asset id.
     *
     * @return The registered specs
     */
    public static Map<ResourceKey<EquipmentAsset>, EquipmentAssetSpec> entries() {
        return Collections.unmodifiableMap(SPECS);
    }
}
