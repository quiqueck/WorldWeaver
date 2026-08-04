package de.ambertation.wover.enchantment.impl;

import de.ambertation.wover.enchantment.api.EnchantmentKey;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EnchantmentKeyImpl implements EnchantmentKey {
    private final ResourceKey<Enchantment> key;

    @ApiStatus.Internal
    public EnchantmentKeyImpl(ResourceKey<Enchantment> key) {
        this.key = key;
    }

    @Override
    public ResourceKey<Enchantment> key() {
        return key;
    }


    @Override
    @Nullable
    public Holder<Enchantment> getHolder(@Nullable HolderGetter<Enchantment> getter) {
        if (getter == null) return null;

        return getter.get(key).orElse(null);
    }

    @Override
    @Nullable
    public Holder<Enchantment> getHolder(@Nullable RegistryAccess access) {
        if (access == null) return null;

        return getHolder(access.lookup(Registries.ENCHANTMENT).orElse(null));
    }

    @Override
    public Holder<Enchantment> getHolder(@NotNull BootstrapContext<?> context) {
        return getHolder(context.lookup(Registries.ENCHANTMENT));
    }

    @Override
    public void register(@NotNull BootstrapContext<Enchantment> context, Enchantment.Builder builder) {
        context.register(this.key, builder.build(key.location()));
    }
}
