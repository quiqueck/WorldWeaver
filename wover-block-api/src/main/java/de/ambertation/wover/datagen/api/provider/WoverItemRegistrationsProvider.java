package de.ambertation.wover.datagen.api.provider;

import de.ambertation.wover.block.api.trait.CompostableTrait;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverDataProvider;
import de.ambertation.wover.item.api.trait.ItemTrait;

import com.google.common.hash.Hashing;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.ComposterBlock;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;

/**
 * Writes a deterministic {@code item_registrations.txt} snapshot of the runtime auxiliary-registry state of
 * every item in the consuming mod's namespace, one line per item sorted by id. This is the item-side sibling
 * of {@link WoverBlockRegistrationsProvider}: items are not covered by that provider (it only walks
 * {@code BuiltInRegistries.BLOCK}), yet an item can carry its own runtime {@link CompostableTrait} independent
 * of any backing block (e.g. a food item made compostable via {@code CompostableItemTrait} in bclib), which is
 * otherwise invisible to both a {@code diff -rq} of the generated assets and to datagen. Captured columns:
 *
 * <ul>
 *     <li>{@code compostable=<chance>} - resolved first from {@link ComposterBlock#COMPOSTABLES} keyed by the
 *     item and, failing that, from a {@link CompostableTrait} runtime trait attached to the item directly (the
 *     mechanism modded items use, since they are never added to the vanilla map); {@code -} when the item is
 *     not compostable by either source.</li>
 * </ul>
 *
 * <p>Like {@code block_registrations.txt} this file is committed under {@code src/main/generated} but excluded
 * from the packed jar via {@code processResources { exclude 'item_registrations.txt' }} in the consuming mod's
 * build.gradle.
 */
public class WoverItemRegistrationsProvider implements WoverDataProvider<DataProvider> {
    protected final ModCore modCore;

    public WoverItemRegistrationsProvider(ModCore modCore) {
        this.modCore = modCore;
    }

    @Override
    public DataProvider getProvider(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        return new Provider(output);
    }

    private static String compostable(Item item) {
        if (item != Items.AIR && ComposterBlock.COMPOSTABLES.containsKey(item)) {
            // Locale-independent (Float.toString uses '.'), so the file is byte-identical across locales.
            return Float.toString(ComposterBlock.COMPOSTABLES.getFloat(item));
        }
        // Modded items resolve their composting chance at runtime from a CompostableTrait rather than the
        // vanilla map (which is only ever populated for vanilla items), so mirror that here.
        final float traitChance = compostableTraitChance(item);
        if (traitChance >= 0.0f) return Float.toString(traitChance);
        return "-";
    }

    private static float compostableTraitChance(Item item) {
        return ItemTrait.runtimeTraits(item)
                         .filter(CompostableTrait.class::isInstance)
                         .map(CompostableTrait.class::cast)
                         .map(CompostableTrait::compostChance)
                         .findFirst()
                         .orElse(-1.0f);
    }

    private String lineFor(ResourceLocation id, Item item) {
        return id
                + "  compostable=" + compostable(item);
    }

    private class Provider implements DataProvider {
        private final FabricDataOutput output;

        private Provider(FabricDataOutput output) {
            this.output = output;
        }

        @Override
        public @NotNull CompletableFuture<?> run(@NotNull CachedOutput writer) {
            final List<String> lines = new ArrayList<>();
            for (Item item : BuiltInRegistries.ITEM) {
                final ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
                if (!id.getNamespace().equals(modCore.namespace)) continue;
                lines.add(lineFor(id, item));
            }
            // Sorting by the full line orders by the (unique) id prefix, so the file is fully deterministic.
            Collections.sort(lines);

            final byte[] bytes = (String.join("\n", lines) + "\n").getBytes(StandardCharsets.UTF_8);
            final Path path = output.getOutputFolder().resolve("item_registrations.txt");
            try {
                writer.writeIfNeeded(path, bytes, Hashing.sha1().hashBytes(bytes));
            } catch (IOException e) {
                return CompletableFuture.failedFuture(e);
            }
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public @NotNull String getName() {
            return "Item Registrations Audit (" + modCore.namespace + ")";
        }
    }
}
