package org.betterx.wover.enchantment.api;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A key interface for managing enchantment references and registration.
 * 
 * <p>This interface provides a convenient way to work with enchantments by wrapping
 * the {@link ResourceKey} and providing utility methods for retrieving holders and
 * registering enchantments in various contexts.
 * 
 * <p>The main purpose of this interface is to:
 * <ul>
 *   <li>Provide a type-safe reference to enchantments</li>
 *   <li>Simplify holder retrieval from different sources</li>
 *   <li>Streamline the enchantment registration process</li>
 * </ul>
 * 
 * @see Enchantment
 * @see ResourceKey
 * @see Holder
 */
public interface EnchantmentKey {
    /**
     * The key for the {@link Enchantment} you can use to reference it.
     *
     * @return The key
     */
    ResourceKey<Enchantment> key();

    /**
     * Gets the {@link Holder} for the {@link Enchantment} from the given getter.
     *
     * @param enchantmentGetter The getter to get the holder from or {@code null}
     * @return The holder for the {@link Enchantment} or {@code null} if it is not present
     */
    @Nullable
    Holder<Enchantment> getHolder(@Nullable HolderGetter<Enchantment> enchantmentGetter);

    /**
     * Gets the {@link Holder} for the {@link Enchantment} from the given registry access.
     *
     * @param registryAccess The registry access to get the holder from
     * @return The holder for the {@link Enchantment} or {@code null} if it is not present
     */
    @Nullable
    Holder<Enchantment> getHolder(@Nullable RegistryAccess registryAccess);

    /**
     * Gets the {@link Holder} for the {@link Enchantment} from the given bootstrap context.
     *
     * <p>
     * This method internally looks up {@link Registries#ENCHANTMENT}. If you need to retrieve
     * a lot of holders, it is recommended to manually lookup the
     * Registry first and use {@link #getHolder(HolderGetter)} instead.
     *
     * @param bootstrapContext The {@link BootstrapContext} to get the holder from
     * @return The holder for the {@link Enchantment} or {@code null} if it is not present
     */
    Holder<Enchantment> getHolder(@NotNull BootstrapContext<?> bootstrapContext);

    /**
     * Registers the {@link Enchantment} with the given builder to the given context.
     *
     * @param bootstrapContext The context to register the {@link Enchantment} with
     * @param enchantmentBuilder The builder to use to build the {@link Enchantment}
     */
    void register(@NotNull BootstrapContext<Enchantment> bootstrapContext, Enchantment.Builder enchantmentBuilder);
}
