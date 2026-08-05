package de.ambertation.wover.item.api.armor;

import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * A common-side (client-independent) description of the layers that an {@code EquipmentAsset}
 * should have.
 *
 * <p>Minecraft's actual equipment asset representation ({@code EquipmentClientInfo}, in
 * {@code net.minecraft.client.resources.model}) is a client-only type, and cannot be referenced
 * from code that also needs to run on a dedicated server - which includes armor material
 * definitions built through {@link CustomArmorMaterial.Builder}. This class mirrors the shape of
 * vanilla's client-only {@code EquipmentClientInfo.Builder} closely enough that it can be declared
 * from common code, then converted 1:1 into a real {@code EquipmentClientInfo} by client-side
 * datagen (see {@code WoverEquipmentAssetProvider} in wover-item-api's client sourceset), which
 * writes it to {@code assets/<namespace>/equipment/<path>.json}.
 *
 * @see CustomArmorMaterial.Builder#humanoidEquipmentAsset()
 * @see CustomArmorMaterial.Builder#wingsEquipmentAsset(Identifier)
 * @see CustomArmorMaterial.Builder#equipmentAsset(java.util.function.Consumer)
 */
public final class EquipmentAssetSpec {
    /**
     * Mirrors (a subset of) vanilla's {@code EquipmentClientInfo.LayerType} enum by name only, so
     * client-side code can resolve the real, client-only enum constant via
     * {@code EquipmentClientInfo.LayerType.valueOf(name())} without this (common) class ever
     * depending on the client-only type itself. Only the layer types relevant to armor/elytra are
     * listed here; add more constants (matching vanilla's names exactly) if other equipment kinds
     * need to be supported.
     */
    public enum LayerType {
        HUMANOID,
        HUMANOID_LEGGINGS,
        HUMANOID_BABY,
        WINGS
    }

    /**
     * A single layer entry - mirrors vanilla's {@code EquipmentClientInfo.Layer}.
     *
     * @param texture          The texture {@link Identifier} for this layer
     * @param usePlayerTexture Whether the player's own skin should be blended in (used by elytra wings)
     * @param dyeable          Whether this layer supports leather-style dyeing
     */
    public record Layer(Identifier texture, boolean usePlayerTexture, boolean dyeable) {
        public Layer(Identifier texture) {
            this(texture, false, false);
        }
    }

    private final Map<LayerType, List<Layer>> layersByType = new EnumMap<>(LayerType.class);

    private EquipmentAssetSpec() {
    }

    /**
     * Creates a new, empty spec.
     *
     * @return A new, empty {@link EquipmentAssetSpec}
     */
    public static EquipmentAssetSpec create() {
        return new EquipmentAssetSpec();
    }

    /**
     * Adds one or more layers for the given {@link LayerType}.
     *
     * @param type   The layer type/slot to add the layers for
     * @param layers The layers to add
     * @return This instance, for chaining
     */
    public EquipmentAssetSpec addLayers(LayerType type, Layer... layers) {
        this.layersByType.computeIfAbsent(type, t -> new ArrayList<>()).addAll(Arrays.asList(layers));
        return this;
    }

    /**
     * Adds the standard humanoid + humanoid_leggings layers, both pointing at {@code texture},
     * matching the common "plain armor set" shape used by most custom armor materials.
     * <p>
     * Unlike vanilla's own {@code EquipmentClientInfo.Builder#addHumanoidLayers}, this does
     * <b>not</b> also add a {@code humanoid_baby} layer - none of this project's hand-authored
     * equipment assets ever declared one, so matching that (rather than vanilla's own default)
     * keeps datagen output byte-for-byte equivalent to what shipped before.
     *
     * @param texture The texture to use for both layers
     * @return This instance, for chaining
     */
    public EquipmentAssetSpec addHumanoidLayers(Identifier texture) {
        return addHumanoidLayers(texture, false);
    }

    /**
     * Same as {@link #addHumanoidLayers(Identifier)}, but also marks the layers as leather-style
     * dyeable.
     *
     * @param texture The texture to use for both layers
     * @param dyeable Whether the layers should support leather-style dyeing
     * @return This instance, for chaining
     */
    public EquipmentAssetSpec addHumanoidLayers(Identifier texture, boolean dyeable) {
        addLayers(LayerType.HUMANOID, new Layer(texture, false, dyeable));
        addLayers(LayerType.HUMANOID_LEGGINGS, new Layer(texture, false, dyeable));
        return this;
    }

    /**
     * Adds a {@code wings} layer for {@code texture}, with {@code use_player_texture} set - the
     * shape elytra items need.
     *
     * @param texture The texture to use for the wings layer
     * @return This instance, for chaining
     */
    public EquipmentAssetSpec addWingsLayer(Identifier texture) {
        return addLayers(LayerType.WINGS, new Layer(texture, true, false));
    }

    /**
     * Returns an unmodifiable view of the layers configured so far, keyed by {@link LayerType}.
     *
     * @return The configured layers
     */
    public Map<LayerType, List<Layer>> layers() {
        return Collections.unmodifiableMap(layersByType);
    }

    /**
     * Checks whether any layer was configured.
     *
     * @return {@code true} if no layer was added
     */
    public boolean isEmpty() {
        return layersByType.isEmpty();
    }
}
