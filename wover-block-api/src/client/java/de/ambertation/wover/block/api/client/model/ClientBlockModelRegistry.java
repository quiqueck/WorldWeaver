package de.ambertation.wover.block.api.client.model;

import de.ambertation.wover.block.api.model.BlockModelBinding;
import de.ambertation.wover.block.api.model.ModelKey;
import de.ambertation.wover.block.api.model.WoverBlockModelGenerators;
import de.ambertation.wover.entrypoint.LibWoverBlock;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * Client-side registry mapping a {@link ModelKey} to the {@link ClientModelFactory} that generates its models.
 * wover populates it with the built-in catalogue from a {@code wover.client.traits} entrypoint; third-party mods
 * register their own keys the same way. The datagen walk looks a block's {@link BlockModelBinding} up here and
 * invokes the resolved factory with the binding's payload.
 */
@Environment(EnvType.CLIENT)
public class ClientBlockModelRegistry {
    private static final Map<ModelKey<?>, ClientModelFactory<?>> FACTORIES = new HashMap<>();

    private ClientBlockModelRegistry() {
    }

    /**
     * Registers the factory for a model key. Later registrations for the same key replace earlier ones.
     *
     * @param key     the model shape
     * @param factory the generator for that shape
     * @param <P>     the payload type
     */
    public static <P> void register(ModelKey<P> key, ClientModelFactory<P> factory) {
        FACTORIES.put(key, factory);
    }

    /**
     * Resolves and runs the factory for a binding.
     *
     * @param binding   the block's model binding
     * @param key       the registry key of the block
     * @param block     the block to generate models for
     * @param generator the model generator to use
     * @return {@code true} if a factory was found and invoked, {@code false} if the key was not registered
     */
    @SuppressWarnings("unchecked")
    public static boolean apply(
            BlockModelBinding binding,
            ResourceKey<Block> key,
            Block block,
            WoverBlockModelGenerators generator
    ) {
        final ClientModelFactory<Object> factory = (ClientModelFactory<Object>) FACTORIES.get(binding.modelKey());
        if (factory == null) {
            LibWoverBlock.C.LOG.error("No client model factory registered for " + binding.modelKey() + " (block " + key + ")");
            return false;
        }
        factory.provide(key, block, generator, binding.payload());
        return true;
    }
}
