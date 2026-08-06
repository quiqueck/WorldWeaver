package de.ambertation.wover.block.impl.trait.material;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.*;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverSets;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class MetalMaterialBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.TagOnlyBuilderWithDefaults {
    public static final GenericBlockTrait.TagOnlyBuilderWithDefaults BUILDER = new MetalMaterialBuilder();
    private final GenericBlockTrait DEFAULT = new Trait();

    private MetalMaterialBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "metal"));
    }

    public @Nullable List<BlockTrait<?, ?>> withDefault() {
        if (!ModCore.isDatagen()) return combine(DEFAULT);
        return combine(
                DEFAULT,
                BlockTraits.MINEABLE_WITH.needsPickAxe(),
                // What vanilla does with iron/gold/netherite/copper blocks and their ores. Override per
                // block with SULFUR_CUBE_ARCHETYPE.
                BlockTraits.SULFUR_CUBE_ARCHETYPE.slowFlat()
        );
    }

    @Override
    public @Nullable List<BlockTrait<?, ?>> tagOnly() {
        // Task #32: only the pickaxe tag, no forced strength/instrument/reqTool/sound - for metal-classified
        // blocks that must keep their own strength (e.g. the netherite fire bowls). Matches BetterNether's
        // NetherMaterial.metalTagOnly().
        return combine(
                BlockTraits.MINEABLE_WITH.needsPickAxe(),
                BlockTraits.SULFUR_CUBE_ARCHETYPE.slowFlat()
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
                    .instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                    .requiresCorrectToolForDrops()
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.METAL);
        }
    }
}
