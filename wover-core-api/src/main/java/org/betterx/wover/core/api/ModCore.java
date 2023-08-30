package org.betterx.wover.core.api;

import de.ambertation.wunderlib.utils.Version;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.util.*;
import java.util.stream.Stream;


/**
 * This class is used to identify your mod and provide some helpfull utilities
 * specific to your Mod.
 * <p>
 * It is considered best practice to create, and store an instance of this class
 * for you mod in your main Entrypoint (the class that implements
 * {@link net.fabricmc.api.ModInitializer}).
 */
public final class ModCore implements Version.ModVersionProvider {
    private static final HashMap<String, ModCore> cache = new HashMap<>();

    private final List<ResourceLocation> providedDatapacks = new LinkedList<>();
    /**
     * This logger is used to write text to the console and the log file.
     * The mod id is used as the logger's name, making it clear which mod wrote info,
     * warnings, and errors.
     */
    public final Logger LOG;

    /**
     * alias for {@link #LOG}
     */
    public final Logger log;

    /**
     * The mod id is used to identify your mod.
     */
    public final String modId;
    public final String namespace;
    private final Version modVersion;

    public final ModContainer modContainer;

    private ModCore(String modID, String namespace) {
        LOG = Logger.create(modID);
        log = LOG;
        modId = modID;
        this.namespace = namespace;

        Optional<ModContainer> optional = FabricLoader.getInstance().getModContainer(modId);
        if (optional.isPresent()) {
            this.modContainer = optional.get();
            modVersion = new Version(modContainer.getMetadata().getVersion().toString());
        } else {
            this.modContainer = null;
            modVersion = new Version(0, 0, 0);
            ;
        }
    }


    /**
     * Returns the {@link Version} of this mod.
     *
     * @return the {@link Version} of this mod.
     */
    public Version getModVersion() {
        return modVersion;
    }

    /**
     * Returns the {@link #modId} of this mod.
     *
     * @return the {@link #modId} of this mod.
     */
    @Override
    public String getModID() {
        return modId;
    }

    /**
     * Returns the namespace of this mod.
     *
     * @return the namespace of this mod.
     */
    @Override
    public String getNamespace() {
        return namespace;
    }

    /**
     * Returns the {@link ResourceLocation} for the given name in the namespace of this mod.
     * <p>
     * You should always prefer this method over {@link ResourceLocation#ResourceLocation(String, String)}.
     *
     * @param name The name or path of the resource.
     * @return The {@link ResourceLocation} for the given name.
     */
    public ResourceLocation id(String name) {
        return new ResourceLocation(namespace, name);
    }


    /**
     * Returns the {@link ResourceLocation} for the given path in the namespace of this mod.
     *
     * @param location The {@link ResourceLocation} to convert.
     * @return The {@link ResourceLocation} for the given path in the namespace of this Mod.
     */
    public ResourceLocation convertNamespace(ResourceLocation location) {
        return id(location.getPath());
    }

    /**
     * Returns the {@link ResourceLocation} for the given path in the namespace of this mod.
     *
     * @param key The {@link ResourceKey} to convert.
     * @return The {@link ResourceLocation} for the given path in the namespace of this Mod.
     */
    public <T> ResourceLocation convertNamespace(ResourceKey<T> key) {
        return convertNamespace(key.location());
    }

    /**
     * alias for {@link #id(String)}
     *
     * @param key The name or path of the resource.
     * @return The {@link ResourceLocation} for the given name.
     */
    @Override
    public ResourceLocation mk(String key) {
        return new ResourceLocation(namespace, key);
    }

    /**
     * Returns a stream of all Datapacks {@link ResourceLocation}s that are provided by this mod.
     *
     * @return a stream of all Datapacks {@link ResourceLocation}s that are provided by this mod.
     */
    public Stream<ResourceLocation> providedDatapacks() {
        return providedDatapacks.stream();
    }

    /**
     * Register a Datapack {@link ResourceLocation} that is provided by this mod.
     *
     * @param name           The name of the Datapack.
     * @param activationType The {@link ResourcePackActivationType} of the Datapack.
     * @return The {@link ResourceLocation} of the Datapack.
     */
    public ResourceLocation addDatapack(String name, ResourcePackActivationType activationType) {
        final ResourceLocation id = id(name);
        providedDatapacks.add(id);

        ResourceManagerHelper.registerBuiltinResourcePack(
                id,
                this.modContainer,
                activationType
        );
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModCore modCore)) return false;
        return Objects.equals(modId, modCore.modId) && Objects.equals(namespace, modCore.namespace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modId, namespace);
    }

    @Override
    public String toString() {
        return "ModCore: " + modId + " (" + namespace + ")";
    }

    /**
     * Returns the instance of {@link ModCore} for the given mod id. Every mod id has a unique, single
     * instance. Calling this method multiple times with the same mod id is guaranteed to return
     * the same instance.
     *
     * @param modID The mod id of the mod.
     * @return The instance of {@link ModCore} for the given mod id.
     */
    public static ModCore create(String modID) {
        return cache.computeIfAbsent(modID, id -> new ModCore(id, id));
    }

    /**
     * Returns the instance of {@link ModCore} for the given mod id. Every mod id has a unique, single
     * instance. Calling this method multiple times with the same mod id is guaranteed to return
     * the same instance.
     *
     * @param modID     The mod id of the mod.
     * @param namespace The namespace of the mod. The namespace is used to create
     *                  {@link ResourceLocation}s in {@link #id(String)} and {@link #mk(String)}.
     * @return The instance of {@link ModCore} for the given mod id.
     */
    public static ModCore create(String modID, String namespace) {
        return cache.computeIfAbsent(modID, id -> new ModCore(id, namespace));
    }

    /**
     * Returns true if the game is currently running in a data generation environment.
     *
     * @return true if the game is currently running in a data generation environment.
     */
    public static boolean isDatagen() {
        return System.getProperty("fabric-api.datagen") != null;
    }

    /**
     * Returns true if the game is currently running in a development environment.
     *
     * @return true if the game is currently running in a development environment.
     */
    public static boolean isDevEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    /**
     * Returns true if the game is currently running on the client.
     *
     * @return true if the game is currently running on the client.
     */
    public static boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

}
