package de.ambertation.wover.config.impl;

import de.ambertation.wunderlib.configs.AbstractConfig;
import de.ambertation.wover.config.api.Configs;
import de.ambertation.wover.core.api.ModCore;

import net.minecraft.resources.Identifier;

import java.util.Hashtable;
import java.util.Map;
import java.util.function.Supplier;

public class ConfigsImpl {
    private static final Map<Identifier, AbstractConfig<?>> CONFIGS = new Hashtable<>();

    public static <T extends AbstractConfig<?>> T register(
            ModCore mod,
            String category,
            Configs.ConfigSupplier<T> configSupplier
    ) {
        T config = configSupplier.create(mod, category);
        CONFIGS.put(config.location, config);
        return config;
    }

    public static <T extends AbstractConfig<?>> T register(
            Supplier<T> configSupplier
    ) {
        T config = configSupplier.get();
        CONFIGS.put(config.location, config);
        return config;
    }

    public static void saveConfigs() {
        CONFIGS.values().forEach(AbstractConfig::save);
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractConfig<?>> T get(Identifier location) {
        return (T) CONFIGS.get(location);
    }
}
