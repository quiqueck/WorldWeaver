package org.betterx.wover.testmod.block;

import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.tag.api.predefined.CommonPoiTags;
import org.betterx.wover.testmod.entrypoint.TestModWoverBlock;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.PushReaction;

import org.jetbrains.annotations.ApiStatus;

public class TestBlockRegistry {
    private static final BlockRegistry R = BlockRegistry.forMod(TestModWoverBlock.C);

    private TestBlockRegistry() {
    }

    public static final TestBlock TEST_BLOCK = R.register(
            "test_block",
            new TestBlock(Block.Properties.of().ignitedByLava().instabreak().setId(R.key("test_block"))),
            CommonPoiTags.MASON_WORKSTATION
    );

    public static final TestDoorBlock TEST_DOOR = R.register(
            "test_door",
            new TestDoorBlock(
                    BlockSetType.COPPER, Block.Properties
                    .of()
                    .ignitedByLava()
                    .pushReaction(PushReaction.DESTROY)
                    .setId(R.key("test_door"))
            )
    );

    public static final TestWall TEST_WALL = R.register(
            "test_wall",
            new TestWall(Block.Properties.of().ignitedByLava().forceSolidOn()
                                         .setId(R.key("test_wall"))),
            BlockTags.WALLS
    );

    @ApiStatus.Internal
    public static void ensureStaticallyLoaded() {
        // NO-OP
    }
}
