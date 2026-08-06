package de.ambertation.wover.item.api.model;

import de.ambertation.wover.entrypoint.LibWoverItem;
import de.ambertation.wover.item.api.trait.ItemTraitKey;

import net.minecraft.world.item.Item;

import java.util.function.Supplier;

/**
 * The catalogue of built-in {@link ItemModelKey}s. Each key identifies a client-side item model factory that
 * {@code wover}'s client source set registers against it (see the {@code wover.client.traits} entrypoint).
 * Payloads use only common-safe values.
 */
public class ItemModelKeys {
    private static <P> ItemModelKey<P> key(String path) {
        return new ItemModelKey<>(ItemTraitKey.ofUnique(LibWoverItem.C, "model/" + path));
    }

    /** A plain flat item model using the item's own texture. */
    public static final ItemModelKey<Void> FLAT_ITEM = key("flat_item");
    /** A plain flat item model reusing another item's texture as the model's layer. */
    public static final ItemModelKey<Supplier<Item>> FLAT_ITEM_FROM = key("flat_item_from");
    /** A vanilla-style elytra item model (dispatching normal/broken by durability). */
    public static final ItemModelKey<Void> ELYTRA = key("elytra");
    /** A plain flat item model for a boat/chest-boat. */
    public static final ItemModelKey<Void> BOAT = key("boat");
    /**
     * A vanilla-style spear item model: the flat icon for {@code gui}/{@code ground}/{@code fixed}/
     * {@code on_shelf}, dispatching to a separate {@code spear_in_hand}-parented model (with its own
     * {@code _in_hand} texture) for every other display context (hand/head rendering).
     */
    public static final ItemModelKey<Void> SPEAR = key("spear");

    private ItemModelKeys() {
    }
}
