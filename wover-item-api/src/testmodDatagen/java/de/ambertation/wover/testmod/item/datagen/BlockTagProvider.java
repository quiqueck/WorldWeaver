package de.ambertation.wover.testmod.item.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverTagProvider;
import de.ambertation.wover.tag.api.event.context.TagBootstrapContext;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class BlockTagProvider extends WoverTagProvider.ForBlocks {
    public BlockTagProvider(ModCore modCore) {
        super(modCore, List.of(modCore.namespace, modCore.modId, "c"));
    }

    @Override
    public void prepareTags(TagBootstrapContext<Block> context) {
        context.add(CommonBlockTags.IS_OBSIDIAN, Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN);
    }
}
