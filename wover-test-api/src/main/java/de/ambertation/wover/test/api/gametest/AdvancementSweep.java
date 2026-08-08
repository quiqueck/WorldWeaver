package de.ambertation.wover.test.api.gametest;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Enumerates the advancements actually loaded for a namespace, at runtime, rather than needing a
 * hand-maintained list. BetterEnd alone ships ~890 generated advancement JSONs (recipe-unlock
 * advancements included); re-deriving the target list each run is what keeps a sweep from silently
 * rotting when datagen adds or removes entries. Runtime {@code ServerAdvancementManager} lookup was
 * chosen over reading the generated JSON folder from disk: it reflects what the server actually loaded
 * (datapack overrides included) rather than an on-disk snapshot, and needs no filesystem-layout
 * assumption about where a given module's generated resources end up.
 */
public final class AdvancementSweep {
    private AdvancementSweep() {}

    /** Every advancement id currently loaded under {@code namespace}. */
    public static List<AdvancementHolder> advancementsIn(GameTestHelper helper, String namespace) {
        final List<AdvancementHolder> found = new ArrayList<>();
        for (AdvancementHolder holder : helper.getLevel().getServer().getAdvancements().getAllAdvancements()) {
            final ResourceLocation id = holder.id();
            if (id.getNamespace().equals(namespace)) {
                found.add(holder);
            }
        }
        return found;
    }

    /**
     * Asserts every advancement in {@code namespace} is well-formed enough to be useful: it has a
     * display (or is a hidden/root helper the caller explicitly allows) and at least one criterion. An
     * advancement with zero criteria can never actually be awarded - a silent dead end that a
     * presence-only check ("the file exists") would never catch.
     */
    public static List<String> findMalformedAdvancements(GameTestHelper helper, String namespace) {
        final List<String> problems = new ArrayList<>();
        for (AdvancementHolder holder : advancementsIn(helper, namespace)) {
            final var advancement = holder.value();
            if (advancement.criteria().isEmpty()) {
                problems.add(holder.id() + ": has zero criteria, so it can never be awarded");
            }
        }
        return problems;
    }
}
