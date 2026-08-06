package de.ambertation.wover.block.impl.trait.material;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.*;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverSets;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class WoodMaterialBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.WoodBuilderWithDefaults {
    public static final GenericBlockTrait.WoodBuilderWithDefaults BUILDER = new WoodMaterialBuilder();
    private final GenericBlockTrait DEFAULT = new Trait();

    private WoodMaterialBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "wood"));
    }

    public @Nullable List<BlockTrait<?, ?>> withDefault() {
        if (!ModCore.isDatagen()) return combine(DEFAULT, BlockTraits.FLAMMABLE.withDefault());
        return combine(
                DEFAULT,
                BlockTraits.MINEABLE_WITH.needsAxe(),
                BlockTraits.FLAMMABLE.withDefault(),
                // What vanilla does with planks, logs and bamboo blocks. Override per block with
                // SULFUR_CUBE_ARCHETYPE.
                BlockTraits.SULFUR_CUBE_ARCHETYPE.bouncy()
        );
    }

    @Override
    public @Nullable List<BlockTrait<?, ?>> netherWood() {
        if (!ModCore.isDatagen()) return combine(DEFAULT);
        return combine(
                DEFAULT,
                BlockTraits.MINEABLE_WITH.needsAxe(),
                BlockTraits.SULFUR_CUBE_ARCHETYPE.bouncy()
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
                    .mapColor(MapColor.WOOD)
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD);
        }
    }
}
