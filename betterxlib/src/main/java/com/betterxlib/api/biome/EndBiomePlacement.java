package com.betterxlib.api.biome;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

/**
 * Defines where in the End dimension a biome should appear.
 */
public enum EndBiomePlacement implements StringRepresentable {
    /**
     * The central End island where the dragon fight occurs.
     * Only use for the main End biome replacement.
     */
    END_CENTER("end_center"),

    /**
     * The highlands on the main island and surrounding ring.
     */
    END_HIGHLANDS("end_highlands"),

    /**
     * The midlands between highlands and barrens.
     */
    END_MIDLANDS("end_midlands"),

    /**
     * The barren outer islands.
     */
    END_BARRENS("end_barrens"),

    /**
     * Small End islands floating in the void.
     */
    SMALL_END_ISLANDS("small_end_islands"),

    /**
     * Generic land placement - will appear in highlands and midlands.
     */
    END_LAND("end_land"),

    /**
     * Void biomes that appear in low-terrain areas.
     */
    END_VOID("end_void");

    public static final Codec<EndBiomePlacement> CODEC = StringRepresentable.fromEnum(EndBiomePlacement::values);

    private final String name;

    EndBiomePlacement(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    /**
     * Check if this placement is valid for the main End island.
     *
     * @return true if this is a central island placement
     */
    public boolean isMainIsland() {
        return this == END_CENTER || this == END_HIGHLANDS;
    }

    /**
     * Check if this placement is for outer End islands.
     *
     * @return true if this is an outer island placement
     */
    public boolean isOuterIsland() {
        return this == END_MIDLANDS || this == END_BARRENS || this == SMALL_END_ISLANDS;
    }
}
