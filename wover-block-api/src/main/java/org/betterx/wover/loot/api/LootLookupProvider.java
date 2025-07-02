package org.betterx.wover.loot.api;

import org.betterx.wover.tag.api.predefined.CommonItemTags;

import net.minecraft.advancements.critereon.*;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.predicates.DataComponentPredicates;
import net.minecraft.core.component.predicates.EnchantmentsPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class LootLookupProvider {
    public static final float[] VANILLA_LEAVES_STICK_CHANCES = new float[]{
            0.02F,
            0.022222223F,
            0.025F,
            0.033333335F,
            0.1F
    };
    public static final float[] VANILLA_LEAVES_SAPLING_CHANCES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};

    public LootItemCondition.Builder hasSilkTouch() {
        return vanillaBlockLoot.hasSilkTouch();
    }

    public record DropInfo(ItemLike item, NumberProvider numberProvider) {
        public DropInfo(ItemLike item, int count) {
            this(item, ConstantValue.exactly(count));
        }
    }

    public final HolderLookup.Provider provider;
    private final VanillaBlockLoot vanillaBlockLoot;
    public final HolderLookup.RegistryLookup<Enchantment> enchantmentLookup;

    public LootLookupProvider(HolderLookup.Provider provider) {
        this.vanillaBlockLoot = new VanillaBlockLoot(provider);
        this.provider = provider;
        this.enchantmentLookup = provider.lookupOrThrow(Registries.ENCHANTMENT);
    }

    public HolderLookup.RegistryLookup<Item> itemLookup() {
        return provider.lookupOrThrow(Registries.ITEM);
    }

    public Holder<Enchantment> enchantment(ResourceKey<Enchantment> key) {
        return this.enchantmentLookup.getOrThrow(key);
    }

    public Holder<Enchantment> fortune() {
        return enchantment(Enchantments.FORTUNE);
    }

    public Holder<Enchantment> silkTouch() {
        return enchantment(Enchantments.SILK_TOUCH);
    }

    public HolderLookup.Provider getProvider() {
        return provider;
    }

    public LootItemCondition.Builder silkTouchCondition() {
        return MatchTool.toolMatches(ItemPredicate.Builder
                .item()
                .withComponents(
                        DataComponentMatchers.Builder
                                .components()
                                .partial(
                                        DataComponentPredicates.ENCHANTMENTS,
                                        EnchantmentsPredicate.enchantments(List.of(
                                                new EnchantmentPredicate(
                                                        silkTouch(),
                                                        MinMaxBounds.Ints.atLeast(1)
                                                )
                                        ))
                                )
                                .build()
                ));
    }

    public LootItemCondition.Builder shearsCondition() {
        return MatchTool.toolMatches(ItemPredicate.Builder.item().of(itemLookup(), CommonItemTags.SHEARS));
    }

    public LootItemCondition.Builder hoeCondition() {
        return MatchTool.toolMatches(ItemPredicate.Builder.item().of(itemLookup(), ItemTags.HOES));
    }

    public LootItemCondition.Builder shearsOrHoeSilkTouchCondition() {
        return shearsCondition().or(hoeCondition().or(silkTouchCondition()));
    }

    public LootItemCondition.Builder shearsOrSilkTouchCondition() {
        return shearsCondition().or(silkTouchCondition());
    }

    public LootItemCondition.Builder neitherShearsNorSilkTouchCondition() {
        return shearsOrSilkTouchCondition().invert();
    }

    public LootTable.Builder dropWithSilkTouch(
            ItemLike withSilkTouch
    ) {
        return vanillaBlockLoot.createSilkTouchOnlyTable(withSilkTouch);
//        return LootTable
//                .lootTable()
//                .withPool(LootPool
//                        .lootPool()
//                        .setRolls(ConstantValue.exactly(1.0F))
//                        .add(LootItem.lootTableItem(withSilkTouch).when(vanillaBlockLoot.hasSilkTouch()))
//                );
    }

    public LootTable.Builder dropWithSilkTouchOrHoeOrShears(
            ItemLike withSilkTouch
    ) {
        return dropWithSilkTouchOrHoeOrShears(withSilkTouch, ConstantValue.exactly(1.0f));
    }

    public LootTable.Builder dropWithSilkTouchOrHoeOrShears(
            ItemLike withSilkTouch,
            NumberProvider rolls
    ) {
        return LootTable
                .lootTable()
                .withPool(LootPool
                        .lootPool()
                        .when(shearsOrHoeSilkTouchCondition())
                        .setRolls(rolls)
                        .add(LootItem.lootTableItem(withSilkTouch))
                );
    }

    public LootTable.Builder dropWithSilkTouchOrShears(
            ItemLike withSilkTouch
    ) {
        return LootTable
                .lootTable()
                .withPool(LootPool
                        .lootPool()
                        .when(vanillaBlockLoot.hasShearsOrSilkTouch())
                        .setRolls(ConstantValue.exactly(1.0f))
                        .add(LootItem.lootTableItem(withSilkTouch)));
    }

    public LootTable.Builder dropWithSilkTouch(
            Block withSilkTouch,
            ItemLike withoutSilkTouch,
            NumberProvider numberProvider
    ) {
        return vanillaBlockLoot.createSingleItemTableWithSilkTouch(withSilkTouch, withoutSilkTouch, numberProvider);
    }

    public LootTable.Builder dropWithSilkTouch(
            Block withSilkTouch,
            List<DropInfo> withoutSilkTouch
    ) {
        if (withoutSilkTouch.isEmpty()) return dropWithSilkTouch(withSilkTouch);

        var mainBuilder = LootTable.lootTable();
        for (DropInfo dropInfo : withoutSilkTouch) {
            LootPoolSingletonContainer.Builder<? extends LootPoolSingletonContainer.Builder<?>> item = LootItem
                    .lootTableItem(dropInfo.item)
                    .apply(SetItemCountFunction.setCount(dropInfo.numberProvider));
            createSelfDropDispatchTable(
                    mainBuilder,
                    withSilkTouch,
                    vanillaBlockLoot.hasSilkTouch(),
                    vanillaBlockLoot.applyExplosionDecay(withSilkTouch, item)
            );
        }

        return mainBuilder;
    }

    protected static void createSelfDropDispatchTable(
            LootTable.Builder tableBuilder,
            Block block,
            LootItemCondition.Builder builder,
            LootPoolEntryContainer.Builder<?> builder2
    ) {
        tableBuilder.withPool(LootPool
                .lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(block).when(builder).otherwise(builder2)));
    }


    public LootTable.Builder drop(ItemLike block) {
        return vanillaBlockLoot.createSingleItemTable(block);
    }

    public LootTable.Builder dropPottedContents(FlowerPotBlock block) {
        return vanillaBlockLoot.createPotFlowerItemTable(block.getPotted());
    }

    public LootTable.Builder dropOre(Block oreBlock, Item ore) {
        return vanillaBlockLoot.createOreDrop(oreBlock, ore);
    }

    public LootTable.Builder dropOre(Block oreBlock, Item ore, NumberProvider numberProvider) {
        return vanillaBlockLoot.createSilkTouchDispatchTable(
                oreBlock,
                vanillaBlockLoot.applyExplosionDecay(
                        oreBlock,
                        LootItem
                                .lootTableItem(ore)
                                .apply(SetItemCountFunction.setCount(numberProvider))
                                .apply(ApplyBonusCount.addOreBonusCount(enchantmentLookup.getOrThrow(Enchantments.FORTUNE)))
                )
        );
    }

    public LootTable.Builder dropDoor(Block doorBlock) {
        return vanillaBlockLoot.createSinglePropConditionTable(doorBlock, DoorBlock.HALF, DoubleBlockHalf.LOWER);
    }

    /*
    this.
     */

    public <T extends Comparable<T> & StringRepresentable> LootTable.Builder dropSingleWithCondition(
            Block block,
            Property<T> property,
            T comparable
    ) {
        return vanillaBlockLoot.createSinglePropConditionTable(block, property, comparable);
    }

    public <T extends Comparable<T> & StringRepresentable> LootTable.Builder dropWithSilkTouchAndCondition(
            Block withSilkTouch,
            ItemLike withoutSilkTouch,
            NumberProvider numberProvider,
            Property<T> property,
            T comparable
    ) {
        return vanillaBlockLoot.createSilkTouchDispatchTable(
                withSilkTouch,
                vanillaBlockLoot.applyExplosionCondition(
                        withSilkTouch,
                        LootItem.lootTableItem(withoutSilkTouch)
                                .when(LootItemBlockStatePropertyCondition
                                        .hasBlockStateProperties(withSilkTouch)
                                        .setProperties(net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder
                                                .properties()
                                                .hasProperty(property, comparable))
                                )
                                .apply(SetItemCountFunction.setCount(numberProvider))
                )
        );
    }

    public <T extends Comparable<T> & StringRepresentable> LootTable.Builder dropWithSilkTouchAndCondition(
            Block withSilkTouch,
            Property<T> property,
            T comparable
    ) {
        return LootTable.lootTable().withPool(
                LootPool
                        .lootPool()
                        .when(vanillaBlockLoot.hasSilkTouch())
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(withSilkTouch)
                                     .when(LootItemBlockStatePropertyCondition
                                             .hasBlockStateProperties(withSilkTouch)
                                             .setProperties(net.minecraft.advancements.critereon.StatePropertiesPredicate.Builder
                                                     .properties()
                                                     .hasProperty(property, comparable))
                                     )
                        )
        );
    }

    public LootTable.Builder dropPlant(Block plantBlock, ItemLike sapling) {
        return dropPlant(plantBlock, sapling, 0.125F, ConstantValue.exactly(1), 2);
    }

    public LootTable.Builder dropPlant(
            Block plantBlock,
            ItemLike sapling,
            float saplingChance,
            NumberProvider saplingCount,
            int fortuneBonus
    ) {
        return vanillaBlockLoot.createShearsDispatchTable(
                plantBlock, vanillaBlockLoot.applyExplosionDecay(
                        plantBlock, (LootItem
                                .lootTableItem(sapling)
                                .when(LootItemRandomChanceCondition.randomChance(saplingChance)))
                                .apply(SetItemCountFunction.setCount(saplingCount))
                                .apply(ApplyBonusCount.addUniformBonusCount(
                                        enchantmentLookup.getOrThrow(Enchantments.FORTUNE),
                                        fortuneBonus
                                ))
                )
        );
    }

    public <T extends Comparable<T> & StringRepresentable> LootTable.Builder dropPlant(
            Block plantBlock,
            ItemLike fruit,
            ItemLike seed,
            Property<T> property,
            T comparable
    ) {
        return this.dropPlant(
                plantBlock,
                fruit,
                ConstantValue.exactly(1),
                seed,
                ConstantValue.exactly(1),
                0.571f,
                3,
                property,
                comparable
        );
    }

    public LootTable.Builder dropPlant(
            Block plantBlock,
            ItemLike fruit,
            ItemLike seed,
            IntegerProperty property,
            int comparable
    ) {
        return this.dropPlant(
                plantBlock,
                fruit,
                ConstantValue.exactly(1),
                seed,
                ConstantValue.exactly(1),
                0.571f,
                3,
                property,
                comparable
        );
    }

    public <T extends Comparable<T> & StringRepresentable> LootTable.Builder dropPlant(
            Block plantBlock,
            ItemLike fruit,
            NumberProvider fruitCount,
            ItemLike seed,
            NumberProvider seedCount,
            float probability,
            int extraRounds,
            Property<T> property,
            T comparable
    ) {
        LootItemCondition.Builder condition = LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(plantBlock)
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(property, comparable));
        return dropPlant(plantBlock, fruit, fruitCount, seed, seedCount, probability, extraRounds, condition);
    }

    public LootTable.Builder dropPlant(
            Block plantBlock,
            ItemLike fruit,
            NumberProvider fruitCount,
            ItemLike seed,
            NumberProvider seedCount,
            float probability,
            int extraRounds,
            IntegerProperty property,
            int comparable
    ) {
        LootItemCondition.Builder condition = LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(plantBlock)
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(property, comparable));
        return dropPlant(plantBlock, fruit, fruitCount, seed, seedCount, probability, extraRounds, condition);
    }

    public LootTable.Builder dropPlant(
            Block plantBlock,
            ItemLike fruit,
            NumberProvider fruitCount,
            ItemLike seed,
            NumberProvider seedCount,
            float probability,
            int extraRounds,
            LootItemCondition.Builder condition
    ) {
        return vanillaBlockLoot.applyExplosionDecay(
                plantBlock,
                LootTable.lootTable().withPool(
                        LootPool
                                .lootPool()
                                .add(LootItem
                                        .lootTableItem(fruit)
                                        .apply(SetItemCountFunction.setCount(fruitCount))
                                        .when(condition).otherwise(LootItem.lootTableItem(seed))

                                )
                ).withPool(
                        LootPool
                                .lootPool()
                                .when(condition)
                                .add(LootItem
                                        .lootTableItem(seed)
                                        .apply(SetItemCountFunction.setCount(seedCount))
                                        .apply(ApplyBonusCount.addBonusBinomialDistributionCount(
                                                enchantmentLookup.getOrThrow(Enchantments.FORTUNE),
                                                probability,
                                                extraRounds
                                        ))
                                )
                )

        );
    }

    public LootTable.Builder dropLeaves(Block leaves, Block sapling) {
        return this.dropLeaves(leaves, sapling, VANILLA_LEAVES_SAPLING_CHANCES);
    }

    public LootTable.Builder dropLeaves(Block leaves, Block sapling, float... saplingChances) {
        return vanillaBlockLoot.createLeavesDrops(leaves, sapling, saplingChances);
    }

    public LootTable.Builder dropLeaves(
            Block leaveBlock,
            ItemLike saplingBlock,
            int fortuneRate,
            int dropRate
    ) {
        return dropLeaves(leaveBlock, saplingBlock, null, null, fortuneRate, dropRate);
    }

    public LootTable.Builder dropLeaves(
            Block leaveBlock,
            ItemLike saplingBlock,
            @Nullable ItemLike stickBlock,
            @Nullable NumberProvider stickCount,
            int fortuneRate,
            int dropRate
    ) {
        float fortuneSaplingChance = 1.0f / fortuneRate;
        float saplingChance = 1.0f / dropRate;
        float[] fortuneSaplingChances = {
                0.8f * saplingChance,
                fortuneSaplingChance,
                1.3333f * fortuneSaplingChance,
                1.6666f * fortuneSaplingChance
        };
        var baseBuilder = vanillaBlockLoot
                .createSilkTouchOrShearsDispatchTable(
                        leaveBlock,
                        vanillaBlockLoot
                                .applyExplosionCondition(
                                        leaveBlock,
                                        LootItem.lootTableItem(saplingBlock)
                                )
                                .when(BonusLevelTableCondition.bonusLevelFlatChance(
                                        enchantmentLookup.getOrThrow(Enchantments.FORTUNE), fortuneSaplingChances))
                );
        if (stickBlock != null) {
            if (stickCount == null) {
                stickCount = UniformGenerator.between(1.0f, 2.0f);
            }
            baseBuilder = baseBuilder.withPool(
                    LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0f))
                            .when(vanillaBlockLoot.doesNotHaveShearsOrSilkTouch())
                            .add(vanillaBlockLoot
                                    .applyExplosionDecay(
                                            leaveBlock,
                                            LootItem.lootTableItem(Items.STICK)
                                                    .apply(SetItemCountFunction.setCount(stickCount))
                                    )
                                    .when(BonusLevelTableCondition.bonusLevelFlatChance(
                                            enchantmentLookup.getOrThrow(Enchantments.FORTUNE),
                                            VANILLA_LEAVES_STICK_CHANCES
                                    )))
            );
        }
        return baseBuilder;
    }

    public LootTable.Builder dropDoublePlantShears(Block block) {
        return vanillaBlockLoot.createDoublePlantShearsDrop(block);
    }

    public LootTable.Builder dropDoublePlantShears(Block block, Block seed) {
        return vanillaBlockLoot.createDoublePlantWithSeedDrops(block, seed);
    }


}
