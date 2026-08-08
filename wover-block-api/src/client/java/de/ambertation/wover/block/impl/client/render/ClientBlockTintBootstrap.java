package de.ambertation.wover.block.impl.client.render;

import de.ambertation.wover.block.api.client.render.ClientTinterRegistry;
import de.ambertation.wover.block.api.render.TintBinding;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.client.api.ClientTraitBootstrap;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.registries.BuiltInRegistries;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Client-side applier for {@link TintBinding}: walks the block registry once at client init and registers each
 * bound block's resolved {@code BlockColor} against vanilla's {@link BlockColors}.
 * <p>
 * This is the in-world half of a tint binding. The item half is baked into the generated item model during
 * datagen, from the same tint source - see {@code WoverBlockModelGenerators#delegateItemModel}.
 * <p>
 * Called from {@code MinecraftMixin} rather than an entrypoint because {@code BlockColors} is created inside
 * {@code Minecraft}'s constructor, so there is no earlier hook that can see the instance.
 * <p>
 * <b>Divergence from 26.x:</b> the resolved source is a {@code BlockColor}, not a {@code BlockTintSource}
 * (which 1.21.6 predates), so this registers it with {@code BlockColors.register(color, block)} instead of
 * {@code register(List.of(source), block)} - the list overload was introduced with the newer type.
 */
@Environment(EnvType.CLIENT)
public final class ClientBlockTintBootstrap {
    private ClientBlockTintBootstrap() {
    }

    /**
     * Registers the in-world colour of every block carrying a {@link TintBinding}.
     *
     * @param blockColors vanilla's block colour registry, freshly constructed
     */
    public static void applyBlockTints(BlockColors blockColors) {
        // The bindings were attached at block-definition time, but the factories that turn them into tint
        // sources come from mods' wover.client.traits entrypoints - make sure those have run.
        ClientTraitBootstrap.ensureRegistered();

        BuiltInRegistries.BLOCK.forEach(block -> {
            final var bindings = BlockTrait.<net.minecraft.world.level.block.Block, TintBinding>getRuntimeTraits(
                    block,
                    TintBinding.TINT_KEY
            );
            if (bindings == null || bindings.isEmpty()) return;

            final var source = ClientTinterRegistry.resolve(bindings.getLast(), block);
            if (source == null) return;

            blockColors.register(source, block);
        });
    }
}
