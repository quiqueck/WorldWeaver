package org.betterx.wover.state.impl;

import de.ambertation.wunderlib.configs.AbstractConfig;
import de.ambertation.wunderlib.utils.Version;

import net.minecraft.nbt.CompoundTag;

import com.google.gson.JsonObject;

import org.jetbrains.annotations.Nullable;

public class WorldConfigResource extends AbstractConfig<WorldConfigResource> {
    public final JsonObject root;

    WorldConfigResource(Version.ModVersionProvider versionProvider, CompoundTag root) {
        super(versionProvider, "world_config");
        this.root = root;
    }

    @Override
    protected @Nullable JsonObject loadRootElement() {
        return null;
    }

    @Override
    protected boolean saveRootElement(String root) {
        return false;
    }

    @Override
    protected boolean isReadOnly() {
        return false;
    }
}
