package de.ambertation.wover.structure.impl.builders;

import de.ambertation.wover.structure.api.StructureKey;
import de.ambertation.wover.structure.api.builders.RandomNbtBuilder;
import de.ambertation.wover.structure.api.structures.StructurePlacement;
import de.ambertation.wover.structure.api.structures.nbt.RandomNbtStructure;
import de.ambertation.wover.structure.api.structures.nbt.RandomNbtStructureElement;
import de.ambertation.wover.util.RandomizedWeightedList;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.Structure;

public class RandomNbtBuilderImpl
        extends BaseStructureBuilderImpl<RandomNbtStructure, RandomNbtBuilder, StructureKey.RandomNbt>
        implements RandomNbtBuilder {
    private StructurePlacement placement;
    private final RandomizedWeightedList<RandomNbtStructureElement> elements;
    private boolean keepAir;

    public RandomNbtBuilderImpl(
            StructureKey.RandomNbt key,
            BootstrapContext<Structure> context
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
    public RandomNbtBuilder addElement(Identifier elementId, int yOffset, double weight) {
        elements.add(new RandomNbtStructureElement(elementId, yOffset), weight);
        return this;
    }

    @Override
    protected Structure build() {
        return new RandomNbtStructure(buildSettings(), placement, keepAir, elements);
    }
}
