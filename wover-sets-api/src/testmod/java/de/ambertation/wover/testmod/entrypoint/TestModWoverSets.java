package de.ambertation.wover.testmod.entrypoint;

import de.ambertation.wover.block.api.trait.SignBlockDefinition;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.sets.api.blocks.SlotType;
import de.ambertation.wover.tabs.api.CreativeTabs;
import de.ambertation.wover.testmod.sets.TestEquipmentSet;
import de.ambertation.wover.testmod.sets.TestStoneSet;
import de.ambertation.wover.testmod.sets.TestWoodSet;

import net.fabricmc.api.ModInitializer;

public class TestModWoverSets implements ModInitializer {
    // ModCore for the TestMod. TestMod's do not share the wover namespace,
    // but (like other Mods that include Wover) have a unique one
    public static final ModCore C = ModCore.create("wover-sets-testmod");
    public static SignBlockDefinition.SignType sign;
    public static TestWoodSet woodBlockSet;

    @Override
    public void onInitialize() {
        TestEquipmentSet.ensureStaticInit();
        woodBlockSet = new TestWoodSet().buildAndRegister();
        var stoneBlockSet = new TestStoneSet().buildAndRegister();

//        sign = new SignBlockDefinition(
//                BlockRegistry.forMod(C),
//                "wooden_hanging_sign",
//                "wooden_hanging_wall_sign",
//                (def) -> new CeilingHangingSignBlock(woodBlockSet.woodType(), def.getProperties()),
//                (def) -> new WallHangingSignBlock(woodBlockSet.woodType(), def.getProperties()),
//                HangingSignItem::new
//        )
//                .addTrait(BlockTraits.VALID_BLOCK_ENTITY.with(BlockEntityType.HANGING_SIGN))
//                .buildAndRegisterSign();

//        var boat = ItemRegistry
//                .forMod(C)
//                .defineBoatItem("wooden_boat")
//                .addTrait(ClientBlockTraits.BOAT_RENDERER.withDefault())
//                .buildAndRegisterBoat();

        CreativeTabs.start(C)
                    .createTab("all")
                    .setIcon(woodBlockSet.getBlock(SlotType.LOG))
                    .buildAndAdd()
                    .processRegistries()
                    .registerAllTabs();
    }
}