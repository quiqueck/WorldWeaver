package org.betterx.wover.poi.api;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

/**
 * Mixin-injected interface implemented by every vanilla {@link net.minecraft.world.entity.ai.village.poi.PoiType},
 * letting WoVer associate a {@link TagKey} of blocks with a POI type so datapacks can extend which block
 * states count as that POI (see {@link org.betterx.wover.poi.impl.PoiManagerImpl}).
 */
public interface PoiTypeExtension {
    /**
     * Associates a block tag with this POI type. Blocks in the tag are added to
     * {@code PoiTypes.TYPE_BY_STATE} the next time POI states are refreshed.
     *
     * @param tag The block tag to associate, or {@code null} to clear the association
     */
    void wover_setTag(TagKey<Block> tag);

    /**
     * Gets the block tag associated with this POI type.
     *
     * @return The associated block tag, or {@code null} if none was set
     */
    TagKey<Block> wover_getTag();
}
