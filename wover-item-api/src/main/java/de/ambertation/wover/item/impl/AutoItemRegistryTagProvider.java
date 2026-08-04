package de.ambertation.wover.item.impl;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverAutoProvider;
import de.ambertation.wover.datagen.api.WoverTagProvider;
import de.ambertation.wover.item.api.ItemRegistry;
import de.ambertation.wover.tag.api.event.context.ItemTagBootstrapContext;

import java.util.List;

/**
 * Creates item tags for all items that were registered with an
 * {@link de.ambertation.wover.item.api.ItemRegistry} and had
 * some tags added to them.
 * <p>
 * This provider is automatically registered to the global datapack by {@link de.ambertation.wover.datagen.api.WoverDataGenEntryPoint}.
 */
public class AutoItemRegistryTagProvider extends WoverTagProvider.ForItems implements WoverAutoProvider {

    public AutoItemRegistryTagProvider(ModCore modCore) {
        //do not filter any tags
        super(modCore, (List<String>) null);
    }

    @Override
    public void prepareTags(ItemTagBootstrapContext context) {
        ItemRegistry.streamAll().forEach(registry -> registry.bootstrapItemTags(context));
    }
}
