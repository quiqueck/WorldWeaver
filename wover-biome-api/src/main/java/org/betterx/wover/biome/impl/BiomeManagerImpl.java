package org.betterx.wover.biome.impl;

import org.betterx.wover.biome.api.BiomeKey;
import org.betterx.wover.biome.api.builder.BiomeBuilder;
import org.betterx.wover.biome.api.builder.event.OnBootstrapBiomes;
import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.impl.data.BiomeDataRegistryImpl;
import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.entrypoint.WoverBiome;
import org.betterx.wover.events.api.Event;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;
import org.betterx.wover.events.impl.EventImpl;
import org.betterx.wover.surface.api.AssignedSurfaceRule;
import org.betterx.wover.surface.api.SurfaceRuleRegistry;
import org.betterx.wover.tag.api.TagManager;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class BiomeManagerImpl {
    public static final EventImpl<OnBootstrapRegistry<Biome>> BOOTSTRAP_BIOME_REGISTRY
            = new EventImpl<>("BOOTSTRAP_BIOME_REGISTRY");
    public static final EventImpl<OnBootstrapBiomes> BOOTSTRAP_BIOMES_WITH_DATA
            = new EventImpl<>("BOOTSTRAP_BIOMES_WITH_DATA");

    private static void onBootstrap(BootstapContext<Biome> ctx) {
        BOOTSTRAP_BIOME_REGISTRY.emit(c -> c.bootstrap(ctx));
    }

    @ApiStatus.Internal
    public static void initialize() {
        DatapackRegistryBuilder.addBootstrap(
                Registries.BIOME,
                BiomeManagerImpl::onBootstrap
        );

        BOOTSTRAP_BIOME_REGISTRY.subscribe(
                BiomeManagerImpl::onBootstrapBiomeRegistry,
                Event.DEFAULT_PRIORITY / 2
        );

        BiomeDataRegistryImpl.BOOTSTRAP_BIOME_DATA_REGISTRY.subscribe(
                BiomeManagerImpl::onBootstrapBiomeDataRegistry,
                Event.DEFAULT_PRIORITY / 2
        );

        SurfaceRuleRegistry.BOOTSTRAP_SURFACE_RULE_REGISTRY.subscribe(
                BiomeManagerImpl::onBootstrapSurfaceRuleRegistry,
                Event.DEFAULT_PRIORITY / 2
        );

        TagManager.BIOMES.bootstrapEvent().subscribe(
                BiomeManagerImpl::onBootstrapTags,
                Event.DEFAULT_PRIORITY / 2
        );
    }


    private static HolderGetter<Biome> lastBiomeGetter = null;
    private static BiomeBootstrapContextImpl bootstrapContext = null;

    private static <T> BiomeBootstrapContextImpl initContext(BootstapContext<T> lookupContext) {
        if (lookupContext == null) return bootstrapContext;

        final HolderGetter<Biome> biomeGetter = lookupContext.lookup(Registries.BIOME);
        if (biomeGetter != lastBiomeGetter) {
            lastBiomeGetter = biomeGetter;
            WoverBiome.C.log.debug("Biome getter changed, resetting bootstrap context");
            bootstrapContext = new BiomeBootstrapContextImpl();
            bootstrapContext.setLookupContext(lookupContext);

            BOOTSTRAP_BIOMES_WITH_DATA.emit(c -> c.bootstrap(bootstrapContext));
        } else {
            bootstrapContext.setLookupContext(lookupContext);
        }

        return bootstrapContext;
    }

    private static void onBootstrapBiomeDataRegistry(BootstapContext<BiomeData> biomeDataBootstapContext) {
        final BiomeBootstrapContextImpl context = initContext(biomeDataBootstapContext);
        context.bootstrapBiomeData(biomeDataBootstapContext);
    }

    private static void onBootstrapBiomeRegistry(BootstapContext<Biome> biomeBootstapContext) {
        final BiomeBootstrapContextImpl context = initContext(biomeBootstapContext);
        context.bootstrapBiome(biomeBootstapContext);
    }

    private static void onBootstrapSurfaceRuleRegistry(BootstapContext<AssignedSurfaceRule> assignedSurfaceRuleBootstapContext) {
        final BiomeBootstrapContextImpl context = initContext(assignedSurfaceRuleBootstapContext);
        context.bootstrapSurfaceRules(assignedSurfaceRuleBootstapContext);
    }

    private static void onBootstrapTags(TagBootstrapContext<Biome> biomeTagBootstrapContext) {
        final BiomeBootstrapContextImpl context = initContext(null);
        context.prepareTags(biomeTagBootstrapContext);

        //preparing tags is the last step of the bootstrap process, when we are done
        // we can invalidate the context
        bootstrapContext = null;
    }

    public static ResourceKey<Biome> createKey(
            ResourceLocation biomeID
    ) {
        return ResourceKey.create(
                Registries.BIOME,
                biomeID
        );
    }

    public static BiomeKey<BiomeBuilder.Vanilla> vanilla(ResourceLocation location) {
        return new VanillaKeyImpl(location);
    }


    public static BiomeKey<BiomeBuilder.Wrapped> wrapped(@NotNull ResourceKey<Biome> key) {
        return new WrappedKeyImpl(key.location());
    }
}
