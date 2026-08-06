package de.ambertation.wover.block.impl.trait.material;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.*;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverSets;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

import java.util.List;
import org.jetbrains.annotations.Nullable;

/**
 * Marks a block as belonging to the "plant-derived light source" material family - the
 * {@code minecraft:shroomlight} archetype: a soft, non-flammable organic block that emits a constant
 * light level of 15, breaks like shroomlight (strength 1.0/1.0, no required tool for drops) and is
 * speed-mined with a hoe rather than an axe/pickaxe/shears.
 * <p>
 * Deliberately does <em>not</em> configure {@code mapColor}: unlike {@link WoodMaterialBuilder}'s uniform
 * brown, plant-light blocks across both mods keep visually distinct map colors (blue vine, orange pillar,
 * blue gourd, ...), so callers set their own via {@code .mapColor(...)} after adding this trait.
 * <p>
 * Also deliberately excludes composting: vanilla shroomlight composts at 0.65, but that chance is applied
 * via a separate registry-side trait ({@code CompostableBlockTrait}, owned by BCLib, which this module
 * cannot depend on) - callers add {@code CompostableBlockTrait.withChance(0.65f)} alongside this trait.
 */
public class PlantLightMaterialBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.BuilderWithDefaults {
    public static final GenericBlockTrait.BuilderWithDefaults BUILDER = new PlantLightMaterialBuilder();
    private final GenericBlockTrait DEFAULT = new Trait();

    private PlantLightMaterialBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "plant_light"));
    }

    public @Nullable List<BlockTrait<?, ?>> withDefault() {
        if (!ModCore.isDatagen()) return combine(DEFAULT);
        return combine(
                DEFAULT,
                BlockTraits.MINEABLE_WITH.needsHoe(),
                // vanilla shroomlight - the block this family is modelled on - is slow_sliding, alongside
                // the mushroom blocks and wart blocks. Override per block with SULFUR_CUBE_ARCHETYPE.
                BlockTraits.SULFUR_CUBE_ARCHETYPE.slowSliding()
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
                    .strength(1.0F)
                    .sound(SoundType.SHROOMLIGHT)
                    .lightLevel(state -> 15);
        }
    }
}
