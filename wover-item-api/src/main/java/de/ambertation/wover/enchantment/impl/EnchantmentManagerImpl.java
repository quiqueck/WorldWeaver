package de.ambertation.wover.enchantment.impl;

import de.ambertation.wover.core.api.registry.DatapackRegistryBuilder;
import de.ambertation.wover.events.api.types.OnBootstrapRegistry;
import de.ambertation.wover.events.impl.EventImpl;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.enchantment.Enchantment;

import org.jetbrains.annotations.ApiStatus;

public class EnchantmentManagerImpl {
    public static final EventImpl<OnBootstrapRegistry<Enchantment>> BOOTSTRAP_ENCHANTMENTS =
            new EventImpl<>("BOOTSTRAP_ENCHANTMENTS");

    private static boolean didInit = false;

    @ApiStatus.Internal
    public static void initialize() {
        if (didInit) return;
        didInit = true;

        DatapackRegistryBuilder.addBootstrap(
                Registries.ENCHANTMENT,
                EnchantmentManagerImpl::onBootstrap
        );
    }

    private static void onBootstrap(BootstrapContext<Enchantment> context) {
        BOOTSTRAP_ENCHANTMENTS.emit(c -> c.bootstrap(context));
    }
}
