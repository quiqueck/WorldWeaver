package de.ambertation.wover.generator.api.client.biomesource.client;

import de.ambertation.wover.common.generator.api.biomesource.BiomeSourceConfig;
import de.ambertation.wover.common.generator.api.biomesource.BiomeSourceWithConfig;
import de.ambertation.wover.entrypoint.LibWoverWorldGenerator;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.level.biome.BiomeSource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Client-side registry mapping a {@link BiomeSourceWithConfig} implementation's {@link Class} to the
 * {@link PanelFactory} that builds its settings panel for the world-creation screen.
 * <p>
 * This exists so common-sourceSet {@link BiomeSource}s (used during real chunk generation, including on
 * dedicated servers) never need to implement a client-only panel method themselves - they just have a
 * {@link BiomeSourceWithConfig}, and their mod's client entrypoint registers the panel factory here instead.
 * wover's own {@code WoverEndBiomeSource}/{@code WoverNetherBiomeSource} are registered this way from
 * {@code LibWoverWorldGeneratorClient}.
 */
@Environment(EnvType.CLIENT)
public class ClientBiomeSourceConfigScreenRegistry {
    @FunctionalInterface
    public interface PanelFactory<B extends BiomeSource, C extends BiomeSourceConfig<B>> {
        BiomeSourceConfigPanel<B, C> create(@NotNull Screen parent, @NotNull C config);
    }

    private static final Map<Class<?>, PanelFactory<?, ?>> FACTORIES = new HashMap<>();

    private ClientBiomeSourceConfigScreenRegistry() {
    }

    /**
     * Registers the panel factory for a {@link BiomeSource} implementation. Later registrations for the
     * same class replace earlier ones.
     *
     * @param biomeSourceClass the concrete {@link BiomeSource} class the factory builds panels for
     * @param factory          the panel factory
     * @param <B>              the {@link BiomeSource} type
     * @param <C>              the {@link BiomeSourceConfig} type
     */
    public static <B extends BiomeSource, C extends BiomeSourceConfig<B>> void register(
            Class<? extends B> biomeSourceClass,
            PanelFactory<B, C> factory
    ) {
        FACTORIES.put(biomeSourceClass, factory);
    }

    /**
     * Resolves and runs the factory registered for {@code biomeSource}'s class, if any.
     *
     * @param parent       the screen the panel will be shown from
     * @param biomeSource  the currently selected biome source
     * @return the new panel, or {@code null} if {@code biomeSource} isn't a {@link BiomeSourceWithConfig}
     * or has no factory registered
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public static BiomeSourceConfigPanel<?, ?> createPanel(@NotNull Screen parent, @NotNull BiomeSource biomeSource) {
        if (!(biomeSource instanceof BiomeSourceWithConfig<?, ?> source)) {
            return null;
        }

        final PanelFactory<BiomeSource, BiomeSourceConfig<BiomeSource>> factory =
                (PanelFactory<BiomeSource, BiomeSourceConfig<BiomeSource>>) (PanelFactory<?, ?>) FACTORIES.get(biomeSource.getClass());
        if (factory == null) {
            LibWoverWorldGenerator.C.LOG.verbose(
                    "No client biome source config panel factory registered for " + biomeSource.getClass()
            );
            return null;
        }

        final BiomeSourceConfig<BiomeSource> config = (BiomeSourceConfig<BiomeSource>) source.getBiomeSourceConfig();
        return factory.create(parent, config);
    }
}
