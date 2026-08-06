package de.ambertation.wover.block.impl.trait.behaviour;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.AbstractBlockTraitBuilder;
import de.ambertation.wover.block.api.trait.BlockTraitKey;
import de.ambertation.wover.block.api.trait.behaviour.SulfurCubeArchetypeTrait;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverSets;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.Nullable;

public class SulfurCubeArchetypeBuilder extends AbstractBlockTraitBuilder.Generic implements SulfurCubeArchetypeTrait.Builder {
    public static final SulfurCubeArchetypeTrait.Builder BUILDER = new SulfurCubeArchetypeBuilder();

    private SulfurCubeArchetypeBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "sulfur_cube_archetype"));
    }

    @Override
    public @Nullable SulfurCubeArchetypeTrait regular() {
        return with(ItemTags.SULFUR_CUBE_ARCHETYPE_REGULAR);
    }

    @Override
    public @Nullable SulfurCubeArchetypeTrait bouncy() {
        return with(ItemTags.SULFUR_CUBE_ARCHETYPE_BOUNCY);
    }

    @Override
    public @Nullable SulfurCubeArchetypeTrait slowBouncy() {
        return with(ItemTags.SULFUR_CUBE_ARCHETYPE_SLOW_BOUNCY);
    }

    @Override
    public @Nullable SulfurCubeArchetypeTrait slowFlat() {
        return with(ItemTags.SULFUR_CUBE_ARCHETYPE_SLOW_FLAT);
    }

    @Override
    public @Nullable SulfurCubeArchetypeTrait fastFlat() {
        return with(ItemTags.SULFUR_CUBE_ARCHETYPE_FAST_FLAT);
    }

    @Override
    public @Nullable SulfurCubeArchetypeTrait light() {
        return with(ItemTags.SULFUR_CUBE_ARCHETYPE_LIGHT);
    }

    @Override
    public @Nullable SulfurCubeArchetypeTrait fastSliding() {
        return with(ItemTags.SULFUR_CUBE_ARCHETYPE_FAST_SLIDING);
    }

    @Override
    public @Nullable SulfurCubeArchetypeTrait slowSliding() {
        return with(ItemTags.SULFUR_CUBE_ARCHETYPE_SLOW_SLIDING);
    }

    @Override
    public @Nullable SulfurCubeArchetypeTrait sticky() {
        return with(ItemTags.SULFUR_CUBE_ARCHETYPE_STICKY);
    }

    @Override
    public @Nullable SulfurCubeArchetypeTrait highResistance() {
        return with(ItemTags.SULFUR_CUBE_ARCHETYPE_HIGH_RESISTANCE);
    }

    @Override
    public @Nullable SulfurCubeArchetypeTrait explosive() {
        return with(ItemTags.SULFUR_CUBE_ARCHETYPE_EXPLOSIVE);
    }

    @Override
    public @Nullable SulfurCubeArchetypeTrait hot() {
        return with(ItemTags.SULFUR_CUBE_ARCHETYPE_HOT);
    }

    @Override
    public @Nullable SulfurCubeArchetypeTrait with(TagKey<Item> archetypeTag) {
        if (!ModCore.isDatagen()) return null;
        return new Trait(archetypeTag);
    }

    @Override
    public @Nullable SulfurCubeArchetypeTrait notSwallowable() {
        // Not simply "return null": null would leave an inherited archetype (from a material bundle) in
        // place, since addTrait(null) is a no-op and never reaches the keepLatestOnly() de-duplication.
        // A tagless trait does reach it, and supersedes the earlier one without adding anything itself.
        if (!ModCore.isDatagen()) return null;
        return new Trait(null);
    }

    class Trait extends BlockTraitImpl.Generic implements SulfurCubeArchetypeTrait {
        private final @Nullable TagKey<Item> archetypeTag;

        Trait(@Nullable TagKey<Item> archetypeTag) {
            this.archetypeTag = archetypeTag;
        }

        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public boolean keepLatestOnly() {
            // A block is exactly one thing to a sulfur cube, so the latest archetype added wins and the
            // superseded one's configure() is dropped along with it (BlockDefinition.addTrait).
            return true;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            if (this.archetypeTag != null) {
                definition.addItemTags(this.archetypeTag);
            }
        }

        @Override
        public @Nullable TagKey<Item> archetypeTag() {
            return this.archetypeTag;
        }
    }
}
