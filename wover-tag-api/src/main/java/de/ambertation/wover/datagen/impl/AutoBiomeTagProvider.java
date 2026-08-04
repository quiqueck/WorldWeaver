package de.ambertation.wover.datagen.impl;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverAutoProvider;
import de.ambertation.wover.datagen.api.WoverDataProvider;
import de.ambertation.wover.datagen.api.WoverTagProvider;
import de.ambertation.wover.entrypoint.LibWoverTag;
import de.ambertation.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.data.DataProvider;
import net.minecraft.world.level.biome.Biome;

import java.util.LinkedList;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public class AutoBiomeTagProvider extends WoverTagProvider.ForBiomes implements WoverAutoProvider.WithRedirect {
    private final List<WoverTagProvider<Biome, TagBootstrapContext<Biome>>> redirects = new LinkedList<>();

    public AutoBiomeTagProvider(ModCore modCore) {
        super(modCore);
    }

    @Override
    public void prepareTags(TagBootstrapContext<Biome> provider) {
        redirects.forEach(redirect -> {
            LibWoverTag.C.LOG.debug(
                    "   {} includes {} for {}",
                    this.getClass().getSimpleName(),
                    redirect.getClass().getSimpleName(),
                    redirect.modCore.namespace
            );
            redirect.prepareTags(provider);
        });
    }

    @Override
    public @Nullable <T extends DataProvider> WoverDataProvider<T> redirect(@Nullable WoverDataProvider<T> provider) {
        if (provider instanceof WoverTagProvider<?, ?> tagProvider) {
            if (tagProvider.tagRegistry == this.tagRegistry && tagProvider.modCore.equals(this.modCore)) {
                LibWoverTag.C.LOG.debug("Redirecting {} to {}", tagProvider.getClass().getName(), this
                        .getClass()
                        .getName());
                this.mergeAllowedAndForced((WoverTagProvider<Biome, TagBootstrapContext<Biome>>) tagProvider);
                redirects.add((WoverTagProvider<Biome, TagBootstrapContext<Biome>>) tagProvider);
                return null;
            }
        }
        return provider;
    }
}
