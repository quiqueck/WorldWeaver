package org.betterx.wover.item.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

import org.jetbrains.annotations.Nullable;

/**
 * Specialized configuration class for creating armor items with armor-specific properties.
 * This class extends {@link ItemDefinition} to provide additional methods for configuring
 * armor materials, armor types, and trim materials.
 *
 * @param <I> The type of armor item being created, must extend {@link Item}
 * @author Quiqueck
 * @since 21.6.0
 */
public class ArmorItemDefinition<I extends Item> extends ItemDefinition<I, ArmorItemDefinition<I>> {
    private @Nullable ArmorMaterial material;
    private @Nullable ArmorType type;

    /**
     * Factory interface for creating armor items from configuration objects.
     * Extends the base ItemFactory to work specifically with ArmorItemDefinition.
     *
     * @param <I> The type of armor item to create
     */
    public interface ItemFactory<I extends Item> extends ItemDefinition.ItemFactory<I, ArmorItemDefinition<I>> {
    }

    /**
     * Creates a new armor item configuration.
     *
     * @param registry    The item registry to use for registration
     * @param armorName   The name identifier for the armor item
     * @param itemFactory The factory used to create the armor item instance
     */
    protected ArmorItemDefinition(
            ItemRegistry registry,
            String armorName,
            ItemDefinition.ItemFactory<I, ArmorItemDefinition<I>> itemFactory
    ) {
        super(registry, armorName, itemFactory);
    }

    public @Nullable ArmorType armorType() {
        return this.type;
    }

    public @Nullable ArmorMaterial material() {
        return this.material;
    }

    /**
     * Called before the armor item is built to allow for any final configuration.
     * Currently empty but can be overridden by subclasses for custom setup logic.
     */
    @Override
    protected void beforeBuild() {

    }

    /**
     * Called before the armor item is registered to allow for any final modifications.
     * This default implementation returns the item unchanged, but subclasses can override
     * this method to perform custom post-creation setup before registration.
     *
     * @param item The built armor item instance
     * @return The armor item instance (potentially modified) that should be registered
     */
    @Override
    protected I beforeRegister(I item) {
        return item;
    }

    /**
     * Configures this item as humanoid armor with the specified material and type.
     * This is used for standard player armor pieces like helmets, chestplates, leggings, and boots.
     *
     * @param material The armor material defining protection values, durability, and other properties
     * @param type     The armor type (HELMET, CHESTPLATE, LEGGINGS, BOOTS) defining which slot this armor occupies
     * @return This configuration instance for method chaining
     */
    public ArmorItemDefinition<I> humanoidArmor(ArmorMaterial material, ArmorType type) {
        this.material = material;
        this.type = type;
        this.properties.humanoidArmor(material, type);
        return this;
    }

    /**
     * Configures this item as wolf armor with the specified material.
     * Wolf armor provides protection for tamed wolves.
     *
     * @param material The armor material defining protection values and durability for wolf armor
     * @return This configuration instance for method chaining
     */
    public ArmorItemDefinition<I> wolfArmor(ArmorMaterial material) {
        this.material = material;
        this.type = ArmorType.BODY;

        this.properties.wolfArmor(material);
        return this;
    }

    /**
     * Configures this item as horse armor with the specified material.
     * Horse armor provides protection for horses and other equine mobs.
     *
     * @param material The armor material defining protection values and durability for horse armor
     * @return This configuration instance for method chaining
     */
    public ArmorItemDefinition<I> horseArmor(ArmorMaterial material) {
        this.material = material;
        this.type = ArmorType.BODY;

        this.properties.horseArmor(material);
        return this;
    }

    /**
     * Sets the trim material that can be used to customize this armor's appearance.
     * Trim materials allow players to add decorative patterns to armor pieces.
     *
     * @param trimMaterialKey The resource key for the trim material
     * @return This configuration instance for method chaining
     */
    public ArmorItemDefinition<I> trimMaterial(ResourceKey<TrimMaterial> trimMaterialKey) {
        this.properties.trimMaterial(trimMaterialKey);
        return this;
    }
}
