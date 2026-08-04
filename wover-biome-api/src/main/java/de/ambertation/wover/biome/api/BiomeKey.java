package de.ambertation.wover.biome.api;

import de.ambertation.wover.biome.api.builder.BiomeBootstrapContext;
import de.ambertation.wover.biome.api.builder.BiomeBuilder;
import de.ambertation.wover.biome.api.data.BiomeData;
import de.ambertation.wover.biome.api.data.BiomeDataRegistry;
import de.ambertation.wover.biome.impl.BiomeManagerImpl;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A handle for a {@link Biome} that was (or will be) registered through this API.
 * <p>
 * A {@link BiomeKey} bundles the {@link ResourceKey} of the {@link Biome} together with the
 * {@link ResourceKey} of the matching {@link BiomeData} entry, and acts as the entry point for
 * creating the {@link BiomeBuilder} that fills in the actual content of the Biome.
 * <p>
 * Instances are created through {@link de.ambertation.wover.biome.api.BiomeManager#vanilla(ResourceLocation)}
 * (a fully custom, vanilla-style Biome) or
 * {@link de.ambertation.wover.biome.api.BiomeManager#wrapped(ResourceKey)} (attaching {@link BiomeData} to an
 * already existing Biome, for example one added by vanilla or another mod).
 *
 * @param <B> The type of {@link BiomeBuilder} returned by {@link #bootstrap(BiomeBootstrapContext)}.
 */
public abstract class BiomeKey<B extends BiomeBuilder<B>> {
    /**
     * The key for the {@link Biome} you can use to reference it.
     */
    @NotNull
    public final ResourceKey<Biome> key;

    /**
     * The key for the {@link BiomeData}.
     */
    @NotNull
    public final ResourceKey<BiomeData> dataKey;

    /**
     * Gets the {@link Holder} for the {@link Biome} from the given getter.
     *
     * @param getter The getter to get the holder from or {@code null}
     * @return The holder for the {@link Biome} or {@code null} if it is not present
     */
    @Nullable
    public Holder<Biome> getHolder(@Nullable HolderGetter<Biome> getter) {
        return getter == null ? null : getter.get(this.key).orElse(null);
    }

    /**
     * Gets the {@link Holder} for the {@link Biome} from the given getter.
     *
     * @param access The registry access to get the holder from
     * @return The holder for the {@link Biome} or {@code null} if it is not present
     */
    @Nullable
    public Holder<Biome> getHolder(@Nullable RegistryAccess access) {
        return access == null
                ? null
                : access.lookupOrThrow(Registries.BIOME).get(this.key).orElse(null);
    }

    /**
     * Gets the {@link Holder} for the {@link Biome} from the given getter.
     *
     * @param provider The provider to get the holder from or {@code null}
     * @return The holder for the {@link Biome} or {@code null} if it is not present
     */
    @Nullable
    public Holder<Biome> getHolder(@Nullable HolderLookup.@NotNull Provider provider) {
        return provider == null
                ? null
                : provider.lookup(Registries.BIOME).orElseThrow().get(this.key).orElse(null);
    }


    /**
     * Gets the {@link Holder} for the {@link Biome} from the given getter.
     *
     * <p>
     * This method internally looks up {@link Registries#BIOME}. If you need to retrieve
     * a lot of holders, it is recommended to manually lookup the
     * Registry first and use {@link #getHolder(HolderGetter)} instead.
     *
     * @param context The {@link BootstrapContext} to get the holder from
     * @return The holder for the {@link Biome} or {@code null} if it is not present
     */
    public Holder<Biome> getHolder(@NotNull BootstrapContext<?> context) {
        return context.lookup(Registries.BIOME).get(this.key).orElse(null);
    }

    /**
     * Creates the {@link BiomeBuilder} for this key.
     * <p>
     * The returned builder still needs to be filled in and finished with a call to
     * {@link BiomeBuilder#register()}.
     *
     * @param context The bootstrap context that the builder will register itself with.
     * @return A new builder instance for this key.
     */
    public abstract B bootstrap(BiomeBootstrapContext context);

    /**
     * Creates a new {@link BiomeKey} for the given location.
     * <p>
     * The {@link #key} is created in the {@link Registries#BIOME} registry, while {@link #dataKey} is
     * created for the same location in the {@link BiomeDataRegistry}.
     *
     * @param location The location of the Biome.
     */
    protected BiomeKey(@NotNull ResourceLocation location) {
        this.key = BiomeManagerImpl.createKey(location);
        this.dataKey = BiomeDataRegistry.createKey(location);
    }
}
