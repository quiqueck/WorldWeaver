package de.ambertation.wover.testmod.block;

import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.impl.trait.behaviour.FlammableBlockBuilder;
import de.ambertation.wover.tag.api.predefined.CommonPoiTags;
import de.ambertation.wover.testmod.entrypoint.TestModWoverBlock;

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
            .addTrait(FlammableBlockBuilder.BUILDER.withDefault())
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
            .addTrait(FlammableBlockBuilder.BUILDER.withDefault())
            .addTags(BlockTags.DOORS)
            .addItemTags(ItemTags.DOORS)
            .pushReaction(PushReaction.DESTROY)
            .buildAndRegister();

    public static final TestWall TEST_WALL = R
            .defineDefaultBlockWithProps("test_wall", TestWall::new)
            .addTrait(FlammableBlockBuilder.BUILDER.withDefault())
            .addTags(BlockTags.WALLS)
            .addItemTags(ItemTags.WALLS)
            .forceSolidOn()
            .buildAndRegister();

    @ApiStatus.Internal
    public static void ensureStaticallyLoaded() {
        // NO-OP

        var traits = FlammableBlockBuilder.BUILDER.getRuntimeTraits(TEST_BLOCK);
        TestModWoverBlock.C.LOG.info("Trait: " + traits);
    }
}
