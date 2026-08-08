package de.ambertation.wover.item.api.model;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverItem;
import de.ambertation.wover.item.api.trait.ItemTrait;
import de.ambertation.wover.item.api.trait.ItemTraitKey;
import de.ambertation.wover.item.impl.trait.ItemTraitImpl;

import net.minecraft.world.item.Item;

/**
 * The common, client-free runtime trait that attaches an item model to an item. Instead of storing a client
 * model factory lambda, it stores only an {@link ItemModelKey} plus its common-safe payload; the client source
 * set resolves the key while walking items at datagen time (see {@code ClientItemModelRegistry}).
 * <p>
 * Bindings share the {@link #MODEL_TRAIT_KEY} identity with the client-only escape-hatch trait
 * ({@code ClientItemTraits.MODEL}), so a single datagen walk finds both.
 */
public final class ItemModelBinding extends ItemTraitImpl<Item, ItemModelBinding> implements ItemTrait<Item, ItemModelBinding> {
    /**
     * The shared trait key under which every item model trait (data-driven {@link ItemModelBinding} as well as
     * the client-only escape-hatch trait) is attached.
     */
    public static final ItemTraitKey MODEL_TRAIT_KEY = ItemTraitKey.ofUnique(LibWoverItem.C, "model");

    private final ItemModelKey<?> modelKey;
    private final Object payload;

    private ItemModelBinding(ItemModelKey<?> modelKey, Object payload) {
        this.modelKey = modelKey;
        this.payload = payload;
    }

    /**
     * Creates a binding pairing an item model key with its payload.
     *
     * @param modelKey the item model shape to generate
     * @param payload  the common-safe payload consumed by the shape's client factory
     * @param <P>      the payload type
     * @return the new binding
     */
    public static <P> ItemModelBinding of(ItemModelKey<P> modelKey, P payload) {
        if (!ModCore.isDatagen() || !ModCore.isClient()) return null;
        return new ItemModelBinding(modelKey, payload);
    }

    /**
     * @return the item model shape this binding requests
     */
    public ItemModelKey<?> modelKey() {
        return modelKey;
    }

    /**
     * @return the common-safe payload for the item model shape
     */
    public Object payload() {
        return payload;
    }

    @Override
    public ItemTraitKey key() {
        return MODEL_TRAIT_KEY;
    }

    @Override
    public ItemModelBinding forRuntime() {
        return this;
    }
}
