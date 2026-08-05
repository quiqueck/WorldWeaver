package de.ambertation.wover.testmod.gametest;

import de.ambertation.wover.common.item.api.ItemWithCustomStack;
import de.ambertation.wover.enchantment.api.EnchantmentUtils;
import de.ambertation.wover.item.api.ItemStackHelper;
import de.ambertation.wover.testmod.item.TestItemRegistry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Fabric GameTest for {@code wover-item-api}: guards item registration through the {@code ItemRegistry}
 * fluent API and the {@code ItemWithCustomStack} trait behaviour.
 * <p>
 * The testmod registers a single item, {@code wover-item-testmod:enchanted_axe} (an {@link de.ambertation.wover.testmod.item.EnchantedAxe}),
 * via {@code ItemRegistry.forMod(...).defineDefaultItem(...).buildAndRegister()}. Two things must hold once
 * the server is up:
 * <ul>
 *   <li>the item is present in {@link BuiltInRegistries#ITEM} under its expected id, and the value there is
 *       the very instance the registry handed back - this proves {@code buildAndRegister()} both registers
 *       and tracks the item;</li>
 *   <li>the item implements {@link ItemWithCustomStack} and its {@code setupItemStack} actually runs when
 *       driven through {@link ItemStackHelper#callItemStackSetupIfPossible(ItemStack, net.minecraft.core.HolderLookup.Provider)}:
 *       a fresh stack must come back carrying Sharpness 5 (what {@code EnchantedAxe.setupItemStack} applies).</li>
 * </ul>
 * A failure here means item registration or the custom-stack trait dispatch stopped working - a real regression.
 */
public class ItemGameTest {
    // Namespace comes from ModCore.create("wover-item-testmod") in TestModWoverItem.
    private static final Identifier ENCHANTED_AXE_ID = Identifier.parse("wover-item-testmod:enchanted_axe");
    // Level EnchantedAxe.setupItemStack applies (EnchantmentUtils.enchantInWorld(..., SHARPNESS, 5, ...)).
    private static final int EXPECTED_SHARPNESS = 5;

    @GameTest
    public void enchantedAxeIsRegistered(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        if (!BuiltInRegistries.ITEM.containsKey(ENCHANTED_AXE_ID)) {
            failures.add(ENCHANTED_AXE_ID + ": expected the item registered via ItemRegistry.buildAndRegister() but it is missing");
        } else {
            final Item registered = BuiltInRegistries.ITEM.getValue(ENCHANTED_AXE_ID);
            // The registry must hand back the exact instance the testmod holds a reference to.
            if (registered != TestItemRegistry.ENCHANTED_AXE) {
                failures.add(ENCHANTED_AXE_ID + ": registry value does not match TestItemRegistry.ENCHANTED_AXE");
            }
            // The registered item must still carry the custom-stack trait marker.
            if (!(registered instanceof ItemWithCustomStack)) {
                failures.add(ENCHANTED_AXE_ID + ": expected the item to implement ItemWithCustomStack");
            }
        }

        if (!failures.isEmpty()) {
            helper.fail(Component.literal(
                    "Item registration regression:\n - " + String.join("\n - ", failures)
            ));
            return;
        }
        helper.succeed();
    }

    @GameTest
    public void customStackTraitEnchantsStack(GameTestHelper helper) {
        // A freshly created stack has no enchantments yet.
        final ItemStack stack = new ItemStack(TestItemRegistry.ENCHANTED_AXE);
        final int before = EnchantmentUtils.getItemEnchantmentLevel(helper.getLevel(), Enchantments.SHARPNESS, stack);
        if (before != 0) {
            helper.fail(Component.literal(
                    "Expected a fresh enchanted_axe stack to have Sharpness 0 but it was " + before
            ));
            return;
        }

        // Drive the ItemWithCustomStack dispatch exactly as the Wover API does (creative tabs, /give, ...).
        ItemStackHelper.callItemStackSetupIfPossible(stack, helper.getLevel().registryAccess());

        final int after = EnchantmentUtils.getItemEnchantmentLevel(helper.getLevel(), Enchantments.SHARPNESS, stack);
        if (after != EXPECTED_SHARPNESS) {
            helper.fail(Component.literal(
                    "ItemWithCustomStack trait regression: expected setupItemStack to apply Sharpness "
                            + EXPECTED_SHARPNESS + " but stack had Sharpness " + after
            ));
            return;
        }

        helper.succeed();
    }
}
