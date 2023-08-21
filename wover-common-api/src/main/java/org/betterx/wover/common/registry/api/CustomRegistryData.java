package org.betterx.wover.common.registry.api;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Access custom Registry Data.
 * Wover adds a map for custom Data to every {@link net.minecraft.core.MappedRegistry} Object.
 */
public interface CustomRegistryData {
    class DataKey<T> {
        public @NotNull
        final ResourceLocation id;

        public DataKey(@NotNull ResourceLocation id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DataKey<?> dataKey)) return false;
            return Objects.equals(id, dataKey.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    /**
     * Get custom Data from the Registry.
     *
     * @param id  The Data ID
     * @param <T> The Data Type
     * @return The Data (or null)
     */
    <T> T wover_getData(DataKey<T> id);

    /**
     * Get existing data or create a new one using a passed function
     *
     * @param id  The Data ID
     * @param fkt The function to create a new Data
     * @param <T> The Data Type
     * @return The Data
     */
    <T> @Nullable T wover_computeDataIfAbsent(@NotNull DataKey<T> id, @NotNull Function<ResourceLocation, T> fkt);

    /**
     * Put custom Data into the Registry.
     *
     * @param id   The Data ID
     * @param data The Data
     * @param <T>  The Data Type
     */
    <T> void wover_putData(DataKey<T> id, T data);
}
