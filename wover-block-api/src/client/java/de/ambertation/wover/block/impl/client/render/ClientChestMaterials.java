package de.ambertation.wover.block.impl.client.render;

import de.ambertation.wover.block.api.render.ChestRendererBinding;
import de.ambertation.wover.block.api.trait.BlockTrait;

import static net.minecraft.client.renderer.Sheets.CHEST_MAPPER;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.Nullable;

/**
 * Client-side derivation of the single/left/right chest render {@link Material}s for a custom chest block. This
 * is the {@code net.minecraft.client.renderer.Sheets#CHEST_MAPPER} computation the former
 * {@code ChestRenderTrait.configure(...)} ran at registration time; it now runs lazily (and is cached) for any
 * block carrying the common {@link ChestRendererBinding}, and {@code SheetsMixin} reads it at render time.
 */
@Environment(EnvType.CLIENT)
public final class ClientChestMaterials {
    /**
     * The three chest-renderer materials needed to draw a (double) chest.
     *
     * @param single the single-chest material
     * @param left   the left half of a double chest
     * @param right  the right half of a double chest
     */
    public record ChestMaterialSet(Material single, Material left, Material right) {
    }

    private static final Map<Block, ChestMaterialSet> CACHE = new ConcurrentHashMap<>();

    private ClientChestMaterials() {
    }

    /**
     * @param block the block being rendered
     * @return the chest materials for {@code block} if it carries the wover chest-renderer marker, else {@code null}
     */
    public static @Nullable ChestMaterialSet materialFor(Block block) {
        if (!BlockTrait.hasRuntimeTrait(block, ChestRendererBinding.CHEST_RENDERER_KEY)) {
            return null;
        }
        return CACHE.computeIfAbsent(block, b -> {
            final var location = BuiltInRegistries.BLOCK.getKey(b);
            return new ChestMaterialSet(
                    CHEST_MAPPER.apply(location),
                    CHEST_MAPPER.apply(location.withSuffix("_left")),
                    CHEST_MAPPER.apply(location.withSuffix("_right"))
            );
        });
    }
}
