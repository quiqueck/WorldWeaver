package de.ambertation.wover.loot.api;

import de.ambertation.wover.tag.api.predefined.CommonItemTags;

import net.minecraft.advancements.criterion.*;
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
import net.minecraft.world.level.block.ComposterBlock;
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

/**
 * Passed to loot table factories during loot table datagen. Wraps vanilla's {@link VanillaBlockLoot} with
 * the registry lookups (enchantments, items) needed to build loot table conditions/functions, and adds a set
 * of higher-level {@code drop...(...)} helpers that mirror the private helper methods vanilla's own loot
 * table providers use internally (silk touch dispatch, ore drops, leaves drops, plant/crop drops,
 * composters, ...).
 */
public class LootLookupProvider {
    /**
     * The vanilla per-fortune-level chance for a stick to additionally drop from leaves (fortune 0-4).
     */
    public static final float[] VANILLA_LEAVES_STICK_CHANCES = new float[]{
            0.02F,
            0.022222223F,
            0.025F,
            0.033333335F,
            0.1F
    };
    /**
     * The vanilla per-fortune-level chance for a sapling to drop from leaves (fortune 0-3).
     */
    public static final float[] VANILLA_LEAVES_SAPLING_CHANCES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};

    /**
     * Gets the vanilla "has silk touch" loot condition.
     *
     * @return The silk touch condition
     */
    public LootItemCondition.Builder hasSilkTouch() {
        return vanillaBlockLoot.hasSilkTouch();
    }

    /**
     * Describes a single item/count pair that should be added as a loot table drop, used by
     * {@link #dropWithSilkTouch(Block, List)}.
     *
     * @param item           The item to drop
     * @param numberProvider The number of items to drop
     */
    public record DropInfo(ItemLike item, NumberProvider numberProvider) {
        /**
         * Creates a drop with a fixed item count.
         *
         * @param item  The item to drop
         * @param count The fixed number of items to drop
         */
        public DropInfo(ItemLike item, int count) {
            this(item, ConstantValue.exactly(count));
        }
    }

    /**
     * The registry lookup provider passed to this instance's constructor.
     */
    public final HolderLookup.Provider provider;
    private final VanillaBlockLoot vanillaBlockLoot;
    /**
     * Lookup for the enchantment registry, used to resolve enchantment holders (e.g. for {@link #fortune()}).
     */
    public final HolderLookup.RegistryLookup<Enchantment> enchantmentLookup;

    /**
     * Creates a new provider wrapping the given registry lookup.
     *
     * @param provider The registry lookup provider, usually the one passed to the surrounding datagen callback
     */
    public LootLookupProvider(HolderLookup.Provider provider) {
        this.vanillaBlockLoot = new VanillaBlockLoot(provider);
        this.provider = provider;
        this.enchantmentLookup = provider.lookupOrThrow(Registries.ENCHANTMENT);
    }

    /**
     * Gets the lookup for the item registry.
     *
     * @return The item registry lookup
     */
    public HolderLookup.RegistryLookup<Item> itemLookup() {
        return provider.lookupOrThrow(Registries.ITEM);
    }

    /**
     * Resolves an enchantment holder from its resource key.
     *
     * @param key The enchantment's resource key
     * @return The resolved enchantment holder
     */
    public Holder<Enchantment> enchantment(ResourceKey<Enchantment> key) {
        return this.enchantmentLookup.getOrThrow(key);
    }

    /**
     * Resolves the {@link Enchantments#FORTUNE} enchantment holder.
     *
     * @return The fortune enchantment holder
     */
    public Holder<Enchantment> fortune() {
        return enchantment(Enchantments.FORTUNE);
    }

    /**
     * Resolves the {@link Enchantments#SILK_TOUCH} enchantment holder.
     *
     * @return The silk touch enchantment holder
     */
    public Holder<Enchantment> silkTouch() {
        return enchantment(Enchantments.SILK_TOUCH);
    }

    /**
     * Gets the registry lookup provider this instance was created with.
     *
     * @return The registry lookup provider
     */
    public HolderLookup.Provider getProvider() {
        return provider;
    }

    /**
     * Builds a loot condition that matches if the tool used has the Silk Touch enchantment with at least
     * level 1.
     *
     * @return The silk touch condition
     */
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

    /**
     * Builds a loot condition that matches if the tool used has the {@code c:shears} tag.
     *
     * @return The shears condition
     */
    public LootItemCondition.Builder shearsCondition() {
        return MatchTool.toolMatches(ItemPredicate.Builder.item().of(itemLookup(), CommonItemTags.SHEARS));
    }

    /**
     * Builds a loot condition that matches if the tool used has the {@link ItemTags#HOES} tag.
     *
     * @return The hoe condition
     */
    public LootItemCondition.Builder hoeCondition() {
        return MatchTool.toolMatches(ItemPredicate.Builder.item().of(itemLookup(), ItemTags.HOES));
    }

    /**
     * Builds a loot condition that matches shears, a hoe, or Silk Touch.
     *
     * @return The combined condition
     */
    public LootItemCondition.Builder shearsOrHoeSilkTouchCondition() {
        return shearsCondition().or(hoeCondition().or(silkTouchCondition()));
    }

    /**
     * Builds a loot condition that matches shears or Silk Touch.
     *
     * @return The combined condition
     */
    public LootItemCondition.Builder shearsOrSilkTouchCondition() {
        return shearsCondition().or(silkTouchCondition());
    }

    /**
     * Builds a loot condition that matches neither shears nor Silk Touch.
     *
     * @return The inverted combined condition
     */
    public LootItemCondition.Builder neitherShearsNorSilkTouchCondition() {
        return shearsOrSilkTouchCondition().invert();
    }

    public LootTable.Builder dropSelfNoExplosion(Block block) {
        return LootTable
                .lootTable()
                .withPool(LootPool
                        .lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(block)));
    }

    /**
     * Builds a loot table that drops {@code withSilkTouch} only if the tool used has Silk Touch, and
     * nothing otherwise.
     *
     * @param withSilkTouch The item to drop when Silk Touch is used
     * @return The loot table builder
     */
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

    /**
     * Builds a loot table that drops {@code withSilkTouch} (with 1 roll) if the tool used is Silk Touch, a
     * hoe, or shears, and drops nothing otherwise.
     *
     * @param withSilkTouch The item to drop
     * @return The loot table builder
     */
    public LootTable.Builder dropWithSilkTouchOrHoeOrShears(
            ItemLike withSilkTouch
    ) {
        return dropWithSilkTouchOrHoeOrShears(withSilkTouch, ConstantValue.exactly(1.0f));
    }

    /**
     * Builds a loot table that drops {@code withSilkTouch} if the tool used is Silk Touch, a hoe, or shears,
     * and drops nothing otherwise.
     *
     * @param withSilkTouch The item to drop
     * @param rolls         The number of rolls for the drop pool
     * @return The loot table builder
     */
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

    /**
     * Builds a loot table that drops {@code withSilkTouch} (with 1 roll) if the tool used is Silk Touch or
     * shears, and drops nothing otherwise.
     *
     * @param withSilkTouch The item to drop
     * @return The loot table builder
     */
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

    /**
     * Builds a loot table that drops {@code withSilkTouch} itself when the tool used has Silk Touch, and
     * {@code withoutSilkTouch} (with explosion decay applied) otherwise.
     *
     * @param withSilkTouch    The block to drop when Silk Touch is used
     * @param withoutSilkTouch The item to drop otherwise
     * @param numberProvider   The number of {@code withoutSilkTouch} items to drop
     * @return The loot table builder
     */
    public LootTable.Builder dropWithSilkTouch(
            Block withSilkTouch,
            ItemLike withoutSilkTouch,
            NumberProvider numberProvider
    ) {
        return vanillaBlockLoot.createSingleItemTableWithSilkTouch(withSilkTouch, withoutSilkTouch, numberProvider);
    }

    /**
     * Builds a loot table that drops {@code withSilkTouch} itself when the tool used has Silk Touch, and
     * every entry in {@code withoutSilkTouch} (each with explosion decay applied) otherwise.
     *
     * @param withSilkTouch    The block to drop when Silk Touch is used
     * @param withoutSilkTouch The items to drop otherwise, one pool per entry
     * @return The loot table builder
     */
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

    /**
     * Adds a pool to {@code tableBuilder} that drops {@code block} itself when {@code builder} matches, and
     * falls back to {@code builder2} otherwise.
     *
     * @param tableBuilder The loot table builder to add the pool to
     * @param block        The block to drop when the condition matches
     * @param builder      The condition that selects the self-drop
     * @param builder2     The fallback entry used when the condition doesn't match
     */
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


    /**
     * Builds a loot table that always drops a single instance of {@code block}.
     *
     * @param block The item/block to drop
     * @return The loot table builder
     */
    public LootTable.Builder drop(ItemLike block) {
        return vanillaBlockLoot.createSingleItemTable(block);
    }

    /**
     * Builds a loot table for a flower pot that drops its potted content.
     *
     * @param block The flower pot block
     * @return The loot table builder
     */
    public LootTable.Builder dropPottedContents(FlowerPotBlock block) {
        return vanillaBlockLoot.createPotFlowerItemTable(block.getPotted());
    }

    /**
     * Builds a loot table for an ore block that drops {@code ore} itself when Silk Touch is used, and a
     * fortune-boosted count of {@code ore} otherwise (vanilla ore-drop behavior).
     *
     * @param oreBlock The ore block
     * @param ore      The item the ore drops
     * @return The loot table builder
     */
    public LootTable.Builder dropOre(Block oreBlock, Item ore) {
        return vanillaBlockLoot.createOreDrop(oreBlock, ore);
    }

    /**
     * Builds a loot table for an ore block that drops {@code oreBlock} itself when Silk Touch is used, and
     * a fortune-boosted count (based on {@code numberProvider}) of {@code ore} otherwise.
     *
     * @param oreBlock       The ore block
     * @param ore            The item the ore drops
     * @param numberProvider The base number of items to drop before the fortune bonus
     * @return The loot table builder
     */
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

    /**
     * Builds a loot table for a door block that only drops the door item when broken from its
     * {@link DoubleBlockHalf#LOWER} half (so a two-tall door doesn't drop twice).
     *
     * @param doorBlock The door block
     * @return The loot table builder
     */
    public LootTable.Builder dropDoor(Block doorBlock) {
        return vanillaBlockLoot.createSinglePropConditionTable(doorBlock, DoorBlock.HALF, DoubleBlockHalf.LOWER);
    }

    /**
     * Builds a loot table that drops the block itself only when it has the given block state property value.
     *
     * @param block      The block to drop
     * @param property   The block state property to check
     * @param comparable The required value of the property
     * @param <T>        The property's value type
     * @return The loot table builder
     */
    public <T extends Comparable<T> & StringRepresentable> LootTable.Builder dropSingleWithCondition(
            Block block,
            Property<T> property,
            T comparable
    ) {
        return vanillaBlockLoot.createSinglePropConditionTable(block, property, comparable);
    }

    /**
     * Builds a loot table that drops {@code withSilkTouch} itself when the tool used has Silk Touch, and
     * {@code withoutSilkTouch} otherwise, but only when the block has the given block state property value
     * (explosion decay is applied to the non-silk-touch drop).
     *
     * @param withSilkTouch    The block to drop when Silk Touch is used
     * @param withoutSilkTouch The item to drop otherwise
     * @param numberProvider   The number of {@code withoutSilkTouch} items to drop
     * @param property         The block state property to check
     * @param comparable       The required value of the property
     * @param <T>              The property's value type
     * @return The loot table builder
     */
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
                                        .setProperties(net.minecraft.advancements.criterion.StatePropertiesPredicate.Builder
                                                .properties()
                                                .hasProperty(property, comparable))
                                )
                                .apply(SetItemCountFunction.setCount(numberProvider))
                )
        );
    }

    /**
     * Builds a loot table that drops the block itself only when Silk Touch is used and it has the given
     * block state property value.
     *
     * @param withSilkTouch The block to drop
     * @param property      The block state property to check
     * @param comparable    The required value of the property
     * @param <T>           The property's value type
     * @return The loot table builder
     */
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
                                             .setProperties(net.minecraft.advancements.criterion.StatePropertiesPredicate.Builder
                                                     .properties()
                                                     .hasProperty(property, comparable))
                                     )
                        )
        );
    }

    /**
     * Builds a loot table for a plant that has a chance to also drop a sapling-like item, using a default
     * 12.5% chance and a fortune bonus range of 2.
     *
     * @param plantBlock The plant block
     * @param sapling    The item that may additionally drop
     * @return The loot table builder
     */
    public LootTable.Builder dropPlant(Block plantBlock, ItemLike sapling) {
        return dropPlant(plantBlock, sapling, 0.125F, ConstantValue.exactly(1), 2);
    }

    /**
     * Builds a loot table for a plant that has a chance to also drop a sapling-like item, with fortune
     * increasing the chance via a uniform bonus count distribution (explosion decay is applied).
     *
     * @param plantBlock    The plant block
     * @param sapling       The item that may additionally drop
     * @param saplingChance The base chance (without fortune) that the sapling drops
     * @param saplingCount  The number of saplings to drop when the chance succeeds
     * @param fortuneBonus  The fortune bonus range applied to the chance
     * @return The loot table builder
     */
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

    /**
     * Builds a loot table for a crop-like plant (fruit + seed, e.g. berries or fully-grown vanilla crops)
     * that only drops fully when the given block state property has the given value, with a default fortune
     * curve for the seed count.
     *
     * @param plantBlock The plant block
     * @param fruit      The item dropped when the property condition is met
     * @param seed       The item that always drops (with a fortune-boosted extra chance)
     * @param property   The block state property to check for the fruit condition
     * @param comparable The required value of the property
     * @param <T>        The property's value type
     * @return The loot table builder
     */
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

    /**
     * Builds a loot table for a crop-like plant (fruit + seed) that only drops fully when the given integer
     * block state property has the given value, with a default fortune curve for the seed count.
     *
     * @param plantBlock The plant block
     * @param fruit      The item dropped when the property condition is met
     * @param seed       The item that always drops (with a fortune-boosted extra chance)
     * @param property   The integer block state property to check for the fruit condition
     * @param comparable The required value of the property
     * @return The loot table builder
     */
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

    /**
     * Builds a loot table for a crop-like plant (fruit + seed) that only drops fully when the given block
     * state property has the given value, with an explicit seed-count fortune curve
     * ({@code ApplyBonusCount.addBonusBinomialDistributionCount}).
     *
     * @param plantBlock  The plant block
     * @param fruit       The item dropped when the property condition is met
     * @param fruitCount  The number of fruit items to drop
     * @param seed        The item that always drops
     * @param seedCount   The base number of seed items to drop
     * @param probability The binomial distribution probability for the fortune-boosted extra seed count
     * @param extraRounds The binomial distribution extra rounds for the fortune-boosted extra seed count
     * @param property    The block state property to check for the fruit condition
     * @param comparable  The required value of the property
     * @param <T>         The property's value type
     * @return The loot table builder
     */
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

    /**
     * Builds a loot table for a crop-like plant (fruit + seed) that only drops fully when the given integer
     * block state property has the given value, with an explicit seed-count fortune curve.
     *
     * @param plantBlock  The plant block
     * @param fruit       The item dropped when the property condition is met
     * @param fruitCount  The number of fruit items to drop
     * @param seed        The item that always drops
     * @param seedCount   The base number of seed items to drop
     * @param probability The binomial distribution probability for the fortune-boosted extra seed count
     * @param extraRounds The binomial distribution extra rounds for the fortune-boosted extra seed count
     * @param property    The integer block state property to check for the fruit condition
     * @param comparable  The required value of the property
     * @return The loot table builder
     */
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

    /**
     * Builds a loot table for a crop-like plant (fruit + seed) whose fruit condition is expressed as an
     * arbitrary loot condition, with an explicit seed-count fortune curve. This is the common implementation
     * the other {@code dropPlant(...)} overloads delegate to.
     *
     * @param plantBlock  The plant block
     * @param fruit       The item dropped when {@code condition} matches
     * @param fruitCount  The number of fruit items to drop
     * @param seed        The item that always drops
     * @param seedCount   The base number of seed items to drop
     * @param probability The binomial distribution probability for the fortune-boosted extra seed count
     * @param extraRounds The binomial distribution extra rounds for the fortune-boosted extra seed count
     * @param condition   The condition that selects the fruit drop (seeds drop when it is met, in addition
     *                    to the always-present fruit-or-seed pool)
     * @return The loot table builder
     */
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

    /**
     * Builds a loot table for leaves that drop a sapling, using the vanilla
     * {@link #VANILLA_LEAVES_SAPLING_CHANCES} fortune curve.
     *
     * @param leaves  The leaves block
     * @param sapling The sapling block to drop
     * @return The loot table builder
     */
    public LootTable.Builder dropLeaves(Block leaves, Block sapling) {
        return this.dropLeaves(leaves, sapling, VANILLA_LEAVES_SAPLING_CHANCES);
    }

    /**
     * Builds a loot table for leaves that drop a sapling, using vanilla's leaves drop logic (shears/silk
     * touch always drop the leaves, otherwise a sapling drops per fortune-level chance, with explosion decay).
     *
     * @param leaves         The leaves block
     * @param sapling        The sapling block to drop
     * @param saplingChances The per-fortune-level sapling drop chance (index 0 = no fortune)
     * @return The loot table builder
     */
    public LootTable.Builder dropLeaves(Block leaves, Block sapling, float... saplingChances) {
        return vanillaBlockLoot.createLeavesDrops(leaves, sapling, saplingChances);
    }

    /**
     * Builds a loot table for leaves that drop a sapling, with the sapling and fortune chances derived from
     * simple rate fractions instead of an explicit per-level chance array.
     *
     * @param leaveBlock   The leaves block
     * @param saplingBlock The sapling item to drop
     * @param fortuneRate  1-in-{@code fortuneRate} chance that fortune boosts the sapling drop
     * @param dropRate     1-in-{@code dropRate} base chance that the sapling drops
     * @return The loot table builder
     */
    public LootTable.Builder dropLeaves(
            Block leaveBlock,
            ItemLike saplingBlock,
            int fortuneRate,
            int dropRate
    ) {
        return dropLeaves(leaveBlock, saplingBlock, null, null, fortuneRate, dropRate);
    }

    /**
     * Builds a loot table for leaves that drop a sapling (with fortune-boosted chance derived from rate
     * fractions) and, optionally, an additional stick drop (using the vanilla
     * {@link #VANILLA_LEAVES_STICK_CHANCES} fortune curve) when shears/silk touch are not used.
     *
     * @param leaveBlock   The leaves block
     * @param saplingBlock The sapling item to drop
     * @param stickBlock   The stick item to additionally drop, or {@code null} to skip the stick drop
     * @param stickCount   The number of sticks to drop (defaults to a uniform 1-2 range if {@code null})
     * @param fortuneRate  1-in-{@code fortuneRate} chance that fortune boosts the sapling drop
     * @param dropRate     1-in-{@code dropRate} base chance that the sapling drops
     * @return The loot table builder
     */
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

    /**
     * Builds a loot table for a two-tall plant (e.g. tall grass) that only drops itself (2 items) when
     * broken with shears.
     *
     * @param block The plant block
     * @return The loot table builder
     */
    public LootTable.Builder dropDoublePlantShears(Block block) {
        return vanillaBlockLoot.createDoublePlantShearsDrop(block);
    }

    /**
     * Builds a loot table for a two-tall plant that drops two of itself when broken with ANY tool (or by
     * hand), mirroring how a single ground plant drops via {@code dropSelf}. Unlike
     * {@link #dropDoublePlantShears(Block)} there is no shears requirement; the drop is still subject to
     * explosion survival.
     *
     * @param block The plant block
     * @return The loot table builder
     */
    public LootTable.Builder dropDoublePlant(Block block) {
        return LootTable.lootTable().withPool(
                LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(vanillaBlockLoot.applyExplosionCondition(
                                block,
                                LootItem.lootTableItem(block)
                                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F)))
                        ))
        );
    }

    /**
     * Builds a loot table for a two-tall plant that drops itself when broken with shears, and a seed item
     * (with a small random chance) otherwise.
     *
     * @param block The plant block
     * @param seed  The seed item to drop when shears are not used
     * @return The loot table builder
     */
    public LootTable.Builder dropDoublePlantShears(Block block, Block seed) {
        return vanillaBlockLoot.createDoublePlantWithSeedDrops(block, seed);
    }

    /**
     * Builds a loot table for a block that keeps its custom name in a block entity (e.g. a nameable
     * container), copying the name from the block entity onto the dropped item.
     *
     * @param block The block
     * @return The loot table builder
     */
    public LootTable.Builder dropNamedBlockEntity(Block block) {
        return vanillaBlockLoot.createNameableBlockEntityTable(block);
    }

    /**
     * Builds a loot table for a slab that drops 2 items when broken as a double slab, and 1 otherwise.
     *
     * @param block The slab block
     * @return The loot table builder
     */
    public LootTable.Builder dropSlab(Block block) {
        return vanillaBlockLoot.createSlabItemTable(block);
    }

    /**
     * Builds a loot table for a composter that drops itself (with explosion decay), plus a bone meal item
     * when broken at composter level 8 (fully composted).
     *
     * @param compsterBlock The composter block
     * @return The loot table builder
     */
    public LootTable.Builder dropComposter(Block compsterBlock) {
        return LootTable
                .lootTable()
                .withPool(LootPool.lootPool()
                                  .add(vanillaBlockLoot.applyExplosionDecay(
                                          compsterBlock,
                                          LootItem.lootTableItem(compsterBlock.asItem())
                                  )))
                .withPool(
                        LootPool.lootPool()
                                .add(LootItem.lootTableItem(Items.BONE_MEAL))
                                .when(
                                        LootItemBlockStatePropertyCondition
                                                .hasBlockStateProperties(compsterBlock)
                                                .setProperties(
                                                        StatePropertiesPredicate
                                                                .Builder.
                                                                properties()
                                                                .hasProperty(ComposterBlock.LEVEL, 8)
                                                )
                                )
                );
    }
}
