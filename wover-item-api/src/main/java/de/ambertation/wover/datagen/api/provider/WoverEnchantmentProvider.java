package de.ambertation.wover.datagen.api.provider;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverRegistryContentProvider;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Base class for datagen providers that register {@link Enchantment}s (world-generation/registry style datapack
 * entries, written to {@code data/<namespace>/enchantment/<path>.json}).
 *
 * <p>Subclass this and implement {@link #bootstrap(BootstrapContext)} to register enchantments with
 * {@link de.ambertation.wover.enchantment.api.EnchantmentKey#register(BootstrapContext, Enchantment.Builder)}, then
 * add the provider to a {@code PackBuilder} from a {@code WoverDataGenEntryPoint}.
 *
 * @see de.ambertation.wover.enchantment.api.EnchantmentManager
 * @see de.ambertation.wover.enchantment.api.EnchantmentKey
 */
public abstract class WoverEnchantmentProvider extends WoverRegistryContentProvider<Enchantment> {
    /**
     * Creates a new instance of {@link WoverRegistryContentProvider}.
     *
     * @param modCore The ModCore instance of the Mod that is providing this instance.
     * @param title   The title of the provider. Mainly used for logging.
     */
    public WoverEnchantmentProvider(
            ModCore modCore,
            String title
    ) {
        super(modCore, title, Registries.ENCHANTMENT);
    }

    /**
     * Registers this provider's enchantments with the given bootstrap context.
     *
     * @param context The bootstrap context used to register enchantments and look up other registries
     */
    @Override
    abstract protected void bootstrap(BootstrapContext<Enchantment> context);
}
