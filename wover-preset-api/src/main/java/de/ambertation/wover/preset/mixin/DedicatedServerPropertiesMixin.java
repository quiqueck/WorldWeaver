package de.ambertation.wover.preset.mixin;

import de.ambertation.wover.preset.impl.WorldPresetsManagerImpl;

import net.minecraft.server.dedicated.DedicatedServerProperties;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Properties;

@Mixin(DedicatedServerProperties.class)
public class DedicatedServerPropertiesMixin {
    /**
     * Makes a freshly generated {@code server.properties} come up on our default World Preset, without ever
     * overruling one that is already configured.
     * <p>
     * This runs as the {@code super(properties)} call, before vanilla reads {@code level-type} (whose own
     * fallback is {@code minecraft:normal}). Seeding the property here means a new file is written out with
     * our preset in it, and every later boot then reads it back like any other configured value - so "we
     * chose this for you once" and "the admin chose this" converge on the same, visible mechanism.
     * <p>
     * An existing value is never touched: whatever {@code level-type} says - ours, vanilla's or another
     * mod's - is what the server generates with. Note that a datapack shipping its own
     * {@code dimension/*.json} still overrides the terrain settings on top of that; see
     * {@link de.ambertation.wover.generator.impl.chunkgenerator.BiomeRepairHelper}.
     */
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/Settings;<init>(Ljava/util/Properties;)V"))
    private static Properties wover_defaultPreset(Properties property) {
        property.setProperty(
                "level-type",
                property.getProperty("level-type", WorldPresetsManagerImpl.getDefault().location().toString())
        );
        return property;
    }
}
