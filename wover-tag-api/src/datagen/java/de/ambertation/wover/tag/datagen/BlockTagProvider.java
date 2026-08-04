package de.ambertation.wover.tag.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverTagProvider;
import de.ambertation.wover.tag.api.event.context.TagBootstrapContext;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;
import de.ambertation.wover.tag.api.predefined.CommonPoiTags;
import de.ambertation.wover.tag.api.predefined.MineableTags;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Set;

public class BlockTagProvider extends WoverTagProvider.ForBlocks {
    public BlockTagProvider(ModCore modCore) {
        super(
                modCore,
                List.of(modCore.namespace, modCore.modId, "c", "minecraft"),
                Set.of(
                        CommonBlockTags.NETHER_MYCELIUM,
                        CommonBlockTags.BARREL,
                        CommonBlockTags.WOODEN_CHEST,
                        CommonBlockTags.WOODEN_COMPOSTER,
                        CommonBlockTags.WOODEN_BARREL,
                        CommonBlockTags.WORKBENCHES,
                        MineableTags.NEEDS_GOLD_TOOL,
                        MineableTags.NEEDS_NETHERITE_TOOL,
                        MineableTags.NEEDS_WOOD_TOOL,
                        CommonBlockTags.MYCELIUM,
                        CommonBlockTags.END_STONES,
                        CommonBlockTags.NETHER_TERRAIN
                )
        );
    }

    @Override
    protected boolean initAll() {
        return true;
    }

    public void prepareTags(TagBootstrapContext<Block> ctx) {
        prepareBlockTags(ctx);
        preparePOITags(ctx);
    }

    public static void preparePOITags(TagBootstrapContext<Block> ctx) {

        ctx.add(CommonPoiTags.ARMORER_WORKSTATION, Blocks.BLAST_FURNACE);
        ctx.add(CommonPoiTags.BUTCHER_WORKSTATION, Blocks.SMOKER);
        ctx.add(CommonPoiTags.CARTOGRAPHER_WORKSTATION, Blocks.CARTOGRAPHY_TABLE);
        ctx.add(CommonPoiTags.CLERIC_WORKSTATION, Blocks.BREWING_STAND);
        ctx.add(CommonPoiTags.FARMER_WORKSTATION, CommonBlockTags.COMPOSTER, CommonBlockTags.WOODEN_COMPOSTER);
        ctx.add(CommonPoiTags.FISHERMAN_WORKSTATION, CommonBlockTags.BARREL, CommonBlockTags.WOODEN_BARREL);
        ctx.add(CommonPoiTags.FLETCHER_WORKSTATION, Blocks.FLETCHING_TABLE);
        ctx.add(CommonPoiTags.LEATHERWORKER_WORKSTATION, CommonBlockTags.CAULDRONS);
        ctx.add(CommonPoiTags.LIBRARIAN_WORKSTATION, Blocks.LECTERN);
        ctx.add(CommonPoiTags.MASON_WORKSTATION, Blocks.STONECUTTER);
        ctx.add(CommonPoiTags.SHEPHERD_WORKSTATION, Blocks.LOOM);
        ctx.add(CommonPoiTags.TOOLSMITH_WORKSTATION, Blocks.SMITHING_TABLE);
        ctx.add(CommonPoiTags.WEAPONSMITH_WORKSTATION, Blocks.GRINDSTONE);
        ctx.add(CommonPoiTags.HOME, CommonBlockTags.BEDS);
        ctx.add(CommonPoiTags.MEETING_PLACE, Blocks.BELL);
        ctx.add(CommonPoiTags.BEEHIVE, Blocks.BEEHIVE);
        ctx.add(CommonPoiTags.BEE_NEST, Blocks.BEE_NEST);
        ctx.add(CommonPoiTags.NETHER_PORTAL, Blocks.NETHER_PORTAL);
        ctx.add(CommonPoiTags.LODESTONE, Blocks.LODESTONE);
        ctx.add(CommonPoiTags.LIGHTNING_ROD, Blocks.LIGHTNING_ROD);

        ctx.addOptional(
                WoverTagDatagen.VILLAGER_JOB_SITES,
                CommonPoiTags.ARMORER_WORKSTATION,
                CommonPoiTags.BUTCHER_WORKSTATION,
                CommonPoiTags.CARTOGRAPHER_WORKSTATION,
                CommonPoiTags.CLERIC_WORKSTATION,
                CommonPoiTags.FARMER_WORKSTATION,
                CommonPoiTags.FISHERMAN_WORKSTATION,
                CommonPoiTags.FLETCHER_WORKSTATION,
                CommonPoiTags.LEATHERWORKER_WORKSTATION,
                CommonPoiTags.LIBRARIAN_WORKSTATION,
                CommonPoiTags.MASON_WORKSTATION,
                CommonPoiTags.SHEPHERD_WORKSTATION,
                CommonPoiTags.TOOLSMITH_WORKSTATION,
                CommonPoiTags.WEAPONSMITH_WORKSTATION
        );
    }

    public static void prepareBlockTags(TagBootstrapContext<Block> ctx) {

        ctx.add(MineableTags.HAMMER, net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE);
        ctx.add(CommonBlockTags.SCULK_LIKE, Blocks.SCULK);

        ctx.add(CommonBlockTags.END_STONES, Blocks.END_STONE);
        ctx.add(CommonBlockTags.NETHER_STONES, net.minecraft.tags.BlockTags.BASE_STONE_NETHER);

        ctx.add(
                CommonBlockTags.NETHERRACK,
                Blocks.NETHERRACK,
                Blocks.NETHER_QUARTZ_ORE,
                Blocks.NETHER_GOLD_ORE,
                Blocks.CRIMSON_NYLIUM,
                Blocks.WARPED_NYLIUM
        );

        ctx.add(CommonBlockTags.NETHER_ORES, Blocks.NETHER_QUARTZ_ORE, Blocks.NETHER_GOLD_ORE);
        ctx.add(CommonBlockTags.SOUL_GROUND, Blocks.SOUL_SAND, Blocks.SOUL_SOIL);

        ctx.add(CommonBlockTags.IS_OBSIDIAN, Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN);

        ctx.add(CommonBlockTags.MYCELIUM, Blocks.MYCELIUM);
        ctx.addOptional(CommonBlockTags.MYCELIUM, CommonBlockTags.NETHER_MYCELIUM);


        ctx.add(
                CommonBlockTags.TERRAIN,
                Blocks.MAGMA_BLOCK,
                Blocks.GRAVEL,
                Blocks.SAND,
                Blocks.RED_SAND,
                Blocks.GLOWSTONE,
                Blocks.BONE_BLOCK,
                Blocks.SCULK,
                Blocks.DIRT,
                Blocks.FARMLAND,
                Blocks.GRASS_BLOCK
        );

        ctx.add(
                CommonBlockTags.TERRAIN,
                net.minecraft.tags.BlockTags.DRIPSTONE_REPLACEABLE,
                net.minecraft.tags.BlockTags.BASE_STONE_OVERWORLD,
                net.minecraft.tags.BlockTags.NYLIUM
        );
        ctx.addOptional(
                CommonBlockTags.TERRAIN,
                CommonBlockTags.NETHER_TERRAIN,
                CommonBlockTags.MYCELIUM,
                CommonBlockTags.END_STONES
        );

        ctx.add(
                CommonBlockTags.NETHER_TERRAIN,
                Blocks.MAGMA_BLOCK,
                Blocks.GRAVEL,
                Blocks.RED_SAND,
                Blocks.GLOWSTONE,
                Blocks.BONE_BLOCK,
                Blocks.BLACKSTONE
        );
        ctx.add(
                CommonBlockTags.NETHER_TERRAIN,
                net.minecraft.tags.BlockTags.NYLIUM
        );
        ctx.addOptional(
                CommonBlockTags.NETHER_TERRAIN,
                CommonBlockTags.NETHERRACK,
                CommonBlockTags.NETHER_ORES,
                CommonBlockTags.SOUL_GROUND,
                CommonBlockTags.NETHER_MYCELIUM
        );

        ctx.add(CommonBlockTags.BOOKSHELVES, Blocks.BOOKSHELF);
        ctx.add(CommonBlockTags.CHEST, Blocks.CHEST);
        ctx.add(CommonBlockTags.COMPOSTER, Blocks.COMPOSTER);
        ctx.add(CommonBlockTags.WOODEN_COMPOSTER, Blocks.COMPOSTER);

        ctx.add(
                net.minecraft.tags.BlockTags.NETHER_CARVER_REPLACEABLES,
                Blocks.BASALT,
                Blocks.RED_SAND,
                Blocks.MAGMA_BLOCK,
                Blocks.SCULK
        );
        ctx.add(
                net.minecraft.tags.BlockTags.NETHER_CARVER_REPLACEABLES,
                CommonBlockTags.NETHER_STONES,
                CommonBlockTags.NETHERRACK
        );

        ctx.add(
                net.minecraft.tags.BlockTags.MINEABLE_WITH_AXE,
                CommonBlockTags.WOODEN_BARREL,
                CommonBlockTags.WOODEN_COMPOSTER,
                CommonBlockTags.WOODEN_CHEST,
                CommonBlockTags.WORKBENCHES
        );

        ctx.add(
                CommonBlockTags.WATER_PLANT,
                Blocks.KELP,
                Blocks.KELP_PLANT,
                Blocks.SEAGRASS,
                Blocks.TALL_SEAGRASS
        );
        ctx.add(
                CommonBlockTags.SAPLINGS,
                Blocks.OAK_SAPLING,
                Blocks.SPRUCE_SAPLING,
                Blocks.BIRCH_SAPLING,
                Blocks.JUNGLE_SAPLING,
                Blocks.ACACIA_SAPLING,
                Blocks.DARK_OAK_SAPLING,
                Blocks.CHERRY_SAPLING,
                Blocks.BAMBOO_SAPLING,
                Blocks.MANGROVE_PROPAGULE
        );
        ctx.add(CommonBlockTags.PLANT, CommonBlockTags.SAPLINGS, CommonBlockTags.VINE);
        ctx.add(
                CommonBlockTags.PLANT,
                Blocks.MANGROVE_LEAVES,
                Blocks.SHORT_GRASS,
                Blocks.FERN,
                Blocks.DANDELION,
                Blocks.TORCHFLOWER,
                Blocks.POPPY,
                Blocks.BLUE_ORCHID,
                Blocks.ALLIUM,
                Blocks.AZURE_BLUET,
                Blocks.RED_TULIP,
                Blocks.ORANGE_TULIP,
                Blocks.WHITE_TULIP,
                Blocks.PINK_TULIP,
                Blocks.OXEYE_DAISY,
                Blocks.CORNFLOWER,
                Blocks.WITHER_ROSE,
                Blocks.LILY_OF_THE_VALLEY,
                Blocks.WHEAT,
                Blocks.CACTUS,
                Blocks.SUGAR_CANE,
                Blocks.ATTACHED_PUMPKIN_STEM,
                Blocks.ATTACHED_MELON_STEM,
                Blocks.PUMPKIN_STEM,
                Blocks.MELON_STEM,
                Blocks.VINE,
                Blocks.LILY_PAD,
                Blocks.COCOA,
                Blocks.CARROTS,
                Blocks.POTATOES,
                Blocks.SUNFLOWER,
                Blocks.LILAC,
                Blocks.ROSE_BUSH,
                Blocks.PEONY,
                Blocks.TALL_GRASS,
                Blocks.LARGE_FERN,
                Blocks.TORCHFLOWER_CROP,
                Blocks.PITCHER_CROP,
                Blocks.PITCHER_PLANT,
                Blocks.BEETROOTS,
                Blocks.BAMBOO,
                Blocks.SWEET_BERRY_BUSH,
                Blocks.CAVE_VINES,
                Blocks.CAVE_VINES_PLANT,
                Blocks.SPORE_BLOSSOM,
                Blocks.AZALEA,
                Blocks.FLOWERING_AZALEA,
                Blocks.PINK_PETALS,
                Blocks.BIG_DRIPLEAF,
                Blocks.BIG_DRIPLEAF_STEM,
                Blocks.SMALL_DRIPLEAF
        );


        // BlockTags.DIRT already covers grass_block, podzol, mycelium, moss, mud, ...
        ctx.add(CommonBlockTags.SOIL, BlockTags.DIRT);
        // Vanilla soils are declared here on the tag owner (WoVer): a downstream mod's tag provider
        // is namespace-scoped and cannot add minecraft:* blocks to a wover:* tag. end_stone is the
        // End's vanilla ground, so it groups with the other vanilla soils.
        ctx.add(CommonBlockTags.SOIL, Blocks.FARMLAND, Blocks.END_STONE);

        ctx.add(CommonBlockTags.SOIL_OR_LOGS, BlockTags.LOGS, BlockTags.PLANKS);
        ctx.addOptional(CommonBlockTags.SOIL_OR_LOGS, CommonBlockTags.SOIL, CommonBlockTags.TERRAIN);

        ctx.addOptional(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, MineableTags.NEEDS_NETHERITE_TOOL);
        ctx.addOptional(BlockTags.INCORRECT_FOR_WOODEN_TOOL, MineableTags.NEEDS_GOLD_TOOL);

        ctx.add(CommonBlockTags.CAULDRONS, Blocks.LAVA_CAULDRON, Blocks.WATER_CAULDRON, Blocks.POWDER_SNOW_CAULDRON);
        ctx.add(
                CommonBlockTags.BEDS,
                Blocks.RED_BED,
                Blocks.BLACK_BED,
                Blocks.BLUE_BED,
                Blocks.BROWN_BED,
                Blocks.CYAN_BED,
                Blocks.GRAY_BED,
                Blocks.GREEN_BED,
                Blocks.LIGHT_BLUE_BED,
                Blocks.LIGHT_GRAY_BED,
                Blocks.LIME_BED,
                Blocks.MAGENTA_BED,
                Blocks.ORANGE_BED,
                Blocks.PINK_BED,
                Blocks.PURPLE_BED,
                Blocks.WHITE_BED,
                Blocks.YELLOW_BED
        );

        ctx.add(CommonBlockTags.ENCHANTING_MAGIC_SOURCE, CommonBlockTags.BOOKSHELVES);
    }
}
