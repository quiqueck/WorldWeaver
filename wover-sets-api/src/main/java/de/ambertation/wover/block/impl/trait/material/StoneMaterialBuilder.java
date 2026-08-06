package de.ambertation.wover.block.impl.trait.material;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.*;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverSets;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class StoneMaterialBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.TagOnlyBuilderWithDefaults {
    public static final GenericBlockTrait.TagOnlyBuilderWithDefaults BUILDER = new StoneMaterialBuilder();
    private final GenericBlockTrait DEFAULT = new Trait();

    private StoneMaterialBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "stone"));
    }

    public @Nullable List<BlockTrait<?, ?>> withDefault() {
        if (!ModCore.isDatagen()) return combine(DEFAULT);
        return combine(
                DEFAULT,
                BlockTraits.MINEABLE_WITH.needsPickAxe(),
                // What vanilla does with stone and its whole decorative family (stone, deepslate, bricks,
                // quartz, purpur, end_stone, ...). Override per block with SULFUR_CUBE_ARCHETYPE.
                BlockTraits.SULFUR_CUBE_ARCHETYPE.slowBouncy()
        );
    }

    @Override
    public @Nullable List<BlockTrait<?, ?>> tagOnly() {
        // Task #32: only the pickaxe tag, no forced strength/instrument/reqTool - for stone-strength blocks
        // whose own (tougher) strength must survive. Matches BetterNether's NetherMaterial.stoneTagOnly().
        return combine(
                BlockTraits.MINEABLE_WITH.needsPickAxe(),
                BlockTraits.SULFUR_CUBE_ARCHETYPE.slowBouncy()
        );
    }

    class Trait extends BlockTraitImpl.Generic {
        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            definition
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(2.0F, 6.0F);
        }
    }
}
