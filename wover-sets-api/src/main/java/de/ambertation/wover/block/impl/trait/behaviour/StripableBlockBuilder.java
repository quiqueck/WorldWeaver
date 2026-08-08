package de.ambertation.wover.block.impl.trait.behaviour;


import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.AbstractBlockTraitBuilder;
import de.ambertation.wover.block.api.trait.BlockTraitKey;
import de.ambertation.wover.block.api.trait.RuntimeBlockTrait;
import de.ambertation.wover.block.api.trait.behaviour.StripableBlockTrait;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.entrypoint.LibWoverSets;

import net.minecraft.core.component.BlockTransformer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.CopyPropertiesProvider;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StripableBlockBuilder extends AbstractBlockTraitBuilder<Block, StripableBlockTrait> implements StripableBlockTrait.Builder {
    public static final StripableBlockTrait.Builder BUILDER = new StripableBlockBuilder();

    /**
     * Every block that was registered with a {@link StripableBlockTrait}, in registration order.
     * <p>
     * The trait is stored on the block itself (as a {@link RuntimeBlockTrait}), so this map is only an index -
     * it exists so {@link StripableBlockTransformers} does not have to walk the whole block registry on every
     * datapack reload. Registration order is preserved because it is the order the resulting
     * {@link BlockTransformer.BlockTransformData} entries end up in, and vanilla's transformer list is
     * evaluated first-match-wins.
     */
    private static final Map<Block, Trait> STRIPABLE_BLOCKS = new LinkedHashMap<>();

    private StripableBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "stripable"));
    }

    public @Nullable StripableBlockTrait with(@Nullable Block strippedBlock) {
        if (strippedBlock == null) return null;
        // Matches vanilla's own strippables (BlockTransformerMappings#getStrippableBlockData): the target's
        // default state with every property it shares with the block being stripped copied over, which covers
        // RotatedPillarBlock.AXIS for logs/bark without needing to name it.
        return new Trait(new CopyPropertiesProvider(strippedBlock));
    }

    public @Nullable StripableBlockTrait with(@Nullable BlockState strippedBlock) {
        if (strippedBlock == null) return null;
        return new Trait(BlockStateProvider.simple(strippedBlock));
    }

    public @Nullable StripableBlockTrait with(@Nullable BlockStateProvider strippedBlock) {
        if (strippedBlock == null) return null;
        return new Trait(strippedBlock);
    }

    public @Nullable StripableBlockTrait withDeferred(@Nullable Supplier<BlockStateProvider> strippedBlock) {
        if (strippedBlock == null) return null;
        return new Trait(strippedBlock);
    }

    /**
     * Builds the {@link BlockTransformer.BlockTransformData} entry for every registered strippable block.
     * <p>
     * Each entry is shaped exactly like vanilla's, i.e. {@code BlockPredicate.matchesBlocks(source)} paired
     * with the trait's {@link BlockStateProvider} and the {@code minecraft:item.axe.strip} sound; every other
     * field keeps the builder default (consume-on-use, one point of durability, neighbour update, no
     * particle), which is what vanilla's strippables use too.
     *
     * @return the transform data for all strippable blocks, in registration order
     */
    public static List<BlockTransformer.BlockTransformData> axeTransforms() {
        final var result = new ArrayList<BlockTransformer.BlockTransformData>(STRIPABLE_BLOCKS.size());
        for (var entry : STRIPABLE_BLOCKS.entrySet()) {
            result.add(BlockTransformer.BlockTransformData
                    .builder(BlockPredicate.matchesBlocks(entry.getKey()), entry.getValue().strippedBlock())
                    .sound(SoundEvents.AXE_STRIP)
                    .build()
            );
        }
        return result;
    }

    class Trait extends BlockTraitImpl<Block, StripableBlockTrait> implements StripableBlockTrait, RuntimeBlockTrait<Block, StripableBlockTrait> {
        private final Supplier<BlockStateProvider> strippedBlock;
        private @Nullable BlockStateProvider resolved;

        Trait(BlockStateProvider strippedBlock) {
            this.strippedBlock = () -> strippedBlock;
            this.resolved = strippedBlock;
        }

        Trait(Supplier<BlockStateProvider> strippedBlock) {
            this.strippedBlock = strippedBlock;
        }

        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public StripableBlockTrait forRuntime() {
            return this;
        }

        @Override
        public void afterBlockRegistration(
                Block block,
                BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition
        ) {
            STRIPABLE_BLOCKS.put(block, this);
        }

        @Override
        public @NotNull BlockStateProvider strippedBlock() {
            if (resolved == null) resolved = strippedBlock.get();
            return resolved;
        }
    }
}
