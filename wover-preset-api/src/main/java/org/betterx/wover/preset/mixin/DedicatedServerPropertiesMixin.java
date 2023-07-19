package org.betterx.wover.preset.mixin;

import org.betterx.wover.config.api.Configs;
import org.betterx.wover.preset.impl.WorldPresetsManagerImpl;

import net.minecraft.server.dedicated.DedicatedServerProperties;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Properties;

@Mixin(DedicatedServerProperties.class)
public class DedicatedServerPropertiesMixin {
    //Make sure the default server properties use our Default World Preset by default (read from "level-type")
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/DedicatedServerProperties$WorldDimensionData;<init>(Lcom/google/gson/JsonObject;Ljava/lang/String;)V"))
    protected String wover_defaultPreset(String string) {
        if (Configs.MAIN.forceDefaultWorldPresetOnServer.get()) {
            return WorldPresetsManagerImpl.getDefault().location().toString();
        }
        return string;
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/Settings;<init>(Ljava/util/Properties;)V"))
    private static Properties wover_defaultPreset(Properties property) {
        //init default value level preset in server.properties
        property.setProperty(
                "level-type",
                property.getProperty("level-type", WorldPresetsManagerImpl.getDefault().location().toString())
        );
        return property;
    }
}
