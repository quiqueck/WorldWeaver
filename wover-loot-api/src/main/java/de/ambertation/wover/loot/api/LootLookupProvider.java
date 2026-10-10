package de.ambertation.wover.loot.api;

import de.ambertation.wover.tag.api.predefined.CommonItemTags;

import net.minecraft.advancements.predicates.DataComponentMatchers;
import net.minecraft.advancements.predicates.EnchantmentPredicate;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.predicates.DataComponentPredicates;
import net.minecraft.core.component.predicates.EnchantmentsPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
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
import net.minecraft.world.level.storage.loot.entries.UniformContainerBase;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

import java.util.List;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

/**
 * Passed to loot table factories during loot table datagen. Wraps vanilla's {@link VanillaBlockLoot} with
 * the registry lookups (enchantments, items) needed to build loot table conditions/functions, and adds a set
 * of higher-level {@code drop...(...)} helpers that mirror the private helper methods vanilla's own loot
 * table providers use internally (silk touch dispatch, ore drops, leaves drops, plant/crop drops,
 * composters, ...).
 *
 * <h2>26.3 migration notes</h2>
 * Two vanilla changes reach through this whole class:
 * <ul>
 *     <li><b>Number providers became a datapack registry</b>. In the 26.3 snapshots every place vanilla used to
 *     take a {@code NumberProvider} took a {@code Holder<NumberProvider>}; the 26.3 release then split number
 *     providers into integer and float providers ({@code minecraft:context_int_provider} /
 *     {@code minecraft:context_float_provider}). Loot pool rolls and item counts are integer providers now, so
 *     the {@code drop...(...)} helpers below take a {@code Holder<ContextIntProvider>}. Build them with
 *     {@link ContextIntProviders#exactly(int)} / {@link ContextIntProviders#between(int, int)}, which return the
 *     holder directly.</li>
 *     <li><b>Loot conditions became a reloadable registry</b> too, and vanilla moved its silk-touch/shears
 *     conditions into it as {@code minecraft:tool/can_silk_touch} and {@code minecraft:tool/can_shear}. See
 *     {@link WoverVanillaBlockLoot} for why we cannot let vanilla resolve them during datagen.</li>
 * </ul>
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
        // Identical to silkTouchCondition() since 26.3 - see WoverVanillaBlockLoot for why we no longer
        // delegate to VanillaBlockLoot#hasSilkTouch() (which now resolves a registry entry that is empty
        // during datagen) and build the very same condition inline instead.
        return silkTouchCondition();
    }

    /**
     * Describes a single item/count pair that should be added as a loot table drop, used by
     * {@link #dropWithSilkTouch(Block, List)}.
     *
     * @param item           The item to drop
     * @param numberProvider The number of items to drop
     */
    public record DropInfo(ItemLike item, Holder<ContextIntProvider> numberProvider) {
        /**
         * Creates a drop with a fixed item count.
         *
         * @param item  The item to drop
         * @param count The fixed number of items to drop
         */
        public DropInfo(ItemLike item, int count) {
            this(item, ContextIntProviders.exactly(count));
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
        this.provider = provider;
        this.enchantmentLookup = provider.lookupOrThrow(Registries.ENCHANTMENT);
        // Must come last: WoverVanillaBlockLoot's overrides read the fields above.
        this.vanillaBlockLoot = new WoverVanillaBlockLoot(new LookupContext(provider));
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
     * Gets the lookup for the block registry.
     * <p>
     * 26.3 replaced {@code LootItemBlockStatePropertyCondition} with {@link MatchBlock}, which builds an
     * {@code advancements} {@code BlockPredicate} and therefore needs a block lookup to resolve the block into
     * a {@code HolderSet}. Exposed for the same reason {@link #itemLookup()} is.
     *
     * @return The block registry lookup
     */
    public HolderLookup.RegistryLookup<Block> blockLookup() {
        return provider.lookupOrThrow(Registries.BLOCK);
    }

    /**
     * Adapts a plain {@link HolderLookup.Provider} to the {@link LootTableSubProvider.Context} that 26.3's
     * {@code BlockLootSubProvider} constructor now takes in place of the provider itself.
     * <p>
     * Loot tables became a datapack registry in 26.3, so {@code LootTableSubProvider} is written against a
     * bootstrap-style context: a {@link net.minecraft.data.worldgen.BootstrapContextAccess} (registry lookups
     * plus the new {@code listContextElements}) extended with {@code accept(key, builder)} for emitting tables.
     * We only ever use the sub-provider's {@code create...} / {@code apply...} table <em>builders</em>; the
     * tables themselves are collected and written by {@code WoverLootTableProvider}, which predates this and
     * still speaks {@code BiConsumer<ResourceKey<LootTable>, LootTable.Builder>}. So {@code accept} is
     * deliberately unimplementable here rather than silently swallowing a table: reaching it would mean a
     * caller used a vanilla {@code dropSelf}/{@code add}-style method that registers instead of returning, and
     * that table would otherwise disappear without a trace.
     *
     * @param provider The registry lookup provider backing every {@code lookup(...)} call
     */
    private record LookupContext(HolderLookup.Provider provider) implements LootTableSubProvider.Context {
        @Override
        public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> registryKey) {
            return provider.lookupOrThrow(registryKey);
        }

        @SuppressWarnings("deprecation") // deprecated in vanilla but still abstract, so it must be implemented
        @Override
        public <S> Stream<Holder.Reference<S>> listContextElements(ResourceKey<? extends Registry<? extends S>> registryKey) {
            return provider.lookupOrThrow(registryKey).listElements();
        }

        @Override
        public Holder.Reference<LootTable> accept(ResourceKey<LootTable> key, LootTable.Builder builder) {
            throw new UnsupportedOperationException(
                    "LootLookupProvider only uses VanillaBlockLoot as a builder factory; it cannot register the "
                            + "loot table " + key.identifier() + ". Return the LootTable.Builder to the "
                            + "WoverLootTableProvider callback instead of using a vanilla dropXXX(...) method."
            );
        }
    }

    /**
     * {@link VanillaBlockLoot} with its two condition accessors rebuilt inline.
     * <p>
     * 26.3 moved vanilla's silk-touch and shears loot conditions into the reloadable
     * {@code minecraft:predicate} registry ({@code minecraft:tool/can_silk_touch} and
     * {@code minecraft:tool/can_shear}), so {@code BlockLootSubProvider#hasSilkTouch()}/{@code #hasShears()}
     * now return a {@code Holder<LootItemCondition>} they resolve through that registry. Fabric's data
     * generator builds every reloadable registry <em>empty</em> (see
     * {@code FabricDataGenHelper#createReloadableLookupProvider}), so those lookups throw during datagen.
     * <p>
     * Both methods are re-implemented here with the exact same content vanilla's {@code LootPredicates#bootstrap}
     * registers - a {@code match_tool} on silk touch level >= 1, and a {@code match_tool} on
     * {@link Items#SHEARS} - wrapped in a direct holder. A direct holder is serialized inline by the
     * {@code Holder<LootItemCondition>} codec, so the generated JSON keeps the shape it had in 26.1 instead of
     * gaining a reference to a registry entry, and every inherited {@code create...DispatchTable} helper keeps
     * working unchanged (they all reach these two through virtual dispatch). Same fix as wunderreich's
     * {@code LootTableHelper}.
     */
    private class WoverVanillaBlockLoot extends VanillaBlockLoot {
        private WoverVanillaBlockLoot(LootTableSubProvider.Context context) {
            super(context);
        }

        @Override
        public Holder<LootItemCondition> hasSilkTouch() {
            return Holder.direct(silkTouchCondition().build());
        }

        @Override
        public Holder<LootItemCondition> hasShears() {
            // Vanilla matches the SHEARS item itself here, not the c:shears tag - LootLookupProvider's own
            // public shearsCondition() intentionally uses the broader tag, so the two must not be conflated.
            return Holder.direct(
                    MatchTool.toolMatches(ItemPredicate.Builder.item().of(itemLookup(), Items.SHEARS)).build()
            );
        }
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
                        .setRolls(ContextIntProviders.exactly(1))
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
//                        .setRolls(ContextIntProviders.exactly(1))
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
        return dropWithSilkTouchOrHoeOrShears(withSilkTouch, ContextIntProviders.exactly(1));
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
            Holder<ContextIntProvider> rolls
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
                        .setRolls(ContextIntProviders.exactly(1))
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
            Holder<ContextIntProvider> numberProvider
    ) {
        return vanillaBlockLoot.createSingleItemTableWithSilkTouch(withSilkTouch, withoutSilkTouch, numberProvider);
    }

    /**
     * Builds a loot table that drops {@code withSilkTouch} itself when the tool used has Silk Touch, and
     * every entry in {@code withoutSilkTouch} (each with explosion decay applied) otherwise.
     *
     * <p>
     * Only the first pool carries the Silk Touch self-drop; every further entry gets a pool of its own that is
     * gated on the tool <i>not</i> having Silk Touch. Giving each pool its own self-drop alternative would make a
     * Silk Touch tool yield the block once per entry.
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
        boolean first = true;
        for (DropInfo dropInfo : withoutSilkTouch) {
            // 26.3 renamed LootPoolSingletonContainer to UniformContainerBase (the "uniform" refers to the
            // weight/quality fields every single-entry container shares); LootItem.lootTableItem still returns
            // its Builder.
            UniformContainerBase.Builder<? extends UniformContainerBase.Builder<?>> item = LootItem
                    .lootTableItem(dropInfo.item)
                    .apply(SetItemCountFunction.setCount(dropInfo.numberProvider));
            if (first) {
                createSelfDropDispatchTable(
                        mainBuilder,
                        withSilkTouch,
                        hasSilkTouch(),
                        vanillaBlockLoot.applyExplosionDecay(withSilkTouch, item)
                );
                first = false;
            } else {
                mainBuilder.withPool(LootPool
                        .lootPool()
                        .setRolls(ContextIntProviders.exactly(1))
                        .when(hasSilkTouch().invert())
                        .add(vanillaBlockLoot.applyExplosionDecay(withSilkTouch, item)));
            }
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
                .setRolls(ContextIntProviders.exactly(1))
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
    public LootTable.Builder dropOre(Block oreBlock, Item ore, Holder<ContextIntProvider> numberProvider) {
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
            Holder<ContextIntProvider> numberProvider,
            Property<T> property,
            T comparable
    ) {
        return vanillaBlockLoot.createSilkTouchDispatchTable(
                withSilkTouch,
                vanillaBlockLoot.applyExplosionCondition(
                        withSilkTouch,
                        LootItem.lootTableItem(withoutSilkTouch)
                                .when(hasBlockStateProperties(withSilkTouch, property, comparable))
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
                        .when(hasSilkTouch())
                        .setRolls(ContextIntProviders.exactly(1))
                        .add(LootItem.lootTableItem(withSilkTouch)
                                     .when(hasBlockStateProperties(withSilkTouch, property, comparable))
                        )
        );
    }

    /**
     * Builds the "this block is in state {@code property=comparable}" loot condition.
     * <p>
     * 26.3 removed {@code LootItemBlockStatePropertyCondition} and folded block matching into
     * {@link MatchBlock}, which wraps an {@code advancements} {@code BlockPredicate} - hence the block lookup.
     * The condition is semantically the same but serializes differently: it is now
     * {@code {"type": "minecraft:match_block", "blocks": <holder set>, "state": {...}}} instead of
     * {@code {"condition": "minecraft:block_state_property", "block": <id>, "properties": {...}}}. That
     * rewrite is visible in the regenerated loot tables and is expected.
     *
     * @param block      The block whose state is checked
     * @param property   The block state property to check
     * @param comparable The required value of the property
     * @param <T>        The property's value type
     * @return The condition builder
     */
    private <T extends Comparable<T> & StringRepresentable> LootItemCondition.Builder hasBlockStateProperties(
            Block block,
            Property<T> property,
            T comparable
    ) {
        return MatchBlock.blockMatches(
                blockLookup(),
                block,
                StatePropertiesPredicate.Builder.properties().hasProperty(property, comparable)
        );
    }

    /**
     * {@link #hasBlockStateProperties(Block, Property, Object)} for an integer-valued property, which is not
     * {@link StringRepresentable} and therefore does not fit the generic overload.
     *
     * @param block      The block whose state is checked
     * @param property   The integer block state property to check
     * @param comparable The required value of the property
     * @return The condition builder
     */
    private LootItemCondition.Builder hasBlockStateProperties(
            Block block,
            IntegerProperty property,
            int comparable
    ) {
        return MatchBlock.blockMatches(
                blockLookup(),
                block,
                StatePropertiesPredicate.Builder.properties().hasProperty(property, comparable)
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
        return dropPlant(plantBlock, sapling, 0.125F, ContextIntProviders.exactly(1), 2);
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
            Holder<ContextIntProvider> saplingCount,
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
                ContextIntProviders.exactly(1),
                seed,
                ContextIntProviders.exactly(1),
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
                ContextIntProviders.exactly(1),
                seed,
                ContextIntProviders.exactly(1),
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
            Holder<ContextIntProvider> fruitCount,
            ItemLike seed,
            Holder<ContextIntProvider> seedCount,
            float probability,
            int extraRounds,
            Property<T> property,
            T comparable
    ) {
        LootItemCondition.Builder condition = hasBlockStateProperties(plantBlock, property, comparable);
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
            Holder<ContextIntProvider> fruitCount,
            ItemLike seed,
            Holder<ContextIntProvider> seedCount,
            float probability,
            int extraRounds,
            IntegerProperty property,
            int comparable
    ) {
        LootItemCondition.Builder condition = hasBlockStateProperties(plantBlock, property, comparable);
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
            Holder<ContextIntProvider> fruitCount,
            ItemLike seed,
            Holder<ContextIntProvider> seedCount,
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
            @Nullable Holder<ContextIntProvider> stickCount,
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
                stickCount = ContextIntProviders.between(1, 2);
            }
            baseBuilder = baseBuilder.withPool(
                    LootPool.lootPool()
                            .setRolls(ContextIntProviders.exactly(1))
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
                        .setRolls(ContextIntProviders.exactly(1))
                        .add(vanillaBlockLoot.applyExplosionCondition(
                                block,
                                LootItem.lootTableItem(block)
                                        .apply(SetItemCountFunction.setCount(ContextIntProviders.exactly(2)))
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
                                .when(hasBlockStateProperties(compsterBlock, ComposterBlock.LEVEL, 8))
                );
    }
}
