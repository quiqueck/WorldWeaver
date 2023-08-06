package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;
import org.betterx.wover.events.impl.EventImpl;
import org.betterx.wover.feature.api.configured.configurators.FeatureConfigurator;
import org.betterx.wover.feature.impl.placed.FeaturePlacementBuilderImpl;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Optional;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class FeatureConfiguratorImpl<FC extends FeatureConfiguration, F extends Feature<FC>> {
    public static final EventImpl<OnBootstrapRegistry<ConfiguredFeature<?, ?>>> BOOTSTRAP_CONFIGURED_FEATURES =
            new EventImpl<>("BOOTSTRAP_CONFIGURED_FEATURES");

    @ApiStatus.Internal
    public static void initialize() {
        DatapackRegistryBuilder.addBootstrap(
                Registries.CONFIGURED_FEATURE,
                FeatureConfiguratorImpl::onBootstrap
        );
    }

    private static void onBootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        BOOTSTRAP_CONFIGURED_FEATURES.emit(c -> c.bootstrap(context));
    }

    /**
     * Creates a new {@link ResourceKey} for a {@link ConfiguredFeature} from the given id.
     *
     * @param id The id of the {@link ConfiguredFeature}
     * @return The key for the {@link ConfiguredFeature}
     */
    @NotNull
    public static ResourceKey<ConfiguredFeature<?, ?>> createKey(
            @NotNull ResourceLocation id
    ) {
        return ResourceKey.create(
                Registries.CONFIGURED_FEATURE,
                id
        );
    }

    @Nullable
    public static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<ConfiguredFeature<?, ?>> getHolder(
            @Nullable HolderGetter<ConfiguredFeature<?, ?>> getter,
            @NotNull ResourceKey<ConfiguredFeature<?, ?>> key
    ) {
        if (getter == null) return null;

        final Optional<Holder.Reference<ConfiguredFeature<?, ?>>> h = getter.get(key);
        return h.orElse(null);
    }

    // --------------------------------------------------
    // Instance Data and Methods
    // --------------------------------------------------
    @Nullable
    public final ResourceKey<ConfiguredFeature<?, ?>> key;
    protected final @Nullable BootstapContext<ConfiguredFeature<?, ?>> bootstrapContext;

    private ResourceKey<PlacedFeature> transitiveFeatureKey;
    private BootstapContext<PlacedFeature> transitiveBootstrapContext;

    FeatureConfiguratorImpl(
            @Nullable BootstapContext<ConfiguredFeature<?, ?>> ctx,
            @Nullable ResourceKey<ConfiguredFeature<?, ?>> key
    ) {
        this.key = key;
        this.bootstrapContext = ctx;
    }

    void setTransitive(BootstapContext<PlacedFeature> ctx, ResourceKey<PlacedFeature> key) {
        this.transitiveBootstrapContext = ctx;
        this.transitiveFeatureKey = key;
    }

    @ApiStatus.Internal
    public ResourceKey<PlacedFeature> getTransitiveFeatureKey() {
        return this.transitiveFeatureKey;
    }

    @ApiStatus.Internal
    public BootstapContext<PlacedFeature> getTransitiveBootstrapContext() {
        return this.transitiveBootstrapContext;
    }

    protected abstract @NotNull FC createConfiguration();
    protected abstract @NotNull F getFeature();

    public Holder<ConfiguredFeature<?, ?>> register() {
        if (key == null) {
            throw new IllegalStateException("A ResourceKey can not be null if a feature should be registered!");
        }
        if (bootstrapContext == null) {
            throwStateError("Can not register a feature without a bootstrap context!");
        }
        ConfiguredFeature<FC, F> cFeature = build();

        return bootstrapContext.register(
                key,
                cFeature
        );
    }

    /**
     * Creates an unnamed {@link Holder} for this {@link FeatureConfigurator}.
     * <p>
     * This method is usefull, if you want to create an anonymous {@link ConfiguredFeature}
     * that is directly inlined into the definition of a
     * {@link net.minecraft.world.level.levelgen.placement.PlacedFeature} (instead of referenced by ID)
     *
     * @return the holder
     */
    public Holder<ConfiguredFeature<?, ?>> directHolder() {
        return Holder.direct(build());
    }

    public FeaturePlacementBuilderImpl inlinePlace() {
        return FeaturePlacementBuilderImpl.withTransitive(this, (cfg, plc) -> {
            var res = new RandomPatchImpl(bootstrapContext, cfg);
            res.setTransitive(transitiveBootstrapContext, plc);
            return res;
        });
    }

    @NotNull
    private ConfiguredFeature<FC, F> build() {
        FC config = createConfiguration();

        if (config == null) {
            throw new IllegalStateException("Feature configuration for " + key.location() + " can not be null!");
        }

        return new ConfiguredFeature<>(getFeature(), config);
    }

    void throwStateError(String message) {
        throw new IllegalStateException(message + (key == null
                ? ""
                : ("(" + key.location() + ")")
        ));
    }
}
