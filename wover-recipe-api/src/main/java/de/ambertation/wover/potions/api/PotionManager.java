package de.ambertation.wover.potions.api;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.events.api.Event;
import de.ambertation.wover.potions.impl.PotionManagerImpl;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;

/**
 * Helpers for registering {@link Potion}s and for hooking into vanilla's brewing-stand recipe bootstrap.
 */
public class PotionManager {
    /**
     * Fired while vanilla builds its {@link net.minecraft.world.item.alchemy.PotionBrewing} instance. Subscribe
     * to add custom potion mixes, container conversions, or brewing ingredients.
     */
    public static final Event<OnBootstrapPotions> BOOTSTRAP_POTIONS =
            PotionManagerImpl.BOOTSTRAP_POTIONS;

    /**
     * Registers a new potion whose id is derived from the mod's namespace and the given name, wrapping a
     * single {@link MobEffectInstance} of the given effect and duration. The translation key registered by
     * vanilla is {@code <namespace>_<name>}.
     *
     * @param modCore  The mod the potion is registered under.
     * @param name     The potion's path (used both for the id and, prefixed with the mod's namespace, as the
     *                 translation key).
     * @param effect   The mob effect the potion applies.
     * @param duration The effect duration, in ticks.
     * @return The holder for the newly registered potion.
     */
    public static Holder<Potion> registerPotion(ModCore modCore, String name, Holder<MobEffect> effect, int duration) {
        return registerPotion(modCore.id(name), new Potion(modCore.namespace + "_" + name, new MobEffectInstance(effect, duration)));
    }

    /**
     * Registers a fully constructed {@link Potion} under the given id.
     *
     * @param id     The potion's registry id.
     * @param potion The potion to register.
     * @return The holder for the newly registered potion.
     */
    public static Holder<Potion> registerPotion(Identifier id, Potion potion) {
        return Registry.registerForHolder(BuiltInRegistries.POTION, id, potion);
    }

    private PotionManager() {
    }
}
