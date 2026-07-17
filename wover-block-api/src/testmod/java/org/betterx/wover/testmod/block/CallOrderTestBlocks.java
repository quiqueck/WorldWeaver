package org.betterx.wover.testmod.block;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.trait.BlockTraitKey;
import org.betterx.wover.block.impl.trait.BlockTraitImpl;
import org.betterx.wover.core.api.ModCore;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Consumer;
import org.jetbrains.annotations.ApiStatus;

/**
 * Probe blocks that pin down the <em>call-order</em> property/trait precedence contract that
 * WorldWeaver commit {@code 5dcd992} introduced (see {@code MIGRATION_1.21.7_STATUS.md}, "trap 0").
 * <p>
 * The rule under test: chain setters ({@code .strength(...)}, {@code .sound(...)}, ...) and
 * {@code addTrait(...)}'s configuration steps are applied <b>interleaved by call order</b>; the block
 * factory (constructor lambda) runs last and its {@code Properties} always wins; and
 * {@code replacePropertiesWithCopy(...)} is <b>eager</b>, so it is always the base, never an override.
 * <p>
 * The blocks are intentionally registered under a <b>dedicated namespace</b> ({@code wover-block-callorder})
 * rather than the {@code wover-block-testmod} namespace. Fabric's model/item datagen validation only
 * inspects blocks whose namespace equals the datagen mod id, so keeping these pure-logic probe blocks in
 * their own namespace means the existing {@code wover-block-testmod} datagen needs no model/loot files for
 * them and keeps passing unchanged. The blocks still register into {@code BuiltInRegistries.BLOCK} at mod
 * init exactly like any other block, so the runtime {@link Block#defaultDestroyTime()} /
 * {@link Block#getExplosionResistance()} / {@link Block#defaultMapColor()} / sound values the GameTest reads
 * are the real built values.
 */
public class CallOrderTestBlocks {
    /**
     * Dedicated namespace so these probe blocks are invisible to the {@code wover-block-testmod} datagen
     * validation (which filters by mod id).
     */
    public static final ModCore C = ModCore.create("wover-block-callorder");
    private static final BlockRegistry R = BlockRegistry.forMod(C);

    // Unmistakable, distinct numeric markers so a wrong precedence is obvious in the assertion output.
    public static final float CHAIN_STRENGTH = 1.0f;
    public static final float TRAIT_STRENGTH = 7.0f;
    public static final float CTOR_STRENGTH = 13.0f;
    // Blocks.OBSIDIAN: destroyTime 50, explosionResistance 1200 - the "copy base" marker.
    public static final float COPY_DESTROY_TIME = 50.0f;

    public static final MapColor TRAIT_MAP_COLOR = MapColor.COLOR_BLUE;
    public static final SoundType TRAIT_SOUND = SoundType.METAL;
    public static final SoundType EARLY_SOUND = SoundType.WOOL;
    public static final SoundType LATE_SOUND = SoundType.GRAVEL;

    /**
     * A trait whose {@link #configure(BlockDefinition)} runs an arbitrary action against the definition,
     * so a test can express "this trait sets strength 7" (or a sound/map color) at its call position.
     * A distinct {@link BlockTraitKey} keeps two such traits on the same block from de-duplicating each
     * other.
     */
    private static final class ConfigTrait extends BlockTraitImpl.Generic {
        private final BlockTraitKey key;
        private final Consumer<BlockDefinition<Block, ? extends BlockDefinition<Block, ?>>> action;

        private ConfigTrait(
                BlockTraitKey key,
                Consumer<BlockDefinition<Block, ? extends BlockDefinition<Block, ?>>> action
        ) {
            this.key = key;
            this.action = action;
        }

        @Override
        public BlockTraitKey key() {
            return key;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            action.accept(definition);
        }
    }

    // A trait that sets strength(7, 7). Stateless, so it is reused across several probe blocks.
    private static final ConfigTrait STRENGTH_TRAIT = new ConfigTrait(
            BlockTraitKey.ofUnique(C, "strength_trait"),
            def -> def.strength(TRAIT_STRENGTH, TRAIT_STRENGTH)
    );

    // A trait that sets both a sound and a map color, to probe interleaving with those accessors.
    private static final ConfigTrait SOUND_MAP_TRAIT = new ConfigTrait(
            BlockTraitKey.ofUnique(C, "sound_map_trait"),
            def -> def.sound(TRAIT_SOUND).mapColor(TRAIT_MAP_COLOR)
    );

    // Case 1: chain setter BEFORE the trait -> the trait (later in call order) wins. Expect destroyTime 7.
    public static final Block CASE1_TRAIT_AFTER_SETTER = R
            .defineDefaultBlockWithProps("case1_trait_after_setter", Block::new)
            .strength(CHAIN_STRENGTH, CHAIN_STRENGTH)
            .addTrait(STRENGTH_TRAIT)
            .buildAndRegister();

    // Case 2: chain setter AFTER the trait -> the chained strength (later in call order) wins. Expect 1.
    // This is the trap-0 regression guard: pre-5dcd992 the trait always won regardless of position.
    public static final Block CASE2_SETTER_AFTER_TRAIT = R
            .defineDefaultBlockWithProps("case2_setter_after_trait", Block::new)
            .addTrait(STRENGTH_TRAIT)
            .strength(CHAIN_STRENGTH, CHAIN_STRENGTH)
            .buildAndRegister();

    // Case 3: the block factory (constructor) sets strength(13) and runs LAST -> ctor wins over BOTH a
    // chain setter (1) and a trait (7). Expect destroyTime 13.
    public static final Block CASE3_CTOR_WINS = R
            .defineDefaultBlockWithProps(
                    "case3_ctor_wins",
                    props -> new Block(props.strength(CTOR_STRENGTH, CTOR_STRENGTH))
            )
            .strength(CHAIN_STRENGTH, CHAIN_STRENGTH)
            .addTrait(STRENGTH_TRAIT)
            .buildAndRegister();

    // Case 4: replacePropertiesWithCopy(OBSIDIAN) is the EAGER base; a later chain setter wins over it.
    // Expect destroyTime 1 (NOT obsidian's 50).
    public static final Block CASE4_COPY_IS_BASE = R
            .defineDefaultBlockWithProps("case4_copy_is_base", Block::new)
            .replacePropertiesWithCopy(Blocks.OBSIDIAN)
            .strength(CHAIN_STRENGTH, CHAIN_STRENGTH)
            .buildAndRegister();

    // Case 4b: replacePropertiesWithCopy(OBSIDIAN) as base, then a property-bearing trait applied after it
    // wins. Expect destroyTime 7 (NOT obsidian's 50) - the copy is a base, not an override over the trait.
    public static final Block CASE4B_TRAIT_AFTER_COPY = R
            .defineDefaultBlockWithProps("case4b_trait_after_copy", Block::new)
            .replacePropertiesWithCopy(Blocks.OBSIDIAN)
            .addTrait(STRENGTH_TRAIT)
            .buildAndRegister();

    // Case 5: interleave sound + map color. sound(WOOL) then trait{sound(METAL), map(BLUE)} then
    // sound(GRAVEL). Expect sound GRAVEL (last chain op wins) and map color BLUE (only the trait set it).
    public static final Block CASE5_SOUND_MAP_INTERLEAVE = R
            .defineDefaultBlockWithProps("case5_sound_map_interleave", Block::new)
            .sound(EARLY_SOUND)
            .addTrait(SOUND_MAP_TRAIT)
            .sound(LATE_SOUND)
            .buildAndRegister();

    private CallOrderTestBlocks() {
    }

    @ApiStatus.Internal
    public static void ensureStaticallyLoaded() {
        // NO-OP: referencing the class forces its static initializers (block registration) to run.
    }
}
