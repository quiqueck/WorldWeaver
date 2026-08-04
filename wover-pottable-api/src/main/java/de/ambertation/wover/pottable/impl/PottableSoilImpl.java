package de.ambertation.wover.pottable.impl;

import de.ambertation.wover.pottable.api.PottableSoil;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

public class PottableSoilImpl extends PottableSoil {
    public static final Codec<PottableSoil> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    ResourceKey.codec(Registries.BLOCK).fieldOf("block").forGetter(o -> o.block)
            )
            .apply(instance, PottableSoilImpl::new)
    );

    PottableSoilImpl(ResourceKey<Block> block) {
        super(block);
    }
}
