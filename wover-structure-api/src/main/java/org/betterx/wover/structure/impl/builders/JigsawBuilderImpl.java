package org.betterx.wover.structure.impl.builders;

import org.betterx.wover.structure.api.StructureKey;
import org.betterx.wover.structure.api.builders.JigsawBuilder;
import org.betterx.wover.structure.api.pools.StructurePoolKey;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

import java.util.Optional;

public class JigsawBuilderImpl extends BaseStructureBuilderImpl<JigsawStructure, JigsawBuilder> implements JigsawBuilder {
    private Holder<StructureTemplatePool> startPool;
    private Optional<ResourceLocation> startJigsawName;
    private int maxDepth;
    private HeightProvider startHeight;
    private boolean useExpansionHack;
    private Optional<Heightmap.Types> projectStartToHeightmap;
    private int maxDistanceFromCenter;

    public JigsawBuilderImpl(
            StructureKey<JigsawStructure, JigsawBuilder> key,
            BootstapContext<Structure> context
    ) {
        super(key, context);

        this.maxDepth = 6;
        this.startHeight = ConstantHeight.of(VerticalAnchor.absolute(0));
        this.maxDistanceFromCenter = 80;
        this.useExpansionHack = false;
        this.startJigsawName = Optional.empty();
        this.projectStartToHeightmap = Optional.empty();
    }

    @Override
    public JigsawBuilder projectStartToHeightmap(Heightmap.Types value) {
        this.projectStartToHeightmap = Optional.of(value);
        return this;
    }

    @Override
    public JigsawBuilder maxDistanceFromCenter(int value) {
        this.maxDistanceFromCenter = value;
        return this;
    }

    @Override
    public JigsawBuilder startJigsawName(ResourceLocation value) {
        this.startJigsawName = Optional.of(value);
        return this;
    }

    @Override
    public JigsawBuilder useExpansionHack(boolean value) {
        this.useExpansionHack = value;
        return this;
    }

    @Override
    public JigsawBuilder maxDepth(int value) {
        this.maxDepth = value;
        return this;
    }

    @Override
    public JigsawBuilder startHeight(HeightProvider value) {
        this.startHeight = value;
        return this;
    }

    @Override
    public JigsawBuilder startPool(Holder<StructureTemplatePool> pool) {
        this.startPool = pool;
        return this;
    }

    @Override
    public JigsawBuilder startPool(ResourceKey<StructureTemplatePool> pool) {
        this.startPool = context.lookup(Registries.TEMPLATE_POOL).getOrThrow(pool);
        return this;
    }

    @Override
    public JigsawBuilder startPool(StructurePoolKey pool) {
        return startPool(pool.key);
    }

    @Override
    protected Structure build() {
        if (startPool == null) {
            throw new IllegalStateException("Start pool must be set for " + key.key.location());
        }
        return new JigsawStructure(
                buildSettings(),
                startPool,
                startJigsawName,
                maxDepth,
                startHeight,
                useExpansionHack,
                projectStartToHeightmap,
                maxDistanceFromCenter
        );
    }


}
