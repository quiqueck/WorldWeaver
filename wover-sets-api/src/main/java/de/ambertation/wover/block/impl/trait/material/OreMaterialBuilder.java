package de.ambertation.wover.block.impl.trait.material;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.*;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverSets;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

import java.util.List;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The "ore" classification: the trait-side replacement for bclib's {@code BaseOreBlock} base class (which
 * was nothing more than a vanilla {@link net.minecraft.world.level.block.DropExperienceBlock} carrying the
 * {@code BehaviourOre} marker and a code-driven ore loot table).
 * <p>
 * {@link #withDefault()} bundles the pickaxe-mineable tag (datagen only) with the classification trait, whose
 * {@code configure()} reproduces {@code BaseOreBlock}'s constructor defaults - {@code instrument(BASEDRUM)},
 * {@code requiresCorrectToolForDrops}, {@code strength(3, 9)} and {@code sound(STONE)} - and adds the
 * {@link CommonBlockTags#ORES c:ores} tag that the {@code BehaviourOre} marker used to contribute through
 * bclib's {@code BCLAutoBlockTagProvider}.
 * <p>
 * The map color is deliberately <b>not</b> set here (ore colors are per-ore): chain a {@code mapColor(...)}
 * override after this trait, or let the block's own constructor set it. Per the resolved property call-order
 * rule (WorldWeaver 5dcd992), a {@code strength}/{@code sound}/{@code mapColor} chained after
 * {@code addTrait(ORE_BLOCK.withDefault())} - or set by the block factory, which runs last - wins over these
 * defaults, so an ore with different toughness (e.g. BetterNether's 3/5 netherrack ores) keeps its values.
 */
public class OreMaterialBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.OreBuilderWithDefaults {
    public static final GenericBlockTrait.OreBuilderWithDefaults BUILDER = new OreMaterialBuilder();
    private final GenericBlockTrait DEFAULT = new Trait();

    private OreMaterialBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "ore"));
    }

    public @Nullable List<BlockTrait<?, ?>> withDefault() {
        if (!ModCore.isDatagen()) return combine(DEFAULT);
        return combine(
                DEFAULT,
                BlockTraits.MINEABLE_WITH.needsPickAxe(),
                // Vanilla splits its ores by what they hold: the metal ores are slow_flat, everything else
                // (coal/lapis/redstone/diamond/emerald/quartz) is slow_bouncy with the stone family. This
                // takes the non-metal side as the default; a metal ore overrides it with
                // SULFUR_CUBE_ARCHETYPE.slowFlat().
                BlockTraits.SULFUR_CUBE_ARCHETYPE.slowBouncy()
        );
    }

    @Override
    public @Nullable List<BlockTrait<?, ?>> dropping(@NotNull Supplier<Item> drop, int min, int max) {
        // withDefault() (classification + pickaxe tag + sulfur cube archetype) plus the vanilla ore-drop loot
        // trait. The pickaxe tag, the archetype and the loot trait are all datagen-only (null off-datagen);
        // combine(...) drops the nulls, so this reduces to combine(DEFAULT) outside datagen, exactly like
        // withDefault().
        if (!ModCore.isDatagen()) return combine(DEFAULT);
        return combine(
                DEFAULT,
                BlockTraits.MINEABLE_WITH.needsPickAxe(),
                BlockTraits.SULFUR_CUBE_ARCHETYPE.slowBouncy(),
                BlockTraits.LOOT_TABLE.dropOre(drop, min, max)
        );
    }

    class Trait extends BlockTraitImpl.Generic {
        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            // c:ores comes from the BehaviourOre marker (via bclib's BCLAutoBlockTagProvider), so it must be
            // supplied here for this trait to be a complete replacement for it.
            definition.addTags(CommonBlockTags.ORES);

            definition
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(3.0F, 9.0F)
                    .sound(SoundType.STONE);
        }
    }
}
