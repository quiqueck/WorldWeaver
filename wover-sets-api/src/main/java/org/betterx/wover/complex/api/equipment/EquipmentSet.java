package org.betterx.wover.complex.api.equipment;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.item.api.ArmorItemDefinition;
import org.betterx.wover.item.api.ToolItemDefinition;
import org.betterx.wover.item.api.trait.ItemRecipeTrait;

import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Registers a full set of tools and armor (a "material" like diamond or netherite) built from a
 * {@link ToolTier}/{@link ArmorTier} pair, generating a matching crafting/smithing recipe for every piece.
 * <p>
 * Subclass this, call {@link #add(ToolSlot)}/{@link #add(ArmorSlot)} (or one of their overloads taking a custom
 * factory/recipe override) from the constructor for every piece the set should contain, then retrieve the built
 * items with {@link #get(ToolSlot)}/{@link #get(ArmorSlot)}. See {@code TestEquipmentSet} in this module's test
 * mod for a minimal worked example.
 */
public abstract class EquipmentSet {
    private static final List<EquipmentSet> SETS = new LinkedList<>();

    /**
     * Creates a tool item for a slot, given the fully resolved {@link ToolTier.ToolValues} for that slot.
     *
     * @param <I> the item type being created
     */
    public interface ToolFactory<I extends Item> {
        /**
         * @param definition the item definition being built
         * @param values     the resolved values for this slot
         * @return the new item
         */
        @NotNull I create(@NotNull ToolItemDefinition<I> definition, @NotNull ToolTier.ToolValues values);
    }

    /**
     * Creates an armor item for a slot.
     *
     * @param <I> the item type being created
     */
    public interface ArmorFactory<I extends Item> extends ArmorItemDefinition.ItemFactory<I> {
    }

    /** The tool tier this set's tools are built from. */
    public final ToolTier toolTier;
    /** The armor tier this set's armor is built from. */
    public final ArmorTier armorTier;
    /** The naming prefix shared by every item this set registers (e.g. {@code <baseName>_pickaxe}). */
    public final String baseName;
    /** The mod this set's items are registered under. */
    public final ModCore C;
    /** The handle material used in default tool recipes (e.g. a stick). */
    public final ItemLike handleItem;

    private final Map<ToolSlot, ToolDescription<?>> tools = new HashMap<>();
    private final Map<ArmorSlot, ArmorDescription<?>> armors = new HashMap<>();
    /**
     * Lazily supplies the equipment set that recipes for this set should be upgraded from
     * (e.g. via a smithing template). Resolved only when recipes are actually built, since the
     * referenced set (typically a static field on some registry class) may not exist yet at the
     * time this set itself is constructed.
     */
    protected final @Nullable Supplier<EquipmentSet> templateBaseSet;

    /**
     * Creates a new equipment set with no smithing-template upgrade base.
     *
     * @param C          the mod this set's items are registered under
     * @param baseName   the naming prefix shared by every item this set registers
     * @param toolTier   the tool tier to build tools from
     * @param armorTier  the armor tier to build armor from
     * @param handleItem the handle material used in default tool recipes
     */
    public EquipmentSet(
            ModCore C, String baseName,
            ToolTier toolTier, ArmorTier armorTier,
            ItemLike handleItem

    ) {
        this(C, baseName, toolTier, armorTier, handleItem, null);
    }

    /**
     * Creates a new equipment set.
     *
     * @param C               the mod this set's items are registered under
     * @param baseName        the naming prefix shared by every item this set registers
     * @param toolTier        the tool tier to build tools from
     * @param armorTier       the armor tier to build armor from
     * @param handleItem      the handle material used in default tool recipes
     * @param templateBaseSet lazily supplies the equipment set that a smithing-template recipe should use as its
     *                        base item, or {@code null} to always use a plain crafting recipe
     */
    public EquipmentSet(
            ModCore C, String baseName,
            ToolTier toolTier, ArmorTier armorTier,
            ItemLike handleItem, @Nullable Supplier<EquipmentSet> templateBaseSet
    ) {
        this.C = C;
        this.baseName = baseName;
        this.toolTier = toolTier;
        this.armorTier = armorTier;
        this.handleItem = handleItem;
        this.templateBaseSet = templateBaseSet;
        SETS.add(this);
    }

    /**
     * Hook for applying properties common to every tool in this set (e.g. a shared rarity or tooltip). The
     * default implementation returns {@code properties} unchanged.
     *
     * @param properties the properties to modify
     * @return the modified properties
     */
    public @NotNull Item.Properties commonToolProperties(@NotNull Item.Properties properties) {
        return properties;
    }

    /**
     * Hook for applying properties common to every armor piece in this set. The default implementation delegates
     * to {@link #commonToolProperties}.
     *
     * @param properties the properties to modify
     * @return the modified properties
     */
    public @NotNull Item.Properties commonArmorProperties(@NotNull Item.Properties properties) {
        return commonToolProperties(properties);
    }

    /**
     * Registers the default item for {@code slot} (an {@link AxeItem}/{@link HoeItem}/{@link ShovelItem}/
     * {@link ShearsItem}, or a plain digger {@link Item} for pickaxe/sword/hammer), with an auto-generated
     * crafting recipe.
     *
     * @param slot the tool slot to register
     * @param <I>  unused type parameter, kept for API symmetry with the other overloads
     */
    public <I extends Item> void add(ToolSlot slot) {
        add(slot, (ItemRecipeTrait) null);
    }

    /**
     * Registers the default item for {@code slot}, with a custom recipe.
     *
     * @param slot           the tool slot to register
     * @param recipeOverride the recipe trait to use instead of the auto-generated recipe, or {@code null} to
     *                       keep the default
     * @param <I>  unused type parameter, kept for API symmetry with the other overloads
     */
    public <I extends Item> void add(ToolSlot slot, ItemRecipeTrait recipeOverride) {
        if (slot == ToolSlot.AXE_SLOT) {
            add(
                    slot, (definition, values) -> new AxeItem(
                            this.toolTier.toolMaterial,
                            values.attackDamage(), values.attackSpeed(),
                            commonToolProperties(definition.getProperties())
                    ),
                    recipeOverride
            );
        } else if (slot == ToolSlot.HOE_SLOT) {
            add(
                    slot, (definition, values) -> new HoeItem(
                            this.toolTier.toolMaterial,
                            values.attackDamage(), values.attackSpeed(),
                            commonToolProperties(definition.getProperties())
                    ),
                    recipeOverride
            );
        } else if (slot == ToolSlot.SHOVEL_SLOT) {
            add(
                    slot, (definition, values) -> new ShovelItem(
                            this.toolTier.toolMaterial,
                            values.attackDamage(), values.attackSpeed(),
                            commonToolProperties(definition.getProperties())
                    ),
                    recipeOverride
            );
        } else if (slot == ToolSlot.SHEARS_SLOT) {
            add(
                    slot, (definition, values) -> new ShearsItem(
                            commonToolProperties(definition.getProperties())),
                    recipeOverride
            );
        } else add(
                slot, (definition, values) -> new Item(
                        commonToolProperties(definition.getProperties()).tool(
                                this.toolTier.toolMaterial,
                                this.toolTier.blockTag,
                                values.attackDamage(), values.attackSpeed(), values.disableBlockingForSeconds()
                        )),
                recipeOverride
        );
    }

    /**
     * Registers a custom item for {@code slot}, with an auto-generated crafting/smithing recipe.
     *
     * @param slot        the tool slot to register
     * @param toolFactory creates the item from the definition and this slot's resolved tool values
     * @param <I>         the item type being registered
     */
    public <I extends Item> void add(
            ToolSlot slot,
            ToolFactory<I> toolFactory
    ) {
        add(slot, toolFactory, null);
    }

    /**
     * Registers a custom item for {@code slot}, with a custom recipe.
     *
     * @param slot           the tool slot to register
     * @param toolFactory    creates the item from the definition and this slot's resolved tool values
     * @param recipeOverride the recipe trait to use instead of the auto-generated recipe, or {@code null} to
     *                       keep the default
     * @param <I>            the item type being registered
     */
    public <I extends Item> void add(
            ToolSlot slot,
            ToolFactory<I> toolFactory,
            ItemRecipeTrait recipeOverride
    ) {
        tools.put(
                slot,
                ToolDescription.registerTool(
                        C,
                        slot,
                        nameForSlot(slot),
                        toolFactory,
                        this,
                        recipeOverride
                )
        );
    }

    /**
     * Registers the default (plain) item for {@code slot}, with an auto-generated crafting/smithing recipe.
     *
     * @param slot the armor slot to register
     */
    public void add(ArmorSlot slot) {
        add(slot, (ItemRecipeTrait) null);
    }

    /**
     * Registers the default (plain) item for {@code slot}, with a custom recipe.
     *
     * @param slot           the armor slot to register
     * @param recipeOverride the recipe trait to use instead of the auto-generated recipe, or {@code null} to
     *                       keep the default
     */
    public void add(ArmorSlot slot, ItemRecipeTrait recipeOverride) {
        add(slot, (definition) -> new Item(commonArmorProperties(definition.getProperties())), recipeOverride);
    }

    /**
     * Registers a custom item for {@code slot}, with an auto-generated crafting/smithing recipe.
     *
     * @param slot         the armor slot to register
     * @param armorFactory creates the item from the definition
     * @param <I>          the item type being registered
     */
    public <I extends Item> void add(
            ArmorSlot slot,
            ArmorFactory<I> armorFactory
    ) {
        add(slot, armorFactory, null);
    }

    /**
     * Registers a custom item for {@code slot}, with a custom recipe.
     *
     * @param slot           the armor slot to register
     * @param armorFactory   creates the item from the definition
     * @param recipeOverride the recipe trait to use instead of the auto-generated recipe, or {@code null} to
     *                       keep the default
     * @param <I>            the item type being registered
     */
    public <I extends Item> void add(
            ArmorSlot slot,
            ArmorFactory<I> armorFactory,
            @Nullable ItemRecipeTrait recipeOverride
    ) {
        armors.put(
                slot,
                ArmorDescription.registerArmor(
                        C,
                        slot,
                        nameForSlot(slot),
                        armorFactory,
                        this,
                        recipeOverride
                )
        );
    }

    @NotNull
    private String nameForSlot(ToolSlot slot) {
        return nameForSlot(slot.name);
    }

    @NotNull
    private String nameForSlot(ArmorSlot slot) {
        return nameForSlot(slot.name);
    }

    /**
     * @param slotName the slot's naming suffix (e.g. {@code "pickaxe"})
     * @return {@link #baseName} + {@code "_" +} {@code slotName}
     */
    @NotNull
    protected String nameForSlot(String slotName) {
        return baseName + "_" + slotName;
    }

    /**
     * @param slot the tool slot to look up
     * @param <I>  the expected item type
     * @return the item registered for {@code slot}
     * @throws NullPointerException if {@code slot} was never {@code add}ed to this set
     */
    public <I extends Item> I get(ToolSlot slot) {
        return (I) tools.get(slot).item();
    }

    /**
     * @param slot the armor slot to look up
     * @param <I>  the expected item type
     * @return the item registered for {@code slot}
     * @throws NullPointerException if {@code slot} was never {@code add}ed to this set
     */
    public <I extends Item> I get(ArmorSlot slot) {
        return (I) armors.get(slot).item();
    }

    /**
     * @return every tool item registered on this set, in no particular order
     */
    public Item[] getTools() {
        var items = new Item[tools.size()];
        int i = 0;
        for (var desc : tools.values()) {
            items[i++] = desc.item();
        }
        return items;
    }

    /**
     * @return every armor item registered on this set, in no particular order
     */
    public Item[] getArmorPieces() {
        var items = new Item[armors.size()];
        int i = 0;
        for (var desc : armors.values()) {
            items[i++] = desc.item();
        }
        return items;
    }

    /**
     * @return every tool and armor item registered on this set, in no particular order
     */
    public Item[] getAll() {
        var items = new Item[tools.size() + armors.size()];
        int i = 0;
        for (var desc : tools.values()) {
            items[i++] = desc.item();
        }
        for (var desc : armors.values()) {
            items[i++] = desc.item();
        }
        return items;
    }
}

