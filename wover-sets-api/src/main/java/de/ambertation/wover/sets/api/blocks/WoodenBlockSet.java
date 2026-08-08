package de.ambertation.wover.sets.api.blocks;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.block.api.trait.behaviour.FlammableBlockTrait;
import de.ambertation.wover.block.api.trait.behaviour.FuelBlockTrait;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.sets.api.blocks.slots.WoodSlots;
import de.ambertation.wover.tag.api.TagManager;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;

import net.fabricmc.fabric.api.object.builder.v1.block.type.BlockSetTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.type.WoodTypeBuilder;

import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link BlockSet} specialized for wood-like materials: derives a {@link WoodType} alongside the
 * {@link net.minecraft.world.level.block.state.properties.BlockSetType}, maintains a shared "logs" block/item
 * tag every log-family block is added to, and applies the wood/flammable traits plus map colors common to every
 * block in the set.
 * <p>
 * {@link #createDefaultDefinitions()} returns every slot in
 * {@code de.ambertation.wover.sets.api.blocks.slots.WoodSlots} by default; override to customize. See this module's
 * test mod ({@code TestWoodSet}) for a worked example.
 *
 * @param <S> the concrete subclass, for fluent chaining (see {@link #setPlanksColor})
 */
public class WoodenBlockSet<S extends WoodenBlockSet<S>> extends BlockSet<S> {
    /** The item tag every log-family block's {@code BlockItem} is added to. */
    public final TagKey<Item> logsItemTag;
    /** The block tag every log-family block is added to. */
    public final TagKey<Block> logsBlocksTag;

    /** The default map color applied to every block in this set except planks (see {@link #setPlanksColor}). */
    public final MapColor woodColor;
    private MapColor planksColor;
    private WoodType woodType;
    public final boolean fromNether;

    /**
     * Creates a new wooden block set using {@link SlotType#PLANKS} as the base slot.
     *
     * @param modCore   the mod this set's blocks/items are registered under
     * @param baseName  the naming prefix shared by every block/item this set registers
     * @param woodColor the default map color for this set's blocks
     */
    public WoodenBlockSet(
            @NotNull ModCore modCore,
            @NotNull String baseName,
            MapColor woodColor
    ) {
        this(modCore, baseName, woodColor, SlotType.PLANKS);
    }

    /**
     * Creates a new wooden block set.
     *
     * @param modCore   the mod this set's blocks/items are registered under
     * @param baseName  the naming prefix shared by every block/item this set registers
     * @param woodColor the default map color for this set's blocks
     * @param baseSlot  the slot considered this set's primary/base block
     */
    public WoodenBlockSet(
            @NotNull ModCore modCore,
            @NotNull String baseName,
            MapColor woodColor,
            @NotNull SlotType baseSlot
    ) {
        this(modCore, baseName, woodColor, baseSlot, false);
    }

    /**
     * Creates a new wooden block set.
     *
     * @param modCore   the mod this set's blocks/items are registered under
     * @param baseName  the naming prefix shared by every block/item this set registers
     * @param woodColor the default map color for this set's blocks
     * @param baseSlot  the slot considered this set's primary/base block
     */
    public WoodenBlockSet(
            @NotNull ModCore modCore,
            @NotNull String baseName,
            MapColor woodColor,
            @NotNull SlotType baseSlot,
            boolean fromNether
    ) {
        super(modCore, baseName, baseSlot);

        this.fromNether  = fromNether;
        this.woodColor = woodColor;
        this.planksColor = woodColor;
        this.logsBlocksTag = TagManager.BLOCKS.makeTag(modCore, baseName + "_logs");
        this.logsItemTag = TagManager.ITEMS.makeTag(modCore, baseName + "_logs");
    }

    @Override
    protected BlockSetType createSetType(BlockSetType setType) {
        if (setType == null) {
            var builder = BlockSetTypeBuilder.copyOf(BlockSetType.OAK);
            SoundType setTypeSound = setTypeSound();
            if (setTypeSound != null) builder.soundGroup(setTypeSound);
            setType = builder.register(this.C.id(this.baseName));
        }

        var woodTypeBuilder = WoodTypeBuilder.copyOf(WoodType.OAK);
        SoundType woodTypeSound = setTypeSound();
        if (woodTypeSound != null) woodTypeBuilder.soundGroup(woodTypeSound);
        this.woodType = woodTypeBuilder.register(this.C.id(this.baseName), setType);

        return setType;
    }

    /**
     * The sound applied to the {@link BlockSetType}/{@link WoodType} this set derives when
     * {@link #createSetType} builds them, or {@code null} to keep the copied-from-{@code OAK} default
     * ({@code SoundType.WOOD}).
     * <p>
     * This is a separate lever from {@link #addCommonBlockDefinitions}'s per-block {@code sound(...)} chain
     * setter: vanilla's {@code DoorBlock}/{@code TrapDoorBlock}/{@code ButtonBlock}/{@code
     * BasePressurePlateBlock} constructors call {@code properties.sound(type.soundType())} using the
     * <em>set's</em> {@link BlockSetType}, and {@code FenceGateBlock}/{@code SignBlock} do the same with the
     * set's {@link WoodType} - both unconditionally, after every trait/chain setter has already run. A block
     * definition's own {@code sound(...)} call is therefore clobbered for those six slots unless the
     * set-type/wood-type itself carries the desired sound.
     *
     * @return the sound to bake into this set's {@link BlockSetType}/{@link WoodType}, or {@code null} to
     *         leave the {@code OAK} default
     */
    protected @Nullable SoundType setTypeSound() {
        return null;
    }

    /**
     * @return the {@link WoodType} derived for this set, used for signs/fence gates/doors that need one
     */
    public WoodType woodType() {
        return this.woodType;
    }

    /**
     * Overrides the map color used for the planks block, independent of {@link #woodColor} (which every other
     * block in the set keeps using).
     *
     * @param planksColor the map color to use for the planks slot
     * @return this set, cast to its concrete subclass, for chaining
     */
    public S setPlanksColor(MapColor planksColor) {
        this.planksColor = planksColor;
        return (S) this;
    }

    @Override
    protected void addCommonBlockDefinitions(SlotType slot, BlockDefinition<?, ?> blockDefinition) {
        super.addCommonBlockDefinitions(slot, blockDefinition);

        blockDefinition.addTrait(fromNether ? BlockTraits.WOOD_BLOCK.netherWood() : BlockTraits.WOOD_BLOCK.withDefault());
        blockDefinition.addTrait(flammableTrait(slot));
        // addTrait(null) is a documented no-op (BlockDefinition#addTrait), so the default fuelTrait()
        // below (no fuel) costs nothing here.
        blockDefinition.addTrait(fuelTrait(slot));

        if (slot == SlotType.PLANKS) {
            blockDefinition.mapColor(planksColor);
        } else {
            blockDefinition.mapColor(woodColor);
        }

    }

    /**
     * The {@link FlammableBlockTrait} applied to a block built for the given slot. Defaults to the flat
     * burn/spread chance of 5 shared by every slot, matching this class's previous behaviour.
     * <p>
     * Override to give specific slots a different, vanilla-matching burn/spread chance (e.g. logs vs.
     * planks-derived parts). Do this here rather than by adding a second {@code FLAMMABLE} trait after the
     * fact: {@link de.ambertation.wover.block.api.BlockRegistry#registerAsFlammable(net.minecraft.world.level.block.Block, int, int)}
     * only registers a block once (it no-ops if the block is already registered as flammable), so a later
     * trait with a different value would silently be dropped instead of overriding the earlier one.
     *
     * @param slot the slot being configured
     * @return the flammable trait to apply to this slot
     */
    protected FlammableBlockTrait flammableTrait(SlotType slot) {
        return BlockTraits.FLAMMABLE.withDefault();
    }

    /**
     * Furnace-fuel burn times (ticks) per slot, per the WP8.3 fuel policy (decision 6 + user's "include
     * derived" scope choice): bark/log/stem/trunk stay non-fuel (absent from this map - {@link #fuelTrait}
     * returns {@code null}), planks/stripped variants/plank-derived building blocks/furniture all become
     * furnace fuel. Values match vanilla's {@code FuelValues#vanillaBurnTimes} table (base tier
     * {@code i = 200}: planks/logs/stairs/fences/... = {@code i*3/2 = 300}, slabs = {@code i*3/4 = 150},
     * doors/signs = {@code i = 200}, buttons = {@code i/2 = 100}, hanging signs = {@code i*4 = 800}) rather
     * than BetterNether's legacy hard-coded {@code 40} (that value was the old {@code addFuel} helper's
     * bowl-tier constant, never a real per-type burn time). {@code WALL} and the furniture slots
     * (taburet/chair/bar stool) have no vanilla analog - they're wooden building blocks built from planks, so
     * they're given the general 300-tick building-block tier.
     */
    public static final Map<SlotType, FuelBlockTrait.DefaultFuelTicks> FUEL_TICKS = Map.ofEntries(
            Map.entry(SlotType.LOG, FuelBlockTrait.DefaultFuelTicks.LOGS),
            Map.entry(SlotType.BARK, FuelBlockTrait.DefaultFuelTicks.LOGS),
            Map.entry(SlotType.PLANKS, FuelBlockTrait.DefaultFuelTicks.LOGS),
            Map.entry(SlotType.STRIPPED_LOG, FuelBlockTrait.DefaultFuelTicks.LOGS),
            Map.entry(SlotType.STRIPPED_BARK, FuelBlockTrait.DefaultFuelTicks.LOGS),
            Map.entry(SlotType.SLAB, FuelBlockTrait.DefaultFuelTicks.SLABS),
            Map.entry(SlotType.STAIRS, FuelBlockTrait.DefaultFuelTicks.LOGS),
            Map.entry(SlotType.FENCE, FuelBlockTrait.DefaultFuelTicks.LOGS),
            Map.entry(SlotType.GATE, FuelBlockTrait.DefaultFuelTicks.LOGS),
            Map.entry(SlotType.WALL, FuelBlockTrait.DefaultFuelTicks.LOGS),
            Map.entry(SlotType.BUTTON, FuelBlockTrait.DefaultFuelTicks.HALF),
            Map.entry(SlotType.PRESSURE_PLATE, FuelBlockTrait.DefaultFuelTicks.LOGS),
            Map.entry(SlotType.TRAPDOOR, FuelBlockTrait.DefaultFuelTicks.LOGS),
            Map.entry(SlotType.DOOR, FuelBlockTrait.DefaultFuelTicks.BASE),
            Map.entry(SlotType.LADDER, FuelBlockTrait.DefaultFuelTicks.LOGS),
            Map.entry(SlotType.SIGN, FuelBlockTrait.DefaultFuelTicks.BASE),
            Map.entry(SlotType.HANGING_SIGN, FuelBlockTrait.DefaultFuelTicks.QUADRUPEL),
            Map.entry(SlotType.CHEST, FuelBlockTrait.DefaultFuelTicks.LOGS),
            Map.entry(SlotType.BARREL, FuelBlockTrait.DefaultFuelTicks.LOGS),
            Map.entry(SlotType.CRAFTING_TABLE, FuelBlockTrait.DefaultFuelTicks.LOGS),
            Map.entry(SlotType.BOOKSHELF, FuelBlockTrait.DefaultFuelTicks.LOGS),
            Map.entry(SlotType.CHISELED_BOOKSHELF, FuelBlockTrait.DefaultFuelTicks.LOGS),
            Map.entry(SlotType.COMPOSTER, FuelBlockTrait.DefaultFuelTicks.LOGS),
            Map.entry(SlotType.TABURET, FuelBlockTrait.DefaultFuelTicks.LOGS),
            Map.entry(SlotType.CHAIR, FuelBlockTrait.DefaultFuelTicks.LOGS),
            Map.entry(SlotType.BAR_STOOL, FuelBlockTrait.DefaultFuelTicks.LOGS)
    );

    /**
     * The furnace-fuel trait applied to a block built for the given slot, or {@code null} for a slot that is
     * not furnace fuel. Defaults to Traits set in {@code FUEL_TICKS}.
     *
     * @param slot the slot being configured
     * @return the fuel trait to apply to this slot, or {@code null} for no fuel
     */
    protected @Nullable BlockTrait<?, ?> fuelTrait(SlotType slot) {
        var ticks = FUEL_TICKS.getOrDefault(slot, null);
        if (ticks == null) return null;
        return ticks.trait;
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        return SlotMap.of(
                WoodSlots.BARK,
                WoodSlots.BARREL,
                WoodSlots.BOAT,
                WoodSlots.BOOKSHELF,
                WoodSlots.BUTTON,
                WoodSlots.CHEST,
                WoodSlots.CHEST_BOAT,
                WoodSlots.CHISELED_BOOKSHELF,
                WoodSlots.COMPOSTER,
                WoodSlots.CRAFTING_TABLE,
                WoodSlots.DOOR,
                WoodSlots.FENCE,
                WoodSlots.FENCE_GATE,
                WoodSlots.HANGING_SIGN,
                WoodSlots.LADDER,
                WoodSlots.LOG,
                WoodSlots.PLANKS,
                WoodSlots.PRESSURE_PLATE,
                WoodSlots.SIGN,
                WoodSlots.SLAB,
                WoodSlots.STAIRS,
                WoodSlots.STRIPPED_BARK,
                WoodSlots.STRIPPED_LOG,
                WoodSlots.TRAPDOOR
        );
    }
}
