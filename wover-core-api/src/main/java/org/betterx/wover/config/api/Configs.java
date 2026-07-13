package org.betterx.wover.config.api;

import de.ambertation.wunderlib.configs.AbstractConfig;
import org.betterx.wover.config.impl.ConfigsImpl;
import org.betterx.wover.core.api.ModCore;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * Central registry for {@link AbstractConfig} instances.
 * <p>
 * Mods that want to expose configuration options should create a subclass of {@link AbstractConfig} (for example
 * a {@code ConfigFile}, see {@link MainConfig} for an example) and register it here with one of the
 * {@link #register} methods. Once registered, the config can be looked up again with {@link #get(ResourceLocation)}
 * and saved together with all other registered configs using {@link #saveConfigs()}.
 * <p>
 * WorldWeaver's own general configuration is exposed as {@link #MAIN}.
 */
public class Configs {
    /**
     * Factory used by {@link #register(ModCore, String, ConfigSupplier)} to create a new config instance for a
     * given mod and category.
     *
     * @param <T> The type of the config that is created.
     */
    public interface ConfigSupplier<T extends AbstractConfig<?>> {
        /**
         * Creates a new config instance.
         *
         * @param owner    The mod that owns the config.
         * @param category The category the config belongs to.
         * @return The newly created config instance.
         */
        T create(ModCore owner, String category);
    }

    /**
     * WorldWeaver's main configuration file. Contains general options that apply to WorldWeaver itself, like
     * whether verbose logging is enabled ({@link MainConfig#verboseLogging}).
     */
    public static final MainConfig MAIN = register(MainConfig::new);

    /**
     * Saves all configs that were registered with one of the {@link #register} methods to disk.
     */
    public static void saveConfigs() {
        ConfigsImpl.saveConfigs();
    }

    /**
     * Registers a new config for the given mod and category.
     *
     * @param mod            The mod that owns the config.
     * @param category       The category the config belongs to.
     * @param configSupplier The factory used to create the config instance.
     * @param <T>            The type of the config that is registered.
     * @return The newly created and registered config instance.
     */
    public static <T extends AbstractConfig<?>> T register(
            ModCore mod,
            String category,
            Configs.ConfigSupplier<T> configSupplier
    ) {
        return ConfigsImpl.register(mod, category, configSupplier);
    }

    /**
     * Registers a new config.
     *
     * @param configSupplier The factory used to create the config instance.
     * @param <T>            The type of the config that is registered.
     * @return The newly created and registered config instance.
     */
    public static <T extends AbstractConfig<?>> T register(
            Supplier<T> configSupplier
    ) {
        return ConfigsImpl.register(configSupplier);
    }

    /**
     * Looks up a previously registered config by its location.
     *
     * @param location The location of the config, usually {@code AbstractConfig#location}.
     * @param <T>       The type of the config that is looked up.
     * @return The config instance, or {@code null} if no config was registered for the given location.
     */
    public static <T extends AbstractConfig<?>> T get(ResourceLocation location) {
        return ConfigsImpl.get(location);
    }
}
