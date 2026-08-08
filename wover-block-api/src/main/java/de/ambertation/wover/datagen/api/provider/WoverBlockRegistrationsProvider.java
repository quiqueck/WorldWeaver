package de.ambertation.wover.datagen.api.provider;

import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.CompostableTrait;
import de.ambertation.wover.block.api.trait.behaviour.FuelBlockTrait;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverDataProvider;

import com.google.common.hash.Hashing;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;

/**
 * Writes a deterministic {@code block_registrations.txt} snapshot of the runtime auxiliary-registry state of
 * every block in the consuming mod's namespace, one line per block sorted by id. These registrations are fed by
 * block traits/properties but live in side registries rather than on the {@link net.minecraft.world.level.block.state.BlockBehaviour}
 * itself, so they are invisible to both a {@code diff -rq} of the generated assets and to the
 * {@link BlockPropertiesProvider} audit. Captured columns:
 *
 * <ul>
 *     <li>{@code flammable=<burn>/<spread>} from Fabric's {@link FlammableBlockRegistry} default instance
 *     ({@code -} when the block is not registered as flammable),</li>
 *     <li>{@code compostable=<chance>} - resolved first from {@link ComposterBlock#COMPOSTABLES} keyed by the
 *     block's item and, failing that, from a {@link CompostableTrait} runtime trait attached to the block (the
 *     mechanism modded blocks use, since they are never added to the vanilla map); {@code -} when the block is
 *     not compostable by either source,</li>
 *     <li>{@code fuel=?} for every block: furnace fuel lives in {@code FuelValues}, whose vanilla table is built
 *     by dereferencing item tags (e.g. {@code minecraft:logs}) that are not bound in the datagen registry
 *     context ({@code FuelValues.vanillaBurnTimes} throws {@code "Tag ... can't be dereferenced during
 *     construction"}). Runtime fuel registrations are therefore not observable at datagen time; the value is
 *     left as {@code ?} rather than faked.</li>
 * </ul>
 *
 * <p>Like {@code block_properties.txt} this file is committed under {@code src/main/generated} but excluded from
 * the packed jar via {@code processResources { exclude 'block_registrations.txt' }} in the consuming mod's
 * build.gradle.
 */
public class WoverBlockRegistrationsProvider implements WoverDataProvider<DataProvider> {
    protected final ModCore modCore;

    public WoverBlockRegistrationsProvider(ModCore modCore) {
        this.modCore = modCore;
    }

    @Override
    public DataProvider getProvider(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        return new Provider(output);
    }

    private static String flammable(Block block) {
        final FlammableBlockRegistry.Entry entry = FlammableBlockRegistry.getDefaultInstance().get(block);
        if (entry == null) return "-";
        return entry.getBurnChance() + "/" + entry.getSpreadChance();
    }

    private static String compostable(Block block) {
        final Item item = block.asItem();
        if (item != Items.AIR && ComposterBlock.COMPOSTABLES.containsKey(item)) {
            // Locale-independent (Float.toString uses '.'), so the file is byte-identical across locales.
            return Float.toString(ComposterBlock.COMPOSTABLES.getFloat(item));
        }
        // Modded blocks resolve their composting chance at runtime from a CompostableTrait rather than the
        // vanilla map (which is only ever populated for vanilla items), so mirror that here.
        final float traitChance = compostableTraitChance(block);
        if (traitChance >= 0.0f) return Float.toString(traitChance);
        return "-";
    }

    private static float compostableTraitChance(Block block) {
        return BlockTrait.runtimeTraits(block)
                         .filter(CompostableTrait.class::isInstance)
                         .map(CompostableTrait.class::cast)
                         .map(CompostableTrait::compostChance)
                         .findFirst()
                         .orElse(-1.0f);
    }

    /**
     * Furnace fuel lives in {@code FuelValues}, whose vanilla table is built by dereferencing item tags (e.g.
     * {@code minecraft:logs}) that are not bound in the datagen registry context — {@code vanillaBurnTimes} throws
     * during construction there. So the effective burn time is not observable at datagen time; what <em>is</em>
     * observable is the {@link FuelBlockTrait} the block was registered with, which is the value that trait hands
     * to {@code FuelRegistryEvents.BUILD} at runtime. Blocks without the trait emit {@code -} (they may still be
     * fuel through a vanilla item tag, which this file cannot see).
     */
    private String fuel(Block block) {
        var fuelTrait = (FuelBlockTrait) (BlockTrait.runtimeTraits(block)
                                                    .filter(FuelBlockTrait.class::isInstance)
                                                    .findAny().orElse(null));

        if (fuelTrait != null) {
            return "" + fuelTrait.ticks;
        }
        return "-";
    }

    private String lineFor(ResourceLocation id, Block block) {
        return "{\"id\":\"" + id + "\""
                + ",\"flammable\":\"" + flammable(block) + "\""
                + ",\"compostable\":\"" + compostable(block) + "\""
                + ",\"fuel\":\"" + fuel(block) + "\"}";
    }

    private class Provider implements DataProvider {
        private final FabricDataOutput output;

        private Provider(FabricDataOutput output) {
            this.output = output;
        }

        @Override
        public @NotNull CompletableFuture<?> run(@NotNull CachedOutput writer) {
            final List<String> lines = new ArrayList<>();
            for (Block block : BuiltInRegistries.BLOCK) {
                final ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
                if (!id.getNamespace().equals(modCore.namespace)) continue;
                lines.add(lineFor(id, block));
            }
            // Sorting by the full line orders by the (unique) id prefix, so the file is fully deterministic.
            Collections.sort(lines);

            final byte[] bytes = ("[\n" + String.join(",\n", lines) + "\n]\n").getBytes(StandardCharsets.UTF_8);
            final Path path = output.getOutputFolder().resolve("block_registrations.json");
            try {
                writer.writeIfNeeded(path, bytes, Hashing.sha1().hashBytes(bytes));
            } catch (IOException e) {
                return CompletableFuture.failedFuture(e);
            }
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public @NotNull String getName() {
            return "Block Registrations Audit (" + modCore.namespace + ")";
        }
    }
}
