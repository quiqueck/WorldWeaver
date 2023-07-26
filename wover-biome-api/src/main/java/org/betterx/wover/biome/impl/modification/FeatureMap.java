package org.betterx.wover.biome.impl.modification;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FeatureMap extends ArrayList<LinkedList<Holder<PlacedFeature>>> {
    public static final Codec<List<List<Holder<PlacedFeature>>>> CODEC = PlacedFeature.CODEC.listOf().listOf();

    public List<Holder<PlacedFeature>> getFeatures(GenerationStep.Decoration decoration) {
        final int index = decoration.ordinal();

        while (this.size() <= index) {
            this.add(new LinkedList<>());
        }

        return this.get(index);
    }

    public static HolderSet<PlacedFeature> getFeatures(
            List<HolderSet<PlacedFeature>> features,
            GenerationStep.Decoration decoration
    ) {
        final int index = decoration.ordinal();

        while (features.size() <= index) {
            features.add(HolderSet.direct(Collections.emptyList()));
        }

        return features.get(index);
    }

    public List<List<Holder<PlacedFeature>>> generic() {
        @SuppressWarnings("unchecked") final List<List<Holder<PlacedFeature>>> res = (List<List<Holder<PlacedFeature>>>) (List<?>) this;
        return res;
    }

    public static FeatureMap of(List<List<Holder<PlacedFeature>>> features) {
        FeatureMap map = new FeatureMap();
        for (final List<Holder<PlacedFeature>> list : features) {
            if (list instanceof LinkedList lList) {
                map.add(lList);
            } else {
                final LinkedList<Holder<PlacedFeature>> nList = new LinkedList<>(list);
                map.add(nList);
            }
        }
        return map;
    }
}
