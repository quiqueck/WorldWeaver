package de.ambertation.wover.biome.impl;

import de.ambertation.wover.biome.api.BiomeKey;
import de.ambertation.wover.biome.api.builder.BiomeBuilder;
import de.ambertation.wover.biome.api.builder.event.OnBootstrapBiomes;
import de.ambertation.wover.biome.api.data.BiomeData;
import de.ambertation.wover.biome.impl.data.BiomeDataRegistryImpl;
import de.ambertation.wover.core.api.registry.CustomBootstrapContext;
import de.ambertation.wover.core.api.registry.DatapackRegistryBuilder;
import de.ambertation.wover.events.api.Event;
import de.ambertation.wover.events.api.types.OnBootstrapRegistry;
import de.ambertation.wover.events.impl.EventImpl;
import de.ambertation.wover.surface.api.AssignedSurfaceRule;
import de.ambertation.wover.surface.api.SurfaceRuleRegistry;
import de.ambertation.wover.tag.api.TagManager;
import de.ambertation.wover.tag.api.event.context.TagBootstrapContext;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;

import java.util.Objects;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class BiomeManagerImpl {
    public static final EventImpl<OnBootstrapRegistry<Biome>> BOOTSTRAP_BIOME_REGISTRY
            = new EventImpl<>("BOOTSTRAP_BIOME_REGISTRY");
    public static final EventImpl<OnBootstrapBiomes> BOOTSTRAP_BIOMES_WITH_DATA
            = new EventImpl<>("BOOTSTRAP_BIOMES_WITH_DATA");

    private static void onBootstrap(BootstrapContext<Biome> ctx) {
        BOOTSTRAP_BIOME_REGISTRY.emit(c -> c.bootstrap(ctx));
    }

    private static boolean didInit = false;

    @ApiStatus.Internal
    public static void initialize() {
        if (didInit) return;
        didInit = true;

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

    private static <B> BiomeBootstrapContextImpl initContext(BootstrapContext<B> lookupContext) {
        return CustomBootstrapContext.initContext(
                lookupContext,
                Registries.BIOME,
                BiomeBootstrapContextImpl::new
        );
    }

    private static void onBootstrapBiomeDataRegistry(BootstrapContext<BiomeData> biomeDataBootstrapContext) {
        final BiomeBootstrapContextImpl context = initContext(biomeDataBootstrapContext);
        context.bootstrapBiomeData(biomeDataBootstrapContext);
    }

    private static void onBootstrapBiomeRegistry(BootstrapContext<Biome> biomeBootstrapContext) {
        final BiomeBootstrapContextImpl context = initContext(biomeBootstrapContext);
        context.bootstrapBiome(biomeBootstrapContext);
    }

    private static void onBootstrapSurfaceRuleRegistry(BootstrapContext<AssignedSurfaceRule> assignedSurfaceRuleBootstrapContext) {
        final BiomeBootstrapContextImpl context = initContext(assignedSurfaceRuleBootstrapContext);
        context.bootstrapSurfaceRules(assignedSurfaceRuleBootstrapContext);
    }

    private static void onBootstrapTags(TagBootstrapContext<Biome> biomeTagBootstrapContext) {
        // The cached context is the only place the registered BiomeBuilders live, and they are collected
        // exactly once per context instance (in BiomeBootstrapContextImpl.onBootstrapContextChange). Tag
        // loading is *not* the last step of the world's lifetime: TagLoader runs again on every /reload,
        // while the worldgen registries stay frozen from world load, so no biome bootstrap re-runs to
        // rebuild the context. The context must therefore survive this phase - initContext already scopes
        // it to a single datapack load by comparing the Biome HolderGetter, which is the lifetime boundary
        // we actually want.
        final BiomeBootstrapContextImpl context = Objects.requireNonNull(
                initContext(null),
                "No biome bootstrap context: biome tags are loaded before the biome registry was bootstrapped"
        );
        context.prepareTags(biomeTagBootstrapContext);
    }

    public static ResourceKey<Biome> createKey(
            Identifier biomeID
    ) {
        return ResourceKey.create(
                Registries.BIOME,
                biomeID
        );
    }

    public static BiomeKey<BiomeBuilder.Vanilla> vanilla(Identifier location) {
        return new VanillaKeyImpl(location);
    }


    public static BiomeKey<BiomeBuilder.Wrapped> wrapped(@NotNull ResourceKey<Biome> key) {
        return new WrappedKeyImpl(key.identifier());
    }
}
