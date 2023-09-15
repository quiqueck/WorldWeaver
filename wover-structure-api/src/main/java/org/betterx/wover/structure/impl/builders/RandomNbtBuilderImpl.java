package org.betterx.wover.structure.impl.builders;

import org.betterx.wover.structure.api.StructureKey;
import org.betterx.wover.structure.api.builders.RandomNbtBuilder;
import org.betterx.wover.structure.api.structures.StructurePlacement;
import org.betterx.wover.structure.api.structures.nbt.RandomNbtStructure;
import org.betterx.wover.structure.api.structures.nbt.RandomNbtStructureElement;
import org.betterx.wover.util.RandomizedWeightedList;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;

public class RandomNbtBuilderImpl
        extends BaseStructureBuilderImpl<RandomNbtStructure, RandomNbtBuilder, StructureKey.RandomNbt>
        implements RandomNbtBuilder {
    private StructurePlacement placement;
    private final RandomizedWeightedList<RandomNbtStructureElement> elements;
    private boolean keepAir;

    public RandomNbtBuilderImpl(
            StructureKey.RandomNbt key,
            BootstapContext<Structure> context
    ) {
        super(key, context);

        this.placement = StructurePlacement.SURFACE;
        this.elements = new RandomizedWeightedList<>();
        this.keepAir = false;
    }

    @Override
    public RandomNbtBuilder keepAir(boolean value) {
        this.keepAir = value;
        return this;
    }

    @Override
    public RandomNbtBuilder placement(StructurePlacement value) {
        this.placement = value;
        return this;
    }

    @Override
    public RandomNbtBuilder addElement(ResourceLocation elementId, int yOffset, double weight) {
        elements.add(new RandomNbtStructureElement(elementId, yOffset), weight);
        return this;
    }

    @Override
    protected Structure build() {
        return new RandomNbtStructure(buildSettings(), placement, keepAir, elements);
    }
}
