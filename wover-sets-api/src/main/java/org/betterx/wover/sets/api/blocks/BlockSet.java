package org.betterx.wover.sets.api.blocks;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.core.api.ModCore;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockSet<S extends BlockSet<S>> {
    protected final HashMap<SlotType, SlotData> slots;
    public final ModCore C;
    public final String baseName;
    public final SlotType baseSlot;

    public BlockSet(@NotNull ModCore modCore, @NotNull String baseName, @NotNull SlotType baseSlot) {
        this.slots = new HashMap<>();
        this.C = modCore;
        this.baseName = baseName;
        this.baseSlot = baseSlot;
    }

    protected SlotMap createDefaultSlots() {
        return SlotMap.of();
    }

    protected void addCommonBlockDefinitions(SlotType slot, BlockDefinition<?, ?> blockDefinition) {
        // This method can be overridden to add common block definitions
    }

    public S register() {

        final BiConsumer<SlotDefinition, BlockDefinition<?, ?>> acceptDefinition = (slotDefinition, blockDefinition) -> {
            Block block = blockDefinition.buildAndRegister();
            slots.put(slotDefinition.slot, new SlotData(slotDefinition.slot, block));
        };

        final SlotMap slotDefinitions = createDefaultSlots();
        for (SlotDefinition slotDefinition : slotDefinitions) {
            slotDefinition.createBlockDefinition(
                    this,
                    (blockDefinition) -> acceptDefinition.accept(slotDefinition, blockDefinition)
            );
        }
        return (S) this;
    }

    /**
     * Get initiated {@link Block} from this {@link BlockSet}.
     *
     * @param type         {@link SlotType} the slot you want to check
     * @param runIfPresent {@link Consumer} to run if block is present.
     * @return {@link Block} or {@code null} if nothing is stored.
     */
    @Nullable
    public Block ifBlockPresent(@NotNull SlotType type, @NotNull Consumer<Block> runIfPresent) {
        final Block block = this.getBlock(type);
        if (block != null) runIfPresent.accept(block);
        return block;
    }

    /**
     * Get the {@link Block} for the given {@link SlotType} from this set of blocks.
     * This method will return {@code null} if no block is stored for the given type.
     *
     * @param type {@link SlotType} The Block Type
     * @return {@link Block} or {@code null} if nothing is stored.
     */
    @Nullable
    public Block getBlock(@NotNull SlotType type) {
        final SlotData slotData = slots.get(type);
        if (slotData != null) {
            return slotData.block();
        }

        return null;
    }


    /**
     * Get the {@link Block} for the given {@link SlotType} from this set of blocks.
     * This method will try to find a block for the given types in the given order and return the first found block.
     * If no block is found for the given types, it will try to find a block for the {@link #baseSlot}.
     * If no block is found for the {@link #baseSlot} either, it will return a default Minecraft Stone block.
     *
     * @param type {@link SlotType} The Block Type(s) to check
     * @return {@link Block} or a default Minecraft Stone block if nothing is stored.
     */
    public @NotNull Block getBlockWithFallback(@NotNull SlotType... type) {
        // Try the given types in order, first found block will be returned
        for (SlotType slotType : type) {
            final Block block = this.getBlock(slotType);
            if (block != null) return block;
        }

        //Try the base Block slot
        final Block baseBlock = this.getBlock(this.baseSlot);
        if (baseBlock != null) return baseBlock;

        // If no block was found, return a default Minecraft Stone block
        return Blocks.STONE;
    }

    @NotNull
    public Block getBaseBlock() {
        return Objects.requireNonNull(this.getBlock(this.baseSlot));
    }
}
