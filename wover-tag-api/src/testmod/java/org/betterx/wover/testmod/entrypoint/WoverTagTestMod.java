package org.betterx.wover.testmod.entrypoint;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.tag.api.TagManager;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import net.fabricmc.api.ModInitializer;

public class WoverTagTestMod implements ModInitializer {
    // ModCore for the TestMod. TestMod's do not share the wover namespace,
    // but (like other Mods that include Wover) have a unique one
    public static final ModCore C = ModCore.create("wover-tag-testmod");

    @Override
    public void onInitialize() {
        TagKey<Block> aa = TagManager.BLOCKS.makeTag(C, "aa");
        var aaa = TagManager.BLOCKS.makeTag(C, "aa");
        var bb = TagManager.BLOCKS.makeTag(C, "bb");
        var cc = TagManager.BLOCKS.makeTag(C, "cc");

        TagManager.BLOCKS.bootstrapEvent().subscribe(ctx -> {
            ctx.add(aa, Blocks.ACACIA_BUTTON);
            ctx.add(aa, Blocks.ACACIA_DOOR);

            ctx.add(aa, Blocks.ACACIA_BUTTON);
            ctx.addOptional(aa, Blocks.ACACIA_FENCE);

            ctx.add(bb, Blocks.ACACIA_BUTTON);
            ctx.addOptional(bb, Blocks.ACACIA_BUTTON);

            ctx.addOptional(cc, Blocks.ACACIA_BUTTON);
            ctx.add(cc, Blocks.ACACIA_BUTTON);

            C.log.info(ctx.toString());
        });
    }
}