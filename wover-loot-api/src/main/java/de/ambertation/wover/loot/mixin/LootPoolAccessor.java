package de.ambertation.wover.loot.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Optional;

/**
 * {@code LootPool.entries} is public in 26.3, but still {@code final} and still an immutable list, so adding
 * an entry to a pool that already exists means replacing the list.
 * <p>
 * This is what a "roll exactly one thing" table such as {@code minecraft:gameplay/piglin_bartering} needs:
 * its single weighted pool has to gain entries, because an additional pool would produce an additional stack
 * on every roll.
 * <p>
 * {@code LootPool.condition} is the {@code Optional<Holder<LootItemCondition>>} that
 * {@code LootPool.Builder#when(...)} collapses its list of conditions into via
 * {@code ConditionUserBuilder.buildCondition} - 26.3 no longer keeps a raw list on the built pool. Restricting
 * an existing pool therefore means reading this, combining it with the new condition, and writing the result
 * back the same way the builder would have.
 */
@Mixin(LootPool.class)
public interface LootPoolAccessor {
    @Mutable
    @Accessor("entries")
    void wover_setEntries(List<LootPoolEntryContainer> entries);

    @Accessor("condition")
    Optional<Holder<LootItemCondition>> wover_getCondition();

    @Mutable
    @Accessor("condition")
    void wover_setCondition(Optional<Holder<LootItemCondition>> condition);
}
