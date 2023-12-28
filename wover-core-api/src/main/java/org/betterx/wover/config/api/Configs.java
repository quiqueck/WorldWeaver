package org.betterx.wover.config.api;

import de.ambertation.wunderlib.configs.AbstractConfig;
import org.betterx.wover.config.impl.ConfigsImpl;
import org.betterx.wover.core.api.ModCore;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class Configs {
    public interface ConfigSupplier<T extends AbstractConfig<?>> {
        T create(ModCore owner, String category);
    }

    public static final MainConfig MAIN = register(MainConfig::new);

    public static void saveConfigs() {
        ConfigsImpl.saveConfigs();
    }

    public static <T extends AbstractConfig<?>> T register(
            ModCore mod,
            String category,
            Configs.ConfigSupplier<T> configSupplier
    ) {
        return ConfigsImpl.register(mod, category, configSupplier);
    }

    public static <T extends AbstractConfig<?>> T register(
            Supplier<T> configSupplier
    ) {
        return ConfigsImpl.register(configSupplier);
    }

    public static <T extends AbstractConfig<?>> T get(ResourceLocation location) {
        return ConfigsImpl.get(location);
    }
}
