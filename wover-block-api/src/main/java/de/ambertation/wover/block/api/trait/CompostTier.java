package de.ambertation.wover.block.api.trait;

/**
 * The composting rates a modded item can have.
 * <p>
 * On this branch a composting rate is still an arbitrary {@code float} that {@code ComposterBlock#addItem}
 * rolls against, so nothing here is technically forced. The set is finite because 26.3 makes it so: there,
 * vanilla removed {@code ComposterBlock.COMPOSTABLES} and an item is compostable iff it carries the
 * {@code minecraft:compostable} component, whose rate is a {@code ResourceKey} into the
 * {@code number_provider} registry rather than a number. A registry reference cannot express an arbitrary
 * float, so the rates that exist there are vanilla's five tiers plus a 10&nbsp;% one WoVer ships (vanilla's
 * floor is 30&nbsp;%, and modded plants have always composted at 10&nbsp;%).
 * <p>
 * Snapping here as well keeps the rates identical across branches, which is the whole point - a food that
 * composts 36&nbsp;% of the time on one version and 30&nbsp;% on the next is a bug report waiting to happen.
 * {@link #nearest(float)} is what callers get when they declare a chance.
 */
public enum CompostTier {
    /** 10&nbsp;%. Below vanilla's floor; 26.3 backs this with WoVer's own number provider. */
    VERY_LOW(0.1f),
    /** 30&nbsp;%, vanilla's {@code compostable/low} - leaves, saplings, most small plants. */
    LOW(0.3f),
    /** 50&nbsp;%, vanilla's {@code compostable/low_medium}. */
    LOW_MEDIUM(0.5f),
    /** 65&nbsp;%, vanilla's {@code compostable/medium}. */
    MEDIUM(0.65f),
    /** 85&nbsp;%, vanilla's {@code compostable/medium_high}. */
    MEDIUM_HIGH(0.85f),
    /** 100&nbsp;%, vanilla's {@code compostable/always_add_one} - cakes, pumpkin pie. */
    ALWAYS(1.0f);

    /** The chance this tier composts with, in {@code (0,1]}. */
    public final float chance;

    CompostTier(float chance) {
        this.chance = chance;
    }

    /**
     * The tier closest to {@code chance}. A chance at or above {@code 1.0} is {@link #ALWAYS} - the roll is
     * {@code random.nextDouble() < chance}, which always succeeds there, so nothing is lost.
     *
     * @param chance a requested composting chance
     * @return the nearest tier, never {@code null}
     */
    public static CompostTier nearest(float chance) {
        CompostTier best = VERY_LOW;
        float bestDistance = Float.MAX_VALUE;
        for (CompostTier tier : values()) {
            final float distance = Math.abs(tier.chance - chance);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = tier;
            }
        }
        return best;
    }
}
