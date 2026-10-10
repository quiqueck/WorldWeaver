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
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedStateProvider;
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
        // Matches vanilla's own strippables (BlockTransformers#axeStrippables): the target's
        // default state with every property it shares with the block being stripped copied over, which covers
        // RotatedPillarBlock.AXIS for logs/bark without needing to name it.
        return new Trait(new CopyPropertiesProvider(strippedBlock));
    }

    public @Nullable StripableBlockTrait with(@Nullable BlockState strippedBlock) {
        if (strippedBlock == null) return null;
        return new Trait(BlockStateProvider.of(strippedBlock));
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
     * Builds the {@link BlockTransformer.BlockTransformData} for the registered strippable blocks.
     * <p>
     * Shaped exactly like vanilla's own strippables since the 26.3 release ({@code BlockTransformers#axeStrippables}):
     * a single entry whose {@link RuleBasedStateProvider} pairs {@code BlockPredicate.matchesBlocks(source)} with
     * the trait's {@link BlockStateProvider} for every block, in registration order (first match wins), and the
     * {@code minecraft:item.axe.strip} sound. Every other field keeps the builder default, which is what
     * vanilla's strippables use too. The rule-based provider yields no state for a block it has no rule for, so
     * the transformer simply falls through to the next entry.
     *
     * @return the transform data for all strippable blocks (empty when there are none)
     */
    public static List<BlockTransformer.BlockTransformData> axeTransforms() {
        if (STRIPABLE_BLOCKS.isEmpty()) return List.of();
        final RuleBasedStateProvider.Builder rules = RuleBasedStateProvider.builder();
        for (var entry : STRIPABLE_BLOCKS.entrySet()) {
            rules.ifTrueThenProvide(BlockPredicate.matchesBlocks(entry.getKey()), entry.getValue().strippedBlock());
        }
        return List.of(BlockTransformer.BlockTransformData
                .builder(rules.build())
                .sound(SoundEvents.AXE_STRIP)
                .build()
        );
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
