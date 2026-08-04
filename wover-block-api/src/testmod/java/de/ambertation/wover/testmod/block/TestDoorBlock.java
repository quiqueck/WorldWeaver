package de.ambertation.wover.testmod.block;

import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class TestDoorBlock extends DoorBlock {
    public TestDoorBlock(
            BlockSetType blockSetType,
            Properties properties
    ) {
        super(blockSetType, properties);
    }
}
