package de.ambertation.wover.feature.impl.configured;

import de.ambertation.wover.core.api.registry.DatapackRegistryBuilder;
import de.ambertation.wover.events.api.types.OnBootstrapRegistry;
import de.ambertation.wover.events.impl.EventImpl;
import de.ambertation.wover.feature.api.configured.configurators.FeatureConfigurator;
import de.ambertation.wover.feature.impl.placed.FeaturePlacementBuilderImpl;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Optional;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base implementation for every {@link FeatureConfigurator}.
 * <p>
 * 26.3 collapsed {@code ConfiguredFeature<FC, F>} into {@link Feature}. The former two abstract steps
 * ({@code createConfiguration()} returning an {@code FC} plus {@code getFeature()} returning the stateless
 * {@code F} singleton) therefore merged into a single {@link #createFeature()} that returns the fully
 * configured {@link Feature}. The class also lost its {@code <FC, F>} type parameters, which no longer
 * describe anything.
 */
public abstract class FeatureConfiguratorImpl {
    public static final EventImpl<OnBootstrapRegistry<Feature>> BOOTSTRAP_FEATURES =
            new EventImpl<>("BOOTSTRAP_FEATURES");

    private static boolean didInit = false;

    @ApiStatus.Internal
    public static void initialize() {
        if (didInit) return;
        didInit = true;

        // Registries.CONFIGURED_FEATURE -> Registries.FEATURE in 26.3 (directory
        // data/<ns>/worldgen/configured_feature -> worldgen/feature), element type Feature.
        DatapackRegistryBuilder.addBootstrap(
                Registries.FEATURE,
                FeatureConfiguratorImpl::onBootstrap
        );
    }

    private static void onBootstrap(BootstrapContext<Feature> context) {
        BOOTSTRAP_FEATURES.emit(c -> c.bootstrap(context));
    }

    /**
     * Creates a new {@link ResourceKey} for a {@link Feature} from the given id.
     *
     * @param id The id of the {@link Feature}
     * @return The key for the {@link Feature}
     */
    @NotNull
    public static ResourceKey<Feature> createKey(
            @NotNull Identifier id
    ) {
        return ResourceKey.create(
                Registries.FEATURE,
                id
        );
    }

    @Nullable
    public static Holder<Feature> getHolder(
            @Nullable HolderGetter<Feature> getter,
            @NotNull ResourceKey<Feature> key
    ) {
        if (getter == null) return null;

        final Optional<Holder.Reference<Feature>> h = getter.get(key);
        return h.orElse(null);
    }

    // --------------------------------------------------
    // Instance Data and Methods
    // --------------------------------------------------
    @Nullable
    public final ResourceKey<Feature> key;
    protected final @Nullable BootstrapContext<Feature> bootstrapContext;

    private ResourceKey<PlacedFeature> transitiveFeatureKey;
    private BootstrapContext<PlacedFeature> transitiveBootstrapContext;

    FeatureConfiguratorImpl(
            @Nullable BootstrapContext<Feature> ctx,
            @Nullable ResourceKey<Feature> key
    ) {
        this.key = key;
        this.bootstrapContext = ctx;
    }

    void setTransitive(BootstrapContext<PlacedFeature> ctx, ResourceKey<PlacedFeature> key) {
        this.transitiveBootstrapContext = ctx;
        this.transitiveFeatureKey = key;
    }

    @ApiStatus.Internal
    public ResourceKey<PlacedFeature> getTransitiveFeatureKey() {
        return this.transitiveFeatureKey;
    }

    @ApiStatus.Internal
    public BootstrapContext<PlacedFeature> getTransitiveBootstrapContext() {
        return this.transitiveBootstrapContext;
    }

    /**
     * Builds the fully configured {@link Feature} this configurator describes.
     *
     * @return the feature
     */
    protected abstract @NotNull Feature createFeature();

    public Holder<Feature> register() {
        if (key == null) {
            throw new IllegalStateException("A ResourceKey can not be null if a feature should be registered!");
        }
        if (bootstrapContext == null) {
            throwStateError("Can not register a feature without a bootstrap context!");
        }

        // 26.3 removed BootstrapContext#register(key, value, Lifecycle). 26.1 used that overload here
        // because it also bound an already-created *unbound* reference, which could happen when another
        // registry (e.g. biome_modifications) looked this feature up first under parallel registry loading.
        // Vanilla offers no replacement, so the two-argument register is the only option now.
        return bootstrapContext.register(
                key,
                createFeature()
        );
    }

    /**
     * Creates an unnamed {@link Holder} for this {@link FeatureConfigurator}.
     * <p>
     * This method is usefull, if you want to create an anonymous {@link Feature}
     * that is directly inlined into the definition of a
     * {@link net.minecraft.world.level.levelgen.placement.PlacedFeature} (instead of referenced by ID)
     *
     * @return the holder
     */
    public Holder<Feature> directHolder() {
        return Holder.direct(createFeature());
    }

    public FeaturePlacementBuilderImpl inlinePlace() {
        return FeaturePlacementBuilderImpl.withTransitive(this, (cfg, plc) -> {
            var res = new RandomPatchImpl(bootstrapContext, cfg);
            res.setTransitive(transitiveBootstrapContext, plc);
            return res;
        });
    }

    void throwStateError(String message) {
        throw new IllegalStateException(message + (key == null
                ? ""
                : ("(" + key.identifier() + ")")
        ));
    }
}
