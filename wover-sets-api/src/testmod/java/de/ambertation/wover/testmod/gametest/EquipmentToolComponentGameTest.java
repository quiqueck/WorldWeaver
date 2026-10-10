package de.ambertation.wover.testmod.gametest;

import de.ambertation.wover.complex.api.equipment.ToolSlot;
import de.ambertation.wover.testmod.entrypoint.TestModWoverSets;
import de.ambertation.wover.testmod.sets.TestEquipmentSet;

import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Tool;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Regression coverage for quiqueck/BetterEnd#601: {@code EquipmentSet#add(ToolSlot)} built a
 * {@link ToolSlot#SPEAR_SLOT} item through its catch-all {@code Item.Properties#tool(...)} branch. Spear tool
 * values carry no mining tag, so the resulting rule's {@link HolderSet.Named} had a {@code null} key and
 * {@link HolderSet#unwrapKey()} threw for any mod inspecting tool rules. The same call also replaced the spear's
 * attack attributes with the digger baseline.
 */
public class EquipmentToolComponentGameTest {
    @GameTest
    public void toolRulesNeverUseUnnamedTagSets(GameTestHelper helper) {
        final List<String> broken = new ArrayList<>();
        int checked = 0;
        for (Item item : BuiltInRegistries.ITEM) {
            final var id = BuiltInRegistries.ITEM.getKey(item);
            if (!TestModWoverSets.C.namespace.equals(id.getNamespace())) continue;

            final Tool tool = item.components().get(DataComponents.TOOL);
            if (tool == null) continue;
            checked++;
            for (Tool.Rule rule : tool.rules()) {
                // Direct sets (like vanilla's sword cobweb rule) are fine; a tag-backed set must name its tag.
                if (rule.blocks() instanceof HolderSet.Named<?> named && named.key() == null) {
                    broken.add(id.toString());
                }
            }
        }
        if (checked == 0) {
            throw helper.assertionException(Component.literal("no testmod item with a tool component found"));
        }
        if (!broken.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    "minecraft:tool rules backed by a tag set without a tag key: " + broken
            ));
        }
        helper.succeed();
    }

    @GameTest
    public void spearHasNoToolComponentLikeVanilla(GameTestHelper helper) {
        if (Items.IRON_SPEAR.components().has(DataComponents.TOOL)) {
            throw helper.assertionException(Component.literal("vanilla iron_spear unexpectedly has a tool component"));
        }
        final Item spear = TestEquipmentSet.INSTANCE.get(ToolSlot.SPEAR_SLOT);
        final List<String> broken = new ArrayList<>();
        if (spear.components().has(DataComponents.TOOL)) broken.add("has a minecraft:tool component");

        // Item.Properties#spear sets the attack speed from the attack duration (1 / duration - 4, always
        // negative); a later tool(...) call used to overwrite it with the digger baseline of 0.
        final var modifiers = spear.components().get(DataComponents.ATTRIBUTE_MODIFIERS);
        final boolean slowedDown = modifiers != null && modifiers
                .modifiers()
                .stream()
                .anyMatch(e -> e.attribute().is(Attributes.ATTACK_SPEED)
                        && e.modifier().is(Item.BASE_ATTACK_SPEED_ID)
                        && e.modifier().amount() < 0);
        if (!slowedDown) broken.add("attack speed not taken from the spear tuning");
        if (!spear.components().has(DataComponents.KINETIC_WEAPON)) broken.add("no kinetic_weapon component");

        if (!broken.isEmpty()) {
            throw helper.assertionException(Component.literal(
                    BuiltInRegistries.ITEM.getKey(spear) + " not configured like a vanilla spear: " + broken
            ));
        }
        helper.succeed();
    }
}
