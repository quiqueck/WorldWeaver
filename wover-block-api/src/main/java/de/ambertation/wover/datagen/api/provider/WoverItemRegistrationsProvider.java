package de.ambertation.wover.datagen.api.provider;

import de.ambertation.wover.block.api.trait.CompostableTrait;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverDataProvider;
import de.ambertation.wover.item.api.trait.ItemTrait;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Compostable;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.minecraft.world.level.storage.loot.providers.number.ResolvableNumber;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import com.google.common.hash.Hashing;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
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
 *     <li>{@code compostable=<chance>} - resolved first from the item's vanilla {@code minecraft:compostable}
 *     data component (26.3's replacement for the removed {@code ComposterBlock.COMPOSTABLES} map, see
 *     {@link #VANILLA_COMPOST_CHANCES}) and, failing that, from a {@link CompostableTrait} runtime trait
 *     attached to the item directly (the mechanism modded items use, since they never carry the vanilla
 *     component); {@code -} when the item is not compostable by either source.</li>
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
            FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        return new Provider(output, registriesFuture);
    }

    /**
     * The default {@code minecraft:compostable} components of every item, keyed by item.
     * <p>
     * An {@link Item}'s default components live on its {@code builtInRegistryHolder()} and are only bound when a
     * {@code ReloadableServerResources} is built, which never happens in a datagen run - {@link Item#components()}
     * throws {@code NullPointerException: Components not bound yet} there. Vanilla hits the same wall in
     * {@code RegistryComponentsReport} and solves it by baking the initializers against the datagen registry
     * provider instead, which is what this does. {@code PendingComponents#apply()} is deliberately *not* called:
     * this is an audit, it must observe the registries without mutating them for the providers that run after it.
     *
     * @param registries the datagen registry provider the initializers are resolved against
     * @return an identity map from item to its baked default component map; items with no initializer are absent
     */
    @SuppressWarnings("unchecked")
    static Map<Item, DataComponentMap> bakeDefaultComponents(HolderLookup.Provider registries) {
        final Map<Item, DataComponentMap> baked = new IdentityHashMap<>();
        for (DataComponentInitializers.PendingComponents<?> pending :
                BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(registries)) {
            if (!Registries.ITEM.equals(pending.key())) continue;
            ((DataComponentInitializers.PendingComponents<Item>) pending)
                    .forEach((holder, components) -> baked.put(holder.value(), components));
        }
        return baked;
    }

    /**
     * The five composting tiers vanilla ships, mapped back to the {@code 0..1} chance the old
     * {@code ComposterBlock.COMPOSTABLES} map stored for them.
     * <p>
     * 26.3 removed that map entirely: composting is now data-driven through the {@code minecraft:compostable}
     * item component, which references a {@code minecraft:number_provider} by key instead of storing a float.
     * The five vanilla keys are built by {@code NumberProviders#bootstrap} from the very same percentages the
     * 26.1 map used ({@code LOW}=30, {@code LOW_MEDIUM}=50, {@code MEDIUM}=65, {@code MEDIUM_HIGH}=85,
     * {@code ALWAYS_ADD_ONE}=100), so translating back is exact rather than approximate - and it keeps the
     * {@code compostable=} column directly comparable against the committed 26.1 golden file, which is the
     * whole point of this audit. A key outside this set (a modded or future tier) has no float equivalent and
     * is emitted as its identifier instead, see {@link #compostable(Item)}.
     */
    private static final Map<ResourceKey<NumberProvider>, Float> VANILLA_COMPOST_CHANCES = Map.of(
            NumberProviders.COMPOSTABLE_LOW, 0.3f,
            NumberProviders.COMPOSTABLE_LOW_MEDIUM, 0.5f,
            NumberProviders.COMPOSTABLE_MEDIUM, 0.65f,
            NumberProviders.COMPOSTABLE_MEDIUM_HIGH, 0.85f,
            NumberProviders.COMPOSTABLE_ALWAYS_ADD_ONE, 1.0f
    );

    /**
     * The vanilla composting value of an item, read off its {@code minecraft:compostable} data component (26.3's
     * replacement for the removed {@code ComposterBlock.COMPOSTABLES} map), or {@code null} when the item is not
     * compostable by vanilla. Shared with {@link WoverBlockRegistrationsProvider}, which reports the same column
     * for a block's item.
     *
     * @param defaultComponents the baked default components, see {@link #bakeDefaultComponents(HolderLookup.Provider)}
     * @param item the item to inspect
     * @return the {@code 0..1} chance as a string for one of the five vanilla tiers, the referenced number
     *         provider's identifier for anything else, or {@code null} when the item has no component
     */
    static String vanillaCompostValue(Map<Item, DataComponentMap> defaultComponents, Item item) {
        if (item == Items.AIR) return null;
        final DataComponentMap components = defaultComponents.get(item);
        if (components == null) return null;
        final Compostable compostable = components.get(DataComponents.COMPOSTABLE);
        if (compostable == null) return null;
        final ResolvableNumber layers = compostable.layers();
        // 26.3 turned Compostable#layers() into a ResolvableNumber - either a bare Constant chance or a
        // Reference to a NumberProvider (what the vanilla tiers below are).
        // Locale-independent (Float.toString uses '.'), so the file is byte-identical across locales.
        if (layers instanceof ResolvableNumber.Constant constant) return Float.toString(constant.value());
        final ResourceKey<NumberProvider> key = ((ResolvableNumber.Reference) layers).key();
        final Float chance = VANILLA_COMPOST_CHANCES.get(key);
        if (chance != null) return Float.toString(chance);
        return key.identifier().toString();
    }

    private static String compostable(Map<Item, DataComponentMap> defaultComponents, Item item) {
        final String vanilla = vanillaCompostValue(defaultComponents, item);
        if (vanilla != null) return vanilla;
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

    private String lineFor(Map<Item, DataComponentMap> defaultComponents, Identifier id, Item item) {
        return "{\"id\":\"" + id + "\""
                + ",\"compostable\":\"" + compostable(defaultComponents, item) + "\"}";
    }

    private class Provider implements DataProvider {
        private final FabricPackOutput output;
        private final CompletableFuture<HolderLookup.Provider> registriesFuture;

        private Provider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            this.output = output;
            this.registriesFuture = registriesFuture;
        }

        @Override
        public @NotNull CompletableFuture<?> run(@NotNull CachedOutput writer) {
            return registriesFuture.thenCompose(registries -> write(writer, registries));
        }

        private CompletableFuture<?> write(CachedOutput writer, HolderLookup.Provider registries) {
            final Map<Item, DataComponentMap> defaultComponents = bakeDefaultComponents(registries);
            final List<String> lines = new ArrayList<>();
            for (Item item : BuiltInRegistries.ITEM) {
                final Identifier id = BuiltInRegistries.ITEM.getKey(item);
                if (!id.getNamespace().equals(modCore.namespace)) continue;
                lines.add(lineFor(defaultComponents, id, item));
            }
            // Sorting by the full line orders by the (unique) id prefix, so the file is fully deterministic.
            Collections.sort(lines);

            final byte[] bytes = ("[\n" + String.join(",\n", lines) + "\n]\n").getBytes(StandardCharsets.UTF_8);
            final Path path = output.getOutputFolder().resolve("item_registrations.json");
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
