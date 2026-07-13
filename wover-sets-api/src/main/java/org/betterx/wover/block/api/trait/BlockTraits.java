package org.betterx.wover.block.api.trait;

import org.betterx.wover.block.api.trait.behaviour.*;
import org.betterx.wover.block.api.trait.type.BarkBlockTrait;
import org.betterx.wover.block.api.trait.type.LogBlockTrait;
import org.betterx.wover.block.impl.trait.BlockRecipeTraitBuilder;
import org.betterx.wover.block.impl.trait.behaviour.*;
import org.betterx.wover.block.impl.trait.material.MetalMaterialBuilder;
import org.betterx.wover.block.impl.trait.material.ObsidianMaterialBuilder;
import org.betterx.wover.block.impl.trait.material.StoneMaterialBuilder;
import org.betterx.wover.block.impl.trait.material.WoodMaterialBuilder;
import org.betterx.wover.block.impl.trait.type.*;

/**
 * Central registry of every ready-made {@link org.betterx.wover.block.api.trait.BlockTrait} builder shipped by
 * {@code wover-sets-api}, mirroring the {@code BlockTraits}-style constant collections used across WoVer's
 * trait system.
 * <p>
 * Pass any of these constants to
 * {@link org.betterx.wover.block.api.BlockDefinition#addTrait(org.betterx.wover.block.api.trait.BlockTrait)}
 * (or, for the {@code BuilderWithDefault(s)} entries, call {@code .withDefault()}/{@code .withDefault(...)} on
 * them first) while configuring a block. The "type" traits ({@link #BARK_BLOCK}, {@link #LOG_BLOCK},
 * {@link #DOOR_BLOCK}, {@link #STAIR_BLOCK}, ...) are what the slot classes in
 * {@code org.betterx.wover.sets.api.blocks.types} use to configure the blocks they build.
 */
public class BlockTraits {
    /** Registers a block with Fabric's {@code FlammableBlockRegistry} once it exists. */
    public static final FlammableBlockTrait.Builder FLAMMABLE = FlammableBlockBuilder.BUILDER;
    /** Marks a block as strippable (e.g. logs/barks) and supplies the resulting stripped {@link net.minecraft.world.level.block.state.BlockState}. */
    public static final StripableBlockTrait.Builder STRIPABLE = StripableBlockBuilder.BUILDER;
    /** Marks a block as a valid placement target for a given {@link net.minecraft.world.level.block.entity.BlockEntityType}. */
    public static final ValidForBlockEntityTypeTrait.Builder VALID_BLOCK_ENTITY = ValidForBlockEntityTypeBuilder.BUILDER;
    /** Attaches a code-driven loot table factory to a block, see {@link LootTableTrait}. */
    public static final LootTableTrait.Builder LOOT_TABLE = LootTableTraitBuilder.BUILDER;
    /** Marks a block as a magic light/particle source. */
    public static final GenericBlockTrait.BuilderWithDefault MAGIC_SOURCE = MagicSourceTrait.BUILDER;
    /** Makes a block climbable, like a vanilla ladder or vine. */
    public static final GenericBlockTrait.BuilderWithDefault CLIMBABLE = ClimbableBlockTraitBuilder.BUILDER;

    // Material Traits
    /** Marks a block as belonging to the "stone" material family (used by {@link org.betterx.wover.sets.api.blocks.types.Button} to pick timings, recipe groups, ...). */
    public static final GenericBlockTrait.BuilderWithDefaults STONE_BLOCK = StoneMaterialBuilder.BUILDER;
    /** Marks a block as belonging to the "metal" material family. */
    public static final GenericBlockTrait.BuilderWithDefaults METAL_BLOCK = MetalMaterialBuilder.BUILDER;
    /** Marks a block as belonging to the "wood" material family (used to pick recipe groups/model shapes throughout {@code sets.api.blocks.types}). */
    public static final GenericBlockTrait.BuilderWithDefaults WOOD_BLOCK = WoodMaterialBuilder.BUILDER;
    /** Marks a block as belonging to the "obsidian" material family. */
    public static final GenericBlockTrait.BuilderWithDefaults OBSIDIAN_BLOCK = ObsidianMaterialBuilder.BUILDER;

    // Tab-Based traits
    /** Adds one or more {@code minecraft:mineable/...}-style tool tags to a block. */
    public static final MineableWithTagTrait.Builder MINEABLE_WITH = MineableWithTagBuilder.BUILDER;
    /** Adds one or more arbitrary block tags to a block, see {@link BlockTagTrait}. */
    public static final BlockTagTrait.Builder BLOCK_TAG = BlockTagTraitBuilder.BUILDER;

    // Type traits
    /** Configures a block as the "bark"/"stripped bark" member of a wood set, see {@link BarkBlockTrait}. */
    public static final BarkBlockTrait.Builder BARK_BLOCK = BarkBlockBuilder.BUILDER;
    /** Configures a block as a barrel. */
    public static final GenericBlockTrait.BuilderWithDefaults BARREL_BLOCK = BarrelBlockBuilder.BUILDER;
    /** Configures a block as the "log"/"stripped log" member of a wood set, see {@link LogBlockTrait}. */
    public static final LogBlockTrait.Builder LOG_BLOCK = LogBlockBuilder.BUILDER;
    /** Configures a block as a planks-like block. */
    public static final GenericBlockTrait.BuilderWithDefaults PLANK_BLOCK = PlankBlockBuilder.BUILDER;
    /** Configures a block as a slab. */
    public static final GenericBlockTrait.BuilderWithDefaults SLAB_BLOCK = SlabBlockBuilder.BUILDER;
    /** Configures a block as a bookshelf. */
    public static final GenericBlockTrait.BuilderWithDefaults BOOK_SHELF = BookshelfBlockBuilder.BUILDER;
    /** Configures a block as a button. */
    public static final GenericBlockTrait.BuilderWithDefaults BUTTON_BLOCK = ButtonBlockBuilder.BUILDER;
    /** Configures a block as a chest. */
    public static final GenericBlockTrait.BuilderWithDefaults CHEST_BLOCK = ChestBlockBuilder.BUILDER;
    /** Configures a block as a composter. */
    public static final GenericBlockTrait.BuilderWithDefaults COMPOSTER_BLOCK = ComposterBlockBuilder.BUILDER;
    /** Configures a block as a crafting table. */
    public static final GenericBlockTrait.BuilderWithDefaults CRAFTING_TABLE_BLOCK = CraftingTableBlockBuilder.BUILDER;
    /** Configures a block as a door. */
    public static final GenericBlockTrait.BuilderWithDefaults DOOR_BLOCK = DoorBlockBuilder.BUILDER;
    /** Configures a block as a fence. */
    public static final GenericBlockTrait.BuilderWithDefaults FENCE_BLOCK = FenceBlockBuilder.BUILDER;
    /** Configures a block as a fence gate. */
    public static final GenericBlockTrait.BuilderWithDefaults FENCE_GATE_BLOCK = GateBlockBuilder.BUILDER;
    /** Configures a block as a hanging sign. */
    public static final GenericBlockTrait.BuilderWithDefaults HANGING_SIGN_BLOCK = HangingSignBlockBuilder.BUILDER;
    /** Configures a block as a (standing/wall) sign. */
    public static final GenericBlockTrait.BuilderWithDefaults SIGN_BLOCK = SignBlockBuilder.BUILDER;
    /** Configures a block as a ladder. */
    public static final GenericBlockTrait.BuilderWithDefaults LADDER_BLOCK = LadderBlockBuilder.BUILDER;
    /** Configures a block as a pressure plate. */
    public static final GenericBlockTrait.BuilderWithDefaults PRESSURE_PLATE_BLOCK = PressurePlateBlockBuilder.BUILDER;
    /** Configures a block as a set of stairs. */
    public static final GenericBlockTrait.BuilderWithDefaults STAIR_BLOCK = StairsBlockBuilder.BUILDER;
    /** Configures a block as a trapdoor. */
    public static final GenericBlockTrait.BuilderWithDefaults TRAPDOOR_BLOCK = TrapdoorBlockBuilder.BUILDER;
    /** Configures a block as a wall. */
    public static final GenericBlockTrait.BuilderWithDefaults WALL_BLOCK = WallBlockBuilder.BUILDER;
    /** Configures a block as a rotated pillar. */
    public static final GenericBlockTrait.BuilderWithDefaults PILLAR_BLOCK = PillarBlockBuilder.BUILDER;

    //Recipe Traits
    /** Attaches a code-driven recipe factory to a block, generated automatically during recipe datagen. */
    public static final BlockRecipeTrait.Builder RECIPE = BlockRecipeTraitBuilder.BUILDER;
}
