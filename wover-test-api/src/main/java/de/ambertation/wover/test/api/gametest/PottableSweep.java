package de.ambertation.wover.test.api.gametest;

import de.ambertation.wover.pottable.api.PottablePlant;
import de.ambertation.wover.pottable.api.PottablePlantRegistry;
import de.ambertation.wover.pottable.api.PottableSoil;
import de.ambertation.wover.pottable.api.PottableSoilRegistry;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Enumerates {@code wover-pottable-api} registry entries for a namespace and checks their internal
 * consistency, so a mod's own flower-pot test drives its plant list from the registry instead of a
 * hand-maintained one.
 * <p>
 * <b>Scope note:</b> this module has no generic in-world "pot" block to interact with - both mods'
 * actual flower pot implementations are their own blocks ({@code FlowerPotBlock}, {@code BlockBNPot}),
 * not something WorldWeaver provides. So the physical "does this plant actually go into my pot block"
 * check stays in each mod's own GameTest, driven by {@link #plantsIn} for the enumeration; what this
 * class checks generically is that the <em>data</em> is coherent - a plant restricted to a soil tag that
 * no registered soil actually satisfies is a silent dead end no amount of clicking pots would catch,
 * since the plant would simply never be potable by anything.
 */
public final class PottableSweep {
    private PottableSweep() {}

    /** Every {@link PottablePlant} registered under {@code namespace}, keyed by its {@code ResourceKey}. */
    public static List<Map.Entry<ResourceKey<PottablePlant>, PottablePlant>> plantsIn(
            GameTestHelper helper,
            String namespace
    ) {
        final Registry<PottablePlant> plants =
                helper.getLevel().registryAccess().lookupOrThrow(PottablePlantRegistry.POTTABLE_PLANT_REGISTRY);
        final List<Map.Entry<ResourceKey<PottablePlant>, PottablePlant>> found = new ArrayList<>();
        for (var entry : plants.entrySet()) {
            if (entry.getKey().location().getNamespace().equals(namespace)) {
                found.add(entry);
            }
        }
        return found;
    }

    /**
     * Reports every plant in {@code namespace} whose declared soil tag matches none of the currently
     * registered {@link PottableSoil}s - a typo'd or stale tag reference that would otherwise leave the
     * plant silently un-potable on anything.
     */
    public static List<String> findPlantsWithUnsatisfiableSoilTag(GameTestHelper helper, String namespace) {
        final Registry<PottableSoil> soils =
                helper.getLevel().registryAccess().lookupOrThrow(PottableSoilRegistry.POTTABLE_SOIL_REGISTRY);

        final List<Block> soilBlocks = new ArrayList<>();
        soils.forEach(soil -> BuiltInRegistries.BLOCK.get(soil.block)
                .map(Holder.Reference::value)
                .ifPresent(soilBlocks::add));

        final List<String> problems = new ArrayList<>();
        for (var entry : plantsIn(helper, namespace)) {
            final PottablePlant plant = entry.getValue();
            if (plant.validSoils.isEmpty()) continue; // "any soil" - always satisfiable.

            final boolean satisfiable = soilBlocks.stream()
                    .anyMatch(plant::isValidSoil);
            if (!satisfiable) {
                problems.add(entry.getKey().location() + ": validSoils=" + plant.validSoils.get()
                        + " matches none of the " + soilBlocks.size() + " currently registered soils");
            }
        }
        return problems;
    }
}
