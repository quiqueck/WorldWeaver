package de.ambertation.wover.block.api.render;

import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockTraitKey;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverBlock;

import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.Nullable;

/**
 * Common, client-free marker trait for a custom chest block that needs the wooden-chest render materials. It
 * carries no client {@code Material} (which would be a client type); instead the client source set derives the
 * single/left/right chest materials from the block's registry location on demand - the same computation the old
 * {@code ChestRenderTrait.configure(...)} performed - and {@code SheetsMixin} reads them for blocks carrying
 * this marker.
 */
public final class ChestRendererBinding extends BlockTraitImpl<Block, ChestRendererBinding> implements BlockTrait<Block, ChestRendererBinding> {
    /** The trait key every chest-renderer marker is attached under. */
    public static final BlockTraitKey CHEST_RENDERER_KEY = BlockTraitKey.ofUnique(LibWoverBlock.C, "chest_renderer");

    /** WithDefault-style builder producing the marker (client only, matching the former client trait). */
    public static final class Builder {
        /**
         * @return the marker binding, or {@code null} on a dedicated server
         */
        public @Nullable BlockTrait<?, ?> withDefault() {
            if (ModCore.isClient()) return new ChestRendererBinding();
            return null;
        }
    }

    private ChestRendererBinding() {
    }

    @Override
    public BlockTraitKey key() {
        return CHEST_RENDERER_KEY;
    }

    @Override
    public ChestRendererBinding forRuntime() {
        return this;
    }
}
