package org.betterx.wover.datagen.impl;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverTagProvider;
import org.betterx.wover.tag.api.BlockTagDataProvider;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

import java.util.List;

/**
 * Creates block tags for all blocks that implement {@link BlockTagDataProvider}
 * and are registered in the namespace of this mod.
 * <p>
 * This provider is automatically registered to the global datapack by {@link org.betterx.wover.datagen.api.WoverDataGenEntryPoint}.
 */
public class AutoBlockTagProvider extends WoverTagProvider.ForBlocks {
    public AutoBlockTagProvider(
            ModCore modCore
    ) {
        super(List.of(modCore.namespace, modCore.modId));
    }

    @Override
    protected void prepareTags(TagBootstrapContext<Block> provider) {
        BuiltInRegistries.BLOCK
                .entrySet()
                .stream()
                .filter(entry -> modIDs.contains(entry.getKey().location().getNamespace()))
                .forEach(entry -> {
                    if (entry.getValue() instanceof BlockTagDataProvider tagProvider) {
                        tagProvider.addBlockTags(provider);
                    }
                });
    }
}