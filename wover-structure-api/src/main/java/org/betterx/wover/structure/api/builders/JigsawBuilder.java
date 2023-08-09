package org.betterx.wover.structure.api.builders;

import org.betterx.wover.structure.api.pools.StructurePoolKey;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

public interface JigsawBuilder extends BaseStructureBuilder<JigsawStructure, JigsawBuilder> {
    JigsawBuilder projectStartToHeightmap(Heightmap.Types value);

    JigsawBuilder maxDistanceFromCenter(int value);

    JigsawBuilder startJigsawName(ResourceLocation value);

    JigsawBuilder useExpansionHack(boolean value);

    JigsawBuilder maxDepth(int value);

    JigsawBuilder startHeight(HeightProvider value);

    JigsawBuilder startPool(Holder<StructureTemplatePool> pool);

    JigsawBuilder startPool(ResourceKey<StructureTemplatePool> pool);

    JigsawBuilder startPool(StructurePoolKey pool);
}
