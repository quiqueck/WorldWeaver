package de.ambertation.wover.testmod.block;

import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.impl.trait.behaviour.FlammableBlockBuilder;
import de.ambertation.wover.tag.api.predefined.CommonPoiTags;
import de.ambertation.wover.testmod.entrypoint.TestModWoverBlock;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.PushReaction;

import org.jetbrains.annotations.ApiStatus;

public class TestBlockRegistry {
    private static final BlockRegistry R = BlockRegistry.forMod(TestModWoverBlock.C);

    /**
     * 26.3 dropped the {@code ItemTags.DOORS} constant (the same pruning that removed {@code ItemTags.SLABS}
     * and {@code ItemTags.TRAPDOORS}), but it still <em>ships</em> {@code data/minecraft/tags/item/doors.json}
     * - only the Java handle is gone, the tag itself is unchanged. Recreating the key keeps this testmod
     * exercising {@code addItemTags(...)} against a real vanilla item tag, so the generated
     * {@code data/minecraft/tags/item/doors.json} stays in the committed output instead of silently
     * disappearing. {@link ItemTags#WALLS} kept its constant and is used directly below.
     */
    private static final TagKey<Item> ITEM_TAG_DOORS =
            TagKey.create(Registries.ITEM, Identifier.withDefaultNamespace("doors"));

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
            .addItemTags(ITEM_TAG_DOORS)
            // 26.3 renamed the whole PushReaction enum: NORMAL/DESTROY/BLOCK/IGNORE/PUSH_ONLY became
            // PUSH_PULL/POPPED/IMMOVEABLE/IGNORE_ENTITY/PUSH. Pure renames - verified against
            // PistonBaseBlock#isPushable's switch, which maps the new constants to the exact same branches.
            .pushReaction(PushReaction.POPPED)
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
