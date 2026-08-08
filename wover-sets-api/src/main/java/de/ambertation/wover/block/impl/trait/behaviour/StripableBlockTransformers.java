package de.ambertation.wover.block.impl.trait.behaviour;

import de.ambertation.wover.block.api.trait.behaviour.StripableBlockTrait;

import net.minecraft.core.component.BlockTransformer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.BlockTransformerMappings;

import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;

import java.util.ArrayList;
import org.jetbrains.annotations.ApiStatus;

/**
 * Teaches every axe about the blocks registered with a {@link StripableBlockTrait}.
 * <p>
 * This replaces the {@code AxeItemMixin} that {@code wover-sets} carried up to 26.1. That mixin injected into
 * {@code AxeItem#getStripped} and answered "what does this block strip to?" from the trait. 26.3 removed
 * {@code AxeItem} entirely - tools are no longer {@link Item} subclasses - and moved stripping (along with
 * shovel path-making, hoe tilling, copper scraping and de-waxing) into a single data-driven
 * {@code minecraft:block_transformer} item component. {@link BlockTransformer#transformBlock} walks that
 * component's list of {@link BlockTransformer.BlockTransformData} entries and applies the first one whose
 * predicate matches the clicked block.
 * <p>
 * So the correct 26.3 answer is not "inject into stripping" but "put our blocks into the list an axe already
 * carries". That is what happens here, using Fabric's {@link DefaultItemComponentEvents#MODIFY}, which fires
 * from {@code DataComponentInitializers$PendingComponents#apply} - i.e. during a datapack reload, once every
 * block is registered and every delayed vanilla component has been resolved and bound. No mixin is needed, the
 * result is a plain serializable component, and it therefore also reaches the client (which needs it to predict
 * the interaction) and any other code that inspects the item.
 * <h2>Which items are modified</h2>
 * An item counts as an axe if its block transformer contains all of {@link BlockTransformerMappings#AXE}'s
 * entries. That is exactly the set of items built through {@link Item.Properties#axe} - vanilla's axes plus any
 * modded ones - and, because the test is "contains" rather than "equals", it keeps working when another mod has
 * already appended its own entries. Items with a deliberately different transformer (a mod that replaced the
 * axe mapping wholesale, or a hoe/shovel) are intentionally left alone.
 */
@ApiStatus.Internal
public final class StripableBlockTransformers {
    private StripableBlockTransformers() {
    }

    /**
     * Registers the listener. Called once from {@code LibWoverSets#onInitialize}.
     */
    public static void register() {
        DefaultItemComponentEvents.MODIFY.register(context -> {
            final var extraTransforms = StripableBlockBuilder.axeTransforms();
            // Nothing declared a StripableBlockTrait (this is also the case during datagen, where the log/bark
            // builders deliberately skip the runtime traits). Leave every item untouched rather than rebinding
            // components to an identical value.
            if (extraTransforms.isEmpty()) return;

            context.modify(
                    StripableBlockTransformers::isAxe,
                    (builder, lookup, item) -> {
                        final var current = item.components().get(DataComponents.BLOCK_TRANSFORMER);
                        if (current == null) return;

                        final var merged = new ArrayList<>(current.transforms());
                        merged.addAll(extraTransforms);
                        builder.set(DataComponents.BLOCK_TRANSFORMER, new BlockTransformer(merged));
                    }
            );
        });
    }

    private static boolean isAxe(Item item) {
        final var transformer = item.components().get(DataComponents.BLOCK_TRANSFORMER);
        return transformer != null
                && transformer.transforms().containsAll(BlockTransformerMappings.AXE.transforms());
    }
}
