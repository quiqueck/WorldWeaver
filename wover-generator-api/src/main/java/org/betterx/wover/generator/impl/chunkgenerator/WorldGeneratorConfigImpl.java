package org.betterx.wover.generator.impl.chunkgenerator;

import org.betterx.wover.core.impl.registry.ModCoreImpl;
import org.betterx.wover.state.api.WorldConfig;

import net.minecraft.nbt.CompoundTag;

public class WorldGeneratorConfigImpl {
    public static final String TAG_PRESET = "preset";
    public static final String TAG_GENERATOR = "generator";

    static CompoundTag getPresetsNbt() {
        return WorldConfig.getCompoundTag(ModCoreImpl.GLOBAL_MOD, TAG_PRESET);
    }
}
