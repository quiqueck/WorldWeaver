package de.ambertation.wover.loot.mixin;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

/**
 * {@code LootTable.pools} is a {@code private final List<LootPool>} with a private constructor, so appending a
 * pool to a table the datapacks already built is only possible by replacing the list outright.
 */
@Mixin(LootTable.class)
public interface LootTableAccessor {
    @Accessor("pools")
    List<LootPool> wover_getPools();

    @Mutable
    @Accessor("pools")
    void wover_setPools(List<LootPool> pools);
}
