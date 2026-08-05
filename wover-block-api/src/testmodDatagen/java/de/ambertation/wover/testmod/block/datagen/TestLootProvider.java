package de.ambertation.wover.testmod.block.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.WoverLootTableProvider;
import de.ambertation.wover.loot.api.LootLookupProvider;
import de.ambertation.wover.loot.api.LootTableManager;
import de.ambertation.wover.testmod.block.TestBlock;
import de.ambertation.wover.testmod.block.TestBlockRegistry;

import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;

/**
 * Datagen provider for the block testmod's block loot tables.
 *
 * <p>Extends the wover loot datagen base ({@link WoverLootTableProvider}), and uses the wover loot API
 * ({@link LootLookupProvider} helpers plus the vanilla loot builder classes) to build the tables. Registered
 * alongside {@link TestModelProvider} in {@link TestModWoverBlockAndItemDatagen}.
 *
 * <p>Generates:
 * <ul>
 *     <li>{@code wover-block-testmod:blocks/test_block} - a silk-touch / fortune "ore/crop-style" drop table
 *     gated on {@link TestBlock#AGE} (now {@code 0..2}).</li>
 *     <li>{@code wover-block-testmod:blocks/test_door} - a door table that only drops on the
 *     {@link DoubleBlockHalf#LOWER} half, via {@link LootLookupProvider#dropWithSilkTouchAndCondition}.</li>
 * </ul>
 */
public class TestLootProvider extends WoverLootTableProvider {
    public TestLootProvider(ModCore modCore) {
        super(modCore, LootContextParamSets.BLOCK);
    }

    @Override
    protected void boostrap(
            HolderLookup.@NotNull Provider lookup,
            @NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer
    ) {
        final LootLookupProvider provider = new LootLookupProvider(lookup);

        biConsumer.accept(blockLootKey(TestBlockRegistry.TEST_BLOCK), buildTestBlockTable(provider));

        // Door only drops from its lower half. dropWithSilkTouchAndCondition(block, property, value) builds a
        // single silk-touch pool whose entry is gated on the given block-state property.
        biConsumer.accept(
                blockLootKey(TestBlockRegistry.TEST_DOOR),
                provider.dropWithSilkTouchAndCondition(
                        TestBlockRegistry.TEST_DOOR,
                        DoorBlock.HALF,
                        DoubleBlockHalf.LOWER
                )
        );
    }

    private static ResourceKey<LootTable> blockLootKey(Block block) {
        return LootTableManager.getBlockLootTableKey(
                BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow()
        );
    }

    /**
     * Builds the {@code test_block} table, reproducing the committed golden's spec adapted to the current age
     * range ({@code age: 0..2}, max age {@code 2}):
     * <ul>
     *     <li>a silk-touch pool that drops the block, with a per-age fortune {@code apply_bonus} curve
     *     ({@code ore_drops} at the max age, descending {@code uniform_bonus_count} multipliers below it),
     *     {@code uniform 1..3} count at the max age (and {@code 1} otherwise), plus {@code explosion_decay};</li>
     *     <li>a non-silk-touch pool that, only at the max age, drops {@code uniform 1..3} with
     *     {@code explosion_decay}.</li>
     * </ul>
     */
    private LootTable.Builder buildTestBlockTable(LootLookupProvider provider) {
        final Block block = TestBlockRegistry.TEST_BLOCK;
        final int maxAge = 2;

        final LootPool.Builder silkTouchPool = LootPool
                .lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .when(provider.hasSilkTouch())
                .add(LootItem
                        .lootTableItem(block)
                        // count uniform 1..3 at the max age, otherwise a single item
                        .apply(SetItemCountFunction
                                .setCount(UniformGenerator.between(1.0F, 3.0F))
                                .when(hasAge(block, maxAge)))
                        .apply(SetItemCountFunction
                                .setCount(ConstantValue.exactly(1.0F))
                                .when(hasAge(block, maxAge).invert()))
                        // fortune bonus curve, strongest at the max age
                        .apply(ApplyBonusCount
                                .addOreBonusCount(provider.fortune())
                                .when(hasAge(block, maxAge)))
                        .apply(ApplyBonusCount
                                .addUniformBonusCount(provider.fortune(), 2)
                                .when(hasAge(block, maxAge - 1)))
                        .apply(ApplyBonusCount
                                .addUniformBonusCount(provider.fortune(), 1)
                                .when(hasAge(block, maxAge - 2)))
                        .apply(ApplyExplosionDecay.explosionDecay()));

        final LootPool.Builder noSilkTouchPool = LootPool
                .lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .when(AllOfCondition.allOf(provider.hasSilkTouch().invert(), hasAge(block, maxAge)))
                .add(LootItem
                        .lootTableItem(block)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                        .apply(ApplyExplosionDecay.explosionDecay()));

        return LootTable.lootTable().withPool(silkTouchPool).withPool(noSilkTouchPool);
    }

    private static LootItemCondition.Builder hasAge(Block block, int age) {
        return LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(block)
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(TestBlock.AGE, age));
    }
}
