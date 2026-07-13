package org.betterx.wover.enchantment.api;

import org.betterx.wover.state.api.WorldState;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

/**
 * Static utility methods for looking up enchantment holders and applying/querying enchantments on items and
 * entities, without needing direct access to a {@link net.minecraft.core.HolderLookup.Provider}.
 *
 * <p>Most methods here mirror {@link net.minecraft.world.item.enchantment.EnchantmentHelper} but resolve the
 * {@link net.minecraft.core.Holder} for a {@link ResourceKey} from a {@link Level}, a
 * {@link net.minecraft.core.HolderLookup.RegistryLookup}, or (via {@link WorldState}) the current world, and
 * return safe defaults (e.g. {@code 0} or {@code false}) instead of throwing when the registry isn't available.
 */
public class EnchantmentUtils {
    private EnchantmentUtils() {
    }

    /**
     * Gets an enchantment from the world. This method is safe to call from anywhere. But it will only
     * work if the WorldState has a valid registryAccess.
     *
     * @param enchantment The enchantment to get
     * @return The enchantment, or null if it does not exist.
     */
    public static Holder<Enchantment> getEnchantment(ResourceKey<Enchantment> enchantment) {
        return WorldState.registryAccess()
                         .lookup(Registries.ENCHANTMENT)
                         .flatMap(r -> r.get(enchantment))
                         .orElse(null);
    }

    /**
     * Gets an enchantment from the world. This method is safe to call from anywhere.
     *
     * @param world       The world to get the enchantment from (actually the registryAccess of the world to get the item from)
     * @param enchantment The enchantment to get
     * @return The enchantment, or null if it does not exist.
     */
    public static Holder<Enchantment> getEnchantment(Level world, ResourceKey<Enchantment> enchantment) {
        return world
                .registryAccess()
                .lookup(Registries.ENCHANTMENT)
                .flatMap(r -> r.get(enchantment))
                .orElse(null);
    }

    /**
     * Gets an enchantment from the world. This method is safe to call from anywhere.
     *
     * @param lookup      A Lookup object for the enchantment registry
     * @param enchantment The enchantment to get
     * @return The enchantment, or null if it does not exist.
     */
    public static Holder<Enchantment> getEnchantment(
            HolderLookup.RegistryLookup<Enchantment> lookup,
            ResourceKey<Enchantment> enchantment
    ) {
        return lookup
                .get(enchantment)
                .orElse(null);
    }

    /**
     * Gets the level of an enchantment on an item. This method is safe to call from anywhere.
     *
     * @param world       The world to get the enchantment from (actually the registryAccess of the world to get the item from)
     * @param enchantment The enchantment to get
     * @param stack       The item to check for the enchantment
     * @return The level of the enchantment on the item
     */
    public static int getItemEnchantmentLevel(
            Level world,
            ResourceKey<Enchantment> enchantment,
            ItemStack stack
    ) {
        final Holder<Enchantment> enc = getEnchantment(world, enchantment);
        if (enc != null) {
            return EnchantmentHelper.getItemEnchantmentLevel(enc, stack);
        }

        return 0;
    }

    /**
     * Gets the level of an enchantment on an entity. This method is safe to call from anywhere.
     *
     * @param world       The world to get the enchantment from (actually the registryAccess of the world to get the item from)
     * @param enchantment The enchantment to get
     * @param entity      The entity to check for the enchantment
     * @return The level of the enchantment on the entity
     */
    public static int getItemEnchantmentLevel(
            Level world,
            ResourceKey<Enchantment> enchantment,
            LivingEntity entity
    ) {
        final Holder<Enchantment> enc = getEnchantment(world, enchantment);
        if (enc != null) {
            return EnchantmentHelper.getEnchantmentLevel(enc, entity);
        }

        return 0;
    }

    /**
     * Enchants an item in the world. This method is safe to call from anywhere. But it will only
     * work if the WorldState has a valid registryAccess.
     *
     * @param stack       The item to enchant
     * @param enchantment The enchantment to apply
     * @param level       The level of the enchantment
     * @param provider    The provider to use to lookup the enchantment Registry. If null, this method will return false.
     * @return True if the enchantment was applied, false otherwise.
     */
    public static boolean enchantInWorld(
            ItemStack stack,
            ResourceKey<Enchantment> enchantment,
            int level,
            HolderLookup.Provider provider
    ) {
        if (provider == null) return false;
        return enchantInWorld(stack, enchantment, level, provider.lookup(Registries.ENCHANTMENT).orElse(null));
    }

    /**
     * Enchants an item in the world. This method is safe to call from anywhere. But it will only
     * work if the WorldState has a valid registryAccess.
     *
     * @param stack       The item to enchant
     * @param enchantment The enchantment to apply
     * @param level       The level of the enchantment
     * @param lookup      The lookup for the enchantment Registry. If null, this method will return false.
     * @return True if the enchantment was applied, false otherwise.
     */
    public static boolean enchantInWorld(
            ItemStack stack,
            ResourceKey<Enchantment> enchantment,
            int level,
            HolderLookup.RegistryLookup<Enchantment> lookup
    ) {
        if (lookup == null) return false;

        return lookup
                .get(enchantment)
                .map(e -> {
                    stack.enchant(e, level);
                    return true;
                })
                .orElse(false);
    }
}
