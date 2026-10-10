package de.ambertation.wover.block.api.trait;

import de.ambertation.wover.entrypoint.LibWoverBlock;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.component.Compostable;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

/**
 * The composting rates a modded item can have.
 * <p>
 * Up to 26.1 compostability was an arbitrary {@code float} chance, rolled in Java by
 * {@code ComposterBlock#addItem}. 26.3 removed that: an item is compostable iff it carries the
 * {@code minecraft:compostable} component, whose {@code layers} is a {@link ResourceKey} into the reloadable
 * {@code minecraft:context_int_provider} datapack registry (called {@code minecraft:number_provider} in the 26.3 snapshots). The referenced provider decides how many layers an
 * insert adds, so the rate is no longer a number the game reads off an item - it is a data file.
 * <p>
 * A registry reference cannot express an arbitrary float, so the set of rates is necessarily finite. These are
 * vanilla's own five tiers plus {@link #VERY_LOW}, a 10&nbsp;% tier WoVer ships because vanilla's lowest is
 * 30&nbsp;% and modded plants have always composted at 10&nbsp;%. {@link #nearest(float)} maps a legacy chance
 * onto the closest one, which is what lets callers keep declaring a chance.
 * <p>
 * Every tier's provider - including {@link #VERY_LOW}, see
 * {@code data/wover/context_int_provider/compostable/very_low.json} - reproduces the rule that the first item into
 * an <em>empty</em> composter always fills a layer. Vanilla encodes it as a {@code number_dispatcher} case on
 * {@code composter[level=0]}; before 26.3 it was the {@code fillLevel != 0} branch of {@code addItem}.
 */
public enum CompostTier {
    /** 10&nbsp;%. Below vanilla's floor, so WoVer ships the provider for it. */
    VERY_LOW(0.1f, ResourceKey.create(
            Registries.CONTEXT_INT_PROVIDER,
            LibWoverBlock.C.id("compostable/very_low")
    )),
    /** 30&nbsp;%, vanilla's {@code compostable/low} - leaves, saplings, most small plants. */
    LOW(0.3f, ContextIntProviders.COMPOSTABLE_LOW),
    /** 50&nbsp;%, vanilla's {@code compostable/low_medium}. */
    LOW_MEDIUM(0.5f, ContextIntProviders.COMPOSTABLE_LOW_MEDIUM),
    /** 65&nbsp;%, vanilla's {@code compostable/medium}. */
    MEDIUM(0.65f, ContextIntProviders.COMPOSTABLE_MEDIUM),
    /** 85&nbsp;%, vanilla's {@code compostable/medium_high}. */
    MEDIUM_HIGH(0.85f, ContextIntProviders.COMPOSTABLE_MEDIUM_HIGH),
    /** 100&nbsp;%, vanilla's {@code compostable/always_add_one} - cakes, pumpkin pie. */
    ALWAYS(1.0f, ContextIntProviders.COMPOSTABLE_ALWAYS_ADD_ONE);

    /** The chance this tier composts with, in {@code (0,1]}. */
    public final float chance;
    /** The {@code context_int_provider} this tier's {@code minecraft:compostable} component points at. */
    public final ResourceKey<ContextIntProvider> layers;

    CompostTier(float chance, ResourceKey<ContextIntProvider> layers) {
        this.chance = chance;
        this.layers = layers;
    }

    /**
     * The tier closest to {@code chance}. A chance at or above {@code 1.0} is {@link #ALWAYS} - the legacy roll
     * was {@code random.nextDouble() < chance}, which always succeeded there, so nothing is lost.
     *
     * @param chance a legacy composting chance
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

    /**
     * @return the {@code minecraft:compostable} component an item of this tier carries
     */
    public Compostable asCompostableComponent() {
        return new Compostable(layers);
    }
}
