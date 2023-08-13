package org.betterx.wover.datagen.impl;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverTagProvider;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.world.level.biome.Biome;

public class AutoBiomeTagProvider extends WoverTagProvider.ForBiomes {
    public AutoBiomeTagProvider(ModCore modCore) {
        super(modCore);
    }

    @Override
    protected void prepareTags(TagBootstrapContext<Biome> provider) {

    }
}
