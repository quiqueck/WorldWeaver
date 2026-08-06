package de.ambertation.wover.block.api.trait.behaviour;

import de.ambertation.wover.block.api.trait.GenericBlockTrait;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import org.jetbrains.annotations.Nullable;

/**
 * A {@link GenericBlockTrait} that puts a block's item into one of vanilla's
 * {@code #minecraft:sulfur_cube_archetype/...} item tags - the tags a sulfur cube consults when it decides
 * what it may swallow, and which physics profile it adopts once it has.
 * <p>
 * {@code SulfurCube} gates swallowing on a single tag, {@code #minecraft:sulfur_cube_swallowable}, which is
 * nothing but the union of the twelve archetype tags; the matching {@code SulfurCubeArchetype} entries then
 * supply the attribute modifiers, knockback, sounds, buoyancy and (for {@link Builder#explosive()}) the
 * explosion data. So a block becomes edible <em>and</em> gets its behaviour from the same single tag entry -
 * there is nothing else to register, and no block tag is involved anywhere.
 * <p>
 * Because a block may only ever be one thing to a sulfur cube, this trait is
 * {@link de.ambertation.wover.block.api.trait.BlockTrait#keepLatestOnly() keepLatestOnly}: the last
 * {@code addTrait(SULFUR_CUBE_ARCHETYPE...)} wins and the superseded one contributes nothing. That is what
 * lets a material bundle carry a sensible default ({@code METAL_BLOCK} → {@link Builder#slowFlat()},
 * {@code STONE_BLOCK} → {@link Builder#slowBouncy()}, ...) while an individual block overrides it:
 * <pre>{@code
 * .addTrait(NetherMaterial.stone())                      // would be slow_bouncy
 * .addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.hot())     // ... but this one runs hot
 * }</pre>
 * {@link Builder#notSwallowable()} is the same lever pointed the other way: it overrides an inherited
 * archetype with nothing at all, for a block that a cube should not be able to eat.
 * <p>
 * Like every tag trait this is datagen-only - the builder returns {@code null} outside datagen and
 * {@code addTrait(null)} is a no-op, so both the default and the override disappear together at runtime.
 */
public interface SulfurCubeArchetypeTrait extends GenericBlockTrait {
    /**
     * Builds {@link SulfurCubeArchetypeTrait} instances, one method per vanilla archetype.
     * <p>
     * The names mirror the vanilla archetype ids; the parenthesised blocks are what vanilla itself puts in
     * that tag, as the reference point for classifying a modded block.
     */
    interface Builder extends GenericBlockTrait.Builder {
        /** @return the {@code regular} archetype (dirt, clay, mud, concrete powder, coal/bone blocks) */
        @Nullable SulfurCubeArchetypeTrait regular();

        /** @return the {@code bouncy} archetype (planks, logs, bamboo blocks) */
        @Nullable SulfurCubeArchetypeTrait bouncy();

        /** @return the {@code slow_bouncy} archetype (stone and its whole decorative family, ores, obsidian, glowstone) */
        @Nullable SulfurCubeArchetypeTrait slowBouncy();

        /** @return the {@code slow_flat} archetype (metal blocks, metal ores, copper in all its states) */
        @Nullable SulfurCubeArchetypeTrait slowFlat();

        /** @return the {@code fast_flat} archetype (coral, sponge, moss, melon/pumpkin, hay, froglights) */
        @Nullable SulfurCubeArchetypeTrait fastFlat();

        /** @return the {@code light} archetype (wool) */
        @Nullable SulfurCubeArchetypeTrait light();

        /** @return the {@code fast_sliding} archetype (packed/blue ice, snow blocks) */
        @Nullable SulfurCubeArchetypeTrait fastSliding();

        /** @return the {@code slow_sliding} archetype (mushroom blocks, mycelium, wart blocks, shroomlight) */
        @Nullable SulfurCubeArchetypeTrait slowSliding();

        /** @return the {@code sticky} archetype (honeycomb block) */
        @Nullable SulfurCubeArchetypeTrait sticky();

        /** @return the {@code high_resistance} archetype (soul sand, soul soil) */
        @Nullable SulfurCubeArchetypeTrait highResistance();

        /** @return the {@code explosive} archetype (TNT) - the cube gains a fuse and a power-3 explosion */
        @Nullable SulfurCubeArchetypeTrait explosive();

        /** @return the {@code hot} archetype (magma block) */
        @Nullable SulfurCubeArchetypeTrait hot();

        /**
         * A trait for an arbitrary archetype tag, for a mod that registers its own
         * {@code SulfurCubeArchetype} (the registry is datapack-driven, so
         * {@code data/<mod>/sulfur_cube_archetype/<name>.json} plus its {@code "items"} tag is all it takes).
         * <p>
         * A custom tag must be reachable from {@code #minecraft:sulfur_cube_swallowable} - either by pointing
         * the archetype at one of the vanilla tags, or by adding the custom tag to {@code swallowable}
         * yourself - or the cube will never pick the block up in the first place.
         *
         * @param archetypeTag the item tag to add the block's item to
         * @return the new trait
         */
        @Nullable SulfurCubeArchetypeTrait with(TagKey<Item> archetypeTag);

        /**
         * A trait that adds no tag at all. Only useful to <em>cancel</em> an archetype inherited from a
         * material bundle (see the class javadoc on {@code keepLatestOnly}), for a block a sulfur cube has no
         * business eating.
         *
         * @return the new trait
         */
        @Nullable SulfurCubeArchetypeTrait notSwallowable();
    }

    /**
     * @return the {@link ItemTags#SULFUR_CUBE_SWALLOWABLE swallowable} sub-tag this trait adds the block's
     * item to, or {@code null} for {@link Builder#notSwallowable()}
     */
    @Nullable TagKey<Item> archetypeTag();
}
