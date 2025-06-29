package org.betterx.wover.testmod.block;

import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.tag.api.predefined.CommonPoiTags;
import org.betterx.wover.testmod.entrypoint.TestModWoverBlock;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.PushReaction;

import org.jetbrains.annotations.ApiStatus;

public class TestBlockRegistry {
    private static final BlockRegistry R = BlockRegistry.forMod(TestModWoverBlock.C);

    private TestBlockRegistry() {
    }

    public static final TestBlock TEST_BLOCK = R
            .defineDefaultBlockWithProps("test_block", TestBlock::new)
            .addTrait(BlockTraits.FLAMMABLE.withDefault())
            .addTags(CommonPoiTags.MASON_WORKSTATION)
            .instabreak()
            .buildAndRegister();

    public static final TestDoorBlock TEST_DOOR = R
            .defineDefaultBlockWithProps(
                    "test_door", (props) -> new TestDoorBlock(
                            BlockSetType.COPPER,
                            props
                    )
            )
            .addTrait(BlockTraits.FLAMMABLE.withDefault())
            .addTags(BlockTags.DOORS)
            .addItemTags(ItemTags.DOORS)
            .pushReaction(PushReaction.DESTROY)
            .buildAndRegister();

    public static final TestWall TEST_WALL = R
            .defineDefaultBlockWithProps("test_wall", TestWall::new)
            .addTrait(BlockTraits.FLAMMABLE.withDefault())
            .addTags(BlockTags.WALLS)
            .addItemTags(ItemTags.WALLS)
            .forceSolidOn()
            .buildAndRegister();

    @ApiStatus.Internal
    public static void ensureStaticallyLoaded() {
        // NO-OP

        var traits = BlockTraits.FLAMMABLE.getRuntimeTraits(TEST_BLOCK);
        TestModWoverBlock.C.LOG.info("Trait: " + traits);
    }
}
