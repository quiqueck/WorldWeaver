package org.betterx.wover.state.impl;

import de.ambertation.wunderlib.utils.Version;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.core.impl.registry.ModCoreImpl;
import org.betterx.wover.entrypoint.WoverEvents;
import org.betterx.wover.events.api.Event;
import org.betterx.wover.events.api.WorldLifecycle;
import org.betterx.wover.events.api.types.OnWorldConfig;
import org.betterx.wover.events.impl.EventImpl;

import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus;

/**
 * Mod-specific data-storage for a world.
 * <p>
 * This class provides the ability for mod to store persistent data inside a world.
 */
public class WorldConfigImpl {
    private static final Map<ModCore, CompoundTag> TAGS = Maps.newHashMap();
    private static final List<ModCore> MODS = Lists.newArrayList();
    private static final Map<ModCore, EventImpl<OnWorldConfig>> EVENTS = Maps.newHashMap();

    private static final String TAG_CREATED = "create_version";
    private static final String TAG_MODIFIED = "modify_version";
    private static File dataDir;

    @ApiStatus.Internal
    public static void initialize() {
        WorldLifecycle.WORLD_FOLDER_READY.subscribe(WorldConfigImpl::loadForWorld);
        registerMod(ModCoreImpl.GLOBAL_MOD);
    }

    private static void loadForWorld(LevelStorageSource.LevelStorageAccess levelStorageAccess) {
        dataDir = levelStorageAccess.getLevelPath(LevelResource.ROOT).resolve("data").toFile();

        MODS.stream()
            .parallel()
            .forEach(modCore -> {
                File file = new File(dataDir, modCore.modId + ".nbt");
                if (file.exists()) {
                    try {
                        CompoundTag root = NbtIo.readCompressed(file);
                        TAGS.put(modCore, root);
                    } catch (IOException e) {
                        WoverEvents.C.log.error("World data loading failed", e);
                    }
                }
            });

        TAGS.forEach((modCore, root) -> {
            EVENTS.computeIfPresent(modCore, (key, event) -> {
                event.emit(subscriber -> subscriber.config(modCore, root, OnWorldConfig.State.LOADED));
                return event;
            });
        });
    }

    private static void setupNewConfig(ModCore modCore, CompoundTag root) {
        root.putString(TAG_CREATED, modCore.getModVersion().toString());
        root.putString(TAG_MODIFIED, modCore.getModVersion().toString());

        EVENTS.computeIfPresent(modCore, (key, event) -> {
            event.emit(subscriber -> subscriber.config(modCore, root, OnWorldConfig.State.CREATED));
            return event;
        });
    }

    public static void registerMod(ModCore modCore) {
        if (!MODS.contains(modCore))
            MODS.add(modCore);
    }

    public static Event<OnWorldConfig> event(ModCore modCore) {
        return EVENTS.computeIfAbsent(
                modCore,
                key -> new EventImpl<>("WORLD_CONFIG_READY (" + key.modId + ")")
        );
    }

    public static CompoundTag getRootTag(ModCore modCore) {
        CompoundTag root = TAGS.get(modCore);
        if (root == null) {
            root = new CompoundTag();
            TAGS.put(modCore, root);
            setupNewConfig(modCore, root);
        }
        return root;
    }

    public static boolean hasMod(ModCore modCore) {
        return MODS.contains(modCore);
    }

    public static CompoundTag getCompoundTag(ModCore modCore, String path) {
        String[] parts = path.split("\\.");
        CompoundTag tag = getRootTag(modCore);
        for (String part : parts) {
            if (tag.contains(part)) {
                tag = tag.getCompound(part);
            } else {
                CompoundTag t = new CompoundTag();
                tag.put(part, t);
                tag = t;
            }
        }
        return tag;
    }

    public static void saveFile(ModCore modCore) {
        if (!hasMod(modCore)) {
            WoverEvents.C.log.error("Mod " + modCore.modId + " is not registered for a worldconfig file");
            return;
        }

        try {
            if (dataDir != null && !dataDir.exists()) {
                dataDir.mkdirs();
            }
            CompoundTag tag = getRootTag(modCore);
            tag.putString(TAG_MODIFIED, modCore.getModVersion().toString());

            final File tempFile = new File(dataDir, modCore.modId + "_temp.nbt");
            NbtIo.writeCompressed(tag, tempFile);

            final File oldFile = new File(dataDir, modCore.modId + "_old.nbt");
            final File dataFile = new File(dataDir, modCore.modId + ".nbt");
            Util.safeReplaceFile(dataFile, tempFile, oldFile);
        } catch (IOException e) {
            WoverEvents.C.log.error("World data saving failed", e);
        }
    }

    /**
     * Get the version of the last modification
     *
     * @param modCore The Mod
     * @return The Version object
     */
    public static Version getModifiedVersion(ModCore modCore) {
        return new Version(getRootTag(modCore).getString(TAG_MODIFIED));
    }

    /**
     * Get the version of the original version that create dthis file
     *
     * @param modCore The Mod
     * @return The Version object
     */
    public static Version getCreatedVersion(ModCore modCore) {
        return new Version(getRootTag(modCore).getString(TAG_CREATED));
    }
}

