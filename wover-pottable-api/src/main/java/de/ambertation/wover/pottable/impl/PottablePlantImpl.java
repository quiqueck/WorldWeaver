package de.ambertation.wover.pottable.impl;

import de.ambertation.wover.pottable.api.PottablePlant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class PottablePlantImpl extends PottablePlant {
    public static final Codec<PottablePlant> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    ResourceKey.codec(Registries.BLOCK).fieldOf("block").forGetter(o -> o.block),
                    TagKey.codec(Registries.BLOCK)
                          .optionalFieldOf("valid_soils")
                          .forGetter(o -> o.validSoils)
            )
            .apply(instance, PottablePlantImpl::new)
    );

    PottablePlantImpl(ResourceKey<Block> block, Optional<TagKey<Block>> validSoils) {
        super(block, validSoils);
    }
}
