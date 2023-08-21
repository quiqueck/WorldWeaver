package org.betterx.wover.tag.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverTagProvider;
import org.betterx.wover.tag.api.event.context.TagBootstrapContext;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;
import org.betterx.wover.tag.api.predefined.CommonPoiTags;
import org.betterx.wover.tag.api.predefined.MineableTags;

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
                        CommonBlockTags.WORKBENCHES
                )
        );
    }

    @Override
    protected boolean initAll() {
        return true;
    }

    protected void prepareTags(TagBootstrapContext<Block> ctx) {
        prepareBlockTags(ctx);
        preparePOITags(ctx);
    }

    public static void preparePOITags(TagBootstrapContext<Block> ctx) {
        ctx.add(
                CommonPoiTags.FISHERMAN_WORKSTATION,
                CommonBlockTags.BARREL,
                CommonBlockTags.WOODEN_BARREL
        );
        ctx.add(CommonPoiTags.FARMER_WORKSTATION, Blocks.COMPOSTER);
    }

    public static void prepareBlockTags(TagBootstrapContext<Block> ctx) {
        ctx.add(MineableTags.HAMMER, net.minecraft.tags.BlockTags.MINEABLE_WITH_PICKAXE);
        ctx.add(CommonBlockTags.SCULK_LIKE, Blocks.SCULK);
        ctx.add(CommonBlockTags.DRAGON_IMMUNE, net.minecraft.tags.BlockTags.DRAGON_IMMUNE);

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
        ctx.add(CommonBlockTags.MYCELIUM, CommonBlockTags.NETHER_MYCELIUM);


        ctx.add(
                CommonBlockTags.TERRAIN,
                Blocks.MAGMA_BLOCK,
                Blocks.GRAVEL,
                Blocks.SAND,
                Blocks.RED_SAND,
                Blocks.GLOWSTONE,
                Blocks.BONE_BLOCK,
                Blocks.SCULK
        );
        ctx.add(
                CommonBlockTags.TERRAIN,
                CommonBlockTags.NETHER_TERRAIN,
                net.minecraft.tags.BlockTags.DRIPSTONE_REPLACEABLE,
                net.minecraft.tags.BlockTags.BASE_STONE_OVERWORLD,
                net.minecraft.tags.BlockTags.NYLIUM,
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
                CommonBlockTags.NETHERRACK,
                net.minecraft.tags.BlockTags.NYLIUM,
                CommonBlockTags.NETHER_ORES,
                CommonBlockTags.SOUL_GROUND,
                CommonBlockTags.NETHER_MYCELIUM
        );

        ctx.add(CommonBlockTags.BOOKSHELVES, Blocks.BOOKSHELF);
        ctx.add(CommonBlockTags.CHEST, Blocks.CHEST);

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
        ctx.add(CommonBlockTags.PLANT, CommonBlockTags.SAPLINGS);
        ctx.add(
                CommonBlockTags.PLANT,
                Blocks.MANGROVE_LEAVES,
                Blocks.GRASS,
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

        ctx.add(CommonBlockTags.GROWS_GRASS, BlockTags.DIRT, CommonBlockTags.TERRAIN, BlockTags.LOGS, BlockTags.PLANKS);
    }
}
