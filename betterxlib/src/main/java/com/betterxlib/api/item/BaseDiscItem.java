package com.betterxlib.api.item;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

/**
 * A base music disc item.
 */
public class BaseDiscItem extends Item {

    private final Holder<SoundEvent> sound;
    private final int lengthInTicks;

    public BaseDiscItem(Properties properties, Holder<SoundEvent> sound, int lengthInSeconds) {
        super(properties);
        this.sound = sound;
        this.lengthInTicks = lengthInSeconds * 20;
    }

    /**
     * Create a music disc with default properties.
     *
     * @param sound the sound event to play
     * @param lengthInSeconds the length of the track in seconds
     * @return a new BaseDiscItem
     */
    public static BaseDiscItem create(Holder<SoundEvent> sound, int lengthInSeconds) {
        return new BaseDiscItem(
            new Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE),
            sound,
            lengthInSeconds
        );
    }

    /**
     * Create a music disc with custom comparator output.
     *
     * @param sound the sound event to play
     * @param lengthInSeconds the length of the track in seconds
     * @param comparatorOutput the comparator signal strength (1-15)
     * @return a new BaseDiscItem
     */
    public static BaseDiscItem create(Holder<SoundEvent> sound, int lengthInSeconds, int comparatorOutput) {
        return new BaseDiscItem(
            new Properties()
                .stacksTo(1)
                .rarity(Rarity.RARE),
            sound,
            lengthInSeconds
        );
    }

    /**
     * Get the sound event for this disc.
     *
     * @return the sound event holder
     */
    public Holder<SoundEvent> getSound() {
        return sound;
    }

    /**
     * Get the length of the track in ticks.
     *
     * @return the length in ticks
     */
    public int getLengthInTicks() {
        return lengthInTicks;
    }
}
