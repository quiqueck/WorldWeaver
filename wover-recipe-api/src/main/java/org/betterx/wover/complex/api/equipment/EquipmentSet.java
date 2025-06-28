package org.betterx.wover.complex.api.equipment;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public abstract class EquipmentSet {
    private static final List<EquipmentSet> SETS = new LinkedList<>();

    public interface ToolFactory<I extends Item> {
        I create(Item.Properties properties);
    }

    public interface ArmorFactory<I extends Item> {
        I create(Item.Properties properties);
    }

    public final ToolTier toolTier;
    public final ArmorTier armorTier;
    public final String baseName;
    public final ModCore C;
    public final ItemLike handleItem;

    private final Map<ToolSlot, ToolDescription<?>> tools = new HashMap<>();
    private final Map<ArmorSlot, ArmorDescription<?>> armors = new HashMap<>();
    protected final EquipmentSet templateBaseSet;

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
            ItemLike handleItem, EquipmentSet templateBaseSet
    ) {
        this.C = C;
        this.baseName = baseName;
        this.toolTier = toolTier;
        this.armorTier = armorTier;
        this.handleItem = handleItem;
        this.templateBaseSet = templateBaseSet;
        SETS.add(this);
    }

    public <I extends Item> void add(ToolSlot slot) {
        add(slot, Item::new);
    }

    public <I extends Item> void add(ToolSlot slot, ToolFactory<I> toolFactory) {
        add(slot, toolFactory, ToolSlot::buildProperties);
    }

    public <I extends Item> void add(
            ToolSlot slot,
            ToolFactory<I> toolFactory,
            ToolSlot.PropertiesBuilder propertiesBuilder
    ) {
        tools.put(
                slot,
                new ToolDescription<>(
                        C,
                        slot,
                        nameForSlot(slot),
                        () -> toolFactory.create(propertiesBuilder
                                .build(slot, toolTier)
                                .setId(ResourceKey.create(Registries.ITEM, C.mk(nameForSlot(slot))))
                        )
                )
        );
    }

    public <I extends Item> void add(ArmorSlot slot) {
        add(slot, Item::new);
    }

    public <I extends Item> void add(ArmorSlot slot, ArmorFactory<I> armorFactory) {
        add(slot, armorFactory, ArmorSlot::buildProperties);
    }

    public <I extends Item> void add(
            ArmorSlot slot,
            ArmorFactory<I> armorFactory,
            ArmorSlot.PropertiesBuilder propertiesBuilder
    ) {
        armors.put(
                slot,
                new ArmorDescription<>(
                        C,
                        slot,
                        nameForSlot(slot),
                        () -> armorFactory.create(
                                propertiesBuilder
                                        .build(slot, armorTier)
                                        .setId(ResourceKey.create(Registries.ITEM, C.mk(nameForSlot(slot))))
                        )
                )
        );
    }


    public void buildRecipes(RecipeBuilder.Context context) {
        for (var desc : tools.entrySet()) {
            desc.getValue().addRecipe(context, toolTier, handleItem, templateBaseSet);
        }

        for (var desc : armors.entrySet()) {
            desc.getValue().addRecipe(context, armorTier, handleItem, templateBaseSet);
        }
    }

    public static void buildAllRecipes(
            ModCore modCore,
            RecipeBuilder.Context context
    ) {
        SETS.stream().filter(set -> set.C == modCore).forEach(set -> set.buildRecipes(context));
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
        return (I) tools.get(slot).getItem();
    }

    public <I extends Item> I get(ArmorSlot slot) {
        return (I) armors.get(slot).getItem();
    }

    public Item[] getTools() {
        var items = new Item[tools.size()];
        int i = 0;
        for (var desc : tools.values()) {
            items[i++] = desc.getItem();
        }
        return items;
    }

    public Item[] getArmorPieces() {
        var items = new Item[armors.size()];
        int i = 0;
        for (var desc : armors.values()) {
            items[i++] = desc.getItem();
        }
        return items;
    }

    public Item[] getAll() {
        var items = new Item[tools.size() + armors.size()];
        int i = 0;
        for (var desc : tools.values()) {
            items[i++] = desc.getItem();
        }
        for (var desc : armors.values()) {
            items[i++] = desc.getItem();
        }
        return items;
    }
}

