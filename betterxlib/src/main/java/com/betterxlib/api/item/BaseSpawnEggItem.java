package com.betterxlib.api.item;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.SpawnEggItem;

import java.util.function.Supplier;

/**
 * A base spawn egg item for custom entities.
 */
public class BaseSpawnEggItem extends SpawnEggItem {

    public BaseSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, int primaryColor, int secondaryColor, Properties properties) {
        super(type.get(), primaryColor, secondaryColor, properties);
    }

    /**
     * Create a spawn egg with default properties.
     *
     * @param type the entity type supplier
     * @param primaryColor the primary egg color
     * @param secondaryColor the secondary egg color
     * @return a new BaseSpawnEggItem
     */
    public static BaseSpawnEggItem create(Supplier<? extends EntityType<? extends Mob>> type, int primaryColor, int secondaryColor) {
        return new BaseSpawnEggItem(type, primaryColor, secondaryColor, new Properties());
    }

    /**
     * Create a spawn egg from RGB values.
     *
     * @param type the entity type supplier
     * @param primaryR primary red (0-255)
     * @param primaryG primary green (0-255)
     * @param primaryB primary blue (0-255)
     * @param secondaryR secondary red (0-255)
     * @param secondaryG secondary green (0-255)
     * @param secondaryB secondary blue (0-255)
     * @return a new BaseSpawnEggItem
     */
    public static BaseSpawnEggItem create(Supplier<? extends EntityType<? extends Mob>> type,
                                          int primaryR, int primaryG, int primaryB,
                                          int secondaryR, int secondaryG, int secondaryB) {
        int primary = (primaryR << 16) | (primaryG << 8) | primaryB;
        int secondary = (secondaryR << 16) | (secondaryG << 8) | secondaryB;
        return create(type, primary, secondary);
    }
}
