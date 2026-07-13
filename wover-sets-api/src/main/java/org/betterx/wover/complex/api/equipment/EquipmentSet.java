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

public abstract class EquipmentSet {
    private static final List<EquipmentSet> SETS = new LinkedList<>();

    public interface ToolFactory<I extends Item> {
        @NotNull I create(@NotNull ToolItemDefinition<I> definition, @NotNull ToolTier.ToolValues values);
    }

    public interface ArmorFactory<I extends Item> extends ArmorItemDefinition.ItemFactory<I> {
    }

    public final ToolTier toolTier;
    public final ArmorTier armorTier;
    public final String baseName;
    public final ModCore C;
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

    public EquipmentSet(
            ModCore C, String baseName,
            ToolTier toolTier, ArmorTier armorTier,
            ItemLike handleItem

    ) {
        this(C, baseName, toolTier, armorTier, handleItem, null);
    }

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

    public @NotNull Item.Properties commonToolProperties(@NotNull Item.Properties properties) {
        return properties;
    }

    public @NotNull Item.Properties commonArmorProperties(@NotNull Item.Properties properties) {
        return commonToolProperties(properties);
    }

    public <I extends Item> void add(ToolSlot slot) {
        add(slot, (ItemRecipeTrait) null);
    }

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

    public <I extends Item> void add(
            ToolSlot slot,
            ToolFactory<I> toolFactory
    ) {
        add(slot, toolFactory, null);
    }

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

    public void add(ArmorSlot slot) {
        add(slot, (ItemRecipeTrait) null);
    }

    public void add(ArmorSlot slot, ItemRecipeTrait recipeOverride) {
        add(slot, (definition) -> new Item(commonArmorProperties(definition.getProperties())), recipeOverride);
    }

    public <I extends Item> void add(
            ArmorSlot slot,
            ArmorFactory<I> armorFactory
    ) {
    }

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

    @NotNull
    protected String nameForSlot(String slotName) {
        return baseName + "_" + slotName;
    }

    public <I extends Item> I get(ToolSlot slot) {
        return (I) tools.get(slot).item();
    }

    public <I extends Item> I get(ArmorSlot slot) {
        return (I) armors.get(slot).item();
    }

    public Item[] getTools() {
        var items = new Item[tools.size()];
        int i = 0;
        for (var desc : tools.values()) {
            items[i++] = desc.item();
        }
        return items;
    }

    public Item[] getArmorPieces() {
        var items = new Item[armors.size()];
        int i = 0;
        for (var desc : armors.values()) {
            items[i++] = desc.item();
        }
        return items;
    }

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

