package de.ambertation.wover.loot.mixin;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.function.Predicate;

/**
 * {@code LootPool.entries} is public but still {@code final} and still an immutable list, so adding an entry
 * to a pool that already exists means replacing the list.
 * <p>
 * This is what a "roll exactly one thing" table such as {@code minecraft:gameplay/piglin_bartering} needs: its
 * single weighted pool has to gain entries, because an additional pool would produce an additional stack on
 * every roll.
 * <p>
 * {@code LootPool.conditions} is a plain {@code List<LootItemCondition>}, ANDed together once at construction
 * time into the cached {@code compositeCondition} predicate that {@code addRandomItems} actually tests -
 * replacing {@code conditions} alone would leave that cache stale and any added condition would silently never
 * apply. {@link #wover_setCompositeCondition(Predicate)} exists so callers can recompute and write both
 * together (see {@code net.minecraft.util.Util#allOf(List)}, which is how the constructor derives it).
 */
@Mixin(LootPool.class)
public interface LootPoolAccessor {
    @Mutable
    @Accessor("entries")
    void wover_setEntries(List<LootPoolEntryContainer> entries);

    @Accessor("conditions")
    List<LootItemCondition> wover_getConditions();

    @Mutable
    @Accessor("conditions")
    void wover_setConditions(List<LootItemCondition> conditions);

    @Mutable
    @Accessor("compositeCondition")
    void wover_setCompositeCondition(Predicate<LootContext> compositeCondition);
}
