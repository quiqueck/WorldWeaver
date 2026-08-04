package de.ambertation.wover.tag.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.PackBuilder;
import de.ambertation.wover.datagen.api.WoverDataGenEntryPoint;
import de.ambertation.wover.entrypoint.LibWoverTag;
import de.ambertation.wover.tag.api.TagManager;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class WoverTagDatagen extends WoverDataGenEntryPoint {
    static final TagKey<Block> VILLAGER_JOB_SITES = TagManager.BLOCKS.makeWorldWeaverTag("villager_job_sites");

    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        globalPack.addProvider(BlockTagProvider::new);
        globalPack.addProvider(ItemTagProvider::new);
        globalPack.addProvider(BiomeTagProvider::new);
    }


    @Override
    protected ModCore modCore() {
        return LibWoverTag.C;
    }
}
