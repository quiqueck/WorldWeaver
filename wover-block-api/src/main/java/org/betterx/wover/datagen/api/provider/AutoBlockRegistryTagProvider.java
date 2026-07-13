package org.betterx.wover.datagen.api.provider;

import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverAutoProvider;
import org.betterx.wover.datagen.api.WoverTagProvider;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.world.level.block.Block;

import java.util.List;

/**
 * Creates item tags for all items that were registered with an
 * {@link org.betterx.wover.block.api.BlockRegistry} and had
 * some tags added to them.
 * <p>
 * This provider is automatically registered to the global datapack by {@link org.betterx.wover.datagen.api.WoverDataGenEntryPoint}.
 */
public class AutoBlockRegistryTagProvider extends WoverTagProvider.ForBlocks implements WoverAutoProvider {

    /**
     * Creates a new provider for the given mod. Tags from every namespace are written (no filtering).
     *
     * @param modCore The mod this provider generates block tags for
     */
    public AutoBlockRegistryTagProvider(ModCore modCore) {
        //do not filter any tags
        super(modCore, (List<String>) null);
    }

    /**
     * Adds the tags collected by every {@link BlockRegistry} to {@code context}.
     *
     * @param context The context to add the collected tags to
     */
    @Override
    public void prepareTags(TagBootstrapContext<Block> context) {
        BlockRegistry.streamAll().forEach(registry -> registry.bootstrapBlockTags(context));
    }
}
