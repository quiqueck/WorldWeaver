package org.betterx.wover.structure.impl.builders;

import org.betterx.wover.structure.api.StructureKey;
import org.betterx.wover.structure.api.builders.BaseStructureBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public abstract class BaseStructureBuilderImpl<S extends Structure, R extends BaseStructureBuilder<S, R>> implements BaseStructureBuilder<S, R> {
    protected final StructureKey<S, R> key;

    protected final BootstapContext<Structure> context;

    @NotNull
    protected TerrainAdjustment terrainAdjustment;

    @NotNull
    protected final Map<MobCategory, StructureSpawnOverride> spawnOverrides = new HashMap<>();

    public BaseStructureBuilderImpl(StructureKey<S, R> key, BootstapContext<Structure> context) {
        this.key = key;
        this.context = context;

        terrainAdjustment = TerrainAdjustment.NONE;
    }

    @Override
    public R adjustment(TerrainAdjustment adjustment) {
        this.terrainAdjustment = adjustment;
        return (R) this;
    }

    @Override
    public Holder<Structure> register() {
        return context.register(key.key, build());
    }

    @Override
    public Holder<Structure> directHolder() {
        return Holder.direct(build());
    }

    protected abstract Structure build();

    protected Structure.StructureSettings buildSettings() {
        return new Structure.StructureSettings(
                context.lookup(Registries.BIOME).getOrThrow(key.getBiomeTag()),
                spawnOverrides,
                key.getDecoration(),
                terrainAdjustment
        );
    }
}
